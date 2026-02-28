
package com.moneydance.modules.features.JConsole2026;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
//import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;


import java.awt.Dimension;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Font;
import java.awt.Color;

//import java.io.*;
//import java.util.*;
//import java.text.*;
//import java.awt.*;
import java.awt.Image;
import java.lang.Runtime;
import java.lang.Thread;


import java.util.Properties;  
import java.util.Enumeration; 
import java.io.ByteArrayOutputStream; 
import java.io.IOException;
import java.lang.InterruptedException;

import java.awt.Toolkit; 
import java.net.URL;
import java.lang.ProcessBuilder;
import java.lang.ProcessBuilder.Redirect;

import com.moneydance.apps.md.controller.FeatureModule;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.apps.md.controller.ModuleUtil;
import com.moneydance.apps.md.controller.UserPreferences;

//import com.moneydance.apps.md.model.*;
import com.infinitekind.moneydance.model.*;


public class Main extends FeatureModule implements ActionListener, ItemListener {

//  public JTextArea output;
  public static final Boolean  debug = false ;
  protected JCheckBoxMenuItem[] cbmi;
//  protected Action leftAction, middleAction, rightAction;
  public JPanel pnl ;
  public static final int PREFERRED_WIDTH = 640;  // for the JFrame
  public static final int PREFERRED_HEIGHT = 480;
  public static final String newline = "\n";
  public JConsole2026 jConsole;
  public JCheckBoxMenuItem cbMenuItem1;
  public JCheckBoxMenuItem cbMenuItem2;

//  private ConsoleWindow consoleWindow = null; -------------------------------------------------------
  
  public void init() {

    if (debug){ System.err.println("Main init Line 80");};

    // the first thing we will do is register this module to be invoked
    // via the application toolbar
    FeatureModuleContext context = getContext();
    try {
//      context.registerFeature(this, "showconsole",
      context.registerFeature(this, "showconsole",
        getIcon("icon-debug"),  // no extension
        getName());
//        getIcon("jpython_icon.gif"),
    }
    catch (Exception e) {
      e.printStackTrace(System.err);
    }
  }

  public void cleanup() {
    closeConsole();
  }
  
  private Image getIcon(String action) { // String action is not used
    try {
//      if (debug){ System.err.println("getIcon line 103 in main");};  /// got called when the extension was installed
      ClassLoader cl = getClass().getClassLoader();
//      if (debug){ System.err.println("getIcon line 105 in main:"+cl);};
      java.io.InputStream in =
//        cl.getResourceAsStream("/com/moneydance/modules/features/JConsole2026/images/jpython_icon.gif");
        cl.getResourceAsStream("/com/moneydance/modules/features/JConsole2026/images/icon-debug.gif");
//        cl.getResourceAsStream("images/icon-debug.gif");
      if (in != null) {
//        if (debug){ System.err.println("getIcon line 112 in main");};
        ByteArrayOutputStream bout = new ByteArrayOutputStream(2000);
        byte buf[] = new byte[256];
        int n = 0;
        while((n=in.read(buf, 0, buf.length))>=0)
        {
          bout.write(buf, 0, n);
//          if (debug){ System.err.println("getIcon line 118 in main:" + n );}; // got 256 then 140 // ls -l says 396 bytes
          }
//        if (debug){ System.err.println("getIcon line 117 in main " + buf.length);};  // says 256 .. not much use
///        byte image = Toolkit.getDefaultToolkit().createImage(bout.toByteArray());    // error Image cannot be converted to byte
///        if (debug){ System.err.println("getIcon line 119 in main" + image.length());}; // error byte cannot be dereferenced
        return Toolkit.getDefaultToolkit().createImage(bout.toByteArray());  // the icon on the program never changed???????????????????/
      }
    } catch (Throwable e) { }
    return null;
  }
  
  /** Process an invokation of this module with the given URI */
  public void invoke(String uri) {
    String command = uri;
    String parameters = "";
    int theIdx = uri.indexOf('?');
    if(theIdx>=0) {
      command = uri.substring(0, theIdx);
      parameters = uri.substring(theIdx+1);
    }
    else {
      theIdx = uri.indexOf(':');
      if(theIdx>=0) {
        command = uri.substring(0, theIdx);
      }
    }
    if (debug){ System.err.println("Main invoke 144 "+ command);};
    if(command.equals("showconsole")) {
    runConsole();
    }    
  }

  public String getName() {
    return ("JConsole2026");
  }

