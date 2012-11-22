package gui;
import java.awt.event.*;
import javax.swing.*;
/**
 * <p>Title: step listener</p>
 * <p>Description: Listener to listen for action in step field in control panel</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Craig Eales
 * @version 1.0
 */

public class StepListener implements ActionListener
{
  WorkSpace workSpace;
  String text;
  public StepListener(WorkSpace w)
  {
    workSpace = w;
  }

  public void actionPerformed(ActionEvent e)
  {
    //text = new String(((JTextField)(e.getSource())).getSelectedText());
    //if(the text can be parsed as an integer....)
    //workSpace.setStepSize(((JTextField)(e.getSource())).getSelectedText());
  }
}