# Making El-Get sync like vim-plug

As [my post](/giving-emacs-another-chance/) from last night says, I’m giving Emacs another go. And in an even older post, when I was trying Emacs for the first time, I show [my approach to getting my packages syncing like I do in Vim](/2014/10/20/making-package-el-behave-like-vundle/) (spoiler alert: It didn’t work very well). My Vim setup will remove unused packages, update what I currently have and then install any that are missing through [this simple script](https://github.com/Wolfy87/dotfiles/blob/85937edfd5330cc0478a3165f6aa7d132173ef57/vim/.vim/update.sh) and the use of [vim-plug](https://github.com/junegunn/vim-plug).

```
#!/usr/bin/env bash

vim +"PlugSnapshot $HOME/.vim/revert.sh" +PlugUpgrade +PlugClean! +PlugUpdate +qa
```

I’ve been yearning for the same, or at least similar, experience in Emacs. Without good package management and automatic cleaning I just don’t want to use it. The main reason for this is that I use [my dotfiles](https://github.com/Wolfy87/dotfiles) across multiple machines and I can’t be dealing with package hell when I pull my dotfiles at work each morning. I need everything to always represent my declarative list of packages in my dotfiles perfectly.

After a late night Elisp session yesterday, which ended around 1am, I found a very concise approach to manage my packages in a satisfactory way. It’s nowhere near as clean, efficient and parallel as my Vim set up, but it gets the same result. It’s a shame it’s not shiny and perfect, but sometimes good enough is good enough. [My synchronisation script for Emacs](https://github.com/Wolfy87/dotfiles/blob/85937edfd5330cc0478a3165f6aa7d132173ef57/emacs/.emacs.d/sync.sh) isn’t as short and sweet, but it does the job.

```
#!/usr/bin/env bash

PACKAGES=~/.emacs.d/config/packages.el
ELGET=~/.emacs.d/el-get/

if [ ! -d $ELGET ]; then
    mkdir $ELGET
    git clone git@github.com:dimitri/el-get.git $ELGET/el-get
fi

emacs --batch -l $PACKAGES -f dotfiles-sync
rm ~/.emacs.d/el-get/.loaddefs.*
emacs --batch -l $PACKAGES
```

This will fetch [el-get](https://github.com/dimitri/el-get) if required, boot Emacs once to perform the sync operation (fetch, update and clean), remove the loaddefs because they get out of sync _really_ easily (so if magit was removed, for example, it would still appear in my tab complete although I couldn’t execute any of the commands, that’s loaddefs being old) and finally launch it again to generate the new loaddefs file ahead of time. The real magic happens in [my packages configuration module](https://github.com/Wolfy87/dotfiles/blob/85937edfd5330cc0478a3165f6aa7d132173ef57/emacs/.emacs.d/config/packages.el) though.

```
(add-to-list 'load-path "~/.emacs.d/el-get/el-get")
(require 'el-get)

(setq dotfiles-packages '())

(defmacro bundle (name &rest content)
  `(progn
    (add-to-list 'dotfiles-packages ',name)
    (el-get-bundle ,name ,@content)))

(defun dotfiles-sync ()
  (el-get-cleanup dotfiles-packages)
  (el-get-update-all t))

(bundle monokai-theme
        (load-theme 'monokai t))
```

That little _bundle_ macro is a passthrough to the _el-get-bundle_ macro, but before it passes the forms off to it the name is stored in a list. That list becomes the “required packages” list which we use when performing a cleanup of packages. Basically, when you run _el-get-cleanup_ (which appears to be undocumented? I found it by perusing the source) you can pass it a list of packages _not_ to remove. This list is obtained by intercepting my declarative list of dependencies. Neat, right?

So I’ve done it, I’ve got Emacs packages working the way I needed them to for me to take it seriously. It works just like my Vim + vim-plug setup, albeit not as elegantly. It’s a small sacrifice to make for all the lisp I could ever ask for.

It’s parenthesis time now.
