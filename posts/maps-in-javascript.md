---
tags:
  - blog-post
  - imported-blog-post
---
# Maps in JavaScript

No, I don’t mean the things that help you find obscure places, I mean the things you can store data within. Your average JavaScript object that makes up just about everything in the language is actually a map, it just restricts you to mapping string keys to values of any kind.

Wouldn’t it be great if you could map anything to anything though? Maybe a user model to a view, for example. Well that’s something you can achieve with a map implementation. You can replace the string key in an object with anything you want. If you want to see a good example of a map, I’d turn to [C++’s map documentation](http://www.cplusplus.com/reference/map/map/).

## A vanilla implementation

You can actually use the default JavaScript objects as a [hash map](http://en.wikipedia.org/wiki/Hash_table). This requires adding a `toString` method to every item you want to use as a key though. So if you were mapping user models to their view counterparts, your user model would need to have a `toString` method that returned a unique identifier.

This identifier could be the users email, a generated unique ID number or any other value that will never be repeated between objects. You could also prefix the unique value with the class or objects name to reduce collisions further. Here’s an example of this technique. You can also play with [a similar thing on jsFiddle](http://jsfiddle.net/Wolfy87/ATUSS/).

```
// This will be our map storage.
// It will map users to views.
var userViews = {};

// Now your user class needs to have a toString method.
function User(name) {
    this._name = name;
}

// user.toString will return the class name and the user name stored within the object.
// You should probably use something that is definitely going to be unique.
User.prototype.toString = function () {
    return this.constructor.name + '-' + this._name;
};

var myUser = new User('Oliver Caldwell');

console.log(myUser.toString()); // "User-Oliver Caldwell"

// Now you can assign values to the object using the user object.
userViews[myUser] = new View(myUser);

// The object would now look something like this:
{
    "User-Oliver Caldwell": {view object}
}

// You can access it again in the same way.
var viewForUser = userViews[myUser];
```

It works because when you use an object as a key, JavaScript automatically calls `toString` on it. So the object reference is converted to our key style using the class name and user name.

This gives you nice vanilla code that should be incredibly quick; it’s about as direct as you can get. This is great, but you can still take it further with a fairly small amount of code. A better method does not involve relying on `toString`, it should use the real references to things as keys in exchange for a little bit less speed and simplicity.

## Getting classy

So here’s my better method [as a gist](https://gist.github.com/Wolfy87/5759960) and [as a fiddle](http://jsfiddle.net/Wolfy87/Wuqag/) with a little extra code to show you how to use it; although the JSDoc comments should be enough. It may seem complicated in comparison to the method above, but it’s a lot more powerful and isn’t actually that intricate.

It works by keeping an array of keys and an array of values which it cross references on request. When you set a value it is added to the key and value arrays at the same index. When you request a value by key it will search the keys array for that key and then use that index to return the value on the adjacent array.

Because I’m using two arrays and not a single array, containing objects with a key and value property, I can search the keys array with `indexOf`, or potentially the [binary search function](/searching-javascript-arrays-with-a-binary-search/) I talked about in my last post.

Swapping out `indexOf` for a binary search would speed things up, but then you would also need to keep the arrays in order because of the nature of binary searches. This may or may not work in your situation, that decision is up to you.

If you do decide to use a binary search, you should probably use a [set](http://www.cplusplus.com/reference/set/set/) implementation in place of the keys array; that will keep your items in order for you. You should probably only bother with this if you can prove that searching the keys array is definitely a bottleneck. It should only be the case in very extreme circumstances.
