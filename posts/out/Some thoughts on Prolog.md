---
alias: some-thoughts-on-prolog
tags:
- blog-post
- imported-blog-post
---


I usually write some notes as I read and work then pad those notes out into a full post after I’m done with the chapter. This time I’m just going to publish it as is (pretty much).

## Initial thoughts

Very concise. Beautiful, after you spend hours getting it working. Reminds me of my experience with Haskell (which isn’t much).

Obviously not something you’d want to use for every problem, but as the anecdote in the book illustrates, it’s great for working out schedules for example.

I could see myself defining a recipe with it and then getting the optimal way to cook it with how long I think it’ll take me to perform each step.

## The exercises

Found it difficult to get a solution that worked but was also tail recursive. Also struggled with working out why I would call my function and Prolog kept giving me the same result until I hit enter. So semi-colon would continually yield 10 for example (which was correct) but wouldn’t just execute and return the single value.

Reminded me of wiring chips together in [HDL](http://en.wikipedia.org/wiki/Hardware_description_language) for the [nand2tetris](https://www.coursera.org/course/nand2tetris1) course at some points.

I realised after I’d written my own _min_list_ implementation that all it wanted me to do was use _min_list_. Oops. Could have saved some time there.

The sudoku and 8 queens solvers on day 3 are beautiful. It feels I’m comparing a normal TDD tool to [quickcheck](https://hackage.haskell.org/package/QuickCheck). The former has you defining every step whilst the latter has you set the rules and it’ll figure out the rest.

## Closing thoughts

Sure this “program is just rules” idea only applies well to certain problems (sudoku and testing for example) but that doesn’t mean we should ignore these tools because they’re not completely general. I could see myself falling back to Prolog (or a logic library in my host language at least) for relevant problems in the future. There’s a time and a place basically, I’m just glad I know to look out for those opportunities now.

I think [miniKanren](http://minikanren.org/) (which I’ll be looking at soon) may spur me on to get logic programming into my day to day work for the right kinds of problems. I’d certainly prefer that to the JavaScript “model” monstrosities that we so frequently rely on as JavaScript developers. I was surprised to find that core.logic (from Clojure) is based on miniKanren, so that’s looking more and more interesting.
