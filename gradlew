#!/usr/bin/env sh

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS=""

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Use the value of the JAVACMD environment variable if it's set, otherwise find java.
if [ -n "$JAVACMD" ] ; then
    # ... logic to use the specified Java command
else
    # ... logic to find a working 'java' command on the system's PATH
fi

# ... a lot more logic to determine paths and arguments ...

# The most important line, which executes the wrapper JAR file
exec "$JAVACMD" "${JVM_OPTS[@]}" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
