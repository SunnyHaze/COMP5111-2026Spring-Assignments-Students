import subprocess
import os
import shutil
import sys
import argparse

BASE = os.path.normpath(os.path.join(os.path.dirname(os.path.abspath(__file__)), ".."))
BIN_ORIG = os.path.join(BASE, "bin")
BIN_DEBUG = os.path.join(BASE, "debug_bin")
INST_DEBUG = os.path.join(BASE, "debug_instrumented")
OUT_DEBUG = os.path.join(BASE, "debug_spectrum")
LIB = os.path.join(BASE, "lib")

# Default test suites: (suite_name, test_src_dir_under src/test/, fully_qualified_test_class)
DEFAULT_SUITES = [
    ("randoop0",  "fault-revealing-randoop0",  "comp5111.assignment.cut.Subject_randoop111_RegressionTest0"),
    ("randoop1",  "fault-revealing-randoop1",  "comp5111.assignment.cut.Subject_randoop112_RegressionTest0"),
    ("evosuite0", "fault-revealing-evosuite0", "comp5111.assignment.cut.Subject_evosuite65_ESTest"),
    ("evosuite1", "fault-revealing-evosuite1", "comp5111.assignment.cut.Subject_evosuite66_ESTest"),
]

SEP = ";"


def run(cmd, label=""):
    print("  > " + " ".join(cmd[:6]) + (" ..." if len(cmd) > 6 else ""))
    r = subprocess.run(cmd, capture_output=True, text=True)
    if r.returncode != 0:
        print("  FAILED: " + label)
        print(r.stdout[-500:] if r.stdout else "")
        print(r.stderr[-500:] if r.stderr else "")
        return False
    return True


def count_fails(tsv_path):
    if not os.path.exists(tsv_path):
        return -1, -1
    total = 0
    fails = 0
    with open(tsv_path, "r") as f:
        next(f)
        for line in f:
            total += 1
            if "\tFAIL" in line:
                fails += 1
    return total, fails


def parse_args():
    parser = argparse.ArgumentParser(
        description="Task 2: recompile Subject.java, re-instrument with Soot, run test suites, generate Ochiai reports.",
        formatter_class=argparse.RawTextHelpFormatter
    )
    parser.add_argument(
        "--suite", action="append", metavar="NAME:DIR:CLASS",
        help=(
            "Add a test suite in the format  name:src_dir:test_class\n"
            "  name      = short label, e.g. randoop0\n"
            "  src_dir   = directory under src/test/, e.g. fault-revealing-randoop0\n"
            "  test_class = fully-qualified JUnit class name\n"
            "Can be specified multiple times. Omit to use the four default suites."
        )
    )
    return parser.parse_args()


