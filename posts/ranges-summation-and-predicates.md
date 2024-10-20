---
tags:
  - blog-post
  - imported-blog-post
---
# Ranges, summation and predicates

I’ve been reading a book called [Functional JavaScript](http://shop.oreilly.com/product/0636920028857.do) by [Michael Fogus](http://blog.fogus.me/) which, surprisingly, is all about applying functional programming to JavaScript. The majority of the book depends upon [Underscore](http://underscorejs.org/) to make JavaScript have a more functional feel, but on the whole it’s fairly vanilla.

I have since started trying to apply functional concepts to problems I have come across. This post will detail my approach to solving a small challenge inspired by a [Project Euler](https://projecteuler.net/) problem. It may not be the most efficient imperative solution but it should be quite elegant in nature.

The problem I wish to solve is this: Find the product of all numbers within an array that pass a specific test. In this case I will be finding the sum of all _even_ numbers within a specific range.

## Decimating it with Underscore

Okay, this might not be a vanilla solution, but _wow_ does it look pretty.

```
var result = _.reduce(_.filter(_.range(1, 11), isEven), add);

function add(a, b) {
    return a + b;
}

function isEven(value) {
    return value % 2 === 0;
}

result; // 30
```

This adds every even value in the range of 1 to 10 together using minimal and functional JavaScript. I have defined a couple of functions to help it along the way; `isEven` is a predicate that is used to extract all even numbers through the `_.filter` function and `add` is used by `_.reduce` to add the even values together.

`_.reduce` works by passing each of the values in an array to a function (in this case: `add`) and having that function do something with two arguments. The first is the previous value carried over from the last step in the reduce process; the second is the next value in the array ready for adding to or manipulating the carried value with.

This can be made a bit more functional and probably a bit more readable by doing something like this.

```
var even = partialRight(_.filter, isEven);
var summation = partialRight(_.reduce, add);
var upTo = _.partial(_.range, 1);

var result = summation(even(upTo(11)));

function add(a, b) {
    return a + b;
}

function isEven(value) {
    return value % 2 === 0;
}

function partialRight(target) {
    var args = _.rest(arguments);

    return function () {
        return target.apply(null, _.toArray(arguments).concat(args));
    };
}

result; // 30
```

The main difference is that I am now creating functions with other functions. The end result is a summation of all even numbers up to, but excluding, 11; identical to the original code, just more abstraction in the form of reusable functions.

I don’t know about you, but I think the line that actually calculates the result clearly expresses it’s intent without need for comments or any other sort of explanation. It took me a while to stop thinking in a solely imperative way, but I understood the high level side of functional JavaScript pretty quickly. I’m only now beginning to grasp the thought processes involved in writing it. It feels like unlearning bad habits, I love it.

## The imperative equivalent

I thought it would be worth showing you a quick and dirty imperative equivalent without the use of Underscore; just in case you’re finding it hard to grasp these weird functional concepts.

```
var range = createRange(1, 11);
var result = 0;
var i;

for (i = 0; i < range.length; i += 1) {
    if (range[i] % 2 === 0) {
        result += range[i];
    }
}

function createRange(from, to) {
    var result = [];
    var i;

    for (i = from; i < to; i += 1) {
        result.push(i);
    }

    return result;
}

result; // 30
```

I personally prefer the functional method because you end up with a toolbox of useful functions and a _beautiful_ high level API that you can just skim over and quickly see what’s going on. The fact that the functional version hides the use of iteration makes me feel all warm inside too.

Functional programming seems to be for humans, once you stop thinking in an imperative way, imperative programming appears to be better for the computer.

## In summary

Using functional programming techniques where appropriate can result in an incredibly clean and reusable API where the imperative version would probably encounter spaghettification.

I encourage you to go out and read [Functional JavaScript](http://shop.oreilly.com/product/0636920028857.do) and have a play with my code on [JSFiddle](http://jsfiddle.net/Wolfy87/2fv3b/). You can take so much away from this side of programming even if you never use it directly in your day to day work. It should influence your decisions slightly in a _very_ good way.
