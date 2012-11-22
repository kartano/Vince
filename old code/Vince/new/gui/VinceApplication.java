package gui;

//=============================================
// VinceApplication
//
// MODIFICATION HISTORY:
// 11/11/2003     -     Fixed to only allow for 4 ext. interrupts
//                -     Added DeskCheck feature
//                -     Exception in constructor now dies gracefully
// 07/11/2003     -     Fixed thread bugs, improved GUI,
//                      improved program loader.
// 04/11/2003     -     Added some breakpoint code
// 30/10/2003     -     Added debug buttons, minor GUI changes
// 29/10/2003     -     Added more graphics, improved action handling
//                -     Improved the Inner Classes
// 28/10/2003     -     Fixed indentation
// NOTE: As of 28/10/2003, this uses CVS for version numbers
// 05/10/2003     -     1.0.1       Expanding the GUI
// 16/08/2003     -     1.0.0       Prototype
//=============================================

import java.io.File;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.lang.*;
import AVR.*;
import javax.swing.table.*;
import java.util.*;

/*
 * Main Vince application class
 * @author Simon Mitchell
 */
 public class VinceApplication implements Observer
 {      
      // Application frame
      // Everything gets housed in this
      private JFrame mApplicationFrame;
      
      // Application frame content panel
      private JPanel mApplicationPanel;

      // Program info panel - for stack dump, registry list, data memory, program memory, etc.
      private JTabbedPane mProgramTabStrip;
      
      private JTable mProgramMemoryTable;
      private HexTableModel mProgramMemoryModel;
      private JScrollPane mProgramMemoryScroller;
      private JPanel mProgramMemory;
      private JPanel mProgramMemoryOptions;

      private JTable mDataMemoryTable;
      private HexTableModel mDataMemoryModel;
      private JScrollPane mDataMemoryScroller;
      private JPanel mDataMemory;
      private JPanel mDataMemoryOptions;

      private JScrollPane mDeskcheckScroller;
      private JTextArea mDeskcheckBox;
      
      // Just sillyness - add a pic of Vince to the desktop
      private VinceImagePanel mSausage;

      // Menus
      private JMenuBar mVinceMenu;
      
      // Output pane - where all the run-time and debug info will get dumped
      private JPanel mOutputPanel;
      private JScrollPane mOutputTextBoxScroller;
      private JTextArea mOutputTextBox;

      // Stats pane - where info on the current controller gets shown
      private JPanel mStatsPanel;
      private JLabel mAVRType;
      private JLabel mAVRProgramMemorySize;
      private JLabel mAVRDataMemorySize;
      private JLabel mAVRStackType;
      private JLabel mAVRExternalInterrupts;
      private JLabel mAVRPCBitSize;
      private JLabel mAVRSupportsWatchdog;
      private JProgressBar mStatusBar;

      // File menu
      private JMenu mFileMenu;
      private JMenuItem mFileMenu_chooseAVR;
      private JMenuItem mFileMenu_openProgramFile;
      private JMenuItem mFileMenu_exit;
      
      // Debug menu
      private JMenu mDebugMenu;
      private JMenuItem mDebugMenu_run;
      private JMenuItem mDebugMenu_step;
      private JMenuItem mDebugMenu_pause;
      private JMenuItem mDebugMenu_external_reset;
      private JMenuItem mDebugMenu_toggleBreakpoint;
      private JMenuItem mDebugMenu_clearBreakpoints;      
      private JCheckBoxMenuItem mDebugMenu_deskcheck;
      private JMenuItem mDebugMenu_clearDeskCheck;      
      
      // Toolbar
      private JToolBar mVinceToolbar;
      private JButton mVinceToolbar_chooseAVR;
      private JButton mVinceToolbar_openProgramFile;
      private JButton mVinceToolbar_run;
      private JButton mVinceToolbar_step;
      private JButton mVinceToolbar_pause;
      private JButton mVinceToolbar_external_reset;
      private Vector mVinceToolbar_ExtInterrupts;
      
      // AVR Chooser dialog
      private AVRChooser chooser;
      
      // The Atmel AVR controller/manager thread
      private AtmelThread mAtmelThread;

      // Used to remember location of last prog file chosen
      // Makes things a little more convenient for the user
      private File previousFile;

      // Maximum number of external interrupts supported by any AVR
      private final static int MAX_INTERRUPT_COUNT = 4;      

      // Option to toggle the desk checker
      private boolean mEnableDeskCheck;
      
      /**
      * Default constructor - no arguments
      */
      public VinceApplication()
      {
            System.out.println("VinceApplication.VinceApplication");
            try
            {
                  UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());      
                  mApplicationFrame = new JFrame("Vince!");
                  mApplicationPanel = new JPanel();
                  mApplicationPanel.setLayout(new BorderLayout());
                  mApplicationFrame.setContentPane(mApplicationPanel);
                  mApplicationFrame.addWindowListener(new VinceWindowListener());
                  
                  // Add all the bits to the main application frame
                  buildDefaultProperties();
                  buildAtmelThread();
                  buildDesktop();
                  buildOutput();
                  buildStats();
                  buildMenus();
                  buildToolbar();
                  UpdateControls();
                  
                  // Size and display
                  mApplicationFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE );
                  mApplicationFrame.pack();
                  mApplicationFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
                  mApplicationFrame.setVisible(true);
                  mApplicationFrame.show();
            }
            catch (Exception e)
            {
                  System.out.println(e.toString());
                  this.dieGracefully();
            }
      }

      //--------------------------------------------------------------------------
      // Loads and sets up default properties
      //--------------------------------------------------------------------------
      private void buildDefaultProperties()
      {
            // TO DO:  Get from config file
            mEnableDeskCheck = false;
      }     
      
      //--------------------------------------------------------------------------
      // Builds a new AtmelThread object
      //--------------------------------------------------------------------------
      private void buildAtmelThread()
      {
            System.out.println("VinceApplication.buildAtmelThread");
            mAtmelThread = new AtmelThread(this);
            mAtmelThread.addObserver(this);
      }
      
      //--------------------------------------------------------------------------
      // Builds the desktop and adds it to the Application frame
      //--------------------------------------------------------------------------
      private void buildDesktop()
      {
            System.out.println("VinceApplication.buildDesktop");
            mProgramTabStrip = new JTabbedPane(JTabbedPane.TOP);
            
            System.out.println("\tCreating Program Memory Model");
            mProgramMemory = new JPanel();
            GridBagLayout mGridBagLayout = new GridBagLayout();
            mProgramMemory.setLayout(mGridBagLayout);
            
            mProgramMemoryModel = new HexTableModel(null, HexTableModel.OBSERVE_PROGRAM_MEMORY, HexTableModel.STYLE_CODE);
            mProgramMemoryTable = new JTable(mProgramMemoryModel);
            mProgramMemoryTable.setDefaultRenderer(Color.class, new ProgramColorRenderer(mProgramMemoryModel));
            fixTableSettings(mProgramMemoryTable);
            mProgramMemoryScroller = new JScrollPane(mProgramMemoryTable);
            mProgramMemoryOptions = new MemoryTableOptions(mProgramMemoryModel);
            mProgramMemory.add(mProgramMemoryScroller, new GridBagConstraints(0, 0, 1, 7, 1.0, 1.0 ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 30, 0), 325, 0));
            mProgramMemory.add(mProgramMemoryOptions, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0 ,GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(20, 0, 0, 0), 325, 0));

            System.out.println("\tCreating Data Memory Model");
            mDataMemory = new JPanel();
            mDataMemory.setLayout(mGridBagLayout);
            mDataMemoryModel = new HexTableModel(null, HexTableModel.OBSERVE_DATA_MEMORY,HexTableModel.STYLE_HEX);
            mDataMemoryTable = new JTable(mDataMemoryModel);
            mDataMemoryTable.setDefaultRenderer(Color.class, new DataColorRenderer(mDataMemoryModel));
            fixTableSettings(mDataMemoryTable);
            mDataMemoryScroller = new JScrollPane(mDataMemoryTable);
            mDataMemoryOptions = new MemoryTableOptions(mDataMemoryModel);
            mDataMemory.add(mDataMemoryScroller, new GridBagConstraints(0, 0, 1, 7, 1.0, 1.0 ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 30, 0), 325, 0));
            mDataMemory.add(mDataMemoryOptions, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0 ,GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(20, 0, 0, 0), 325, 0));

            System.out.println("\tCreating Desk Check tab");
            mDeskcheckBox = createTextArea(24,40);
            mDeskcheckScroller = new JScrollPane(mDeskcheckBox);

            mProgramTabStrip.addTab("Program Memory", new ImageIcon("./images/program.gif"),mProgramMemory);
            mProgramTabStrip.addTab("Data Memory",new ImageIcon("./images/data.gif"), mDataMemory);
            mProgramTabStrip.addTab("Desk Check", new ImageIcon("./images/deskcheck.gif"), mDeskcheckScroller);
            
            mApplicationPanel.add(BorderLayout.CENTER,mProgramTabStrip);
      }
      private void fixTableSettings(JTable theTable)
      {
            theTable.getTableHeader().setReorderingAllowed(false);
            theTable.getTableHeader().setResizingAllowed(false);
      }      

      //--------------------------------------------------------------------------
      // Build the output pane and add it to the Application frame
      //--------------------------------------------------------------------------
      private void buildOutput()
      {
            System.out.println("VinceApplication.buildOutput");
            mOutputPanel = new JPanel();
            mOutputTextBox = createTextArea(6,80);
            mOutputTextBoxScroller = new JScrollPane(mOutputTextBox);
            mOutputPanel.add(mOutputTextBoxScroller);
            mApplicationPanel.add(BorderLayout.SOUTH,mOutputPanel);
            appendToOutput("Welcome to Vince!\n");
            appendToOutput("=================\n\n");
            appendToOutput("Fueled by Sausage!\n");
      }
      private JTextArea createTextArea(int rows, int columns)
      {
            JTextArea newTextArea;
            if (rows > 0 && columns >0)
                  newTextArea = new JTextArea(rows, columns);
            else
                  newTextArea = new JTextArea();
            newTextArea.setEditable(false);
            newTextArea.setFont(new Font("Monospaced",0,14));
            return newTextArea;
      }
      //--------------------------------------------------------------------------
      // Build the stats pane and add it to the Application frame
      //--------------------------------------------------------------------------
      private void buildStats()
      {
            System.out.println("VinceApplic3ation.buildStats");
            mAVRType = createLabel("<None>", Color.BLUE);
            mAVRProgramMemorySize = createLabel("<None>", Color.BLUE);
            mAVRDataMemorySize = createLabel("<None>", Color.BLUE);
            mAVRStackType = createLabel("<None>", Color.BLUE);
            mAVRExternalInterrupts = createLabel("<None>", Color.BLUE);
            mAVRPCBitSize = createLabel("<None>", Color.BLUE);
            mAVRSupportsWatchdog = createLabel("<None>",Color.BLUE);
            mStatusBar = new JProgressBar(JProgressBar.HORIZONTAL);
            mStatusBar.setIndeterminate(false);
            mStatusBar.setStringPainted(true);
            mStatusBar.setString("Ready!");
            mSausage = new VinceImagePanel();
            mSausage.setPreferredSize(new Dimension(180,180));
       
            mStatsPanel = new JPanel();
            GridBagLayout mGridBagLayout = new GridBagLayout();
            GridBagConstraints mGridBagConstraints = new GridBagConstraints();
            mStatsPanel.setLayout(mGridBagLayout);
            mGridBagConstraints.fill = GridBagConstraints.NONE;
            mGridBagConstraints.anchor = GridBagConstraints.NORTH;
            mGridBagConstraints.weightx = 0;
            mGridBagConstraints.weighty = 0;
            mGridBagConstraints.gridx = 0;
            mGridBagConstraints.gridy = 0;
            mGridBagConstraints.gridwidth = 2;
            mGridBagConstraints.gridheight = 10;
            mGridBagConstraints.insets = new Insets(1,0,1,0);
       
            addComp(new JLabel("Type:"), mStatsPanel, mGridBagConstraints, 0, 0,1,1);
            addComp(mAVRType, mStatsPanel, mGridBagConstraints, 1, 0,1,1);
            addComp(new JLabel("Prog. Mem size:"), mStatsPanel, mGridBagConstraints,0,1,1,1);
            addComp(mAVRProgramMemorySize, mStatsPanel, mGridBagConstraints,1,1,1,1);
            addComp(new JLabel("Data Mem size:"), mStatsPanel, mGridBagConstraints,0,2,1,1);
            addComp(mAVRDataMemorySize, mStatsPanel, mGridBagConstraints,1,2,1,1);
            addComp(new JLabel("Stack Type:"), mStatsPanel, mGridBagConstraints,0,3,1,1);
            addComp(mAVRStackType, mStatsPanel, mGridBagConstraints,1,3,1,1);
            addComp(new JLabel("Ext. Interrupts:"), mStatsPanel, mGridBagConstraints,0,4,1,1);
            addComp(mAVRExternalInterrupts, mStatsPanel, mGridBagConstraints,1,4,1,1);
            addComp(new JLabel("PC. Bit Size:"), mStatsPanel, mGridBagConstraints,0,5,1,1);
            addComp(mAVRPCBitSize, mStatsPanel, mGridBagConstraints,1,5,1,1);
            addComp(new JLabel("Has Watchdog:"), mStatsPanel, mGridBagConstraints,0,6,1,1);
            addComp(mAVRSupportsWatchdog, mStatsPanel, mGridBagConstraints,1,6,1,1);
            addComp(mStatusBar, mStatsPanel, mGridBagConstraints,0,7,2,1);
            addComp(mSausage, mStatsPanel, mGridBagConstraints,0,8,2,2);

            mApplicationPanel.add(BorderLayout.WEST,mStatsPanel);
      }
      private JLabel createLabel(String theCaption, Color theColor)
      {
            JLabel newLabel = new JLabel(theCaption);
            newLabel.setForeground(theColor);
            return newLabel;
      }      
      private void addComp(Component c, JPanel thePanel, GridBagConstraints GBC, int x, int y, int w, int h)
      {
            GBC.gridx = x;
            GBC.gridy = y;
            GBC.gridwidth = w;
            GBC.gridheight = h;
            thePanel.add(c,GBC);
      }
      
      //--------------------------------------------------------------------------
      // Builds menus and adds them to the application frame      
      //--------------------------------------------------------------------------
      private void buildMenus()
      {
            System.out.println("VinceApplication.buildMenus");
            mVinceMenu = new JMenuBar();
            mApplicationFrame.setJMenuBar(mVinceMenu);
            
            System.out.println("\tCreating File menu");
            // FILE menu
            mFileMenu = new JMenu("File");
            mFileMenu.setMnemonic(KeyEvent.VK_F);
            mVinceMenu.add(mFileMenu);

            mFileMenu_chooseAVR = addMenuItem(mFileMenu, "Choose AVR", new AVRChooserListener(),"./images/chip.gif",KeyEvent.VK_C);
            mFileMenu_openProgramFile = addMenuItem(mFileMenu, "Load Program", new LoadProgramListener(),"./images/open.gif",KeyEvent.VK_L);
            mFileMenu.addSeparator();
            mFileMenu_exit = addMenuItem(mFileMenu, "Exit", new CloseAction(),"",KeyEvent.VK_X);
      
            System.out.println("\tCreating Debug menu");
            // Debug menu
            mDebugMenu = new JMenu("Debug");
            mFileMenu.setMnemonic(KeyEvent.VK_D);
            mVinceMenu.add(mDebugMenu);
            mDebugMenu_run = addMenuItem(mDebugMenu, "Run", new RunAction(),"./images/run.gif",KeyEvent.VK_R);
            mDebugMenu_step = addMenuItem(mDebugMenu, "Step next", new RunAction(),"./images/step.gif",KeyEvent.VK_S);
            mDebugMenu_pause = addMenuItem(mDebugMenu, "Pause", new PauseAction(),"./images/pause.gif",KeyEvent.VK_P);
            mDebugMenu_external_reset = addMenuItem(mDebugMenu, "External Reset", new ExternalResetAction(),"./images/reset.gif",KeyEvent.VK_E);
            mDebugMenu.addSeparator();
            mDebugMenu_toggleBreakpoint = addMenuItem(mDebugMenu, "Set Breakpoint", null,"./images/toggle.gif",KeyEvent.VK_T);
            mDebugMenu_clearBreakpoints = addMenuItem(mDebugMenu, "Clear Breakpoints", new ClearBreakpointsAction(),"./images/clear.gif",KeyEvent.VK_C);
            mDebugMenu.addSeparator();
            
            mDebugMenu_deskcheck = new JCheckBoxMenuItem("Enable Deskcheck");
            mDebugMenu_deskcheck.setMnemonic(KeyEvent.VK_D);
            mDebugMenu_deskcheck.addActionListener(new DeskcheckOptionListener());
            mDebugMenu.add(mDebugMenu_deskcheck);
            mDebugMenu_clearDeskCheck = addMenuItem(mDebugMenu, "Clear Deskcheck", new ClearDeskcheckAction(),"",KeyEvent.VK_L);
      }
      private JMenuItem addMenuItem(JMenu theMenu, String theCaption, ActionListener theListener, String theImageFile, int theMnemonic)
      {
            JMenuItem newItem;
            if (theImageFile.length() != 0)
                  newItem = new JMenuItem(theCaption, new ImageIcon(theImageFile));
            else
                  newItem = new JMenuItem(theCaption);
            if (theListener != null) newItem.addActionListener(theListener);
            if (theMnemonic != 0) 
                  newItem.setMnemonic(theMnemonic);
                  //newItem.setAccelerator(KeyStroke.getKeyStroke(theMnemonic, ActionEvent.ALT_MASK));
            theMenu.add(newItem);
            return newItem;
      }
      
      //--------------------------------------------------------------------------
      // Builds the toolbar and adds it to the desktop
      //--------------------------------------------------------------------------
      private void buildToolbar()
      {
            System.out.println("VinceApplication.buildToolbar");
            mVinceToolbar = new JToolBar();
            mVinceToolbar.setFloatable(false);
            
            mVinceToolbar_chooseAVR = createButton(mVinceToolbar,"./images/chip.gif",new AVRChooserListener(),"Choose AVR type");
            mVinceToolbar_openProgramFile = createButton(mVinceToolbar,"./images/open.gif",new LoadProgramListener(),"Load Program");
            mVinceToolbar.addSeparator();
            mVinceToolbar_run = createButton(mVinceToolbar,"./images/run.gif",new RunAction(),"Run Program");
            mVinceToolbar_step = createButton(mVinceToolbar,"./images/step.gif",new StepAction(),"Step next");
            mVinceToolbar_pause = createButton(mVinceToolbar,"./images/pause.gif",new PauseAction(),"Pause");
            mVinceToolbar_external_reset = createButton(mVinceToolbar,"./images/reset.gif",new ExternalResetAction(),"External Reset");
            mVinceToolbar.addSeparator();

            mVinceToolbar_ExtInterrupts = new Vector();
            JButton externalInterrupt;
            for (int i = 1; i<=MAX_INTERRUPT_COUNT; i++)
            {
                  externalInterrupt = createButton(mVinceToolbar,"./images/"+i+".gif", new InterruptAction(i), "Ext. int. "+i);
                  mVinceToolbar_ExtInterrupts.add(externalInterrupt);
            }

            mApplicationPanel.add(BorderLayout.NORTH,mVinceToolbar);          
      }
      private JButton createButton(JToolBar theToolbar, String imageFile, ActionListener theActionListener, String tooltipText)
      {
            JButton theButton = new JButton(new ImageIcon(imageFile));
            theButton.setBorderPainted(false);
            theButton.addActionListener(theActionListener);
            theButton.setToolTipText(tooltipText);
            theToolbar.add(theButton);
            return theButton;
      }

      /**
      * Appends information to the output box
      * @param stuffToAdd String containing text to append to the box.
      */      
      public synchronized void appendToOutput(String stuffToAdd)
      {
            if (mOutputTextBox != null)
            mOutputTextBox.append(stuffToAdd);
		mOutputTextBox.setCaretPosition(mOutputTextBox.getDocument().getLength());
      }
      
      /**
      * Appends information to the desk check box, if the option is enabled
      * @param stuffToAdd String containing text to append to the box.
      */      
      public synchronized void appendToDeskChecker(String stuffToAdd)
      {
            if (mEnableDeskCheck && mDeskcheckBox != null)
            {
                  mDeskcheckBox.append(stuffToAdd);
      		mDeskcheckBox.setCaretPosition(mDeskcheckBox.getDocument().getLength());
            }
      }
     
      //-------------------------------------------------------------------------
      // Displays the AVR chooser dialog
      //-------------------------------------------------------------------------
      private void chooseAVR()
      {
            Atmel newAtmel;
            if (chooser == null)
                  chooser = new AVRChooser(mApplicationFrame, true);
            else
                  chooser.setVisible(true);

            if (chooser.Selection().length() > 0)
            {
                  
                  newAtmel = AtmelFactory.CreateAtmel(chooser.Selection());
                  if (newAtmel!=null)
                  {
                        mProgramMemoryModel.setParent(newAtmel);
                        mDataMemoryModel.setParent(newAtmel);
                        newAtmel.addObserver(mProgramMemoryModel);
                        newAtmel.addObserver(mDataMemoryModel);
                        mAtmelThread.setAtmel(newAtmel);
                        UpdateControls();
                  }
            }
      }
      
      //-------------------------------------------------------------------------
      // Display the file chooser and get the user to select either a HEX or BIN file
      //-------------------------------------------------------------------------
      private void loadProgram()
      {
            Atmel mCurrentAVR = mAtmelThread.getAtmel();

            JFileChooser fileChooser;
            if (previousFile != null)
                  fileChooser = new JFileChooser(previousFile);
            else
                   fileChooser = new JFileChooser();

            int returnVal;
            
            ProgramFileFilter filter = new ProgramFileFilter();
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(filter);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setDialogTitle("Select a program to load");
            fileChooser.setMultiSelectionEnabled(false);
            returnVal = fileChooser.showOpenDialog(mApplicationFrame);
            if(returnVal == JFileChooser.APPROVE_OPTION)
            {
                  previousFile = fileChooser.getSelectedFile();
                  try
                  {
                        mCurrentAVR.power_on_reset();
                        String selectedFileExtension = Utils.getExtension(fileChooser.getSelectedFile());
                        if (selectedFileExtension.equalsIgnoreCase("BIN"))
                        {
                              mCurrentAVR.LoadBinaryFile(fileChooser.getSelectedFile().getPath());
                              appendToOutput("New binary file loaded: " + fileChooser.getSelectedFile().getName()+"\n");
                        }
                        else if (selectedFileExtension.equalsIgnoreCase("HEX"))
                        {
                              mCurrentAVR.LoadHexFile(fileChooser.getSelectedFile().getPath());
                              appendToOutput("New hex file loaded: " + fileChooser.getSelectedFile().getName()+"\n");
                        }
                        else
                              appendToOutput("The type of file you selected is not supported by Vince\n");
                  }                  
                  catch (AVR.RuntimeException RE)
                  {
                        appendToOutput("There was an exception while loading the file:\n"+RE.toString()+"\n");
                  }
            }
            fileChooser = null;
      }
      
      //-------------------------------------------------------------------------
      // Update labels in the dialog
      //-------------------------------------------------------------------------
      private void UpdateControls()
      {
            Atmel mCurrentAVR = mAtmelThread.getAtmel();
            boolean AVRLoaded = (mCurrentAVR != null);
            mFileMenu_openProgramFile.setEnabled(AVRLoaded);
            mDebugMenu_run.setEnabled(AVRLoaded);
            mDebugMenu_step.setEnabled(AVRLoaded);
            mDebugMenu_pause.setEnabled(AVRLoaded);
            mDebugMenu_external_reset.setEnabled(AVRLoaded);
            mVinceToolbar_openProgramFile.setEnabled(AVRLoaded);
            mVinceToolbar_run.setEnabled(AVRLoaded);
            mVinceToolbar_step.setEnabled(AVRLoaded);
            mVinceToolbar_pause.setEnabled(AVRLoaded);
            mVinceToolbar_external_reset.setEnabled(AVRLoaded);
            mDebugMenu_toggleBreakpoint.setEnabled(AVRLoaded);
            mDebugMenu_clearBreakpoints.setEnabled(AVRLoaded);

            int i;
            for (i = 0; i<mProgramTabStrip.getTabCount(); i++)
                  mProgramTabStrip.setEnabledAt(i,AVRLoaded);
                  
            // Enable/disable buttons accoding to whether any
            // AVR is loaded and if so, it's ext. interrupt count
            JButton extInterrupt;
            for (i = 0; i < mVinceToolbar_ExtInterrupts.size(); i++)
            {
                  extInterrupt = (JButton)(mVinceToolbar_ExtInterrupts.elementAt(i));
                  if (AVRLoaded)
                  {
                        if ((i + 1) <= mCurrentAVR.ExternalInterruptCount())
                              extInterrupt.setEnabled(true);
                        else
                              extInterrupt.setEnabled(false);
                  }
                  else
                        extInterrupt.setEnabled(false);
            }
            if (!AVRLoaded)
            {
                  mAVRType.setText("<None>");
                  mAVRProgramMemorySize.setText("<None>");
                  mAVRDataMemorySize.setText("<None>");
                  mAVRStackType.setText("<None>");
                  mAVRExternalInterrupts.setText("<None>");
                  mAVRPCBitSize.setText("<None>");
                  mAVRSupportsWatchdog.setText("<None>");
            }
            else
            {
                  mAVRType.setText(chooser.Selection());
                  mAVRProgramMemorySize.setText("0x"+Utils.hex(mCurrentAVR.ProgramMemorySize())+" bytes");
                  mAVRDataMemorySize.setText("0x"+Utils.hex(mCurrentAVR.DataMemorySize())+" bytes");
                  switch (mCurrentAVR.Stack().get_type())
                  {
                        case StackInterface.HARDWARE :
                              mAVRStackType.setText("Hardware");
                              break;
                        case StackInterface.SOFTWARE :
                              mAVRStackType.setText("Software");
                              break;
                        default :
                              mAVRStackType.setText("Surprise me");
                              break;
                  }
                  mAVRExternalInterrupts.setText(String.valueOf(mCurrentAVR.ExternalInterruptCount()));
                  mAVRPCBitSize.setText(mCurrentAVR.getPCBitSize()+ " bits");
                  if (mCurrentAVR.supportsWatchdog())
                        mAVRSupportsWatchdog.setText("Yes Indeed!");
                  else
                        mAVRSupportsWatchdog.setText("Nope.");
            }
      }

      /**
      * Handles messages sent to the VinceApp
      * This is part of the Observer interface
      */
      public void update(Observable o, Object payload)
      {             
            if (o instanceof AtmelThread)
            {
                  System.out.println("VinceApplication.update (Thread state changed)");
                  boolean canRun, canStep, canPause;
                  switch (mAtmelThread.getThreadState())
                  {
                        case AtmelThread.THREAD_STOP :
                              mStatusBar.setIndeterminate(false);
                              mStatusBar.setString("Stopped");
                              canRun = canStep = true;
                              canPause = false;
                              break;
                        case AtmelThread.THREAD_RUN :
                              mStatusBar.setIndeterminate(true);
                              mStatusBar.setString("Running");
                              canRun = false;
                              canStep = canPause = true;
                              break;
                        case AtmelThread.THREAD_PAUSE :
                              mStatusBar.setIndeterminate(false);
                              mStatusBar.setString("Paused");
                              canRun = canStep = true;
                              canPause = false;
                              break;
                        case AtmelThread.THREAD_STEP :
                              mStatusBar.setIndeterminate(true);
                              mStatusBar.setString("Stepping");
                              canRun = canStep =false;
                              canPause = true;
                              break;
                        default :
                              mStatusBar.setIndeterminate(false);
                              mStatusBar.setString("???");
                              canRun = canStep = canPause = true;
                              break;
                        
                  }
                  mDebugMenu_run.setEnabled(canRun);
                  mDebugMenu_step.setEnabled(canStep);
                  mDebugMenu_pause.setEnabled(canPause);
                  mVinceToolbar_run.setEnabled(canRun);
                  mVinceToolbar_step.setEnabled(canStep);
                  mVinceToolbar_pause.setEnabled(canPause);

                  // Force an update of the program counter field
                  mProgramMemoryModel.fireTableDataChanged();
            }
      }

      /**
      * Returns whether or not the deskcheck is currently on.
      * @return True if the desk checker is running
      */
      public boolean DeskCheckerActive() { return mEnableDeskCheck; }

      /**
      * Stops the Vince Program running
      */
      public void dieGracefully()
      {
            System.out.println("VinceApplication.dieGracefully");
            if (mAtmelThread != null)
            {
                  mAtmelThread.setThreadState(AtmelThread.THREAD_STOP);
                  mAtmelThread = null;
            }
            if (mVinceToolbar_ExtInterrupts != null) mVinceToolbar_ExtInterrupts.removeAllElements();
            System.out.println("Vince is dead!");
            System.exit(0);
      }

      //------------------------------------------------------------
      //------------------------------------------------------------
      // Inner classes follow under here
      //------------------------------------------------------------
      //------------------------------------------------------------
            
      //------------------------------------------------------------
      // Causes AVRChooser dialog to display
      //------------------------------------------------------------
      class AVRChooserListener implements ActionListener
      {        
            public AVRChooserListener() { }
            public void actionPerformed(ActionEvent e) { chooseAVR(); }
      }

      //------------------------------------------------------------
      // Displays "load program" dialog
      //------------------------------------------------------------
      class LoadProgramListener implements ActionListener
      {        
            public LoadProgramListener() { }
            public void actionPerformed(ActionEvent e) { loadProgram(); }
      }

      //---------------------------------------------------------------------------------------
      // CloseAction - action listener that causes the program to end when performed
      //---------------------------------------------------------------------------------------
      class CloseAction implements ActionListener
      {
            public CloseAction() { }
            public void actionPerformed(ActionEvent e) { dieGracefully(); }
      }

      //---------------------------------------------------------------------------------------
      // RunAction - action listener for RUN
      //---------------------------------------------------------------------------------------
      class RunAction implements ActionListener
      {
            public RunAction() { }
            public void actionPerformed(ActionEvent e)
            {
                  mAtmelThread.setThreadState(AtmelThread.THREAD_RUN);
                  appendToOutput("Running ...\n");
            }
      }

      //---------------------------------------------------------------------------------------
      // StepACtion - action listener for STEP
      //---------------------------------------------------------------------------------------
      class StepAction implements ActionListener
      {
            public StepAction() { }
            public void actionPerformed(ActionEvent e)
            {
                  mAtmelThread.setThreadState(AtmelThread.THREAD_STEP);
                  appendToOutput("Stepping ...\n");
            }
      }

      //---------------------------------------------------------------------------------------
      // PauseAction - action listener for PAUSE
      //---------------------------------------------------------------------------------------
      class PauseAction implements ActionListener
      {
            public PauseAction() { }
            public void actionPerformed(ActionEvent e)
            {
                  mAtmelThread.setThreadState(AtmelThread.THREAD_PAUSE);
                  appendToOutput("Paused ...\n");
            }
      }

      //---------------------------------------------------------------------------------------
      // ExternalResetAction - action listener for External Reset
      //---------------------------------------------------------------------------------------
      class ExternalResetAction implements ActionListener
      {
            public ExternalResetAction() { }
            public void actionPerformed(ActionEvent e)
            {
                  mAtmelThread.setThreadState(AtmelThread.THREAD_STOP);
                  mAtmelThread.getAtmel().external_reset();   
                  appendToOutput("External Reset ...\n");
                  // Force reset of program counter highlight
                  mProgramMemoryModel.fireTableDataChanged();
            }
      }
      
      //---------------------------------------------------------------------------------------
      // ClearBreakpointsAction - Action listener for clearing breakpoints
      //---------------------------------------------------------------------------------------
      class ClearBreakpointsAction implements ActionListener
      {
            public ClearBreakpointsAction() { }
            public void actionPerformed(ActionEvent e)
            {
                  mAtmelThread.clearBreakPoints();
                  appendToOutput("Breakpoints cleared.\n");
            }
      }

      //---------------------------------------------------------------------------------------
      // Clear Deskcheck Option Action - handler for clearing deskcheck text
      //---------------------------------------------------------------------------------------
      class ClearDeskcheckAction implements ActionListener
      {
            public ClearDeskcheckAction() { }
            public void actionPerformed(ActionEvent e)
            {
                  mDeskcheckBox.setText("");
            }
      }
      //---------------------------------------------------------------------------------------
      // Deskcheck Option Action - handler for toggling deskcheck enable/disable
      //---------------------------------------------------------------------------------------
      class DeskcheckOptionListener implements ActionListener
      {
            public DeskcheckOptionListener() { }
            public void actionPerformed(ActionEvent e)
            {
                  mEnableDeskCheck = !mEnableDeskCheck;                  
                  if (mEnableDeskCheck)
                        appendToOutput("Deskcheck now ON!\n");
                  else
                        appendToOutput("Deskcheck now OFF!\n");
            }
      }
      //---------------------------------------------------------------------------------------
      // Interrupt Action - handler for external interrupt triggers
      //---------------------------------------------------------------------------------------
      class InterruptAction implements ActionListener
      {
            int mMyInterruptNo;
            
            public InterruptAction(int interruptNumber) { mMyInterruptNo = interruptNumber; }
            public void actionPerformed(ActionEvent e)
            {
                  if (mAtmelThread.getAtmel() != null)
                  {
                        appendToOutput("Ext. Interrupt "+mMyInterruptNo+" triggered.");
                        mAtmelThread.getAtmel().TriggerExternalInterrupt(mMyInterruptNo);
                  }
            }
      }

      //---------------------------------------------------------------------------------------
      // Memory table style options
      //---------------------------------------------------------------------------------------
      class MemoryTableOptions extends JPanel implements ActionListener
      {
            private JRadioButton mOptionHex;
            private JRadioButton mOptionCode;
            private HexTableModel mTableToControl;
            
            public MemoryTableOptions(HexTableModel tableToControl)
            {
                  super(new GridLayout(1,2));
                  
                  mTableToControl = tableToControl;
                  mOptionHex = new JRadioButton("View as Hex");
                  mOptionHex.setMnemonic(KeyEvent.VK_H);
                  mOptionHex.setActionCommand("HEX");
                  mOptionHex.addActionListener(this);
                  
                  add(mOptionHex);
            
                  mOptionCode = new JRadioButton("View as Code");
                  mOptionCode.setMnemonic(KeyEvent.VK_C);
                  mOptionCode.setActionCommand("CODE");
                  mOptionCode.addActionListener(this);
                  add(mOptionCode);
                  
                  ButtonGroup group = new ButtonGroup();
                  group.add(mOptionHex);
                  group.add(mOptionCode);
                  if (tableToControl.getStyle() == HexTableModel.STYLE_HEX)
                        mOptionHex.setSelected(true);
                  else
                        mOptionCode.setSelected(true);
            }
            public void actionPerformed(ActionEvent e) 
            {
                  // Doing this because with large AVRs (I.E: Megas)
                  // switching from Hex to Code or back can cause
                  // the thing to hang or run very very slowly.
                  // Accessor methods for Data and Program memory in
                  // Atmel are syncrhonised.
                  int previousThreadState = mAtmelThread.getThreadState();
                  mAtmelThread.setThreadState(AtmelThread.THREAD_PAUSE);
                  if (e.getActionCommand().equalsIgnoreCase("HEX"))
                        mTableToControl.setStyle(HexTableModel.STYLE_HEX);
                  else
                        mTableToControl.setStyle(HexTableModel.STYLE_CODE);
                  mAtmelThread.setThreadState(previousThreadState);
            }      
      }

      //---------------------------------------------------------------------------------------
      // Window listener
      //---------------------------------------------------------------------------------------
            class VinceWindowListener extends WindowAdapter
      {
            public VinceWindowListener() { }
            
            public void windowClosing(WindowEvent e) 
            {
		mApplicationFrame.setVisible(false);
		dieGracefully();
            }
      }
}

