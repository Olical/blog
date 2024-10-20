---
tags:
  - blog-post
  - imported-blog-post
---
# Building vim-netrw-signs: Introduction

I thought of another Vim plugin idea a while back at [Vim London](http://www.meetup.com/Vim-London/), which happens to be my second of such ideas, the first being [vim-enmasse](https://github.com/Wolfy87/vim-enmasse). My plan is to build this plugin over the coming weeks (probably months, ideally not years) and take you along for the ride with these brief posts. From `git init` to `git tag v1.0.0`. To begin with, here’s [the repository](https://github.com/Wolfy87/vim-netrw-signs) and the [initial commit](/building-vim-netrw-signs-introduction/232121235a31ee282d363ae331050f40f8dbdc38). As with all of my open source code now, it’s “licensed” under [The Unlicence](http://unlicense.org/).

This plugin is much like [vim-signify](https://github.com/mhinz/vim-signify) or [syntastic](https://github.com/scrooloose/syntastic) but for netrw, Vim’s built in file browser. This is inspired by the way that some super-massive IDEs will show parts of your folder structure that have been touched according to git. I want to create something that could hook into linters or version control systems to warn you about things when you drop into netrw from time to time. This becomes an easy thing to do and a frequent occurrence if you have something like [vim-vinegar](https://github.com/tpope/vim-vinegar) installed.

So this will be quite generic with, probably, only git integrated to begin with. It will simply allow small composable modules to decide which files or folders should have signs attached to them. First thing’s first (after creating the repository)…

## Setting up the tests

Yes, tests first. I used [vader.vim](https://github.com/junegunn/vader.vim) on vim-enmasse and I’ll be doing the same here, but this time from the very beginning. It can be integrated in such a way that you can run tests from your local command line and [Travis CI](https://travis-ci.org/), as I’ll be doing soon enough. I’ll start off by defining a script to initialise dependencies and run my test suite, which is stolen from [vim-enmasse’s](https://github.com/Wolfy87/vim-enmasse/blob/835ec0bd794183514865943188990669511d546b/test/run) (mostly).

```
#!/usr/bin/env bash

vader=.vader

if [[ ! -d $vader ]]; then
    git clone https://github.com/junegunn/vader.vim.git $vader
else
    cd $vader
    git pull
    cd ..
fi

vim -Nu <(cat << EOF
filetype off
set rtp+=$vader
filetype plugin indent on
EOF) +Vader tests/*.vader
```

And then I dropped in the Travis config, just because it’s that easy.

```
language: vim

before_script: |
  git clone https://github.com/junegunn/vader.vim.git

script: |
  vim -Nu <(cat << VIMRC
  filetype off
  set rtp+=vader.vim
  set rtp+=.
  set rtp+=after
  filetype plugin indent on
  VIMRC) -c 'Vader! tests/*.vader' > /dev/null
```

So I have two scripts, one Travis will use to execute quickly on some headless machine, and another I will repeatedly run locally (probably within a tmux split). The first script will check if I have vader cloned yet and will go and fetch it if not. When it executes it will run the tests in a minimal Vim environment and then report the results through the quickfix list of the minimal Vim window. I may change this to just dump it’s output like the Travis version, I haven’t decided yet.

So now all I have to do is add a `*.vader` file to `tests/` and either or my two execution methods should run them in identical environments. I have to be careful to not let my highly customised Vim configuration leak through to my testing environment.

## Next

I’ll be adding my first hunk of source code, even if it’s a bit of autoload magic as well as a rudimentary test to check the Travis integration. Here’s [tonight’s last commit](https://github.com/Wolfy87/vim-netrw-signs/commit/14e769dd281bffb6c7a77c30e6d33d2c65b47423). Baby steps are the way forwards.
