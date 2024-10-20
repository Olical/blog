# Clojure prepl for tool authors

I’ve written pretty extensively about [how to start a prepl](https://oli.me.uk/repling-into-projects-with-prepl-and-propel/) as well as [what prepls are](https://oli.me.uk/clojure-socket-prepl-cookbook/). I’ve also written a few tools such as [Conjure](https://github.com/Olical/conjure) and [Propel](https://github.com/Olical/propel) that wouldn’t exist without it.

Since fantastic tools such as [shadow-cljs](https://github.com/thheller/shadow-cljs) are beginning to [implement their own prepls](https://github.com/thheller/shadow-cljs/issues/508) I thought it was about time I transcribed my understating of how the prepl should be implemented, as well as how you should interface with it.

> I’ve actually found discrepancies between the Clojure and ClojureScript’s official prepl implementations, I’ve had some patches merged already and some will have fixes in future releases (Clojure(Script) `1.11` etc). I’ll be showing you what I see as the canonical implementation and contract that we should all strive for, even if there’s still a few small differences in Clojure core right now.

## High level essentials

The prepl is a essentially an agreement of how output from a socket will be wrapped in EDN data structures, the only input to a prepl is Clojure or ClojureScript code as a string over a socket. If you start a prepl, through any means, you should be able to `netcat` into it from your terminal and send code to it. It should respond in a predictable way with some EDN data structures.

That data structure is simple enough that any language that can parse fairly basic EDN should be able to work with it. This means you could write a tool in Rust that simply sent off code for evaluation then parsed the resulting strings out with an EDN library.

Sending `:repl/quit` or an "end of file" should kick you off of the socket prepl. All evaluations are blocking and will be evaluated and returned in order, if you want async evaluations you should implement it yourself by wrapping code for evaluation in a framework of futures, for example.

Sending two forms in one go should result in at least two responses. Evaluating `(foo) (bar)`, for example, shouldn’t hide the result of `(foo)`, it should return the result of both top level forms one after the other. Evaluations are _not_ wrapped in an implicit `(do ...)`.

You **need** to allow the use of reader conditionals inside the evaluation (like `#?(:clj 10, :cljs 20)`), make sure you test this! Clojure `1.10` doesn’t currently allow them but you can work around it by wrapping the code to be evaluated in an evaluation call that does allow it (Conjure does this). I’ve fixed this and it should be released with `1.11`.

## Messages

There are four kinds of messages denoted by the `:tag` of the response, it’s possible that prepl implementors may add their own but support for them will depend on the tool (shadow-cljs might let you know if the browser was disconnected, for example).

> If you’re building something that connects to a prepl, think about what you could do with message types you don’t recognise. Maybe just show them somewhere?

* `:ret` - returned result from an evaluation.
* `:out` - from `**out**`.
* `:err` - from `**err**`.
* `:tap` - data sent through a call to `(tap> ...)`.

Let’s look at the messages in turn and I’ll highlight some key things to pay attention to.

## `:ret`

```clojure
(+ 10 20)
{:tag :ret, :val "30", :ns "user", :ms 3, :form "(+ 10 20)"}
```

* The type of the message (`:ret`) is a keyword under `:tag`, as already mentioned.
* The result of the evaluation is a **string** stored under the `:val` key.
* The namespace that you ended up in **after** the evaluation is stored as a **string** under `:ns`.
* The time it took to perform the evaluation is stored as an integer under the `:ms` key.
* Finally, the form that was evaluated is stored as a **string** under `:form`.

Pay close attention to the fact that `:val` and `:ns` have both been turned into strings, do not be tempted to put the original data as nested EDN here, that is incorrect.

When an exception occurs in an evaluation you will also get an `:exception true` inside the message body. The `:val` in that case will be the error after it’s been run through `Throwable->map` (or `Error->map` in the case of ClojureScript) then `pr-str`.

```clojure
(throw (Error. "oh no"))
{:tag :ret, :val "{:via [...ELIDED FOR READABILITY ...] :cause \"oh no\", :phase :execution}", :ns "user", :form "(throw (Error. \"oh no\"))", :exception true}
```

This is enough information to let a client know when something went wrong as well as what it was.

> ClojureScript `1.10.520` doesn’t yet mark them with `:exception true`, I’ve fixed this but it hasn’t been released yet.

## `:out`

```clojure
(println "Hello, World!")
{:tag :out, :val "Hello, World!\n"}
{:tag :ret, :val "nil", :ns "user", :ms 2, :form "(println \"Hello, World!\")"}
```

* All data written to `**out**` should appear under `:tag :out`.
* The actual data is stored as a string under `:val`, just like `:ret` messages.
* New lines are preserved if the user performed a `println`, for example. Don’t trim anything!
* You may want to flush on every prepl evaluation to ensure that things like `pr` or `print` appear under `:out` as they happen.
* Things like `println` will return `nil`, so you will see an `:out` message as well as a `:ret` when you evaluate this sort of thing.

## `:err`

See `:out`, it’s the same but for data sent to `**err**`, as you might expect.

## `:tap`

```clojure
(tap> :foo)
{:tag :ret, :val "true", :ns "user", :ms 2, :form "(tap> :foo)"}
{:tag :tap, :val ":foo"}
```

* When someone executes `tap>` it returns true to let the user know it worked, more `tap` functions may exist in the future that avoid this `true`. If you want to use it you may want to use `(doto (my-thing) tap>)` to `tap` the result as well as return it.
* `tap` messages have a `:tag` of `:tap`, which is thankfully unsurprising by this point.
* The `:val` contains the body of the `tap` as a string.
* If you’re implementing a prepl you’ll need to hook yourself into Clojure’s `tap` infrastructure although that’s beyond the scope of this post.

## Wrapping up

That’s actually all there is to it! Just make sure you’re returning strings, not data, in the right places.

Pay special attention to how exceptions work and when you’re flushing to `**out**` or `**err**` to avoid surprising `print` behaviour for the client.

Please do get in touch if you have questions or thoughts about how to build or use a prepl as a Clojure (or ClojureScript) tool author.
