@echo off

mkdir class 2> nul.txt

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

%PACKAGES%\java\%JAVA_VERSION%\bin\javac -d class -classpath %CLASSPATH% com/scitegic/proxy/examples/*.java
