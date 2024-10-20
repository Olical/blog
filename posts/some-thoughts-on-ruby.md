# Some thoughts on Ruby

You may have noticed that I recently started working my way through [Seven Languages in Seven Weeks](https://pragprog.com/book/btlang/seven-languages-in-seven-weeks) (which will be shortly followed by [Seven More Languages in Seven Weeks](https://pragprog.com/book/7lang/seven-more-languages-in-seven-weeks)). I almost definitely won’t finish in that time frame with Coursera deadlines looming ever closer, but it’s worth a shot. I finished the first chapter this evening, which happens to be on Ruby. I thought I’d write up my thoughts on it after this first dive into a language I’ve been meaning to get my head around.

All of my work for these books can be found within my [langs](https://github.com/Wolfy87/langs) repository. So check out the Ruby directory for some more context with regards to what I was asked to do during the little exercises.

## My thoughts?

I _like_ it. I do **not** _love_ it. This is for quite a few reasons, most of which wouldn’t annoy most other programmers but I’ll try to list them all here.

### Sugar

There’s just so much of it, every sentence of the chapter introduced some new magical syntax that did something miraculous yet non-obvious. The meta programming just felt downright clunky when compared to the king of macros, [INSERT ANY LISP HERE]. I know that’s not really a fair fight, but if you’re going to let the user add to the language let it be in a beautiful and seamless way.

### A dozen paths

There seems to be an overwhelming amount of ways to do one thing so I felt a little lost when looking for a canonical solution. Sure, this happens to a certain extent in any language, but with Ruby fundamental things like parentheses are optional some of the time. That _really_ threw me.

Blocks going after the last argument, or maybe there is no arguments list, inferring a single hash argument if you pass _foo(:bar => :baz)_, it just became a bit much. The syntax soup is particularly off putting for me, others may be absolutely fine with that.

I think I’ve been spoiled by falling in love with simplicity and simple languages. I’m looking at you, Clojure, you beautiful enigma I yearn to work with.

### SURPRISE!

I felt like the book shouted this at every paragraph and expected me to be happy about it. “Look at all these shiny features, you can make things happen without having to explicitly execute it! Isn’t that wonderful!?”

No.

It is not.

Ruby is putty, you (and every other developer that touches that code base) can mould it into any shape you wish. That lack of rigidity makes me question what it would be like in a large project. I mean million lines of code and upwards project. If I go to do something on a string and someone monkey patched a function with a conflicting name a year ago I wouldn’t be very happy.

I just spent a lot of time going “wat?” which put me off a little.

'''''

## Right tool for the job

I’m bashing on a fairly good language for things that make it good. I know. It is a great tool for scripting things and just generally getting something done very quickly. I’d definitely select it over JavaScript / node.js for any scripts or small web services, but that’s not a hard choice. If JavaScript wasn’t currently omnipotent I’d drop it, burn it and erase it from my memory in a heart beat. Then I’d go use something elegant, well rounded and functional. But that’s not going to happen for a very long time. _Sigh._

I’m sceptical of Ruby for what I see day to day at work, it wouldn’t fit there. I think that’s where my “ew, no structure” response comes from. That kind of softness would cause chaos in my current environment, I think. Unless you have a bunch of _very_ experienced and disciplined Ruby developers that won’t set up a gun to shoot someone else in the foot a year down the line.

I sure hope this doesn’t anger anyone. Also, the next chapter is on Io, so that should be fun. Really looking forward to Elm and Haskell too.
