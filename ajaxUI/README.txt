This example uses CXF as the Web service framework. YOu can find out more about CXF at:

  http://???

We have developed some simple template to create a secure Web service
that uses a username password authentication scheme. You need to
deploy the server first.


SET ADMIN USER NAME, PSWD

SERVER INSTALATION
==================

The server is by default installed on port 8998. However, if you like
to cahnge it, you can do so in the Makefile. We have conveniently
included a PORT variable that you can set.


To compile use 

  make server-build

To deploy the web service use 

  make server-run

Calling make server-run, will set up the server and the console waits
till the server has been interrupted. You can do this with CTRL+c.

Often it is desirable to run the server in the background. YOu can do this with ??

Please note that it will print a PID of the provcess that runs the server.
However we have a convenient interface developed to stop the server with 

  ???? make server-stop

If you like to combine build and run in one command, you can also use 

  make server.


To see if the server is running, simply check in your browser the following link:

  http://iris01.rit.edu:8998/mediator?wsdl

You should see the wsdl document describing the server.



ADDING USERS TO THE SERVER
==========================

To add users to the server you need to do the following:

  ???

REMOVING USERS FROM THE SERVER
==============================

To remove users from accessing the server you need to do the following:

   ???


CLIENT INSTALATION
==================

In order for the client to be able to be compiled the server must be running

To compile and run the client to consume the web service

  make client



