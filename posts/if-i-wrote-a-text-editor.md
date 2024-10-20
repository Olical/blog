---
tags:
  - blog-post
  - imported-blog-post
---
# If I wrote a text editor

A little while ago [Jezen Thomas](http://jezenthomas.com/) linked a video to me on [twitter](https://twitter.com/jezenthomas/status/553260993405784064) ([non-twitter link](https://www.destroyallsoftware.com/talks/a-whole-new-world)). It’s a talk by Gary Bernhardt of [Destroy All Software](https://www.destroyallsoftware.com/screencasts). Please go watch it… now. The following post may contain spoilers that could ruin an otherwise excellent talk.

'''''

So after that big tableflip inducing reveal, it got me thinking (just like thousands of others who had the same thought), how would I build a text editor. I love my editor, I cherish and nurture it inside [it’s own cultivated repository](https://github.com/Wolfy87/dotfiles), perfecting it in any way I can. My Vim is just that, **mine**, nobody else can use it in the same way I couldn’t be effective with my colleagues configuration (probably). Our editors are as much of a project as the projects we’re using them on.

Some think this notion of constantly working on your editor instead of just building things is ludicrous, which is perfectly fine. There are editors and IDEs out there that work perfectly well, but for some of us, that one key combo that doesn’t quite feel right, or that slightly strange indentation in that one edge case is just unacceptable. We need a platform upon which to build our environment. A beautifully simple text editor that we can turn into our very own code editor. They’re all slightly different and they’re all unique. I assume every seasoned Vim (or Emacs!) user would consider their setup as more of a glove than a tool that you grip.

## But I want _more_

If I ever have to type text in anything other than Vim I’m usually unhappy about it. No modes, no Ctrl-W to erase the last word that I completely screwed up, I’m on my own. After watching the aforementioned [video](https://www.destroyallsoftware.com/talks/a-whole-new-world) however, it made me yearn for even more power and control, something I think Emacs may be able to give me, but with (what I consider to be) a slightly clunky Lisp and way too many features. The package management makes me shy away from Emacs too, especially when compared to tools such as [vim-plug](https://github.com/junegunn/vim-plug). I just find Vim more elegant and focussed on the job of editing text, but it lacks flexibility. Sure there’s some cool plugins out there, and I’ve written a couple, but they’re hard to build, test and maintain. There are a few elite developers churning out masterpieces, but the bar is too high for most to contribute their good ideas. Everything has always seemed pretty hacky to me.

So the only obvious solution would be to devote **years** of my life to building my own. This is an insane commitment and I may never even begin, but I just wanted to write up and really think about how I would do it either for future reference or so someone else can take those ideas and build it for me.

### Simplicity

This is key. It should open to a blank, or almost blank buffer with little to guide you. This is your perfectly honed tool that you’ve been using for years, eight hours a day, you don’t need to be told how to get started. It was probably the first line in the readme. The UI should be simple text based buffers, like a terminal but rendered outside of it to allow fonts and colours beyond the capabilities of your average shell. It is still text based for consistency and easy of plugin development, but it breaks free of the terminals limitations.

### Modes

Just like Vim, modes such as normal, visual (line and block) and insert are essential. Maybe there can be modes that run alongside or atop each other (like Emacs). Maybe you can have an infinite stack of them that you can pop and push to as you fly through the syntax constructs. It needs modes, and there are hundreds of ways you could take the concepts from Vim (modal like normal) and Emacs (modes like org-mode) and combine them to create something entirely new and exciting.

### Some form of shell

It would possibly contain some kind of terminal emulator which could be built and extended with even more of the editors host language. Just like eshell in Emacs. Use iTerm or xfce4-term if you want the full thing, this would be built to run, monitor and integrate tasks, not provide a full environment. Now that would be _true_ wheel reinvention.

### Easily extensible

I would like it to be written in a beautiful language such as Clojure as well as extended with it. Forget a plugin framework as such, let the plugins be written in the language of the tool and manipulate the tool directly. They’re essentially lazy loaded modules that have complete control over the platform. They can cause it to do things that was originally thought impossible during the initial implementation. No trying to get patches into the core or hacking around with things that don’t quite give you that value you need. You should have full unrestricted access to every part of the system. We’re using it to write code, so when we change our editor we should be able to change **every** aspect.

The core modules should probably be implemented through this loading system too with some sort of loading priority. WordPress got some things wrong, but it also got a lot very right, the way you can hook in and rip things out of the underlying infrastructure as it boots is fantastic. WordPress gives you almost total control as a plugin or theme developer. If the core editor with all of its modes was loaded in this way, someone could stop it from loading (or simply disable it at runtime) and replace it with their own implementation. This gives complete control without nasty, albeit rather clever, hacks.

Imagine, for example, unhooking the underlying syntax engine and replacing it with an identical C implementation for performance simply by installing a plugin. That would be amazing. Your editor then becomes a tiny platform with a few default modules that turn it into a little editor. You then add or build everything you need. Much like a [kit car](http://en.wikipedia.org/wiki/Kit_car), this would take time and effort, but the result is something you essentially built and you’re proud of. For enthusiasts of text editors, such as myself, this should be an exciting prospect.

### Mine

Nobody would use this, even if I built it and executed it perfectly. Well, nobody would use it any time soon at least. I would need to turn it into either an attractive side project for others to build their environment on top of or create enough tooling myself that people can jump right in. Worst case scenario: I’d get to see just how hard it is to write a good text editor. But I’d learn a hell of a lot along the way.

'''''

So that’s about all I have so far. I’ll probably jot down ideas as they come to me, but I would really like to build this, or part of it, over the course of 2015. I think it would be a fun project regardless of the outcome.
