#!/usr/bin/env bash

# build
a=$1

if [[ "$a" == "b" ]]; then
    JAVA_HOME="/opt/homebrew/Cellar/openjdk@21/21.0.6/libexec/openjdk.jdk/Contents/Home" mvn --no-transfer-progress clean install
fi
# run
if [[ "$a" == "c" ]]; then
    java -classpath $PWD/target/classes cluelesscoders.clueless.Clueless c
fi
if [[ "$a" == "s" ]]; then
    java -classpath $PWD/target/classes cluelesscoders.clueless.Clueless s
fi

if [[ "$a" == "t" ]]; then
    JAVA_HOME="/opt/homebrew/Cellar/openjdk@21/21.0.6/libexec/openjdk.jdk/Contents/Home" mvn --no-transfer-progress test
fi