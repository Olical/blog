# Faster JavaScript

I have been working on [my EventEmitter class](https://github.com/Wolfy87/EventEmitter) for about a year so far. I have rewritten it roughly every three months to put all of the techniques I have learned into practice. Now I have finished version 4 and it is the fastest yet by a long way. This [jsPerf test](http://jsperf.com/eventemitter-3-vs-4/4) between version 3 and 4 proves my point nicely.

In this post I will point out the main things I changed to get this kind of speed increase with the same functionality as the original. Well, roughly the same, I actually dropped some of the scoping stuff which can be achieved easily anyway. The old class was just getting a little bloated with settings and arguments.

## Simplification

Version 3 used a class to manage the attributes of an event called [`Event`](https://github.com/Wolfy87/EventEmitter/blob/v3.1.7/src/EventEmitter.js#L27-70). This gave me a huge amount of power but also a huge overhead. In version 4 I have completely removed this middle class and just pointed directly to a function for each listener.

Having everything in classes is useful for some situations, such as managing shapes on a canvas, but it is slower than more stringy methods that may also do the job just fine. So try not to overcomplicate your scripts, just do what works and abstract if you **really** need to. Don’t do it for the sake of OOP.

## Loop directly and correctly

You know those each functions that libraries such as MooTools and jQuery provide? Yeah, don’t ever use them.

Think about it, instead of just looping directly, you are initialising an anonymous function which is executed every time the loop is run. That loop is having to pass arguments around and perform checks you might not need. The unnecessary overhead is huge. In version 3 I used my own each function to make my code a little more [DRY](https://en.wikipedia.org/wiki/Don%27t_repeat_yourself). Just look at all the code that goes on in [that function](https://github.com/Wolfy87/EventEmitter/blob/v3.1.7/src/EventEmitter.js#L72-103).

And now compare that to [the way I loop over listeners](https://github.com/Wolfy87/EventEmitter/blob/v4.0.0/EventEmitter.js#L242-249) in version 4. I am using a reverse loop which uses less characters and less logic. It is a trick I learnt from writing [140byt.es](http://www.140byt.es/) scripts but I now use it wherever possible. Instead of comparing a counter to the current length every time as you would in a normal for loop and then incrementing, it de-increments and checks for a positive value in one swift movement.

It may not be much faster, but over a large sample size it really makes a difference. It really depends on the circumstances though, you can only use it if order is not important and the counter is not allowed to go into negatives.

## Use null instead of delete

At one point in the development of version 4 I was using the `delete` keyword to remove values from the storage object, it turns out this is pretty slow. Someone came along into my jsPerf tests and changed all removal to just set the value to null. Sure it now stacks up event names and does not remove completely but it’s contents are still removed by GC. The script is now about another 5% faster because of it. Here is [the usage of null](https://github.com/Wolfy87/EventEmitter/blob/v4.0.0/EventEmitter.js#L200-222), and here is [the usage of delete](https://github.com/Wolfy87/EventEmitter/blob/ca3104295d8020be936347c76341dba8131aa16b/EventEmitter.js#L196-217).

A small change in the code and a small increase in speed.

## Keep things short

The [`getListeners`](https://github.com/Wolfy87/EventEmitter/blob/v4.0.0/EventEmitter.js#L53-69) method in version 4 creates the storage objects and arrays as quickly as possible. The old version actually had to [build a whole array](https://github.com/Wolfy87/EventEmitter/blob/v3.1.7/src/EventEmitter.js#L223-243) because of the class based architecture.

Just try to build your functions in a simple and concise way. It will make a huge difference, especially if the function is used a lot.

## That’s about it

There are many other small optimisations out there but these should help. And it’s nice to see these in use, my examples should hopefully provide nice examples as to how they can be implemented. If you have any questions or suggestions please feel free to leave them in a comment below.
