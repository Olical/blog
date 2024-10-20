---
alias: my-new-prompt-oh-my-zsh
tags:
- blog-post
- imported-blog-post
---


I’ve rewritten this in fish shell functions since writing this, you can find that in [a gist](https://gist.github.com/Olical/1491b2072f0daf84072a).

'''''

I’ve been thinking about my terminal prompt recently, more specifically my [oh-my-zsh](http://ohmyz.sh/) theme and what I actually need it to show. I’ve been using the “clean” theme since I began using oh-my-zsh but I’ve realised I basically ignored the information it’s showing me most of the time.

I type gs continually to check my git status (provided by the wonderful [oh-my-zsh git plugin](https://github.com/robbyrussell/oh-my-zsh/blob/master/plugins/git/git.plugin.zsh)) and know which terminal is in which directory (most of the time). I’d rather have something much more concise since I can type a small command to get the information I need when I need it.

So I’ve written my own little theme that displays a single lambda ([λ](https://en.wikipedia.org/wiki/Lambda)) character. No matter what. If it’s underlined, my git status is dirty, if it’s red the last command had a bad exist status. Otherwise it’s green. I could make it bold or italic in some special cases too, I just haven’t found a use for those just yet.

Whenever I need to know where I am I execute the other great oh-my-zsh command, d, to list my directory stack. With regards to navigating that stack, you can use “cd -” to jump to the previous directory, or “cd -{index}” to jump to the directory at the given index.

Here it is in action.

I hope you like the minimalistic approach I’ve taken, it feels quite refreshing to have a slightly faster prompt (I think) that stays consistent after each command. You can find it in my [dotfiles](https://github.com/Olical/dotfiles), but I’ve inserted it below as well. The oh-my-zsh documentation shows you [how to add custom themes](https://github.com/robbyrussell/oh-my-zsh/wiki/Customization).
