---
alias: understanding-emacs-errors
tags:
- blog-post
- imported-blog-post
---


You can probably begin to get to the bottom of most internal Emacs problems with [debug-on-entry](https://www.gnu.org/software/emacs/manual/html_node/elisp/Function-Debugging.html). This function prompts for a function name interactively (which also hooks into helm) and will essentially set a breakpoint within Emacs on that function. When it is called you’ll get to see what functions were called and with what arguments.

This came out of my _:w_ (evil write) stopping working today, it was down to [this commit](https://bitbucket.org/lyro/evil/commits/b156bd87585a93acce503247bfb3cbd41fc5e179) and fixed in [this one](https://bitbucket.org/lyro/evil/commits/ce5eaa56c30271e212bbfa1b5805d59cb064e07f). The prompt response was much appreciated.

But you can use debug-on-entry on the offending function (which you should be able to find in the messages buffer) to set a breakpoint and walk up the stack to see if you can understand the problem better. It was interesting in my case but didn’t solve the issue, I just commented on the GitHub mirror of the commit and the author fixed it for me. Open source rules.
