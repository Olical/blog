---
alias: asynchronicity-and-javascript
tags:
- blog-post
- imported-blog-post
---


There are two very difficult things in UI development, well, there’s a lot more than that but here’s two very important aspects.

* Management of state
* Management of asynchronicity

A lot has been done to make state less… evil? (see React, Redux, Cycle, Elm, Om, etc) The same can’t really be said for async operations, we kind of dumped some bare bones promise implementation into the language then wandered off to rendering land having done a good job painting callbacks in another colour.

[Wikipedia](https://en.wikipedia.org/wiki/Futures_and_promises) describes futures and promises like so.

> Futures and promises originated in [functional programming](https://en.wikipedia.org/wiki/Functional_programming) and related paradigms (such as [logic programming](https://en.wikipedia.org/wiki/Logic_programming)) to decouple a value (a future) from how it was computed (a promise), allowing the computation to be done more flexibly, notably by parallelizing it. It later found use in [distributed computing](https://en.wikipedia.org/wiki/Distributed_computing), in reducing the latency from communication round trips. **More recently, it has gained popularity by allowing writing asynchronous programs in [direct style](https://en.wikipedia.org/wiki/Direct_style), rather than in [continuation-passing style](https://en.wikipedia.org/wiki/Continuation-passing_style).**

Hang on, apparently they allow us to program async in a direct style, which is _really_ nice instead of a continuation passing style (see: callbacks). So [Clojure](http://clojure.org/) gives us [futures](https://clojuredocs.org/clojure.core/future) which allows async (In another thread! Amazing!) but in a direct programming style. We can follow the code in one function, we don’t have to go back and forth to work out when it’ll return.

JavaScript promises on the other hand, callbacks. Callbacks everywhere. Callbacks all the way down. JavaScript promises are just glorified callbacks, it’s just another syntax and another style, but it still has the same problems. The caller is not in control of pulling the value back through. It’s continuation passing style, the thing promises and futures are supposed to help you avoid, but they’re built upon those concepts in JavaScript.

![[/Attachments/imported-blog-posts/legacy-images/2016/01/wp-1453555723852-1024x768]]

So I’m guessing you think I’m wrong and that promises in JavaScript are actually different from callback based spaghetti hell in some way? Syntactically, sure, but that’s as deep as the difference goes.

```
function slowAdd(a, b, cb) {
    const result = a + b
    setTimeout(() => cb(result), 1000)
}

function slowMultiply(a, b, cb) {
    const result = a * b
    setTimeout(() => cb(result), 1000)
}

slowAdd(5, 10, res => slowMultiply(res, 2, res => console.log(res)))
```

That was just callbacks, but I’m working asynchronously and passing the results through. If you wanted error handling you just have to make the first argument to the callback the “err” argument. I’ve always thought that naming makes the function look uncertain though. It reads like it’s giving an interview after a particularly bad game of football.

```
function slowAdd(a, b) {
    const result = a + b
    return new Promise(resolve => setTimeout(() => resolve(result), 1000))
}

function slowMultiply(a, b) {
    const result = a * b
    return new Promise(resolve => setTimeout(() => resolve(result), 1000))
}

slowAdd(5, 10)
    .then(res => slowMultiply(res, 2))
    .then(res => console.log(res))
```

Notice the similarities? The callback approach even looks cleaner in this situation, in my opinion. I’m not even a proponent of callbacks over promises. I’m a proponent of neither, my point being that they’re essentially the exact same thing, you just put the callbacks somewhere else but they’re still there.

The promises we have right now add some value over callbacks, but not much. They’re just a different way of writing the same spaghetti with less indentation. The only good thing about them are the 3rd party implementations (such as [bluebird](http://bluebirdjs.com/docs/api-reference.html)) that add some nice functions to handle asynchronous transformation of data. Other than that, promises ~= callbacks.

## If not those, then what?

I’m calling callbacks bad and promises essentially the same thing with some minor differences. What we actually need is something that allows us to invert control and pull data through (potentially in a blocking fashion) thus eliminating the need for callbacks and handing off control to another function which may _never_ give you the control back.

Different languages solve this problem in various ways, [Go](https://www.golang-book.com/books/intro/10) is fantastic at this, as is [Clojure](https://clojure.github.io/core.async/) (which copies the good ideas in Go) among many other languages that were designed for concurrency from the ground up. JavaScript was not designed for concurrency, it was just about designed to make forms submit when a user clicks a button and it struggles with that **sometimes** most of the time. A lot of the _good_ languages handle this with channels or communicating sequential processes (CSP). There are many other solutions, far better than what JavaScript has to offer, but CSP is the only one I’m highlighting here.

ES6 is adding [generators](https://davidwalsh.name/es6-generators) and ES7 is adding [async/await](https://jakearchibald.com/2014/es7-async-functions/). You can actually use CSP with generators like Clojure and Go if you try hard enough, [David Nolen wrote something really cool](http://swannodette.github.io/2013/08/24/es6-generators-and-csp/) regarding this. There’s also [entire libraries](https://github.com/ubolonton/js-csp) to allow use of CSP in JavaScript via generators. I’m not sure if it’s the right way to go since generators are essentially just a way to lazily generate sequences with async as a side effect, but it’s better than promises. As far as I can tell, async/await is just another syntactic abstraction on top of promises.

## These aren’t the solutions

All of this syntax being added to JavaScript seems like a really bad idea. Each comes with it’s own rules, complexities and even more libraries just to be able to use them conveniently. My advice? Use a good language that compiles to JavaScript to escape the impending madness. Yes it works and sort of gets the job done, but setting fire to your house will warm it up and keep heating bills down. It doesn’t mean there isn’t a far better alternative out there.

Do yourself and your team a favour, investigate very well established languages that compile to JavaScript and make async (among many other things) easy by default. You don’t need to use them if you decide it’s not for you, but at the very least you may learn that promises aren’t the best thing since sliced bread as most JavaScript developers appear to believe. They’re actually pretty mediocre compared to other solutions. Not all promises are bad though, the whole future/promise thing in Scala and Clojure is pretty good, we just have a _very_ simplistic implementation that adds 30% of the value.

I was originally going to write about how generators + CSP (**not promises**) and maybe async/await would come to save us, but after researching them I honestly can’t do that. Just think of all the legacy code you’ll constantly have to wrap and adapt to when each one uses a different approach to containing the async madness. Stick to something simple and widely used (even if it’s terrible in comparison to other good async solutions) or bite the bullet and learn a good language with good async tooling and use that instead.
