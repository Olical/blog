---
tags:
  - blog-post
  - imported-blog-post
---
# Exploring REPL tooling with socket prepl

> This post is mainly to help me plan my talk at [London Clojurians](https://www.meetup.com/London-Clojurians/events/262000841/) on 16th July 2019 on the same topic.

If you’re reading this, you’re _probably_ a [Clojure](https://clojure.org/) programmer to some degree, even if that’s just dipping your toes into the pool of immutability now and again.
Chances are you’ve encountered some sort of command line tooling such as [Leiningen](https://leiningen.org/) or the [Clojure CLI](https://clojure.org/guides/deps_and_cli) as well as some sort of REPL tooling for your editor.

This post is (hopefully) going to explain the inner workings of your current REPL tooling, as well as explain how my preferred tooling works and how it’s different.

## What is REPL tooling?

For those of you that aren’t sure, you probably already use it already, here’s an incomplete list of tools to give you an idea.

* [CIDER](https://cider.mx/)
* [Calva](https://github.com/BetterThanTomorrow/calva)
* [vim-fireplace](https://github.com/tpope/vim-fireplace)
* [vim-iced](https://github.com/liquidz/vim-iced)
* [Conjure](https://github.com/Olical/conjure) (this one’s mine but we’ll get to that)

There’s essentially one or more tools for every editor in existence out there somewhere.
REPL tooling, to me, means a plugin that connects to some remote Clojure (or [ClojureScript](https://clojurescript.org/)) process and allows you to send code to that process for evaluation from your text editor of choice.

It’s much richer than a normal terminal REPL since you can use mappings to send specific forms inside your editor to the REPL and get the results beside the source code.
They can provide autocomplete, documentation lookup, go to definition, formatting and much more without any static analysis or extra programs.
The tooling gets to be your IDE by running inside your existing Clojure process!

This is a super power very few languages get to enjoy, it’s something that’s hard to understand as a beginner.
It’s something that, when it clicks, can’t easily be left behind.
REPL tooling is how we write our Clojure programs, it’s the single essential tool in any Clojure programmer’s toolbelt.
Without this kind of tooling your only way to try something new is to turn it off and on again, which is completely normal across the industry.
Normal isn’t always good.

These plugins do not exist in a vacuum, they’re built upon a shared interface for connecting to REPLs over a network.
These interfaces influence the design of the plugins, their methodologies morph to fit the foundation they’re built on.
Let’s explore what your REPL tooling uses to actually make things happen.

## nREPL

[nREPL](https://nrepl.org/) is the golden standard of networked REPLs, it always has been and probably always will be.
[Bozhidar](https://batsov.com/) has done a great job of building up a community around the CIDER and nREPL stack.
Although originally tailored for the Emacs crowd, nREPL and some of CIDER’s middleware (we’ll get to what that is soon) has been extracted in such a way that any other editor tooling can lean on this solid foundation.

I used fireplace in Vim for years which connected to the same server as my colleague in Emacs, they get to use the same community effort to share that power.
The editor plugins are then thin clients around this nREPL based stack, the majority of the clever Clojure workings occur within the nREPL server which sits inside our project’s process.

To extend nREPL we have to write [middleware](https://nrepl.org/nrepl/design/middleware.html) for our nREPL server, this can add new operations and capabilities although it requires writing an nREPL specific wrapper to hook it all together.
Just like Leiningen plugins, you can rely on a generic library but you need to write something nREPL specific to connect it up in such a way that editors can use it.

Let’s start an nREPL server and see how it behaves when we connect to it via `telnet` (I’m going to use the [Clojure CLI](https://clojure.org/guides/deps_and_cli) for this).
Feel free to follow along in your terminal!

```sh
clj -Sdeps '{:deps {nrepl/nrepl {:mvn/version "0.7.0-alpha1"}}}' -m nrepl.cmdline -t nrepl.transport/edn
nREPL server started on port 35177 on host localhost - nrepl+edn://localhost:35177
```

We have to depend on `nrepl/nrepl`, enter the `nrepl.cmdline` namespace and then specify that we want to use [EDN](https://github.com/edn-format/edn).
It defaults to [Bencode](https://en.wikipedia.org/wiki/Bencode) which is a binary representation that’s not usable from the CLI.
EDN support isn’t in a stable release at the time of writing, so we need to rely on `0.7.0-alpha1`.

So it’s chosen a port for us, `35177` in this case (yours will probably be different!), let’s `telnet` into that and try evaluating something.

```sh
telnet 127.0.0.1 35177
Trying 127.0.0.1...
Connected to 127.0.0.1.
Escape character is '^]'.
(+ 10 10)
```

And in our nREPL server we see the following with a stack trace.

```
ERROR: Unhandled REPL handler exception processing message (+ 10 10)
```

That’s because nREPL expects all messages to be wrapped in a map data structure with an [`op`](https://github.com/clojure/tools.nrepl/blob/master/doc/ops.md) key that we can set to `:eval` to perform an evaluation.
Middleware adds more ``op``s to your server.
Let’s send this over `telnet` instead with a new "session".

```sh
telnet 127.0.0.1 35177
Trying 127.0.0.1...
Connected to 127.0.0.1.
Escape character is '^]'
-> {:op :clone}
<- {:session "621d5eda-799c-4447-b3e9-4a358eeee821", :new-session "8ece86b4-79d8-4753-a5f2-d0246d86fe83", :status #{:done}}
-> {:op :eval, :code "(+ 10 10)", :session "8ece86b4-79d8-4753-a5f2-d0246d86fe83"}
<- {:session "8ece86b4-79d8-4753-a5f2-d0246d86fe83", :ns "user", :value "20"}
<- {:session "8ece86b4-79d8-4753-a5f2-d0246d86fe83", :status #{:done}}
```

I’ve added arrows to illustrate where I was sending (`->`) and where nREPL was responding (`<-`), as you can see, it’s a little back and forth.
We have to clone the root session, grab that new ID, send an `:eval` with our code and the session ID then get back two responses.

The first contains the value, the second tells us the session is `:done`, I’m not really sure what that means.
I _think_ it means whatever we evaluated is done and there will be no further output.

So, your nREPL tooling essentially connects for you, manages your sessions and dishes out various ``op``s for you as you work.
I think things like autocompletion are actually an `op`, for example.
This does mean that nREPL has a bunch of plumbing that you need to be aware of while building tools (sessions etc) but for good reasons, it’ll allow you to cancel long running or infinite evaluations, for example.

There’s not really much else to show with regards to nREPL, I think [JUXT’s post on nREPL](https://juxt.pro/blog/posts/nrepl.html) is a fantastic resource if you wish to know more.
We’re going to move onto an equivalent technology that’s built into newer Clojure (and ClojureScript!) versions, let’s compare the value and trade offs.

## Socket REPL

So you may have seen the term thrown about in various Clojure circles but not many people are using it "in anger" right now.
The socket REPL is exactly what the name implies, a REPL attached to a socket.
Let’s start a server now, you can do it from the CLI.

```sh
clj -J-Dclojure.server.jvm="{:port 5555 :accept clojure.core.server/repl}"
Clojure 1.10.1
user=>
```

So we don’t need any dependencies (other than Clojure `1.10.0`+) and we get dropped into a regular REPL after it starts.
Let’s `telnet` into port `5555` (which I’ve selected) and send it some code!

```sh
telnet 127.0.0.1 5555
Trying 127.0.0.1...
Connected to 127.0.0.1.
Escape character is '^]'.
user=> (+ 10 10)
20
user=>
```

What’s interesting here is that we have the `user=>` prefix, just like the original REPL in the first terminal.
It’s exactly the same as if we typed that code into the normal default REPL, but we can do it over the network.
What happens when we print something though.

```sh
user=> (println "Hello, World!")
Hello, World!
nil
user=>
```

Ah, herein lies a problem.
Although we as humans can pretty easily tell that the first line is from stdout and the second is the `nil` returned from calling `println`, programs can’t.
Writing some software to understand what’s an error, stdout, stderr or a successful evaluation result with this tool would be a nightmare.

What we really need is a REPL over the network that evaluates code for us and wraps the responses in some sort of data structure so we knew what kind of response it was.

## Enter the prepl

Say hello to your new best friend, the prepl (pronounced like "prep-ul", not "p-repl"), it does just what we described!
Let’s start up a prepl and give our previous `println` evaluation another go.

```sh
clj -J-Dclojure.server.jvm="{:port 5555 :accept clojure.core.server/io-prepl}"
Clojure 1.10.1
user=>
```

Starting a prepl is done by starting a normal socket REPL but you give it a different `:accept` function, this handles all input and output for the socket.
You can learn a little more about starting prepls in my [Clojure socket prepl cookbook](/clojure-socket-prepl-cookbook) post.

```sh
telnet 127.0.0.1 5555
Trying 127.0.0.1...
Connected to 127.0.0.1.
Escape character is '^]'.
(println "Hello, World!")
{:tag :out, :val "Hello, World!\n"}
{:tag :ret, :val "nil", :ns "user", :ms 121, :form "(println \"Hello, World!\")"}
```

Excellent!
We connect to the same port as before, send the same code as before, but we get back two wrapped responses.
We can parse these two EDN values one line at a time and dispatch some code based on the `:tag`.

This is more than enough information for some remote program to connect, evaluate and act on the responses.
These are the exact principals that [Conjure](https://github.com/Olical/conjure) is built on top of, it builds strings of Clojure code and fires them at a prepl for you.
This means your project doesn’t require any dependencies to enable your REPL tooling, you can just start a server and connect your editor to it, it’ll handle the rest.

One of my favourite things about this is that ClojureScript support doesn’t require you to jump through any hoops like [piggieback](https://github.com/nrepl/piggieback) for nREPL.
We can just start a ClojureScript prepl and connect to that, let’s start one that automatically opens and runs in our browser.

> Yes, this is all built into vanilla ClojureScript, just make sure you’re using the latest version!
> I’ve had a few patches already merged to unify the ClojureScript prepl with the canonical Clojure one, but I still have patch outstanding ([CLJS-3096](https://clojure.atlassian.net/browse/CLJS-3096)).
> Hopefully my work here makes future prepl tooling authors lives a lot easier!

```sh
clj -J-Dclojure.server.browser="{:port 5555 :accept cljs.server.browser/prepl}"
Clojure 1.10.1
user=>
```

So our prepl server is up (on the same port as before) and we get dropped into a regular _Clojure_ REPL, this isn’t ClojureScript.
We’ve started a ClojureScript prepl from inside a JVM process.
If you want to have figwheel building your ClojureScript as well as a prepl then check out the [figwheel section](/clojure-socket-prepl-cookbook#figwheel-and-prepl) in my prepl post.
A prepl can be plugged into any ClojureScript environment, it just might take a little research.

```sh
telnet 127.0.0.1 5555
Trying 127.0.0.1...
Connected to 127.0.0.1.
Escape character is '^]'.
(println "Hello, World!")
{:tag :out, :val "Hello, World!"}
{:tag :out, :val "\n"}
{:tag :ret, :val "nil", :ns "cljs.user", :ms 161, :form "(println \"Hello, World!\")"}
```

Well that’s pretty cool but how did this work.
The JVM process ended up compiling our ClojureScript to JavaScript, sent that off to a newly opened browser tab in Firefox and evaluated it there.
It gathered the results and printed that out of my socket prepl with each output wrapped in machine friendly data.

The two `:out` results is probably due to how `println` is implemented in ClojureScript.
If you write prepl tooling finding these sorts of differences in the two becomes quite common place.

## How Conjure uses the prepl

[Conjure](https://github.com/Olical/conjure) is my Clojure(Script) tooling for [Neovim](https://neovim.io/), written in Clojure and running on top of prepl connections.
It has it’s own JVM that build strings of Clojure code to send to your prepl for evaluations.

It supports things like documentation lookup, go to definition and completion (via [Compliment](https://github.com/alexander-yakushev/compliment) which is injected for you).
None of this requires any dependencies or changes to your existing project, other than starting a prepl.

That prepl isn’t modified in any way though, it just acts as a way to evaluate code remotely that Conjure takes advantage of.
If it was built on top of nREPL I supposed I’d be relying on a few bits of middleware, I’d maybe be more inclined to require a project dependency since using nREPL requires one anyway.

Let’s look at how Conjure prepares any code you send it for evaluation.
It doesn’t just evaluate the code as-is, it wraps it up in such a way that the symbols defined in that evaluation will get the correct source file and line associated with them (not in ClojureScript, yet).

```clojure
(defn eval-str [{:keys [ns path]} {:keys [conn code line]}]
  (let [path-args-str (when-not (str/blank? path) ;; 1
                        (str " \"" path "\" \"" (last (str/split path #"/")) "\""))]
    (case (:lang conn) ;; 2
      :clj
      (str "
           (do ;; 3
             (ns " (or ns "user") ") ;; 4
             (let [rdr (-> (java.io.StringReader. \"" (util/escape-quotes code) "\n\") ;; 5
                           (clojure.lang.LineNumberingPushbackReader.) ;; 6
                           (doto (.setLineNumber " (or line 1) ")))]
               (binding [*default-data-reader-fn* tagged-literal]
                 (let [res (. clojure.lang.Compiler (load rdr" path-args-str "))] ;; 7
                   (cond-> res (seq? res) (doall)))))) ;; 8
           ")

      :cljs
      (str "
           (in-ns '" (or ns "cljs.user") ") ;; 9
           (do " code "\n)
           "))))
```

This is probably the most complex code rendering function in Conjure, let’s step through it with the number comments I’ve added.

1. Optionally build a string that’ll be an argument to `(.load clojure.lang.Compiler)`, it sets the path for all ``def``s within this evaluation.
2. Build different strings for Clojure (`:clj`) and ClojureScript (`:cljs`) connections.
I’m working to patch prepl to require less of these language specific things but there will always be subtle differences.
3. Wrap the two parts of Clojure evaluations in a do so we only get one output from the prepl.
4. Swap the namespace before the evaluation, this is read out of your buffer in Neovim through some interesting process.
5. Wrap the code to be evaluated in a `StringReader`.
6. Pass that to a `LineNumberingPushbackReader` then set the line number to what was specified or 1 by default.
7. Actually evaluate the code, I use `clojure.lang.Compiler` because some of the higher level functions don’t let you set this path.
8. If the result is a sequence, fully realise it with `doall` otherwise we’ll get weird behavior with lazy sequences that print things.
9. In ClojureScript we perform two evaluations: Swapping the namespace and evaluating the code in a `do`.
This means that the code calling this in Conjure needs to throw away the first prepl result since it’s just a confirmation that the namespace was changed.

An evaluation function inside Conjure will execute this template function with the appropriate code and connection information.
It’ll then pass the result off to your prepl, get the result and deal with it accordingly, showing you any errors.
Everything in Conjure works like this to some degree, building up code from template functions, evaluating it then working with the result.

## Trade offs

All of this is pretty great but it comes at a cost: We don’t have anything like middleware, the _only_ feature we have available is evaluation.
Now some may say that’s bad, I think that’s totally fine.
We can now craft evaluations in such a way that we can do anything we want.

What better API than Clojure itself, we can build any tool imaginable with a REPL that lets us evaluate something.
nREPL definitely has benefits by managing our sessions, allowing us to cancel execution and extend the messaging layer itself, but I don’t miss them here.
I like the fact that I have one infinitely powerful thing, I just have to send it the right code.

## Wrap up

I hope this tour has taught you even one small thing about any of these technologies.
My main takeaway from this is that nREPL is super powerful, but you have to learn nREPL.
The socket REPL and prepl are _much_ simpler but still allow you to do anything you want, albeit with carefully crafted Clojure code strings.

There’s a lot to be said for middleware, it definitely feels like a more proper way to do some things, but so far in my 6-12 months worth of work on Conjure the lack of it hasn’t hindered me.

To all of you future or current Clojure tool authors out there, whatever technology you end up building upon, build amazing usable tools that will draw more people to our lovely language.

Let’s make everyone else jealous.
