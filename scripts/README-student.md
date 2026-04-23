# Scripts

> All commands below should be run from the **project root**, not from `scripts/`.

## task2_run_spectrum.py

Recompiles `Subject.java`, re-instruments with Soot, runs test suites, generates Ochiai reports.

The pipeline involves multiple Java processes (Soot instrumentation, test execution, ranking) that
need separate JVM invocations with different classpaths, so Python is used as the orchestrator.

**Default (four provided fault-revealing suites):**
```
python scripts\task2_run_spectrum.py
```

**Custom suites** — use `--suite name:src_dir:test_class`, repeatable:
```
python scripts\task2_run_spectrum.py ^
  --suite randoop0:fault-revealing-randoop0:comp5111.assignment.cut.Subject_randoop111_RegressionTest0 ^
  --suite evosuite0:fault-revealing-evosuite0:comp5111.assignment.cut.Subject_evosuite65_ESTest
```
- `name` — short label used in output file names
- `src_dir` — directory under `src/test/` containing the test `.java` file
- `test_class` — fully-qualified JUnit 4 class name

**Output locations** (all relative to project root):

| Step | Output path |
|------|-------------|
| Recompiled classes | `debug_bin/` |
| Instrumented CUT | `debug_instrumented/` |
| Statement registry | `debug_instrumented/stmt_registry.tsv` |
| Per-suite test results | `debug_spectrum/<name>/test_results.tsv` |
| Per-suite coverage matrix | `debug_spectrum/<name>/coverage_matrix.tsv` |
| Per-suite summary | `debug_spectrum/<name>/summary.txt` |
| Spectrum report | `debug_spectrum/spectrum_fl_ochiai_<name>.tsv` |

Original `bin/`, `instrumented-bin/`, `spectrum_output/` are never touched.

---

## task1_evosuite_experiments.py

Batch EvoSuite test generation experiments for Task 1. Varies search budget and algorithm.

```
python scripts\task1_evosuite_experiments.py
```
