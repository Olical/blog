---
tags:
  - blog-post
  - imported-blog-post
---
# Some thoughts on Io

I’m talking about [the language](http://iolanguage.org/) not input/output. This whole Io != I/O thing made Googling around for documentation and examples a whole lot harder too. This was the second language from [the book](https://pragprog.com/book/btlang/seven-languages-in-seven-weeks) which, on the whole, was a pleasant experience. My source code for the exercises can be found in [my languages repository](https://github.com/Wolfy87/langs).

The book says it’s as close as you’ll get to an object oriented Lisp, which felt apt after working with it for a few days. It’s [homoiconic](http://en.wikipedia.org/wiki/Homoiconicity) which is something I’ve begun really hunting for recently. I think it’s Clojure’s fault that I’m now hooked on the idea and revel in writing the AST directly in the languages data structures.

## Things that were “meh”

It doesn’t feel as elegant as a Lisp though, the message passing gets in the way sometimes. I found myself struggling to do simple application of functions and manipulation of argument lists which would even be easy in JavaScript. It felt inconsistent in places and appears to have many undocumented / sparsely documented features that are great, but only if you can find them. I suppose I’m not really being fair by comparing and contrasting to Clojure though.

Because I found the message passing stuff fiddly it meant [the XML builder exercise](https://github.com/Wolfy87/langs/blob/master/io/day3/xml.io) ended up really messy. It doesn’t feel elegant at all, yet I can see an elegant solution if I were using Lisp and macros as opposed to redefining the _forward_ function and hacking around with that for a while. The key point of the language, objects and prototypes, is the main thing I didn’t like. I just wanted to use plain data structures and lists as opposed to inheritance and types.

It’s probably obvious by now: I don’t really like OOP. So a OO Lisp is a Lisp with one of the best things removed: Lists all the way down (with the odd hash-map thrown in).

## Things that rocked

Auto loading of modules is a really nice feature. If you have _Something.io_ in the same directory as your REPL or whatever, when you do _Something clone_ it will automatically load the file for you. It’s a nice touch that leads to really clean code. No idea how it works if the file’s in another directory though. It’s probably explained in the guide or something, there must be a way to load a file by path. _doFile_ would work but seems like an odd approach.

It would definitely make a good embedded language, I think I’d rather use it over Lua since it’s close to a Lisp. It’s just a shame that the documentation and community are both really thin on the ground.

I’m not sure if I’ll ever use it but it was a nice experience. A refreshing language that has changed my perspective some more, which is exactly what I was hoping to get out of this book. It did start out as a learning exercise, so it’s okay that it’s small.

## Tangent: I want to build a little language now too

It’s pretty much inspired me to go off and design my own language and compiler. I’m thinking about a Lisp-like thing with significant whitespace and implicit lists. So you can add parenthesis where required, but most of them are added automatically based on new lines and indentation. The benefit of this is that you should be able to move a line and reindent without having to balance the parenthesis, so you don’t need paredit (even though it’s amazing). It’s the homonocity and AST writing of Lisp, but with a few less parenthesis. I think I could make it work, we’ll see.