  private synchronized void showConsole() {
  runConsole();
  }
  
  FeatureModuleContext getUnprotectedContext() {
//    FeatureModuleContext context = new FeatureModule.getContext();
    return getContext();
  }

  synchronized void closeConsole() {
      if (debug){ System.err.println("Main closeConsole 164 ");}; // never gets called
      System.gc();
    }
    
  public void runConsole()
  {

    if (debug){ System.err.println("Main runConsole 171 JFrame setup");};

	JFrame frame = new JFrame("JConsole2026");
    Dimension dim = new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT); // need this for setMinimumSize and others
//	frame.setMinimumSize(dim); // this worked instead of setSize(int,int)
	frame.setPreferredSize(dim); // this worked too .. so setSize() is just ignored by the BorderLayout. It was squeezing everything as small as it could.
//	frame.setSize(640, 480); // also have setSize(),setMinimumSize(),setPreferredSize(), setMaximumSize()
//	frame.setLayout(new GridLayout()); // removed then put it back something has changed the size? GridLayout(0,3)
	frame.setLayout(new BorderLayout());
    JPanel pnl = new JPanel(new BorderLayout());
    this.pnl = pnl;
    frame.add(pnl);
    JConsole2026 jConsole = new JConsole2026(getUnprotectedContext());
    this.jConsole = jConsole;
    pnl.add(new JScrollPane( jConsole ), BorderLayout.CENTER);
//    pnl.setFont(new java.awt.Font("Arial", Font.BOLD, 18));     // added Dec 31 2025  didn't work

//    pnl.add(new JScrollPane(new JConsole2026(getUnprotectedContext())), BorderLayout.CENTER); //broke into 2 lines now
// there is only room for one big button in the South.. will try a menu
//    JButton btn3 = new JButton("Show Tips");
//    pnl.add(btn3, BorderLayout.SOUTH);

/**  there is a lot of good info on docs.oracle.com see java-dev.JOptionPane-With-Timer.JOptionPane.txt about all kinds of message boxes
**/
                                                                   // below is all to do with captureing the user clicking on the window close x
	frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);   // was DISPOSE_ON_CLOSE never EXIT_ON_CLOSE HIDE_ON_CLOSE DO_NOTHING_ON_CLOSE
	frame.addWindowListener(new WindowAdapter()
	{
	   public void windowClosing(WindowEvent e)
	     {
//	     if (debug){ System.err.println("So you want to close the window  ");};
// using showMessageDialog(component parentComponent,String message,String messageTitle,int optionType) method to display a message dialog box
//        JOptionPane.showMessageDialog(null,"Updating the Command History ","Updateing History",JOptionPane.INFORMATION_MESSAGE);  // have to click OK
//	     JFrame frame = (JFrame)e.getSource();
//            int result = JOptionPane.showConfirmDialog(
//            frame,
//            "Updating the Command History");
//            "Exit Application ?",
//            JOptionPane.YES_NO_OPTION);
//            if (result == JOptionPane.YES_OPTION)
	     // get the commandline History and save it to ~/.moneydance/JConsole2026.save
///////////////////////// not any more Feb 7 2026 	        JConsole2026.history.saveHistory(); // update the command line history
//            jConsole.setText("Updateing Command History",true); // doesn't work
            if (debug){ System.err.println("JConsole2026 is shuting down  ");}; // didn't show up until after the timer finished running ???
            try{Thread.sleep(100);}catch (Exception ee) {ee.printStackTrace(System.err);}
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // never EXIT_ON_CLOSE it will crash moneydance
         }
     });
//Create/set menu bar and content pane.
 //   ActionDemo jConsole = new ActionDemo();
 //    JConsole2026 jConsole = new JConsole2026();  // this is done on line 144 too .. maybe screw things up
//        frame.setJMenuBar(jConsole.createMenuBar());
    frame.setJMenuBar(this.createMenuBar());
    this.createToolBar();
