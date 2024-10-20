---
tags:
  - blog-post
  - imported-blog-post
---
# Use verbose naming in JavaScript

I love verbose names in JavaScript, or any language for that matter. Sadly I see a lot of people yearning for short and obscure method names for no real reason. I can only imagine it’s because the API documentation looks cool in their opinion with no method name being longer than five characters.

I hope to show a few people the errors of their ways with this post; using longer and descriptive method or variable names solves a lot of issues very elegantly. I’ll try as hard as I can to not turn this into an aimless rant.

## Examples of the pain

Let’s play “guess the intention of the code”. You could imagine a real world version of this occurring whilst you’re browsing some unfamiliar code at the high level side of things. You want to be able to skim the code and work out exactly what’s going on without digging into a method’s source and documentation.

```
health.add(b);
update(current);
```

What’s going on? Is `b` a number being added to the `health` object? Is `health` a list of some sort and `b` a child of that list? What the hell does `add` do?! And `update`? Update what? Also, what on the Internet is `current`?

It may be a small snippet and a fairly extreme example, but it is _really_ difficult to work out what this code is doing at a glance. Now what if everything was renamed like this, forget the fact it would take longer to type, that mentality is for sissies. Your expensive IDE probably does most of the work for you anyway, even [my Vim setup](https://github.com/Wolfy87/vim-config) will!

```
healthBar.addChildElement(healthBlock);
updateScoreDisplay(currentScore);
```

Now it’s about as clear as it can get. `healthBar` is some kind of container in a display (you could even call it `healthBarElement`) and `b` is a child element. With the new name given to `b` we can actually see it’s a `healthBlock`, so this health bar is displayed with a series of block child elements.

`update` is actually `updateScoreDisplay`, so it must render a value to the screen. The value that was passed to it will almost certainly be a number representing the users current score.

Clean and simple. It’s self commenting code without a `//` in sight.

## Real world stuff

My [EventEmitter](https://github.com/Wolfy87/EventEmitter) class has an `addListener` method, which had an `on` alias which was added in a pull request. Say you have a class called `Switch` which extends `EventEmitter`. Before you’ve even added anything to the prototype your class has an `on` and `off` method. It’s not exactly ideal. `switch.addListener(...)` makes heaps more sense than `switch.on(...)`.

I used to let this kind of thing slide, since working at Amazon I feel compelled to move more and more code to their verbose alternative naming schemes. I can’t really remove those aliases from my class, but hopefully I can convince anyone reading this not to add anything similar to theirs. It doesn’t matter what the rest of the JavaScript community is doing, _they’re_ doing it wrong.

## TL;DR

Use longer, more descriptive, method and variable names. Don’t opt for the short ones because it’s easier to type, it’s what the other guy did or because your users will moan about their fingers aching. You’ll thank yourself later for putting the effort in now.
