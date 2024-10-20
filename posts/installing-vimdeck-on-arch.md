---
tags:
  - blog-post
  - imported-blog-post
---
# Installing Vimdeck on Arch

I’m going to be talking at [Vim London](http://www.meetup.com/Vim-London/events/174682642/) again on Tuesday, so I thought I’d knock a quick presentation together. I was planning on using [Vimdeck](https://github.com/tybenz/vimdeck) but it turns out it freaks out on Arch based Linux distributions, such as Manjaro (probably some other Linux distributions too).

When executing `gem install vimdeck` it tries to install [RMagick](https://github.com/rmagick/rmagick) which fails on my machine due to [ImageMagick](http://www.imagemagick.org/) being installed with HDRI enabled, whatever the hell that is. Apparently the Arch team enabled it a few months back.

The solution? Clone the [RMagick](https://github.com/rmagick/rmagick) repository and execute the following.

```
gem build rmagick.gemspec
gem install rmagick-{VERSION}.gem
```

Where `{VERSION}` is the version of the gem you just built. This will vary from the time when this post was written so I won’t bother putting the version I had in there. Now you have a fixed version of the gem install you can execute `gem install vimdeck` and it will install perfectly. If not, congratulations, you’ve discovered a new issue!

Now to write my presentation…
