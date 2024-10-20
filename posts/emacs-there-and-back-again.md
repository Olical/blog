---
tags:
  - blog-post
  - imported-blog-post
---
# Emacs: There and Back Again

So I’m back in Vim already. I gave Emacs a few months of good usage though and tried out multiple styles of configuration. At first I tried to build my own from the ground up, a la Vim, but it was too fiddly. I found Emacs to be too big to handle cleanly for one person, so I eventually resorted to [Spacemacs](https://github.com/syl20bnr/spacemacs).

Spacemacs worked great and provided me with many of my normal Vim comforts. It essentially uses the space key as the leader and remaps all the functionality you could ever need, be that default or plugin, to the space key.

I missed the feeling of building my own editor bit by bit however, and only Vim lets me customise a tool that starts out completely devoid of usefulness. Strangely, that’s a good thing for me. It always felt like I was suppressing some built in tooling with Emacs. Sure it’s powerful and Lisp is sexp but I couldn’t shake that “it’s just doing too much” feeling.

I’m a huge advocate of the unix philosophy, and even though Vim can break it a bit, Emacs breaks it a lot. As it’s widely known, it’s essentially an operating system. Linux is my OS of choice, Vim in my text editor.

## Good came out of it

Emacs is great, don’t get me wrong. I learnt a lot of useful tricks that I want to translate over to the Vim world since there’s always an equivalent. A prime example being [helm](https://github.com/emacs-helm/helm), it’s an excellent piece of software, who’d have thunk that uniting textual interfaces worked so well. This is where [Unite](https://github.com/Shougo/unite.vim) steps in to fill the void in Vim. I’ve already got it hooked up in my new setup but I’ll touch on that towards the end of the post.

I’ve also learnt the importance of consistent leader bindings. I won’t be rebinding core Vim functionality to my leader key (as Spacemacs does) but I will be mapping all of my plugins and functionality under common groupings. So anything to do with [fugitive](https://github.com/tpope/vim-fugitive) (the best Vim wrapper ever, despite [Magit](https://github.com/magit/magit) being great) is under `\g*` where the asterisk is a mnemonic key for what it’ll do. `\gs` is `Gstatus` for example.

I’ve also bound `\gj` to pull and `\gk` to push, because they’re synonymous with up and down. Sure it’s not mnemonic, but it’s very Vim.

So in the end, I found Emacs to be an excellent _platform_ for almost anything you can imagine that can be represented with text. But I want an editor, and I kind of like Vim Script. Please don’t die from shock. It’s a horrible language, but a good DSL, in my opinion.

## Rebuilding my dotfiles

All of this discovery and new found ideas prompted me to `rm -rf ~/dotfiles`, which are stored on GitHub, but still. You get the idea, I started my dotfiles again. Mostly. Go peruse my new [dotfiles](https://github.com/Wolfy87/dotfiles) if you so wish.

I discovered [vim-sensible](https://github.com/tpope/vim-sensible) after a quick check up on tpope’s recent shenanigans. This is like the plugin equivalent of `set nocompatible`, it switches on so many basic things that every configuration should have anyway. I highly recommend it as a starting point for any new Vim configuration. I found my core Vim configuration shrink considerably since this handles pretty much everything for me.

As you’ll be able to see from my repository, I’ve also swapped to a very modular configuration system. I have a few directories where every Vim Script file is automatically sourced for easy grouping of settings. I did this in my previous setup too, but I’ve taken it a step further by doing it for bundles too. If I create a file in a certain directory with the same name as a currently installed bundle, it’ll be sourced at the appropriate time.

On the subject of bundles, I also swapped from [Vundle](https://github.com/gmarik/Vundle.vim) to [NeoBundle](https://github.com/Shougo/neobundle.vim). It originally started as a fork but the underlying principals of it appear to be slowly shifting away from it’s parent project. I like the parallelism among other things. It’s definitely a lot quicker, obviously. It delegates to [vimproc](https://github.com/Shougo/vimproc.vim) to get those installs running in parallel, which is worth the required `make` command after installation.

One key point of this new setup is the fact that it can be installed and linked in with a single bash script. It works incredibly well, check out the readme for more detailed information on the inner workings of my new tooling.

## Please copy things

This is what I hope others will obtain from this post and the actual repository: Take as much as you can from my work, fork if you wish. Just please learn something from it. This is like spring cleaning for my tools, I can’t wait to refine them over the coming year, especially Vim. I want my changes to inspire others to improve their tooling too.

Our dotfiles grow and adapt with us, organically, after long enough the fundamental principals need updating which prompts a rebuild such as mine. It’s refreshing and exciting, I wonder what my next rewrite, possibly years from now, will look like.
