# EvoSuite Test Generation

Environment: JDK 11 (Temurin-11.0.30), EvoSuite 1.2.0

JVM options (set before running):

```
$env:JDK_JAVA_OPTIONS='--add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/java.net=ALL-UNNAMED --add-opens=java.base/java.text=ALL-UNNAMED --add-opens=java.desktop/java.awt=ALL-UNNAMED'
```

Strategy: `-criterion branch` with `-Dassertions=false -Dminimize=false` for focused branch coverage optimization.

## generated-evosuite0 (DynaMOSA, branch, 240s)

```
java -jar ".\lib\evosuite-1.2.0.jar" -class "comp5111.assignment.cut.Subject" -projectCP bin -criterion branch -Dsearch_budget=240 -Dassertions=false -Dminimize=false -mem 32000
```

EvoSuite: Branch 92%
Eclipse: Branch 91.4%, Line 94.9%

## generated-evosuite1 (DynaMOSA, branch, 300s)

```
java -jar ".\lib\evosuite-1.2.0.jar" -class "comp5111.assignment.cut.Subject" -projectCP bin -criterion branch -Dsearch_budget=300 -Dassertions=false -Dminimize=false -mem 32000
```

EvoSuite: Branch 92%
Eclipse: Branch 92.3%, Line 95.1%

## generated-evosuite2 (DynaMOSA, branch, 180s)

```
java -jar ".\lib\evosuite-1.2.0.jar" -class "comp5111.assignment.cut.Subject" -projectCP bin -criterion branch -Dsearch_budget=180 -Dassertions=false -Dminimize=false -mem 32000
```

EvoSuite: Branch 90%
Eclipse: Branch 89.3%, Line 94.5%
