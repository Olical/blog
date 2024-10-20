# Getting started with Clojure, Neovim and Conjure in minutes

> 14-06-2020: Updated the post to reflect the current state of Conjure.

[Conjure](https://github.com/Olical/conjure) is my attempt at [Clojure](https://clojure.org/) and [ClojureScript](https://clojurescript.org/) tooling for [Neovim](https://neovim.io/). Here’s a quick demo of Conjure in action for those of you that haven’t seen it before.

<script id="asciicast-325517" src="https://asciinema.org/a/325517.js" async></script>

I’m going to help you get Clojure code evaluating in a matter of minutes, you’ll be able to try Conjure for yourself and see what it’s all about. All you’re going to need are the following.

* The [Clojure CLI](https://clojure.org/guides/getting_started) - I installed it through the Arch Linux package manager, you can probably do something similar.
* [Neovim](https://neovim.io/)
* A hot beverage to enjoy as you follow along.

## Grab plugin manager

I’m assuming you have nothing set up for Neovim right now, let’s get started with a plugin manager. Skip ahead if you already have a preferred way to install plugins!

I’ve used [vim-plug](https://github.com/junegunn/vim-plug) for years and I highly recommend it. Setup is really easy, let’s start out by fetching the script.

```bash
curl -fLo ~/.local/share/nvim/site/autoload/plug.vim --create-dirs \
    https://raw.githubusercontent.com/junegunn/vim-plug/master/plug.vim
```

Now we need to edit `~/.config/nvim/init.vim` to initialise it. I’m going to get you started with [vim-better-default](https://github.com/liuchengxu/vim-better-default), as you’d expect, it’s a set of great default configuration values.

```viml
" Specify a directory for plugins.
call plug#begin(stdpath('data') . '/plugged')

" Specify your required plugins here.
Plug 'liuchengxu/vim-better-default'

" Initialize plugin system.
call plug#end()
```

Now we can open Neovim and execute `:PlugInstall`. Have a look around the [vim-plug](https://github.com/junegunn/vim-plug) documentation to find more commands you can run for things like updating or pruning of unused plugins.

Whenever you make a change to your plugin configuration in `init.vim` you’ll need to reload the file by restarting Neovim. I think you might be able to `:source %` too but restarting Neovim is really quick. Once you’ve done that you can install or update whatever was specified.

## Optional (amazing) plugins

Although you can get away with _just_ Conjure, I’d highly recommend you also add at least some of these too. They’ll greatly improve your experience!

### https://github.com/easymotion/vim-easymotion[EasyMotion]

_The_ way to move around your buffer with ease, extremely useful when you’re jumping from form to form performing evaluations. The only downside is that editing without it in any other editor now feels far too cumbersome.

```viml
Plug 'easymotion/vim-easymotion'
```

### https://github.com/guns/vim-sexp[vim-sexp] and https://github.com/tpope/vim-sexp-mappings-for-regular-people[vim-sexp-mappings-for-regular-people]

These will take you a while to get the hang of but they’re so worth it. They allow you to structurally edit any lisp language, this means you can turn `(+ 10 20) 30` into `(+ 10 20 30)` with a key press all while keeping your cursor in the same place.

The learning curve is steep but optional, you can always edit the text manually. The more you learn the faster and more fluid you’ll get, it’s well worth investing some time into.

```viml
Plug 'guns/vim-sexp'
Plug 'tpope/vim-sexp-mappings-for-regular-people'
```

### https://github.com/Shougo/deoplete.nvim[Deoplete] and https://github.com/ncm2/float-preview.nvim[float-preview]

> Make sure you have Python `3.6.1` or greater installed before attempting to use Deoplete!

Conjure has completion built in via Neovim’s native [Omnicompletion](https://vim.fandom.com/wiki/Omni_completion) but I’d recommend you add an asynchronous autocompleter for maximum convinience.

```viml
Plug 'Shougo/deoplete.nvim'
Plug 'ncm2/float-preview.nvim'

" Place configuration AFTER `call plug#end()`!
let g:deoplete#enable_at_startup = 1
call deoplete#custom#option('keyword_patterns', {'clojure': '[\w!$%&*+/:<=>?@\^_~\-\.#]*'}) 
set completeopt-=preview

let g:float_preview#docked = 0
let g:float_preview#max_width = 80
let g:float_preview#max_height = 40
```

Conjure comes bundled with the Python code required to hook itself into Deoplete automatically, combine that with float-preview and suddenly you have automatic completions popping up with their documentation alongside it as you type.

> If Deoplete isn’t your thing you might want to try [Conquer of Completion](https://github.com/neoclide/coc.nvim) (CoC for short!). [coc-conjure](https://github.com/jlesquembre/coc-conjure) will provide Conjure support, I think Coc works with language servers out of the box too, it’s definitely worth considering.
>
> I’ve just always been a Deoplete fan and don’t find myself using language servers. The plugins being JavaScript dependencies always felt a little odd to me too although that’s a minor silly point.

### https://github.com/jiangmiao/auto-pairs[auto-pairs]

Simply a really handy way to keep your pairs of characters in check, not quite essential but nice to have.

```viml
Plug 'jiangmiao/auto-pairs', { 'tag': 'v2.0.0' }
```

### https://github.com/w0rp/ale[ALE]

The Asynchronous Lint Engine, when combined with [clj-kondo](https://github.com/borkdude/clj-kondo) and [joker](https://github.com/candid82/joker), is an indispensable tool. It’s caught countless typos and brain farts for me _before_ I’ve had a chance to evaluate the code.

```viml
Plug 'w0rp/ale'

" Place configuration AFTER `call plug#end()`!
let g:ale_linters = {
      \ 'clojure': ['clj-kondo', 'joker']
      \}
```

## https://github.com/liuchengxu/vim-clap[vim-clap]

If you want to get into Neovim properly you’re probably going to want a way to find things in your project, that’s where [vim-clap](https://github.com/liuchengxu/vim-clap) comes in.

It allows you to hook into various searching tools such [ripgrep](https://github.com/BurntSushi/ripgrep) in a pretty floating window. You’ll have to spend a little while setting up your mappings to each command but it’s well worth the investment. You can find my configuration in [my dotfiles](https://github.com/Olical/dotfiles/blob/29f47aaaa279769ea82367a4ff4a3c5916d2c082/neovim/.config/nvim/modules/plugins/vim-clap.vim).

```viml
Plug 'liuchengxu/vim-clap'

" Configuration from my dotfiles.
let g:clap_provider_grep_delay = 50
let g:clap_provider_grep_opts = '-H --no-heading --vimgrep --smart-case --hidden -g "!.git/"'

nnoremap <leader>* :Clap grep ++query=<cword><cr>
nnoremap <leader>fg :Clap grep<cr>
nnoremap <leader>ff :Clap files --hidden<cr>
nnoremap <leader>fb :Clap buffers<cr>
nnoremap <leader>fw :Clap windows<cr>
nnoremap <leader>fr :Clap history<cr>
nnoremap <leader>fh :Clap command_history<cr>
nnoremap <leader>fj :Clap jumps<cr>
nnoremap <leader>fl :Clap blines<cr>
nnoremap <leader>fL :Clap lines<cr>
nnoremap <leader>ft :Clap filetypes<cr>
nnoremap <leader>fm :Clap marks<cr>
```

Update: I’m actually using [fzf.vim](https://github.com/junegunn/fzf.vim) now and loving it so far! Clap is still great, it’s down to personal preference.

## Adding Conjure

Installing the plugin is the same as any other.

> I’m specifying the latest tagged version at the time of writing this post, have a look at the [Conjure](https://github.com/Olical/conjure) repository to find the latest version and decide if you would like to use that instead.
>
> I’d highly recommend you subscribe to new releases through GitHub’s UI, that way you can be notified automatically and update to newer versions when it suits you.

```viml
Plug 'Olical/conjure', { 'tag': 'v4.3.1' }
```

If you haven’t already, be sure to execute `:PlugInstall` within Neovim to ensure all of your plugins are installed and set up correctly.

If you’d prefer to give Conjure a go without changing your config you can launch `:ConjureSchool` through this script that will temporarily download the plugin for you. You can then learn the UX of Conjure through evaluating some [Fennel](https://github.com/bakpakin/Fennel) without the install step.

```bash
curl -fL conjure.fun/school | sh
```

If you have the plugin installed you can run `:ConjureSchool` to get the same tutorial experience.

> Beware! `vim-sexp` isn’t configured to work in Fennel buffers by default so it won’t work in the school. You need to add `fennel` to `g:sexp_filetypes` to use it during the school.

## Using Conjure to evaluate Clojure

Start an nREPL server (with CIDER middleware) in a terminal.

```bash
clojure -Sdeps '{:deps {nrepl {:mvn/version "0.7.0"} cider/cider-nrepl {:mvn/version "0.25.0"}}}' -m nrepl.cmdline --middleware '["cider.nrepl/cider-middleware"]'
```

Now open any Clojure file with Neovim and start to edit and eval as you were taught in `:ConjureSchool`. You can consult `:h conjure` and `:h conjure-client-clojure-nrepl` for more information, configuration and mappings.

```bash
nvim foo.clj

# Edit and evaluate away!
```

That’s all there is to it! If you’re interested, you can also use Conjure to evaluate [Fennel](https://github.com/Olical/conjure/wiki/Quick-start:-Fennel-(Aniseed)) or [Janet](https://github.com/Olical/conjure/wiki/Quick-start:-Janet-(netrepl)). There’s even talk of a [Common Lisp client](https://github.com/Olical/conjure/issues/97)!

## Configuration round up

Your full `~/.config/nvim/init.vim` may look a little something like this.

```viml
" Specify a directory for plugins.
call plug#begin(stdpath('data') . '/plugged')

" Specify your required plugins here.
Plug 'liuchengxu/vim-better-default'

" Optional useful plugins I highly recommend.
Plug 'easymotion/vim-easymotion'
Plug 'guns/vim-sexp'
Plug 'tpope/vim-sexp-mappings-for-regular-people'
Plug 'Shougo/deoplete.nvim'
Plug 'ncm2/float-preview.nvim'
Plug 'jiangmiao/auto-pairs', { 'tag': 'v2.0.0' }
Plug 'w0rp/ale'

" I skipped vim-clap but feel free to add it!

" Conjure! :D
Plug 'Olical/conjure', { 'tag': 'v4.3.1' }

" Initialize plugin system.
call plug#end()

" Configuration for various plugins.
let g:deoplete#enable_at_startup = 1
call deoplete#custom#option('keyword_patterns', {'clojure': '[\w!$%&*+/:<=>?@\^_~\-\.#]*'})
set completeopt-=preview

let g:float_preview#docked = 0
let g:float_preview#max_width = 80
let g:float_preview#max_height = 40

let g:ale_linters = {
      \ 'clojure': ['clj-kondo', 'joker']
      \}
```

There’s not much to it but we’ve already got a fully Clojure integrated editor! Head over to the [Conjure](https://github.com/Olical/conjure) repo and [wiki](https://github.com/Olical/conjure/wiki) to find out more about configuration, mappings and features.

I hope this post as well as the others I’ve linked to are enough to get you started and hooked on Conjure. Please do get in touch with any questions, thoughts or feelings on the project. You can find me on twitter (link in the footer) as well as `#conjure` in the [Clojurians Slack](http://clojurians.net/).
