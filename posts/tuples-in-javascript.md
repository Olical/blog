---
tags:
  - blog-post
  - imported-blog-post
---
# Tuples in JavaScript

As my last post mentioned, I recently read [Functional JavaScript](http://shop.oreilly.com/product/0636920028857.do) and can’t physically recommend it enough. This book has caused me to make the mental leap between real world applications and functional programming due to the fact that it’s written in a language I live and breath.

After reading this I was inspired to pick up [Haskell](http://www.haskell.org/) from where I left off last time; a “Hello, World!” application. I’ve since started to work through [Learn You a Haskell for Great Good!](http://learnyouahaskell.com/), which is yet another excellent book. Reading this has caused me to think about certain programming things we don’t have in JavaScript.

One of the (many) things I saw in Haskell that I couldn’t think of an alternative to in JavaScript were [tuples](https://en.wikipedia.org/wiki/Tuple). Sure you can use an array, but it won’t have a fixed size or pseudo named properties. I say pseudo because they are not named, like an array, but they can easily be mapped to names, like an object. It seems to work like a middle ground between the two in my opinion.

So, I set about writing a tiny tuple implementation in JavaScript; this is what I came up with.

## The base class definition

For starters, I needed a simple class definition that created the tuple instance, set it’s size and stored some initial data.

```
function Tuple(/* values */) {
    this._store = new Array(arguments.length);
}
```

By calling `new Tuple(...)` you can now define a tuple that creates an internal storage array of the appropriate length. Now we need a function that sets those values, that function also needs to be called from the constructor.

```
function Tuple(/* values */) {
    var args = Array.prototype.slice.call(arguments, 0);
    this._store = new Array(args.length);
    this.pack.apply(this, args);
}

Tuple.prototype.pack = function pack(/* values */) {
    var store = this._store;
    var i = store.length;

    while (i--) {
        store[i] = arguments[i];
    }

    return this;
};
```

Now the arguments you pass to the constructor are sliced into an array and pushed onto the `pack` method which bundles them into the storage array. The pack method simply iterates over it’s arguments and assigns each one to it’s place in the storage.

## Getting the data out

It’s all well and good dumping values into a fixed size tuple, but how do you get them out? Better still, how do you get them out in a Haskell-ish way the will allow you to assign each value to a variable of your choice?

```
Tuple.prototype.unpack = function unpack(callback) {
    return callback.apply(this, this._store);
};
```

When you call unpack all of the values in the storage array are passed to your callback function for you to do with as you please. That means you can assign each value to any name you want; `x`, `y` and `z` for example. This is inspired by pattern matching a tuple in Haskell and unpacking the values into variables of your choosing.

So with the current code, you could do something like this.

```
var box = new Tuple(10, 20, 15);

var volume = box.unpack(function (x, y, z) {
    return x * y * z;
});

console.log(volume); // 3000
```

This creates a tuple that contains three numbers: A width, height and depth of a box. We can then use `unpack` to assign each value to a variable, do some stuff with them and return their result. You could recreate [Haskell’s `fst` and `snd` functions](https://en.wikibooks.org/wiki/Haskell/Lists_and_tuples#Example:_fst_and_snd) fairly easily with similar code.

```
function fst(tuple) {
    return tuple.unpack(function (first, second) {
        return first;
    }):
}

function snd(tuple) {
    return tuple.unpack(function (first, second) {
        return second;
    }):
}
```

I would not advise using anonymous functions like I have done above in production. Please place them into stand alone functions that are not declared every time. I have only written them like that for brevity.

## Integration with other tuples and types

One thing you can add to make it behave slightly more like a native type is to add a `toString` method.

```
Tuple.prototype.toString = function toString() {
    return ['(', this._store.join(', '), ')'].join('');
};
```

You can take this a little further by adding a `valueOf` method that will even allow you to concatenate or add together multiple tuples, albeit in a rudimentary way.

```
Tuple.prototype.valueOf = function valueOf() {
    var store = this._store;
    var storeLength = store.length;
    var total = store[0];
    var i;

    for (i = 1; i < storeLength; i += 1) {
        total += store[i];
    }

    return total;
};
```

Now with a `valueOf` method you can do stuff like this…

```
var p1 = new Tuple(10, 20);
var p2 = new Tuple(40, 50);

console.log(p2 > p1); // true
console.log(p1 + p2); // 120
```

So now you can create a tuple type which sits in between an object and an array which has some pretty cool arithmetic and concatenation methods due to `valueOf`. Hopefully you can find this useful. If there was actually any demand for it then I guess I could dump this into a repository with some unit tests too. Maybe I’m the only one that finds this kind of idea to JavaScript porting interesting though.

You can play about with my code in [this fiddle](http://jsfiddle.net/Wolfy87/nuGWQ/).

## Edit

Due to a sudden influx of views and opinions, I have decided to create an actual project based off of this idea; albeit a very simplified one. It can be found in the repository, [Wolfy87/tuple](https://github.com/Wolfy87/tuple). I have finished and published the first release tonight and you can grab it from GitHub, Bower (`bower install tuple`) and npm (`npm install tuple-w`, the name “tuple” was taken).

You will notice that this solidified version is a lot simpler though. There is no `valueOf` and there is no `pack` method. Once you have set the values with the constructor they are set forever. The object is now immutable (or as immutable as JavaScript can be) and can only be set once and then read. Exactly how a tuple should be.

Hopefully this is closer to what most people expect from a tuple, although I know it is still a **very** long way off due to the limitations of JavaScript. Regardless, enjoy!
