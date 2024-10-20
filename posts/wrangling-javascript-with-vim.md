# Wrangling JavaScript with Vim

I’ve created a [few](/equipping-vim-for-javascript/) [posts](/2014/11/21/essential-vim-bundles-for-javascript-and-clojure/) on here about writing JavaScript with Vim, this is because it’s what I do all day (and night) long, I’ve refined my editor to make writing this slightly questionable language as easy as possible. I’ve now hit a point, once again, where I feel like I should share my tooling for everyone else to take advantage of. You can find everything I’m going to talk about in my [dotfiles](https://github.com/Wolfy87/dotfiles) under the Vim directory if you’re curious.

## Plugin management

I’ve gone through a few different systems since starting to use Vim, I’ve finally settled on one that’ll be tough to beat: [vim-plug](https://github.com/junegunn/vim-plug). A good plugin manager is essential to any good Vim configuration, in my opinion. It allows me to easily add, remove and update plugins as well as keep them in sync across machines. The parallel processing and optional deferred loading are excellent features.

My configuration is modularised by having [bootstrap.vim](https://github.com/Wolfy87/dotfiles/blob/9c5f008620287bb495e56452123d6bff76bb4639/vim/.vim/bootstrap.vim) load all files in my [modules](https://github.com/Wolfy87/dotfiles/tree/9c5f008620287bb495e56452123d6bff76bb4639/vim/.vim/modules) directory, one of these is [plugins.vim](https://github.com/Wolfy87/dotfiles/blob/9c5f008620287bb495e56452123d6bff76bb4639/vim/.vim/modules/plugins.vim) which configures vim-plug and then loads the individual configuration files for each plugin from [modules/plugins](https://github.com/Wolfy87/dotfiles/tree/9c5f008620287bb495e56452123d6bff76bb4639/vim/.vim/modules/plugins). This allows [my actual plugin list](https://github.com/Wolfy87/dotfiles/blob/9c5f008620287bb495e56452123d6bff76bb4639/vim/.vim/plugins.vim) to remain clean and concise. I recommend following a similar pattern to keep your configuration clean.

The point of this section being: Make sure you have a good plugin manager and a nice place to list your plugins as well as their configuration. It’ll help keep things clean. Feel free to copy my system exactly, in fact, I encourage it. Fork my dotfiles if you so wish.

## Essential plugins

I’m going to list a fairly exhaustive list of every plugin I use that can help with JavaScript development. There’s going to be a lot here, but I’ll try to justify each one as best as I can. Adding all of them will have no impact on the performance of your editor (unless you’re on a Raspberry Pi for example) and vim-plug will fetch / update them incredibly quickly. I actually use [a lot more](https://github.com/Wolfy87/dotfiles/blob/9c5f008620287bb495e56452123d6bff76bb4639/vim/.vim/plugins.vim) than this, but these are some of the most relevant.

* [.pl-s]#[Lokaltog/vim-easymotion](https://github.com/Lokaltog/vim-easymotion) – _The_ way to navigate a file quickly, regardless of language.\
#
* [PeterRincker/vim-argumentative](https://github.com/PeterRincker/vim-argumentative) – Allows you to change the order of arguments with ease.
* [Raimondi/delimitMate](https://github.com/Raimondi/delimitMate) – Automatically match pairs intelligently.
* [.pl-s]#[Valloric/YouCompleteMe](http://github.com/Valloric/YouCompleteMe) – The best completion engine I’ve found.\
#
* [Olical/vim-enmasse](http://github.com/Olical/vim-enmasse) – My own, allows you to edit your quickfix list and write the changes to their files. Like find and replace, but better.
* [ctrlpvim/ctrlp.vim](http://github.com/ctrlpvim/ctrlp.vim) – _The_ way to jump around your code base by rough file names.
* [helino/vim-json](http://github.com/helino/vim-json) – We work with a lot of it, show it some love.
* [junegunn/vim-easy-align](http://github.com/junegunn/vim-easy-align) – Makes those funny alignment issues trivial.
* [marijnh/tern_for_vim](http://github.com/marijnh/tern_for_vim) – Provides pretty good completion in JavaScript, works great with YouCompleteMe. Checkout the [tern](http://ternjs.net/) website for more information.
* [mhinz/vim-signify](http://github.com/mhinz/vim-signify) – Git info in the gutter.
* [pangloss/vim-javascript](http://github.com/pangloss/vim-javascript) – My favourite JavaScript syntax plugin of them all so far. (has great conceal features which I’ll talk about below)
* [rking/ag.vim](http://github.com/rking/ag.vim) – We have to search for a lot of stuff across a lot of files, [Ag](http://geoff.greer.fm/ag/) does it best.
* [scrooloose/syntastic](http://github.com/scrooloose/syntastic) – Provides automatic JSHint linting. (among many other JavaScript linters, check the docs)
* [Too many to list](https://github.com/Wolfy87/dotfiles/blob/9c5f008620287bb495e56452123d6bff76bb4639/vim/.vim/plugins.vim#L40-L57) from [tpope](https://github.com/tpope), but honestly, I use almost all of these on a daily basis. Feel free to pick and choose any that sound cool, [vim-sensible](https://github.com/tpope/vim-sensible) is a start and [vim-surround](https://github.com/tpope/vim-surround) is essential.

## Project specific configuration

I use [embear/vim-localvimrc](http://embear/vim-localvimrc) to provide project specific functionality such as executing tests or running the build. Because this is something that isn’t universal, having a _.lvimrc_ to hand is extremely useful. I have the following binding defined at the moment so I can just hit “&lt;[localleader](http://learnvimscriptthehardway.stevelosh.com/chapters/06.html#local-leader)>tt” (which is “|tt” right now for me) to ***t****est **t***his file, it doesn’t matter if I’m in the source or test file, it just works.

```
command! Test Dispatch grunt test-dev --filter %:t:r
nnoremap <localleader>tt :Test<CR>
```

This is relying on [vim-dispatch](https://github.com/tpope/vim-dispatch) to make the whole thing asynchronous. It actually executes in a tmux split and pulls the results into my Vim quickfix list when done.

I also have my _path_ set to some greedy globs and _suffixesadd_ set to contain _.js_. This allows me to press _gf_ ([open file under cursor](http://vim.wikia.com/wiki/Open_file_under_cursor)) on the following require statement, and it will actually take me to the source (if it exists)!

```
// Where httpService.js exists somewhere in this project.
// I press gf within the quotes and it will take me there.

var httpService = require('httpService');
```

And here’s the configuration I’m currently using for that particular bit of magic.

```
set path+=**/src/main/**,**
set suffixesadd+=.js
```

## Snippets

I use [UltiSnips](https://github.com/SirVer/ultisnips) to manage my snippets, which is a fantastic tool. The key to snippets, however, is to not have too many. That is why [I only have three](https://github.com/Wolfy87/dotfiles/blob/9c5f008620287bb495e56452123d6bff76bb4639/vim/.vim/UltiSnips/javascript.snippets) at the time of writing. It’s pretty obvious what they’re for, the most used being _fn_. Having a few for your most common patterns is a good idea, but delete them if you find you’re not using them, keep your snippets clean.

## Concealing

Concealing is a neat (and relatively new) feature in Vim that allows you to mask a set of characters as a single character. It just so happens that [vim-javascript](https://github.com/pangloss/vim-javascript) has some excellent conceal configuration that’s easy to use and very effective. Here’s what I currently use, it’s pretty self explanatory.

```
" General conceal settings. Will keep things concealed
" even when your cursor is on top of them.
set conceallevel=1
set concealcursor=nvic

" vim-javascript conceal settings.
let g:javascript_conceal_function = "λ"
let g:javascript_conceal_this = "@"
let g:javascript_conceal_return = "<"
let g:javascript_conceal_prototype = "#"
```

This is all well and good, but it got me thinking, what if I could just press the @ key and have it expand to “this” but still show an @ through conceal? Essentially creating a cute little language on top of JavaScript within Vim that’s arguably easier to write and read.

## Expanding and concealing

This is where my [vim-syntax-expand](https://github.com/Wolfy87/vim-syntax-expand) plugin comes in. Here’s a quick demo to give you an idea of what I mean if my previous paragraph was not clear.

[![image](https://asciinema.org/a/ag49t530108fu0qp2cuefondl)(https://asciinema.org/a/ag49t530108fu0qp2cuefondl.png)]

I can write concealed characters and see concealed characters, but it actually writes the real JavaScript to the file. It’s caused some people to be slightly confused when looking at my screen, but normal JavaScript is only a _set conceallevel=0_ away. And yes, it’s intelligent enough to not expand in comments or strings, so you can still type @ or # when required.

The &lt; to “return” mapping uses a special rule that will only work if you’re at the beginning of a line, so you can still type “&lt;=”. Here’s my full configuration, which is also featured in the repository README.md.

```
" Map the conceal characters to their expanded forms.
inoremap <silent> @ <C-r>=syntax_expand#expand("@", "this")<CR>
inoremap <silent> # <C-r>=syntax_expand#expand("#", "prototype")<CR>
inoremap <silent> < <C-r>=syntax_expand#expand_head("<", "return")<CR>

" Keeps everything concealed at all times. Even when my cursor is on the word.
set conceallevel=1
set concealcursor=nvic

" JavaScript thanks to pangloss/vim-javascript
let g:javascript_conceal_function = "λ"
let g:javascript_conceal_this = "@"
let g:javascript_conceal_return = "<"
let g:javascript_conceal_prototype = "#"
```

I’ve been using it for around and week so far and it feels great. I guess I’m just yearning for a more concise functional language. I type _fn_, hit “&lt;C-j>” and I get a lambda symbol and a block to write in. Then I can return true by typing “&lt; true”.

This is the sort of thing Vim is amazing at, removing the cruft between you and your text, so you can edit without thinking and concentrate on the problem at hand. Yes it takes practice to use efficiently, but so does every good tool.

[#eow-title .watch-title]#This is Shia LaBeouf responding to your “should I give Vim a go?” thoughts.\
#
