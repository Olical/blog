---
tags:
  - blog-post
  - imported-blog-post
---
# Searching JavaScript arrays with a binary search

I migrated from Octopress back to WordPress and all of my code snippets exploded, I’m in the process of fixing those, please bear with me. This post also has huge improvements detailed in the comments section **that makes me think this post warrants a revisit, I’ll link to it from here if so**. **I’ve written a [new post](/revisiting-searching-javascript-arrays-with-a-binary-search/)!** This one includes benchmarks and generative testing (that all passes!). So you can be sure that it works and that any changes made do actually make it faster. This post is therefore **deprecated**. The repository for the new implementation can be found at [Olical/binary-search](https://github.com/Olical/binary-search).

'''''

We are asking browsers to do more and more as they become more capable. I’m not sure if that’s a good thing or not, but that’s a heated flame war that should be saved for another day.

There may come a time in your browser (ab)use in which you need to search a ridiculous amount or values within a cat gif processing platform. Or maybe you need to search through all of the elements in a bloated DOM.

You will probably find that `indexOf` doesn’t quite cut it in those situations. In fact, if you _do_ happen survive the inevitable violent explosion caused by your CPU trying to take the easy way out, you might want to try a JavaScript search method that is better suited for that much data.

This is where you have an excuse to suggest a binary search and blow everyone else’s minds.

## What is it

A binary search searches by splitting your array into smaller and smaller chunks until it finds your desired value. Unlike the normal `indexOf` which searches from left to right in a simple iteration. [The binary search Wikipedia article](http://en.wikipedia.org/wiki/Binary_search_algorithm) explains it best (as always). There are a couple of downsides; It will be slower with smaller data sets (this needs proving) and the array you are searching **needs to be sorted**.

Because a binary search is [O(log n)](http://en.wikipedia.org/wiki/Logarithmic_time#Logarithmic_time), and not O(n) like `indexOf`, it’s great for large sets of data. I set up a little [jsPerf for my version](http://jsperf.com/binaryindexof-and-indexof) of the JavaScript implementation; it is searching **100,000** numbers.

The source for my [JavaScript binary search implementation](https://gist.github.com/Wolfy87/5734530) is currently held in a gist. The really cool thing about this is that it’s only 138 bytes when minified, that’s tiny enough to fit inside a tweet.

```
/**
 * Performs a binary search on the host array. This method can either be
 * injected into Array.prototype or called with a specified scope like this:
 * binaryIndexOf.call(someArray, searchElement);
 *
 * @param {*} searchElement The item to search for within the array.
 * @return {Number} The index of the element which defaults to -1 when not found.
 */
function binaryIndexOf(searchElement) {
    'use strict';

    var minIndex = 0;
    var maxIndex = this.length - 1;
    var currentIndex;
    var currentElement;

    while (minIndex <= maxIndex) {
        currentIndex = (minIndex + maxIndex) / 2 | 0;
        currentElement = this[currentIndex];

        if (currentElement < searchElement) {
            minIndex = currentIndex + 1;
        }
        else if (currentElement > searchElement) {
            maxIndex = currentIndex - 1;
        }
        else {
            return currentIndex;
        }
    }

    return -1;
}
```

_Edit: I’ve swapped `Math.floor` for `number | 0` at [Yehonatan’s reccomendation](/searching-javascript-arrays-with-a-binary-search/#comment-924876342). [It’s faster sometimes](http://jsperf.com/jsfvsbitnot/8)._

## Real world use case

Okay, you might not want to search an array of numbers. Say you were working on a JavaScript map implementation, as I will be talking about in my next post, you might need to search an array of objects. As long as this array contains some kind of number you can use as an index whilst sorting, then it can be done.

Say we have a `Model` class that each have a numerical ID or publish time. The first thing you would need to do is sort them. You can either do this with a sorting function like this…

```
models.sort(function (a, b) {
    return a.id < b.id ? -1 : 1;
});
```

Or you can do it the smart way which kills two birds with one stone. You define a [`valueOf` method](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/valueOf) which returns the ID. This value can then be used by the default sort method and, more importantly, within the `binaryIndexOf` method which needs the comparison operators to work correctly in order to find things.

```
Model.prototype.valueOf = function () {
    return this.id;
};

models.sort(); // Smooth!
```

So now that your models are sorted and their greater than and less than comparison operators will work, you can use the `binaryIndexOf` method on it to search through your 100,000 models. Why are you even doing this to a browser, you monster.

```
// You can either execute it with call like this:
var index = binaryIndexOf.call(models, someModel);

// Or you can inject it into the prototype of Array to have a more native feel.
Array.prototype.binaryIndexOf = binaryIndexOf;
var index = models.binaryIndexOf(someModel);
```

Let me know what you think and if you have any improvements or suggestions. I hope this can help to destroy a bottleneck or two (or 100,000).

## Edit: Where should you stick it?

[doomslice over on reddit](http://www.reddit.com/r/javascript/comments/1fx4od/searching_javascript_arrays_with_a_binary_search/caeo5is) suggested that I return the [twos compliment](http://en.wikipedia.org/wiki/Two%27s_complement) in place of -1. As far as I can tell, this means returning the negative version of the last place that was checked. So if you wanted to insert a value and wanted to know where you should put it, you could run the function and use the returned number to splice the value into the array.

This way you can add items without ruining the order. You don’t exactly want to go re-sorting potentially thousands of values every time you add something. Here’s the modified function I came up with, as well as a working example. It demonstrates finding where to insert an element and then inserting it.

As you can see, because I use the bitwise NOT operator (`~`, also suggested by doomsice!) on the index during the splice, it will always insert the element in the right place, even if there is an identical value already there! Pretty cool. You do have to be careful though, now you are going to need to check for `index < 0`, and not `index === -1` when you are looking for existence of a value.
