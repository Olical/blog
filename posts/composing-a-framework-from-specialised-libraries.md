# Composing a framework from specialised libraries

Whenever I dive into a frontend MVC project of my own I always end up getting frustrated at the framework selection stage. I quite like aspects of Backbone et al, but none of them feel like that exact thing I’m looking for. This leaves me with three choices; put up with the little things that annoy me about a given framework, write my own _or_ compose my own from smaller libraries.

I find this last approach **really** appealing and I’ll probably put it into practice soon. One of the many reasons that I am drawn to composing my own framework (possibly on a per-project basis) is the similarities between that and the Unix philosophy.

> This is the Unix philosophy: Write programs that do one thing and do it well. Write programs to work together. Write programs to handle text streams, because that is a universal interface.
>
> **Doug McIlroy** [The Unix Philosophy](https://en.wikipedia.org/wiki/Unix_philosophy)

I want my router, MVC and templating modules to do one thing and do it well.

## Selecting frameworks

It didn’t take me very long at all to find these frameworks and libraries. If you know what you need then you just have to find the most used and best supported project in that field. The things I decided I would need were: MVC classes, routing, templating, file loading (including templates and JavaScript modules) and a bucket load of little helpers.

When you are starting out your project you can have a think about what you actually need. Maybe you don’t need templating or everything will be within one file. You can just drop dependencies as you see fit. No bloat, no getting tied in to massive code bases.

All you need is Google, GitHub and an idea of what you are building.

## MVC

I decided I wanted something that focussed purely on the MVC structure and nothing else. I also wanted something that worked through AMD and kept opinions to a minimum. This is why I settled on [Maria](http://peter.michaux.ca/maria/) which also mentions that it is the Gang Of Four MVC framework; that’s something I find very attractive.

So this library can be loaded via AMD, it’s pure and simple MVC and it lets you build your application exactly how you want. It seems to lack opinions which is nice for this kind of thing; it would be awful if one library said “you must use this directory structure” and another suggested something entirely different.

## Routing

This one was very easy to select (surprisingly). I stumbled across [Crossroads.js](http://millermedeiros.github.io/crossroads.js/) almost immediately and it was obvious that this was what I was looking for. It happens to work in a similar way to [Django](https://www.djangoproject.com/) which is a massive bonus in my opinion. I can already tell that it will have a robust and sane approach to routing complex URLs.

Yet again, it’s also agnostic to pretty much everything, so we can plug it into our beautiful Frankenstein framework easily without having to adapt it or include massive and unnecessary dependencies. It will __just work__^TM^.

## Templating

Templates have always been an area for flame wars, mass debates and the occasional rage induced murder. I made my choice based on AMD compatibility and simplicity; I selected [mustache.js](https://github.com/janl/mustache.js) **PLEASE DON’T HURT ME – REMAIN CALM – I’M SURE YOUR FAVOURITE TEMPLATING IS COOL TOO**.

When I combine this with the [requirejs-mustache](https://github.com/jfparadis/requirejs-mustache) plugin you get a wonderful templating experience. Not only will it load your templates easily through AMD but it will also compile them and bundle them into your final code when you flatten everything through the optimiser. You can’t really get any better than that; it does pretty much everything for you!

## Loading stuff

You’ve already seen me ranting about AMD, obviously I will be using [RequireJS](http://requirejs.org/) for all things loady. I can use it to load all of my personal code, all of the dependencies (these are _all_ AMD compatible!) and provide a mechanism for loading, compiling and optimising my templates.

There is no question about this really, it’s the obvious choice for splitting everything into chunks and loading them when required into the browser.

## Helpers and functional sugar

I love having a library dedicated to helping me mess with my data, especially when I know how to use it within the world of functional programming (something I find extremely enjoyable). I find [Underscore.js](http://underscorejs.org/) incredibly useful for this, but I will definitely be using it’s younger brother instead; the beauty that is, [Lo-Dash](http://lodash.com/).

Lo-Dash is a fork of Underscore.js that includes more features and a huge amount of optimisations. The extra things include more functional programming helpers such as right partial application. I’d recommend it as a dependency for most projects, but this composition doubly so. It will provide some much needed helpers that can be used pretty much anywhere.

Oh, and this also has AMD support, something Underscore.js lacks, sadly.

## Fetching everything

The cool thing about this is that you can depend on them all through [npm](https://npmjs.org/) or [Bower](http://bower.io/), so there’s no need to fetch them all individually or commit them into your repository. Here’s what my Bower package would look like.

```
{
    "name": "some-project",
    "version": "0.0.0",
    "main": "main.js",
    "ignore": [
        "**/.*",
        "node_modules",
        "bower_components",
        "test",
        "tests"
    ],
    "dependencies": {
        "lodash": "*",
        "crossroads.js": "*",
        "maria": "*",
        "mustache": "*",
        "requirejs-mustache": "*",
        "requirejs": "*"
    }
}
```

Obviously you can lock your dependencies to a specific version, but I’m just using the latest versions of all for now. All you have to do is run `bower install` to fetch the latest version of all your dependencies and you will have them all available within `bower_components`. Pretty neat.

## Like the idea of this?

I personally love the look of this and would prefer to compose my own framework as opposed to using a massive one that tries to do everything on its own. I’d love to hear reasons for or against it though, so let me know. Surely I can’t be the _only_ developer out there that likes blending multiple focussed libraries together?

I hope you’ve found this useful.
