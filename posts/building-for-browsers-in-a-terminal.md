---
tags:
  - blog-post
  - imported-blog-post
---
# Building for browsers in a terminal

My entire web development work flow takes place within my terminal; obviously I need a browser too, but other than that I have no GUI tools floating around. I’m going to show you what tools I use and how I use them. Take from this what you will.

## A quick overview

I make use of [tmux](http://tmux.sourceforge.net/) for multitasking, [Vim](http://www.vim.org/) for any editing with [my extensive configuration](https://github.com/Wolfy87/vim-config), [git](http://git-scm.com/), [ag](https://github.com/ggreer/the_silver_searcher) (also known as “The Silver Searcher”) as a grep replacement, [Python](http://docs.python.org/3.0/library/http.server.html) for simple HTTP servers, [node](http://nodejs.org/) and a few linters including [JSHint](http://www.jshint.com/), [CSSLint](https://github.com/stubbornella/csslint) and [JSONLint](https://github.com/zaach/jsonlint). These linters are completely integrated into Vim with [Syntastic](https://github.com/scrooloose/syntastic), so I don’t need to worry about running them.

I also use the general Unix tools a lot which include `less`, `grep`, `curl` and `ssh`. Never underestimate the base tools that come with your OS; if you’re on Linux or Mac that is. I personally prefer a clean [Arch](https://www.archlinux.org/) Linux install with [XFCE](http://www.xfce.org/) as a desktop environment. Combine that with my SSD wielding laptop and you have yourself some insane speeds to help you get your work done.

## Tying things together

All of those tools would have no edge over GUIs if they all ran separately and could not be linked together easily. That’s why I have multiple bundles within my [Vim configuration](https://github.com/Wolfy87/vim-config) that allow me to interface with programs such as ag ([ag.vim](https://github.com/rking/ag.vim)) and git ([vim-fugitive](https://github.com/tpope/vim-fugitive)). I don’t have to worry about running my linters either, [Syntastic](https://github.com/scrooloose/syntastic) does that for me and shows me where the problems reside.

## Multitasking

So I can do 80% of my work comfortably without leaving Vim by tying other programs into it via bundles. For the last 20% which are easier to do outside of Vim, such as manipulating large portions of the file system, I can send my Vim to the background with `+` and bring it back by running `+fg` when I’m done.

I can also use tmux for a huge amount of flexibility by splitting (`<%>` or `<">`) or by creating a new window with `+`. I have remapped my leader key to `+` too, just to make it easier to hit.

## Why?

It may be because I have been working like this for a while now, but this kind of thing feels so natural now. If I work inside my terminal 100% of the time I know that any tool I use will work in a similar way. I can search any output and pipe it around however I like. I can tie things into my editor with minimal effort and I can fix things when they go wrong (which is very, very rare).

After working with and getting used to Unix terminal style tooling for long enough you stop thinking about the interface; it no longer gets in the way, it just works. There doesn’t seem to be a learning curve for anything I start using now either because the interfaces are so very similar.

This way of working seems ridiculous and backwards to some people; it’s not worse, it’s just different. _I_ love it.
