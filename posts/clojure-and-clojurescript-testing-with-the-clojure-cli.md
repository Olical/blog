# Clojure and ClojureScript testing with the Clojure CLI

This post is sort of an extension of a previous post, [Clojure projects from scratch](/clojure-projects-from-scratch).
That will introduce you to structuring your project around a `deps.edn` file, here we’re going to simply add a couple of dependencies that allow you to run your tests.

In a [Leiningen](https://leiningen.org/) project, `lein test` will execute your Clojure tests, no questions asked.
In a Clojure CLI / `deps.edn` based project we have no such command, tests have to be executed by a custom built test runner script.

You probably don’t want to be writing and modifying a test runner namespace every time you add a test, that’s why [test-runner](https://github.com/cognitect-labs/test-runner) and [cljs-test-runner](https://github.com/Olical/cljs-test-runner) exist.
I’m the author of the latter, I hope that doesn’t put you off.

## test-runner

First we’ll add test-runner, the Clojure version.
This will give us an equivalent to `lein test`.
Add a `test` alias to your `aliases` section of your `deps.edn` file.

```clojure
{:deps ;; 1
 {org.clojure/clojure {:mvn/version "1.9.0"}
  org.clojure/clojurescript {:mvn/version "1.10.145"}}

 :aliases
 {:test ;; 2
  {:extra-paths ["test"] ;; 3
   :extra-deps
   {com.cognitect/test-runner {:git/url "git@github.com:cognitect-labs/test-runner"
                               :sha "76568540e7f40268ad2b646110f237a60295fa3c"}} ;; 4
   :main-opts ["-m" "cognitect.test-runner"]}}} ;; 5
```

Let’s break this down a little, just in case you aren’t super familiar with `deps.edn` just yet.

1. Map of your dependencies, here we’re depending on the latest Clojure and ClojureScript.
Having your language as a versioned dependency is a wonderful thing.
2. Our test alias, we’ll activate it with `clojure -Atest`.
3. For Clojure tests, we need to add the test directory to the classpath.
4. Dependency on the test-runner, there may be a new commit by now.
I’m waiting for my [return code fix](https://github.com/cognitect-labs/test-runner/pull/12) to be merged.
5. Set the entry namespace to the test runner.

We can now execute our tests as we would with `lein`!

```bash
$ clojure -Atest
```

At the time of writing, even if your tests fail, the return code of the process will always be 0.
This means that your CLI and CI will think the tests passed just fine.
I’ve fixed this and it may have been merged by the time you’re reading this.
If not, feel free to use the fixed commit from my fork.

```clojure
{:git/url "git@github.com:Olical/test-runner.git"
 :sha "7c4f5bd4987ec514889c7cd7e3d13f4ef95f256b"}
```

## cljs-test-runner

Running ClojureScript tests is usually a bit of a pain.
You’ve got to work out how to get it compiling then have that plugged into a test runner such as [doo](https://github.com/bensu/doo).
This doesn’t account for test file discovery, so you’ve still got to specify each of your test namespaces manually.

I’ve wrapped up doo with a few namespace tools and the ClojureScript compiler to give you a single command that handles all of this for you.
Let’s add another alias containing cljs-test-runner.
You may want to check for new versions since it may have changed since I wrote this.

```clojure
:test-cljs
{:extra-deps
 {olical/cljs-test-runner {:mvn/version "0.1.1"}}
 :main-opts ["-m" "cljs-test-runner.main"]}
```

As you can see it’s extremely similar to the Clojure test-runner setup, one difference is that we don’t need to add our test directory to the classpath.
ClojureScript doesn’t rely on the classpath, instead there is a flag that you can set if your tests are somewhere other than the default test directory.

You can execute your tests with the following command, it’ll run them in node by default.
You can change the environment to phantom if required, this may have changed since writing.

```bash
$ clojure -Atest-cljs
```

If everything went according to plan it should have found your `.cljs` and `.cljc` test namespaces, compiled them and executed them through node.
If it didn’t, I’m sure you can work it out, or raise an issue if you think there’s a problem.

## Running both

You’ll now be able to run your Clojure and ClojureScript tests side by side, hassle free, locally or through CI.

Bear in mind that the Clojure test-runner will always return a good exit status, even when the tests fail at the time of writing.
Make sure my fix has been merged or use my fork if you need the correct exit status.
