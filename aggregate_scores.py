import csv
import sys
import os

REPORTS = [
    ("R0", "spectrum_fl_ochiai_randoop0.tsv"),
    ("R1", "spectrum_fl_ochiai_randoop1.tsv"),
    ("E0", "spectrum_fl_ochiai_evosuite0.tsv"),
    ("E1", "spectrum_fl_ochiai_evosuite1.tsv"),
]

def main():
    base_dir = os.path.dirname(os.path.abspath(__file__))
    out_path = os.path.join(base_dir, "aggregated_suspicion.tsv")

    line_scores = {}

    for idx, (tag, fname) in enumerate(REPORTS):
        fpath = os.path.join(base_dir, fname)
        if not os.path.exists(fpath):
            print("WARNING: %s not found, skipping" % fpath)
            continue
        with open(fpath, "r", encoding="utf-8") as fh:
            reader = csv.reader(fh, delimiter="\t")
            next(reader)
            for row in reader:
                if len(row) < 5:
                    continue
                method_sig = row[0]
                jimple = row[1]
                score = float(row[2])
                ranking = row[3]
                srcline = row[4]

                if srcline not in line_scores:
                    line_scores[srcline] = {
                        "scores": [0.0, 0.0, 0.0, 0.0],
                        "ranks": ["", "", "", ""],
                        "methods": set(),
                        "jimple_samples": set(),
                    }
                entry = line_scores[srcline]
                if score > entry["scores"][idx]:
                    entry["scores"][idx] = score
                    entry["ranks"][idx] = ranking
                short_method = method_sig.split(":")[1].split("(")[0].strip()
                entry["methods"].add(short_method)
                if len(entry["jimple_samples"]) < 3:
                    entry["jimple_samples"].add(jimple[:80])

    results = []
    for line, data in line_scores.items():
        s = data["scores"]
        sum_s = sum(s)
        max_s = max(s)
        cnt = sum(1 for x in s if x > 0)
        methods = ", ".join(sorted(data["methods"]))
        samples = " | ".join(sorted(data["jimple_samples"]))
        results.append({
            "line": line,
            "sum": sum_s,
            "max": max_s,
            "num_suites": cnt,
            "R0_score": s[0],
            "R1_score": s[1],
            "E0_score": s[2],
            "E1_score": s[3],
            "R0_rank": data["ranks"][0],
            "R1_rank": data["ranks"][1],
            "E0_rank": data["ranks"][2],
            "E1_rank": data["ranks"][3],
            "methods": methods,
            "jimple_samples": samples,
        })

    results.sort(key=lambda x: (-x["sum"], -x["max"]))

    for i, r in enumerate(results):
        r["agg_rank"] = i + 1

    headers = [
        "agg_rank", "source_line", "sum_score", "max_score", "num_suites",
        "R0_score", "R0_rank", "R1_score", "R1_rank",
        "E0_score", "E0_rank", "E1_score", "E1_rank",
        "methods", "jimple_samples",
    ]

    with open(out_path, "w", encoding="utf-8", newline="") as fh:
        writer = csv.writer(fh, delimiter="\t")
        writer.writerow(headers)
        for r in results:
            writer.writerow([
                r["agg_rank"],
                r["line"],
                "%.10f" % r["sum"],
                "%.10f" % r["max"],
                r["num_suites"],
                "%.10f" % r["R0_score"],
                r["R0_rank"],
                "%.10f" % r["R1_score"],
                r["R1_rank"],
                "%.10f" % r["E0_score"],
                r["E0_rank"],
                "%.10f" % r["E1_score"],
                r["E1_rank"],
                r["methods"],
                r["jimple_samples"],
            ])

    print("Written %d lines to %s" % (len(results), out_path))
    print("\nTop 30:")
    print("%4s %6s %8s %8s %2s %8s %8s %8s %8s  %s" % (
        "#", "Line", "Sum", "Max", "#S", "R0", "R1", "E0", "E1", "Method"))
    print("-" * 100)
    for r in results[:30]:
        print("%4d %6s %8.4f %8.4f %2d %8.4f %8.4f %8.4f %8.4f  %s" % (
            r["agg_rank"], r["line"], r["sum"], r["max"], r["num_suites"],
            r["R0_score"], r["R1_score"], r["E0_score"], r["E1_score"],
            r["methods"]))


if __name__ == "__main__":
    main()
