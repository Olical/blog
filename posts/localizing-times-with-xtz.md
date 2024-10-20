---
tags:
  - blog-post
  - imported-blog-post
---
# Localizing times with xtz

I have just uploaded the first version of **xtz**. xtz, shorthand for cross timezone, is a script that converts times on your page to the users local time. So you could replace something like “_27 January, 2000 10:00:00 CST_” with “_27th January, 2000 at 4:00pm_” if the user is in the UK. If the user is in the CST timezone then you would have something like this “_27th January, 2000 at 10:00am_”.

This conversion is incredibly useful for things such as gaming clan website where they have users all over the world who need to get online at the same time. No matter where the user is, they will see the correct time and all you need to do is add some classes and a couple of lines of JavaScript.

You can learn more about it in the [GitHub repository](https://github.com/Wolfy87/xtz) or from [this fiddle](http://jsfiddle.net/Wolfy87/qgDLY/). Or you can carry on reading to see a quick example.

## An example

So here is a really simple example as to how you use it. First off you start out with your dates within elements. I am using span tags within a paragraph because that is probably one of the most common places.

```
<p>The London 2012 Olympic games will begin on <span id='olympics-date' data-format='dddd [the] Do [of] MMMM [at] h:mma'>27 July 2012, 21:00:00 GMT</span>.</p>
```

As you can see I am specifying an ID to access it with, this can be anything you want, and a date format for the parsed date to be outputted with. When converted this will output the following for users in the CST timezone.

> The London 2012 Olympic games will begin on Friday the 27th of July at 3:00pm.

Now to make this date actually get executed you are going to first need two scripts. The first is [moment.js](http://momentjs.com/) for the date parsing and the second is [the actual xtz script](https://github.com/Wolfy87/xtz/blob/master/xtz.js). You need to load these two scripts before your own code. xtz supports AMD too if thats how you want to do it. Once done you setup an instance of `xtz.DateConverter` and run it. If you want to then you can easily swap out moment.js for any other date library, it just [requires a tiny bit of configuration](https://github.com/Wolfy87/xtz#dependencies).

```
<script type='text/javascript' src='moment.js'></script>
<script type='text/javascript' src='xtz.js'></script>
<script type='text/javascript'>
    // Create your instance
    var converter = new xtz.DateConverter();

    // And execute it
    converter.run(document.getElementById('olympics-date'));
</script>
```

I am using the ID to fetch a single element in this example. Although you can also pass an array of elements. So if you are using MooTools or jQuery you can use their selector engines to make it a lot easier.

## Feedback

This is a very new library although I have put a lot of thought into it. So any feedback is greatly appreciated. Feel free to leave a comment below or [open an issue in the repository](https://github.com/Wolfy87/xtz/issues).
