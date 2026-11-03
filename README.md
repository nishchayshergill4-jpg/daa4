# DAA Assignment 4 — Dynamic Programming and String Algorithms

**Student:** Nishchay Shergill  
**Group:** `[GROUP NAME / NUMBER]`  
**Runtime:** Java 17, Maven

## Contents

- Recursive, full-table, and two-row Levenshtein edit distance.
- Naive and Knuth–Morris–Pratt (KMP) exact string search. KMP's public `lps` method returns the prefix/LPS array, and both algorithms report overlapping matches.
- Algorithm-level counters, deterministic benchmark, CSV output, four generated PNG figures, report, and JUnit 5 tests.

## Build and test

From this directory, run:

```sh
mvn clean test
```

Java 17 and Maven 3.9 or later are required. Maven downloads JUnit 5 and build plugins on the first run if they are not cached.

## Run the experiment

```sh
mvn -q -DskipTests compile exec:java
```

This writes `results.csv` and four plots under `plots/`. Optional arguments to the main class specify the CSV path and plot directory:

```sh
mvn -q -DskipTests compile exec:java -Dexec.args="results.csv plots"
```

For a standalone run, the plot generator needs Python 3 with Matplotlib installed. The experiment uses fixed seed `20260404` for edit-distance inputs and `20260405` for string-search inputs. Recursive edit distance is limited to input lengths 2–12 to keep the exponential baseline practical.

## Counter definitions

- Recursive edit distance: number of recursive calls, including base cases.
- Full-table and two-row edit distance: number of interior DP cells computed; initialization is excluded. `allocated_cells` reports the total integer slots in the table or two row arrays, including boundary slots.
- Naive and KMP search: character equality comparisons between text and pattern. LPS preprocessing comparisons are excluded, as the counter describes the text-search phase.
- Elapsed time is a single measured invocation per generated case and is reported as an observation, not a stable performance guarantee.

## AI-use acknowledgement

AI assistance was used to draft and organize parts of this project, including implementation, tests, experiment tooling, and report text. The student should review the code, rerun the experiment, understand each algorithm and counter, and follow the course's disclosure rules before submission.
