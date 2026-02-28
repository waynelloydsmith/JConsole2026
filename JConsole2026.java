package com.moneydance.modules.features.JConsole2026;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Font;
import java.awt.Color;
import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.lang.InterruptedException;
import java.io.FileWriter;

import javax.swing.JTextArea;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;


import com.moneydance.modules.features.JConsole2026.streams.ConsoleInputStream;
import com.moneydance.modules.features.JConsole2026.streams.ConsoleOutputStream;

import com.moneydance.apps.md.controller.FeatureModule;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.apps.md.controller.ModuleUtil;
import com.moneydance.apps.md.controller.UserPreferences;

//import com.infinitekind.moneydance.model.*;

//import org.python.core.*;
import org.python.core.PyException;
import org.python.util.InteractiveInterpreter; // InteractiveConsole does the buffering but eats the error messages
import org.python.core.PySystemState;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.python.core.PyObject;
import java.lang.ProcessBuilder;
import java.lang.ProcessBuilder.Redirect;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.io.PrintStream;
import java.io.FileNotFoundException;

/**
 * @author Andrew Somebody modifed by waynelloydsmith
 */
public class JConsole2026 extends JTextArea implements KeyListener
{
	public static final Boolean  debug2 = false ;
	public static final Boolean  debug = false ;
	public FileWriter   	logWriter;
	public FileWriter	CMDwriter;

	private static final long	serialVersionUID	= -5866169869353990380L;
	/**
	 * The input stream that will pass data to the script engine
	 */
//	public final Reader		IN; // not required
	/**
	 * The output stream from the script engine
	 */
	public final Writer		OUT;
	/**
	 * The error stream from the script engine
	 */
	public final Writer		ERR;


	public static CommandHistory		history;
	/**
	 * index of where we can start editing text
	 */
	int				editStart;
	/**
	 * True when a script is running
	 */
	boolean				running;
	/**
	 * The script engine and scope we're using
	 */
	public static InteractiveInterpreter  engine;
	/**
	 * The allowed variables and stuff to use
	 */
	// private Bindings bindings;
	// ScriptContext context;
	private ConsoleFilter		filter;
	private Thread			pythonThread;

	// the runsource filter and return value
	public List<String> buffer = new ArrayList<>();
	public boolean	                more = false;

//	public class MyConsole extends InteractiveInterpreter { // only works with the Interactiveconsole
//                        @Override
//                        public String raw_input(PyObject prompt)
//                        {
 //                       // Custom input logic here
//                        return "Java-provided input";
//                        }
//                }


	/**
	 * 
	 */
	  
