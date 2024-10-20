---
tags:
  - blog-post
  - imported-blog-post
---
# Clojure socket prepl cookbook

> This post has been translated into [Russian](https://web.archive.org/web/20201028075024/http://softdroid.net/povarennaya-kniga-clojure-socket-prepl) by Vlad at [Softdroid](https://web.archive.org/web/20201024055921/https://softdroid.net/).

> I’ve since released [Propel](https://github.com/Olical/propel) which essentially does everything this blog post talks about but via an easy to use CLI or function.
> Feel free to have a read anyway!

The socket prepl is a relatively new tool built into Clojure that allows you to REPL into a running application.
It’s essentially a much simpler "official" approach to the problem that nREPL also happens to solve.
If your current nREPL tooling is working well for you then congratulations, carry on, this post won’t be of much use to you.

I’ve been developing Clojure(Script) tooling for [Neovim](https://neovim.io/) over a prepl connection for the past six months or so (involving three attempts in different languages).
It’s called [Conjure](https://github.com/Olical/conjure), you may have seen me constantly talking about it [over on twitter](https://twitter.com/OliverCaldwell).
Since writing this has involved prepling into all sorts of environments and applications I ended up learning quite a few recipes for starting your prepls.

This post is intended to be a reference for various socket prepl techniques, I hope you find them useful!

## From the CLI

You can start a prepl from the Clojure CLI without your program being aware of it at all.

```bash
clj -J-Dclojure.server.jvm="{:port 5555 :accept clojure.core.server/io-prepl}" \
    -J-Dclojure.server.node="{:port 5556 :accept cljs.server.node/prepl}" \
    -J-Dclojure.server.browser="{:port 5557 :accept cljs.server.browser/prepl}"
```

This starts up three prepls then drops into a regular REPL session.
The REPL session and `jvm` prepl share the same environment so changes in the CLI REPL affect the prepl and the other way around.

The `node` and `browser` prepls each start up their own environments upon eval.
Give it a go!
Start up the prepls then use [netcat](https://en.wikipedia.org/wiki/Netcat) (or similar) to send them code.

```bash
# JVM
nc localhost 5555
(+ 10 10)
{:tag :ret, :val "20", :ns "user", :ms 2, :form "(+ 10 10)"}
:repl/quit

# node
nc localhost 5556
(+ 10 10)
{:tag :ret, :val "20", :ns "cljs.user", :ms 9, :form "(+ 10 10)"}
:repl/quit

# browser
nc localhost 5557
{:tag :ret, :val "20", :ns "cljs.user", :ms 51, :form "(+ 10 10)"}
:repl/quit
```

The resulting data structures you see are the prepl part, that’s what prepl does, wraps things in a predictable data structure that tools like [Conjure](https://github.com/Olical/conjure) can parse and work with.

## From your code

You don’t have to start things from the CLI, sometimes that’s not practical.
For example, at work we’re running everything through `lein` and I wasn’t totally sure how the arguments would work with that, so I just added the code next to where we start our nREPL server.

```clojure
(ns my.project.prepl
  (:require [clojure.core.server :as server]))

(defn start-prepl! [{:keys [bind port name]}]
  (server/start-server {:accept 'clojure.core.server/io-prepl
                        :address bind
                        :port port
                        :name name}))

;; In some -main fn somewhere...
(start-prepl! {:bind "localhost", :port 5555, :name "jvm"})
```

This is _essentially_ the same as the first line in the CLI only approach, you can swap out the `:accept` function for `cljs.server.node/prepl` for example and get a node prepl.

## Gotcha: Multiple node prepls

Something I got caught on and eventually worked out was why starting multiple node prepls on different ports conflicted with each other.
Well it turns out there’s a [hard coded port](https://github.com/clojure/clojurescript/blob/230e46aee2c9b76e426e85865ab8930c4c26e14f/src/main/clojure/cljs/server/node.clj#L27) (49001, if you’re interested) inside the node prepl source.
Luckily this is just a default and you can configure it pretty easily.

```bash
# Start these in two terminals and nc into both, only one will work.
clj -J-Dclojure.server.nodea="{:port 6661 :accept cljs.server.node/prepl}"
clj -J-Dclojure.server.nodeb="{:port 6662 :accept cljs.server.node/prepl}"

# You can set the args the accept function receives though.
# This means we can configure a port for the cljs.server.node/prepl function.
clj -J-Dclojure.server.nodea="{:port 6661 :accept cljs.server.node/prepl}"
clj -J-Dclojure.server.nodeb="{:port 6662 :accept cljs.server.node/prepl, :args [{:env-opts {:port 48000}}]}"
```

With this configured you should be able to run multiple node prepls on one machine at the same time.

## Figwheel and prepl

For regular Clojure projects, the information above should be enough for all situations.
For ClojureScript however it’s rare that you would be developing _without_ figwheel, it’s not an edge case, it’s the norm.
If you start up a browser prepl though that’s going to launch another tab to evaluate in, it doesn’t share the same context as figwheel.

Thankfully there’s a way to have figwheel reloading your ClojureScript as well as prepl into that figwheel environment!
I got this working with `figwheel-main` and a few tips from Bruce himself over Slack and Twitter ([@bhauman](https://twitter.com/bhauman)).
Here’s a minimal `deps.edn` for this technique.

```clojure
{:paths ["src" "target"]
 :deps {org.clojure/clojure {:mvn/version "1.10.0"}
        org.clojure/clojurescript {:mvn/version "1.10.520"}
        com.bhauman/figwheel-main {:mvn/version "0.2.0"}}}
```

And a small amount of code to start up figwheel then hook the prepl into figwheel’s `repl-env`.
This means you can send code to the socket prepl but it’ll rely on figwheel for compiling that ClojureScript and getting the resulting JavaScript into the browser (or node process!) for evaluation.

```clojure
(ns pfig.main
  (:require [figwheel.main.api :as fig]
            [clojure.core.server :as server]))

(defn -main []
  (figwheel.main.api/start
    {:id "dev"
     :options {:main 'pfig.test}
     :config {:watch-dirs ["src"]
              :mode :serve}})

  (println "=== START PREPL")
  (server/start-server {:accept 'cljs.core.server/io-prepl
                        :address "127.0.0.1"
                        :port 6776
                        :name "pfig"
                        :args [:repl-env (fig/repl-env "dev")]})

  (fig/cljs-repl "dev"))
```

I’m using the newer figwheel-main but this is definitely doable in other iterations of figwheel, the API might just be a little different.

## :repl/quit

Got any more tips or comments?
Say hi on twitter, I’m [@OliverCaldwell](https://twitter.com/OliverCaldwell).
I hope you’ve learned something new, have a great day!

## Edit 2019-03-23

José Luis Lafuente ([@jlesquembre](https://twitter.com/jlesquembre)) [pointed out](https://twitter.com/jlesquembre/status/1109461402069225472) that you can put these prepl JVM args in your `deps.edn` file but you’re not allowed to use spaces which makes Clojure maps tricky to write.
You can get around this issue by replacing the spaces in the string with commas since Clojure treats commas as whitespace anyway.

```clojure
{:deps {}

 :aliases
 {:prepl {:jvm-opts ["-Dclojure.server.repl={:port,40404,:accept,clojure.core.server/io-prepl}"]}}}
```

This example is taken from [github.com/seancorfield/dot-clojure](https://github.com/seancorfield/dot-clojure/blob/c4a98f4a62b3caba92b1cd05b897eadad80e4a07/deps.edn#L55-L56).

## Edit 2019-07-18

[thecontinium](https://github.com/thecontinium) over in issue [#49](https://github.com/Olical/conjure/issues/49) of Conjure got a prepl launched from Leiningen via `~/.lein/profiles.clj`.

```clojure
{:repl
 {:repl-options
  {:init (clojure.core.server/start-server {:accept 'clojure.core.server/io-prepl
                                            :address "localhost"
                                            :port 55555
                                            :name "lein"})}}}
```

Which they could connect to Conjure with a `.conjure.edn` containing the following.

```clojure
{:conns {:lein {:port 55555}}}
```

Neat!
