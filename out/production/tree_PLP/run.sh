# Purpose:  
# This script demonstrates a simple asynchronous protocol run using the
# Java Client SDK for Server.  In order to run this
# script the JAVA_HOME environment variable must be property set and you
# must have previously run the compile.sh file.
#
# Usage: 
# run.sh [server_url] [username] [password]
#
# Example Usage:
# run.sh http://localhost:9944 scitegicuser nopassword

export CLASSPATH=class
export CLASSPATH=$CLASSPATH:../../bin/lib/activation.jar
export CLASSPATH=$CLASSPATH:../../bin/lib/jaxp-api.jar
export CLASSPATH=$CLASSPATH:../../bin/lib/jax-qname.jar
export CLASSPATH=$CLASSPATH:../../bin/lib/jaxrpc_pp_stubs.jar
export CLASSPATH=$CLASSPATH:../../bin/lib/jaxrpc-api.jar
export CLASSPATH=$CLASSPATH:../../bin/lib/jaxrpc-impl.jar
export CLASSPATH=$CLASSPATH:../../bin/lib/jaxrpc-spi.jar
export CLASSPATH=$CLASSPATH:../../bin/lib/mail.jar
export CLASSPATH=$CLASSPATH:../../bin/lib/relaxngDatatype.jar
export CLASSPATH=$CLASSPATH:../../bin/lib/saaj-api.jar
export CLASSPATH=$CLASSPATH:../../bin/lib/saaj-impl.jar
export CLASSPATH=$CLASSPATH:../../bin/lib/xerces.jar
export CLASSPATH=$CLASSPATH:../../bin/lib/xsdlib.jar
export CLASSPATH=$CLASSPATH:../../bin/jalpp.jar

echo $CLASSPATH

. jdk_version.sh

PACKAGES_DIR=../../../core/packages_linux32
if [ -d ../../../core/packages_linux64 ]; then
	PACKAGES_DIR=../../../core/packages_linux64
fi

$PACKAGES_DIR/java/$JAVA_VERSION/bin/java -classpath $CLASSPATH com.scitegic.proxy.examples.SimpleAsynchronousRun $1 $2 $3

