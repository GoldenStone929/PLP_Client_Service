Title: Using the Java Client SDK for Pipeline Pilot Server (JDK 11 + )

The document provides instructions for using the Java Client SDK, a set of pre-built Java classes that simplify the creation of a custom SOAP client for the Pipeline Pilot server. By abstracting much of the complexity involved in creating SOAP clients, the SDK offers an object-oriented interface that may feel familiar.

Section: Building the Example
 
The root folder contains aids for Windows development, including:
compile.bat: A batch file with a command line to build the source code.
run.bat: A file to execute a specific class.
A tip notes that the example requires jar files located in a specific directory where the Pipeline Pilot server is installed.
Section: Running the Example

The Java example is designed to run one of the example Web services protocols and can be used as a starting point for writing Java client scripts to access custom Web services protocols on the server.
Steps to run the example on Windows:
Open a command shell and change the directory to the root folder.
Run compile.bat in the shell.
Run the example with the run.bat batch file.
The syntax is run.bat <server_URL> <username> <password>, with details provided for HTTP and SSL usage, server name, and port.
Another tip advises copying the jar files and editing the paths in the batch file to the jars and jdk/bin directory if the example is copied to another location.



Note:
"This project is currently compatible with JDK version 11 or higher. I am in the process of downgrading it to JDK 1.8, and the updated version will be available in a separate repository. 
The goal is to transform it into a simple plugin or a software itself for data handling.

All functionalities are working as expected, and it serves as a free alternative to `biovia webport`. 
Feel free to download and use it, and please don't hesitate to let me know if you encounter any issues."	