	public JConsole2026(FeatureModuleContext context)  // the constructor
	{

                try {
                    String homeDir = System.getProperty("user.home");
                    String path = homeDir+"/.JConsole2026.log";
                    if (debug){ System.err.println(Thread.currentThread().getStackTrace()[1] +" JConsole logfile path is :" + path );};
                    FileWriter logWriter = new FileWriter(path ,true); // true for append
                    this.logWriter = logWriter;
                }
                catch (Exception e){
                    if (debug){ System.err.println("JConsole2026.log FileWrite open error occurred.");};
                    e.printStackTrace();
                }

		// create streams that will link with this
//		IN = new ConsoleInputStream(this);  // removed this from the project after a lot of confusion and many hours wasted
		// System.setIn(IN);
		OUT = new ConsoleOutputStream(this);     // sends stdout output to console and appends to the log file
		// System.setOut(new PrintStream(out));
		ERR = new ConsoleOutputStream(this);     // sends stderr output to console and appends to the log file

                // setup the command history
		history = new CommandHistory();
		JConsole2026.history = history;
		history.loadHistory();
		String homeDir = System.getProperty("user.home");
                String path = homeDir+"/.JConsole2026.save";
                try {
		    FileWriter CMDwriter = new FileWriter(path ,true); // false means make a new file .. true for append
                                                            // note: consoleConfig.py removes duplicates from this file
                    this.CMDwriter = CMDwriter;
		 } // end try
		 catch (IOException e1) {
                 System.err.println("148 logWriter IOException "+ e1.getMessage());
                 System.err.println("JConsole2026.save FileWrite open error occurred.");
                }

		// setup the script engine
		PySystemState.initialize(); // instead of engine.initialize ??
                setTabSize(4);
		setLineWrap(true); // new this worked
		setWrapStyleWord(true); // new this worked
		setFont(new java.awt.Font("Arial", Font.PLAIN, 18));  //Dec 31 2025
		engine = new InteractiveInterpreter();
		engine.setIn(System.in); //
//		engine.setIn(IN); //   java handler for sys.stdin console
		engine.setOut(OUT); // java handler sys.stdout console and the log file
		engine.setErr(ERR); // java handler sys.stderr console and the log file
//		engine.exec("import sys");
//                engine.exec ("import os");
//		engine.exec("buff = sys.registry['user.home']+'/moneydance/scripts/'");
//        PyObject result ;
//       String buff22 = new String ("sys.path.count('/opt/moneydance/scripts/')");
//        result = engine.eval (buff22);      // this worked .. run the command on jython and get the results in Java :)
//        if (debug){ System.err.println("JConsole 118 engine.eval sys.path.count "+ result.asInt());};
//        if (!(result.asInt() >= 1) ){
//              if (debug){ System.err.println("JConsole 120 doing sys.path.append ");};
//              engine.exec("sys.path.append('/opt/moneydance/scripts/')");
//              }
//        else {
//              if (debug){ System.err.println("JConsole 124 NOT doing sys.path.append ");};
//         }

//        if (!sys.path.__contains__("/opt/moneydance/scripts/")) //
//          if (!os.path.exists("/opt/moneydance/scripts/")) //
		engine.set("moneydance", context );  // creates a local variable called moneydance and sets it  to context
                engine.set("Java_Parent", "JConsole2026");  // used by consoleConfig.py to set up the logger
//                engine.set("IMPORT", "runScripts" );
//                engine.exec("os.chdir('/opt/moneydance/scripts')");
//moved it down jan 21 2026                engine.exec ("execfile('/opt/moneydance/scripts/configConsole.py')"); // needs a full path
// below exec crashed because sys wasn't imported I think
//                engine.exec ("try:\n  execfile('configConsole.py')\nexcept:\n  sys.stderr.write('JConsole2026 failed to load configConsole.py\\n')\n");
//                                                                                                                                        this \\n is needed by write
//      engine.exec ("del buff"); // buff not used
//		engine.exec("from definitions import definitions");  // definitions not used for this anymore
//		engine.exec("for buff in definitions.ClassPathNames:\n  exec(buff)\n");
		addKeyListener(this);
		filter = new ConsoleFilter(this);
		((AbstractDocument) getDocument()).setDocumentFilter(filter);
		// start text and edit location
		setText("JConsole2026 Interactive Console\r\nuse window x to close\r\n >>> "); // this shows up as the first three lines on the console
		// editStart = getText().length();
		getCaret().setDot(editStart);
                engine.exec ("execfile('/opt/moneydance/scripts/configConsole.py')"); // needs a full path
        } // end of constructor


	@Override
	public void setText(String text)
	{
		setText(text, true);
	}

	/**
	 * @param text
	 * @param updateEditStart
	 */
	public void setText(String text, boolean updateEditStart)
	{
		filter.useFilters = false;
		super.setText(text);
		filter.useFilters = true;
		if (updateEditStart)
		{
			editStart = text.length();
		}
		getCaret().setDot(text.length());
	}
// setup the document filter so output and old text can't be modified

	private class ConsoleFilter extends DocumentFilter
	{


		private JConsole2026	console;
		public boolean		useFilters;

		public ConsoleFilter(JConsole2026 console)
		{
			this.console = console;
			useFilters = true;
			if (debug){ System.err.println("ConsoleFilter Init");};

		}

		@Override
		public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr)
				throws BadLocationException
		{
                        if (debug){ System.err.println("ConsoleFilter insertString offset string attr" + offset + string + attr);};
			if (useFilters)
			{
				// determine if we can insert
				if (console.getSelectionStart() >= console.editStart)
				{
					// can insert
					fb.insertString(offset, string, attr);
				}
				else
				{
					// insert at the end of the document
					fb.insertString(console.getText().length(), string, attr);
					// move cursor to the end
					console.getCaret().setDot(console.getText().length());
					// console.setSelectionEnd(console.getText().length());
					// console.setSelectionStart(console.getText().length());
				}
			}
			else
			{
				fb.insertString(offset, string, attr);
			}
		}
//
// this replace() gets called every time you type in a character .. useFilters is always false , attrs is null , length 0
// offset is zero text is the single character you typed in
// when you hit enter length becomes the length of everything on the screen and text becomes everthing on the screen
// I don't understand what this filter is filtering.. it seems to be driven by setText()

