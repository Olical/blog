---
alias: handling-concurrency-and-asynchronous-javascript
tags:
- blog-post
- imported-blog-post
---


If you’ve worked with JavaScript in a browser, or indeed on the server, for any length of time you’ve probably had to deal with asynchronous spaghetti. This kind of spaghetti is not delicious in any way, shape or form.

You end up with multiple boolean variables and function calls constantly checking if all of those AJAX requests have finished; it is far from ideal. I’m going to show you how to break the problem down and come out of the other side with your sanity still intact.

## An example

The first step in explaining this involves creating a problem, here’s the scenario: You’re running a site like [gist.github.com](https://gist.github.com/) and you need to fetch some meta data for a list of gists from the server. This is going to involve you making multiple requests and then doing something when they’re all finished, you’ll probably want a loading spinner overlay or something while it fetches them as well.

The way to do this in an elegant way is to have something execute a set of functions or requests that can each tell the central component when they have finished. I’m going to create a very simple class called `Batch` that will do just that.

```
/**
 * Executes a list of functions that call back when they are finished.
 *
 * @class
 * @param {Array} functions Target methods to execute when requested.
 * @param {Function} completionHandler Executed when all target functions are finished.
 */
function Batch(functions, completionHandler) {
    this._functions = functions;
    this._completionHandler = completionHandler;
}
```

This is just an empty class that will take an array of functions that it will execute later. It also takes a `completionHandler` function argument; it is executed when all of the functions are completely finished.

## Starting the requests

Now we need a method that will execute all of our provided functions.

```
/**
 * Executes the functions passed to the constructor.
 */
Batch.prototype.execute = function execute() {
    var i;
    var functions = this._functions;
    var length = this._remaining = functions.length;
    this._results = [];

    for (i = 0; i < length; i += 1) {
        functions[i](this);
    }
};
```

When called, this will store the amount of remaining functions left to finish executing (`this._remaining`) and then begin the execution of each and every one of them. Each function will be passed the current instance of `Batch`, the functions will then have to call a method on that instance to signify that they are done.

The `this._results` array will be used to hand the results of each function back to the completion handler when everything is finished.

## Letting Batch know we’re done

Each function that is executed is going to need to signify that it is done somehow. We will do this by adding a third method to the `Batch` class which knocks one off of the `this._remaining` counter and executes the completion handler if we’re done. We’ll also allow this function to store a result in the `this._results` array.

```
/**
 * Signifies that another function has finished executing. Can be provided with
 * a value to store in the results array which is passed to the completion
 * handler.
 *
 * All functions in the batch must call this when done.
 *
 * @param {*} [result] Optional value to store and pass back to the completion handler when done.
 */
Batch.prototype.done = function done(result) {
    this._remaining -= 1;

    if (typeof result !== 'undefined') {
        this._results.push(result);
    }

    if (this._remaining === 0) {
        this._completionHandler(this._results);
    }
};
```

Now our asynchronous functions can let the class know when they’re done, we can also store resulting values to be passed along to the completion handler. In our case, this will probably be gist meta data object or a chunk of JSON. This call would probably be made from the `oncomplete` event of our chosen AJAX library.

## Putting it into use

Now we need to construct our array of functions, execute them as a batch and use their results. You’d need to do a little bit more work to add error handling and potential timeouts, but this should get you more than started.

_I’m using an imaginary AJAX library and this could be written in much more efficient ways._

```
// The URLs we want to fetch, probably returned by an API or something.
var urls = [
    '/api/gists/1000',
    '/api/gists/1001',
    '/api/gists/1002',
    '/api/gists/1003',
    '/api/gists/1004',
    // ...
    '/api/gists/1337',
    // etc...
];

var i;
var length = urls.length;
var batchFunctions = [];

// Create our functions to be executed by the batch class.
for (i = 0; i < length; i += 1) {
    batchFunctions.push(function (batch) {
        AJAXLib.get(urls[i], function (response) {
            batch.done(response);
        });
    });
}

var gistBatch = new Batch(batchFunctions, function (results) {
    // Here we have an array of responses that we can render to the page.
    // It's only executed once all of the requests have called "batch.done(...)".
});

gistBatch.execute(); // Away we go!
```

As you can see, I’ve used a simple class to solve a potentially complicated problem. Hopefully this will save you an hour or ten in the future.

As always, I’d gladly turn this into a fully fledged package if there was enough interest.

## Obligatory JSFiddle example
