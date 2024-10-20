# If you’re using sudo with npm you’re doing it wrong

I see countless developers blindly firing off _sudo npm install -g XYZ_ and it hurts. A lot. From then on, every time you update or execute global npm commands from within scripts or tools the whole thing will halt or die when it encounters permission related problems. One “solution” I’ve seen for this is to chmod **/usr/local**. You don’t own this directory though, you only own your home directory. Assume everything outside of that is shared by other users on the system. Ruby gems are installed to your home directory (or should be if it’s not the case already!) so why not node modules?

This is an easy thing to fix, all you have to do is add a prefix attribute to your _~/.npmrc_ which you can even do with this single line of bash.

```
echo "prefix = ~/npm" >> ~/.npmrc
```

After this, all _npm install -g XYZ_ commands (without sudo) will install to the npm directory within your home. The only other thing you have to do is add _$HOME/npm/bin_ to your path, which is very easy to do. I won’t go into that here because it’s probably [one of the most common questions ever asked in the history of humanity](https://www.google.com/search?q=How+do+I+add+to+my+path+variable%3F&ie=utf-8&oe=utf-8&aq=t&rls=org.mozilla:en-US:official&client=firefox-a&channel=fflb).

Don’t use sudo with programming package managers, configure them to store things in your home directory. It’ll save you your sanity at some point in the future.
