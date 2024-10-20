# Implementing a message bus in JavaScript

For starters, what on Earth is a message bus? It’s not a device used for bulk transportation of postmen… yet. I see message buses as the predecessors to the event managers you see so commonly in today’s modern JavaScript.

The only problem with event emitters and the like is that you require a library built to handle it which incurs an overhead on every front; even if it is tiny, it’s still an overhead.

The brilliant thing about message buses, is that you can implement one using only a single array. Sure, it’s quite rudimentary and you will want to write a wrapper class if you want to add anything special. But the point is you can use the technique with no third party code.

It’s great for when you want to keep your code clean and lean. It also suites some situations a lot better than specialised event managers.

## But seriously, what are they?

A message bus is simply a list of things that slowly stack up as things happen. Then when required, you can pop some of those things out of the stack and deal with them as more pile up. This is great for asynchronous UIs and games that can have a lot of things going on at once that you need to sift through without having hundreds of functions called every second.

It’s a synchronous process that can be used within a game loop to clear the backlog of events that happened whilst something else was going on.

## A simple (bad) implementation

```
// Define our message bus. Yep, that's all there is to it.
var messages = [];

// Now we would have a lot of code (possibly asyncronous events) that would occour.
// These events would push messages to the stack like so.
messages.push({
    type: 'collision-detected',
    position: {
        x: 234,
        y: 827
    }
});

// Obviously you can lay that object out in any way you want. Or maybe you will only pass a string, or maybe a class instance.
// Use whatever is best for the situation.
messages.push({
    type: 'gun-fired',
    shooter: theGuyThatShot,
    angle: 234,
    position: {
        x: 756,
        y: 322
    }
});

// Now, when you are ready you can loop through your stack and handle them.
for (var i = 0; i < messages.length; i++) {
    switch (messages[i].type) {
        case 'collision-detected':
            // ...
            break;

        case 'gun-fired':
            // ...
            break;
    }
}
```

As you can see, it only uses an array. All this really is, is an abstract concept. I’m just pushing objects to an array which contain some meta data and then handling them later. This in its self is quite useful, but we can do a lot better.

You can already see problems with this bland approach. The array is never cleared, so you will loop over old objects every time you execute your message bus parsing function, and if messages are added during your iteration, you might not see them if you iterate in certain ways.

## Improving the iteration

The coolest part of this technique is the way you can loop over the array. The code here is so elegant it makes me smile just writing it.

```
// We would use the original array definition and method of adding it here from the previous example.
var messages = [];

// Add messages...

// Now for the better iteration.
var message;
while (message = messages.pop()) {
    // Handle each message which is stored in the message object here.
    switch (message.type) {
        case 'collision-detected':
            // ...
            break;
    }
}
```

Now messages are removed as you parse them leaving the code readable and expressive. As soon as the message stack runs out of items, `messages.pop()` returns undefined which breaks out of the loop. So even if you add messages mid-loop it will get rid of them before finishing.

You may want to swap the push or pop usage for a method that alters elements at the bottom of the stack, such as [shift](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/shift) in place of pop. This will stop the stack being first in, first out (a.k.a. FIFO) and instead become first in, last out.

You wouldn’t want to add a message and have that dealt with first, what if the first message you added has been sitting there for 3000 iterations. That’s a very extreme case, but it has the potential to happen if you only use push and pop which only affect the very top of the stack. The only reason I have not used shift here is because pop will work fine for a lot of cases. And [shift is a lot slower than pop](http://localhost:4000/2013/06/05/implementing-a-message-bus-in-javascript/).

## When to execute the loop

You can’t exactly leave the loop running the whole time waiting for input; that would lock the whole browser up. This method should be run on demand to clear a backlog of something, kind of like [garbage collection](http://en.wikipedia.org/wiki/Garbage_collection_%28computer_science%29).

That “something” could be a list of events or changes the user has made to a document that needed batch saving to the server.

## Example usage

This method could be used to store changes to a document (as mentioned above) which is periodically bundled up and sent off to a server. It could also track all key presses and mouse events in between game logic frames which could then be handled at a set interval of three seconds, for example.

I have set up a quick working example of something using this technique on JSFiddle. You can view it [here](http://jsfiddle.net/Wolfy87/DdzHL/) or in the panel below.

.

Hopefully this will help you to solve some strange architectural problem you have never come across before where you have asynchronous events that need to be handled synchronously. I’d love to hear what you you think of this in the comments, _all_ feedback is welcome.
