# Updating dependencies without CHANGES.md

I recently received an issue on [EventEmitter](https://github.com/Olical/EventEmitter), my longest running project I have carried through my entire career so far, asking [where the change log was](https://github.com/Olical/EventEmitter/issues/126). The bad news is that there isn’t one.

Although I have [a pretty thorough change log](https://github.com/Olical/react-faux-dom/blob/master/CHANGES.md) for my newer projects like [react-faux-dom](https://github.com/Olical/react-faux-dom), I didn’t even understand [semver](http://semver.org/) back when I started it all those years ago (it feels like a long time to me, okay). I never considered explaining what changed over time for my users because I’d never had that requirement or predicament myself. Yet.

## Some tips will have to do

I couldn’t go back in time and tell younger me to track the changes and I certainly wasn’t going to trawl through every commit and build one retrospectively. All I could offer the nice fellow was advice I’ve picked up from books, people and mistakes over time. I’m putting that here to in the hopes that others outside of my projects issue tracker can learn from what I had to say. I hope it’s useful and helps you out some day. I pretty much copied it directly but fixed a handful of small typos.

> I’m afraid I started this project at the start of my career and have carried it all the way through so far. I didn’t know the connotations of semver or the importance of change logs at the time, so I’m afraid there’s nothing.
>
> BUT, I can offer some advice and reassurance. When upgrading any library, even if you _think_ they follow semver to the letter, you should write some tests in your suite around that library. This is a great way to _learn_ a library as well as protect yourself against change. You may even end up fixing something upstream. I know it’s extra work, but some people think TDD or even tests are extra work, they offer the same reward. A stable system over time.
>
> The reassurance is that I have deliberately kept the surface API the same. I bumped the major version when I rewrote it, but kept the method signatures the same. I can’t see you having any issues, but you could always check the docs from the old version (use git tags) and compare them to your desired version.
>
> If you only use add and remove event listener, you only need to check how they work (or test them!).
>
> I know it’s not ideal, but I hope this helps a little, it’s all I can offer really. I have learnt from this on projects like [react-faux-dom](https://github.com/Olical/react-faux-dom/blob/master/CHANGES.md), but I didn’t learn that in time for EventEmitter.
>
> Semver is great and all, but you can’t put absolute trust in it, you need some tools in your brain to deal with changing dependencies and being sure that they work in your new system. Tests are the best way, in my opinion.
>
> (sorry that this turned into a sort of blog post, I didn’t want to just say “no changelog sorry”, I wanted to offer _some_ sort of help too)

Well, now it isn’t a “sort of blog post”, now it’s a “blog post”. Just not a very good one I guess. I do think reiterating my points here will mean more people get to see them though, for better or worse.

The main point I was trying to get across was that you can not trust your dependencies, especially in JavaScript where the default is to let dependencies of dependencies versions slide forward and update silently. [Yarn](https://yarnpkg.com/lang/en/) helps with this problem, but you should always be vigilant in ecosystems that rely so heavily on huge trees of dependencies.

A few tests ahead of time will save you from a world of hurt down the line.
