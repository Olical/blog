# Writing a modular frontend

It may be a bold statement, but I believe that modularity is the way forward. By that I mean writing modular code will coax us into writing reusable and higher quality classes and functions. This feeling is present when writing backend or frontend code, the problem is that modularity in the frontend can feel a little alien at first.

The thing that got me thinking about modules was looking at [Python](http://www.python.org/), more specifically [Django](https://www.djangoproject.com/) with [Mezzanine](http://mezzanine.jupo.org/), and just realizing how nice it is having everything loosely coupled and easily reusable. So I set about finding a way to write modular JavaScript **and** CSS.

## Modular JavaScript

I have already talked about this before but I can’t stress enough how good it is. Using [RequireJS](http://requirejs.org/) for loading and managing modules is just fantastic. Using it you can avoid polluting the global namespace and actually speed up your page. This is because your modules are loaded in parallel, not one after the other. So if you load [MooTools](http://mootools.net/) from [Google’s CDN](https://code.google.com/apis/libraries/devguide.html) and the [Facebook JavaScript SDK](https://developers.facebook.com/docs/reference/javascript/) from the Facebook servers they will be loaded side by side along with your own modules, which can contain anything from single functions to huge classes.

When you go to release your project you can run your code through the [RequireJS optimizer](http://requirejs.org/docs/optimization.html) (r.js) to concatenate and minify your modules into a single file. You end up with your minified code, MooTools and the Facebook SDK loading at the same time. This can prove faster than a single file a lot of the time.

## Modular CSS

Now this is a bit different. You do not use a library to load CSS files in parallel and you don’t have to export selectors with a call to something like `@define`, this is more of an underlying guide or technique than a CSS framework. The core ideas of this can be found in [OOCSS](https://github.com/stubbornella/oocss/wiki). The core idea found in that wiki is that you should separate your structure and skin of your site. I could not agree more.

Everyone does it. By the end of a project you have a thousand line CSS file with comments stating what each section is for (forms, menus and links for example). Over time those lines blur and you end up with stuff all over the place and an unmaintainable monster of a stylesheet. By splitting your styles into many separate files and module directories you can stop all that. And by separating structure and skin you can reuse the structure of certain elements such as menus with the skin of another project.

To create this modular CSS I recommend extensive use of the `@import` statement. Usually I would cry “_burn the heretic!_” if I heard someone recommend that, but then I found out that [the RequireJS optimizer can also be used on CSS](http://requirejs.org/docs/optimization.html#onecss). So while in development you can use `@import` to make managing your CSS easier. and just before you upload you can run it through `r.js` to concatenate and minify your CSS. Here’s the directory structure that I am now using which is specified by OOCSS.

```
\-yourplugin/ {plugin-root}  
+-yourplugin.css {essential CSS}  
+-yourplugin_debug.css {CSS for debugging} 
+-yourplugin_doc.html {Examples and Explanation}  
+-yourplugin_skins.css {all skins that only require pure css, others via @import}  
+-\ skins/ {skins that need more than pure CSS, eg. images}  
  +-\ photo/ {skin-root}  
    +-photo_skin.css  
    +-img/  
  +-\ flow/ {skin-root}  
    +-flow_skin.css  
    +-img/
```

I also create a file called `loader.css` this just contains the core import statements. Then in turn the other files cascade down importing anything they need to function. Once you have written your CSS using the format above then you can flatten it down to one file like so.

```
node r.js -o cssIn=assets/css/loader.css out=styles.css

# Then you can minify it too if you want
# cleancss is a node module for minifying css
cleancss -o styles.min.css styles.css
```

Not too bad eh?

**TL;DR:** Use [RequireJS](http://requirejs.org/) to manage your JavaScript modules. Write CSS that adheres to the principals of [OOCSS](https://github.com/stubbornella/oocss/wiki) and optimize your `@import` riddled CSS with [r.js](http://requirejs.org/docs/optimization.html#onecss).
