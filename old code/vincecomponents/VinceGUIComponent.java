package vincecomponents;
import java.awt.*;
import javax.swing.*;

/**
 * <p>Title: VinceGUIComponent </p>
 * <p>Description: Interface for all components used in the Vince GUI </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author craig eales
 * @version 1.0
 */

public interface VinceGUIComponent
{
  // behaviour if component icon is double clicked
  public void doubleClick();

  // behaviour if component icon is right clicked
  public void rightClick(Point p,Container c);

  // method returns if the point is within the boundaries of the component.
  // The point p is a point in the workspace reference frame, each component
  // maintains a record of it's own position within the workspace, and
  // the extents of it's boundaries.
  public boolean pointInBoundaries(Point p);

  // resolves component by processing inputs
  public void updateState();

  // returns the state of pin given by pin_number
  // must throw an exception if pin_number is an illegal number
  public int getState(int pin_number);

  // Returns an object of type ComponentSelection that is added to the toolbox,
  // and that is used to create VinceGUIComponents.
  public gui.toolbox.toolboxitem getSelection();

  // used to update the position of the component.
  public void setPosition(Point p);

  // used to retrieve the position of the component
  public Point getPosition();

  // used to retrieve the ImageIcon (ie image)
  public ImageIcon getIcon();
}
