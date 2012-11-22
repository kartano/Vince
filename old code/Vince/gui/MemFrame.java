//======================================================
//
// MemFrame
//
// MODIFICATION HISTORY:
// 13/08/2003     -     1.0.1       Added column headers
// ??/??/2003     -     1.0.0       Prototype
//======================================================

package gui;

import vincecomponents.MemoryComponent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;


/**
 * <p>Title: Memory Frame</p>
 * <p>Description: Custom Frame for displaying memory contents</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Craig Eales
 * @author Simon Mitchell
 * @version 1.0.1
 */

public class MemFrame extends JFrame implements java.util.Observer
{
  private JFrame frame;
  private JPanel pane;
  private JTable dataTable;
  private Object[][] dataArray;
  private Object[] colNames = { "Location", "Value" };
  private MemoryComponent component;
  private int memType;
  private TableModel dataModel;
  private int number_of_columns;

  public MemFrame(int memNumber, String title, MemoryComponent c,int col)
  {
    super(title + " Window");
    number_of_columns = col; //number of columns for table, not including address column
    component = c; // maintains a reference to the component that opened it
    String[] data = component.getMem(memType);
    dataModel = new MemoryDataModel(data,number_of_columns);
    
    memType = memNumber;
    pane = new JPanel();

    dataTable = new JTable(dataModel);
    dataTable.removeEditor();
    JScrollPane scroll = new JScrollPane(dataTable);
    this.setContentPane(pane);
    pane.add(scroll);
  }

 // public void paintComponent(Graphics g)
//  {

 // }

  public void update(java.util.Observable obsv, Object obj)
  {
    String[] data = component.getMem(memType);
    dataArray = new Object[data.length/number_of_columns][number_of_columns+1];

    for(int i=0;i<(data.length/number_of_columns);i++)
    {
     ((MemoryDataModel)dataModel).setValueAt(Integer.toHexString(i*number_of_columns),i,0);
     for(int j = 0;j<number_of_columns;j++)
     {
      ((MemoryDataModel)dataModel).setValueAt((data[(number_of_columns*i)+j]),i,j+1);
     }
    }
    repaint();
  }


}
