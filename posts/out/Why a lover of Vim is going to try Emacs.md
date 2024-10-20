---
alias: why-a-lover-of-vim-is-going-to-try-emacs
tags:
- blog-post
- imported-blog-post
---


I’ve been using Vim for a couple of years now, despite that making up around half of my entire career, I feel like I’ve learnt it rather well. I can’t hack VimL like tpope, _all hail_, but I can flit around a large project and edit almost without thinking. The editor becomes a language seared into your muscle memory. It’s so good I couldn’t possibly drop it for a BBOJAAIDE (Big ball of Java as an IDE).

So why on earth am I going to give the forbidden Emacs a whirl? Partially because of a very good, and lengthy, discussion over twitter with [@krisajenkins](https://twitter.com/krisajenkins) and [@JasonImison](https://twitter.com/JasonImison) but also because of the following reasons.

## Modal editing

It has an _excellent_ Vim emulation plugin, I actually struggled a little to find holes in it. Obviously there are some, and that’s okay, vi3w isn’t a particularly common command. It truly feels that Emacs is a platform and Evil mode is the editor. It’s Vim if it was written on top of the “Emacs OS” instead of Unix, for example.

## True async

Vim with Dispatch can piggyback on tmux to sort of run stuff in the background, but the result that’s reported back (when the process is finished) is usually fairly garbled plain text. Emacs can lint my JavaScript on the fly instead of on save, I presume it can do something similar with git signs. When performing a long running search it will allow you to interact with those results _before_ it has finished executing.

## I want to become a Clojarian

Enough said.

## Lisp > VimL

The only way I can describe VimL: A beautiful gnarled old tree that can drop a branch on you at any moment. Also, it’s filled with huge, angry, bees. It seems like something fun to tinker with, but you soon find yourself in a rabbit hole. With the rabbit. It has rabies.

I know Elisp isn’t Clojure, but it’s still a Lisp. Something about the languages really appeals to me, maybe it’s the minimal syntax, maybe I just have a thing for parentheses. Only time will tell. But I think using Lisp day to day to configure something to write more Lisp will help me along my journey to functional enlightenment.

## Finally

Learning shit is fun.