//       jConsole.setOpaque(true); //content panes must be opaque
//        frame.setContentPane(jConsole);  // done on line 144 I think

        //Display the window.
    frame.pack();
	frame.setVisible(true);

  } // end runConsole

  public JMenuBar createMenuBar() {
        JMenuItem menuItem;
        JMenuBar menuBar;
        JMenu mainMenu;
        JMenu submenu;
        JRadioButtonMenuItem rbMenuItem;

        //Create the menu bar.
        menuBar = new JMenuBar();

        //Create the first menu.
        mainMenu = new JMenu("Menu");

//        ImageIcon icon88 = createImageIcon5("/com/moneydance/modules/features/JConsole2026/images/middle.gif","Description"); // crashed md
//        ImageIcon icon88 = createImageIcon3("/com/moneydance/modules/features/JConsole2026/images/middle.gif"); // crashes md
//        ImageIcon icon88 = createImageIcon4("/com/moneydance/modules/features/JConsole2026/images/middle.gif"); // could not find file
        ImageIcon icon88 = createImageIcon2("/com/moneydance/modules/features/JConsole2026/images/right.gif");  // oldMan.gif is too big
        // it uses getResourceAsStream while the others do not.. going to delete all the others

        //Set up the menu bar.
        menuBar.add(mainMenu);
        //a group of JMenuItems
        menuItem = new JMenuItem("Change Command Line History",icon88);
//        menuItem = new JMenuItem("A text-only menu item",KeyEvent.VK_T);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
//        menuItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
        menuItem.setToolTipText("Click to Open The Command Line History File");
        menuItem.setActionCommand("First Menu Item");
        menuItem.addActionListener(this);
        mainMenu.add(menuItem);



        menuItem = new JMenuItem("Open the errlog.txt",icon88 );
//        menuItem = new JMenuItem("Both text and icon",icon88);
//        menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.setToolTipText("Click to Open The Moneydance Error log File .moneydance/errlog.txt");
        menuItem.setActionCommand("Second Menu Item");
        menuItem.addActionListener(this);
        mainMenu.add(menuItem);


        menuItem = new JMenuItem("Open the ReadMe File",icon88);
//        menuItem = new JMenuItem(icon88);  // just an icon
//        menuItem.setMnemonic(KeyEvent.VK_D);
        menuItem.setToolTipText("Click to Open The JConsole2026 ReadMe.txt File .moneydance/JConsole2026.txt");
        menuItem.setActionCommand("Third Menu Item");
        menuItem.addActionListener(this);
        mainMenu.add(menuItem);

/** don't see much use for these .. one changes the other one ??? something to do with the ButtonGroup .. I thinnk
        //a group of radio button menu items
        mainMenu.addSeparator();
        ButtonGroup group = new ButtonGroup();
        rbMenuItem = new JRadioButtonMenuItem("A radio button menu item");
        rbMenuItem.setSelected(true);
//        rbMenuItem.setMnemonic(KeyEvent.VK_R);
        group.add(rbMenuItem);
        rbMenuItem.addActionListener(this);
        mainMenu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem("Another one");
//        rbMenuItem.setMnemonic(KeyEvent.VK_O);
        group.add(rbMenuItem);
        rbMenuItem.addActionListener(this);
        mainMenu.add(rbMenuItem);
***/
/*** need to make these persistent to be useful .. like the toogle buttons
// the problem with the check boxes is they have no memory from restart to restart like the Toggle Buttons
        //a group of check box menu items
        mainMenu.addSeparator();
        cbMenuItem1 = new JCheckBoxMenuItem("A check box menu item");
//        cbMenuItem.setMnemonic(KeyEvent.VK_C);
        cbMenuItem1.addItemListener(this);
//        cbMenuItem.setActionCommand("Action for Check Box1"); // didn't work/do anything I could see
        mainMenu.add(cbMenuItem1);

        cbMenuItem2 = new JCheckBoxMenuItem("Another one");
//        cbMenuItem.setMnemonic(KeyEvent.VK_H);
        cbMenuItem2.addItemListener(this);
        mainMenu.add(cbMenuItem2);
***/
/**
        //a submenu
        mainMenu.addSeparator();
        submenu = new JMenu("A submenu");
//        submenu.setMnemonic(KeyEvent.VK_S);

        menuItem = new JMenuItem("An item in the submenu");
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
        menuItem.addActionListener(this);
        submenu.add(menuItem);

        menuItem = new JMenuItem("Another item in the submenu");
        menuItem.addActionListener(this);
        submenu.add(menuItem);
        mainMenu.add(submenu);
**/
/**
        //Build second menu in the menu bar.
        mainMenu = new JMenu("Another Menu");
//        mainMenu.setMnemonic(KeyEvent.VK_N);
//        mainMenu.getAccessibleContext().setAccessibleDescription("This menu does nothing");
        menuBar.add(mainMenu);
**/
        return menuBar;
    }


  public void createToolBar() {  //  working fine ,, bottons are too big
        JButton button1 ;
        JButton button2 ;
        JButton button3 ;
//checkbox.setFont(new java.awt.Font("Arial", Font.BOLD, 14));
//checkbox.setBackground(Color.BLUE);
//checkbox.setForeground(Color.YELLOW);



        //Create the toolbar.
        JToolBar toolBar = new JToolBar();
        pnl.add(toolBar, BorderLayout.PAGE_START);
//       add(toolBar, BorderLayout.PAGE_START);

        //first button
//        button1 = new JButton(leftAction); // Creates a button where properties are taken from the Action supplied ??
        button1 = new JButton();
        button1.setText("Clear Screen");
        button1.addActionListener(this);
        button1.setToolTipText("Click to Clear the Console.");
        button1.setActionCommand("button1 pressed");   // get this instead of Button1 in the handler
        button1.setFont(new java.awt.Font("Arial", Font.BOLD , 10)); //PLAIN or BOLD or Italic
//        button1.setBackground(Color.BLACK);
//        button1.setForeground(Color.WHITE);
        toolBar.add(button1);

        //second button
//        button2 = new JButton(middleAction);
        button2 = new JButton();
        button2.setText("View Log File");
        button2.addActionListener(this);  // below is a poor mans way to get multi LINE TIPS
        button2.setToolTipText("<html>"
                                         + "Click to Open the log File with EDITOR."
                                         +"<br>"
                                         + "You must set EDITOR in your env."
                                         +"<br>"
                                         + "export EDITOR=kwrite."
                                         +"<br>"
                                         + "Put it in /etc/profile.d/local.sh."
                                         +"</html>");
        button2.setActionCommand("button2 pressed");
        button2.setFont(new java.awt.Font("Arial", Font.BOLD, 10));
//        button2.setBackground(Color.BLACK);
//        button2.setForeground(Color.WHITE);
        toolBar.add(button2);

        //third button
//        button3 = new JButton(rightAction);
        button3 = new JButton();
        button3.setText("Start runScripts.py");
        button3.addActionListener(this);
        button3.setToolTipText("Click to run the runSripts.py file.");
        button3.setActionCommand("button3 pressed");
        button3.setFont(new java.awt.Font("Arial", Font.BOLD , 10));
//        button3.setBackground(Color.BLACK);
//        button3.setForeground(Color.WHITE);
        toolBar.add(button3);

//        below is from ButtonDemo
//        b3.setActionCommand("enable");
//        b3.setEnabled(True);

        //Listen for actions on buttons 1 and 3.
//        b1.addActionListener(this);
//        b3.addActionListener(this);

//        b1.setToolTipText("Click this button to disable the middle button.");
//        b2.setToolTipText("This middle button does nothing when you click it.");
//        b3.setToolTipText("Click this button to enable the middle button.");


    }




