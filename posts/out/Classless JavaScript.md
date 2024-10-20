---
alias: classless-javascript
tags:
- blog-post
- imported-blog-post
---


As JavaScript developers, or even web developers as a whole, we seem terrified by the thought of direct usage. No technique or idea seems to be considered “legit” until it has been abstracted by several layers of syntactic sugar and, potentially useless, fluff.

One of the common abstractions is the addition of “classes” to JavaScript; prototypical inheritance isn’t good enough for us lot by the looks of things. We seem to crave the features of other languages when our language of choice isn’t really built for them, it’s built in a different way and probably shouldn’t have classical inheritance principals forced down its VM / throat.

So what if we worked with JavaScript in a more natural way? First, what would _be_ more natural? Personally I believe that, like the underlying language, everything should revolve around objects; we create and manipulate these malleable entities instead of trying to lock things down and restrict our data.

This idea lends its self exceptionally well to functional programming and the actual VM, which no longer needs to mess with prototype chains or any other form of inheritance for that matter. It’s as raw as JavaScript usage can get, it’s kind of like C structs (although nothing like them at the same time) and I think it’s pretty elegant.

## An example

I wouldn’t be surprised if that rather oversized introduction still left you at a loss; what do I actually mean by focussing on objects? This.

```
var nameList = {
    create: function (self) {
        self.names = [];
        self.characters = 0;
        return self;
    },
    add: function (self, name) {
        self.names.push(name);
        self.characters += name.length;
    }
};

var users = nameList.create({});

nameList.add(users, 'Oliver');
nameList.add(users, 'Sam');
nameList.add(users, 'Reece');
nameList.add(users, 'Robin');

console.log(users.characters); // 19
```

I have not used `new`, yet I have achieved the same functionality as `new` + `function` + `prototype`. I have an incredibly simple object with some namespaced methods that can be used to manipulate it. These methods can be altered using functional programming techniques such as partial application to provide even more powerful and expressive code.

## Why? Other than minimalism?

Well you may have noticed that I pass an empty object to the `create` function, this is because you can actually pull that new object from anywhere you want, say, an object pool. You don’t have to ask the browser to create new objects and garbage collect them at odd intervals, you can control it yourself, to a point.

This should lighten up the load your application puts on the browser (if you’re a heavy [line-through]*ab*user of objects) by making it create and free less objects overall. All you need to do is make sure your create function doesn’t leave anything uninitialised. You can reuse a `nameList` object in a completely different piece of code, you just need to make sure `create` is thorough.

```
var pool = [];

var nameList = {
    create: function (self) {
        self.names = [];
        self.characters = 0;
        return self;
    },
    add: function (self, name) {
        self.names.push(name);
        self.characters += name.length;
    }
};

var car = {
    create: function (self) {
        self.fuel = 100;
        return self;
    },
    drive: function (self) {
        self.fuel -= 5;
        return self.fuel > 0;
    }
};

// The pool is empty, so it will create a new object.
var users = nameList.create(pool.pop() || {});
nameList.add(users, 'Oliver');
nameList.add(users, 'Sam');
// ...
// And now we're done with our name list.
pool.push(users);

// Reuse the SAME object.
var polo = car.create(pool.pop() || {});
while (car.drive(polo)) {
    console.log(polo.fuel);
}
console.log('Out of fuel!');
```

Here I am creating a name list, adding some values and then dropping that object into a pool when I’m done. Then I’m requesting that object back out of the pool and reusing it as a car. This is a very simple example though, so much could have gone on in between those two creation calls that you don’t even know what object you’re reusing, but it doesn’t matter.

You could even have a `destroy` function for each namespace that wipes the object’s values, eliminating the potential for memory leaks. This could easily be part of a pooling tool though, one that makes sure the objects you get out are all sanitised in the same way.

## Improving the pool

If you don’t want to write a `destroy` method for each namespace then, as I mentioned above, you could have your pool code manage it for you. This means your objects will be sanitised as they are added to the pool, removing the risk of memory leaks.

