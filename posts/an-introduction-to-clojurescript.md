---
tags:
  - blog-post
  - imported-blog-post
---
# An introduction to ClojureScript

In this post I’m going to attempt to explain where [ClojureScript](https://github.com/clojure/clojurescript) comes from as well as what it is and what it can do for you. I’m coming at this from the perspective of someone who writes a lot of JavaScript already, probably with React and Redux.

## Lisp

ClojureScript is a very slight sub-set of Clojure, the main difference being one compiles to JavaScript, the other to Java VM bytecode. On the JVM, Clojure has access to threads and other OS level niceties. ClojureScript as a language is very similar to it’s parent, but it lacks certain things that just aren’t possible in the browser.

They are however, both a [Lisp](https://en.wikipedia.org/wiki/Lisp_%28programming_language%29), one of the most fascinating languages ever _discovered_ (see [do aliens have lisp?](https://www.quora.com/Do-aliens-have-LISP)). It’s not a perfect foundation, but it’s an extremely powerful and flexible one with a long and colourful history. Wikipedia will do a better job at listing that history than I can ever do, but here’s some key facts.

* It was first specified in **1958** ([JavaScript](https://en.wikipedia.org/wiki/JavaScript) appeared in 1995, 37 years later)
* Originally designed for AI research
* Has a huge amount of dialects which share the core tenants, including [ClojureScript](https://github.com/clojure/clojurescript), [Clojure](http://clojure.org/), [Racket](https://racket-lang.org/), [Scheme](https://en.wikipedia.org/wiki/Scheme_%28programming_language%29), [Lisp Flavoured Erlang](http://lfe.io/) and many more
* (it’s (lists (all the way (down))))
* It makes use of macros to define the language in the language, which I demonstrate in my post about [implementing a lisp in JavaScript](/a-javascript-clojure-mashup/)

This extremely exhaustive chart that shows the history of programming languages shows just how early Lisp turned up. It’s amazing to see what influenced what, click the image to enlarge it.

[![history-of-languages](/Attachments/imported-blog-posts/legacy-images/2016/02/history-of-languages.png)(/Attachments/imported-blog-posts/legacy-images/2016/02/history-of-languages-1024x631.png)]So it has been around for a very long time and has appeared in many flavours. We’re going to skip all of the other fascinating Lisps in the middle and jump all the way to Clojure, one of the newest and most popular in recent years.

## Clojure

[Clojure](https://en.wikipedia.org/wiki/Clojure) is a Lisp dialect that encourages functional programming with immutable data structures. The data structures themselves are pretty amazing and can be found in JavaScript through [ImmutableJS](https://facebook.github.io/immutable-js/). Immutable data structures never change, they only allow you to create new versions of them with your changes applied, this prevents mutation, a source of pain in many systems. They don’t just copy the data to prevent mutation as some people appear to do in JavaScript, they use efficient algorithms to share as much data as possible to keep things immutable without sacrificing too much speed or efficiency. [Object.freeze](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/freeze) is not persistent immutability, neither is cloning things every time. That’s slow and inefficient. Neither is using [the spread operator](https://gist.github.com/sebmarkbage/005c81e6f2f5ddac443f).

It runs on the JVM by default, so you can use all the Java libraries out there without having to write Java (yay!). You can also run it on the [Common Language Runtime](https://en.wikipedia.org/wiki/Common_Language_Runtime), which is kind of like the JVM but for Windows, I’ve never looked into that though. There’s also support for Unity, VR and JavaScript (through ClojureScript). So it’s an extremely versatile language that can run almost anywhere with the same core functions and principals.

One cool thing about Clojure is that the language is defined by the language. By that I mean, there’s a core interpreter (called a reader) as well as some core functions written outside of Clojure, but the rest is defined in [core.clj](https://github.com/clojure/clojure/blob/master/src/clj/clojure/core.clj), in Clojure. If there’s ever anything you don’t understand about the language, you can actually go and read the source code for that feature, like the [while macro](https://github.com/clojure/clojure/blob/d5708425995e8c83157ad49007ec2f8f43d8eac8/src/clj/clojure/core.clj#L6087).

## Versatility

You may wonder what Clojure can actually do, the Wikipedia page just says is “general purpose”, but so is JavaScript and anything else you’ve probably ever used. Well, it’s more general than you’ll be used to, if you can name a paradigm that a language supports, Clojure probably has support for it too. Because of it’s generic macro based nature, you can define any other style within the language. Here’s a list of libraries that enable awesome paradigms from other languages within Clojure.

* [core.async](https://github.com/clojure/core.async) – Like [Go](https://golang.org/)‘s channels
* [core.typed](http://typedclojure.org/) – Gradual typing
* [core.match](https://github.com/clojure/core.match) – Pattern matching
* [core.logic](https://github.com/clojure/core.logic) – Logic programming (check out [Prolog](https://en.wikipedia.org/wiki/Prolog), it’s really cool!)
* [core.matrix](https://github.com/mikera/core.matrix) – Matrix and array programming
* [schema](https://github.com/plumatic/schema) – Declarative data validation
* [factjor](https://github.com/brandonbloom/factjor) – Stack programming
* [test.generative](https://github.com/clojure/test.generative) – Generative testing!

My friend Ludwik at work described it well.

> You learn this one thing and it can take you to ALL the places.

No kidding, you can do anything with it, it’s the most flexible language I’ve ever researched. To me, it’s a host unifier, you have every paradigm and every platform within the same beautiful language. This versatility means it’s being used all over the world in various industries to solve all sorts of interesting and hard problems, as [the 2015 survey](http://blog.cognitect.com/blog/2016/1/28/state-of-clojure-2015-survey-results) shows. Ludwik actually uses it day to day at [Qubit](http://www.qubit.com/) with his team.

## Tooling

There’s excellent integration into many popular text editors and IDEs, including: Sublime, Atom, Vim, Emacs and [LightTable](http://lighttable.com/) (actually written in ClojureScript). [Cursive](https://cursive-ide.com/) is an IDE developed specifically for Clojure which seems pretty cool too, it’s based off of IntelliJ. I use Vim personally and have [written about it in the past](/essential-vim-bundles-for-javascript-and-clojure/). All of the tooling hooks into a running REPL which allows you to look up source code, documentation and execute things directly from your editor.

Editing Clojure is fun too, because you’re editing the actual AST so you can perform structural editing with things like [Paredit](http://danmidwood.com/content/2014/11/21/animated-paredit.html) or [Parinfer](https://shaunlebron.github.io/parinfer/). Forget manually copying and pasting lines around, adjusting quotes and curly braces, you can edit the AST and always keep it correct with powerful tools.

Now the coolest thing about everything I’ve mentioned above, it pretty much all works the same with ClojureScript and by extension, in the browser. Yes, you can have live editor integration into something running in your browser with first class language editing support across a plethora of editors. Support for multiple paradigms so you can choose the right tool for the job, wherever your code is running. ClojureScript can be automatically fired into your browser as you edit through the wonderful [figwheel](https://github.com/bhauman/lein-figwheel), this will make the hot module reloading you’ve seen in JavaScript look pretty basic.

All of this magic is driven by a very small amount of tooling, no more learning the “npm + grunt / gulp / broccoli + browserify / webpack + react + redux + immutablejs + ….” silliness. All you need is [Leiningen](http://leiningen.org/). A simple “lein new project-name-here” will get you up and running.

## ClojureScript

Let’s assume you’ve got leiningen installed and you want to create a “Hello, World!” application with [Reagent](https://reagent-project.github.io/) (a minimalistic React wrapper for ClojureScript, there’s a few wrappers out there though, go explore!) and live reloading through figwheel.

```
lein new reagent hello-reagent
cd hello-reagent
lein figwheel

# Now open http://localhost:3449
```

You now have a live reloading ClojureScript environment with routing and great React support. Arguably a better React than React since the immutable data structures are integrated seamlessly, those allow you to check for changes far faster than plain JavaScript.

That’s all there is to it though, getting set up is extremely quick and easy. You then have a live environment to edit in any way you want, you are free to explore the language by moving and editing expression that will be sent to the browser when you write the file. The reloading keeps the state, so you can edit the page’s source while interacting with it.

Once you get used to this live / REPL style of programming where you can explore ideas without restarting anything you won’t want to go back. I won’t be teaching you ClojureScript here, but I wanted to get you to a point where it’ll be easy to learn. Now you should go and play, have fun with the language and just try things out. It’ll look odd at first but lisp will seem normal to you surprisingly quickly. You’ll quickly feel that editing JavaScript feels clunky since moving things around requires constant adjustment of the syntax as well as regular reloads, even with HMR enabled within webpack, for example. The JavaScript tooling doesn’t feel like it fits together well, ClojureScript is the opposite.

So go and learn, read books, build toy projects. Explore everything this cool language and ecosystem has to offer (just look how cool [devcards](https://github.com/bhauman/devcards) are!). Even if you don’t end up using it in production I can promise that it’ll teach you things that will make you a better programmer. Even if you only learn about lisp and don’t use it, it’ll change how you solve problems.

I hope this helped and that you have fun.
