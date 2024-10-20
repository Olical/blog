---
tags:
  - blog-post
  - imported-blog-post
---
# Making package.el behave like Vundle

I love [Vundle](https://github.com/gmarik/Vundle.vim), it allows me to specify a list of packages within my [dotfiles repository](https://github.com/Wolfy87/dotfiles) that is kept up to date and in sync with every other machine I use Vim on. The key point with this is that I can remove a package from all machines by simply deleting the line from my configuration.

```
" Load Vundle. Manages all of the bundles.
filetype off
set rtp+=~/.vim/bundle/Vundle.vim
call vundle#begin()

" So Vundle can update itself.
Plugin 'gmarik/Vundle.vim'

" Colour scheme.
Plugin 'jonathanfilip/vim-lucius'

" Plugins without settings.
Plugin 'PeterRincker/vim-argumentative'
Plugin 'Wolfy87/vim-enmasse'
Plugin 'helino/vim-json'


" I have a lot, so I'll skip a few...


Plugin 'myusuf3/numbers.vim'
  nnoremap <leader>l :NumbersToggle<CR>

" Enable some syntax settings that had to be disabled for Vundle.
call vundle#end()
filetype plugin indent on
```

Package management through _~/.**rc_ files is the Shih Tzu. Sadly, [package.el](http://wikemacs.org/wiki/Package.el) doesn’t seem to do this and expects me to use some silly text based menu as well as remembering to keep every single one of my packages in sync. If I remove it from one, I need to remove it from the rest by hand. *NO.**

I’m not having any of that.

## Installing missing packages

This one’s easy enough to solve and many people have many solutions. I have a function, [_dotfiles-sync_](https://github.com/Wolfy87/dotfiles/blob/88926d0f8ad581f4a4953d6fdea40d812638b17d/emacs/init.el#L97-L103), which will get the latest package lists from [MELPA](http://melpa.milkbox.net/) (and a few others) and installs all packages I currently don’t have locally. Simple enough and very effective, even if it does require a few reboots to get them all installed, still trying to work that one out.

```
;; Main package list to fetch from melpa.
(defvar dotfiles-packages
  '(evil
    evil-args
    evil-nerd-commenter
    ;; WAY MORE PACKAGES...
    ))

;; Package manager configuration.
(setq package-archives '(("gnu" . "http://elpa.gnu.org/packages/")
                         ("marmalade" . "http://marmalade-repo.org/packages/")
                         ("melpa" . "http://melpa.milkbox.net/packages/")))
(package-initialize)

(defun dotfiles-sync ()
  "Install packages."
  (interactive)
  (package-refresh-contents)
  (dolist (p dotfiles-packages)
    (when (not (package-installed-p p))
      (package-install p))))

;; A macro from milkbox.net to make load hooks easier.
(defmacro after (mode &rest body)
  "`eval-after-load' MODE evaluate BODY."
  (declare (indent defun))
  `(eval-after-load ,mode
     '(progn ,@body)))

;; Individual package configuration.
(defvar evil-want-C-u-scroll t)
(after `evil-autoloads
  (evil-mode t))

(after `evil-args-autoloads
  ;; Bind evil-args text objects.
  (define-key evil-inner-text-objects-map "a" 'evil-inner-arg)
  (define-key evil-outer-text-objects-map "a" 'evil-outer-arg)

  ;; Bind evil-forward/backward-args.
  (define-key evil-normal-state-map "L" 'evil-forward-arg)
  (define-key evil-normal-state-map "H" 'evil-backward-arg)
  (define-key evil-motion-state-map "L" 'evil-forward-arg)
  (define-key evil-motion-state-map "H" 'evil-backward-arg)

  ;; Bind evil-jump-out-args.
  (define-key evil-normal-state-map "K" 'evil-jump-out-args))

(after `evil-nerd-commenter-autoloads
  (evilnc-default-hotkeys))

;; WAY MORE PACKAGE CONFIGURATION
```

So here I have a list of packages, a function to fetch them and a macro that I use to wait until each package is loaded before I configure them. To update my packages I have to go into the text based menu, mark out of date packages for upgrade and then execute it all (_M-x list-packages RET U x_). Despite this being cumbersome and annoying, this appears to be the only way right now.

## The other problem, pruning

So I can install and update fairly easily, albeit not perfectly, but I’m still lacking the ability to prune old packages that I no longer have listed in my _init.el_ file. My only main requirement for this process is that it executes as part of my synchronisation. Luckily the problem frustrated me enough to construct a solution, now my Emacs package management isn’t that far behind that of Vim / Vundle’s, but it still doesn’t feel quite a smooth. I’ll take what I can get.

```
;; Package pruning tools.
(defun flatten (mylist)
  "Flatten MYLIST, taken from http://rosettacode.org/wiki/Flatten_a_list#Emacs_Lisp for sanity."
  (cond
   ((null mylist) nil)
   ((atom mylist) (list mylist))
   (t
    (append (flatten (car mylist)) (flatten (cdr mylist))))))

(defun filter (predicate subject)
  "Use PREDICATE to filter SUBJECT and return the result."
  (delq nil
        (mapcar (lambda (x) (and (funcall predicate x) x)) subject)))

(defun get-package-name (package)
  "Fetch the symbol name of a PACKAGE."
  (car package))

(defun get-package-version (package)
  "Return the version string for PACKAGE."
  (package-version-join (aref (cdr package) 0)))

(defun get-package-dependencies (package)
  "Fetch the symbol list of PACKAGE dependencies."
  (mapcar 'car (elt (cdr package) 1)))

(defun get-packages-dependency-tree (packages)
  "Recursively fetch all dependencies for PACKAGES and return a tree of lists."
  (mapcar (lambda (package)
            (list (get-package-name package)
                  (get-packages-dependency-tree (get-package-dependencies package))))
          (get-packages-as-alist packages)))

(defun get-packages-as-alist (packages)
  "Return the list of PACKAGES symbols as an alist, containing version and dependency information."
  (filter (lambda (n) (car (member (car n) packages))) package-alist))

(defun get-all-current-dependencies (packages)
  "Return all packages found in PACKAGES with their dependencies recursively."
  (delq nil (delete-dups (flatten (get-packages-dependency-tree packages)))))

(defun get-all-obsolete-packages (packages)
  "Return all packages in an alist which are not contained in PACKAGES."
  (filter (lambda (n) (not (member (car n) (get-all-current-dependencies packages)))) package-alist))

(defun prune-installed-packages (packages)
  "Delete all packages not listed or depended on by anything in PACKAGES."
  (mapc (lambda (n)
          (package-delete
           (symbol-name (get-package-name n))
           (get-package-version n)))
        (get-all-obsolete-packages packages)))
```

I then amended my [_dotfiles-sync_](https://github.com/Wolfy87/dotfiles/blob/d24591ebd7b3a36f629fb5a4ebd921c72f2b5b91/emacs/init.el#L104-L111) function to prune my old packages by adding one line. Fantastic.

```
(defun dotfiles-sync ()
  "Install packages."
  (interactive)
  (prune-installed-packages dotfiles-packages) ;; <-- THIS ONE :D
  (package-refresh-contents)
  (dolist (p dotfiles-packages)
    (when (not (package-installed-p p))
      (package-install p))))
```

I hope others will find this useful, I sure feel safer in the knowledge that packages I no longer list in my repository will be removed on my next synchronisation. You can just rip my code from this post, but I suppose I could turn it into a package if there was any real interest in it.

Edit: I created a [post](https://www.reddit.com/r/emacs/comments/2jtojf/packageel_didnt_prune_my_unused_packages_so_i/) on the Emacs subreddit in which syl20bnr ran with the idea and made it far better. The concept can now be found within [the spacemacs repository](https://github.com/syl20bnr/spacemacs/blob/c517424032a9f43e1365d9f157dc246b38debda1/core/contribsys.el#L245-L270) and it looks great!