/** Returns an ImageIcon, or null if the path was invalid. */ // modified version of the getReadMeFile()
// looks like you have to use getResourceAsStream to get stuff out of the mxt archive on moneydance.
    protected ImageIcon createImageIcon2(String path) {
    try {
      if (debug){ System.err.println("createImageIcon2 line 422 in main");};
      ClassLoader cl = getClass().getClassLoader();
      if (debug){ System.err.println("createImageIcon2 line 424 in main:"+cl);};
      java.io.InputStream in = cl.getResourceAsStream(path);
      if (in != null) {
                if (debug){ System.err.println("createImageIcon2 line 427 in main");};
                ByteArrayOutputStream bout = new ByteArrayOutputStream(5000);
                byte buf[] = new byte[256];
                int n = 0;
                int total = 0;
                while((n=in.read(buf, 0, buf.length))>=0)
                    {
                        total = total +n;
                        if ( total > 5000){
                           System.err.println("createImageIcon2 line 436 in main: buffer overflow" );
                           throw new Exception("buffer overflow");
                           }
                        bout.write(buf, 0, n);
                        if (debug){ System.err.println("createImageIcon2 line 440 in main:" + n +":"+ total );}; //
                    }
                    if (debug){ System.err.println("createImageIcon2 line 442 in main " + total);};  //
//                    Image image = bout.toString();
//                    return ImageIcon(bout);
                    ImageIcon crap = new ImageIcon(bout.toByteArray(),"desc");
                    return crap;
                    } // end if != null
        else {
             System.err.println("createImageIcon2 line 449 in main .. Failed to find Resource");  //
            }
        } // end try
    catch (Throwable e)
    {
      System.err.println("createImageIcon2 Exception line 454 in main ");
      e.printStackTrace();
    }
    return null;
  }


