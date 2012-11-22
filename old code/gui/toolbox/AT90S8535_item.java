package gui.toolbox;
import vincecomponents.*;
import java.awt.*;
import javax.swing.*;

/**
 * <p>Title: AT90S8535 Item </p>
 * <p>Description: Item used for managing graphics and menu for the above item in GUI </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Craig Eales
 * @version 1.0
 */

public class AT90S8535_item implements toolboxitem
{
  private String name;
  private ImageIcon icon;
  private vincecomponents.AT90S8535_Adapter comp;

  public AT90S8535_item()
  {
    // name will be displayed in list, icon is the graphic used to display component
    name = new String("AT90S8535");
    icon = new ImageIcon("./images/AT90S8535.jpg");
  }

    public String toString(){return name;}
    public ImageIcon getIcon(){return icon;}

    // method workspace uses to instantiate new VinceGUIComponents in workspace
    public VinceGUIComponent createComponent(Point p)
    {
      comp = new AT90S8535_Adapter(p,this);
      return comp;
    }
}
