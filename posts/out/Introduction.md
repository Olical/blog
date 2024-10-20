---
alias: introduction
tags:
- blog-post
- imported-blog-post
---


Well, here we go. I have been working on this new blog on and off for about a week now. I thought I would start off with a post about some of the technology behind it.

## Jekyll

The first thing you will notice from checking out [the source](https://github.com/Wolfy87/wolfy87.github.com) will be that it is powered by [Jekyll](https://github.com/mojombo/jekyll). It is a blog aware static site generator that allows me to create templates, loops, posts and pages. A bit like WordPress, just more manual.

The manual process of creating posts could be easily automated with a simple script. So I will probably write one in the near future. I am currently writing my posts in [markdown](http://daringfireball.net/projects/markdown/) to make life easier although everything else is written in HTML.

## LESS

I love [LESS](http://lesscss.org/). I use it on almost every project that I can. I have used it here too. When in development mode the styles for this site are compiled in the browser. But when in production (as it is now) the LESS is compiled into plain old CSS.

I also wrote a grid system in LESS, [more](https://github.com/Wolfy87/more). That is the grid that this site is built on. Because of LESS I can change column and gutter widths so easily. I can even specify how many columns I want. This current grid is set up exactly like [960](http://960.gs/).

## prefixfree

I despise writing prefixes, it feels so backwards. I suppose less helps with it’s mixins but it is not enough. That’s why I use [prefixfree](https://github.com/LeaVerou/prefixfree). It is a script that automatically checks what prefixes are required in your browser and applies them to your CSS. Thus, no more `-webkit`, `-moz` , `-o` or `-ms`.

## GitHub pages

I am hosting this site on GitHub pages which has amazing Jekyll support. All I have to do is run `git push` and GitHub will compile and deploy my blog.

You may have noticed by now how some paragraphs are indented. I have focused heavily on typography, and this technique is recommended. Some people indent every paragraph, but the correct way is to only indent a paragraph if it is preceded by another paragraph.

I have also added hyphenation and made sure my lines are the optimal length. I want it to be as easy to read as possible. Because this is a development blog, I have tried to add nice syntax highlighting too. Here’s how it looks.

```
'use strict';

var i = null;

for(i = 0; i < 100; i += 1) {
    console.log(i);
}
```

## Feedback?

I hope you have enjoyed this little introduction. There are a few useful links dotted around it. Please let me know what you think in the comments section below. Any thoughts are much appreciated. Thanks!
