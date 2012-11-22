package gui.toolbox;
import vincecomponents.VinceGUIComponent;
import javax.swing.*;
import java.awt.*;

/**
 * <p>Title: Selection interface</p>
 * <p>Description: Interface for toolbox menu items in vince GUI </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Craig Eales
 * @version 1.0
 */

public interface toolboxitem
{
  // returns the text to be displayed in toolbox
  public String toString();
  // returns icon to be displayed when component dragged from toolbox
  public ImageIcon getIcon();
  // instantiates and returns a new component associated with this selection
  public VinceGUIComponent createComponent(Point p);
}