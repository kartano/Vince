package vincecomponents;

/**
 * <p>Title: Memory Component Interface </p>
 * <p>Description: If VinceGUIComponent contains memory, it must implement
 *    this interface.  getMem(int i) must return an int[] of the memory contents.
 *    The passed parameter i indicates which memory (ie prog/data) is to be
 *    returned.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

// TO DO: Sort out exceptions due to passing an int i that is too large,
//        ie i = 1 when there is only one type of memory.

public interface MemoryComponent
{
  public String[] getMem(int i);
}