//---------------------------------------------------------------------------------------
// The Vince Image Panel - just for decoration
//---------------------------------------------------------------------------------------
class VinceImagePanel extends JPanel
{
      private Image mVinceImage;

      public VinceImagePanel()
      {
            System.out.println("VinceImagePanel.VinceImagePanel");
            mVinceImage = Toolkit.getDefaultToolkit().getImage("./images/vince.jpg");
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(mVinceImage,0);
            try { tracker.waitForID(0); }
            catch (InterruptedException e) {}
      }

      public void paintComponent(Graphics g)
      {
            super.paintComponent(g);
            g.drawImage(mVinceImage,0,0,this);
      }
}

//---------------------------------------------------------------------------------------
// ProgramFileFilter - used when display the "Load Program" dialog.
// Shows only BIN or HEX files basically.
//---------------------------------------------------------------------------------------

class ProgramFileFilter extends FileFilter
{
      public boolean accept(File theFile)
      {
            boolean returnValue;
            
            if (theFile.isDirectory())
                  returnValue = true;
            else
            {
                  String fileExtension = Utils.getExtension(theFile);
                  returnValue = (fileExtension.equalsIgnoreCase("HEX") || fileExtension.equalsIgnoreCase("BIN"));
            }
            return returnValue;
      }
      
