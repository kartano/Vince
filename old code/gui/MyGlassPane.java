package gui;
import javax.swing.*;
import java.awt.*;

/**
 * <p>Title: GlassPane</p>
 * <p>Description: Custom glass pane for vince GUI </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Craig Eales
 * @version 1.0
 */

public  class MyGlassPane extends JPanel
{
  private Point point = null;
  private ImageIcon image = null;
  private int height;
  private int width;

  public MyGlassPane()
  {
    setOpaque(false);
  }

  public void paint(Graphics g)
  {
    // paints an icon at point if point and image is set.
    // Used for drawing an icon at the moues pointer.
    // Efficiency could be improved significantly.

    if ((point != null)&&(image !=null))
    {
      height = image.getIconHeight();
      width = image.getIconWidth();
      image.paintIcon(null,g,point.x-width/2,point.y-height/2);
    }

  }

  public void setImage(ImageIcon i)
  {
    image = i;
  }

  public void setPoint(java.awt.Point p)
  {
    point = p;
    repaint();
  }
}
