@echo off

rem Purpose:  
rem This batch script demonstrates a simple asynchronous protocol run using 
rem the Java Client SDK for Server.  In order to run this
rem script the JAVA_HOME environment variable must be property set and you
rem must have previously run the compile.bat file.
rem
rem Usage: 
rem run.bat [server_url] [username] [password]
rem
rem Example Usage:
rem run.bat http://localhost:9944 scitegicuser nopassword

set CLASSPATH=class
set CLASSPATH=%CLASSPATH%;../../bin/lib/activation.jar
set CLASSPATH=%CLASSPATH%;../../bin/lib/jaxp-api.jar
set CLASSPATH=%CLASSPATH%;../../bin/lib/jax-qname.jar
set CLASSPATH=%CLASSPATH%;../../bin/lib/JaxRPC_PP_Stubs.jar
set CLASSPATH=%CLASSPATH%;../../bin/lib/jaxrpc-api.jar
set CLASSPATH=%CLASSPATH%;../../bin/lib/jaxrpc-impl.jar
set CLASSPATH=%CLASSPATH%;../../bin/lib/jaxrpc-spi.jar
set CLASSPATH=%CLASSPATH%;../../bin/lib/mail.jar
set CLASSPATH=%CLASSPATH%;../../bin/lib/relaxngDatatype.jar
set CLASSPATH=%CLASSPATH%;../../bin/lib/saaj-api.jar
set CLASSPATH=%CLASSPATH%;../../bin/lib/saaj-impl.jar
set CLASSPATH=%CLASSPATH%;../../bin/lib/xerces.jar
set CLASSPATH=%CLASSPATH%;../../bin/lib/xsdlib.jar
set CLASSPATH=%CLASSPATH%;../../bin/jalpp.jar

call jdk_version.bat

set PACKAGES=..\..\..\core\packages_win32
IF NOT EXIST %PACKAGES% @set PACKAGES=..\..\..\core\packages_win64

%PACKAGES%\java\%JAVA_VERSION%\bin\java -classpath %CLASSPATH% com.scitegic.proxy.examples.Tree_Infor %1 %2 %3
