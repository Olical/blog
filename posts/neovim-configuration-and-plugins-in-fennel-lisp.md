# Neovim configuration and plugins in Fennel (Lisp)

> A lot of this has been made irrelevant or far easier in Aniseed v3.0.0+. There’s a bunch of macros and helper scripts that remove a lot of the boilerplate, check out the documentation! The sentiment of this post is still accurate, it’s just easier to implement now.

In the beginning, there was Vim Script (also known as VimL). All Vim configuration, plugins, tweaks and hacks went through that configuration DSL with a sprinkling of flow control. VimL will take you wherever you want to go, albeit in a slightly slow and clunky fashion.

```viml
let g:my_thing = "Hello, World!"
```

We’re going to explore the present and potential future in this post together.

## Going beyond Vim Script

Eventually people started writing some plugins in Ruby or Python, a lot of great tools came out of that but not so many configuration styles (by configuration, I mean what you’d find in your dotfiles).

Neovim introduced us to remote plugins that communicate with the editor process over a [MessagePack RPC](https://neovim.io/doc/user/api.html#msgpack-rpc) channel, this means we can write plugins in our beloved languages for our beloved text editor. This is what my long term project, [Conjure](https://github.com/Olical/conjure), is built upon! It’s actually written mostly in [Clojure](https://clojure.org/) which is almost entirely hidden away from you.

```clojure
;; From Conjure.

(defn read-buffer
  "Read the entire current buffer into a string."
  []
  (-> (api/get-current-buf) (api/call)
      (api/buf-get-lines {:start 0, :end -1}) (api/call)
      (util/join-lines)))
```

This is great and all but you’ll find that the overhead of communicating back and forth with Neovim begins to add up, especially for things that run as a user types. I found myself writing some of the more chatty functions that needed to call a lot of API functions in VimL then calling out to that from Clojure. This helped speed things up where bulk RPC calls didn’t quite cut it.

Moving things to VimL to optimise MessagePack RPC overhead isn’t a great long term solution though. After all, we’re writing remote plugins so we _don’t_ have to write complex things in VimL!

## Lua to the rescue!(?)

Neovim has an integrated Lua interpreter, [LuaJIT](https://luajit.org/) to be precise. It’s an extremely fast runtime that has access to all the tools remote plugins have at their disposal, minus the requirement for MessagePack RPC encoding overhead. This means you get a much simpler language than VimL that’s many times faster without the encoding burden.

```lua
-- Also from Conjure.

-- Close the log window if it's open in the current tabpage.
function conjure.close_log(log_buf_name)
  local match = find_log(log_buf_name)
  if match.win then
    local win_number = vim.api.nvim_win_get_number(match.win)
    vim.api.nvim_command(win_number .. "close!")
  end
end
```

The only downside to Lua is that it’s minimal (in syntax and built in functions) almost to a fault, you end up having to use Neovim API calls as your standard library, something that gets pretty tedious to type out eventually. I, personally, struggled to move some logic from Conjure’s Clojure into pre-defined Lua functions simply for the fact that I found Lua a bit awkward to type and work with.

## Best of both worlds?

If you know me, you’ll know that I’m a massive Clojure nerd and can’t seem to edit anything unless it’s got [s-expressions](https://en.wikipedia.org/wiki/S-expression). That’s why I’ve written [Aniseed](https://github.com/Olical/aniseed), a convenient way to compile [Fennel](https://github.com/bakpakin/Fennel) (a [Lisp](https://en.wikipedia.org/wiki/Lisp_(programming_language))) into Lua ahead of time or on the fly while you’re inside Neovim. Not only does it help you compile things but it also acts as a small Clojure like standard library to help you build Neovim things with Fennel.

```scheme
;; From my dotfiles.

(local nvim (require :aniseed.nvim))

;; Elided for brevity...

(fn exists? [path]
  (= (nvim.fn.filereadable path) 1))

;; Elided for brevity...
```

The end result is a decent Lisp that drives Neovim directly, no remote plugin overhead and a blisteringly fast JIT runtime to execute it. Once all of the code is compiled ahead of time there’s essentially zero overhead when compared to writing the underlying Lua by hand. You have access to a (subjectively or objectively?) superiour syntax you can manipulate with [vim-sexp](https://github.com/guns/vim-sexp) (and [vim-sexp-mappings-for-regular-people](https://github.com/tpope/vim-sexp-mappings-for-regular-people)) as well as all the macros your heart desires.

## What does this mean for you?

You’ll be able to replace _almost_ all of the VimL in your Neovim configuration or plugin with a Lisp that runs at Lua speeds, no more Googling around for how to compare strings in a case sensitive way regardless of editor configuration (`==#` if I’m not mistaken?).

You can use Fennel macros to remove duplicated boilerplate code without sacrificing performance. This can be extremely useful for plugin authors who want to write clever and lengthy mappings in a concise syntax without a chain of extra function calls on startup.

For some of you, replacing several non-Lisp languages with a Lisp will be enough to convince you to do this, I’m definitely in that camp.

## Plugins

I wrote [nvim-local-fennel](https://github.com/Olical/nvim-local-fennel) as an example plugin that happens to be pretty damn useful in it’s own right. It allows you to write Fennel code in `.lnvim.fnl` files all the way up your directory tree (think machine local and project local configuration) and have them executed on Neovim startup automatically. The Fennel is compiled to Lua as and when the Fennel source changes, this is all really easy thanks to Aniseed.

The majority of the plugin is contained in [`fnl/nvim-local-fennel/init.fnl`](https://github.com/Olical/nvim-local-fennel/blob/249d139d64abaea7c0137213dd82fd22444a1b40/fnl/nvim-local-fennel/init.fnl), the key chunk of code can be found around line 29 (if it hasn’t changed since then).

```scheme
;; Iterate over all directories from the root to the cwd.
;; For every .lnvim.fnl, compile it to .lvim.lua (if required) and execute it.
;; If a .lua is found without a .fnl, delete the .lua to clean up.
(let [cwd (cwd)
      dirs (parents cwd)]
  (table.insert dirs cwd)
  (core.run!
    (fn [dir]
      (let [src (.. dir "/.lnvim.fnl")
            dest (.. dir "/.lnvim.lua")]
        (if (file-readable? src)
          (do
            (compile.file src dest)
            (nvim.ex.luafile dest))
          (when (file-readable? dest)
            (nvim.fn.delete dest)))))
    dirs))
```

There’s a _tiny_ bit of Vim Script in [`plugin/nvim-local-fennel.vim`](https://github.com/Olical/nvim-local-fennel/blob/249d139d64abaea7c0137213dd82fd22444a1b40/plugin/nvim-local-fennel.vim) that simply loads the compiled Lua automatically as the plugin is loaded. You could skip this if your plugin is an optional library users interact with through requiring the Lua modules.

```viml
lua require("nvim-local-fennel")
```

And the final piece of the puzzle is a small [`Makefile`](https://github.com/Olical/nvim-local-fennel/blob/6231efe066db8b5d53e2053309857c2ce18ecd79/Makefile) that allows you to `make compile` the Fennel into Lua (using Aniseed) ahead of time for distribution. I commit the Fennel and Lua code into the repository to save users of my plugin from having to compile any Fennel to use the plugin itself.

```make
.PHONY: compile submodules

compile:
	rm -rf lua
	nvim -c "set rtp+=submodules/aniseed" \
		-c "lua require('aniseed.compile').glob('**/*.fnl', 'fnl', 'lua')" \
		+q
	ln -s ../../submodules/aniseed/lua/aniseed lua/nvim-local-fennel/aniseed

submodules:
	git submodule update --init --recursive
```

Now users of the plugin can simply depend on our repository and have it load and execute without ever knowing about the Lisp it came from! We get a wonderfully expressive language that runs incredibly fast but the user has no idea about what’s going on under the hood.

## Configuration

Another use for Aniseed is to replace your local Neovim configuration with Fennel, I’ve done just that with my [dotfiles](https://github.com/Olical/dotfiles/tree/f1187da605f40908582c7a4356ba5771c23df816/neovim/.config/nvim). We start with [`init.vim`](https://github.com/Olical/dotfiles/blob/f1187da605f40908582c7a4356ba5771c23df816/neovim/.config/nvim/init.vim) which ensures we have Aniseed installed and then requires the bootstrap Lua.

> I depend upon `develop` but you should be using the latest released tag for stability. Since I’m the only one changing `develop` I know I’m not going to surprise myself with breaking changes. Hopefully.

```viml
call plug#begin(stdpath('data') . '/plugged')
Plug 'Olical/aniseed', { 'branch': 'develop' }
call plug#end()

lua require("config/bootstrap")
```

Then we move onto the bootstrap Lua in [`lua/config/bootstrap.lua`](https://github.com/Olical/dotfiles/blob/f1187da605f40908582c7a4356ba5771c23df816/neovim/.config/nvim/lua/config/bootstrap.lua), writing this part in VimL would also be fine to be honest.

```lua
if not(pcall(require, "aniseed.compile")) then
  vim.api.nvim_command("PlugInstall")
end

local dir = vim.api.nvim_call_function("stdpath", {"config"})
require("aniseed.compile").glob("**/*.fnl", dir .. "/fnl", dir .. "/lua")
require("config")
```

All it does is perform a `:PlugInstall` if we don’t have Aniseed yet and then ask the newly installed Aniseed to compile all of the configuration Fennel to Lua. Once that’s done we can simply load the compiled Lua! Let’s take a peek inside [`fnl/config/init.fnl`](https://github.com/Olical/dotfiles/blob/f1187da605f40908582c7a4356ba5771c23df816/neovim/.config/nvim/fnl/config/init.fnl), the first module to be loaded.

```scheme
(local core (require :aniseed.core))
(local nvim (require :aniseed.nvim))
(local util (require :config.util))

;; Load all config modules in no particular order.
(->> (util.glob (.. util.config-path "/lua/config/module/*.lua"))
     (core.run! (fn [path]
                  (require (string.gsub path ".*/(.-)/(.-)/(.-)%.lua" "%1.%2.%3")))))

{:aniseed/module :config.init}
```

That initial module loads every other module found within [`fnl/config/module`](https://github.com/Olical/dotfiles/tree/f1187da605f40908582c7a4356ba5771c23df816/neovim/.config/nvim/fnl/config/module) automatically, allowing me to easily grow my configuration with well named Fennel files, keeping everything easy to find and understand. Have a browse through those modules to see what I do with plugin installation and configuration.

## Interactive buffer evaluation

You may have noticed the `:aniseed/module` key in the return values of the modules I’ve shown, these have special meaning in Aniseed. If you have [Aniseed’s mappings](https://github.com/Olical/aniseed/tree/b82429053cd69d030b380dd3a2598770112ea258#evaluating-with-mappings) set up like I do (shown in the linked documentation), you can go into any Fennel file you like and evaluate it with `<localleader>ef`. This means you can tweak and re-evaluate your library as you work on it, just like you would with Clojure and Conjure, you don’t need to restart Neovim to try something out!

It solves a problem I found with Lua modules where it was easy to require it once, but awkward to change and then require again. This small feature makes interactive development a whole lot easier, something I feel every Lisp needs.

## That's all I've got for now!

This post has been pretty lengthy and dense but I hope it’s given you a good idea of what you can do with Neovim, Aniseed and Fennel. Please get in touch via Twitter or Email (linked below) with any thoughts or questions you may have. Sharing of this post and the linked projects around social media is greatly appreciated!

Have a good day!

```scheme
(local nvim (require :aniseed.nvim))
(nvim.ex.wq_)
```
