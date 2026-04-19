package comp5111.assignment;

import java.util.*;

public class CoverageRuntime {

    private static String currentTestId;
    private static Set<String> currentCoveredStatements = new HashSet<>();
    private static final Map<String, Set<String>> testToCoveredStatements = new LinkedHashMap<>();
    private static final Map<String, Boolean> testResults = new LinkedHashMap<>();

    public static void startTest(String testId) {
        currentTestId = testId;
        currentCoveredStatements = new HashSet<>();
    }

    public static void hit(String stmtId) {
        currentCoveredStatements.add(stmtId);
    }

    public static void finishTest(boolean passed) {
        if (currentTestId != null) {
            testToCoveredStatements.put(currentTestId, currentCoveredStatements);
            testResults.put(currentTestId, passed);
        }
        currentTestId = null;
        currentCoveredStatements = new HashSet<>();
    }

    public static Set<String> getCoveredStatements(String testId) {
        return testToCoveredStatements.getOrDefault(testId, Collections.emptySet());
    }

    public static Map<String, Set<String>> getAllCoverage() {
        return testToCoveredStatements;
    }

    public static Map<String, Boolean> getAllResults() {
        return testResults;
    }

    public static void reset() {
        currentTestId = null;
        currentCoveredStatements = new HashSet<>();
        testToCoveredStatements.clear();
        testResults.clear();
    }
}
