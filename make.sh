#!/bin/sh

echo "Compile..."
/usr/java/j2sdk1.4.2_06/bin/javac src/*.java src/GameMiddlewareLayer/*.java src/MountnFall/*.java

echo "Execute..."
/usr/java/j2sdk1.4.2_06/bin/java -classpath bin MainFrame
