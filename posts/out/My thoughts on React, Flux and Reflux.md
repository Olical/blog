---
alias: my-thoughts-on-react-flux-and-reflux
tags:
- blog-post
- imported-blog-post
---


I could have used “My reaction to React, Flux and Reflux” in this title, but I thought it’d seem unprofessional, so I moved it down to the first point of this post. Because my blog is not an incredibly professional place.

I spent a week of evenings and most of a weekend building an application much like a nested todo list with [React](http://facebook.github.io/react/) and [Reflux](https://github.com/spoike/refluxjs/). I’ve gone from zero to slightly more than zero knowledge in that time and it’s been an interesting experience, I want to share how I approached the problem from a high level, show some examples and finally point out all of the glaringly obvious things I’ve missed but will use next time.

I’d also like to mention [this tangential video](http://dev.hubspot.com/blog/moving-backbone-to-flux-react) that explains what Flux and React solve perfectly, it also talks about things such as optimistic fulfilment within the UI. A concept in which you respond to user input immediately, even if you have to ask the server, and then roll back the state by popping some snapshots off of a stack if a promise fails. This sort of thing would be a monumental challenge in any other world, but it’s feasible with React and Flux.

## My structure

Since I was using Reflux, I tried to follow the general architecture of the [reflux-todo](https://github.com/spoike/refluxjs-todo) example. I really like Reflux’s approach to the Flux architecture, it kicks out the useless string passing in the dispatcher and binds things together through concrete function calls and references. I sincerely hope the project gains traction and continues to grow in this direction. I’d really like to see some immutable data structure integration too.

* build/
  * index.html
  * style.css
  * \{main.js is generated here by gulp + browserify}
* src/
  * actions/
    * listActions.js
  * components/ (which I think are referred to as elements now)
    * App.js
    * Dashboard.js
    * ListItem.js
    * ListItemAttribute.js (these are the [sub-list items in a list item](http://inception.davepedu.com/))
    * Manifest.js
  * stores/
    * listStore.js
  * utils/
    * compile.js (my custom array -> React DOM factory)
    * persistence.js (tiny layer on top of localStorage)
  * main.js (kicks everything off and configures [react-router](https://github.com/rackt/react-router), which is _amazing_ by the way)
* test/ (mirrors the src structure and files, but didn’t test components)

You may be shocked by that last line, I didn’t test my components. This is mostly because I ran out of time and realised all too late that I should have been using [Jest](http://facebook.github.io/jest/) all along. I tested everything else, especially my store, using [Mocha](http://mochajs.org/) and [Should](https://github.com/shouldjs/should.js). I just found it difficult to test my components directly this way without it being boilerplate filled and generally yucky. Luckily, my components didn’t really hold any state whatsoever, so it wasn’t too bad. Testing the store was the most important part.

As you can tell from the general lack of files, it was a pretty small project and didn’t require many actions. The thing that caused this to take a week of evenings and a lot of the weekend was not reading the manual and diving straight in. I’d highly recommend reading all of the documentation before starting something with React, Flux or Reflux. Because of Reflux’s awesomeness, my actions file simply contained this.

```
var Reflux = require('reflux');

/**
 * These are used to construct the actions. Actions are used within Reflux by the components to tell the stores what to do.
 *
 * @type {String[]}
 */
var requiredActions = [
    'addItem',
    'updateItem',
    'removeItem',
    'addItemAttribute',
    'updateItemAttribute',
    'removeItemAttribute',
    'clear'
];

var listActions = Reflux.createActions(requiredActions);

module.exports = listActions;
```

Then I plug it into my store with one line, which is pretty damn incredible.

```
// ...
var listStore = Reflux.createStore({
    listenables: listActions,
    // ...
```

I also ended up with a couple of utility files that made localStorage access even easier and a way to compile an array structure to React DOM, which I now realise was pretty pointless. I don’t want to use JSX right now, but I could have just used the _React.DOM_ API and had basically the same experience as my array DSL. It was fun to build though, I guess I was just yearning for something Lisp / Clojure like.

```
/**
 * An API into localStorage that won't explode if it isn't available (like in node.js).
 *
 * Will also serialise and parse with JSON.
 *
 * @type {Object}
 */
var persistence = {
    write: function (key, value) {
        if (typeof localStorage === 'object') {
            localStorage.setItem(key, JSON.stringify(value));
        }
    },
    read: function (key) {
        if (typeof localStorage === 'object') {
            return JSON.parse(localStorage.getItem(key));
        }
    }
};

module.exports = persistence;
```

```
var React = require('react');
var _ = require('lodash');

/**
 * Compiles an array into a React DOM structure recursively. All segments of the node array are optional.
 *
 * The array segments can be in any order apart from the node type, that always needs to be first.
 *
 * Actually works really well, I'd quite like to open source this with CSS selector parsing in the nodeName.
 *
 * @param {*[]} node Comprised of a string DOM node name, params object and string content or child node(s). The nodes can be more arrays or compiled React elements.
 * @return {Object} A react DOM tree built from your data structure recursively.
 */
function compile(node) {
    if (_.isArray(node)) {
        var nodeName = _.first(node);
        var findProp = _.partial(_.find, _.rest(node));
        var props = _.mapValues({
            attrs: _.isPlainObject,
            children: _.isArray,
            compiled: React.isValidElement,
            text: _.isString
        }, findProp);

        var child = props.text || props.compiled || _.map(props.children, compile);

        return React.createElement(nodeName, props.attrs, child);
    }
    else {
        return node;
    }
}

module.exports = compile;
```

So that should give you a rough idea as to what I was playing with. I may well open up the repository to public ridicule in the coming months. Now onto the things I wish I’d known about before.

## Things I would have done differently

![[http://img3.wikia.nocookie.net/__cb20101028071113/southpark/images/d/d8/Coon2Hindsight10.png]]

1. [propTypes](http://facebook.github.io/react/docs/reusable-components.html#prop-validation) – YOU CAN ADD TYPE ANNOTATIONS?!
2. [defaultProps](http://facebook.github.io/react/docs/reusable-components.html#default-prop-values) – Great for optional configuration.
3. [ref](http://facebook.github.io/react/docs/more-about-refs.html) – A property to allow deep linking into component hierarchy.
4. [this.props.children](http://facebook.github.io/react/docs/top-level-api.html#react.children) – Essentially transclude from Angular land but with some really cool helper utilities.
5. [transferPropsTo](http://facebook.github.io/react/docs/transferring-props.html) – Something I see in Angular all too much made easy. Although hopefully you won’t get into the situation where you have that many props. That smells of SRP violation.
6. I should have been using the component life cycle hooks **way** more. They’re great and there’s probably one for your exact need, you just have to select carefully. It does remind me of building WordPress plugins and themes though…

I hope others can learn from my mishaps! Basically, [RTFM](http://en.wikipedia.org/wiki/RTFM) before you go out into the big not so bad world of sane UI development.
