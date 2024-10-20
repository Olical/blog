---
tags:
  - blog-post
  - imported-blog-post
---
# Portable JavaScript

I gave in to AMD. I am currently using [RequireJS](http://requirejs.org/) on a project which uses quite a few different scripts including [MooTools](http://mootools.net/), [Kalendae](https://github.com/ChiperSoft/Kalendae) (which is amazing by the way) and the [Facebook SDK](https://developers.facebook.com/docs/reference/javascript/). By using AMD I have separated the whole project into manageable modules which are loaded in parallel with each other. I am loading MooTools and the Facebook SDK from their respective CDNs too for an even faster site.

While writing this I realized that everything I was using had to be accessed in different ways. By this I mean MooTools is in the global object along with Kalendae and the `FB` object, but every AMD module I wrote was completely localized and passed it’s self around without going into the global object. If you think about it there are three ways to get your lovely class back to they script that requires it. The most used is probably the global object, stick a value in there and everything can see it. I will let you draw your own conclusions about how good that is.

The other two methods are CommonJS modules and AMD. To define and pass back your class with AMD you would use the following.

```
// Your classes code
define(function() {
    function SomeClass() {
        // ...
    }

    return SomeClass;
});

// The code that requires it
define(['SomeClass'], function(SomeClass) {
    var s = new SomeClass();
});
```

That example involves no global object pollution but still gets the job done and is my favorite method by far. A similar effect can be seen with CommonJS modules which are used for almost everything based on node.js.

```
// Your classes code
function SomeClass() {
    // ...
}

module.exports = SomeClass;

// The code that requires it
var SomeClass = require('SomeClass');
var s = new SomeClass();
```

CommonJS modules may seem a bit simpler but I still think AMD is better for browsers due to its ability to load scripts in parallel. CommonJS is more suited to things like node.js in my opinion. Now what if you want your script to work everywhere and be truly portable. You are going to need to export to all three places. The global object, AMD and CommonJS. In the following example I am using the function wrapper discussed in [my previous article](/writing-great-javascript.html).

```
;(function(exports) {
    function SomeClass() {
        // ...
    }

    // Expose your class
    if(typeof define === 'function' && define.amd) {
        // AMD module
        define(function() {
            return SomeClass;
        });
    }
    else if(typeof module === 'object') {
        // CommonJS module
        module.exports = SomeClass;
    }
    else {
        // Global browser object
        exports.SomeClass = SomeClass;
    }
}(this));
```

With this at the bottom of your script it can be loaded in anyway your user desires. If you want you could take the global object definition (the last one) out of the else statement so that even if something like RequireJS was on the page you could still access it through the global object.
