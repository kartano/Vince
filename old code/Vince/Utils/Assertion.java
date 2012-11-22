package Utils;

/**
 * <p>Title: Assertion
 * <p>Description: Assertion Class</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Craig Eales
 * @version 1.0
 */

public class Assertion
{
    public static void assertion(boolean condition)
    {
      if (!condition)
      {
        System.out.println("Assertion Failed");
        System.exit(1);
      }
    }
}