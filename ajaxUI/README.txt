NOTE: 

* This is a LIVE document. Please come back for the latest version.

* This document will guide you through the system setup.

* This project requires Java SE 6 Update 4 or later.

* The project is managed using Apache Maven2. You must have maven
  installed to finish the installation.

* The first time when you build the project, a large amount of
  dependent jar files will be downloaded.

MEDIATOR SERVER INSTALATION
==================

Prerequisite:
===========

* Globus toolkit need to be installed. Please visit

    http://cyberaide.org/projects/cyberaide/cyberaide-shell/cyberaide-developers-manual#GlobusToolkitInstallation

  for an example on how to do this.  Please see the Refs section for
  detailed information.

* Java CoG Kit is needed. Please visit

    http://wiki.cogkit.org/wiki/Main_Page 

  for more information.  CoG Kit provided GUI for setup, it should be
  fairly easy to do so.  Make sure the environment variable
  COG_INSTALL_PATH is set after installation.

* Please take notice on system and network configurations such as
  GridFTP port. Be sure Globus and CoG Kit is running well.
  
Service build and deployment:
===========
The server is by default installed on port 8998. However, if you like
to change it, you can do so in the Makefile. We have conveniently
included a PORT variable that you can set.

To compile use 

  make mediator-build

To deploy the web service use 

  make mediator-run

Calling make mediator-run, will set up the server and the console
waits till the server has been interrupted. You can do this with
CTRL+c.

If you like to combine build and run in one command, you can also use

  make mediator.

To see if the server is running, simply check in your browser the link such as following:

  http://lily01.rit.edu:8998/mediator?wsdl

Please replace the host and port part with your actual settings.

You should see the wsdl document describing the server.

AGENT SERVICE INSTALATION
==================

Prerequisite:
===========

* TOMCAT need to be installed. Please refer to

  http://tomcat.apache.org/

  for more information

* Configure HTTPS connection for tomcat. Please see

  http://tomcat.apache.org/tomcat-6.0-doc/ssl-howto.html

Service build and deployment:
===========
  make agent-pom
  
to generate the appropriate pom file for the agent service

In order for the agent to be able to be compiled the server must be
running. To compile use

  make agent-build
  
Then the WAR file called agent.war would be generated under

  ./agent/target

Simply put the war file into your TOMCAT webapps directory to
complete the deployment.  The directory is like:

  $TOMCAT_HOME/webapps/

You also can combine the above steps into one by executing

  make agent

JAVASCRIPT API AND WEB PORTAL INSTALL
==================

Prerequisite:
===========
The same as in Agent service part.
Currently tested on Firefox 3.0+.

Web application installation:
===========

* Download Dojo toolkit 1.2.0 from

  http://dojotoolkit.org/downloads

* Unzip and put the whole directory under the ./client/lib
  directory. Rename the directory name to 'dojo', in case of version
  number is included in the name.  (We don't include this into our
  code repository due to the space limit)

* copy all the content of directory ./client into the directory where
  you want to put your portal.  For example, if you put them under
  $TOMCAT_HOME/webapps/grid/ Then the URL of the portal page should be
  like: https://lily01.rit.edu:8443/grid/jsportal.html

* Now open this link to test whether the portal page is installed
  correctly.  Then try to authenticate using MyProxy. If you can
  logged in successfully, congratulations! You have finished the
  installation of all the tiers and the system is running