      public String getDescription() { return "Vince Program Files"; }
}

//--------------------------------------------------------------
// ProgramColorRenderer - used to render Program Memory table
//--------------------------------------------------------------
class ProgramColorRenderer extends JLabel implements TableCellRenderer 
{
      private HexTableModel mHexTableModel;
      
      public ProgramColorRenderer(HexTableModel theModel)
      {
            super();
            mHexTableModel = theModel;
            setOpaque(true);
      }

      public Component getTableCellRendererComponent(JTable table, Object caption, boolean isSelected, boolean hasFocus, int row, int column)
      {
            // Highlight this address if it is the currently 
            // location indicated by the Atmel PC.
            int actualMemoryAddress;
            if (mHexTableModel.getStyle() == HexTableModel.STYLE_CODE)
                  actualMemoryAddress = row;
            else
                  actualMemoryAddress = (row * HexTableModel.VALUES_PER_ROW) + (column-1);
            if (actualMemoryAddress == mHexTableModel.getParent().getLastInstructionAddress())
                  setForeground(Color.RED);
            else
                  setForeground(Color.BLACK); 
            setText((String)caption);
            if ((row % 2) ==0)
                  setBackground(new Color((float)0.0,(float)0.95,(float)0.95));
            else
                  setBackground(Color.white);
            return this;
      }
}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        

