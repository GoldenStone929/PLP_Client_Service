mkdir -p class

export CLASSPATH=class
export CLASSPATH=$CLASSPATH:../../bin/lib/activation.jar
export CLASSPATH=$CLASSPATH:../../bin/lib/jaxp-api.jar
export CLASSPATH=$CLASSPATH:../../bin/lib/jax-qname.jar
export CLASSPATH=$CLASSPATH:../../bin/lib/JaxRPC_PP_Stubs.jar
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

. jdk_version.sh

PACKAGES_DIR=../../../core/packages_linux32
if [ -d ../../../core/packages_linux64 ]; then
	PACKAGES_DIR=../../../core/packages_linux64
fi

$PACKAGES_DIR/java/$JAVA_VERSION/bin/javac -d class -classpath  $CLASSPATH  com/scitegic/proxy/examples/*.java

