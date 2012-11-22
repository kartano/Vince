package gui;

import gui.toolbox.*;
import Utils.*;
import vincecomponents.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * <p>Title: WorkSpace </p>
 * <p>Description: A workspace for arranging and connecting components
 *    in the VInceGUISimulator.  The workspace manages instantiated components
 *    and references to crrently selected components and menu items.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Craig Eales
 * @version 1.0
 */

public class WorkSpace extends JPanel
{
  private toolboxitem selection;
  private VinceGUIComponent component;
  private Container glassPane;
  private Vector componentList;
  private Vector inputPinList;
  private Vector outputPinList;
  private Vector wireList;
  private Iterator iterator;

  public WorkSpace()
  {
    selection = null;
    component = null;
    componentList = new Vector();
    inputPinList = new Vector();
    outputPinList = new Vector();
    wireList = new Vector();
  }

  // on a repaint the workspace paints the images of all currently instantiated
  //   components.
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    iterator=componentList.iterator();
    while(iterator.hasNext())
    {
      myPainter((VinceGUIComponent)iterator.next(),g);
    }
  };

  // when an item is selected in toolbox, a reference is kept to the selection
  //   in the workspace.
  public void setSelection(toolboxitem s)
  {
    selection = s;
  }

  public toolboxitem getSelection()
  {
    return selection;
  }

  // If an instantiated component is selected a reference is kept in the workspace.
  public void setComponent(VinceGUIComponent c)
  {
    component = c;
  }

  public VinceGUIComponent getComponent()
  {
    return component;
  }

  public Container getContentPane()
  {
    return this.getRootPane().getContentPane();
  }

  public Container getGlassPane()
  {
    return (Container)this.getRootPane().getGlassPane();
  }

  // if an already instantiated component is currently selected and to be
  //   placed back in the workspace, then it is added to the componentList.
  //   Does not need to be instantiated first.
  public void placeComponent(Point p)
  {
    componentList.add(component);
    component.setPosition(p);
    selection = null;
    component = null;
  }

  // instantiates a new component using the createComponent method of the
  //   selection object.
  public void createComponent(Point p)
  {
    Assertion.assertion(p!=null);
    Assertion.assertion(selection.createComponent(p)!=null);
    Assertion.assertion(componentList!=null);
    componentList.add(selection.createComponent(p));
    selection = null;
    component = null;
  }

  // remove component from list
  public void removeComponent()
  {
    Assertion.assertion(componentList!=null);
    componentList.remove(component);
    selection = null;
    component = null;
  }

  // if workspace recieves a doubleClick, a check is made to see if there is an
  //   object at that position of the workspace.  If there is the doubleClick()
  //   method of the object is called.
  public void doubleClick(Point p)
  {
    iterator=componentList.iterator();
    while(iterator.hasNext())
    {
      //((VinceGUIComponent)iterator.next()).updateState();
      VinceGUIComponent comp = ((VinceGUIComponent)iterator.next());
      if(comp.pointInBoundaries(p))
      {
        comp.doubleClick();
        break;
      }
    }
  }

  public void rightClick(Point p)
  {
    iterator=componentList.iterator();
    while(iterator.hasNext())
    {
      VinceGUIComponent comp = ((VinceGUIComponent)iterator.next());
      if(comp.pointInBoundaries(p))
      {
        comp.rightClick(p,glassPane);
        break;
      }
    }
  }

  // if mouse is pressed on a component then it is removed from the workspace
  // and a reference is kept to it.
  public void selectPoint(Point p)
  {
    iterator=componentList.iterator();
    while(iterator.hasNext())
    {
      VinceGUIComponent comp = ((VinceGUIComponent)iterator.next());
      if(comp.pointInBoundaries(p))
      {
        componentList.removeElement(comp);
        component = comp;
        selection = null;
        break;
      }
    }
  }

  // painter routine to paint the image of each icon onto the workspace.
  private void myPainter(VinceGUIComponent c,Graphics g)
  {
    Point p = c.getPosition();
    ImageIcon i = c.getSelection().getIcon();
    i.paintIcon(null,g,p.x-i.getIconWidth()/2,p.y-i.getIconHeight()/2);
  }

  public void updateComponents()
  {
    iterator=componentList.iterator();
    while(iterator.hasNext())
    {
      ((VinceGUIComponent)iterator.next()).updateState();
    }
  }

  public void updateWires()
  {
    iterator=wireList.iterator();
    while(iterator.hasNext())
    {
      ((Wire)iterator.next()).updateState();
    }
  }

  public void updateInputs()
  {
    iterator=inputPinList.iterator();
    while(iterator.hasNext())
    {
      ((Pin)iterator.next()).updateState();
    }
  }

  public void updateOutputs()
  {
    iterator=outputPinList.iterator();
    while(iterator.hasNext())
    {
      ((Pin)iterator.next()).updateState();
    }
  }
}