// there is a long list of possible Action Listeners
// JRadioButtonMenuItem , JButton , Check box , combo box, file chooser, formatted text field, menu item ,
// password field, radio button, text field, toggle button
// e.getSource() is huge .. e.getActionCommand() is just the text on the Button
// so .. if ("Button3".equals(e.getActionCommand())) { .. should work
//the menu items behave the same way so the text on menu item is important .. maybe use setActionCommand() instead .. did this
// radio buttons come in here too but not the check boxes
//   JMenuItem source = (JMenuItem)(e.getSource());  // this crashes if its not a JMenuItem like JButton
    public void actionPerformed(ActionEvent e) {

        String s = "Action event detected."
                   + newline
                   + " Event paramString: " + e.paramString()  // its pretty short
                                                               //  ACTION_PERFORMED,cmd=Button3,when=1677524790637,modifiers=Button1
                                                               // the modifiers make no sence
                   + newline
                   + " Command:  "     + e.getActionCommand() ;
//        String test = e.getActionCommand()
      //             + "    Event source: " + source.getText()
      //             + " (an instance of " + getClassName(source) + ")";
      //  jConsole.setText(s + newline);          // put it on the JConsole screen
        if (debug){System.err.println(s);}; // log it
        if (("button1 pressed").equals(e.getActionCommand())){   // clear screen
                if (debug){System.err.println("button1 pressed .. will clear JComsole2026 screen");};
//                String buff66 = jConsole.getText();   // read all the lines on the console
//                System.err.println("length "+ buff66.length());
                jConsole.setText(">>> ",true); // clears everything and true says start the edit again
                }
        else if (e.getActionCommand().equals("button2 pressed")){  //view log file
                if (debug){System.err.println("button2 pressed .. will open the log file");};
                try {  // see Lessons/processBuilder.txt for all the debug stuff including waiting for task to finish
                ProcessBuilder pb = new ProcessBuilder(System.getenv("EDITOR"), System.getenv("HOME")+"/.JConsole2026.log" );
                Process p = pb.start();
                } // end try
                catch (IOException ee){
                    System.err.println("Main 498 ProcessBuilder Exception");
                    ee.printStackTrace();

   } // end catch
                } // end if button2
        else if (e.getActionCommand().equals("button3 pressed")){ // start runSpripts.py
                if (debug){System.err.println("button3 pressed");};
//                System.err.println("System.err button3 pressed");
//                System.out.println("System.out button3 pressed"); nothing showed up anywhere I could see
////                System.err.println("button3 pressed");
/////                JConsole2026.engine.exec ("try:\n   import traceback\n   import runScripts\nexcept Exception as e:\n   sys.stderr.write('JConsole2026 failed to import runScripts\\n')\n   traceback.print_exc()\n");
/////                JConsole2026.engine.exec ("try:\n   runScripts.runScripts()\nexcept Exception as e:\n   sys.stderr.write('JConsole2026 failed to import runScripts.runScripts\\n')\n   traceback.print_exc()\n");

                StringBuilder sb = new StringBuilder();
                    sb.append("try:\n");
                    sb.append("   import traceback\n");
                    sb.append("   import runScripts\n");
                    sb.append("   runScripts.runScripts()\n");
                    sb.append("except Exception as e:\n");
                    sb.append("   sys.stderr.write('JConsole2026 failed to import runScripts\\n')\n");   // note the \\n they all must end in .. \n");
                    sb.append("   traceback.print_exc(file=sys.stdout)\n"); // instead of stderr
//                    sb.append("   traceback.print_exc()\n");

                String resultString = sb.toString();
                JConsole2026.engine.exec(resultString);


  // the above must be one monster string to feed to engine.exec .. the interpreter counts \n as line numbers .. it counts spaces as indents
 //  you can use traceback.print_exc(file=sys.stdout)  other wise output goes to sys.stderr

//                JConsole2026.engine.exec ("try:\n  runScripts.runScripts()\nexcept:\n  sys.stderr.write('JConsole2026 failed to runScripts.runScripts()\\n')\n");

//                JConsole2026.engine.exec ("import runScripts\n");
//                JConsole2026.engine.exec ("runScripts.runScripts()\n");

//                JConsole2026.engine.exec ("try:\n  import scriptsIPC \nexcept:\n  sys.stderr.write('JConsole2026 failed to import scriptsIPC\\n')\n");
//                JConsole2026.engine.exec ("try:\n  scriptsIPC.IMPORT = 'runScripts'\nexcept:\n  sys.stderr.write('JConsole2026 fail 2set scriptsIPC.IMPORT\\n')\n");
//                JConsole2026.engine.exec ("try:\n  execfile('PreImport.py')\nexcept:\n  sys.stderr.write('JConsole2026 failed to exec PreImport.py\\n')\n");
// \\n represents a string containing a literal backslash character followed by the literal character 'n'.   ???????  guess write needs it
//                fire up the unScripts ..  sys must be imported before the above will work .. don't use "print" in any of these scripts
                }
        else if (e.getActionCommand().equals("First Menu Item")){ // Change Command History
                if (debug){System.err.println("First Menu Item");};
                try {
                ProcessBuilder pb = new ProcessBuilder(System.getenv("EDITOR"), System.getenv("HOME")+"/.JConsole2026.save" );
                Process p = pb.start();
                } // end try
                catch (IOException ee){
                    System.err.println("Main 524 ProcessBuilder Exception");
                    ee.printStackTrace();
                } // end catch
                } // end if First Menu Item
        else if (e.getActionCommand().equals("Second Menu Item")){ // Open the errlog.txt
                if (debug){System.err.println("Second Menu Item");};
                try {
                ProcessBuilder pb = new ProcessBuilder(System.getenv("EDITOR"), System.getenv("HOME")+"/.moneydance/errlog.txt" );
                Process p = pb.start();
                } // end try
                catch (IOException ee){
                    System.err.println("Main 535 ProcessBuilder Exception");
                    ee.printStackTrace();
                } // end catch
                } // end if Second Menu Item
        else if (e.getActionCommand().equals("Third Menu Item")){ // Open JConsole2026-ReadMe.txt
                if (debug){System.err.println("Third Menu Item");};
                String readMe;
                readMe = this.getReadMeFile();
//                if (debug){ System.err.println("Main runConsole 229 readMe:" + readMe);}; // dump it on the error console
                jConsole.setText(readMe +">>> ",true); // this is good enough
                } // end if Third Menu Item

        else    {
                System.err.println("Unknown button pressed");
                }


    } // end action Performed

