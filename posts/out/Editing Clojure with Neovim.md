---
alias: editing-clojure-with-neovim
tags:
- blog-post
- imported-blog-post
---


I’ve used [Spacemacs](http://spacemacs.org/) since I started working with [Clojure](https://clojure.org/) a few years ago, it’s an extremely powerful system on par with full IDEs such as [Cursive](https://cursive-ide.com/).
I highly recommend either of these tools to the budding Clojure(Script) developer, they will carry you as far as you need to go and beyond.

The reason I have drifted back to [Vim](https://www.vim.org/) ([Neovim](https://neovim.io/) specifically) is because I never felt quite at home within [Emacs](https://www.gnu.org/software/emacs/), which Spacemacs is built upon.
I wrote JavaScript (among other languages) in Vim for around five years before I began really studying Clojure.
Vim and it’s nuances are pretty deeply buried within my brain and muscle memory (if that’s actually a thing).

I’ve been working on a fresh Neovim setup in my [dotfiles](https://github.com/Olical/dotfiles) repository and I’m finally at a point where I’m happy with it for day to day work.
I extracted my current setup into [spacy-neovim](https://github.com/Olical/spacy-neovim) for others to fork and build upon in their own repositories.
It acts as an opinionated starting point modeled after Spacemacs that you’re expected to modify to fit your needs.

This post will mainly be describing the approach I’m taking in my dotfiles and the spacy-neovim repository.

## Structure

The layout of my configuration is almost identical to my previous JavaScript setup:

* There’s a top level entry point: `init.vim`, it sources every file in the `modules/` directory.
* A file in the root directory, `plugins.vim`, simply lists all of my dependencies for [vim-plug](https://github.com/junegunn/vim-plug) to handle.
* The `modules/` directory contains different configuration related files.
* `modules/core.vim` - Super basic and general configuration for the entire editor.
* `modules/mappings.vim` - Custom mappings for things like closing hidden buffers or trimming whitespace.
* `modules/plugins.vim` - Activates vim-plug and loads configuration files for those plugins.
It also warns you if you’ve remove a plugin but not the configuration file on startup.
* Plugin configuration files are found in `modules/plugins/`, like `modules/plugins/vim-fireplace.vim`.
They set plugins up and define useful bindings to access their functionality.

Speaking of which, let’s have a tour of some of the most important plugins that are included.

## Plugins

### https://github.com/tpope/vim-fireplace[tpope/vim-fireplace]

This is essential.
If you wish to edit Clojure within Vim you’ll need this plugin above all others, it gives you a way to interact with and evaluate your Clojure code via an [nREPL](https://github.com/clojure-emacs/cider-nrepl) connection.

I’ve defined the following bindings in `modules/plugins/vim-fireplace.vim`, they make using it feel a little more like [CIDER](https://github.com/clojure-emacs/cider) (The Clojure Interactive Development Environment that Rocks for Emacs).

```viml
autocmd FileType clojure nnoremap <buffer> <localleader>re :Eval<cr>
autocmd FileType clojure vnoremap <buffer> <localleader>re :Eval<cr>
autocmd FileType clojure nnoremap <buffer> <localleader>rf :%Eval<cr>
autocmd FileType clojure nnoremap <buffer> <localleader>rr :Require<cr>
autocmd FileType clojure nnoremap <buffer> <localleader>rR :Require!<cr>
autocmd FileType clojure nnoremap <buffer> <localleader>rt :RunTests<cr>
autocmd FileType clojure nnoremap <buffer> <localleader>rl :Last<cr>
autocmd FileType clojure nnoremap <buffer> <localleader>rc :FireplaceConnect<cr>
autocmd FileType clojure nnoremap <buffer> gd :normal [<c-d><cr>
```

My `localleader` is set to `,` so I usually hit `,rc` to connect to a REPL then a mixture of `cpp` to evaluate the innermost form and `,re` to evaluate the outermost.

I encountered some features I missed from CIDER, namely being able to reload every namespace and reboot the system with a single binding.
I also created some bindings to automatically connect to my API and UI REPLs at work, I have this `.lvimrc` defined within my work repo.

```viml
autocmd FileType clojure nnoremap <buffer> <silent> <localleader>rA :FireplaceConnect nrepl://localhost:9001 %{getcwd()}<cr>
autocmd FileType clojure nnoremap <buffer> <silent> <localleader>rU :FireplaceConnect nrepl://localhost:9002 %{getcwd()}<cr>:e dev/user.clj<cr>:Eval (cljs-repl)<cr>:bd<cr>
autocmd FileType clojure nnoremap <buffer> <silent> <localleader>rx :Eval (do (require 'clojure.tools.namespace.repl) (bounce.system/stop!) (clojure.tools.namespace.repl/set-refresh-dirs "src/clj" "src/cljc") (clojure.tools.namespace.repl/refresh :after 'bounce.system/start!))<cr>
autocmd FileType clojure nnoremap <buffer> <silent> <localleader>rX :Eval (do (require 'clojure.tools.namespace.repl) (bounce.system/stop!) (clojure.tools.namespace.repl/clear) (clojure.tools.namespace.repl/set-refresh-dirs "src/clj" "src/cljc") (clojure.tools.namespace.repl/refresh-all :after 'bounce.system/start!))<cr>
```

* `,rA` connects me to my API REPL.
* `,rU` connects me to the UI REPL and evaluates the figwheel piggieback code to hook it up to my browser.
* `,rx` shuts the system down, reloads changed namespaces and starts it back up.
* `,rX` shuts the system down, reloads _every_ namespace and starts it back up.

Once I have my API or UI REPL connection up all evaluation and autocompletion works for the given context, so that’ll either be Clojure or ClojureScript.
I can only have one connection at a time, but that connection works really damn well.

### https://github.com/guns/vim-sexp[guns/vim-sexp]

[EasyMotion](https://github.com/easymotion/vim-easymotion) is still my favourite way to navigate but vim-sexp is my favourite way to edit.
I use it in place of [evil-lispy](https://github.com/sp3ctum/evil-lispy) in Emacs to manipulate s-expressions.

The default bindings involve holding down a few modifiers like `ctrl` and `alt` and pressing a mixture of `hjkl` keys which I actually quite like.
I still have [vim-sexp-mappings-for-regular-people](https://github.com/tpope/vim-sexp-mappings-for-regular-people) installed but I’ve found myself using it less and less as I learn the extensive bindings of vim-sexp.

Maybe my slightly esoteric custom ErgoDox keyboard layout makes that easier than the bindings are on a regular keyboard.
Luckily tpope’s enhancement plugin provides more than a couple of simple rebindings so I’d highly recommend you use it even if you don’t mind the meta key shenanigans.

### So many more

The best way to explore this setup is to skim the README in [spacy-neovim](https://github.com/Olical/spacy-neovim) as well as browse the source (which is pretty short).
I list a few more of the features and link to their configuration source in the repository, you may find that useful.

## :wq

I hope this repository helps you to get started with your own Neovim and Clojure editor setup.
If you’ve tried Cursive and Spacemacs but decided it’s not for you and you need to return to Vim I’m here to tell you that’s it’s totally possible.

Use what I’ve made as a starting point to get a bunch of low hanging fruit out of the way then customise your editor to your needs.

Feel free to reach out to me in the comments or on twitter ([@OliverCaldwell](https://twitter.com/OliverCaldwell)) if you have any questions or insights for me.
