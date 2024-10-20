# Fun with negative margins

Ever wanted to overlay elements? You can create great effects and produce a really engaging site by simply making `div`s overlap slightly. But sometimes making elements lay on top of each other is harder than you would think. One of the biggest problems people have when overlaying elements on top of each other seems to be z-index issues. Luckily I am here to show you the perfect way to overlay elements with no z-index woes.

## An example

You may be wondering what on earth a negative margin looks like. Well take the new look of a Facebook profile for example. You see the way the smaller image pushes up into the larger one? That is the kind of effect you would be aiming for with a negative margin. Although I think Facebook are using a slightly different method. Saying that, I can see that they do use a negative margin, just not how I would have.

So you use it when you want an element to either pull it’s self upwards or drag it’s self downwards over other elements. I will show you how to accomplish something similar to the Facebook header but full width which makes for a great blog post style.

To start you off, here is my example code. You can also [have a play with it on jsFiddle](http://jsfiddle.net/Wolfy87/E48MA/).

### HTML

```
<!-- Used for centering my example -->
<div class='container'>
    <!-- This is our banner the content will pull up over -->
    <!-- The class isn't even required, just using it for decoration -->
    <img src='http://placekitten.com/400/220' class='banner'>

    <!-- This is the div that will pull up over the banner -->
    <!-- It's class also adds some decoration -->
    <div class='overlay'>
        <!-- This content div just adds padding for the text -->
        <!-- We add padding to an inner div so as not to stretch the parent -->
        <div class='content'>
            <p>Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas.</p>
            <p>Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Vestibulum tortor quam, feugiat vitae, ultricies eget, tempor sit amet, ante. Donec eu libero sit amet quam egestas semper. Aenean ultricies mi vitae est. Mauris placerat eleifend leo.</p>
        </div>
    </div>
</div>
```

### CSS

```
/* Give the page a nicer background */
body {
    background-color: #CCCCCC;
}

/* Center my example and push it off the top of the page */
.container {
    width: 400px;
    margin: 30px auto;
}

/* Pull the overlay up over the banner */
.overlay {
    /* Don't take up the full width */
    width: 90%;

    /* Give the content a see thru white background, default to solid white in old browsers */
    background: #FFFFFF;
    background: rgba(255, 255, 255, 0.7);

    /* Center the content */
    margin: 0 auto;

    /* Pull the content up over the banner */
    margin-top: -100px;

    /* Using this will make sure that it goes over the element, not under */
    /* This is the magic line that fixes most z-index issues */
    position: relative;

    /* More pointless pretty code */
    border-radius: 8px;
}

/* Make the content look pretty */
.banner {
    border-radius: 6px;
}

.content {
    padding: 12px;
}
```

## Eh, what?

This may seem a little daunting. That is mainly because this is a fancy decorated version. Here is the stripped down version with only the code you need. You should easily be able to work out what is going on from the comments.

### HTML (simple)

```
<!-- This is our banner the content will pull up over -->
<img src='http://placekitten.com/400/220'>

<!-- This is the div that will pull up over the banner -->
<div class='overlay'>
    <p>Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas.</p>
</div>
```

### CSS (simple)

```
.overlay {
    /* Pull the content up over the banner */
    margin-top: -100px;

    /* Using this will make sure that it goes over the element, not under */
    /* This is the magic line that fixes most z-index issues */
    position: relative;
}
```

See, pretty simple when you tear it down. So all we are doing is adding a negative top margin and setting the position to relative. The negative margin pulls the element up and the relative position makes sure the overlay ends up on top of the banner.

The exact same theory applies to downwards or sideways margins. You can pull anything over anything to improve the visual flow of your pages. Play about with it and overlay items in creative ways. Although I doubt anything will be better than text laid over a kitten.

### A note on the delay

Sorry about the delay between this post and the last. I have had quite a bit on. If you have waited it out, thank you, I appreciate your patience. I have two or three ideas already lined up so the next posts should be a lot quicker. Until next time!
