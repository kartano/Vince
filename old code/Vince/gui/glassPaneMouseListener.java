package gui;
import Utils.Assertion;

/**
 * <p>Title: Glass Pane Mouse Listener</p>
 * <p>Description: Listener for glasspane to intercept mouse events </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Craig Eales
 * @version 1.0
 */

// this listener is crap, must fix it!!!!!!!!!

import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

public class glassPaneMouseListener extends MouseInputAdapter
{
  private WorkSpace workSpace;
  private Container contentPane;
  private MyGlassPane glassPane;
  private Component component;
  private Component comp;

  // the listener must have a reference to the underlying workSpace JPanel
  public glassPaneMouseListener(WorkSpace w)
  {
    workSpace = w;
    contentPane=w.getContentPane();
    glassPane=(MyGlassPane)w.getGlassPane();
  }

  // when mouse is moved with a button pressed
  public void mouseDragged(MouseEvent e)
  {
    {
      Point p = e.getPoint(); //point in glasspane that event occured
      // containerPoint is the point translated to the contentpane reference frame
      Point containerPoint = SwingUtilities.convertPoint(glassPane, p,
          contentPane);
      // component is the component under the mouse pointer, below the glasspane
      component = SwingUtilities.getDeepestComponentAt(contentPane,
          containerPoint.x, containerPoint.y);

      // if moved inside of workspace
      if (component == workSpace) {
        // if component is under mouse pointer draw picture under pointer
        if (workSpace.getSelection() != null) {
          glassPane.setImage(workSpace.getSelection().getIcon());
          glassPane.setPoint(e.getPoint());
        }
        else {
          if (workSpace.getComponent() != null) {
            glassPane.setImage(workSpace.getComponent().getIcon());
            glassPane.setPoint(e.getPoint());
          }
          else {
            // thus no icon at mouse pointer
            glassPane.setPoint(null);
          }
        }
      }
      else
      // thus moved outside of workspace
      {
        // if  component is under mouse pointer
        if ( (workSpace.getComponent() != null) || (workSpace.getSelection() != null)) {
          // currently drawing full icon, should draw little icon tho....
          // for some reason it causes an error, check the internet!
          //glassPane.setImage(new ImageIcon("images/small_icon.jpg"));
          glassPane.setPoint(e.getPoint());
        }
        else {
          // thus no icon at mouse pointer
          glassPane.setPoint(null);
        }
      }
    }
  }

  // mouse moved and no buttons pressed
  public void mouseMoved(MouseEvent e)
  {
    Point p = e.getPoint();
    Point containerPoint = SwingUtilities.convertPoint(glassPane, p,contentPane);
    component = SwingUtilities.getDeepestComponentAt(contentPane,
        containerPoint.x, containerPoint.y);
    // if moved inside of workspace
    if (component == workSpace)
    {
      // if  component is under mouse pointer draw picture under pointer
      if (workSpace.getSelection() != null)
      {
        glassPane.setImage(workSpace.getSelection().getIcon());
        glassPane.setPoint(e.getPoint());
      }
      else
      {
        if(workSpace.getComponent() != null)
        {
          glassPane.setImage(workSpace.getComponent().getIcon());
          glassPane.setPoint(e.getPoint());
        }
        else
        {
          glassPane.setPoint(null);
        }
      }
    }

    else
    // thus moved outside of workspace
    {
      // if  component is under mouse pointer
      if ((workSpace.getComponent() != null)||(workSpace.getSelection() != null))
      {
        // should draw little icon tho....
        //glassPane.setImage(new ImageIcon("images/small_icon.jpg"));
        glassPane.setPoint(e.getPoint());
      }
      else
      {
        glassPane.setPoint(null);
      }
    }
  }


