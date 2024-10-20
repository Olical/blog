---
tags:
  - blog-post
  - imported-blog-post
---
# Spacemacs: Emacs + Vim

_This post is pretty rough around the edges. I wrote it in Google keep as notes I intended to write up and eventually publish nicely but I don’t have the time. I’ve done what I can from my phone but I just wanted to get it out there. I hope you find it useful!_

'''''

What do you get if you put Vim, Emacs and a dash of pneumonic / consistent key bindings in a blender? A blender full of inedible computer parts, but also [spacemacs](https://github.com/syl20bnr/spacemacs). Go skim the readme first, its like a starter kit for Emacs that emulates Vim incredibly well.

Things that are hard to get over: Unlearning escape and using fd (still ironing out bugs). Using space for everything, unlearning :w and using SPCfs. SPCgs for git status (magit). SPCph for projectile.

Configuration layers are excellent. I have my local one and will (eventually) push it upstream for people to hook into. You can activate them by adding the name to the list in _~/.spacemacs_. Easy.

It’s neither Vim nor Emacs. It’s this cool blend that’s a little hard to get your head around at first but I think Sylvain, the author, is correct: The best editor is a mix of the two. You have the commands of Emacs with the modal keys of Vim. The huge buster sword of Emacs combined with the subtle dagger of Vim.

I’m trying to make JavaScript badass out of the box in the core repository, but using my overlay makes it even better. Or so I feel anyway. I’m really happy with my JavaScript setup right now. A lot of the changes I built into my layer were actually pulled into the core anyway, so my layer has been getting smaller and smaller.

When you find yourself holding a modifier for something, M-x for example, hit SPC? and check if there’s a space leader binding for it. In the case of M-x that would be SPC:. Emacs is not modal in any way, so when you have to use default emacs bindings now and again it feels odd. Vim has some things that are not modal too, such as window management. Spacemacs seems to do a better job than Vim in some places. Everything’s modal. If it isn’t, make it modal and PR it in.

You shouldn’t even need Ex mode a lot of the time! I’d highly recommend giving it a go if you are a fan of either editor.
