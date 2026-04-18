# EvoSuite Test Generation

Environment: JDK 11 (Temurin-11.0.30), EvoSuite 1.2.0

JVM options (set before running):

```
$env:JDK_JAVA_OPTIONS='--add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/java.net=ALL-UNNAMED --add-opens=java.base/java.text=ALL-UNNAMED --add-opens=java.desktop/java.awt=ALL-UNNAMED'
```

## generated-evosuite0 (DynaMOSA, 240s)

```
java -jar ".\lib\evosuite-1.2.0.jar" -class "comp5111.assignment.cut.Subject" -projectCP bin -Dsearch_budget=240
```

EvoSuite：Line 95%, Branch 87%
Eclipse: Line 94.3%, Branch 86.5%

## generated-evosuite1 (DynaMOSA, 300s)

```
java -jar ".\lib\evosuite-1.2.0.jar" -class "comp5111.assignment.cut.Subject" -projectCP bin -Dsearch_budget=300
```

EvoSuite：Line 93%, Branch 85%
Eclipse: Line 92.0%, Branch 83.7%


## generated-evosuite2 (DynaMOSA, 240s, pop=200)

```
java -jar ".\lib\evosuite-1.2.0.jar" -class "comp5111.assignment.cut.Subject" -projectCP bin -Dsearch_budget=240 -Dpopulation=200
```

EvoSuite：Line 93%, Branch 84%
Eclipse: Line 92.6%, Branch 82.9%
