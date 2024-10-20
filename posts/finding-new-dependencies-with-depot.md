---
tags:
  - blog-post
  - imported-blog-post
---
# Finding new dependencies with Depot

If you’re using lein you can use lein-ancient to find newer versions of your dependencies, if you’re using boot you probably have something similar.
With the Clojure CLI however you have to keep track of these things manually.

I’ve created a little tool called [Depot](https://github.com/Olical/depot) that aims to give you this same new version detection for your `deps.edn` file.
The README is probably enough to get you going but here’s a little example anyway.

## Usage

You’ll want to add Depot to your `deps.edn` file, you can do this at the project level or within your user-wide file at `~/.clojure/deps.edn`.
I’d recommend you add it at a project level however since this’ll allow anyone working on your codebase to use the same tooling, especially if it’s wrapped up in a `Makefile`.

```clojure
{:deps {org.clojure/clojure {:mvn/version "1.9.0"}
        org.clojure/clojurescript {:mvn/version "1.10.238"}}
 :aliases {:outdated {:extra-deps {olical/depot {:mvn/version "1.0.1"}}
                      :main-opts ["-m" "depot.outdated.main"]}}}
```

We can now execute Depot by referring to our alias:

```bash
$ clojure -Aoutdated
```

At the time of writing this doesn’t output anything since everything’s up to date.
If we were to depend on, say, ClojureScript 1.9.946 however:

```bash
$ clojure -Aoutdated
org.clojure/clojurescript: 1.9.946 => 1.10.238
```

If we’re also depending on Depot 1.0.0, as opposed to the currently released 1.0.1, we could even have it check it’s own alias:

```bash
$ clojure -Aoutdated --aliases outdated
org.clojure/clojurescript: 1.9.946 => 1.10.238
olical/depot: 1.0.0 => 1.0.1
```

We can also ask it to consider non-release versions such as snapshots and qualified versions such as alphas.
This will pick the very latest version, regardless of stability:

```bash
$ clojure -Aoutdated --aliases outdated --consider-types qualified,release,snapshot
org.clojure/clojure: 1.9.0 => 1.10.0-alpha4
org.clojure/clojurescript: 1.9.946 => 1.10.238
olical/depot: 1.0.0 => 1.0.1
```

I hope you find Depot useful in keeping your dependencies up to date.
