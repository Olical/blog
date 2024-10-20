# Lazy arrays in JavaScript

So I built a thing called [lazy-array](https://github.com/Wolfy87/lazy-array). It came off the back of [a tiny gist](https://gist.github.com/Wolfy87/75b435fce2091f79155e) and grew into something quite powerful (if I do say so myself). This project allows you to define lazy arrays in JavaScript, surprisingly enough, but what are they?

## Lazy arrays

A lazy array is a sort of collection that doesn’t actually do or contain anything until it’s absolutely necessary. So a lazy [sequence](http://clojure.org/sequences) in Clojure will do as little as possible until required. I’ve actually modelled my implementation on that of [Clojure’s lazy sequences](http://theatticlight.net/posts/Lazy-Sequences-in-Clojure/).

This allows you to represent things that don’t exist yet or are infinite, such as the entire Fibonacci sequence. That’s a powerful concept that I want to explore a little within the JavaScript UI space.

## This implementation

My implementation actually works fine with vanilla arrays as well as the lazy variety, so my library ends up being a set of array manipulation functions (like map and filter) that evaluate at the last possible moment and can consume infinite lazy arrays of all positive integers, for example.

A map or filter over a value would not yield another value, they produce another lazy array and don’t actually execute anything at the time. They only resolve when you force them to using the _all_ function or you request a specific item from the array (like the first or 10th). The results of a lazy array instance are also cached and used the next time it’s requested, just like Clojure, so that’s worth bearing in mind. I haven’t seen it cause issues, but it should in theory make repeated calls to things very fast.

> [@OliverCaldwell](https://twitter.com/OliverCaldwell) [@closuresaddict](https://twitter.com/closuresaddict) I think you now which route I favour. Once you’ve amassed enough lipstick, it’s time to get rid of the pig.
>
> — MacroServices (@krisajenkins) [May 23, 2015](https://twitter.com/krisajenkins/status/602045724164038657)

Well said, [Kris](https://twitter.com/krisajenkins). This is more lipstick. I hope it’s good lipstick.

## Fibonacci: The classic

Every time someone discusses laziness they create an example using the Fibonacci sequence, this is no exception. I’m going to port this little Clojure implementation from [a wiki](http://en.wikibooks.org/wiki/Clojure_Programming/Examples/Lazy_Fibonacci) to lazy-array.

```javascript
(defn fib [a b] (lazy-seq (cons a (fib b (+ a b)))))


(take 5 (fib 1 1))

;; Results in: (1 1 2 3 5)
```

The Clojure implementation is very elegant, fitting for such a lovely language. When a lazy sequence is printed to the command line in Clojure it is automatically resolved, so they didn’t have to use _doall_, in the following JavaScript example I will use my equivalent to Clojure’s _doall_, _all_, just to illustrate how it would actually be expanded.

```javascript
'use strict';

var assert = require('assert');
var larr = require('..');

/**
 * Creates a lazy array that generates the Fibonacci sequence. Requires you to
 * pass in the initial numbers, probably 1 and 1.
 *
 * @param {Number} a
 * @param {Number} b
 * @return {LazyArray}
 */
function fib(a, b) {
    return larr.create(function () {
        return larr.cons(a, fib(b, a + b));
    });
}

describe('fib', function () {
    var f;

    beforeEach(function () {
        f = fib(1, 1);
    });

    it('should provide the 50th number in the sequence', function () {
        // Actually at index 49 since nth is zero indexed.
        var fib50 = 12586269025;
        assert.strictEqual(larr.nth(f, 49), fib50);
    });

    it('should provide the 10th to the 20th', function () {
        // Actually drop 9 and take 11 to get this result.
        var fib10to20 = [
            55,
            89,
            144,
            233,
            377,
            610,
            987,
            1597,
            2584,
            4181,
            6765
        ];

        var actual = larr.all(larr.take(11, larr.drop(9, f)));
        assert.deepEqual(actual, fib10to20);
    });
});
```

So that’s an actual test that I’ve just added to the project which showcases a lot of the functionality. The most interesting thing is the simplicity of the _fib_ function which creates the lazy array. You may say “well a recursive solution would also be that succinct” **but**, a recursive solution would eventually blow up since JavaScript does not have [tail call optimisation](http://en.wikipedia.org/wiki/Tail_call) ([yet](http://www.reddit.com/r/javascript/comments/162tth/javascript_es6_has_tail_call_optimization/)). Thanks to a single tiny while loop inside one of my functions, lazy arrays can just keep going. So at the very least lazy-array allows you to do sort of tail call optimised recursion in JavaScript.

## Applications within the UI

I don’t know about you, but I don’t get to write numerical sequences for a living (boo). We JavaScript frontend wranglers deal with events, networking and state all day long, so building this had me wondering if I could apply laziness to the UI domain. The more I thought about this concept and talked with colleagues about it I realised that I’m essentially heading towards [functional reactive programming](http://en.wikipedia.org/wiki/Functional_reactive_programming), with [bacon.js](https://baconjs.github.io/) as a JavaScript example.

I think it’s cool that I’m heading towards this same realm of reactivity pretty much by accident, it’s the natural progression once you’ve got the core functions down I feel. I don’t know how I could use lazy arrays to replace something such as bacon.js just yet, but we can still use laziness for more practical problems. Suppose we were building a calendar, we could model every day _ever_ as part of an infinite lazy sequence. Then we can run that through a lazy map to convert those raw _new Date()_ instances into pretty strings. Here’s another test file I added to the repository to illustrate this.

```javascript
'use strict';

var assert = require('assert');
var larr = require('..');

// Amount of milliseconds in a day.
var DAY = 86400000;

/**
 * Creates an infinite lazy array of all possible times using a given start
 * date and step timestamp. Providing a negative step will make it go
 * backwards.
 *
 * @param {Date} now
 * @param {Number} step
 */
function dates(now, step) {
    return larr.create(function () {
        var next = new Date(now.getTime() + step);
        return larr.cons(now, dates(next, step));
    });
}

describe('date', function () {
    var start = new Date('1994 Jan 27');
    var d;

    beforeEach(function () {
        d = dates(start, DAY);
    });

    it('should have the start date as the first value', function () {
        assert.strictEqual(larr.first(d).getTime(), start.getTime());
    });

    it('should allow me to skip forward some days', function () {
        var future = larr.nth(d, 3);
        assert.strictEqual(future.getTime(), new Date('1994 Jan 30').getTime());
    });

    it('should allow me to map a sequence of dates to strings', function () {
        function str(date) {
            return date.toDateString();
        }

        var days = larr.all(larr.map(str, larr.take(3, d)));
        var expected = [
            'Thu Jan 27 1994',
            'Fri Jan 28 1994',
            'Sat Jan 29 1994'
        ];

        assert.deepEqual(days, expected);
    });
});
```

As you can see, a simple function can produce some interesting and powerful results. We could quite easily generate an infinite lazy array of every Thursday for the next decade. We could filter out any date that falls on the 25th and then reduce their timestamps down to a value. I don’t know why you’d want to do that, but it illustrates the point, lazy arrays allow you to do some really difficult things very easily. You just have to know when to apply them to your problem.

## A long way to go

These are only the fundamentals really, I could carry on until I had ported the entire [Clojure seq library](http://clojure.org/sequences#toc5), which I would actually be quite happy to do. I think this could provide a powerful tool for problems that are well suited to lazy solutions, you’d just have to implement a few functions that you’re used to having to hand with other more feature rich implementations.

If this receives any interest I’d put some more time into it to flesh it out, but for now I think the basics are enough. It could also really do with some documentation, but comprehensive tests and abundant JSDoc comments will have to do for now. It wouldn’t be hard to generate API documentation from the comments and supply a bunch more examples in the readme. I’d love to hear what you think and what you could imagine using these techniques for.

Just imagine if this supported transducers and other such wonders too.
