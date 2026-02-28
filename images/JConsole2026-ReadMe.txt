This is the ReadMe.txt File for JConsole2026
First:You need to define EDITOR in the enviroment to make everything work.
Do an export EDITOR=kwrite  .. can be any editor but kwrite monitors the file for changes while you have it open.
Put it in /etc/profile.d/local.sh .. also.. you need to reboot to make local.sh run
This JConsole2026-ReadMe.txt is in the .mxt archive under /images .If your wondering where it came from.
The .mxt archive is in ~/.moneydance/fmodules/JConsole2026.mxt
I have added a log file to JConsole2026 .. It is only good for a session
I also added a command line history feature that's good for 64 commands.
It is saved in a file so it lives through reboots etc.
You can delete/edit this file with the first pick from the Menu button.
It is updated when you close JConsole2026 so open the history file with the menu button and then close JConsole2026.
When your done editing the command history save it and restart JConsole2026 the edited History will get reloaded
from ~/.moneydance/JConsole2026.save when JConsole2026 restarts.
I did this because sometimes the command line history gets messed up.
I added a Clear Screen button.
the "View Log File" Button is JConsole2026's log file not moneydance's errlog.txt file.
 JConsole2026 runs configConsole.py with execfile automatically when it starts.
I use runScipts.py to start all my moneydance scripts. Except configConsole.py
So I added a button to import runScripts.py.
You must create a directory /opt/moneydance/scripts and put all your scripts in there.
I'm so used to typing ls and cd and pwd on the console >>> that I added these commands
to JConsole2026. I mapped them to os.whatever rather than relearn these commands.
Monitoring the moneydance errlog.txt is a good idea so I added a menu item to open it.
My moneydance error log was growing into a huge file and I didn't realize it.
That's all for now.
bash-5.1$ ls -l /home/wayne/.moneydance/JConsole*
-rw-r--r-- 1 wayne users  /home/wayne/.moneydance/JConsole2026.log
-rw-r--r-- 1 wayne users  /home/wayne/.moneydance/JConsole2026.save


