import subprocess
import shutil
import os
import csv
import re
import time

BASE_DIR = r"D:\workspace\COMP5111-2026Spring-Assignments-Students-ass2"
JAR = os.path.join(BASE_DIR, "lib", "evosuite-1.2.0.jar")
CLASS = "comp5111.assignment.cut.Subject"
CP = "bin"
RESULTS_DIR = os.path.join(BASE_DIR, "experiment_results")
EVOSUITE_TESTS = os.path.join(BASE_DIR, "evosuite-tests")
EVOSUITE_REPORT = os.path.join(BASE_DIR, "evosuite-report")

os.makedirs(RESULTS_DIR, exist_ok=True)

# Exp 8 already saved: MOSA budget=180 pop=150 -> Line 91%, Branch 81%, Overall 92%
# Now explore more configurations
EXPERIMENTS = [
    {"id": 9,  "desc": "DynaMOSA budget=180",
     "args": "-Dsearch_budget=180"},
    {"id": 10, "desc": "DynaMOSA budget=240",
     "args": "-Dsearch_budget=240"},
    {"id": 11, "desc": "DynaMOSA budget=300",
     "args": "-Dsearch_budget=300"},
    {"id": 12, "desc": "MOSA budget=240 pop=150",
     "args": "-Dsearch_budget=240 -Dalgorithm=MOSA -Dpopulation=150"},
    {"id": 13, "desc": "MOSA budget=300 pop=200",
     "args": "-Dsearch_budget=300 -Dalgorithm=MOSA -Dpopulation=200"},
    {"id": 14, "desc": "DynaMOSA budget=180 pop=150",
     "args": "-Dsearch_budget=180 -Dpopulation=150"},
    {"id": 15, "desc": "DynaMOSA budget=240 pop=200",
     "args": "-Dsearch_budget=240 -Dpopulation=200"},
    {"id": 16, "desc": "MOSA budget=180 pop=200",
     "args": "-Dsearch_budget=180 -Dalgorithm=MOSA -Dpopulation=200"},
]


def parse_coverage_output(text):
    """Parse EvoSuite stdout to extract per-criterion coverage."""
    result = {}
    for m in re.finditer(r"Coverage of criterion (\w+): (\d+)%", text):
        result[m.group(1)] = int(m.group(2))
    m = re.search(r"Resulting test suite's coverage: (\d+)%", text)
    if m:
        result["OVERALL"] = int(m.group(1))
    m = re.search(r"Generated (\d+) tests", text)
    if m:
        result["TESTS"] = int(m.group(1))
    m = re.search(r"mutation score: (\d+)%", text)
    if m:
        result["MUTATION"] = int(m.group(1))
    return result


def run_experiment(exp):
    exp_id = exp["id"]
    desc = exp["desc"]
    args_str = exp["args"]
    exp_dir = os.path.join(RESULTS_DIR, f"exp_{exp_id}")
    os.makedirs(exp_dir, exist_ok=True)

    cmd_str = f'java -jar ".\\lib\\evosuite-1.2.0.jar" -class "{CLASS}" -projectCP {CP} {args_str}'
    cmd_list = ["java", "-jar", JAR, "-class", CLASS, "-projectCP", CP] + args_str.split()

    print(f"\n{'='*70}")
    print(f"  Experiment {exp_id}: {desc}")
    print(f"  CMD: {cmd_str}")
    print(f"{'='*70}")

    # Clean stats before run
    stats_path = os.path.join(EVOSUITE_REPORT, "statistics.csv")
    if os.path.exists(stats_path):
        os.remove(stats_path)

    output = ""
    try:
        proc = subprocess.run(
            cmd_list, cwd=BASE_DIR, timeout=600,
            capture_output=True, text=True, encoding="utf-8", errors="replace"
        )
        output = proc.stdout + proc.stderr
        # Save raw output
        with open(os.path.join(exp_dir, "output.txt"), "w", encoding="utf-8") as f:
            f.write(output)
    except subprocess.TimeoutExpired:
        print(f"  [WARN] Timed out after 600s")
    except Exception as e:
        print(f"  [ERROR] {e}")

    # Copy results
    if os.path.exists(stats_path):
        shutil.copy2(stats_path, os.path.join(exp_dir, "statistics.csv"))
    if os.path.exists(EVOSUITE_TESTS):
        dest = os.path.join(exp_dir, "evosuite-tests")
        if os.path.exists(dest):
            shutil.rmtree(dest)
        shutil.copytree(EVOSUITE_TESTS, dest)

    cov = parse_coverage_output(output)
    success = "Done!" in output

    print(f"  Success: {success}")
    if cov:
        print(f"  LINE={cov.get('LINE','-')}% BRANCH={cov.get('BRANCH','-')}% "
              f"CBRANCH={cov.get('CBRANCH','-')}% WEAKMUTATION={cov.get('WEAKMUTATION','-')}% "
              f"OVERALL={cov.get('OVERALL','-')}% TESTS={cov.get('TESTS','-')}")

    # Save command
    with open(os.path.join(exp_dir, "command.txt"), "w") as f:
        f.write(cmd_str + "\n")

    return {"id": exp_id, "desc": desc, "cmd": cmd_str, "cov": cov, "success": success}


