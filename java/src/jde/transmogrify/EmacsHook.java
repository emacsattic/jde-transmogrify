/*
 * EmacsHook.java
 * Time-stamp: <2002-01-31 12:58:41 nascif>
 *
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

import net.sourceforge.transmogrify.symtab.IDefinition;
import net.sourceforge.transmogrify.symtab.Definition;
import java.io.File;
import java.util.Enumeration;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import net.sourceforge.transmogrify.symtab.Occurrence;

public class EmacsHook
  extends net.sourceforge.transmogrify.hook.Hook
{
  IEmacsEval evaluator_;

  public EmacsHook(IEmacsEval evaluator) {
    evaluator_ = evaluator;
  }
  
  public void showReferencesSourced(IDefinition definition) {
    StringBuffer buf = new StringBuffer();
    if (definition instanceof Definition) {
      Occurrence occ = ((Definition)definition).getOccurrence();
      buf.append(occ.getFile()).append(":");
      buf.append(occ.getLine()).append(":");
      buf.append(occ.getColumn()).append(": ");
    } else {
      buf.append("Sourced reference: ");
    }
    buf.append(definition.getQualifiedName());
    delegate(new String[]{"show-reference", 
                          quote(buf.toString().replace('\\', '/'))});
  }

  public void showReferencesNonsourced(IDefinition definition) {
    delegate(new String[]{"show-reference", 
                          quote(definition.getQualifiedName().replace('\\', '/'))});
  }

  /**
   * Sets focus to specified java source file
   * @param sourceFileName fully qualified or relative java source file name
   * @exception java.lang.Exception thrown on any type of error
   */
  public void openFile(String sourceFileName)
    throws Exception {
    delegate(new String[]{"open-file", 
                          quote(sourceFileName.replace('\\', '/'))});
  }

  /**
   * Returns an array of files selected by the user
   * 
   * @return the selected files
   */
  public File[] getFiles()
    throws Exception {
    // TODO: return data from jde-db-sources? or from a new variable?
    throw new NoSuchMethodException("getFiles");
    //return new File[0];         
  }

  /**
   * Gets the file name of the currently focused file.
   * @return focused file name
   * @exception java.lang.Exception thrown on any type of error
   */
  public String getCurrentFile()
    throws Exception {
    return delegate("get-current-file");
  }

  /**
   * Prompts for and returns user input
   * @return String entered from user
   * @param prompt question to ask the user
   * @param title summary of the prompt
   * @exception java.lang.Exception thrown on any type of error
   */
  public String getUserInput(String prompt, String title)
    throws Exception {
    return delegate(new String[]{"get-user-input", 
                                 quote(prompt), quote(title)});
  }

  public void displayMessage(String title, String message)
    throws Exception {
    delegate(new String[]{"display-message", quote(title), quote(message)});
  }

  public void displayException(Exception e, String description) {
    try {
      StringWriter swriter = new StringWriter();
      PrintWriter pwriter = new PrintWriter(swriter);
      e.printStackTrace(pwriter);
      pwriter.flush();
      swriter.flush();
      String stackTrace = swriter.toString();
      delegate(new String[]{"display-exception", 
                            quote(stackTrace), quote(description)});
      swriter.close();
      pwriter.close();
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }

  /**
   * Returns the entire focused source file
   * @return String of file
   * @exception java.lang.Exception thrown on any type of error
   */
  public String getText()
    throws Exception {
    return delegate("get-text");
  }

  /**
   * Retrieves a line from the focused file.
   *
   * @param lineNumber the line you wish to retrieve
   * @exception java.lang.Exception thrown on any type of error
   */
  public String getLine(int lineNumber) 
    throws Exception {
    return delegate(new String[]{"get-line", Integer.toString(lineNumber)});
  }

  /**
   * Selects a bunch of text.
   * @parm startLineNumber the line number you wish to start selection on.
   * @parm startOffset the position on the line where you with selection to start.
   * @parm endLineNumber the line number you wish to end selection on.
   * @parm endOffset the position on the line where you with selection to end.
   * @exception java.lang.Exception thrown on any type of error
   */
  public void selectText(int startLineNumber, 
                         int startOffset, 
                         int endLineNumber, 
                         int endOffset)
    throws Exception {
    delegate(new String[]{"select-text-lines",
                          Integer.toString(startLineNumber),
                          Integer.toString(startOffset),
                          Integer.toString(endLineNumber),
                          Integer.toString(endOffset)});
  }

  /**
   * Selects a bunch of text.
   * @parm startPos the charcter you wish to start selection on
   * @parm endPos the character you wish to end selection on.
   * @exception java.lang.Exception thrown on any type of error
   */
  public void selectText(int startPos, int endPos)
    throws Exception {
    delegate(new String[]{"select-text-region",
                                 Integer.toString(startPos),
                                 Integer.toString(endPos)});
  }

  /**
   * Deselects text
   * @exception java.lang.Exception thrown on any type of error
   */
  public void deSelectText()
    throws Exception {
    delegate("de-select-text");
  }

  /**
   * Returns the selected text in the form of a String
   * @return String the selected text.
   * @exception java.lang.Exception thrown on any type of error
   */
  public String getSelectedText()
    throws Exception {
    return delegate("get-selected-text");
  }

  /**
   * Returns the selected lines in an Enumeration
   * @return Enumeration the selected lines
   * @exception java.lang.Exception thrown on any type of error
   */
  public Enumeration getSelectedLines()
    throws Exception {
    String text = delegate("get-selected-lines");
    return new StringTokenizer(text, "\n\r");    
  }

  /**
   * Returns the 1 based line number of where the cursor is.
   * @return int the line number that the cursor is on.
   * @exception java.lang.Exception thrown on any type of error
   */
  public int getCaretLineNumber()
    throws Exception {
    String str = delegate("get-caret-line-number");
    return Integer.parseInt(str);
  }

  /**
   * Returns the 1 based offset of where the cursor is.
   * @return int the offset of where on the line the cursor is.
   * @exception java.lang.Exception thrown on any type of error
   */
  public int getCaretOffset()
    throws Exception {
    String str = delegate("get-caret-offset");
    return Integer.parseInt(str);
  }

  /**
   * Returns the 0 based position of where the cursor is.
   * @return int the position of where the cursor is in terms of total
   *         characters 
   * @exception java.lang.Exception thrown on any type of error
   */
  public int getCaretPos()
    throws Exception {
    String str = delegate("get-caret-pos");
    return Integer.parseInt(str);
  }

  public int getSelectionStart() 
    throws Exception {
    String str = delegate("get-selection-start");
    return Integer.parseInt(str);
  }

  public int getSelectionEnd() 
    throws Exception {
    String str = delegate("get-selection-end");
    return Integer.parseInt(str);
  }

  /**
   * Sets the 0 based position of where the cursor is.
   * @parm lineNumber the line number of where the cursor is to be set.
   * @parm offset the position on the line where the cursor is to be set.
   * @exception java.lang.Exception thrown on any type of error
   */
  public void setCaretPos(int lineNumber, int offset)
    throws Exception {
    delegate(new String[]{"set-caret-line-column",
                          Integer.toString(lineNumber),
                          Integer.toString(offset)});
  }

  /**
   * Sets the 0 based position of where the cursor is.
   * @parm pos the position where the cursor is to be set in terms of total
   * characters. 
   * @exception java.lang.Exception thrown on any type of error
   */
  public void setCaretPos(int pos) 
    throws Exception {
    delegate(new String[]{"set-caret-pos", Integer.toString(pos)});
  }

  //////////////////////////////////////////////////////////////
  // Utilities
  //////////////////////////////////////////////////////////////
  
  public String delegate(String cmd) {
    return evaluator_.evalLispCommand("(jde-transmogrify-hook-"+cmd+")");
  }

  public String delegate(String[] cmdAr) {
    StringBuffer buf = new StringBuffer("(jde-transmogrify-hook-");
    int len = cmdAr.length;
    for (int i = 0; i < len; i++) {
      String node = cmdAr[i];
      if (i > 0) {
        node = quote(node);
      }
      buf.append(node).append(" ");
    }
    buf.append(")");
    return evaluator_.evalLispCommand(buf.toString());    
  }

  public String quote(String str) {
    Debug.debug("quote IN: "+str);
    StringBuffer buf = new StringBuffer("\"");
    buf.append(str);
    int len = str.length() + 1;
    for (int i = 1; i < len; i++) { // skip initial quote
      if (buf.charAt(i) == '\"') {
        buf.insert(i, "\\");
        len += 1;
        i += 1;
      }
    }
    buf.append("\"");
    Debug.debug("quote out: "+buf.toString());
    return buf.toString();
  }
}