		@Override
		public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
				throws BadLocationException
		{
		        if (debug2){ System.err.println("ConsoleFilter replace filter bypass " + fb );};
		        if (debug2){ System.err.println("ConsoleFilter replace offset " + offset );};
		        if (debug2){ System.err.println("ConsoleFilter replace length " + length );};
		        if (debug2){ System.err.println("ConsoleFilter replace text " +text );};
		        if (debug2){ System.err.println("ConsoleFilter replace attrs " + attrs );};
		        if (debug2){ System.err.println("ConsoleFilter replace useFilters " + useFilters );};
			if (useFilters)
			{
                                if (debug2){ System.err.println("Console.getSelectionStart() " + console.getSelectionStart() );};
                                if (debug2){ System.err.println("console.editStart "  + console.editStart);};

				// determine if we can replace
				if (console.getSelectionStart() >= console.editStart)
				{
				        if (debug2){ System.err.println("can replace" );};
					                                                           // its always can replace
					fb.replace(offset, length, text, attrs);
				}
				else
				{
				        if (debug){ System.err.println("Console.insertString console.getText.length() " + console.getText().length() );};
					// insert at end
					fb.insertString(console.getText().length(), text, attrs);
					// move cursor to the end
					console.getCaret().setDot(console.getText().length());
					// console.setSelectionEnd(console.getText().length());
					// console.setSelectionStart(console.getText().length());
				}
			}
			else
			{
			        if (debug2){ System.err.println("ConsoleFilter replace replace"  );};
				fb.replace(offset, length, text, attrs); // this got executed at console start up
			}
		}

		@Override
		public void remove(DocumentFilter.FilterBypass fb, int offset, int length)
		    throws BadLocationException
		{
			if (debug2){ System.err.println("ConsoleFilter remove");};
			if (useFilters)
			{
				if (offset > console.editStart)
				{
					// can remove
					fb.remove(offset, length);
				}
				else
				{
					// only remove the portion that's editable
					fb.remove(console.editStart, length - (console.editStart - offset));
					// move selection to the start of the editable section
					console.getCaret().setDot(console.editStart);
					// console.setSelectionStart(console.editStart);
					// console.setSelectionEnd(console.editStart);
				}
			}
			else
			{
				fb.remove(offset, length);
			}
		}
	}

