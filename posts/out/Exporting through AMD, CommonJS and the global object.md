---
alias: exporting-through-amd-commonjs-and-the-global-object
tags:
- blog-post
- imported-blog-post
---


No matter how amazing your script is, you will need to expose it to the wider world at some point. By that I don’t mean publication through NPM or any other package managers, I mean you need to actually expose your classes and functions to the code that needs to consume them.

You may be using AMD, CommonJS or the global object to do this, but are you using all of them at the same time? Because it’s actually quite possible, I use the same technique in [my EventEmitter project](https://github.com/Wolfy87/EventEmitter/blob/ae0c5099bd8f08a61f70a0ebc39b32a2ce52ddb0/EventEmitter.js#L425-L436). If you set this up right, your users will be able to hook into your code in any way they choose.

## The target

This little snippet is the kind of code my following code was designed for. It’s a single class that simply exposes by leaking into the global name space.

```
function Foo(result) {
    this._result = result;
}

Foo.prototype.get = function get() {
    return this._result;
};

function someHelper() {
    // ...
}
```

You will also notice how I have named my `get` method in two places. This is so you can call it with `.get()`, as usual, but it will show up as a function called `get` if you were trying to debug something that called it. The usual practice is to assign anonymous functions which makes it harder to work out what is going on at a glance.

Another thing to note is that my helper function, aptly named `someHelper`, is also leaking into the global name space. That’s awful! Well, presuming it’s meant to be a private method.

## Selectively exposing globally

You can stop any of your functions, variables or classes from leaking by wrapping them in an anonymous function call like this.

```
(function () {
    // YOUR ORIGINAL CODE HERE
}.call(this));
```

By calling the anonymous function with with `call(this)` it sets the `this` variable within the anonymous function to that of the global name space. This would be `window` in a browser. This is assuming the anonymous function wrapper is defined within the global scope.

With that set up you can selectively expose the values you want to without leaking anything.

```
(function () {
    // YOUR ORIGINAL CODE HERE

    this.Foo = Foo;
}.call(this));
```

Now only the `Foo` class is exposed. You can have as many local methods or variables as you require without creating a pile of rubbish that floats around in the `window` object for all eternity.

## Adding AMD into the mix

Global objects are all well and good, but AMD (or any other module system) is much better. You can modify your original selective exposing code to send your class out through AMD very easily.

```
(function () {
    // YOUR ORIGINAL CODE HERE

    if (typeof define === 'function' && define.amd) {
        define(function () {
            return Foo;
        });
    }
    else {
        this.Foo = Foo;
    }
}.call(this));
```

As you can see, I have just added a check for the `define` function, part of the AMD API, and then made sure it is from an AMD library and not a naming collision by looking for the `define.amd` flag. If this check fails and there is no AMD library present it will fall back to exposing through the global object.

You could tweak this so that your code is _always_ exposed globally and add AMD support along side it; I personally think that if someone has AMD on the page, they will probably load all AMD compatible scripts with it. If you are loading through AMD you don’t really want things leaking into the global name space.

## Don’t forget CommonJS!

Global objects and AMD work brilliantly in the browser, but you will probably want to support platforms such as node.js if you can. It’s really easy to add support for node’s module system. We just need to add another case to our exposing if statement.

```
(function () {
    // YOUR ORIGINAL CODE HERE

    if (typeof define === 'function' && define.amd) {
        define(function () {
            return Foo;
        });
    }
    else if (typeof module !== 'undefined' && module.exports) {
        module.exports = Foo;

        // Or maybe: module.exports.Foo = Foo;
        // It's up to you really.
    }
    else {
        this.Foo = Foo;
    }
}.call(this));
```

The CommonJS approach is very similar to the AMD one. We check for a global variable and then confirm our suspicions that it is definitely the system we were looking for.

I am exposing my class by replacing the entire exports object because I only have one value to export, you might want to just add your class to the exports object as I have shown in the comment above. This is the route you need to take if you need to expose multiple classes, functions or variables in a name space format.

Now your code should be exposed via the global name space for loading through `<script>` tags, AMD for modules in the browser and CommonJS for modules within platforms such as node.js.

## For larger AMD projects

If you have multiple classes that you need to expose across one large AMD project you will probably be better off having a stub script that depends on every file in your project. This script can expose your classes as I have done above. You can flatten that script with [r.js](http://requirejs.org/docs/optimization.html), and you can add a minimal AMD shim by including [almond](https://github.com/jrburke/almond).

Hopefully you will all find it a little easier to expose your code on multiple platforms now.
