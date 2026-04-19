# single test runner
java  -cp "bin;lib/junit-4.12.jar;lib/hamcrest-core-1.3.jar" comp5111.assignment.SingleTestRunner comp5111.assignment.cut.Subject_randoop111_RegressionTest0 test039

# SootInstrumenter
java -cp "bin;lib/soot-4.2.1-jar-with-dependencies.jar" comp5111.assignment.SootInstrumenter



● That already ran with python and produced results! Here's the summary of the
   most suspicious source lines, ranked by sum of Ochiai scores across all 4  
  suites:

  ┌──────┬─────────┬──────┬────────────────────────┬───────────────────────┐  
  │ Rank │  Line   │ Sum  │         Method         │       Suspect?        │  
  ├──────┼─────────┼──────┼────────────────────────┼───────────────────────┤  
  │ 1    │ 205     │ 2.38 │ parseToken             │ pos++ before break —  │  
  │      │         │      │                        │ extra increment       │  
  ├──────┼─────────┼──────┼────────────────────────┼───────────────────────┤  
  │      │         │      │                        │ inside parseToken's   │  
  │ 2    │ 183-184 │ 2.38 │ isOneOf                │ hot path, likely      │  
  │      │         │      │                        │ innocent              │  
  ├──────┼─────────┼──────┼────────────────────────┼───────────────────────┤  
  │      │         │      │                        │ num = num*10 +        │  
  │ 3    │ 346     │ 2.09 │ extractIntInStr        │ (ch-'0') — check      │  
  │      │         │      │                        │ logic                 │  
  ├──────┼─────────┼──────┼────────────────────────┼───────────────────────┤  
  │ 4    │ 341     │ 2.09 │ extractIntInStr        │ loop i++ — check      │  
  │      │         │      │                        │ bounds                │  
  ├──────┼─────────┼──────┼────────────────────────┼───────────────────────┤  
  │      │         │      │                        │ calls                 │  
  │ 5    │ 697     │ 1.63 │ daysBetweenDateStrings │ daysBetweenDates —    │  
  │      │         │      │                        │ fault likely          │  
  │      │         │      │                        │ downstream            │  
  ├──────┼─────────┼──────┼────────────────────────┼───────────────────────┤  
  │      │         │      │                        │ parsing code — likely │  
  │ 6    │ 692-696 │ 1.58 │ daysBetweenDateStrings │  innocent, fault in   │  
  │      │         │      │                        │ callee                │  
  ├──────┼─────────┼──────┼────────────────────────┼───────────────────────┤  
  │      │         │      │                        │ calls extractIntInStr │  
  │ 7    │ 375,382 │ 1.72 │ getVersionNo           │  — inherited          │  
  │      │         │      │                        │ suspicion             │  
  ├──────┼─────────┼──────┼────────────────────────┼───────────────────────┤  
  │ 8    │ 442-445 │ 0.72 │ daysBetweenDates       │ lower score, but      │  
  │      │         │      │                        │ check this method     │  
  ├──────┼─────────┼──────┼────────────────────────┼───────────────────────┤  
  │ 9    │ 624     │ 0.82 │ monAbbr2month          │ only in 3 suites      │  
  ├──────┼─────────┼──────┼────────────────────────┼───────────────────────┤  
  │      │         │      │                        │ day < daysInMonth     │  
  │ 10   │ 488     │ —    │ checkValidDate         │ (confirmed < should   │  
  │      │         │      │                        │ be <=)                │  
  └──────┴─────────┴──────┴────────────────────────┴───────────────────────┘  

  Already confirmed faults:
  - Line 205: pos++ before break in parseToken — includes terminator in result
  - Line 488: day < daysInMonth should be day <= daysInMonth in checkValidDate
  - Line 63: return i + 2 should be return i + 1 in decodeOctets

  Note line 488 doesn't appear in the top list because checkValidDate is      
  called indirectly via daysBetweenDates — the suspicion "spreads" to the     
  callers. Lines 442-445 and 697 are suspicious because they pass through     
  checkValidDate.

  The lines to manually check are: 205, 346, 488, 63, 624, 442-445 and any    
  other methods with moderate scores. Want me to print the source for any of  
  those?