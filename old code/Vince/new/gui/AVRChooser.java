package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import AVR.*;
import java.util.*;

//======================================================
// AVRChooser
//
// MODIFICATION HISTORY:
// 31/10/2003     -     Improved action handling, minor GUI improvements
// 29/10/2003     -     Added some graphics
// 28/10/2003     -     Indentation fixes
// NOTE:  As of 28/10/2003 this uses CVS for version numbers
// 05/10/2003     -     1.0.0       Prototype
//======================================================

public class AVRChooser extends JDialog
{
      private JPanel mDialogPanel;
      private DefaultListModel ListModel;
      private JList AVRList;
      private JScrollPane AVRListScroller;
      private JPanel mButtonPanel;
      private JButton mButtonOK, mButtonCancel;
      private String mSelection;      
      
      public AVRChooser(JFrame owner, boolean modal)
      {
            super(owner, modal);
            System.out.println("AVRChooser.AVRChooser");
            mSelection = new String();
            setTitle("Select an AVR");
            mDialogPanel = new JPanel();
            mDialogPanel.setLayout(new BorderLayout());
            this.setContentPane(mDialogPanel);
            
            BuildAVRList();
            BuildButtons();

            this.pack();
            this.setSize(200,300);
            this.setResizable(false);
            this.setLocationRelativeTo(null);
            this.setVisible(true);
      }
      
      private void doOK()
      {
            mSelection = AVRList.getSelectedValue().toString();
            setVisible(false);
      }
      private void doCancel()
      {
            mSelection = "";
            setVisible(false);
      }
      //--------------------------------------------------------------------------
      // Builds the AVR list view
      //--------------------------------------------------------------------------
      private void BuildAVRList()
      {
            System.out.println("AVRChooser.BuildAVRList");

            ListModel = new DefaultListModel();
            
            // TO DO:  Load this list from an XML document or some kind of config script.
            Vector classList = Utils.supportedClasses("AVR");
            String theAvr;
            for (int i=0;i<classList.size();i++)
            {
                  theAvr = (String)classList.elementAt(i);
                  // Ignore anything that doesn't start with AT90
                  if (theAvr.substring(0,4).equalsIgnoreCase("AT90"))
                        ListModel.addElement(theAvr);
            }
            
            AVRList = new JList(ListModel);
            AVRList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            AVRList.setLayoutOrientation(JList.VERTICAL_WRAP);
            AVRList.setVisibleRowCount(-1);
            AVRList.addMouseListener(new DoubleClickListener());
            AVRListScroller = new JScrollPane(AVRList);
            AVRListScroller.setPreferredSize(new Dimension(240,240));
            mDialogPanel.add(AVRList, BorderLayout.CENTER);
      }
      
      //--------------------------------------------------------------------------
      // Builds and adds the button panel
      //--------------------------------------------------------------------------
      private void BuildButtons()
      {
            System.out.println("AVRChooser.BuildButtons");

            mButtonPanel = new JPanel();
            mButtonPanel.setLayout(new GridLayout(1,2));

            mButtonOK = new JButton("OK",new ImageIcon("./images/ok.gif"));
            mButtonOK.setMnemonic(KeyEvent.VK_O);
            mButtonOK.addActionListener(new OKAction());
            mButtonPanel.add(mButtonOK);
            
            mButtonCancel = new JButton("Cancel",new ImageIcon("./images/cancel.gif"));
            mButtonCancel.setMnemonic(KeyEvent.VK_C);
            mButtonOK.addActionListener(new CancelAction());
            mButtonPanel.add(mButtonCancel);

            getRootPane().setDefaultButton(mButtonOK);
            
            mDialogPanel.add(mButtonPanel, BorderLayout.SOUTH);
      }
      
      /**
      * Returns the last known AVR selected from the dialog.
      * Returns a blank string if CANCEL was clicked.
      * @param index Program Memory location to write to
      * @param value Value to write to Program Memory
      */
      public String Selection() { return mSelection; }
      
      class DoubleClickListener implements MouseListener
      {
            public DoubleClickListener() {}
            public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) doOK(); }
            public void mouseEntered(MouseEvent e) { }
            public void mouseExited(MouseEvent e) { }
            public void mousePressed(MouseEvent e) { }
            public void mouseReleased(MouseEvent e) { }
}

      class OKAction implements ActionListener
      {
            public OKAction() { }
            public void actionPerformed(ActionEvent e) { doOK(); }
      }

      class CancelAction implements ActionListener
      {
            public CancelAction() { }
            public void actionPerformed(ActionEvent e) { doCancel(); }
      }
}