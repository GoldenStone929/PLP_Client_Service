# Using the Java Client SDK for Pipeline Pilot Server 

### JDK 11+ Availability
The JDK 11+ version of the project is available, and it provides the necessary functionality for integration with the Pipeline Pilot server. The corresponding ".jar" file's source code is currently withheld but will be uploaded and made accessible once I obtain the required permissions.

### JDK 8 Availability
The JDK 8 version is also available and includes all the essential source code. See below.

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

The document provides instructions for using the Java Client SDK, a set of pre-built Java classes that simplify the creation of a custom SOAP client for the Pipeline Pilot server. By abstracting much of the complexity involved in creating SOAP clients, the SDK offers an object-oriented interface that may feel familiar.

## Building the Example

The root folder contains aids for Windows development, including:
- `compile.bat`: A batch file with a command line to build the source code.
- `run.bat`: A file to execute a specific class.

A tip notes that the example requires jar files located in a specific directory where the Pipeline Pilot server is installed.

## Running the Example

The Java example is designed to run one of the example Web services protocols and can be used as a starting point for writing Java client scripts to access custom Web services protocols on the server.

Steps to run the example on Windows:
1. Open a command shell and change the directory to the root folder.
2. Run `compile.bat` in the shell.
3. Run the example with the `run.bat` batch file.

The syntax is `run.bat <server_URL> <username> <password>`, with details provided for HTTP and SSL usage, server name, and port.

Another tip advises copying the jar files and editing the paths in the batch file to the jars and jdk/bin directory if the example is copied to another location.

## Compatibility and Project Goals

This project is currently compatible with JDK version 11 or higher. I am in the process of downgrading it to JDK 1.8, and the updated version will be available in a separate repository. The goal is to transform it into a simple plugin or a software itself for data handling.

All functionalities are working as expected, and it serves as a free alternative to `biovia webport`. Feel free to download and use it, and please don't hesitate to let me know if you encounter any issues.

### JDK 8 Version

I am pleased to announce that I have successfully decompressed the source code and rectified existing errors for the JDK 8 version. The project is now operable within the JDK environment!

The repository for the JDK 8 version is currently maintained under **private access**. Once the requisite authorization is obtained, I will promptly release the complete source code to the public domain.

In the interim, my efforts will be concentrated on meticulous **debugging** and enhancement of the **user interface**. Stay tuned for exciting updates!

The initiative represents a JDK downgrade version of the existing project, available at the following link:
[https://github.com/GoldenStone929/PLP_Client_Service.git](https://github.com/GoldenStone929/PLP_Client_Service.git)

Your continued interest and support are greatly appreciated!
