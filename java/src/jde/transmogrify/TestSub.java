package jde.transmogrify;

import java.io.File;

/**
 * Test.java
 *
 *
 * Created: Tue Sep 11 10:07:35 2001
 *
 * @author <a href="mailto:nascif@nortelnetworks.com">Nascif A. Abousalh Neto</a>
 */
public class TestSub extends Test
{
  public TestSub()
  {
    try
    {
      File f = new File("c:/tmp/test.txt");
      
      f.createNewFile();
      testRenamedMethod();
    } 
    catch (Exception e)
    {
    } 
  } 
  
  public int subMethod()
  {
    int x;
    
    x = 2;
      
    testRenamedMethod();
    
    return x;
  } 
} 
