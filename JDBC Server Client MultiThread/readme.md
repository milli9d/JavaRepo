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
: Implements some functionality of SQLite DBMS 


1. Client Side
:  Add Entry , Clear(purge) DB , Query all DB Data
2. Server Side 
: Clear DB , Query All DB Data
  
---
This application was submitted as a Capstone Project for CS-9053 Intro To Java at NYU and recieved a perfect score.