```
var pool = {
    create: function (self) {
        self.objects = [];
        return self;
    },
    add: function (self, obj) {
        var key;

        for (key in obj) {
            if (obj.hasOwnProperty(key)) {
                delete obj[key];
            }
        }

        self.objects.push(obj);
    },
    get: function (self) {
        return self.objects.pop() || {};
    }
};

// Accidental pun inbound!
var carPool = pool.create({});

var original = {
    foo: true,
    bar: false
};

console.log(JSON.stringify(original)); // "{"foo":true,"bar":false}"

pool.add(carPool, original);
var output = pool.get(carPool);

console.log(original === output); // true
console.log(JSON.stringify(output)); // "{}"
```

The pool namespace allows you to create a pool object. When you add to this pool the object is emptied to prevent memory leaks. When you fetch from it, it will either return an object from the pool or a new object when required. As you can see, the object I get back out is still _the same object_ according to the browser, it just happens to be empty now.

## When to use this

Unless you really like this style, as I do, you may be wondering why you would bother using this. What do you actually gain from this apart from a style that leans towards composition and functional programming? Well, you get speed in certain circumstances.

I created a test on [jsPerf](http://jsperf.com/classes-vs-simple-objects) to highlight the difference pooling makes here. Using this style and creating a new object every time is actually slower than using `new` to create instances from the prototype, but using it in conjunction with a pool in object heavy code yields a rather large difference between instantiating classes and creating from a pool.

So you can use it wherever you want if you like the style, but it’s definitely a good idea to consider something like this in object heavy and performance critical code such as game engines. It may even yield a visible difference on much more limited platforms such as mobile devices, TVs and consoles.

## Update: Inheritance

I was giving this technique some more thought today and I realised that having some form of inheritance would make it even more flexible. It turns out that it’s incredibly easy to achieve. Personally, I’d say it’s easier than other prototypical inheritance techniques used on constructor based classes.

```
var shouter = {
    create: function (self, message) {
        self.message = message;
        return self;
    },
    shout: function (self) {
        console.log(self.message);
    }
};

var loudShouter = Object.create(shouter);
loudShouter.shout = function (self) {
    shouter.shout(self);
    console.log('(It was pretty damn loud)');
};

var s = loudShouter.create({}, "Hello, World!");
loudShouter.shout(s);
```

So here I am creating a base object in the same style as my previous examples, then I am creating a new object that uses the first as its prototype. I can then override methods as I see fit within the second object. You can also make use of underscore/lodash (or any other code that provides a function to mix objects into each other) to have elegant mixin functionality.

```
var shouter = {
    create: function (self, message) {
        self.message = message;
        return self;
    },
    shout: function (self) {
        console.log(self.message);
    }
};

var someMixin = {
    countCharacters: function (self) {
        console.log('Characters in message: ' + self.message.length);
    }
};

var loudShouter = Object.create(shouter);
_.extend(loudShouter, someMixin);
_.extend(loudShouter, {
    shout: function (self) {
        shouter.shout(self);
        console.log('(It was pretty damn loud)');
    }
});

var s = loudShouter.create({}, "Hello, World!");
loudShouter.shout(s);
loudShouter.countCharacters(s);
```

And if you don’t want to create, potentially complex, trees of inheritance, why not use composition instead. This technique lends its self to it rather well.

```
var view = {
    create: function (self, template) {
        self.template = _.template(template);
        return self;
    },
    render: function (self, values) {
        return self.template(values);
    }
};

var button = {
    create: function (self, action) {
        self.action = action;
        self.clicked = false;
        self.view = view.create({}, 'Button clicked? <%- clicked %>');
        return self;
    },
    click: function (self) {
        self.clicked = true;
    },
    render: function (self) {
        return view.render(self.view, {
            clicked: self.clicked
        });
    }
};

var myButton = button.create({});
console.log(button.render(myButton)); // "Button clicked? false"
button.click(myButton);
console.log(button.render(myButton)); // "Button clicked? true"
```

I’ve created a button that delegates it’s rendering to a view stored within the object. To me, that looks pretty damn nice.
