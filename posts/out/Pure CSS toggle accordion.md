---
alias: pure-css-toggle-accordion
tags:
- blog-post
- imported-blog-post
---


CSS is brilliant. When written properly you can do so much with it. I believe that you should always try to build crucial UI elements with CSS rather than JavaScript. It always feels slightly more responsive, and now with transitions you can make things look amazing with just one line. So I thought I would try my hand at creating an accordion. We have all made one at some point, usually utilizing a massive JavaScript library such as MooTools or jQuery alongside a bloated accordion script. So my version uses _no JavaScript whatsoever_.

I admit, because of using newer CSS, namely the `:checked` pseudo selector, you alienate your older IE audience. But that can be fixed by including [Selectivizr](http://selectivizr.com/). So you have your increased performance and ease of use in newer browsers with a working version in IE. The first thing you will need is the HTML.

## Markup

Now the HTML for this particular accordion is a little more complicated than some. That is because we are utilizing radio buttons to perform the toggle on click action. But do not fear, it only means one extra element that is actually set to `display: none`.

```
<ul class='accordion'>
    <li>
        <label for='cp-1'>Content pane 1</label>
        <input type='radio' name='a' id='cp-1' checked='checked'>
        <div class='content'>
            <p>Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Vestibulum tortor quam, feugiat vitae, ultricies eget, tempor sit amet, ante. Donec eu libero sit amet quam egestas semper. Aenean ultricies mi vitae est. Mauris placerat eleifend leo.</p>
        </div>
    </li>

    <li>
        <label for='cp-2'>Content pane 2</label>
        <input type='radio' name='a' id='cp-2'>
        <div class='content'>
            <p>Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Vestibulum tortor quam, feugiat vitae, ultricies eget, tempor sit amet, ante. Donec eu libero sit amet quam egestas semper. Aenean ultricies mi vitae est. Mauris placerat eleifend leo.</p>
        </div>
    </li>
</ul>
```

I have only added two content panes here, but you can add as many as you require. As you can see, we have a simple unordered list with a class applied to it (`++`). Each list item contains a label, input and div. The label is where you store your title and the div is where you put your content. You can style all of this however you want. Just adapt my CSS that I will show you in a second.

When adding more items you must remember to update the `for` attribute on the label and the `id` of the radio input. Otherwise you will click one and six will show. You can also specify a content pane to be open by default by adding `checked='checked'` to its radio button, just like I have done in the HTML above. Other than that you can just copy and paste to your hearts content.

## CSS

Now here is the code that makes the accordion work. I have added comments to help you out, but you may still find it a bit confusing. If you do just utilize it in your project via copy and paste first, then keep tweaking it until you know what does what. I believe that it is one of the best ways to understand some confusing code. Style it up, add some transitions. Maybe even adapt it to work horizontally. Just don’t say “bah! that looks too hard, I give up”. Just experiment.

```
/* Clean up the lists styles */
ul.accordion {
    list-style: none;
    margin: 0;
    padding: 0;
}

/* Hide the radio buttons */
/* These are what allow us to toggle content panes */
ul.accordion label + input[type='radio'] {
    display: none;
}

/* Give each content pane some styles */
ul.accordion li {
    background-color: #CCCCCC;
    border-bottom: 1px solid #DDDDDD;
}

/* Make the main tab look more clickable */
ul.accordion label {
    background-color: #666666;
    color: #FFFFFF;
    display: block;
    padding: 10px;
}

ul.accordion label:hover {
    cursor: pointer;
}

/* Set up the div that will show and hide */
ul.accordion div.content {
    overflow: hidden;
    padding: 0 10px;
    display: none;
}

/* Show the content boxes when the radio buttons are checked */
ul.accordion label + input[type='radio']:checked + div.content {
    display: block;
}
```

So this CSS first adds some basic styles, so we can actually distinguish between content panes, and then adds the radio button based functionality. This relies heavily on the adjacent sibling selector (`+`). So in the last selector we are saying: “any div with a class of ‘content’ that is under a checked radio button that is under a label should be displayed”. See, that’s not so bad is it, and that is the most complicated line!

The main question is, does it work? Well yes, yes it does. And here is an example. [Here is a fiddle too](http://jsfiddle.net/Wolfy87/Z4Mr3/), just in case you want to play about with the code as well as the accordion itself.
