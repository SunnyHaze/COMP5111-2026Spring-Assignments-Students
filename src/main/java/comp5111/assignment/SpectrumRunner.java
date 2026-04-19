package comp5111.assignment;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

public class SpectrumRunner {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: SpectrumRunner <testClassName> <outputDir>");
            System.exit(1);
        }

        String testClassName = args[0];
        String outputDir = args[1];
        new File(outputDir).mkdirs();

        CoverageRuntime.reset();

        Class<?> testClass = Class.forName(testClassName);
        List<String> testMethods = discoverTests(testClass);
        System.out.println("Found " + testMethods.size() + " test methods in " + testClassName);

        JUnitCore core = new JUnitCore();
        int passed = 0, failed = 0;

        for (String methodName : testMethods) {
            String testId = testClassName + "#" + methodName;
            CoverageRuntime.startTest(testId);

            Result result;
            try {
                result = core.run(Request.method(testClass, methodName));
            } catch (Exception e) {
                CoverageRuntime.finishTest(false);
                failed++;
                continue;
            }

            boolean success = result.wasSuccessful();
            CoverageRuntime.finishTest(success);

            if (success) passed++;
            else failed++;
        }

        System.out.println("Results: " + passed + " passed, " + failed + " failed");

        writeCoverageData(outputDir, testClassName);
    }

    private static List<String> discoverTests(Class<?> clazz) {
        List<String> methods = new ArrayList<>();
        for (Method m : clazz.getMethods()) {
            if (m.isAnnotationPresent(Test.class)) {
                methods.add(m.getName());
            }
        }
        Collections.sort(methods);
        return methods;
    }

    private static void writeCoverageData(String outputDir, String testClassName) throws IOException {
        Map<String, Set<String>> allCoverage = CoverageRuntime.getAllCoverage();
        Map<String, Boolean> allResults = CoverageRuntime.getAllResults();

        // Write test results
        String resultsFile = outputDir + File.separator + "test_results.tsv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(resultsFile))) {
            pw.println("testId\tresult");
            for (Map.Entry<String, Boolean> e : allResults.entrySet()) {
                pw.println(e.getKey() + "\t" + (e.getValue() ? "PASS" : "FAIL"));
            }
        }

        // Collect all statement IDs
        Set<String> allStmts = new TreeSet<>();
        for (Set<String> stmts : allCoverage.values()) {
            allStmts.addAll(stmts);
        }

        // Write coverage matrix
        String matrixFile = outputDir + File.separator + "coverage_matrix.tsv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(matrixFile))) {
            pw.println("testId\tresult\tcoveredStatements");
            for (Map.Entry<String, Boolean> e : allResults.entrySet()) {
                String testId = e.getKey();
                boolean pass = e.getValue();
                Set<String> covered = allCoverage.getOrDefault(testId, Collections.emptySet());
                pw.println(testId + "\t" + (pass ? "PASS" : "FAIL") + "\t" + String.join(";", new TreeSet<>(covered)));
            }
        }

        System.out.println("Coverage data written to " + outputDir);
        System.out.println("  Unique statements covered: " + allStmts.size());
        System.out.println("  Tests: " + allResults.size());

        // Write summary
        int totalTests = allResults.size();
        int passCount = 0, failCount = 0;
        List<String> failNames = new ArrayList<>();
        for (Map.Entry<String, Boolean> e : allResults.entrySet()) {
            if (e.getValue()) passCount++;
            else { failCount++; failNames.add(e.getKey()); }
        }
        String summaryFile = outputDir + File.separator + "summary.txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(summaryFile))) {
            pw.println("total: " + totalTests);
            pw.println("pass:  " + passCount);
            pw.println("fail:  " + failCount);
            pw.println();
            pw.println("failing tests:");
            for (String name : failNames) {
                pw.println("  " + name);
            }
        }
    }
}
