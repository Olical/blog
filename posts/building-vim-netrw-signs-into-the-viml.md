# Building vim-netrw-signs: Into the VimL

In this post I’m going to aim for an MVP that may not be the most usable but can serve as a proof of concept. This version will only run when you call a command and have minimal configuration. Polishing of the plugin will come after in the form of help pages, configuration and automatic execution.

The very first step involves creating the main plugin file which is loaded from the runtime path and defines commands, automatic as well as normal. These commands will point to a namespaced file which will be loaded on demand, this is all contained within the `autoload` directory.

## The initial files

First `plugin/netrw_signs.vim`, which points to an autoload function.

```
command! NetrwSigns :call netrw_signs#SignBuffer()
```

Then we can create the “Hello, World!” function on the other end of this command in `autoload/netrw_signs.vim`.

```
function! netrw_signs#SignBuffer()
  echo "Hello, World!"
endfunction
```

After adding the project directory to my runtime path (`set rtp+=$HOME/.../vim-netrw-signs` in `~/.vimrc`) I can open Vim and execute `:NetrwSigns` to run my new function. This prints “Hello, World!” to the bottom of my screen. Obviously the plugin still lacks a fair bit of functionality.

I will also add a way to fetch the version number, as I did with vim-enmasse.

```
" plugin/netrw_signs.vim
command! NetrwSignsVersion :echo netrw_signs#GetVersion()

" autoload/netrw_signs.vim
function! netrw_signs#GetVersion()
  return "1.0.0"
endfunction
```

Next, we must build an implementation behind the `SignBuffer` function, so that involves building the entire thing. No big deal. But first, let’s test this stupidly simple version function in `tests/version.vader`.

```
Before (set up regular expression):
  let versionRegExp = '\v\d+.\d+.\d+'

Execute (can print the version number with the command):
  redir =&gt; messages
  NetrwSignsVersion
  redir END

  let result = get(split(messages, "\n"), -1, "")

  Assert result =~# versionRegExp

Execute (can get the version number with the function):
  Assert netrw_signs#GetVersion() =~# versionRegExp
```

I also had to add my project folder to my runtime path to get this to work in `test.sh`.

```
vim -Nu &lt;(cat &lt;&lt; EOF
filetype off
set rtp+=$vader
set rtp+=. &lt;-- This thing.
filetype plugin indent on
EOF) +Vader tests/*.vader
```

## High level testing

I’m going to use my tests to define how the plugin will actually work. Some may say that I’m driving my development with tests. These will be high level but provide me with all the checks I need to make sure my basic configuration is actually producing the desired results and signs. One of the best things about writing the tests up front is that my configuration will be thought out in a way that makes sense from the users perspective, I’ll then work back from there.

Here’s my preliminary configuration I’ll be using in my root high level tests. Below it is my thought process in pseudo-English.

```
let g:netrw_signs_checks={
  \'contains-hyphen': 'ContainsHyphen',
  \'contains-upper-case': 'ContainsUpperCase'
\}
let g:netrw_signs_styles={
  \'error': 'text=&gt;&gt; texthl=ErrorMsg'
  \'warning': 'text=&gt;&gt; texthl=WarningMsg'
\}
let g:netrw_signs_bindings={
  \'contains-hyphen': 'error',
  \'contains-upper-case': 'warning'
\}
```

```
Let the check contains-hyphen use the function ContainsHyphen.
Let the check contains-upper-case use the function ContainsUpperCase.

Let the error sign have these arguments passed to it's definition within Vim.
Let the warning sign have these arguments passed to it's definition within Vim.

Let the contains-hyphen check show the error sign if it returns true.
Let the contains-upper-case check show the warning sign if it returns true.
```

As far as I can tell so far, this is all the logic I will need to configure the plugin. This should allow the user more than enough power, it will even allow you to hook into git, which is my end goal. The one thing I’m not so sure about right now is how I’ll execute the functions by name reliably. It should “just work”, but I may encounter some problems with that later on. I also need to work out the format for the check functions return values.

My current thinking is for each check function to either get called once with each line of the netrw buffer or, alternatively, to pass an array of all lines to the function. The function would then run a map over that array and return an array of booleans. The one line approach allows for simple functions and the heavy lifting on my end (probably involving maps), the other approach involves heavier check functions but the chance for optimisations if you had to call `git status` for every line, for example.

With those implementation details in mind, I’ll write my first tests against this configuration.

## Uh oh…

It is at this point that I realised how much work will actually be involved to get a working and tested MVP that didn’t die when it encountered tiny changes in netrw configuration. I don’t have time for this sort of return on investment, so I’m shelving this little project to learn about Clojure and algorithms using this stack of books I’ve accumulated. I’m obviously going to push everything I’ve got so far alongside these few posts, and I may even come back to it one day. Until then, sorry vim-netrw-signs, you’re dead to me.
