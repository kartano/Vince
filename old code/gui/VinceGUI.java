package gui;

import sim.*;

/**
 * <p>Title: </p>
 * <p>Description: VinceGUI application class, sets up GUI </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Craig Eales
 * @version 1.4.8
 */

import gui.toolbox.*;
import vincecomponents.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class VinceGUI {
  // pane is the top level frame
  private JFrame pane;
  private JPanel contentPane;
  private JButton registerButton = new JButton("Register Window");
  private Border border1;
  private JSplitPane jMainSplitPane = new JSplitPane();
  private Border border2;
  private Border border3;
  private JList MCUJList;
  private JList OutputDeviceJList;
  private JList InputDeviceJList;
  private JList OtherJlist;
  private String major;
  private String minor;
  private String version;
  private Vector MCUs = new Vector();
  private String[] Input;
  private String[] Output;
  private String[] Other;
  private JPanel rightPanel = new JPanel();
  private JPanel controlPanel = new JPanel();
  private JButton goButton = new JButton("Go!"); // add icon later
  private JButton stepButton = new JButton("Step"); // add icon later
  // private step field
  private WorkSpace workSpace = new WorkSpace();
  private JScrollPane scrollOutput = new JScrollPane();
  private JScrollPane scrollMCUs = new JScrollPane();
  private JScrollPane scrollOther = new JScrollPane();
  private JTabbedPane toolboxTabbedPane = new JTabbedPane();
  private JScrollPane scrollInput = new JScrollPane();
  private JTextField stepText = new JTextField();
  private JLabel speedBarLabel = new JLabel("Speed");
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private GridBagLayout controlPanelLayout = new GridBagLayout();
  private GridBagConstraints ctoolboxLabel = new GridBagConstraints();
  private GridBagConstraints ctoolboxPanel = new GridBagConstraints();
  private GridBagConstraints cWorkSpace = new GridBagConstraints();
  private GridBagConstraints cRightPanel = new GridBagConstraints();
  private GridBagConstraints cGoButton = new GridBagConstraints();
  private GridBagConstraints cSpeedBar  = new GridBagConstraints();
  private GridBagConstraints cStepButton = new GridBagConstraints();
  private GridBagConstraints cStepText = new GridBagConstraints();
  private GridBagConstraints cSpeedBarLabel = new GridBagConstraints();
  private JLabel toolboxLabel = new JLabel();
  private MyGlassPane glassPane = new MyGlassPane();
  JScrollBar speedBar = new JScrollBar(JScrollBar.HORIZONTAL);
  private final simulator sim = new simulator(workSpace);


  public VinceGUI() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName() );
      //  Vince version number
      major = new String("1");
      minor = new String("4");
      version = new String("8");
      setUpComponentLists();
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  private void jbInit() throws Exception {
    this.initLayouts();
    this.initTopLevelFrame();
    this.createBorders();
    this.initWorkSpace();
    this.initToolboxPane();
    this.initListeners();

    pane.pack();
    pane.setVisible(true);
    sim.start();
  }

  private void setUpComponentLists()
  {
    MCUs.add(new AT90S8535_item());
   // Other=new String[]{"Clock Crystal","Power","Transistor","Resistor","Capacitor"};
    MCUJList = new JList(MCUs);
    // OtherJlist = new JList(Other);
    scrollMCUs = new JScrollPane(MCUJList);
    // scrollOther = new JScrollPane(OtherJlist);
  }

  private void initTopLevelFrame()
  {
    // add a layout manager!!!

    // create main Frame
    pane = new JFrame("Vince V" + major +"."+minor+"."+version);

    // create main content pane
    contentPane = new JPanel();
    contentPane.setBackground(Color.white);
    contentPane.setBorder(border2);
    contentPane.add(workSpace);
    contentPane.add(rightPanel);
    contentPane.setLayout(gridBagLayout1);
    contentPane.setPreferredSize(new Dimension(1000,1000));

    // set main glasspane
    pane.setGlassPane(glassPane);
    glassPane.setVisible(true);

    // set main contentpane
    pane.setContentPane(contentPane);
    pane.setState(Frame.NORMAL);

    // add components to control panel
    controlPanel.add(goButton);
    controlPanel.add(stepButton);
    controlPanel.add(stepText);
    controlPanel.add(speedBarLabel);
    controlPanel.add(speedBar, null); //null?
    controlPanel.setMaximumSize(new Dimension(200,60));
    controlPanel.setMinimumSize(new Dimension(200,50));
    controlPanel.setLayout(controlPanelLayout);

    // set up speed selection bar
    speedBar.setEnabled(true);
    speedBar.setMinimumSize(new Dimension(5, 16));
    speedBar.setMaximumSize(new Dimension(300, 16));
    stepText.setColumns(3);

    // set up right panel (includes toolbox and label and control panel)
    rightPanel.add(toolboxLabel, null);
    rightPanel.add(toolboxTabbedPane, null);
    rightPanel.add(controlPanel);
    rightPanel.setBorder(border2);
    rightPanel.setLayout(gridBagLayout1);
    rightPanel.setMaximumSize(new Dimension(200, 2000));
    rightPanel.setPreferredSize(new Dimension(200,1200));
    rightPanel.setMinimumSize(new Dimension(200, 400));
  }

  private void createBorders()
  {
    // create border types
    border1 = BorderFactory.createLineBorder(Color.black,1);
    border2 = BorderFactory.createLineBorder(Color.black,2);
    border3 = new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(134, 134, 134));
  }

  private void initLayouts()
  {
    cRightPanel.anchor = GridBagConstraints.EAST;
    cRightPanel.fill = GridBagConstraints.BOTH;
    ctoolboxLabel.anchor = GridBagConstraints.NORTH;
    ctoolboxLabel.gridwidth = GridBagConstraints.REMAINDER;
    cWorkSpace.fill = GridBagConstraints.BOTH;
    cWorkSpace.weightx = 1.0;
    cWorkSpace.weighty = 1.0;
    ctoolboxPanel.fill = GridBagConstraints.BOTH;
    ctoolboxPanel.gridwidth = GridBagConstraints.REMAINDER;
    ctoolboxPanel.weighty = 1.0;
    gridBagLayout1.setConstraints(workSpace,cWorkSpace);
    gridBagLayout1.setConstraints(rightPanel,cRightPanel);
    gridBagLayout1.setConstraints(toolboxLabel,ctoolboxLabel);
    gridBagLayout1.setConstraints(toolboxTabbedPane,ctoolboxPanel);
    cGoButton.fill = GridBagConstraints.NONE;
    cGoButton.weightx=1.0;
    cGoButton.weighty=1.0;
    cStepButton.weightx=1.0;
    cGoButton.weighty=1.0;
    cStepText.weightx=1.0;
    cStepButton.fill = GridBagConstraints.HORIZONTAL;
    cStepText.gridwidth = GridBagConstraints.REMAINDER;
    cGoButton.fill = GridBagConstraints.HORIZONTAL;
    cSpeedBar.fill = GridBagConstraints.HORIZONTAL;
    cSpeedBar.gridwidth = GridBagConstraints.REMAINDER;
    cSpeedBarLabel.fill = GridBagConstraints.NONE;
    controlPanelLayout.setConstraints(goButton,cGoButton);
    controlPanelLayout.setConstraints(stepButton,cStepButton);
    controlPanelLayout.setConstraints(stepText,cStepText);
    controlPanelLayout.setConstraints(speedBar,cSpeedBar);
    controlPanelLayout.setConstraints(speedBarLabel,cSpeedBarLabel);
  }

  private void initWorkSpace()
  {
    workSpace.setBackground(Color.white);
    workSpace.setBorder(border1);
    workSpace.setMinimumSize(new Dimension(250,250));
    workSpace.setMaximumSize(new Dimension(2000, 2000));
    workSpace.setPreferredSize(new Dimension(2000, 2000));
    //workSpace.add(registerButton);
  }

  /*  test code... remove
      private void initRegisterButton()
  {
    registerButton.setMinimumSize(new Dimension(150, 30));
    registerButton.setMaximumSize(new Dimension(300, 60));
    registerButton.setPreferredSize(new Dimension(200, 40));
  }
  */

  private void initToolboxPane()
  {
    // add panes....
    toolboxTabbedPane.add(scrollMCUs, "MCUs");
    // toolboxTabbedPane.add(scrollOther, "Other");
    toolboxTabbedPane.setOpaque(true);
    toolboxTabbedPane.setMinimumSize(new Dimension(200, 400));
    toolboxTabbedPane.setMaximumSize(new Dimension(200, 2000));
    toolboxTabbedPane.setPreferredSize(new Dimension(200, 1200));
    toolboxTabbedPane.setBorder(border3);
    toolboxTabbedPane.setBackground(Color.lightGray);
    toolboxTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
    toolboxLabel.setMaximumSize(new Dimension(150, 50));
    toolboxLabel.setMinimumSize(new Dimension(80, 50));
    toolboxLabel.setPreferredSize(new Dimension(100, 50));
    toolboxLabel.setBackground(Color.lightGray);
    toolboxLabel.setFont(new java.awt.Font("Monospaced", 1, 14));
    toolboxLabel.setForeground(Color.black);
    toolboxLabel.setHorizontalAlignment(SwingConstants.CENTER);
    toolboxLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    toolboxLabel.setText("Toolbox");
  }

  private void initListeners()
  {
    glassPaneMouseListener myListener =
        new glassPaneMouseListener(workSpace);
    glassPane.addMouseMotionListener(myListener);
    glassPane.addMouseListener(myListener);
    pane.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        System.exit(0);
      }
    });

    MCUJList.addMouseListener(new MouseInputAdapter()
    {
      public void mousePressed(MouseEvent e)
      {
        //int index = ((JList)e.getSource()).locationToIndex(e.getPoint());
        //only select using left mouse button
        if(e.getButton() == e.BUTTON1)
        {
          glassPane.setImage(((toolboxitem)((JList)e.getSource()).getSelectedValue()).getIcon());
          workSpace.setSelection((toolboxitem)((JList)e.getSource()).getSelectedValue());
        }
      }

      // if mouse released in toolbox, then set selection to null
      public void mouseReleased(MouseEvent e)
      {
        workSpace.setSelection(null);
      }
    });

    goButton.addMouseListener(new MouseInputAdapter()
    {
      public void mouseClicked(MouseEvent e)
      {
        sim.go();
      }
    });

  }
}
