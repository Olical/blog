---
alias: working-with-multiple-of-lists-in-javascript
tags:
- blog-post
- imported-blog-post
---


If you’ve ever had to iterate over multiple lists at the same time or map a filtered map of a map, you’ll understand that nesting all of those blocks and callbacks isn’t very easy to work with or understand.

Luckily, there’s some cool techniques that make turning several arrays (or infinite sequences created by generators, for example) into a single array with a concise and powerful syntax. I’m going to take this rather large list of [my public gists](https://gist.github.com/Olical/fa1c29fdfa42b52604f5) and turn it into a list of all file names with the type set to “JavaScript”. I’m using lodash for convenience.

```
_.map(_.filter(_.flatMap(gists, (gist) => {
  return _.values(gist.files)
}), (file) => {
  return file.language === 'JavaScript'
}), (file) => {
  return file.filename
})
```

Which yields the following array.

```
[ 'lazyArray.js',
  'thebutton.js',
  'formatNumberWithCommas.js',
  'x-example.js',
  'what-we-do.js',
  'compile.js',
  'compile.spec.js',
  'factory.js',
  'harvest.js',
  'main.js',
  'e.js',
  'example.js',
  'EventEmitter.js',
  'api.js',
  'example.js',
  'graphs.js',
  'colours.js' ]
```

You could extract parts of this into named variables or move it into other functions, but my point being, something fairly simple requires a lot of juggling. If you want to add any more maps or filters in the middle you have to constantly move arguments around. It’s not easy to change and adapt.

We can however completely eliminate the need for this nesting by using something called list comprehension.

## List comprehension

My favourite implementation of list comprehension can be found in my favourite language, [Clojure’s for](https://clojuredocs.org/clojure.core/for). You can also find them in many other languages such as Python and Scala, I’m just in love with [all those parenthesis](https://xkcd.com/297/).

JavaScript even _had_ one planned for ES6 although it was pulled from the specification, I’m not sure why. [Firefox had something like the ES6 one](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Array_comprehensions) implemented for a while, but that was non-standard at the time. If it’s any consolation, I didn’t like the syntax anyway.

They allow you to produce a single list from one statement or call that accepts multiple lists alongside some parameters. You should, in theory, be able to reference between those lists and define intermediate values from those lists as they iterate through. You then have a function which is called with singular values from those lists, what it returns becomes your final list.

I’ve built one for JavaScript that is a port of the Clojure implementation, I’ve tried to keep it as close to the original as possible. Including code-as-data based API and laziness all the way down. You can use plain old arrays or infinite generators, it will accept _any_ [iterable](https://developer.mozilla.org/en/docs/Web/JavaScript/Reference/Iteration_protocols).

## Introducing https://github.com/Olical/forc[forc] https://badge.fury.io/js/forc[image:https://badge.fury.io/js/forc.svg[npm version,height=18]]

The name stems from “for comprehension” or maybe even “for Clojure”, but mainly because I obviously can’t use just “for” as a name. It allows you to do everything Clojure’s for does, but with JavaScript and any kind of iterable. It’s written with all sorts of ES6y things and I’d recommend using it with Babel, but it will work without them (I compile it at publish time). Pre-ES6 you won’t have any way to iterate the iterables it generates easily, so I wouldn’t recommend that.

Here’s an example of infinite sequences from the README.

```
// An infinite generator of all natural numbers
function * numbers () {
  let n = 0

  while (true) {
    yield n++
  }
}

forc([
  'n', numbers(),
  ':let', ['square', ({n}) => n * n],
  ':while', ({square}) => square < 100
], ({square}) => square)

// Results in only those whos square is < 100
// [0, 1, 4, 9, 16, 25, 36, 49, 64, 81]
```

The API will look strange to anyone that hasn’t used the Clojure version, but it’s extremely powerful. The declarative array based approach allowed me to do some cool rebinding of values as I walk through the instructions. I’ve seen others do this with long chains of method calls or evals instead, both of which don’t come close to Clojure’s elegance, in my opinion. This library accidentally adds a little lisp to JavaScript, [again](https://github.com/Olical/clojs). I can’t help it.

Now let’s solve the initial problem with [forc](https://github.com/Olical/forc).

```
forc([
  'gist', gists,
  'file', ({gist}) => _.values(gist.files),
  ':when', ({file}) => file.language === 'JavaScript'
], ({file}) => file.filename)
```

Pretty succinct, right? I mean, I think so. It’s a shame the “_.values” is required because “gist.files” is an object but that’s just the shape of the data. An object is not an iterable, maybe I could change the API in the future to automatically extract values from objects. This call produces a generator which contains the same contents as the initial call, you can expand it with “[…result]”.

The cool thing about it being a generator is that it only calculates the values you pull through, this allows you to use infinite iterators, for example. You can even use a generator created by “forc” as an argument for “forc”, it sill won’t execute a thing until you pull the values through by resolving the iterator.

Feel free to check out the README and tests to get a better idea of the capabilities. The Clojure documentation is also pretty good since I’ve copied the API.

I hope you find this useful!