// setup the event handlers and input processing

	@Override
	public void keyPressed(KeyEvent e)
	{
 //       if (debug){ System.err.println("JConsole KeyPressed 282  "+ history.getCurrentCommand());};
		if (e.isControlDown()) // control key is pressed
		{
			if (e.getKeyCode() == KeyEvent.VK_A && !e.isShiftDown() && !e.isAltDown())
			{
				// handle select all (control A )
				// if selection start is in the editable region, try to select
				// only editable text
//				System.err.println("Control A Pressed");
				if (getSelectionStart() >= editStart)
				{
					// however, if we already have the editable region selected,
					// default select all
					if (getSelectionStart() != editStart || getSelectionEnd() != this.getText().length())
					{
						setSelectionStart(editStart);
						setSelectionEnd(this.getText().length());
						// already handled, don't use default handler
						e.consume();
					}
				}
			}
		}	
		else if (e.getKeyCode() == KeyEvent.VK_DOWN && !e.isShiftDown() && !e.isAltDown())
		{
				// next in command history (control down )
//				System.err.println(" Down Pressed");
				StringBuilder temp = new StringBuilder(getText());
				// remove the current command
				temp.delete(editStart, temp.length());
				temp.append(history.getNextCommand());
				setText(temp.toString(), false);
				e.consume();
		}
		else if (e.getKeyCode() == KeyEvent.VK_UP && !e.isShiftDown() && !e.isAltDown())
		{
				// prev in command history (control up )
//				System.err.println(" Up Pressed");				
				StringBuilder temp = new StringBuilder(getText());
				// remove the current command
				temp.delete(editStart, temp.length());
				temp.append(history.getPrevCommand());
				setText(temp.toString(), false);
				e.consume();
		}
		else if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			// handle script execution
			if (!e.isShiftDown() && !e.isAltDown())
			{
			    if (debug){ System.err.println("JConsole 344 Enter Pressed");}; // Enter keyPressed
				if (running) // who sets this
				{
					// why do we need to put text into the input stream
					StringBuilder text = new StringBuilder(this.getText());
					text.append(System.getProperty("line.separator"));
					String command = text.substring(editStart);
					setText(text.toString());
					// ((ConsoleInputStream) IN).addText(command);      // InputStream addText
				}
				else
				{
					// run the engine
					StringBuilder text = new StringBuilder(this.getText());
					String command = text.substring(editStart);
					text.append(System.getProperty("line.separator"));
					setText(text.toString());
					// add to the history
					history.add(command);   // command history called here

					// run on a separate thread
					pythonThread = new Thread(new PythonRunner(command));
					// so this thread can't hang JVM shutdown
					pythonThread.setDaemon(true);
					pythonThread.start();
				}
				e.consume();
			}
			else if (!e.isAltDown()) // shift enter , removes selected text on screen . select text with shift up or control A
			{                        // control C , control V and control X all work 
				// shift+enter
//			        System.err.println("Shift Enter Pressed");
				StringBuilder text = new StringBuilder(this.getText());
				if (getSelectedText() != null)
				{
					// replace text
					text.delete(getSelectionStart(), getSelectionEnd());
				}
				text.insert(getSelectionStart(), System.getProperty("line.separator"));
				setText(text.toString(), false);
			}
		}
		else if (e.getKeyCode() == KeyEvent.VK_HOME)
		{
			int selectStart = getSelectionStart();
			if (selectStart > editStart)
			{
				// we're after edit start, see if we're on the same line as edit
				// start
				for (int i = editStart; i < selectStart; i++)
				{
					if (this.getText().charAt(i) == '\n')
					{
						// not on the same line
						// use default handle
						return;
					}
				}
				if (e.isShiftDown())
				{
					// move to edit start
					getCaret().moveDot(editStart);
				}
				else
				{
					// move select end, too
					getCaret().setDot(editStart);
				}
				e.consume();
			}
		}
	}

	private class PythonRunner implements Runnable
	{
		private String	commands;

		public PythonRunner(String commands)
		{
            this.commands = commands;

		    if (debug){ System.err.println("403 PythonRunner commands "+commands);};

		}

		@Override
		public void run()
		{
			running = true;
//			more = false;
			try
			{

			    if (debug2){ System.err.println("441 PythonRunner run() commands "+commands);};
			    if (commands.equals("ls"))  // this worked    == doesn't work on strings in java
			    {
                              commands = "os.listdir(os.curdir)";   // try swapping the command out.. this worked but the output is ugly
                              if (debug){ System.err.println("Jconsole run() found command ls: "+ commands);};
                            }
                            else if (commands.equals("pwd"))
			    {
                              commands = "os.getcwd()";
                              if (debug){ System.err.println("Jconsole run() found command pwd: "+ commands);};
                            }
                            else if (commands.startsWith("cd "))
			    {
                              String temp2 = commands.replaceFirst("cd ","");
                              if (debug){ System.err.println("Jconsole run() found command cd temp2..: "+ temp2);};
                              commands = "os.chdir('"+temp2+"')";
                              if (debug){ System.err.println("Jconsole run() found command cd ..: "+ commands);};
                            }

                            buffer.add(commands); // Appends commands to the end of buffer
//                            source = "\n".join(self.buffer)
                            String source = String.join("\n", buffer); // dumps all the commands in the buffer with /n between them
                            source = "\n" + source + "\n";             // put /n on both ends of it ??

                            if (commands.equals("")) {                  // trigger to run runsource
                                     more = false;
                            }

//                            code below prints out a nice display of the hex bytes in the command
//                            byte[] bytes = commands.getBytes(StandardCharsets.UTF_8);
//                            System.err.print("456 Commands as bytes (hex): [ ");
//                            for(byte b : bytes) {
//                                System.err.printf("0x%02X ", b);
//                            }
//                            System.err.println("]");

			    if (debug){System.err.println("477 run() commands "+ commands);};
                            if (!more){  // its false

                                 try { // there is are extra /n on both ends of source
                                     String cleanedString = source.replaceFirst("^\n", "");
                                     logWriter.write(cleanedString);  // send it to the log file too
                                     logWriter.flush();
                                     CMDwriter.write(cleanedString); // send it directly to the .save file
                                     CMDwriter.flush();

                                }
                                catch (IOException e1) {
                                       System.err.println("529 logWriter IOException "+ e1.getMessage());
                                }
//			         more  = engine.runsource(commands +'\n');                            // had a  /n on it already
                                 more  = engine.runsource(source);                                    // feeds command to the InteractiveInterpreter
			    }
			    if (debug){System.err.println("527 run() more "+ more);};
			}
                        catch (PyException e)
			{
                           if (debug){ System.err.println("Jconsole 487 PyException "+commands);};
				// prints out the python error message to the console
                            e.printStackTrace();
			}
			StringBuilder text = new StringBuilder(getText());
			if ( more ) {
			   text.append("... ");
                        }
			else {
                           text.append(">>> ");
//                           self.resetbuffer()
                           buffer.clear();
                        }
			setText(text.toString());
			running = false;
		}
	}

//	@SuppressWarnings("deprecation")
//	@Override
//	public void finalize()
//	{
//		if (running)
//		{
//			// I know it's depracated, but since this object is being destroyed,
//			// this thread should go, too
//			pythonThread.stop();
//			//pythonThread.destroy();
//		}
//	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		// don't need to use this for anything
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		// don't need to use this for anything
	}
}
