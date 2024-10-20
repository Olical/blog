---
alias: prototypical-inheritance-done-right
tags:
- blog-post
- imported-blog-post
---


I’ve read a lot of posts in the past on this subject; this includes some by massively influential developers such as [John Resig](http://ejohn.org/blog/simple-javascript-inheritance/). I don’t think the popular methods are the right way though. They all feel synthetic and unnatural. Not nearly vanilla enough for me.

Plain ol’ JavaScript is perfectly capable of handling all of your inheritance needs without the use of wrapper methods or those pesky `Class` instances. Of course, you can wrap these simple techniques up inside functions if you want to, it probably looks a little prettier that way.

## A base class

In JavaScript, classes are simply constructor functions that build an object from their prototype when instantiated with the `new` keyword.

```
function Element(id) {
    this._id = id;
}

Element.prototype.getId = function() {
    return this._id;
};

var el = new Element('example');
el.getId(); // 'example'
```

The `this` keyword is mapped to the new object instance. In this case that would be `el`. So when `this._id = id;` is executed it’s actually assigning the `id` argument to the new `el` object that `new Element(...)` created.

## Inheriting another prototype

To inherit from another class, all you really need to do is copy the prototype object from your base class into the one you want to add the functionality to. Here’s how **not** to do it.

```
function Element() {}
Element.prototype.render = function() {};

function ImageElement() {}
ImageElement.prototype = Element.prototype;
ImageElement.prototype.setImageUrl = function() {};
```

That sound you hear? That’s the sound of a thousand bugs hurtling towards you at the speed of pain. I’m sure you can probably guess what would happen here; any `Element` instances will now have a `setImageUrl` method because `ImageElement` and `Element` share _the same prototype object_.

There is a very easy way around this though. Well, actually there are quite a few. All you need to do is make a new object with the same properties as the original instead of an actual reference to the original.

## The solution to leaky prototypes

Use [`Object.create`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/create) to create a new object (obviously) with it’s prototype set to the one you pass. So when you try to access something on the created object, it will look for it in the local object and not be able to find it. It will, however, then look up the prototype chain to the parent object you specified and find your method or property there (hopefully).

```
var element = {
    id: 'test'
};

var created = Object.create(element);

element.id; // 'test'
created.id; // 'test' It's technically the exact same string.

// The property is in the prototype chain, not on the object!
element.hasOwnProperty('id'); // true
created.hasOwnProperty('id'); // false
```

So you can tie this into the prototype code like so.

```
function Element() {}
Element.prototype.render = function() {};

function ImageElement() {}
ImageElement.prototype = Object.create(Element.prototype);
ImageElement.prototype.setImageUrl = function() {};
```

Now `setImageUrl` will only be added to the `ImageElement` prototype, and not the `Element` one. Perfect. The only real downside to this is that it’s a fairly new method, so [the support for it _isn’t_ perfect](http://kangax.github.io/es5-compat-table/#Object.create). There is a simple [polyfill](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/create#Polyfill) though.

## Dude, where’s my constructor?

A weird quirk you may notice with this technique is that the `constructor` attribute of the child class actually points to the parent.

```
function Element() {}

function ImageElement() {}
ImageElement.prototype = Object.create(Element.prototype);

var element = new Element();
var image = new ImageElement();

element.constructor; // Element
image.constructor; // Element

// Wat.
```

This is because the constructor is assigned to the default prototype object when you create the class. So when you wipe the whole thing with `ImageElement.prototype = ...` you are essentially deleting the original constructor setting. Without the `prototype` assignment, the constructor would stay pointing at the correct object.

So you copy the parent classes prototype and you get the parent classes constructor property. It makes sense really, but it’s not what we want. So, how do we get around it? Monkey patching!

```
function Element() {}

function ImageElement() {}
ImageElement.prototype = Object.create(Element.prototype);
ImageElement.prototype.constructor = ImageElement;

var element = new Element();
var image = new ImageElement();

element.constructor; // Element
image.constructor; // ImageElement

// Boom.
```

You only have to do this if you want to. If you never use the constructor property then it will never actually effect you, you can still use `instanceof`, for example, absolutely fine without it.

```
function Element() {}

function ImageElement() {}
ImageElement.prototype = Object.create(Element.prototype);

var element = new Element();
var image = new ImageElement();

element instanceof Element; // true
element instanceof ImageElement; // false

image instanceof Element; // true
image instanceof ImageElement; // true
```

## Calling the “super” methods

When you extend a class and override a method you will probably want to call the parent method too, just to make sure you’re not missing anything. You do that in the way you would expect, even if it is a little verbose.

```
function Element() {}
Element.prototype.render = function() {
    // Generic element rendering code.
};

function ImageElement() {}
ImageElement.prototype = Object.create(Element.prototype);
ImageElement.prototype.render = function() {
    // Call the super method.
    // Be sure to apply it onto our current object with call(this)!
    Element.prototype.render.call(this);

    // Rendering code specific to images.
};
```

## Polishing it all off

I think this method of inheritance is as simple as it gets and covers every angle in regards to inheriting a class. If you wanted to do things such as mixins, it would work exactly the same as this, you would just need a method which copied all of the properties from the mixin object into the target classes prototype. All of this can be as simple as you want it to be.

To make all of this a bit quicker to type you can add one small function, even though it isn’t completely required. This is just for convenience really.

```
/**
 * Extends one class with another.
 *
 * @param {Function} destination The class that should be inheriting things.
 * @param {Function} source The parent class that should be inherited from.
 * @return {Object} The prototype of the parent.
 */
function extend(destination, source) {
    destination.prototype = Object.create(source.prototype);
    destination.prototype.constructor = destination;
    return source.prototype;
}
```

You can use this method like so.

```
function Element() {}

function ImageElement() {}
var parent = extend(ImageElement, Element);
```

And now the `ImageElement` class will inherit from `Element`, have the correct constructor attribute and `parent` will point to the parent’s prototype object. You can use this reference when calling the parent methods after overriding an existing function.

```
ImageElement.prototype.render = function() {
    // Call the super method.
    // Be sure to apply it onto our current object with call(this)!
    parent.render.call(this);

    // Rendering code specific to images.
};
```

That little reference just makes everything a little bit easier to type and read.

## Got it?

So, that’s about it. That’s how you inherit classes in pure JavaScript in a very robust yet easy way. If any of this isn’t quite clear then please feel free to ask me about it in the comments. I hope you found this useful.
