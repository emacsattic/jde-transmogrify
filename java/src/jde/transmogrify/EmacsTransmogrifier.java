/*
 * Copyright (C) 2001  Nascif A. Abousalh Neto
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jde.transmogrify;

import java.io.*;
import java.util.*;
import net.sourceforge.transmogrify.hook.Hook;
import net.sourceforge.transmogrify.hook.Transmogrifier;
import net.sourceforge.transmogrify.lint.LintTreeWalker;
import net.sourceforge.transmogrify.lint.Warning;
import net.sourceforge.transmogrify.symtab.Occurrence;
import net.sourceforge.transmogrify.symtab.SymbolTable;
import net.sourceforge.transmogrify.symtab.parser.FileParser;
import net.sourceforge.transmogrify.symtab.TableMaker;
import net.sourceforge.transmogrify.symtab.parser.SymTabAST;
import net.sourceforge.transmogrify.lint.Lint;
import java.io.File;

public class EmacsTransmogrifier 
{
  private static final String[] LINTS =  
  {"net.sourceforge.transmogrify.lint.UnbracedBlockLint",
   "net.sourceforge.transmogrify.lint.MultipleReturnLint",
   "net.sourceforge.transmogrify.lint.EmptyReturnLint",
   "net.sourceforge.transmogrify.lint.UnusedVariableLint",
   "net.sourceforge.transmogrify.lint.UnusedParameterLint"};


  private static EmacsTransmogrifier instance_s;

  private SymbolTable symbolTable_;
  private Hashtable   files_;
  private FileParser  fileParser_;
  private EmacsHook   hook_;
  private EmacsEvalGnudoit evaluator_;

  private List    lints_;
  private TreeSet sortedWarnings_;

  /**
   * Creates a new <code>EmacsTransmogrifier</code> instance.
   *
   */
  public EmacsTransmogrifier() 
  {
    try {
      files_ = new Hashtable();
      fileParser_ = new FileParser();
      evaluator_ = new EmacsEvalGnudoit();
      hook_ = new EmacsHook(evaluator_);

      // lint setup
      internalSetLints(LINTS);
      sortedWarnings_ = new TreeSet(new Comparator(){
          public int compare(Object o1, Object o2) {
            Warning w1 = (Warning)o1;
            Warning w2 = (Warning)o2;
            
            int result = w1.getOccurrence().compareTo(w2.getOccurrence());
            if (result == 0) {
              result = w1.getText().compareTo(w2.getText());
            }
            return result;
          }
        });
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * <code>setLints</code> sets the list of Lint operations
   *
   * @param lintNames a <code>String</code> value containing the concatenation
   *        of the qualified name of the classes that implement lint operations,
   *        separated by spaces.
   */
  public static void setLints(String[] lintAr) {
    instance_s.internalSetLints(lintAr);
  }

  private void internalSetLints(String[] lintAr) 
  {
    try {
      int len = lintAr.length;
      lints_  = new ArrayList(len);
      for (int i = 0; i < len; i++) {
        String lintClassName = lintAr[i];
        Class lintClass = Class.forName(lintClassName);
        lints_.add(lintClass.newInstance());
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @return whether or not any files were parsed
   */
  public static void parseSource(String[] fileAr) 
    throws Exception 
  {
    instance_s.internalClearFiles();
    boolean filesParsed = false;

    int len = fileAr.length;
    for ( int i = 0; i < len; i++ ) {
      filesParsed = true;
      instance_s.parseFile( new File(fileAr[i]) );
    }
    if (filesParsed) {
      instance_s.setSymbolTable();
    }
  }

  public static void checkParsing() {
    System.out.println((instance_s.symbolTable_ != null) ? "t" : "nil");
  }


  /**
   * reparses all files in the list of files this
   * <code>EmacsTransmogrifier</code> is keeping track of
   */
  public static void reparse() 
    throws Exception 
  {
    instance_s.fileParser_ = new FileParser();
    Enumeration elts = instance_s.files_.keys();

    while (elts.hasMoreElements()) {
      instance_s.parseFile(new File((String)elts.nextElement()));
      instance_s.setSymbolTable();         // is this one necessary?
    }
  }

  protected void setSymbolTable() 
    throws Exception
  {
    TableMaker maker = new TableMaker( (SymTabAST)(fileParser_.getTree()) );
    symbolTable_ = maker.getTable();
  }

  protected void parseFile( File file ) 
    throws Exception 
  {
    files_.put( file.getName(), file );
    fileParser_.doFile( file );
  }

  /**
   * clears the list of files this <code>EmacsTransmogrifier</code> is keeping
   * track of 
   */
  public static void clearFiles() {
    instance_s.internalClearFiles();
  }

  public void internalClearFiles() {
    files_.clear();
    fileParser_ = new FileParser();
    symbolTable_ = null;
  }

  //////////////////////////////////////////////////////////////
  // Refactorings
  //////////////////////////////////////////////////////////////

  public static void transmogrify(String transmogrifierName)
  {
    try {
      Debug.debug("-- transmogrify "+transmogrifierName);
      Class transmogrifierClass = Class.forName(transmogrifierName);
      Transmogrifier rf = (Transmogrifier)transmogrifierClass.newInstance();
      rf.setup(instance_s.symbolTable_);
      rf.apply(instance_s.hook_);
    } catch (Exception e) {
      Debug.debug(e);
      instance_s.hook_.displayException(e, "Error executing "+transmogrifierName);
    } catch (Throwable e) {
      Debug.debug(e);
      e.printStackTrace();
    }
  }

  //////////////////////////////////////////////////////////////
  // Lint
  //////////////////////////////////////////////////////////////
  
  public static void lint() 
  {
    int dummy = 1;
    instance_s.internalLint("dummy");
  }

  //////////////////////////////////////////////////////////////
  // Utilities
  //////////////////////////////////////////////////////////////

  public static void checkInit()
  {
    boolean ret = true;
    if (instance_s == null) {
      instance_s = new EmacsTransmogrifier();
      ret = instance_s.evaluator_.isValid();
    }
    System.out.println(ret ? "t" : "nil");
  }

  private void internalLint(String dummy) 
  {
    LintTreeWalker treeWalker = 
      new LintTreeWalker(fileParser_.getTree(), lints_);
    treeWalker.walkTree();

    // process warnings
    sortedWarnings_.clear();
    Iterator itLints = lints_.iterator();
    while (itLints.hasNext()) {
      Lint lint = (Lint)itLints.next();
      Iterator itWarnings = lint.getWarnings().iterator();
      while (itWarnings.hasNext()) {
        sortedWarnings_.add(itWarnings.next());
      }
    }
    // display warnings in Emacs compile format
    Iterator itSorted = sortedWarnings_.iterator();
    while (itSorted.hasNext()) {
      System.out.println(toEmacsString((Warning)itSorted.next()));
    }
  }

  private String toEmacsString(Warning w) {
    Occurrence occ = w.getOccurrence(); 
    return occ.getFile() + ":" + occ.getLine() + ":" + 
      occ.getColumn() + ": " + w.getText();
  }

}

