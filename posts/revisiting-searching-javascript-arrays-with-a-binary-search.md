---
tags:
  - blog-post
  - imported-blog-post
---
# Revisiting: Searching JavaScript arrays with a binary search

Last year I wrote a post called [Searching JavaScript arrays with a binary search](/searching-javascript-arrays-with-a-binary-search/), it’s actually become quite popular and has seen a large amount of suggestions in the comments as well as helping out [PowerArray](https://github.com/techfort/PowerArray). The problem is that it’s an untested, performance chasing, mess of a function. I’ve always wanted to redo this in a more formal manner because I don’t want people using code I’ve written that will probably break on multiple edge cases.

So, I’m going to build an actual repository containing a JavaScript binary search function as well as some robust tests. Step one will be to produce a simple reference implementation with benchmarks and generative tests using [testcheck-js](https://github.com/leebyron/testcheck-js), then I will go on to continually speed up the algorithm without breaking that initial suite. I think this’ll produce something far better than my original post with the potential for more speed too. And it’s going to be bullet proof. I hope. Why would you be shooting at a searching algorithm anyway?

You can find the repository at [Wolfy87/binary-search](https://github.com/Wolfy87/binary-search).

## The baseline

So I built up a repository with tests and a function called _binarySearch_ that actually uses _indexOf_ instead. It’s lying to you, it’s not a binary search just yet. This provides us with a working example to base the tests against and a good baseline in performance. Here’s the function which basically does nothing of any significance.

```
/**
 * Performs a binary search on the provided sorted list and returns the index of the item if found. If it can't be found it'll return -1.
 *
 * @param {*[]} list Items to search through.
 * @param {*} item The item to look for.
 * @return {Number} The index of the item if found, -1 if not.
 */
function binarySearch(list, item) {
    return list.indexOf(item);
}

module.exports = binarySearch;
```

And the test file that runs in 80ms on my machine using generative testing.

```
require('mocha-testcheck').install();
var assert = require('assert');
var binarySearch = require('..');

describe('binarySearch', function () {
    check.it('can find a number in some sorted numbers', [gen.array(gen.int), gen.int], function (list, item) {
        list.sort();
        var index = list.indexOf(item);
        assert(index === binarySearch(list, item));
    });
});
```

Now to turn it into an actual binary search. During this process I tried multiple binary search implementations including one from [Khan Academy](https://www.khanacademy.org/) and a plethora of other blogs. Including mine from my previous post. The generative testing found holes in every single one, it was amazing yet terrifying. Even one apparently ported from JDK fell apart, although that’s probably the fault of the port and not in the actual JDK. I hope.

I eventually gave up with them all and went back to the implementation from Khan. It falls over on calls such as “( [0,0,0,13,2,2], 13 )” and “( [10,6], 6 )”, which is sort of bad.

## This is the point where I realised I’m an idiot and slammed my face into my desk

There wasn’t anything wrong with the search functions. The sorting of the sample arrays was wrong. I noticed it after putting in some logging for failing cases that showed numbers being inserted out of order.

```
[ -1, -10, -16, -19, -20, 11, 12, 13, 13, 13, 14, 15, 17, 6 ] 6 13 -1
[ -10, -16, -19, -20, 11, 12, 13, 13, 13, 14, 15, 17, 6 ] 6 12 -1
[ -10, -16, -19, -20, 12, 13, 13, 13, 14, 15, 17, 6 ] 6 11 -1
[ -10, -16, -20, 12, 13, 13, 13, 14, 15, 17, 6 ] 6 10 -1
[ -10, -16, 12, 13, 13, 13, 14, 15, 17, 6 ] 6 9 -1
[ -10, -16, 12, 13, 13, 14, 15, 17, 6 ] 6 8 -1
[ -10, -16, 12, 13, 13, 15, 17, 6 ] 6 7 -1
[ -10, -16, 13, 13, 15, 17, 6 ] 6 6 -1
[ -10, -16, 13, 13, 15, 6 ] 6 5 -1
[ -10, -16, 13, 15, 6 ] 6 4 -1
[ -10, -16, 15, 6 ] 6 3 -1
[ -16, 15, 6 ] 6 2 -1
[ 15, 6 ] 6 1 -1
[ 12, 6 ] 6 1 -1
[ 11, 6 ] 6 1 -1
[ 10, 6 ] 6 1 -1
```

[![quadruple-facepalm](/assets/legacy-images/2014/12/quadruple-facepalm.jpg)(/assets/legacy-images/2014/12/quadruple-facepalm.jpg)]So using _list.sort()_ in the tests wasn’t safe, amazingly. I guess it uses string comparison or something crazy like that by default. **Thanks JavaScript!** So I ended up with this binary search from my Khan academy attempt.

```
/**
 * Performs a binary search on the provided sorted list and returns the index of the item if found. If it can't be found it'll return -1.
 *
 * @param {*[]} list Items to search through.
 * @param {*} item The item to look for.
 * @return {Number} The index of the item if found, -1 if not.
 */
function binarySearch(list, item) {
    var min = 0;
    var max = list.length - 1;
    var guess;

    while (min <= max) {
        guess = Math.floor((min + max) / 2);

        if (list[guess] === item) {
            return guess;
        }
        else {
            if (list[guess] < item) {
                min = guess + 1;
            }
            else {
                max = guess - 1;
            }
        }
    }

    return -1;
}

module.exports = binarySearch;
```

And these tests.

```
require('mocha-testcheck').install();
var assert = require('assert');
var binarySearch = require('..');

describe('binarySearch', function () {
    check.it('can find a number in some sorted numbers', [gen.array(gen.int), gen.int], function (list, item) {
        list.sort(function (a, b) {
            return a - b;
        });

        var base = list.indexOf(item);
        var result = binarySearch(list, item);
        assert(list[base] === list[result]);
    });
});
```

## Now it’s safe

I am free to change the implementation now since I’m happy with the test suite (despite it subtly stabbing me in the back). So I can add in every crazy optimisation under the sun, but to be able to tell that it actually improved I’ll need some benchmarks. I’m going to use [Benchmark.js](http://benchmarkjs.com/).

```
var binarySearch = require('..');
var sample = require('./sample');

module.exports = {
    name: 'binarySearch on 1000 items ranging from -100 to 100',
    tests: [
        {
            name: 'First',
            fn: function () {
                binarySearch(sample, -100);
            }
        },
        {
            name: 'Last',
            fn: function () {
                binarySearch(sample, 100);
            }
        },
        {
            name: '~25%',
            fn: function () {
                binarySearch(sample, -51);
            }
        },
        {
            name: '~50%',
            fn: function () {
                binarySearch(sample, 3);
            }
        },
        {
            name: '~75%',
            fn: function () {
                binarySearch(sample, 52);
            }
        }
    ]
};
```

Which produced this nice little output for me to compare against in the future.

```
binary-search$ gulp benchmark
[23:31:53] Using gulpfile ~/Documents/code/javascript/binary-search/gulpfile.js
[23:31:53] Starting 'benchmark'...
[23:31:53] Running suite binarySearch on 1000 items ranging from -100 to 100 [/home/oliver/Documents/code/javascript/binary-search/benchmark/binarySearch.js]...
[23:31:59]    First x 26,740,056 ops/sec ±0.40% (99 runs sampled)
[23:32:04]    Last x 28,211,591 ops/sec ±0.84% (94 runs sampled)
[23:32:10]    ~25% x 27,309,183 ops/sec ±0.11% (103 runs sampled)
[23:32:15]    ~50% x 51,699,650 ops/sec ±0.56% (97 runs sampled)
[23:32:21]    ~75% x 44,993,017 ops/sec ±0.52% (96 runs sampled)
[23:32:21] Fastest test is ~50% at 1.15x faster than ~75%
[23:32:21] Finished 'benchmark' after 27 s
```

I could go through inserting random optimisations now safe in the knowledge that I’ll be able to see improvements and I won’t break anything, but it’s almost midnight and I want to publish this tomorrow morning. Feel free to hack around in [the repository](https://github.com/Wolfy87/binary-search) and make it blisteringly fast without breaking anything.