/**

 // ItemListener All Known Implementing Classes:
//  AWTEventMulticaster, BasicComboBoxUI.ItemHandler, BasicComboPopup.ItemHandler, Checkbox.AccessibleAWTCheckbox,
//DefaultCellEditor.EditorDelegate, JCheckBox.AccessibleJCheckBox, JRadioButton.AccessibleJRadioButton,
//JToggleButton.AccessibleJToggleButton, List.AccessibleAWTList

    // check box menu Items come in here says New state: "selected" or "unselected"
    // which check box it is is in the getsource() which has the checkbox name:text in it
    //see the CheckBoxDemo to sort this out.. looks more sane
    //will try setActionCommand(String actionCommand)   didn't work
    JMenuItem source = (JMenuItem)(e.getSource());  // will crash if its not a JMenuItem
        String s = "Item event detected."
                   + newline
                   + "    Event source: " + source.getText()  // this is the text on the check box
                   + " (an instance of " + getClassName(source) + ")"
                   + newline
                   + "    New state: "
                   + ((e.getStateChange() == ItemEvent.SELECTED) ? "selected":"unselected")
                   + newline
                   + "    e.getSource(): "   + e.getSource()   // a pile of stuff
                   + newline
                   + "    e.paramString(): "   + e.paramString() // looks same as getSource but could use String funcs to get ,text=
                   + newline
//                   + "    e.getID(): "   + e.getID()  // says 701
//                   + newline
//                   + "    e.getItem(): "   + e.getItem()   // returns an Objet looks the same as getSource()
//                   + newline
                   + "    e.getItemSelectable(): "   + e.getItemSelectable()   // used by CheckBoxDemo  .. looks the same as getSource()
                   + newline
//                   + "    e.getClass(): "   + e.getClass() // says class java.awt.event.ItemEvent
//                   + newline
                   ;

//

//        output.append(s + newline);
        jConsole.setText(s + newline);          // put it on the JConsole screen
//        System.err.println(s); // log it too
**/
// now try to do it like checkBoxDemo does .. no need to look at the text on the CheckBox
 /** Listens to the check boxes. **/
    public void itemStateChanged(ItemEvent e) {

        int index = 0;
        Object source2 = e.getItemSelectable();
           if (source2 == cbMenuItem1) {
            index = 1;
        } else if (source2 == cbMenuItem2) {
            index = 2;
        } else {
            if (debug){System.err.println("Main 593 failed to find the Checkbox: "+index );};
        }

        //Now that we know which checkbox was checked, find out
        //whether it was selected or deselected.
        if (e.getStateChange() == ItemEvent.DESELECTED) {
            if (debug){System.err.println("Main 599 Checkbox: "+ index+ " was DESELECTED " );};

        } else {
            if (debug){System.err.println("Main 602 Checkbox:"+ index+ " was SELECTED " );};
        }


    }
