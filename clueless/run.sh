

# build
a=$1

if [[ "$a" == "b" ]]; then
    cd /Users/evanedelstein/Desktop/JHU/Spring2025/SoftwareEngineering/ClueLessCoders/clueless; JAVA_HOME=/opt/homebrew/Cellar/openjdk/23.0.2/libexec/openjdk.jdk/Contents/Home "/Applications/Apache NetBeans.app/Contents/Resources/netbeans/java/maven/bin/mvn" --no-transfer-progress clean install
fi
# run
if [[ "$a" == "c" ]]; then
    /opt/homebrew/Cellar/openjdk/23.0.2/libexec/openjdk.jdk/Contents/Home/bin/java -classpath /Users/evanedelstein/Desktop/JHU/Spring2025/SoftwareEngineering/ClueLessCoders/clueless/target/classes cluelesscoders.clueless.Clueless c
fi
if [[ "$a" == "s" ]]; then
    /opt/homebrew/Cellar/openjdk/23.0.2/libexec/openjdk.jdk/Contents/Home/bin/java -classpath /Users/evanedelstein/Desktop/JHU/Spring2025/SoftwareEngineering/ClueLessCoders/clueless/target/classes cluelesscoders.clueless.Clueless s
fi