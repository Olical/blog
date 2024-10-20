---
tags:
  - blog-post
  - imported-blog-post
---
# When projects rise from the grave

Halloween is supposed to be over, so why is my project coming back from the dead?

[Olical/Color](https://github.com/Olical/Color) is something I started in early 2011, I think I was still at my first job as a junior web developer tinkering with WordPress and custom PHP sites. I’m now on my 5th workplace doing something entirely different. I’m studying computer science and lisps in my free time instead of “how do I make this button red”, which isn’t to say it’s better, just different. I have moved on so far from these interests, they feel totally alien looking back.

I was reminded by [this small pull request](https://github.com/Olical/Color/pull/1) to fix a bug in my old untested project. It came up out of the blue and instantly had someone else respond, to my amazement. So what I thought I’d do is quickly go through the project with 5-6 more years of experience under my belt and share those feelings. Then I want to clean it up to the standards I have today. Maybe even just rewrite the damn thing within tests.

## Distribution

No _package.json_, no instructions, good luck. Apparently I just wanted people to clone this into their repo or literally copy and paste the file into a directory.

The minified source is actually commited in (which I see as a big **no** now) and it’s licensed under two different licences. I presume I was copying jQuery or whatever was hot at the time I guess. I’ve taken to just using [The Unlicense](http://unlicense.org/) now and I’m a lot happier about it. I don’t know how I expected anyone to actually depend on this, maybe I didn’t, maybe I released it as a bit of fun just in case someone would find some of it useful.

At least I was using [Closure Compiler](https://developers.google.com/closure/compiler/), that’s pretty cool. I mean, [UglifyJS](https://github.com/mishoo/UglifyJS) is enough to get by really, especially at this size, but nice that I was aware of it. I don’t know why everything is a combination of slightly strange looking _Makefile_ and JavaScript though, that’s a really weird build toolchain. I still use _make_ to this day, but not in this way. I’ve just realised I commited the Closure Compiler JAR into the repository too.

[![Shame Cube](/assets/legacy-images/2016/11/giphy.gif)(/assets/legacy-images/2016/11/giphy.gif)]

## Code

[Here’s the source](https://github.com/Olical/Color/blob/45a83fecda62c086e788895182e403a9c9b42807/color.js). Why did I feel the need to comment every damn thing? _The “No Shit Sherlock” award goes to…_

```
// Initialisation
var src = require('fs').readFileSync('color.js', 'utf8'),
    sys = require('sys'),
    jshint = require('./jshint').JSHINT,
    i = null,
    e = null;
```

```
// Initialise any required variables
var i = null,
    split = [],
    colors = this.names;
```

```
// Return the joined version
return '#' + color.join('');
```

```
// Convert it to an array
color = this.toArray(color);
```

```
// Compare
if(color[0] === colors[i][0] && color[1] === colors[i][1] && color[2] === colors[i][2]) {
    // Found it, return the name
    return i;
}
```

Let this be a warning to you: Don’t add useless comments, just write clearer code. Some part of me obviously thought I was doing the right thing here, that part of me was horribly wrong.

The whole thing is pretty damn imperative, lots of loops, no functional stuff. Past me would have probably found the additions of _.map(…)_ hard to read, current me finds all of these loops even harder. And if I’d just stored all those constant colours as hex strings I could have searched through with string comparisons, instead I compared every array element in each item of the list.

Want to know the worst thing about this code though? That’s rhetorical, this is a blog post and I can’t hear your response so I’ll tell you regardless (if you happen to read further into this paragraph, that is): There Are _No_ **Tests**.

**curtain falls**

## Fixing what ain’t broke

It’s fine, honestly, even though I look back and say “ew”, it works. Well, apart from that bug that [pypmannetjies](https://github.com/pypmannetjies) kindly fixed for me. But that doesn’t mean it can’t be “better”. Here’s what I __want __to do, but I don’t _need_ to do.

* Write a full test suite for the current code.
* Clean up the build / linting tooling, no more committed “binaries”.
* Refactor until it basically doesn’t resemble the original code.
* Actually have a way to depend on it (probably npm and [unpkg](https://unpkg.com/#/)).

I thought that’d be a bit much to write up because I’m lazy, so instead I spent an hour configuring [OBS](https://obsproject.com/) on my laptop (totally easier than writing a little bit…) and recorded the whole thing (another hour). You can watch that [over here](https://youtu.be/dCRK7IHg4Bk) or below. It’s the first time I’ve recorded myself actually working on something, I hope you like it. I actually want to do more of that in the future, maybe stream building something in Clojure so people that are unfamiliar with it can ask questions as I go.

I actually put off the raw code refactor, mainly because now it’s tested I feel a lot better about it. If I rewrite it, I’ll end up with the same performance and functionality, but less time to play games and drink coffee. If I ever do refactor any of it now I can do it with confidence, which is great.

So, go back and do this to one of your old projects, it’s refreshing to see how differently you do things now.
