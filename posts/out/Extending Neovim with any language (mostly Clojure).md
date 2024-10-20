---
alias: extending-neovim-with-any-language-mostly-clojure
tags:
- blog-post
- imported-blog-post
---


> This post is mainly to help me plan my talk at [Vim London](https://www.meetup.com/Vim-London/events/262032144/) on 3rd July 2019 on the same topic.
> Come along if you can!

You may have seen me constantly tweeting about [Conjure](https://github.com/Olical/conjure) (Neovim Clojure(Script) tooling over prepl) on twitter at [@OliverCaldwell](https://twitter.com/OliverCaldwell), what you may not know is that it’s my third re-write of the project already.
I originally attempted to write it in Rust, then ClojureScript and finally Clojure.

The current state is a little more complex than that though!
According to GitHub Conjure is made up of the following:

* Clojure `81.5%`
* Vim script `10.3%`
* Lua `3.7%`
* Python `2.6%` (only for the [Deoplete](https://github.com/Shougo/deoplete.nvim) integration)
* Shell `1.8%` (mostly just for tests and builds)

Let’s look into the different ways to write plugins in Neovim (and regular Vim to a slightly lesser extent) and how to avoid the pitfalls of remote plugin development.

## Traditional plugins

Vim plugins have always been written in Vim Script (or VimL), it’s a pretty messy language that’s hard to learn and even harder to master.
There’s a lot of good information out there on the topic (such as [Writing Vim Plugins](http://stevelosh.com/blog/2011/09/writing-vim-plugins/)) but it’s still _really_ hard.

Not only is it hard to actually learn and use, you only get one thread which happens to be the same one as Vim’s UI.
This means if you have a CPU intensive plugin it’ll noticeably block Vim’s UI.
To make matters worse, VimL is pretty slow in the first place so it’s _easy_ to write something sluggish.

Despite all of this, it’s still required in some places and always will be so it’s worth understanding to some extent.
Here’s an example function that calculates Fibonacci numbers to show you some of the language.

```viml
function! s:fib(n)
  let l:a = 0
  let l:b = 1
  let l:t = 0

  let l:i = 0

  while i < a:n
    let t = a + b
    let a = b
    let b = t
    let i += 1
  endwhile

  return a
endfunction

echo s:fib(10)
" => 55
```

We usually store our code inside files under special directory names at the root of a repository.

* `plugin/foo.vim`
* `autoload/foo.vim`
* `doc/foo.txt`

We can then depend on the plugin via a plugin manager such as [vim-plug](https://github.com/junegunn/vim-plug) which will load the files for us.
[Learn Vimscript the Hard Way](http://learnvimscriptthehardway.stevelosh.com/) is an invaluable resource for writing the VimL parts of your plugins.

## Lua enters the fray!

Amazingly, Neovim has [LuaJIT](https://luajit.org/) built into the core binary.
This means we have full [Lua](https://www.lua.org/) support natively within the editor without any overhead of communicating to external processes, it’s _within_ the process.

This should mean you can block the UI by running a lot of Lua, something I haven’t tried.
Luckily, LuaJIT is one of the fastest languages out there ([Why is Lua so fast?](https://www.quora.com/Why-is-Lua-so-fast)), just look at these (possibly contrived) benchmarks!

* 22.29s C
* 23.29s LuaJIT
* 26.33s PyPy
* 54.30s Java
* 92.94s NodeJS
* 159.93s Lua
* 416.55s Python
* ????.??s Vim Script 🤔

> So, I think we can say Lua is fucking fast for a script language.
>
> &mdash;
> Hanno Behrens

Lua gives us the benefit of a polished and _fast_ language without leaving Neovim.
Here’s how neat Fibonacci looks in Lua.

```lua
function fib(n)
  a, b = 0, 1

  for i = 1, n do
    a, b = b, a + b
  end

  return a
end

print(fib(10))
-- => 55
```

That’s so much easier to work with and will run so very much faster.
We’ll still need to use some VimL to write our commands and mappings but now we can call through to our clever Lua functions which have access to the entirety of Neovim’s API.

Here’s a tiny extract from Conjure’s Lua module, `lua/conjure.lua`.

```lua
local conjure = {}

-- ...

-- Close the log window if it's open in the current tabpage.
function conjure.close_log(log_buf_name)
  local match = find_log(log_buf_name)
  if match.win then
    local win_number = vim.api.nvim_win_get_number(match.win)
    vim.api.nvim_command(win_number .. "close!")
  end
end

return conjure
```

### You can _technically_ compile JavaScript to lua...

Using something like [castl](https://github.com/PaulBernier/castl) or [js2lua](https://github.com/wizzard0/js2lua) you can compile some JavaScript to Lua which could come in pretty handy.
What they probably didn’t intend was for me to compile [ClojureScript](https://clojurescript.org/) to JavaScript and then to Lua.

Now if you think that concept is scary, what’s more terrifying is that it actually _worked_, sort of.
I got a "Hello, World!" out of it but it exploded when I tried to use `core.async` or self hosting.
It mostly complained about too many local variables, apparently LuaJIT has an upper limit on that.

Can you imagine self hosted ClojureScript running in Neovim directly?
Now stop.
It’s an awful idea and will lead to so much pain further down the line when something breaks subtly, here be dragons.
You have been warned.

Worryingly, it almost worked.
Check out [cljs-lua-experiment](https://github.com/Olical/cljs-lua-experiment) to see what I was playing with.

## Going remote

Neovim supports the concept of [remote plugins](https://neovim.io/doc/user/remote_plugin.html).
These are programs started by Neovim as a child process that it communicates with over msgpack RPC (through `stdio`) allowing use of the same API that Lua has access to, albeit with interprocess communication overhead.

The main downside to this is that every request and response has a round trip time as the message is encoded, decoded and handled, this can get noticeable fairly quickly.
The upside is that we’re no longer tied to Neovim’s UI thread, we can spawn our own threads in whatever language we see fit!

In my case, this means a Clojure process doing whatever it wants and calling back to Neovim when it needs some information or wants to change something within the editor such as displaying virtual text or appending some lines.

### Regular Vim?

I think Vim 8 introduced a similar system of remote plugins (possibly called "jobs"?) but I haven’t looked into it too much.
As far as I can tell it doesn’t give you a rich API like Neovim, nor any Lua, so you end up rendering VimL strings to be sent across to the editor.

I can see this working to an extent but I would imagine it’ll get pretty awkward as you try to batch requests or optimise your calls.
I did consider supporting Vim 8 in Conjure but decided the API is so different that I’ll end up spending quite a long time just keeping the API shim working correctly across both systems.

If your requirements are quite simple, try to support both systems.
If you’re building something pretty interactive that requires a lot of manipulation of buffers and windows then maybe just stick to Neovim, they’ve clearly designed the API with this in mind.

### Experiments with Rust

Before I tried to write the initial version of Conjure in Rust I played about with a toy plugin called [neofib](https://github.com/Olical/neofib) that calculated Fibonacci numbers.
Here’s how it’s core function looks though since we’re on the topic of Fibonacci language comparisons.

```rust
pub fn fib(n: u64) -> u64 {
    let mut a = 0;
    let mut b = 1;

    for _ in 0..n {
        let t = a + b;
        a = b;
        b = t;
    }

    a
}

fib(10)
// => 55
```

That project demonstrates remote plugins in Rust using [neovim-lib](https://github.com/daa84/neovim-lib) as a sort of framework.
Once I got around to attempting Conjure in Rust I got tangled up in Rust as a language as well as managing so many different asynchronous requests as my first real Rust project.

Ultimately the final nail in the coffin of my Rust attempt was that the EDN parsing wasn’t good enough for my needs.
I needed a _real_ Clojure implementation to parse and format the results I was getting out of the [socket prepl](/clojure-socket-prepl-cookbook).
I think if you don’t need to parse a lot of Clojure and you’re comfortable in Rust then it’s a fine choice for writing complex remote plugins.

### Dabbling with ClojureScript

It didn’t last long, but I did try writing Conjure as a remote plugin running on top of node in ClojureScript.

I killed that attempt because I ~~don’t think JavaScript is that great~~ really struggled to manage all of the asynchronous complexity on the node platform through promises.
I wanted a language that ate asynchronous problems for breakfast and could parse Clojure or ClojureScript with ease.
I wanted my beloved Clojure on the JVM.

### Settling down with Clojure

The iteration you see today on [Conjure’s repo](https://github.com/Olical/conjure) is a Clojure JVM process that sits between your Neovim and your various Clojure project JVMs.
It handles requests you initiate through Neovim, evaluates the right thing on the right prepl connection then manipulates your Neovim UI to display the results.

What’s interesting about this particular project is that Conjure is used to build Conjure, so I edit the source in Neovim and can use the development version to develop itself.
This does mean I’ve broken `eval` in the past which meant I couldn’t `eval` the fixed `eval` (luckily "load the current file from disk" still worked...).
It’s a weird feeling, growing the tool with the tool, but it’s extremely _lispy_ and works so well.
The feedback loop is ~0.

### Performance?

Writing your plugins in a remote process is fantastic in so many ways.
You get the power of whatever language and ecosystem you use to drive your favourite text editor!

The limit to this is that you need to encode and decode msgpack RPC payloads to get anything done, this is okay if you keep it to a minimum but won’t allow you to execute something on every key press, for example.
So as your functions grow in complexity and require more and more communication you’ll start to see things slow down.

The way I worked around this is by writing most of Conjure in Clojure with atomically batched requests to Neovim for most of the work.
I then fall through to calling pre-loaded Lua functions inside Neovim where the API calls would be noticeably slow.

That `conjure.close_log` function I mentioned earlier ends up doing a lot of querying and filtering to find the Conjure log window (if it’s open) in a way that doesn’t require storing any state.
I call it from Conjure like so.

```clojure
(defn ^:dynamic call
  "Simply a thin nvim specific wrapper around rpc/request."
  [req]
  (let [{:keys [error result] :as resp} (rpc/request req)]
    (when error
      (log/error "Error while making nvim call" req "->" resp))
    result))

(defn execute-lua [code & args]
  {:method :nvim-execute-lua
   :params [code args]})

(defn call-lua-function
  "Execute Conjure lua functions."
  [fn-name & args]
  (->> (apply execute-lua
              (str "return require('conjure')." (util/kw->snake fn-name) "(...)")
              args)
       (call)))

(defn call-lua-function
  "Execute Conjure lua functions."
  [fn-name & args]
  (->> (apply execute-lua
              (str "return require('conjure')." (util/kw->snake fn-name) "(...)")
              args)
       (call)))

(defn close-log
  "Closes the log window. In other news: Bear shits in woods."
  []
  (call-lua-function :close-log log-buffer-name))
```

I essentially build up data structures that can either be called on their own or atomically alongside other requests.
In this case, it’s building one request that executes some Lua code which executes a function in the `conjure` module.

## Actually doing this?

I highly recommend you dig through the source of [Conjure](https://github.com/Olical/conjure) and [neofib](https://github.com/Olical/neofib) to see how I implemented the underlying communication with Neovim.
Once you’ve worked that out the rest is entirely up to you, just bear in mind my warning about performance.

You shouldn’t put too much strain on the `stdio` msgpack RPC layer, try to do as much as you can remotely then occasionally ask Neovim to update something.
Ideally through a Lua function since that’ll run the fastest and be extremely easy to write.

I hope this overview has been helpful!