def main():
    args = parse_args()

    if args.suite:
        suites = []
        for s in args.suite:
            parts = s.split(":", 2)
            if len(parts) != 3:
                print("ERROR: --suite must be  name:src_dir:test_class  (got: %s)" % s)
                sys.exit(1)
            suites.append(tuple(parts))
    else:
        suites = DEFAULT_SUITES

    print("Suites to run:")
    for name, d, cls in suites:
        print("  %-12s  %-45s  %s" % (name, d, cls))
    print()

    print("=" * 60)
    print("Step 1: Copy bin -> debug_bin, recompile Subject.java")
    print("=" * 60)
    if os.path.exists(BIN_DEBUG):
        shutil.rmtree(BIN_DEBUG)
    shutil.copytree(BIN_ORIG, BIN_DEBUG)

    subject_src = os.path.join(BASE, "src", "main", "java", "comp5111", "assignment", "cut", "Subject.java")
    ok = run(["javac", "-cp", BIN_DEBUG, "-d", BIN_DEBUG, subject_src], "compile")
    if not ok:
        sys.exit(1)
    print("  Compile OK")

    print()
    print("=" * 60)
    print("Step 2: Re-instrument -> debug_instrumented")
    print("=" * 60)
    if os.path.exists(INST_DEBUG):
        shutil.rmtree(INST_DEBUG)

    cp_soot = SEP.join([BIN_DEBUG,
                        os.path.join(LIB, "soot-4.2.1-jar-with-dependencies.jar"),
                        os.path.join(LIB, "junit-4.12.jar"),
                        os.path.join(LIB, "hamcrest-core-1.3.jar")])
    ok = run(["java", "-cp", cp_soot, "comp5111.assignment.SootInstrumenter", BIN_DEBUG, INST_DEBUG], "instrument")
    if not ok:
        sys.exit(1)

    src_cr = os.path.join(BIN_DEBUG, "comp5111", "assignment", "CoverageRuntime.class")
    dst_cr = os.path.join(INST_DEBUG, "comp5111", "assignment", "CoverageRuntime.class")
    os.makedirs(os.path.dirname(dst_cr), exist_ok=True)
    shutil.copy2(src_cr, dst_cr)
    print("  Instrument OK")

    print()
    print("=" * 60)
    print("Step 3: Run test suites")
    print("=" * 60)
    if os.path.exists(OUT_DEBUG):
        shutil.rmtree(OUT_DEBUG)
    os.makedirs(OUT_DEBUG)

    cp_base = SEP.join([INST_DEBUG, BIN_DEBUG,
                        os.path.join(LIB, "junit-4.12.jar"),
                        os.path.join(LIB, "hamcrest-core-1.3.jar"),
                        os.path.join(LIB, "evosuite-1.2.0.jar")])

    for suite_name, test_dir, test_class in suites:
        print("  --- %s ---" % suite_name)
        test_path = os.path.join(BASE, "src", "test", test_dir)
        cp = cp_base + SEP + test_path
        out_dir = os.path.join(OUT_DEBUG, suite_name)
        r = subprocess.run(
            ["java", "-cp", cp, "comp5111.assignment.SpectrumRunner", test_class, out_dir],
            capture_output=True, text=True
        )
        for line in r.stdout.strip().split("\n"):
            if line.strip():
                print("    " + line.strip())
        if r.returncode != 0:
            print("    ERROR (exit %d)" % r.returncode)
            if r.stderr:
                print("    " + r.stderr.strip()[-300:])

    print()
    print("=" * 60)
    print("Step 4: Generate Ochiai reports")
    print("=" * 60)
    cp_ranker = SEP.join([BIN_DEBUG,
                          os.path.join(LIB, "junit-4.12.jar"),
                          os.path.join(LIB, "hamcrest-core-1.3.jar")])
    registry = os.path.join(INST_DEBUG, "stmt_registry.tsv")

    for suite_name, _, _ in suites:
        data_dir = os.path.join(OUT_DEBUG, suite_name)
        out_tsv = os.path.join(OUT_DEBUG, "spectrum_fl_ochiai_%s.tsv" % suite_name)
        if not os.path.exists(os.path.join(data_dir, "test_results.tsv")):
            print("  %s: SKIPPED (no test results)" % suite_name)
            continue
        run(["java", "-cp", cp_ranker, "comp5111.assignment.OchiaiRanker", registry, data_dir, out_tsv], suite_name)
        print("  %s: OK -> %s" % (suite_name, out_tsv))

    print()
    print("=" * 60)
    print("Step 5: Compare (BEFORE vs AFTER)")
    print("=" * 60)
    print()
    print("  %-12s  %-18s  -->  %-18s" % ("Suite", "BEFORE", "AFTER"))
    print("  " + "-" * 55)
    for suite_name, _, _ in suites:
        before_path = os.path.join(BASE, "spectrum_output", suite_name, "test_results.tsv")
        after_path = os.path.join(OUT_DEBUG, suite_name, "test_results.tsv")
        bt, bf = count_fails(before_path)
        at, af = count_fails(after_path)
        before_str = "%d fails / %d" % (bf, bt) if bf >= 0 else "N/A"
        after_str = "%d fails / %d" % (af, at) if af >= 0 else "N/A"
        delta = ""
        if bf >= 0 and af >= 0:
            d = af - bf
            if d < 0:
                delta = "  (fixed %d)" % (-d)
            elif d == 0:
                delta = "  (no change)"
            else:
                delta = "  (regression +%d!)" % d
        print("  %-12s  %-18s  -->  %-18s%s" % (suite_name, before_str, after_str, delta))

    print()
    print("Done. Outputs in: %s" % OUT_DEBUG)


if __name__ == "__main__":
    main()
