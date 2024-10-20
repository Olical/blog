---
alias: sliced-text-effect
tags:
- blog-post
- imported-blog-post
---


I am a huge fan of Terry Pratchett’s discworld books. At one point in the series Death, with his scythe so sharp it can cut sound, manages to slice words on the page into separate letters. The resulting effect on the page looked strange but brilliant. I thought I would try and recreate that effect. And here is what I came up with.

I am afraid the technique is a little manual so it requires some playing about with pixels. So this is not really feasible for variable length text. Unless you can work something out that I could not of course. Before we look at the CSS you will want to get familiar with the markup. Otherwise you will not understand the counter rotations among other things.

```
<div class='slice'>
    <div class='line'>
        <div class='left'>
            <p>Laceration</p>
        </div>
    </div>

    <div class='right'>
        <p>Laceration</p>
    </div>
</div>
```

The base CSS is pretty simple. It just involves positioning the elements correctly. The left and top values are the ones you need to tweak to get your text in the right place. This can be tricky because you need two copies of your text and you must adjust both separately.

```
div.slice {
    position: relative;
}

div.slice div.left, div.slice div.right {
    position: absolute;
}

div.slice div.left {
    top: 5px;
    left: 55px;

    transform: rotate(-30deg);
}

div.slice div.right {
    top: 25px;
    left: 40px;
}
```

The rotation of `-30deg` in the code above is to counteract the rotation of the element that clips the text (`div.line`). The CSS for the clipping element is a little more complicated. But all you have to worry about are the lower properties. These involve more top / left positions and some color choices.

```
div.slice div.line {
    overflow: hidden;
    z-index: 10;
    position: absolute;

    background-color: #FFFFFF;
    border-right: 1px solid #444444;

    width: 100px;
    height: 110px;

    top: -40px;
    left: 0;

    transform: rotate(30deg);
}
```

So all you have to do is set up your text, position it in an inspector by adjusting the left / top values and color the background. You can also style your border with the last chunk of CSS. A live demo of this is available on [jsFiddle](http://jsfiddle.net/Wolfy87/desCn/). Feel free to have a play around with it. And please leave a comment with any thoughts you may have.
