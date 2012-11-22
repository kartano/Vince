//=============================================================
//
// AT90S8535_Adapter
//
// MODIFICATION HISTORY:
// 13/08/03 -     1.0.1       Very minor tweaks
// ??/??/03 -     1.0.0       Prototype
//=============================================================

package vincecomponents;

import gui.*;
import gui.toolbox.*;
import java.util.*;
import Utils.*;
import AVR.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * <p>Title: VinceGUIAdapter
 * <p>Description:  Adapter Class to incorporate Vince into GUI.
 * <p>Copyright: Copyright (c) 2003</p>
 * @author craig eales
 * @author Simon Mitchell
 * @version 1.0.1
 */

public class AT90S8535_Adapter extends Observable implements VinceGUIComponent,MemoryComponent
{
  private Atmel cpu;
  private ImageIcon image;
  private Point position;
  private String[] progMemData;
  private String[] dataMemData;
  private GridLayout controlBoxLayout;
  private JPopupMenu popupMenu = new JPopupMenu();

// boundaries for selection box
  private int WIDTH;
  private int HEIGHT;
  private toolboxitem selection;

  // set up windows/buttons
  private JFrame controlFrame;
  private JPanel controlContentPane;
  private JButton executeButton;
  private JButton stepButton;
  private JButton burnMem;
  private JLabel controlText;
  private JLabel pcValue;
  private MemFrame progMemFrame;
  private MemFrame dataMemFrame;
  // For use with data memory frames  private final int PROGRAM_MEMORY = 0;
  private final int DATA_MEMORY = 1;
  
  public AT90S8535_Adapter(Point p, toolboxitem theToolboxItem)
  {
      // TO DO:  Check and see if this is used anywhere
    //AT90S8535_Adapter myComponent=this;
    
    cpu = new AT90S8535();
    controlBoxLayout = new GridLayout(2,3);

// set up associated image file and selection boundaries
    image = theToolboxItem.getIcon();
    WIDTH = image.getIconWidth();
    HEIGHT = image.getIconHeight();
    position = p;
    selection = theToolboxItem;

// set mem and register data
    updateData(); // use an observer to keep data updated

// create windows for use in gui...
// creating all windows at start and then just showing/hiding them as needed
// thus may use a lot of memory storage but responds quicker
// possibly change back to creating windows dynamically, ie as needed.

        controlFrame = new JFrame("Control Window");
        controlContentPane = new JPanel();
        executeButton = new JButton("Run");
        stepButton = new JButton("Step");
        burnMem = new JButton("Burn Program");
        controlText = new JLabel("PC");
        pcValue = new JLabel("" + cpu.PC());
        progMemFrame = new MemFrame(PROGRAM_MEMORY,"Programming Memory",this,1);//1 ie 1-byte bus
        dataMemFrame = new MemFrame(DATA_MEMORY,"Data Memory",this,1);//1 ie 1-byte bus
        this.addObserver(progMemFrame); //add observers...
        this.addObserver(dataMemFrame);
        controlContentPane.setLayout(controlBoxLayout);
        controlFrame.setContentPane(controlContentPane);
        controlContentPane.add(controlText);
        controlContentPane.add(pcValue);
        controlContentPane.add(executeButton);
        controlContentPane.add(stepButton);
        controlContentPane.add(burnMem);

        // sets flag indicating that window is open
        controlFrame.addWindowListener(new WindowAdapter() 
        {
            public void windowClosing(WindowEvent e) 
            {
            }
        });

        // behaviour when execute button is clicked
        executeButton.addMouseListener(new MouseInputAdapter() 
        {
            public void mouseClicked(MouseEvent e) 
            {
                try
                {
                  ExecuteNextInstruction();
                }
                catch(Exception e2){}//dodgy!!!
            }
        });

        // behaviour when burn button is pressed
        burnMem.addMouseListener(new MouseInputAdapter() 
        {
              public void mouseClicked(MouseEvent e) 
              {
                JFileChooser chooser = new JFileChooser();
                // ExampleFileFilter pinched from Sun.
                ExampleFileFilter filter = new ExampleFileFilter();
                filter.addExtension("bin");
                filter.addExtension("hex");
                filter.setDescription("bin & hex Images");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(controlFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) 
                {
                  String filename = chooser.getSelectedFile().getAbsolutePath();
                  System.out.println("You chose to open this file: " + filename);
                  try 
                  {
                    // add support for bin files!!!!!
                    if(filename.endsWith("bin"))
                    {
                      cpu.LoadBinaryFile(filename);
                    }
                    else
                    {
                      if(filename.endsWith("hex"))
                      {
                        cpu.LoadHexFile(filename);
                      }
                      else
                      {
                        // should throw an exception
                        System.out.println("not a bin or hex file");
                        System.exit(1);
                      }
                    }
                    setChanged();
                    notifyObservers();
                  }
                  catch (AVR.RuntimeException exception) {
                    System.out.println(exception.toString());
                    System.exit( -1);
                  }
                }
              }
            });

              progMemFrame.addWindowListener(new WindowAdapter()
              {
                public void windowClosing(WindowEvent e)
                {
                }
              });

            dataMemFrame.addWindowListener(new WindowAdapter()
              {
                    public void windowClosing(WindowEvent e)
                    {
                    }
                });
  }
        // behaviour of component on a double click!
  public void doubleClick()
  {

    if (controlFrame != null)
    {
      controlFrame.pack();
      controlFrame.setVisible(true);
    }
    if (progMemFrame != null)
    {
      progMemFrame.pack();
      progMemFrame.setVisible(true);
    }
    if (dataMemFrame != null)
    {
      dataMemFrame.pack();
      dataMemFrame.setVisible(true);
    }
  }

  public void rightClick(Point p, Container c)
  {
    // will be used to display a popup menu allowing the user to choose
    // which windows will be displayed
    //popupMenu.show(c,p.x,p.y);
  }

  private void updateData()
  {
      // TO DO:  Find a way to make this a bit more efficient eventually!
      // If the program or data memory values change, this won't be reflected
      // in the local copies of the arrays.  I.E:  If the cpu's Data Memory changes,
      // the local dataMemData will have a copy of the old data and won't trigger an update.
    progMemData=cpu.getProgMemoryArray();
    dataMemData=cpu.getDataMemoryArray();
  }

  public void setPosition(Point p) { position = p; }

  public Point getPosition() { return position; }

  public Dimension getSize() { return new Dimension(WIDTH,HEIGHT); }

  public boolean pointInBoundaries(Point p)
  {
      return (p.x > position.x-WIDTH/2 && p.x<position.x+WIDTH/2 && p.y > position.y-HEIGHT/2 && p.y < position.y+HEIGHT/2);
  }

  public toolboxitem getSelection() { return selection; }

  public ImageIcon getIcon() { return image; }

  public void ExecuteNextInstruction()
  {
    try
    {
      cpu.ExecuteNextInstruction();
      this.setChanged();
      this.notifyObservers();
    }catch(Exception e2){}
  }

  public String[] getMem(int i)
  {
    switch (i)
    {
      case PROGRAM_MEMORY :
            return cpu.getProgMemoryArray();
      case DATA_MEMORY :
            return cpu.getDataMemoryArray();
      default :
            return new String[]{"-1"};
    }
  }

  public void updateState()
  {
      // TO DO:  Implement
  }

  public int getState(int i) { return 1; }

}
