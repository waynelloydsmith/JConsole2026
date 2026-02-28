



package com.moneydance.modules.features.JConsole2026;

//import java.lang.Runtime;
import java.io.IOException;
//import java.io.Writer; // has append(char c); write (char[] cbuf) , write (char[] cbuf, int off, int len) ,write (char c) , write(string s), write (string s int offset, int len)
//import java.io.File;
import java.io.FileWriter; // same as Writer .. has the append flag
import java.io.FileReader; // need this for BufferedReader
import java.io.BufferedReader; // has readLine() , read() a single char , read(char[] cbuf, int off, int len)
//import java.io.BufferedWriter; // has newLine(), write (char[] cbuf,int offset,int len) , write (int c) ,write (String s, int offset, int len)

//import java.io.BufferedWriter;


public class CommandHistory                // declared in JConsole2026.java line 65 with no arguments then again at line 100
{
    public Boolean  debug = false ;
    public FileWriter   	myWriter;
    public FileReader   	myReader;
    public BufferedReader   buffReader;

	private class Node
	{
		public String	command;
		public Node		next;
		public Node		prev;

		public Node(String command)
		{
			this.command = command;
			next = null;
			prev = null;
		}
	}

	private int		length;
	/**
	 * The top command with an empty string
	 */
	private Node	top;
	private Node	current;
	private int		capacity;

	/**
	 * Creates a CommandHistory with the default capacity of 64
	 */
	public CommandHistory()   // 2nd this is called second
	{
		this(64);
        if (debug){ System.err.println("History 54:default");};

	}

	/**
	 * Creates a CommandHistory with a specified capacity
	 * 
	 * @param capacity
	 */
	public CommandHistory(int capacity)  // this is called first
	{
        if (debug){ System.err.println("History capacity 65:"+capacity);};
		top = new Node("");
		current = top;
		top.next = top;
		top.prev = top;
		length = 1;
		this.capacity = capacity;

	}

	/**
	 * @return
	 */
	public String getPrevCommand() // 4th this called by jConsole.java line 321 up key pressed
	{
//        if (debug){ System.err.println("History getPrev:");};
		current = current.prev;
		return current.command;
	}



	/**
	 * @return
	 */
	public String getNextCommand()// 4th this called by jConsole.java line 338 down key pressed
	{
//	    if (debug){ System.err.println("History getNext:");};
		current = current.next;
		return current.command;
	}

	/**
	 * Adds a command to this command history manager. Resets the command
	 * counter for which command to select next/prev.<br>
	 * If the number of remembered commands exceeds the capacity, the oldest
	 * item is removed.<br>
	 * Duplicate checking only for most recent item.
	 * 
	 * @param command
	 */
	public void add(String command) // 3rd this called by jConsole2026.java line 338 when enter pressed
	{                                // also called many times by loadHistory()
		if (debug){ System.err.println("History add:"+command);}; // ls showed up here
	    // move back to the top
		current = top;

		// see if we even need to insert
		if (top.prev.command.equals(command))
		{
			// don't insert
			return;
		}
		// insert before top.next
		Node temp = new Node(command);
		Node oldPrev = top.prev;
		temp.prev = oldPrev;
		oldPrev.next = temp;
		temp.next = top;
		top.prev = temp;
		length++;
		if (length > capacity)
		{
			// delete oldest command
			Node newNext = top.next.next;
			top.next = newNext;
			newNext.prev = top;
		}
	}


