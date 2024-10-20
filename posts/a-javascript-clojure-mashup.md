# A JavaScript / Clojure mashup

I’m going to create a nightmare inducing monster and you’re going to enjoy it. Hear me out: Data as code in plain JavaScript without a pre-compilation step or new language to learn (even if Clojure is beautiful and takes all of 10 minutes to memorise the syntax). Of course we don’t have the distinction of vectors and lists, so arrays will have to do, but I think things like **mother flipping macros** would more than make up for it.

Join me on an adventure into insanity, forget your current opinions about JavaScript and the web that number more than the amount of stars in the known universe. Forget even the performance implications, they’re a (sort of) irrelevant detail right now. Just indulge in the concepts with me, this post isn’t planned, it’s a learning exercise as much for you as it is for myself.

[![I’ve just received word that the Emperor has dissolved the MIT computer science program permanently.](http://xkcd.com/297/)(http://imgs.xkcd.com/comics/lisp_cycles.png)]

## *What I want to end up with*

So my goal is to execute the following (very simple) Clojure code in JavaScript, but without any kind of string parsing, for example. The only thing I’ll need to cheat on is swapping parentheses for square braces, adding commas (sorry!) and correcting names that are illegal in JavaScript. My end goal is to start implementing the language in the language, just like Clojure. This little experiment could be cultivated into a DSL within JavaScript.

```
(if (> 10 20)
  "Uh, well this is awkward."
  (str "EVERYTHING IS FINE, MOVE ALONG " (reduce + [1 2 3])))
```

We should get “EVERYTHING IS FINE, MOVE ALONG 6” back. Obviously this won’t be able to execute directly, even with the array syntax in place and the dependant functions defined, so the JavaScript version will include a function to interpret the data structure. This could potentially cache the result of walking the tree for far greater performance, but I don’t care right now. Performance smermormance.

```
[$if, [$mt, 10, 20],
    "Uh, well this is awkward.",
    [str, "EVERYTHING IS FINE, MOVE ALONG ", [reduce, $add, [1, 2, 3]]]]
```

In time I could have this supporting things like defn and multiple arity functions, I have my plans, but for the sake of brevity I’ve just used this dumb piece of code. It’s the concept I want to play with right now, not the finished product that could take weeks of work and testing. And for no reason in particular, here’s the normal JavaScript ES5 syntax for this if it didn’t use the extra functions I had to invent.

```
(function () {
    if (10 > 20) {
        return "Uh, well this is awkward.";
    }
    else {
        return "EVERYTHING IS FINE, MOVE ALONG " + [1, 2, 3].reduce(function (a, b) {
            return a + b;
        });
    }
}());
```

I wrapped it in an IIFE because the return statements would have made no sense otherwise. The array syntax should pass the values back implicitly, normal JavaScript is not so lucky.

## *Science time*

Let’s start off small, this function will walk a tree of arrays and execute the first item with the rest of the items as it’s arguments. The function will first recurse to the bottom of the stack then begin evaluation inside out, just like Lisp.

```
function walk(tree) {
    return tree[0].apply(null, tree.slice(1).map(function (n) {
        return Array.isArray(n) ? walk(n) : n;
    }));
}

// It works!
function $mt(a, b) { return a > b; }
function $eq(a, b) { return a === b; }
walk([$eq, false, [$mt, 10, 20]]); // true
```

Oh, wait, now we’re 80% of the way there. Hah. The main thing we’re lacking is meta data on functions and lists (I refuse to call them arrays from here on in) being able to change their behaviour as the walker plods through the data structure. We need a way to “quote” and “unquote” lists for example to defer execution and allow the existence of macros. This essentially means that we should be able to make some lists exempt from execution, so they’re just lists. How do we unquote? Well, the top level function is already an unquote! Amazing! Yet terrifying!

```
function unquote(tree) {
    if (tree.$quoted) {
        var unquoted = tree.map(function (n) {
            return Array.isArray(n) ? unquote(n) : n;
        });
        delete unquoted.$quoted;
        return unquoted;
    }
    else {
        return tree[0].apply(null, tree.slice(1).map(function (n) {
            return Array.isArray(n) ? unquote(n) : n;
        }));
    }
}

function quote(tree) {
    var quoted = tree.map(function (n) {
        return Array.isArray(n) ? quote(n) : n;
    });

    quoted.$quoted = true;
    return quoted;
}

// Again, it works!
function $map(fn, n) { return n.map(fn); }
function $inc(n) { return n + 1; }
unquote([$map, $inc, quote([1, 2, 3])]); // [2, 3, 4]
```

One thing to note here is that I’m getting a lot of code duplication, this is because I wrote this part at midnight and I’m just trying to make a point. If you want it pretty, ask for a full on repository. And now for the silly magic. The point where we pull the metaphorical rabbit out of the metaphorical fedora. [Macros. In. JavaScript](http://gph.is/1a0ge2I) (I was going to embed the gif on the other end of that link but I thought it might be too distracting / awesome).

```
function unquote(tree) {
    if (tree.$quoted) {
        var unquoted = tree.map(function (n) {
            return Array.isArray(n) ? unquote(n) : n;
        });
        delete unquoted.$quoted;
        return unquoted;
    }
    else {
        var fn = tree[0];
        var args = tree.slice(1);

        if (fn.$macro) {
            return fn.apply(null, args);
        }
        else {
            return fn.apply(null, args.map(function (n) {
                return Array.isArray(n) ? unquote(n) : n;
            }));
        }
    }
}

function quote(tree) {
    var quoted = tree.map(function (n) {
        return Array.isArray(n) ? quote(n) : n;
    });

    quoted.$quoted = true;
    return quoted;
}

function macro(fn) {
    var wrapped = function () {
        var result = fn.apply(null, arguments);
        return Array.isArray(result) ? unquote(result) : result;
    };

    wrapped.$macro = true;
    return wrapped;
}

// So now we define the macro "postfixNotation" (as used in Clojure for the Brave and True).
// http://www.braveclojure.com/writing-macros/#2__Anatomy_of_a_Macro
// And it works beautifully.
function $add(a, b) { return a + b; }
var postfixNotation = macro(function (expr) {
    var butLast = expr.slice(0, -1);
    var last = expr.slice(-1);
    return last.concat(butLast);
});
var res = unquote([postfixNotation, [5, 5, $add]]);
```

Getting this working caused me to swear in surprise involuntarily. I just defined a macro in my own little meta language that runs off of a few small functions. And now, without further ado, the if macro.

```
function $eq(a, b) { return a === b; }
var $if = macro(function (expr, t, f) {
    return unquote(expr) ? t : f;
});

unquote([$if, [$eq, 5, 5], 'Yep!', 'Nope!']); // Yep!
```

I don’t know about you, but I find this incredible even if it does look **really** weird, the fact that it’s so easy to implement too is ludicrous. And now to bring it all together and run it.

```
function $mt (a, b) {
    return a > b;
}

function $add (a, b) {
    return a + b;
}

function str() {
    return [].slice.call(arguments).join('');
}

function reduce(fn, list) {
    return list.reduce(fn);
}

var $if = macro(function (expr, t, f) {
    return unquote(expr) ? t : f;
});

unquote(
    [$if, [$mt, 10, 20],
        'Uh, well this is awkward.',
        [str, 'EVERYTHING IS FINE, MOVE ALONG ', [reduce, $add, quote([1, 2, 3])]]]);

// EVERYTHING IS FINE, MOVE ALONG 6
```

It works! Had to quote that array and define all of the required functions, but it works! Only the required code path is executed too, which I find very cool for an array.

## *Take it further?*

It could perform some kind of caching as it walked the tree so you had a compile and execution step to this process. That allows you to build certain code paths and probably make it faster. Maybe not, maybe it’s fast enough right now and the simplicity is a far greater advantage. I would lean towards the latter. I would like to add defun, multiple arity functions and other such sugar. This is all something I would do if I carried this idea further into a more complex project. Even if nobody will ever use it, myself included, I think it would be fun. A language within a language.

I could produce a standard library and obviously flesh it out into an open source repository with tests, documentation and the [unlicense](http://unlicense.org/), as is my way. Or maybe this concept is pure silliness and it should go no further than this page. Either way, I hope it provides at least a little entertainment for those of you that tread the sad line between JavaScript and Clojure. Where the grass is truly greener on the other side, as well as being shaped like parentheses, but you can’t quite drag it kicking and screaming into your day job. Just yet, anyway.

Here’s [the repository](https://github.com/Wolfy87/clojs) for the code I developed during this post. Complete with late night commit messages!

Really thinking about how to implement a macro or macro system and why it should be like that has made Clojure macros quite a bit easier to understand too. So at the very least, I got that out of this exercise. I would love to hear your thoughts, even if they’re simply internal screaming.