  public void mousePressed(MouseEvent e)
  {
    Point p = e.getPoint();
    Point containerPoint = SwingUtilities.convertPoint(glassPane,p,contentPane);
    component = SwingUtilities.getDeepestComponentAt(contentPane,
        containerPoint.x,containerPoint.y);
    // if clicked inside of workspace
    if(component==workSpace)
    {
      // if an instantiated component is currently active (thus under mouse pointer)
      if(workSpace.getComponent() != null)
      {
        // places the already instantiated component back in the workspace
        // does not instantiate a new one
        workSpace.placeComponent(p);
      }
      else
      {
        // if an item is selected but a component is not yet instantiated
        if (workSpace.getSelection() != null)
        {
          // instantiates a new component based on the current selection
          workSpace.createComponent(e.getPoint());
        }
        else
        // no item selected or active
        {
          // passes point to workspace to determine if there
          // is a VinceComponent at mouse position
          if((e.getButton()!=e.BUTTON2)&&(e.getButton()!=e.BUTTON3))
          {
            workSpace.selectPoint(p);
          }
        }
      }
    }
    else
    // thus clicked outside of workspace
    {
      // if an instantiated component is currently active (thus under mouse pointer)
      if(workSpace.getComponent() != null)
      {
        // when a component is dragged back off the workspace it is removed completely
        workSpace.removeComponent();
      }
      else
      {
        // if an item is selected but a component is not yet instantiated
        if (workSpace.getSelection() != null)
        {
          // clear selection
          workSpace.setSelection(null);
          component.dispatchEvent(SwingUtilities.convertMouseEvent(glassPane,e,component));
        }
        else
        // no item selected or active
        {
          component.dispatchEvent(SwingUtilities.convertMouseEvent(glassPane,e,component));
        }
      }

    }
  }

  public void mouseClicked(MouseEvent e)
  {
    Point p = e.getPoint();
    Point containerPoint = SwingUtilities.convertPoint(glassPane,p,contentPane);
    component = SwingUtilities.getDeepestComponentAt(contentPane,
            containerPoint.x,containerPoint.y);
    component.dispatchEvent(SwingUtilities.convertMouseEvent(glassPane,e,component));
    if(component == workSpace)
    {
      // on a double click...
      if (e.getClickCount() == 2)
      {
        workSpace.doubleClick(e.getPoint());
      }
      if((e.getButton()==MouseEvent.BUTTON2)||(e.getButton()==MouseEvent.BUTTON3))
      {
        workSpace.rightClick(e.getPoint());
      }
    }
  }

  public void mouseReleased(MouseEvent e)
  {
    if (e.getButton() == e.BUTTON1) {
      Point p = e.getPoint();
      Point containerPoint = SwingUtilities.convertPoint(glassPane, p,
          contentPane);
      component = SwingUtilities.getDeepestComponentAt(contentPane,
          containerPoint.x, containerPoint.y);
      // if clicked inside of workspace
      if (component == workSpace) {
        // if an instantiated component is currently active (thus under mouse pointer)
        if (workSpace.getComponent() != null) {
          workSpace.placeComponent(p);
        }
        else {
          // if an item is selected but a component is not yet instantiated
          if (workSpace.getSelection() != null) {
            // currently does not prevent overlapping components
            workSpace.createComponent(e.getPoint());
          }
          else
          // no item selected or active
          {
            // currently does not prevent overlapping components
            //if(there is an item under mouse on workSpace)
            //{
            // w.setComponent(c);
            //}
          }
        }
      }
      else
      // thus released outside of workspace
      {
        // if an instantiated component is currently active (thus under mouse pointer)
        if (workSpace.getComponent() != null) {
          workSpace.removeComponent();
        }
        else {
          // if an item is selected but a component is not yet instantiated
          if (workSpace.getSelection() != null) {
            workSpace.setSelection(null);
            Assertion.assertion(component!=null);
            component.dispatchEvent(SwingUtilities.convertMouseEvent(glassPane,
                e, component));
          }
          else
          // no item selected or active
          {
            Assertion.assertion(component!=null);
            component.dispatchEvent(SwingUtilities.convertMouseEvent(glassPane,
                e, component));
          }
        }
      }
    }
  }
}