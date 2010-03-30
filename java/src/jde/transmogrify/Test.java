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
public class Test
{
  public Test()
  {
    carlos();
    testRenamedMethod();
  } 
  
  public void testRenamedMethod()
  {
    newTayfun();
    
    int x = 10;
    String myString = "this is a test string" + x;
    
    System.out.println(myString);
  } 
  
  public void newTayfun()
  {
    new2();
  } 
  
  public void new2()
  {
    int x;
    
    if (true)
    {
      x = 1;
    } 
  } 
  
  public void carlos()
  {
    String i= null;
    String j= null;

    try {
      File f = new File("c:/tmp/test.txt");
      

      f.createNewFile();
    } catch (Exception e) {}

    String k= null;
  } 
} 
