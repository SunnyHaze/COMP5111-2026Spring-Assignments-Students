# Scripts

> All commands below should be run from the **project root**, not from `scripts/`.

## task2_run_spectrum.py

Recompiles `Subject.java`, re-instruments with Soot, runs test suites, generates Ochiai reports.
Outputs go to `debug_bin/`, `debug_instrumented/`, `debug_spectrum/` — original `bin/`, `instrumented-bin/`, `spectrum_output/` are never touched.

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
- `src_dir` — directory under `src/test/`
- `test_class` — fully-qualified JUnit 4 class name

Outputs per suite: `debug_spectrum/<name>/test_results.tsv`, `coverage_matrix.tsv`, `summary.txt`,
and `debug_spectrum/spectrum_fl_ochiai_<name>.tsv`.

---

## task1_evosuite_experiments.py

Batch EvoSuite test generation experiments for Task 1. Varies search budget and algorithm.

```
python scripts\task1_evosuite_experiments.py
```
