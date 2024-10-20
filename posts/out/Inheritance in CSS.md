---
alias: inheritance-in-css
tags:
- blog-post
- imported-blog-post
---


When creating styles for elements such as buttons or text boxes in CSS there are always variations. These variations include things like icons and colors. Copying a chunk of CSS and editing the attributes isn’t ideal, so what can you use instead? **Inheritance**, that’s what.

So, what is it, and how does it work. Well the concept is fairly simple, you have a set of base styles for your element that may make it gray or just very plain. You then add extra classes that almost theme that element. As an example in this post I will be making a “themeable” button using inheritance.

First off we need the core styles, this is just the layout and default styles for your element. So any paddings, margins, borders and font styles. Here is what I am going to use.

```
.button {
    margin: 10px;
    padding: 2px 10px;
    border: 1px solid;
    border-radius: 5px;
    display: inline-block;
    color: #FFFFFF;
    text-decoration: none;

    /* These are the styles we can override */
    border-color: #777777;
    background-color: #999999;
}
```

I have specified the border width and style separate to the color. You do not need to. I have only done this for clarity. Now you can see the two properties we will override with our specific classes. Although you can add as many as you want.

So, what does this look like? Well, it is very plain, but here it is.

Now lets add some color to it. We do not edit the original CSS to achieve this. Instead we write a new class that overrides the original styles. So, lets write that extension class.

```
.green.button {
    border-color: #4C724C;
    background-color: #669966;
}
```

Easy to read and very powerful. You can see that and element with a class of `green button` will now have the layout of a button and a green theme. This can be applied to any element. An anchor for example.

```
<a href='#' class='green button'>This is a green button</a>
```

We end up with something that looks like this.

## Making it simpler with LESS

You can amazingly make this even simpler. My favorite way to do this is by using a mixin with LESS. Here is the green styling again but instead using a mixin. This method means you could have as many button styles as you want and manage them easily.

```
.button-theme(@color) {
    border-color: darken(@color, 10%);
    background-color: @color;
}

.green.button {
    .button-theme(#669966);
}
```

As you can see, I am only specifying one color here. I let LESS do the hard work and calculate the shades of my base color. You can apply these techniques to almost everything in CSS. It means you have to write less code but it is actually so much easier to maintain.
