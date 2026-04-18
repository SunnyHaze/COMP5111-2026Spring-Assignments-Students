mvn install:install-file \
   -Dfile=./lib/evosuite-1.2.0.jar \
   -DgroupId=org.evosuite \
   -DartifactId=evosuite \
   -Dversion=1.2.0 \
   -Dpackaging=jar \
   -DgeneratePom=true

mvn install:install-file "-Dfile=./lib/evosuite-1.2.0.jar"  "-DgroupId=org.evosuite" "-DartifactId=evosuite" "-Dversion=1.2.0" "-Dpackaging=jar" "-DgeneratePom=true"

java -jar ".\lib\evosuite-1.2.0.jar" -class "comp5111.assignment.cut.Subject" -projectCP bin -Dsearch_budget=60

$env:JAVA_TOOL_OPTIONS='--add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/java.net=ALL-UNNAMED --add-opens=java.base/java.text=ALL-UNNAMED --add-opens=java.desktop/java.awt=ALL-UNNAMED'
java -jar ".\lib\evosuite-1.2.0.jar" -class "comp5111.assignment.cut.Subject" -projectCP "bin" -Dsearch_budget=15

$env:JDK_JAVA_OPTIONS='--add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/java.net=ALL-UNNAMED --add-opens=java.base/java.text=ALL-UNNAMED --add-opens=java.desktop/java.awt=ALL-UNNAMED'    