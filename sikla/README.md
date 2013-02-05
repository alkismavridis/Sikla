Sikla
=====

Sokoban-style game written in Java.
Language: Greek

To run (in Linux):

1. Take the ZIP contains all the files.
2. Unpack it, and save the folder named sikla in a path of your choice. From now on I will call this YOUR_PATH.
3. Open your terminal and ct to YOUR_PATH
4. Write:
      javac sikla/*.java
Normally this will compile all files. For this you need to have java version 7. Version 6 maybe will also compile.

5. write
    java sikla.SiklaMain to run

If you want to play with a double click, just open a text editow and write:

#!/bin/bash
cd YOUR_PATH
java sikla.SiklaMain

for example I write

#!/bin/bash
cd ~/Programs/java/classes
java sikla.SiklaMain

Save it. Right-click it and go to properties. Choose "Allow to execute as a program" in the Rights tab.
Have Fun!
