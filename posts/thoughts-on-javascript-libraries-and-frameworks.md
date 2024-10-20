# Thoughts on JavaScript libraries and frameworks

There seems to be a lot of uncertainty about [how we should use JavaScript libraries](http://addyosmani.com/blog/prosconsmicroframeworks/) recently. The main debate is whether we should still use huge 100kb frameworks, such as [MooTools](http://mootools.net/) and [jQuery](http://jquery.com/), or swap to tiny [micro libraries](http://microjs.com/) that each perform a specific task, thus resulting in a smaller page size and load time.

I personally do not get on too well with micro libraries, sure I have written a few, and [one of them](https://github.com/Wolfy87/EventEmitter) has become pretty popular, but if I had the choice I would still use a huge full on framework, namely MooTools. My reasoning behind this is the uniformed API and predictability of the code. You know that each component of the library will work with each other.

## Is there a solution?

So what is the ideal situation? I believe it lies in between a huge framework with a uniformed API and a collection of micro libraries each weighing in at under 3kb or so. Sadly, nothing like this exists at the moment. Although MooTools is almost there, you can use [its packager](https://github.com/kamicane/packager) to build your own custom version only using the files you need. Although it does feel a little awkward to use, you have to write YML headers in each file and run everything though the PHP based packager which has to be meticulously configured.

My proposition is to use a client side module manager such as [RequireJS](http://requirejs.org/) to require the parts of a framework that you need. So if you are not using JavaScript based animation, you do not load the huge animation class. If you only use CSS class management functions then that is all you load. The framework should also steer clear of polluting the global variables. I quite like how MooTools does it, but it does reduce the compatibility slightly (`$` is in almost every library for example as well as the `each` method).

## My ideal framework

A huge modular framework in which you only load what you need via calls in your JavaScript with minimal, if any, global variable pollution, so no editing `Array.prototype` for example. This will mean it can run alongside _anything_ with minimal load time footprint and a familiar consistent API.

I have already begun turning my idea into a reality. I am writing something that matches exactly what I have described above. I do not know if other people feel the same way as me and think there is a need for it, but I think it is a good and useful idea which takes a different approach to aiding frontend development.

I will link to it from here when I put it up on GitHub. I hope at least some people like this idea as much as I do. Please feel free to let me know how you feel about the points discussed in the comments below.
