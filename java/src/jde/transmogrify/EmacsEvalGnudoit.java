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

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class EmacsEvalGnudoit 
  implements IEmacsEval
{
  protected String gnudoitApp_ = "gnudoit";
    
  

  public void setGnudoitApp(String gnudoitApp) 
  {
    gnudoitApp_ = gnudoitApp;
  }

  public String evalLispCommand(String eLispCommand) 
  {
    Debug.debug(">> "+eLispCommand);
    StringBuffer buf = new StringBuffer();
    String ret = "";
    try {
      String cmdAr[] = new String[2];
      cmdAr[0] = gnudoitApp_;
      cmdAr[1] = eLispCommand;
      Process pProcess = Runtime.getRuntime().exec(cmdAr);
      InputStream in = pProcess.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      String inputLine;
      while((inputLine = reader.readLine()) != null) {
        buf.append(inputLine);          
      }
      pProcess.waitFor();
      ret = buf.toString();
      Debug.debug("<< "+ret);
    } catch(Exception e) {
      Debug.debug(e);
    }
    return ret;
  }


  public boolean isValid() {
    String result = evalLispCommand("(1+ 1)");
    return result.equals("2");
  }

}
