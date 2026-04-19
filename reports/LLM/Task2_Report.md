# COMP5111 Assignment 2 - Task 2 Report

## Brief information
- Name: Xiaochen Ma
- ID: 21309693
- LLM used: GPT-5.4 (thinking), Claude Opus 4.

My main usage pattern was: take a suspicious method from the spectrum report, paste it together with its Javadoc spec and a few failing test cases, then ask the LLM to explain what the method is doing and why those tests fail. The spectrum scores tend to highlight an entire region rather than a single line — all statements inside a faulty method typically receive similar scores — so it was not possible to narrow down to one line from the spectrum alone. Feeding the whole function body to the LLM was necessary to get a semantic interpretation of where exactly the logic goes wrong. This was especially useful for methods with abstract or non-obvious implementations.

For `parseToken`, GPT-5.4 quickly pointed out that the method is supposed to stop before the terminator, not after it. It identified `pos++` inside the terminator branch as the fault because that shifts pos past the terminator before the substring is taken. This would have taken me longer to see manually since `isOneOf` looked suspicious at first and was drawing attention away.

For `monAbbr2month`, the method uses integer hashes built from character bit shifts, which is hard to read without computing manually. GPT-5.4 decoded each hash constant and found that the value for `"Sep"` was off by 10 (5465466 vs 5465456), which made the bug immediately obvious.

For `checkValidDate`, GPT-5.4 traced the high-scoring region in `daysBetweenDateStrings` back to this helper and identified the off-by-one in the boundary check (`<` vs `<=`).

For `extractIntInStr`, GPT-5.4 understood that the spec requires returning the last digit sequence, and correctly identified the early-return as the problem area. However, it kept suggesting multi-line rewrites (reversing the loop, using a different accumulation strategy). I also could not come up with a one-line patch myself. Eventually Claude Opus proposed changing `return num;` to `num = 0;`, which resets the accumulator instead of exiting, satisfying both the spec and the one-line constraint.

Overall the LLMs were useful for quickly understanding what a method is supposed to do, especially when the implementation is abstract. They were less reliable for discovering minimal patches under constraints like "change exactly one line," where Claude Opus performed better than GPT-5.4 in at least one case. The spectrum reports were necessary to identify which methods to focus on before applying the LLM's insights.
