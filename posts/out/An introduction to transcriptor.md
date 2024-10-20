---
alias: an-introduction-to-transcriptor
tags:
- blog-post
- imported-blog-post
---


[Transcriptor](https://github.com/cognitect-labs/transcriptor) is a new [Clojure](https://clojure.org/) tool released by [Stuard Halloway](https://twitter.com/stuarthalloway) (who literally writes books on Clojure) that approaches testing from a different angle. I only noticed it after he tweeted the following and I asked for him to elaborate. Luckily enough, he did!

> status: replacing gratuitous test framework goo with information-rich [#clojure](https://twitter.com/hashtag/clojure?src=hash&ref_src=twsrc%5Etfw) ex-info
>
> — stuarthalloway (@stuarthalloway) [October 5, 2017](https://twitter.com/stuarthalloway/status/915902870737833984?ref_src=twsrc%5Etfw)

It allows you to take a REPL interaction and solidify it within a standalone file, this file can be executed later like a normal test suite. Unlike a test suite, you can check the value of any given line in that file against a [Clojure spec](https://clojure.org/about/spec), allowing you to interleave statements and specs.

I think this is a fantastic idea that will work great for some people but probably not everyone. The world is very hung up on TDD and code coverage, so some may shun this without a second thought. Here’s how Stuart outlines the problem in the README (very well put, may I add!):

> Testing frameworks often introduce their own abstractions for e.g. evaluation order, data validation, reporting, scope, code reuse, state, and lifecycle. In my experience, these abstractions are _always_ needlessly different from (and inferior to) related abstractions provided by the language itself.
>
> Adapting an already-working REPL interaction to satisfy such testing abstractions is a waste of time, and it throws away the intermediate REPL results that are valuable in diagnosing a problem.
>
> So transcriptor aims to do _less_, and impose the bare minimum of cognitive load needed to convert a REPL interaction into a test. The entire API is four functions:
>
> * `xr/run` runs a REPL script and produces a transcript
> * `check!` validates the last returned value against a Clojure spec
> * `xr/on-exit` lets you register cleanup code to run after `xr/run` completes
> * `xr/repl-files` finds the `.repl` files in a directory tree

The underline is my addition. I’ve recently come to admire software that solves a specific problem and nothing more with as little code as possible.

## Usage

Thinking about the possibilities is all well and good, but I’m going to show you a simple concrete usage, something I felt the README lacked (which is fine, it’s new!). I’m going to write a dice roller using the REPL to check the code as I go along. I’ll move these checks out into _.repl_ files, when I’m done I should have a working dice roller with an example based test suite all thanks to transcriptor.

I’m going into this knowing almost nothing about the problem and tooling involved, so I [recorded](https://www.youtube.com/watch?v=w8RdTodkxDo) myself (and [streamed](https://www.twitch.tv/olliemakesthings)) as I learned about and built this solution.

As you can see, there wasn’t much to it, you can find the repository for this video at [Olical/clj-dice-roller](https://github.com/Olical/clj-dice-roller). First I needed to add the following dependencies to my _project.clj:_

```
:dependencies [[org.clojure/clojure "1.9.0-beta1"]
               [org.clojure/spec.alpha "0.1.123"]
               [com.cognitect/transcriptor "0.1.5"]]
```

Then I wrote my actual dice rolling namespace like this:

```
(ns dice-roller.core)

(defn roll
  "Rolls some dice, like (roll 3 6) would be three d6."
  [amount die]
  (vec (map #(inc (rand-int %)) (filter pos? (repeat amount die)))))
```

I included a bunch of calls under my function to check various aspects, which is easier to convey in the video. I was experimenting with different inputs to see what may cause errors and what I should do when someone gives me bad inputs like negative numbers.

```
(roll 0 6) ;; []
(roll 1 6) ;; [3]
(roll 3 6) ;; [1 3 6]
(roll 1 0) ;; Error? Empty?
(roll -1 6) ;; Error?
(roll 3 -1) ;; Default to 0????
```

I eventually settled on always returning a vector but not rolling invalid dice, as you can see from the finished function above. I then moved these checks into a _.repl_ file with some spec checks.

```
(require '[cognitect.transcriptor :as xr]
         '[clojure.spec.alpha :as s]
         '[dice-roller.core :as dice])

(s/def ::d6 (s/and int? #(< 0 % 7)))

(dice/roll 0 6)
(xr/check! (s/and vector? empty?))

(dice/roll 1 6)
(xr/check! (s/tuple ::d6))

(dice/roll 3 6)
(xr/check! (s/tuple ::d6 ::d6 ::d6))

(dice/roll 1 0)
(xr/check! (s/and vector? empty?))

(dice/roll -1 6)
(xr/check! (s/and vector? empty?))

(dice/roll 3 -1)
(xr/check! (s/and vector? empty?))
```

I could run the _.repl_ file with _xr/run_ but I created a file called _repls/repl_runner.clj_ that could run all of my projects REPL files in one go.

```
(ns repl-runner
  (:require [cognitect.transcriptor :as xr]))

(defn -main []
  (doseq [repl-file (xr/repl-files "./repls")]
    (xr/run repl-file)))
```

I then added an alias to my _project.clj_ so I could run _lein repls_ to execute the scripts.

```
:profiles {:dev {:source-paths ["src" "repls"]}}
:aliases {"repls" ["run" "-m" "repl-runner"]}
```

You could run _lein repls_ (or maybe _lein transcriptions_ is a better name?) within your CI setup, either instead of or alongside your normal test suite. Here’s an example output from that command.

```
(comment {:transcript "./repls/rolls.repl", :namespace cognitect.transcriptor.t_1})
(require
 '[cognitect.transcriptor :as xr]
 '[clojure.spec.alpha :as s]
 '[dice-roller.core :as dice])
=> nil

(s/def
 :cognitect.transcriptor.t_1/d6
 (s/and int? (fn* [p1__240#] (< 0 p1__240# 7))))
=> :cognitect.transcriptor.t_1/d6

(dice/roll 0 6)
=> []

(xr/check! (s/and vector? empty?))
=> nil

(dice/roll 1 6)
=> [1]

(xr/check! (s/tuple :cognitect.transcriptor.t_1/d6))
=> nil

(dice/roll 3 6)
=> [2 6 6]

(xr/check!
 (s/tuple
  :cognitect.transcriptor.t_1/d6
  :cognitect.transcriptor.t_1/d6
  :cognitect.transcriptor.t_1/d6))
=> nil

(dice/roll 1 0)
=> []

(xr/check! (s/and vector? empty?))
=> nil

(dice/roll -1 6)
=> []

(xr/check! (s/and vector? empty?))
=> nil

(dice/roll 3 -1)
=> []

(xr/check! (s/and vector? empty?))
=> nil
```

And, finally, here’s what happens if I start returning seqs instead of vectors.

```
(comment {:transcript "./repls/rolls.repl", :namespace cognitect.transcriptor.t_1})
(require
 '[cognitect.transcriptor :as xr]
 '[clojure.spec.alpha :as s]
 '[dice-roller.core :as dice])
=> nil

(s/def
 :cognitect.transcriptor.t_1/d6
 (s/and int? (fn* [p1__240#] (< 0 p1__240# 7))))
=> :cognitect.transcriptor.t_1/d6

(dice/roll 0 6)
=> ()

(xr/check! (s/and vector? empty?))

Exception in thread "main" clojure.lang.ExceptionInfo: Transcript assertion failed! val: () fails predicate: vector?
:clojure.spec.alpha/spec  #object[clojure.spec.alpha$and_spec_impl$reify__875 0x765f05af "clojure.spec.alpha$and_spec_impl$reify__875@765f05af"]
:clojure.spec.alpha/value  ()
 #:clojure.spec.alpha{:problems [{:path [], :pred clojure.core/vector?, :val (), :via [], :in []}], :spec #object[clojure.spec.alpha$and_spec_impl$reify__875 0x765f05af "clojure.spec.alpha$and_spec_impl$reify__875@765f05af"], :value ()}, compiling:(/tmp/form-init165212537261342855.clj:1:72)
    at clojure.lang.Compiler.load(Compiler.java:7526)
    at clojure.lang.Compiler.loadFile(Compiler.java:7452)
    at clojure.main$load_script.invokeStatic(main.clj:278)
    at clojure.main$init_opt.invokeStatic(main.clj:280)
    at clojure.main$init_opt.invoke(main.clj:280)
    at clojure.main$initialize.invokeStatic(main.clj:311)
    at clojure.main$null_opt.invokeStatic(main.clj:345)
    at clojure.main$null_opt.invoke(main.clj:342)
    at clojure.main$main.invokeStatic(main.clj:424)
    at clojure.main$main.doInvoke(main.clj:387)
    at clojure.lang.RestFn.applyTo(RestFn.java:137)
    at clojure.lang.Var.applyTo(Var.java:702)
    at clojure.main.main(main.java:37)
Caused by: clojure.lang.ExceptionInfo: Transcript assertion failed! val: () fails predicate: vector?
:clojure.spec.alpha/spec  #object[clojure.spec.alpha$and_spec_impl$reify__875 0x765f05af "clojure.spec.alpha$and_spec_impl$reify__875@765f05af"]
:clojure.spec.alpha/value  ()
 {:clojure.spec.alpha/problems [{:path [], :pred clojure.core/vector?, :val (), :via [], :in []}], :clojure.spec.alpha/spec #object[clojure.spec.alpha$and_spec_impl$reify__875 0x765f05af "clojure.spec.alpha$and_spec_impl$reify__875@765f05af"], :clojure.spec.alpha/value ()}
    at clojure.core$ex_info.invokeStatic(core.clj:4744)
    at clojure.core$ex_info.invoke(core.clj:4744)
    at cognitect.transcriptor.t_1$eval245.invokeStatic(./repls/rolls.repl:8)
    at cognitect.transcriptor.t_1$eval245.invoke(./repls/rolls.repl:8)
    at clojure.lang.Compiler.eval(Compiler.java:7062)
    at clojure.lang.Compiler.eval(Compiler.java:7025)
    at clojure.core$eval.invokeStatic(core.clj:3211)
    at clojure.core$eval.invoke(core.clj:3207)
    at cognitect.transcriptor$repl$read_eval_print__189$fn__192.invoke(transcriptor.clj:58)
    at cognitect.transcriptor$repl$read_eval_print__189.invoke(transcriptor.clj:58)
    at cognitect.transcriptor$repl.invokeStatic(transcriptor.clj:67)
    at cognitect.transcriptor$repl.invoke(transcriptor.clj:35)
    at cognitect.transcriptor$repl_on.invokeStatic(transcriptor.clj:78)
    at cognitect.transcriptor$repl_on.invoke(transcriptor.clj:74)
    at cognitect.transcriptor$run.invokeStatic(transcriptor.clj:90)
    at cognitect.transcriptor$run.invoke(transcriptor.clj:82)
    at repl_runner$_main.invokeStatic(repl_runner.clj:6)
    at repl_runner$_main.invoke(repl_runner.clj:4)
    at clojure.lang.Var.invoke(Var.java:377)
    at user$eval149.invokeStatic(form-init165212537261342855.clj:1)
    at user$eval149.invoke(form-init165212537261342855.clj:1)
    at clojure.lang.Compiler.eval(Compiler.java:7062)
    at clojure.lang.Compiler.eval(Compiler.java:7052)
    at clojure.lang.Compiler.load(Compiler.java:7514)
    ... 12 more
```

It’s a wall of text, but a useful one!

## Thoughts

All in all, I really like the approach. It’s easy to use and should be usable with any problem, you’ll just have to be wary of setup and teardown in stateful applications. I’m not sure on the best practice way of using it, but I think this is a good basic start, a canonical lein plugin would be pretty neat though.

It would be nice to combine this with one of the projects that aims to make spec output more human friendly too, maybe the lein plugin could do that for you automatically.

I feel like the REPL files are simple enough that you could throw them away and start again easily if you wanted to change the thing you’re testing a lot. Something I feel strict TDD severely impairs. When I have deeply nested TDD code over hundreds of lines, a tiny change to the source will break everything. TDD taken to the extreme makes your project become allergic to change, obviously too much of anything is harmful though.

I’d recommend that you give this a whirl on one of your own projects and see what you get. You may find it suits you perfectly.

Thank you very much, Stuart, for releasing this into the world. It’s pretty cool.

## Edit

I went ahead and created [lein-transcriptor](https://github.com/Olical/lein-transcriptor), the dice roller repository used in this post has been updated to use that instead.