	/**
	 *
	 */
	public void saveHistory()  // was triggered by Window Close event .. not used any more commands are written directly to the .save file
	{
        if (debug){ System.err.println("History saveHistory:");};
        String homeDir = System.getProperty("user.home");
        String path = homeDir+"/.JConsole2026.save";
//            File file = new File(path);
//            File fOut = new File(path);  // append ??
//            System.err.println(Thread.currentThread().getStackTrace()[1]); // very cool
        if (debug){ System.err.println(Thread.currentThread().getStackTrace()[1] +" saveFile path is :" + path );};  // gets called twice

//            if(!fOut.exists()){
//               System.err.println("JConsole2026 createNewFile()."); // doesn't run and doesn't work wirh FileWriter has no append
//               fOut.createNewFile();
//            }
//            FileWriter myWriter = new FileWriter(path ,true); // works true for append
        try {
            FileWriter myWriter = new FileWriter(path ,true); // false means make a new file .. true for append
            this.myWriter = myWriter;
            for(int i=0;i < getLength(); i++) {
                String temp = getNextCommand(); // this never tells us when there are no more commands
//                String temp = getPrevCommand(); // this never tells us when there are no more commands
                                                // will use getLenght and skip the blanks
               if (temp.equals("")) {
                    if (debug){ System.err.println("History saveHistory skipping a blank "+i);};
//                    break;
               }
               else {
                if (debug){ System.err.println("History saveHistory "+i+" "+temp);};
                myWriter.write(temp);
                myWriter.write("\n");
                }
                } // end for
//            myWriter.write(getPrevCommand());
//            myWriter.write("\r\n");
//            myWriter.write(getPrevCommand());
//            myWriter.write("\r\n");
            myWriter.close();
//          if(!myWriter.exists())
//            myWriter.write(buff66,0,buff66.length());
            } // end try
        catch (Exception e)
            {
            if (debug){ System.err.println("JConsole2026.save FileWrite open error occurred.");};
            e.printStackTrace();
            }

		return;
	}

    /**
	 *
	 **/


	public void loadHistory()  // first thing called by JConsole2026.java
	{
        if (debug){ System.err.println("History loadHistory:");};
        String homeDir = System.getProperty("user.home");
        String path = homeDir+"/.JConsole2026.save";
        try {

            FileReader myReader = new FileReader(path);
            this.myReader = myReader;
            BufferedReader buffReader = new BufferedReader(myReader);
            this.buffReader = buffReader;
// from https://docs.oracle.com/javase/7/docs/api/java/io/BufferedReader.html#readLine()
//Reads a line of text. A line is considered to be terminated by any one of a line feed ('\n'), a carriage return ('\r'), or a carriage return followed immediately by a linefeed.
//Returns: A String containing the contents of the line, not including any line-termination characters, or null if the end of the stream has been reached
           int n = 0;
           while ( true ){
                 n++;
                 String temp = buffReader.readLine();
//                 if (temp.equals(null)) { // Crash java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "temp" is null
                 if (temp == null) { // there is also a str.isEmpty .. str.isBlank  str.trim
                   if (debug){ System.err.println("History loadHistory found null "+n);};
                    break;
                 }; // end if
                 add(temp);
                 if (debug){System.err.println("loadHistory added "+ temp);};
                } // end while

                myReader.close();
                buffReader.close();

            } // end try

//
//            for(int i=0;i < 64; i++) {
//                String temp = myReader.read();
//                if (temp.equals("")) {
//                    if (debug){ System.err.println("History //loadHistory found end of file "+i);};
//                    break;
//                };
//
//            char buf[] = new char[256];
//            int n = 0;
//            while((n=myReader.read(buf, 0, buf.length))>=0) // returns number of bytes read or -1
//            {
//               if (debug){System.err.println("loadHistory Read:"+ n +"Bytes");}; // says 23
//               String string66 = new String(buf);
//
//                String[] stringArray = string66.split("\n"); // looks like this did it ...........
//                for (String aaa : stringArray){
//                    if (aaa.equals("")){
//                    if (debug){System.err.println("loadHistory skipping a Blank "+ aaa);};
////                      add(aaa);
//                    }
//                    else{
//                    add(aaa);
//                    if (debug){System.err.println("loadHistory added "+ aaa);}; // get 4 println's the last one is blank
//                    }
//                    } // end for
//               System.err.println("loadCommands4 string66:"+ string66); //  get a list of commands now so string66 is ok now
//               System.err.println("loadCommands3 length:"+ stringArray.length); // says 4 ??
//
//               System.err.println("loadCommands2 "+ stringArray[0]); // ok
//               System.err.println("loadCommands2a "+ stringArray[1]); // ok


        catch (Exception e)
               {
               if (debug){ System.err.println("JConsole2026.save FileReader open error occurred.");};
               e.printStackTrace();
               }

		return;
	}

//bash-5.1$ hexdump -c JConsole2026.save
//0000000   e   x   p   o   r   t  \n   t   h   a   t  \n   t   h   i   s
//0000010  \n
//0000011


	/**
	 * @return the capacity
	 */
	public int getCapacity()
	{
        if (debug){ System.err.println("History getCapacity:");};
		return capacity;
	}

	/**
	 * @return the length
	 */
	public int getLength()
	{
        if (debug){ System.err.println("History getLength:"+ length);};
		return length;
	}
}