/**
example of what e.paramString looks like  .. could slice and dice this and get the text= , stateChange= , item= , etc..
 ITEM_STATE_CHANGED,item=javax.swing.JCheckBoxMenuItem[,1,126,197x23,invalid,alignmentX=0.0,alignmentY=0.0,border=javax.swing.plaf.metal.MetalBorders$MenuItemBorder@4d7b54a9,flags=264,maximumSize=,minimumSize=,preferredSize=,defaultIcon=,disabledIcon=,disabledSelectedIcon=,margin=javax.swing.plaf.InsetsUIResource[top=2,left=2,bottom=2,right=2],paintBorder=true,paintFocus=false,pressedIcon=,rolloverEnabled=false,rolloverIcon=,rolloverSelectedIcon=,selectedIcon=,text=A check box menu item],stateChange=SELECTED
**/


/** returns a String max 5000 bytes long **/
public String getReadMeFile(){
    try {
      if (debug){ System.err.println("getReadMe line 617 in main");};  /// got called when the extension was installed
      ClassLoader cl = getClass().getClassLoader();
      if (debug){ System.err.println("getReadMe line 619 in main:"+cl);};
      java.io.InputStream in =
//        cl.getResourceAsStream("/com/moneydance/modules/features/JConsole2026/images/jpython_icon.gif");
        cl.getResourceAsStream("/com/moneydance/modules/features/JConsole2026/images/JConsole2026-ReadMe.txt");
//        cl.getResourceAsStream("images/JConsole2026-ReadMe.txt"); // doesn't work .. need a full class path
      if (in != null) {
                if (debug){ System.err.println("getReadMe line 624 in main");};
                ByteArrayOutputStream bout = new ByteArrayOutputStream(5000);
                byte buf[] = new byte[256];
                int n = 0;
                int total = 0;
                while((n=in.read(buf, 0, buf.length))>=0)
                    {
                        total = total +n;
                        if ( total > 5000){
                           System.err.println("getReadMe line 633 in main: buffer overflow" );
                           throw new Exception("buffer overflow");
                           }
                        bout.write(buf, 0, n);
                        if (debug){ System.err.println("getReadMe line 637 in main:" + n +":"+ total );}; //
                    }
                    if (debug){ System.err.println("getReadMe line 639 in main " + total);};  //
                    return bout.toString();
                    } // end if != null
        else {
             System.err.println("getReadMe line 640 in main .. Failed to find Resource");  //
            }
        } // end try
    catch (Throwable e)
    {
      System.err.println("getReadMe Exception line 648 in main ");
      e.printStackTrace();
    }
    return null;
  }






/**   not used anymore
      // Returns just the class name -- no package info.
    protected String getClassName(Object o) {
        String classString = o.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex+1);
    }
**/

/*** not sure what this was
    protected JMenu createAbleMenu() {   /// this is broken.. the JPanel needs to be modified
        JMenu ableMenu = new JMenu("Action State");
        cbmi = new JCheckBoxMenuItem[3];

        cbmi[0] = new JCheckBoxMenuItem("First action enabled");
        cbmi[1] = new JCheckBoxMenuItem("Second action enabled");
        cbmi[2] = new JCheckBoxMenuItem("Third action enabled");

        for (int i = 0; i < cbmi.length; i++) {
            cbmi[i].setSelected(true);
//            cbmi[i].addItemListener(pnl);  // public class ActionDemo extends JPanel implements ItemListener {  .. will try to run without this
//            cbmi[i].addItemListener(this); // see above
            ableMenu.add(cbmi[i]);
        }

        return ableMenu;
    }
**/
    
} // end main
