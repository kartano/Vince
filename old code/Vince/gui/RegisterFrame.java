package gui;
import vincecomponents.VinceGUIComponent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * <p>Title: Register frame</p>
 * <p>Description: Custom Frame for displaying register contents </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Craig Eales
 * @version 1.0
 */

public class RegisterFrame extends JFrame
{
  private JFrame frame;
  private JPanel pane;
  private JTable dataTable;
  private Object[][] dataArray;
  private Object[] colNames;
  private VinceGUIComponent component;

  public RegisterFrame(byte[] data, VinceGUIComponent c)
  {
    super("Register Window");
    component = c; // maintains a reference to the component that opened it
    dataArray = new Object[data.length][2];
    colNames = new Object[2];
    colNames[0]=new String("Register");
    colNames[1]=new String("Value");
    for(int i=0;i<data.length;i++)
    {
      dataArray[i][0] = new String("Register " + i*4);
      dataArray[i][1] = new Integer(data[i]);
    }
    pane = new JPanel();
    dataTable = new JTable(dataArray,colNames);
    this.setContentPane(pane);
    pane.add(dataTable);
  }
}