//--------------------------------------------------------------
// Data memory renderer
//--------------------------------------------------------------
class DataColorRenderer extends JLabel implements TableCellRenderer 
{
      private HexTableModel mHexTableModel;
      
      public DataColorRenderer(HexTableModel theModel)
      {
            super();
            mHexTableModel = theModel;
            setOpaque(true);
      }

      public Component getTableCellRendererComponent(JTable table, Object caption, boolean isSelected, boolean hasFocus, int row, int column)
      {
            int effectiveAddress;
            if (mHexTableModel.getStyle() == HexTableModel.STYLE_CODE)
                  effectiveAddress = row * 2;
            else
                  effectiveAddress = (row * HexTableModel.VALUES_PER_ROW) + (column-1);
            setText((String)caption);
            
            // Alternate colors in rows
            if ((row % 2) ==0)
                  setBackground(new Color((float)0.0,(float)0.95,(float)0.95));
            else
                  setBackground(Color.white);
                  
            // Don't buggerise text color in the first column!
            if (column == 0)
                  setForeground(Color.BLACK);
                  
            // Foreground color for general purpose registers
            else if (effectiveAddress <= 0x1f)
                  setForeground(new Color((float)1.0,(float)0.0,(float)0.0));
            // Foreground color for IO registers
            else if (effectiveAddress <= 0x5f)
            // Other data memory (or Stack on Software stack models)
                  setForeground(new Color((float)0.0,(float)0.0,(float)1.0));
            else
                  setForeground(Color.BLACK);
            return this;
      }
}
