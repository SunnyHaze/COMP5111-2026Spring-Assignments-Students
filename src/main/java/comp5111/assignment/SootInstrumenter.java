package comp5111.assignment;

import soot.*;
import soot.jimple.*;
import soot.options.Options;
import soot.util.Chain;

import java.io.*;
import java.util.*;

public class SootInstrumenter {

    private static final String CUT_CLASS = "comp5111.assignment.cut.Subject";

    public static void main(String[] args) {
        String inputDir = "bin";
        String outputDir = "instrumented-bin";

        if (args.length >= 2) {
            inputDir = args[0];
            outputDir = args[1];
        }

        new File(outputDir).mkdirs();

        instrument(inputDir, outputDir);
    }

    public static void instrument(String inputDir, String outputDir) {
        G.reset();

        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_soot_classpath(inputDir + File.pathSeparator + System.getProperty("java.class.path"));
        Options.v().set_process_dir(Collections.singletonList(inputDir));
        Options.v().set_output_dir(outputDir);
        Options.v().set_output_format(Options.output_format_class);
        Options.v().set_whole_program(false);
        Options.v().set_src_prec(Options.src_prec_class);
        Options.v().set_keep_line_number(true);
        Options.v().setPhaseOption("jb", "use-original-names:true");

        Scene.v().loadNecessaryClasses();

        SootClass cutClass = Scene.v().getSootClass(CUT_CLASS);

        // Also instrument inner classes (StringAlgorithms, DateTimeAlgorithms, etc.)
        List<SootClass> toInstrument = new ArrayList<>();
        toInstrument.add(cutClass);
        for (SootClass sc : Scene.v().getApplicationClasses()) {
            if (sc.getName().startsWith(CUT_CLASS + "$")) {
                toInstrument.add(sc);
            }
        }

        SootClass runtimeClass = Scene.v().loadClassAndSupport("comp5111.assignment.CoverageRuntime");
        SootMethod hitMethod = runtimeClass.getMethodByName("hit");

        // Registry: stmtId -> [methodSig, jimpleText, sourceLine]
        // Written to a TSV so downstream tools know what each stmtId means
        List<String[]> registry = new ArrayList<>();

        int totalStmts = 0;
        for (SootClass sc : toInstrument) {
            for (SootMethod sm : sc.getMethods()) {
                if (!sm.isConcrete()) continue;

                Body body = sm.retrieveActiveBody();
                String methodSig = body.getMethod().getSignature();
                Chain<Unit> units = body.getUnits();

                List<Unit> original = new ArrayList<>(units);
                int idx = 0;
                for (Unit u : original) {
                    if (u instanceof IdentityStmt) {
                        idx++;
                        continue;
                    }

                    String stmtId = methodSig + " ::: " + idx;
                    String jimpleText = u.toString();
                    int srcLine = u.getJavaSourceStartLineNumber();

                    registry.add(new String[]{stmtId, methodSig, jimpleText, String.valueOf(srcLine)});

                    StringConstant stmtIdConst = StringConstant.v(stmtId);
                    InvokeExpr invokeExpr = Jimple.v().newStaticInvokeExpr(
                            hitMethod.makeRef(), stmtIdConst);
                    Stmt hitStmt = Jimple.v().newInvokeStmt(invokeExpr);

                    units.insertBefore(hitStmt, u);
                    totalStmts++;
                    idx++;
                }

                body.validate();
            }
        }

        System.out.println("Instrumented " + toInstrument.size() + " classes, " + totalStmts + " statements");

        // Write out instrumented classes
        for (SootClass sc : toInstrument) {
            String fileName = SourceLocator.v().getFileNameFor(sc, Options.output_format_class);
            new File(fileName).getParentFile().mkdirs();
            OutputStream os = null;
            try {
                os = new java.io.FileOutputStream(fileName);
                new soot.baf.BafASMBackend(sc, Options.v().java_version()).generateClassFile(os);
                os.close();
            } catch (Exception e) {
                throw new RuntimeException("Failed to write " + sc.getName(), e);
            }
        }

        // Write statement registry
        String registryPath = outputDir + File.separator + "stmt_registry.tsv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(registryPath))) {
            pw.println("stmtId\tmethodSignature\tjimpleStatement\tsourceLine");
            for (String[] row : registry) {
                pw.println(row[0] + "\t" + row[1] + "\t" + row[2] + "\t" + row[3]);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write registry", e);
        }

        System.out.println("Registry: " + registryPath + " (" + registry.size() + " entries)");
        System.out.println("Output written to: " + outputDir);
    }
}
