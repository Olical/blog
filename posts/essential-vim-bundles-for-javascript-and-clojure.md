# Essential Vim bundles for JavaScript and Clojure

_I mention using NeoBundle in this post, but I’ve since made the switch to [vim-plug](https://github.com/junegunn/vim-plug). Do it, it’s wonderful._

My two primary languages that go through Vim right now are JavaScript and Clojure. Obviously there’s others such as HTML, CSS and the odd bit of Java, but these two stand out since they can have astounding tooling if you select carefully. So can Java if you wish to go down the [eclim](http://eclim.org/) route (which I may well do at some point) as one of my colleagues has.

This is just a quick dependency list for those already using the languages or are just starting out. I feel that they are essential. To install these you will need a bundle manager, I recommend [NeoBundle](https://github.com/Shougo/neobundle.vim), [Vundle](https://github.com/gmarik/Vundle.vim) or [vim-plug](https://github.com/junegunn/vim-plug). I am currently using NeoBundle but may well swap to vim-plug soon for the minimalism and impressive parallelism.

## Common bundles

These are extremely useful for either language, and generally enhance Vim in many ways. So really, these will improve your experience no matter what you write.

* [YouCompleteMe](https://github.com/Valloric/YouCompleteMe) – Fast automatic code completion. Works well with completion engines and falls back to a really good fuzzy string search. So good for plain text or languages without a completer.
* [syntastic](https://github.com/scrooloose/syntastic) – Linting using a multitude of backends. Make sure you have JSHint installed for JavaScript! (_npm install -g jshint_)
* [vim-distinguished](https://github.com/Lokaltog/vim-distinguished) – An excellent theme if you don’t already have a preference, especially for JavaScript.
* [vim-projectionist](https://github.com/tpope/vim-projectionist) – Project configuration to create associations between files and other goodies such as pre-filling a file with a JavaScript AMD module definition on creation. I can execute _:AV_ to open the alternate file to the one I’m currently editing, this could be the tests for this source or the other way around.
* [vim-localvimrc](https://github.com/embear/vim-localvimrc) – Have per-project configuration, I have things like _&lt;localleader>tt_ mapped to execute the very specific command to ***t****est **t***his file.
* [vim-easymotion](https://github.com/Lokaltog/vim-easymotion) – Jump to anywhere. This is essential for anything in Vim.

## JavaScript

The common bundles pretty much cover JavaScript, but there’s still a few specific things you need.

* [jshint.vim](https://github.com/wookiehangover/jshint.vim) – As well as having JSHint installed as a global node module for syntastic, I’d highly recommend this to actively check things and add them to the quickfix list.
* [tern_for_vim](https://github.com/marijnh/tern_for_vim) – [Tern](http://ternjs.net/) omnicomplete support that YouCompleteMe hooks in to. Works very well if set up correctly. It can even infer or read types from [JSDoc](http://usejsdoc.org/) comments.
* [vim-javascript](https://github.com/pangloss/vim-javascript) – Provides improved syntax and indentation.

## Clojure

There’s a few more for Clojure and they each have their own learning curves, namely paredit and fireplace. It’ll take you a while to get proficient, but you’ll be very happy when you get there.

* [rainbow_parentheses.vim](https://github.com/kien/rainbow_parentheses.vim) – Makes Clojure’s endless parentheses even more pretty. Colours pairs of parentheses for you, which also works rather well for other languages too!
* [paredit.vim](https://github.com/vim-scripts/paredit.vim) – Stops you from unbalancing the parentheses and provides a plethora of bindings for manipulating the file.
* [vim-fireplace](https://github.com/tpope/vim-fireplace) – Bridges Vim to a REPL process. Can even spin one up for you if you use [vim-dispatch](https://github.com/tpope/vim-dispatch)!
* [vim-clojure-static](https://github.com/guns/vim-clojure-static) – Better static highlighting.
* [vim-clojure-highlight](https://github.com/guns/vim-clojure-highlight) – More dynamic highlighting that tries to fetch context and more information from fireplace. An extension to static.

## There’s a lot more

You have to discover some things for yourself because you probably don’t like everything I do. Saying that, here’s my [dotfiles](https://github.com/Wolfy87/dotfiles) that you can fork, copy, steal or retrofit to fit your needs.
