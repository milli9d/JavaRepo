# SwingUI WebSockets Server/Client Model for JDBC/SQLite in Java
### This applet is a model of how communications can be done over WebsSockets service. 
---
### Salient Features
1. JavaFX Swing UI
: An interactive feature rich UI for both Client and Server
2. Multi-Threading 
: Server is Multi-Threaded i.e each client connection is handled by a seperate thread.
3. Transactional
: Data Transfers are treated as transactions and thus implied , connections themselves are treated as transactions. This means that each Data Transfer happens like the following
           
             Data Ready -> Connection Open -> Handshake Successful -> Data Sent -> Close Connection 

4. Partial DBMS support
: Implements some functionality of SQLite DBMS , on the flipside , SQLite is a dependency
   1. Client Side
   :  Add Entry , Clear(purge) DB , Query all DB Data
   2. Server Side 
   : Clear DB , Query All DB Data
 
5. Read-Only Console
: Both Server and Client implements a built-in Read-Only Console to give easy interface to the system , Console displays current instruction process and status. 
---
## How to DEMO 

1. Clone directory to your local machine , make sure you have JDK installed.
2. You can run directly using compiled JAR files in the root directory. The system is set to communicate on localhost for these compiled binaries , you can run both Server and Client on the same machine.
3. You can also compile the program yourself using the latest JDK, the full source is available in the directory.
    Requirements:        
       
       1. JDBC
       2. SQLite
       3. JDK
       4. JavaFX Swing
  ---
This application was submitted as a Capstone Project for CS-9053 Intro To Java at NYU and recieved a perfect score.

