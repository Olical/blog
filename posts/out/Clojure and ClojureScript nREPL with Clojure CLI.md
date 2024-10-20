---
alias: clojure-and-clojurescript-nrepl-with-clojure-cli
tags:
- blog-post
- imported-blog-post
---


If you’ve decided to try out the new Clojure CLI introduced with Clojure 1.9 you may have found yourself slightly lost when it comes to getting your CIDER (or other development environment) hooked up to your project through nREPL.
My previous post, [Clojure projects from scratch](/clojure-projects-from-scratch), may help you with understanding these concepts and tools if you’re struggling to get going.

In this post I’m going to show you a few simple steps you need to take to get your nREPL running smoothly.
I am hosting the ClojureScript nREPL through NodeJS, you can probably adjust the configuration if you need it to run in something else.

## The deps

We’re going to be using the following tools:

* [clojure/tools.nrepl](https://github.com/clojure/tools.nrepl)
* [clojure-emacs/cider-nrepl](https://github.com/clojure-emacs/cider-nrepl)
* [cemerick/piggieback](https://github.com/cemerick/piggieback)

Here’s the `deps.edn` file we’re going to want.
This is the minimal required amount, so you’ll need to integrate all of this into your own `deps.edn`.
The Clojure and ClojreScript versions may vary.

```clojure
{:deps
 {org.clojure/clojure {:mvn/version "1.9.0"}
  org.clojure/clojurescript {:mvn/version "1.10.64"}}

 :aliases
 {:nrepl-server
  {:extra-paths ["dev"]
   :extra-deps
   {com.cemerick/piggieback {:mvn/version "0.2.2"}
    org.clojure/tools.nrepl {:mvn/version "0.2.12"}
    cider/cider-nrepl {:mvn/version "0.17.0-SNAPSHOT"}}
   :main-opts ["-m" "nrepl-server"]}}}
```

You may want to add a small Makefile to make executing with this alias a little smoother:

```Makefile
.PHONY: nrepl-server

nrepl-server:
	clj -Anrepl-server
```

## The namespaces

You may have noticed my reference to the `nrepl-server` namespace and `dev` directory in the `deps.edn` file, we need to create these before our nREPL server will work.

Add the following files to your project:

### dev/nrepl_server.clj

```clojure
(ns nrepl-server
  (:require [cider-nrepl.main :as nrepl]))

(defn -main []
  (nrepl/init ["cider.nrepl/cider-middleware"
               "cemerick.piggieback/wrap-cljs-repl"]))
```

### dev/user.clj

```clojure
(ns user
  (:require [cemerick.piggieback :as piggieback]
            [cljs.repl.node :as node-repl]))

(defn cljs-repl []
  (piggieback/cljs-repl (node-repl/repl-env)))
```

These files will allow us to firstly boot our nREPL server with the correct middleware and secondly allow us to easily drop into a ClojureScript REPL.

## Getting connected

If you added a rule to your `Makefile`, you can use `make nrepl-server`, alternatively just type `clj -Anrepl-server`.
You should see something like this:

```bash
$ make nrepl-server
clj -Anrepl-server
nREPL server started on port 40047 on host localhost - nrepl://localhost:40047
```

Your bindings may vary, but in [Spacemacs](http://spacemacs.org/) I would type `,sc` and then just hit enter twice to select `localhost` and the currently running server.
CIDER knows the server port because it should’ve created a `.nrepl-port` file in your current directory.

Once you’re connected, you can start evaluating Clojure code, how wonderful!
When you wish to drop into a ClojureScript REPL backed by NodeJS (by default), you can use the `cljs-repl` function we defined in your `user` namespace.
Just open the REPL (`,ss` in Spacemacs) and execute `(cljs-repl)`.

If everything went according to plan, you should now be able to evaluate ClojureScript code as easy as Clojure.
If you are jumping between Clojure and ClojureScript namespaces, you’ll have to connect twice with one of those REPLs in ClojureScript mode.

A REPL can only ever be serving Clojure or ClojureScript, not both at the same time.
Once you execute `(cljs-repl)` that connection becomes ClojureScript specific.
