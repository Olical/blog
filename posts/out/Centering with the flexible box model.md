---
alias: centering-with-the-flexible-box-model
tags:
- blog-post
- imported-blog-post
---


For those of you that have not heard of the flexible box model, brace yourselves. When it is more widely supported you will no longer need to silently (or audibly) scream to yourself when vertically centering a variable height block level element.

But it does more than just center any way you want. It also allows you to center multiple elements and space them how you want. It may sound too good to be true, but it isn’t (for once). It is just a little… new. So please keep that in mind when considering using it.

## Get stuck in with flexbox

Please bear in mind that I am using [prefixfree](https://github.com/LeaVerou/prefixfree) on this site, so I don’t have to worry about vendor prefixes. However, **you do**. Fear not, if you do not want to include prefixfree in your code you can run your flexbox CSS through [prefixr](http://prefixr.com/). It is the web app equivalent of prefixfree. Let’s get started then…

```
div.center {
    /* This is used to center the parent */
    /* Apparently display: box; stops margin: 0 auto; */
    width: 200px;
    height: 200px;
    margin: 20px auto;
}

div.parent {
    /* Flexbox code goes from here... */
    display: box;
    box-pack: center;
    box-align: center;
    /* To here! */

    border: 1px solid #000000;
    width: 200px;
    height: 200px;
}

div.parent div {
    width: 50px;
    height: 50px;
    background-color: #AA3333;
}
```

I think we should begin wit~ wait what, **that’s it?!** Whoa. It only takes that code between the comments. The rest is decorative. It may seem ridiculously simple, but that is all you need to center that elements child vertically and horizontally. `mind = 'blown';`. Let’s take a gander at the hugely complicated HTML that compliments this brain meltingly difficult CSS.

```
<div class='center'>
    <div class='parent'>
        <div></div>
    </div>
</div>
```

Yeah. I don’t hear any screaming developers. Easy as π.

## Need an alternative?

Okay, so maybe it is not the most widely supported feature out there. There’s a good chance your client won’t take “it is a lot easier for the developer” to not support anything other than the latest browsers. And I am all for realistic code, not stuff that will only work in the nightly of Chrome. On a Wednesday. If the wind is blowing in a southerly direction.

So here is my favourite way to vertically center an element which should work in pretty much every browser you need it to. Lets look at the CSS first.

```
html, body {
    /* This allows us to center in the height of the browser. */
    height: 100%;
}

div.aligner {
    height: 50%;

    /* This is the negative version of half the height of the element we wish to center. */
    margin-bottom: -100px;
}

div.to-center {
    /* This is the element to center vertically. */
    width: 200px;
    height: 200px;
    margin: 0 auto;
    background-color: #AA3333;
}
```

Sadly, we have to specify a height. It is a little easier if you are using LESS because you can work out the height dynamically. Still, not ideal. So we are using `div.aligner` to push our element (`div.to-center`) down to the middle of the page. Our centered element is using the beautiful `margin: 0 auto;` trick to center horizontally.

Now for the HTML, it is very simple but still. As you may have guessed from the CSS, it simply involves two `+``+<div>`’s next to each other. Although they can be any element you like to be honest.

```
<div class='aligner'></div>
<div class='to-center'></div>
```

And here is the cross-browser method in action.

Hopefully, when you are asked to center something vertically, you will at least know where to start now. Flexbox is amazing, but poorly supported. Give it time and we will be using it all over the place. I am guessing about a year before the first “flexbox grid system” is released.

## Dive deeper

Want to do more with flexbox than just center things? Well then [this post](http://www.html5rocks.com/en/tutorials/flexbox/quick/) is for you. It covers pretty much everything you need to know.
