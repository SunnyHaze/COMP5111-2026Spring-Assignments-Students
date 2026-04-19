package comp5111.assignment;

import java.io.*;
import java.util.*;

public class OchiaiRanker {

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Usage: OchiaiRanker <registryTsv> <spectrumDir> <outputTsv>");
            System.exit(1);
        }

        String registryPath = args[0];
        String spectrumDir = args[1];
        String outputPath = args[2];

        Map<String, String[]> registry = loadRegistry(registryPath);
        Map<String, Boolean> testResults = loadTestResults(spectrumDir + File.separator + "test_results.tsv");
        Map<String, Set<String>> coverageMatrix = loadCoverageMatrix(spectrumDir + File.separator + "coverage_matrix.tsv");

        int totalFailed = 0;
        for (boolean v : testResults.values()) {
            if (!v) totalFailed++;
        }

        // Compute Ochiai for each statement in the registry
        List<StmtScore> scores = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : registry.entrySet()) {
            String stmtId = entry.getKey();
            String[] info = entry.getValue();
            String methodSig = info[0];
            String jimpleText = info[1];
            String sourceLine = info[2];

            int ef = 0, ep = 0;
            List<String> failingTests = new ArrayList<>();
            for (Map.Entry<String, Boolean> te : testResults.entrySet()) {
                String testId = te.getKey();
                boolean passed = te.getValue();
                Set<String> covered = coverageMatrix.getOrDefault(testId, Collections.emptySet());
                if (covered.contains(stmtId)) {
                    if (passed) ep++;
                    else { ef++; failingTests.add(testId.substring(testId.indexOf('#') + 1)); }
                }
            }
            int nf = totalFailed - ef;

            double score = ochiai(ef, ep, nf);
            scores.add(new StmtScore(stmtId, methodSig, jimpleText, sourceLine, score, ef, ep, nf, failingTests));
        }

        // Sort: descending by score, then alphabetical by methodSig, then jimpleText
        scores.sort((a, b) -> {
            int cmp = Double.compare(b.score, a.score);
            if (cmp != 0) return cmp;
            cmp = a.methodSig.compareTo(b.methodSig);
            if (cmp != 0) return cmp;
            return a.jimpleText.compareTo(b.jimpleText);
        });

        // Compute ranking: (N + M + 1) / 2
        assignRanking(scores);

        // Write output
        File outFile = new File(outputPath);
        if (outFile.getParentFile() != null) outFile.getParentFile().mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(outputPath))) {
            pw.println("method signature\tJimple statement\tsuspicious score\tranking\tsource line\tef\tep\tnf\tfailing tests");
            for (StmtScore s : scores) {
                pw.println(s.methodSig + "\t" + s.jimpleText + "\t"
                        + String.format("%.10f", s.score) + "\t" + s.ranking + "\t" + s.sourceLine
                        + "\t" + s.ef + "\t" + s.ep + "\t" + s.nf
                        + "\t" + String.join(";", s.failingTests));
            }
        }

        System.out.println("Spectrum report written to " + outputPath);
        System.out.println("  Statements: " + scores.size());
        System.out.println("  Top 10:");
        for (int i = 0; i < Math.min(10, scores.size()); i++) {
            StmtScore s = scores.get(i);
            System.out.printf("    rank=%.1f  score=%.6f  line=%-4s  %s%n", s.ranking, s.score, s.sourceLine, s.stmtId);
        }
    }

    static double ochiai(int ef, int ep, int nf) {
        double denom = Math.sqrt((double) (ef + nf) * (ef + ep));
        if (denom == 0) return 0.0;
        return ef / denom;
    }

    static void assignRanking(List<StmtScore> sorted) {
        int n = sorted.size();
        for (int i = 0; i < n; ) {
            int j = i;
            while (j < n && sorted.get(j).score == sorted.get(i).score) {
                j++;
            }
            // i..j-1 have the same score
            // N = i (number of scores strictly higher)
            // M = j (number of scores >= this score)
            double rank = (i + j + 1) / 2.0;
            for (int k = i; k < j; k++) {
                sorted.get(k).ranking = rank;
            }
            i = j;
        }
    }

    static Map<String, String[]> loadRegistry(String path) throws IOException {
        Map<String, String[]> registry = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t", 4);
                if (parts.length >= 3) {
                    registry.put(parts[0], new String[]{parts[1], parts[2],
                            parts.length > 3 ? parts[3] : "-1"});
                }
            }
        }
        return registry;
    }

    static Map<String, Boolean> loadTestResults(String path) throws IOException {
        Map<String, Boolean> results = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t", 2);
                results.put(parts[0], "PASS".equals(parts[1]));
            }
        }
        return results;
    }

    static Map<String, Set<String>> loadCoverageMatrix(String path) throws IOException {
        Map<String, Set<String>> matrix = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t", 3);
                Set<String> stmts = new HashSet<>();
                if (parts.length == 3 && !parts[2].isEmpty()) {
                    for (String s : parts[2].split(";")) {
                        stmts.add(s);
                    }
                }
                matrix.put(parts[0], stmts);
            }
        }
        return matrix;
    }

    static class StmtScore {
        String stmtId;
        String methodSig;
        String jimpleText;
        String sourceLine;
        double score;
        double ranking;
        int ef, ep, nf;
        List<String> failingTests;

        StmtScore(String stmtId, String methodSig, String jimpleText, String sourceLine, double score,
                  int ef, int ep, int nf, List<String> failingTests) {
            this.stmtId = stmtId;
            this.methodSig = methodSig;
            this.jimpleText = jimpleText;
            this.sourceLine = sourceLine;
            this.score = score;
            this.ef = ef;
            this.ep = ep;
            this.nf = nf;
            this.failingTests = failingTests;
        }
    }
}
