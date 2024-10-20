---
alias: writing-great-javascript
tags:
- blog-post
- imported-blog-post
---


I probably could have named this post something like “Writing clean, validating and portable JavaScript”, but that would be no where near as catchy. The problem with “great” is it means different things to different people. I am going to show you my idea of great which may differ from many developers views, but I hope it helps someone improve their code.

So what’s the point in this, why can’t you just carry on writing JavaScript as you have been for ages. It works doesn’t it? Well if you took the same opinion with a car and drove on 20 year old tires which will give out any day you are just asking for something to go horrifically wrong. Want an example? Here you go.

## What could possibly go wrong

You are writing something that manipulates color in some way. Maybe it is a color pallet tool, some form of UI selector element that allows you to set the background of your twitter style profile. You have some code like this in one of your scripts.

```
var color = '#FFFFFF';

colorPicker.addEvent('change', function(selected) {
    color = selected;
});

userSettings.addEvent('save', function() {
    this.saveSetting('profile-background', color);
});
```

So as you move your cursor across the UI element picking your color, the variable `color` is updated. When you hit save the contents of that variable are saved in some way, maybe by posting it back to your server in a call to `XMLHttpRequest` (that’s `ActiveXObject('Microsoft.XMLHTTP')` to silly old Internet Explorer). Now you decide that you want more color capabilities at some point in your script. So you include a micro library called “color.js” which creates the global variable `color`. You can see where I am going with this.

Now your color string has been replaced by a libraries object. Hello bugs and time you did not need to spend. Obviously you could fix this by renaming every occurrence of `color` or you could use a function wrapper to sandbox your code.

```
;(function() {
    var color = '#FFFFFF';

    colorPicker.addEvent('change', function(selected) {
        color = selected;
    });

    userSettings.addEvent('save', function() {
        this.saveSetting('profile-background', color);
    });

    // typeof color === 'string'
}());

// typeof color === 'undefined'
```

And now the color variable is kept in the scope of our anonymous function, not the global object. Thus stopping the global `color` object conflicting with your string. You may be wondering what this mash of characters actually does, it is pretty simple actually. The initial semi-colon saves you from people that miss out the last semi colon in their script when you concatenate files. Without it you may end up with a situation in which you basically wrote this.

```
var myEpicObject = {} someFunction();
```

Obviously that will throw an error, `}` followed by `s` does not make sense in JavaScript. The other parts of our wrapper, `(function() {` and `}());`, simply wrap our code in an anonymous function which is called instantly. It is pretty much the same as writing this.

```
function main() {
    // YOUR CODE
}

main();
```

The only difference is that `main` will now be in the global namespace, whereas the other method does not pollute anything.

## Portability

It is pretty standard for the newer JavaScript libraries to work on both browsers and servers with the same code these days. But how can you write something that will run in Chrome, Firefox and node.js in my terminal? First you place your code in the wrapper as shown above, then you simply create an alias to the global variable of your current environment.

```
;(function(exports) {
    // First you define your class
    function SomeClass() {
        // code...
    }

    SomeClass.prototype.foo = function() {
        // code...
    };

    // And then you expose it
    exports.SomeClass = SomeClass;
}(this)); // <-- this = the global object is passed as exports
```

This will allow compressors such as [UglifyJS](https://github.com/mishoo/UglifyJS/) to minify your code better, will keep any helper functions and variables private and will allow you to expose what you choose to the global object. So with the code above you could then use the class like so.

```
// This is only required for server side environments such as node.js
// In the browser you would use a script tag to load it
var SomeClass = require('someclass').SomeClass;

// Then you can call the class like this
var foo = new SomeClass();
```

## Validation

If you haven’t already I insist you read [JavaScript: The Good Parts](http://www.amazon.co.uk/JavaScript-Good-Parts-Douglas-Crockford/dp/0596517742). Alongside that I urge you to run all of your code through the amazing validation tool that is [JSHint](http://www.jshint.com/). [JSLint](http://www.jslint.com/) will be referred to in the aforementioned book, but don’t use that, it will be mentioned because they are written by the same man, [Douglas Crockford](http://www.crockford.com/). JSHint is a much better fork of JSLint. This tool will show you any problems with your code. Some are purely stylistic, some will fix huge bugs. It will point out extra commas in arrays that will cause IE to complain and help you speed up your code.

I recommend ticking almost **every** box on the JSHint site, use your common sense with the last few on the far right (i.e. don’t tick jQuery unless you are using it), and adding `/**jshint smarttabs:true**/` to the top of your document, if you use JSDoc style function comments that is. Now if you run your code through that, I am sure you will get at least one error, that will probably be “Missing “use strict” statement.“ which is simple to fix. Just add ’use strict';` at the top of your function wrapper like this.

```
/*jshint smarttabs:true*/

;(function(exports) {
    'use strict';

    // code...
}(this));
```

If you follow the guidelines laid down by The Good Parts and JSHint you will find and fix so many errors before they bite you in the…

## Thanks

This post turned out a lot longer and wordier than I first intended. That seems to happen a lot with my posts. I hope you have learned something from it though and I hope it has helped you to write better JavaScript which is **much** less prone to errors.

Thanks for reading!
