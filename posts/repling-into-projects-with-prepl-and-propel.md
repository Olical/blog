---
tags:
  - blog-post
  - imported-blog-post
---
# REPLing into projects with prepl and Propel

This post is intended for [Clojure](https://clojure.org/) or [ClojureScript](https://clojurescript.org/) developers who’re interested in trying prepl based tooling over traditional [nREPL](https://nrepl.org/) approaches.
I hope to show you how to get started quickly with my own tool, [Propel](https://github.com/Olical/propel), then show you where to go once you outgrow it.

You may be interested in my other post, [Clojure socket prepl cookbook](/clojure-socket-prepl-cookbook), which walks you through starting various socket prepls without any extra dependencies or tools.

## Interlude: prepl?

If you aren’t already aware, the prepl (pronounced "prep-ul" and written in lower case) is a new addition to Clojure and ClojureScript versions 1.10 and up.
It’s essentially a slight extension to the existing socket REPL which allows you to use a REPL over a socket as opposed to stdio.

Socket REPLs are fine for human interaction, over `netcat` perhaps, but aren’t great for connecting automated tooling to a running application.
There’s no way to tell what kind of output you’ve received: stdout, stderr, tap and evaluation results are all indistinguishable from each other.

The prepl is a ***p***rogrammable REPL, we send it code and it responds with EDN data structures that we can parse and handle with ease.
Say we had a socket prepl on port `5555`, we could perform the following interaction with it.

```sh
$ nc localhost 5555
(do (println "Hi!") (+ 10 10))
{:tag :out, :val "Hi!\n"}
{:tag :ret, :val "20", :ns "user", :ms 3, :form "(do (println \"Hi!\") (+ 10 10)\n)"}
```

The first line is the evaluation we performed, the other two are what the prepl responded with.
We can clearly see stdout marked by `:tag :out` and the return value of the evaluation by `:tag :ret`.
This is more than enough information to build tools on top of.

## Propel

[Propel](https://github.com/Olical/propel) is a tool I’ve written to make starting prepls more succinct, it also helps with connecting a stdio REPL to an existing prepl which can come in very handy.

> I do think that starting prepls will get easier in the future with built in support for simple command line flags that may look like Propel’s.
> You’ll also see them pop up natively in things like figwheel and shadow-cljs, the latter is being actively worked on (last I heard, anyway).
> My theory is that Propel won’t be needed in the future but might be a nice stopgap and inspiration for future ideas.

You can try it out for yourself by executing the following, providing you have the [Clojure CLI](https://clojure.org/guides/deps_and_cli) installed.

```sh
$ clj -Sdeps '{:deps {olical/propel {:mvn/version "1.3.0"}}}' -m propel.main
[Propel] Started a :jvm prepl at 127.0.0.1:46677
user=> (+ 10 20)
30
```

This command started a socket prepl on a free port and then started a stdio REPL that understands prepl and connected it to the socket.
We can now connect whatever we want to that socket, such as [Conjure](https://github.com/Olical/conjure) (my Neovim tooling for Clojure and ClojureScript) or even another REPL.
This could be useful if you started your socket prepl inside a Docker container or on a remote server.

> From here on in I’m going to assume you have a `deps.edn` containing `{:deps \{olical/propel {:mvn/version "..."}}}` where the `...` is replaced by the current latest version.

```sh
$ clj -m propel.main -rp 46677
user=> (def message "Hi!")
#'user/message
```

I’ve asked Propel to _not_ start a new prepl (`-r` / `--repl-only`) and to set the port (`-p` / `--port`) to the previously selected one.

Then we can access that value in our original REPL.

```sh
user=> message
"Hi!"
```

We could connect [Conjure](https://github.com/Olical/conjure) to our prepl by defining the following `.conjure.edn`.

```edn
{:conns {:my-prepl {:port 46677}}}
```

The port will change on every execution which will probably get annoying, we _could_ get around this by specifying a chosen port when we start the socket prepl (`-p 5555`, for example).
Alternatively, we could have Propel write it’s selected port to a file that our tooling can read from.

```sh
$ clj -m propel.main --write-port-file # (or -w)
[Propel] Started a :jvm prepl at 127.0.0.1:38957 (written to ".prepl-port")
user=>
```

And then read that file in your `.conjure.edn` or equivalent.

```edn
{:conns {:my-prepl {:port #slurp-edn ".prepl-port"}}}
```

You could even have this sort of configuration inside your home directory (or `~/.config/conjure/conjure.edn`) to have this automatic connection across all of your projects.

## ClojureScript

A lot of the ClojureScript use cases are extremely simple, we just need to change the environment from the default `:jvm`.

```sh
$ clj -m propel.main --env node # (or -e)
cljs.user=> (+ 10 20)
30
```

```sh
$ clj -m propel.main -e browser
cljs.user=> (js/alert "Hello!")
# You should see an alert dialog in a new tab in your browser.
```

Figwheel is supported through the `:lein-figwheel` and `:figwheel` (the newer [figwheel-main](https://github.com/bhauman/figwheel-main)) environments.
With `:lein-figwheel` it’ll attempt to read your configuration from your `project.clj`, it’s like executing `lein figwheel` but with a socket prepl automatically attached.

```sh
# https://github.com/bhauman/flappy-bird-demo

# Lein
$ lein run -m propel.main -- -e lein-figwheel -w

# Clojure CLI
$ clj -m propel.main -e lein-figwheel -w

Figwheel: Starting server at http://0.0.0.0:3449
Figwheel: Watching build - flappy-bird-demo
Compiling build :flappy-bird-demo to "resources/public/js/flappy_bird_demo.js" from ["src"]...
Successfully compiled build :flappy-bird-demo to "resources/public/js/flappy_bird_demo.js" in 0.578 seconds.
Figwheel: Starting CSS Watcher for paths  ["resources/public/css"]
[Propel] Started a :lein-figwheel prepl at 127.0.0.1:42315 (written to ".prepl-port")
cljs.user=>
```

With `:figwheel` ([figwheel-main](https://github.com/bhauman/figwheel-main)), you need to specify a `--figwheel-build` which tells figwheel which `*.cljs.edn` file should be loaded.
It defaults to `propel` so you could put your configuration in `propel.cljs.edn` and then execute the following.

```sh
# https://github.com/bhauman/flappy-bird-demo-new
$ clj -m propel.main -e figwheel --figwheel-build flappy -w
2019-09-14 15:07:03.203:INFO::main: Logging initialized @9786ms to org.eclipse.jetty.util.log.StdErrLog
[Figwheel] Validating figwheel-main.edn
[Figwheel] figwheel-main.edn is valid \(ツ)/
[Figwheel] Compiling build flappy to "target/public/cljs-out/flappy-main.js"
[Figwheel] Successfully compiled build flappy to "target/public/cljs-out/flappy-main.js" in 1.022 seconds.
[Figwheel] Watching paths: ("src") to compile build - flappy
[Figwheel] Starting Server at http://localhost:9500
Opening URL http://localhost:9500
[Propel] Started a :figwheel prepl at 127.0.0.1:46075 (written to ".prepl-port")
Open URL http://localhost:9500
cljs.user=>
```

## Outgrowing Propel

Propel isn’t designed to be a comprehensive solution, it’s a way to get going quickly but larger projects will most certainly find it limiting eventually.
What happens when you want an nREPL + prepl + two figwheel instances all running inside the same JVM?
You’re going to need to have your own namespaces to start these things up yourself.

You can do most of this by calling the functions in the `propel.core` namespace, but you may need to do some more manual work to hook up a particularly hairy figwheel configuration to a prepl.
I’d highly recommend you have a read of [Clojure socket prepl cookbook](/clojure-socket-prepl-cookbook) to work that sort of thing out.

You can also learn a lot by reading Propel’s source code, dive in and have a look at how I did things!

My point being: Propel will help you get going but you’re going to need to have a read of some code to grow into multiple figwheel builds and prepls running alongside each other in the same JVM.
_Hopefully_ this won’t be a problem in the future and you’ll just be able to provide a single argument to any ClojureScript environment of your choosing.
