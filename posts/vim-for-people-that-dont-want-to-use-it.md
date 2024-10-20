# Vim for people that don’t want to use it

This is for those of you that use a GUI IDE / editor happily but sometimes use Vim because you have to in your terminal. (think commit messages, SSH and tiny tweaks to files)

I agree, Vim is not perfect, although nothing is perfect in every situation. Most of my colleagues use IntelliJ or similar IDEs and editors, which is fine since they’re also great tools. Sometimes you will find yourself on a command line with your familiar tools out of reach and you’ll have to quickly dip into Vim. This could be to write a commit message, amend a readme or correct a variable name on a remote server. My point being: Even those of you that think a text editor in your terminal is for masochists need to use it every now and then. I’m going to show you some _very small_ changes you can make to create a much more modern Vim. I can’t stand using the default set up on someone else’s machine, so why should you?

## Absolute minimum

You can add two lines to your _~/.vimrc_ (Vim loads this when you start it, it’s essentially your settings but can do a whole lot more) that will remove a lot of the legacy compatibility and enable syntax highlighting. Two core tenants to any editor.

```
set nocompatible
syntax enable
```

So for those of you that really don’t care and just wish it wasn’t a black and white program of despair, that should get you going.

## Dig deeper for a much better tool

The power of Vim lies not in it’s modal editing features, but the extensibility and community. You can find a plugin for almost anything, including managing plugins. If you follow the guide to install [vim-plug](https://github.com/junegunn/vim-plug) then you will easily be able to install [vim-sensible](https://github.com/tpope/vim-sensible) (an excellent set of defaults that make everything even more modern looking) and [vim-sleuth](https://github.com/tpope/vim-sleuth) which detects and manages your indentation settings for you. With all of this installed (which will not take you long at all) you can add a few more settings to make it even better.

```
" Enable line numbers.
set number

" Enable invisible characters.
set list

" More natural splitting.
set splitbelow
set splitright

" Set a default indent, but vim-sleuth should adjust it.
set tabstop=4

" Enable mouse. Great for resizing windows and keeping co-workers sane.
set mouse=a

" Disable swap files.
set noswapfile
```

You won’t actually need _syntax enable_ after this either since vim-sensible adds it for you. I highly recommend performing all of the above actions to get an editor that contains everything you’d expect in a modern tool. You can do even more though if you begin to like the experience. [CtrlP](https://github.com/ctrlpvim/ctrlp.vim) adds a fuzzy file finder under the obvious key binding and [EasyMotion](https://github.com/Lokaltog/vim-easymotion) will make navigating your file easy. You essentially ask Vim “where are all the possible words I could jump to?” and it will assign a key on your keyboard to every word on the screen. You then press the key to jump to the word. But it doesn’t just work for words, it works for any motion, so you can jump around in a couple of keys as opposed to reaching the mouse or pressing _w_ 50 times.

You should definitely spend 20 minutes or so playing with some basic Vim configuration so as to save you much more time later when you’re stuck on a command line for some reason. Maybe you’ll realise it’s not so bad and begin building your own personal configuration. You can find mine in [my dotfiles repository](https://github.com/Wolfy87/dotfiles), which may provide a good starting point. Good luck.