def main():
    # Include exp_8 in ranking
    all_results = [
        {"id": 8, "desc": "MOSA budget=180 pop=150",
         "cmd": 'java -jar ".\\lib\\evosuite-1.2.0.jar" -class "comp5111.assignment.cut.Subject" -projectCP bin -Dsearch_budget=180 -Dalgorithm=MOSA -Dpopulation=150',
         "cov": {"LINE": 91, "BRANCH": 81, "CBRANCH": 81, "WEAKMUTATION": 85,
                 "METHOD": 100, "METHODNOEXCEPTION": 98, "OVERALL": 92, "TESTS": 225, "MUTATION": 55},
         "success": True}
    ]

    for exp in EXPERIMENTS:
        r = run_experiment(exp)
        all_results.append(r)

    # Rank by: LINE desc, then BRANCH desc, then OVERALL desc
    all_results.sort(
        key=lambda x: (x["cov"].get("LINE", 0), x["cov"].get("BRANCH", 0), x["cov"].get("OVERALL", 0)),
        reverse=True
    )

    print(f"\n{'='*70}")
    print("  ALL EXPERIMENTS RANKED (by Line > Branch > Overall)")
    print(f"{'='*70}")
    print(f"  {'#':<3} {'Exp':<6} {'Line':>5} {'Branch':>7} {'CBranch':>8} {'WMut':>5} {'Overall':>8} {'Tests':>6} {'Desc'}")
    print(f"  {'-'*3} {'-'*6} {'-'*5} {'-'*7} {'-'*8} {'-'*5} {'-'*8} {'-'*6} {'-'*30}")
    for rank, r in enumerate(all_results, 1):
        c = r["cov"]
        mark = " <<<" if rank <= 3 else ""
        print(f"  {rank:<3} exp_{r['id']:<2} {c.get('LINE','-'):>4}% {c.get('BRANCH','-'):>5}%  {c.get('CBRANCH','-'):>5}%  {c.get('WEAKMUTATION','-'):>4}% {c.get('OVERALL','-'):>6}%  {c.get('TESTS','-'):>5} {r['desc']}{mark}")

    # Save top 3
    print(f"\n{'='*70}")
    print("  TOP 3 SAVED")
    print(f"{'='*70}")
    for i, r in enumerate(all_results[:3]):
        src = os.path.join(RESULTS_DIR, f"exp_{r['id']}", "evosuite-tests")
        dst = os.path.join(RESULTS_DIR, f"best_{i+1}")
        if os.path.exists(dst):
            shutil.rmtree(dst)
        if os.path.exists(src):
            shutil.copytree(src, dst)
        print(f"  best_{i+1}: exp_{r['id']} | Line={r['cov'].get('LINE','-')}% Branch={r['cov'].get('BRANCH','-')}% Overall={r['cov'].get('OVERALL','-')}%")
        print(f"    CMD: {r['cmd']}")

    # Write summary CSV
    summary_path = os.path.join(RESULTS_DIR, "summary.csv")
    with open(summary_path, "w", newline="") as f:
        writer = csv.writer(f)
        writer.writerow(["rank", "exp_id", "line%", "branch%", "cbranch%", "weakmut%", "overall%", "tests", "mutation%", "command", "description"])
        for rank, r in enumerate(all_results, 1):
            c = r["cov"]
            writer.writerow([rank, r["id"], c.get("LINE",""), c.get("BRANCH",""), c.get("CBRANCH",""),
                             c.get("WEAKMUTATION",""), c.get("OVERALL",""), c.get("TESTS",""),
                             c.get("MUTATION",""), r["cmd"], r["desc"]])
    print(f"\n  Summary: {summary_path}")


if __name__ == "__main__":
    main()
