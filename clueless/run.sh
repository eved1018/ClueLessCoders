#!/usr/bin/env bash

# build
a=$1

if [[ "$a" == "b" ]]; then
    mvn --no-transfer-progress clean install
fi
# run
if [[ "$a" == "c" ]]; then
    java -classpath $PWD/target/classes cluelesscoders.clueless.Clueless c
fi
if [[ "$a" == "s" ]]; then
    java -classpath $PWD/target/classes cluelesscoders.clueless.Clueless s
fi