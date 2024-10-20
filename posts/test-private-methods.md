---
tags:
  - blog-post
  - imported-blog-post
---
# Test private methods

If you’ve barely passed the title and you’re already seething with violent rage: This post is for you. Like medicine that smells of battery acid, this’ll taste bad and potentially kill you but you’ll feel better for it in a few days. Let it settle in for a bit, mull it over.

Herein lies my argument **for** testing private methods within JavaScript (and potentially any other language that doesn’t have _real_ privates, I’m looking at you, Python).

## Other languages

In a language with real privates, such as C++, you can not physically test your private methods. Either you make them so simple that they do not need testing, make them public or extract them into another class.

These approaches are not always valid or possible in the _Real World^TM^_, so the methods in question can sometimes go untested (may the lords of TDD have mercy upon you).

Not testing a private because it doesn’t make sense to expose it in other ways is ridiculous. Especially in a language where you can actually access it programmatically.

## Testing _every_ method

I’m a firm believer in TDD and I personally think writing even one test for your method before writing the implementation helps you to think from a consumer’s perspective. This consumer usually ends up being _you_.

You may write a test for a boolean returning private that a public method depends on. Half way through writing it you could realise it’s bat guano insane and there are far better solutions. TDD ends up being JIT planning (I’m coining that).

So testing every method allows you to: Get far more coverage (even for trivial methods), imagine how your function will be used before you write it and force you to only write testable functions. What happens when your untestable private gets made public and needs tests? A mess, that’s what.

## Tests are friends

Literally. You should treat your test file for a class as if it were a friend class. Friend classes have access to the other class’ protected and private values. You can still keep the idea of privates when one class is talking to another, but the test file is a best bro 4 lyfe. It can check out its class’ privates any time it wants to.

This way, you can still change private methods on a whim without breaking other classes, you just need to update your tests _first_. **This is how it should be.**

## The result?

Potentially: 100% test coverage. Most likely: Easily testable and well thought out methods. This leads to a generally cleaner codebase that doesn’t have a plethora of private methods that were taped onto the side and not tested “because private”.

There will still be some that insist on holding onto the public only view, to them I say: Not all languages were created equal. JavaScript doesn’t have privates, just roll with it and use it to your advantage. Turn that looseness into even more tests.
