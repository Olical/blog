---
alias: graph-from-scratch
tags:
- blog-post
- imported-blog-post
---


[James](https://twitter.com/jamesfublo) mentioned the other day that he was drinking tea “at a rate of 0.75 OC”, OC being the base speed at which I myself consume a mug of that wonderful brown liquid. A little [flurry of tweets](http://www.exquisitetweets.com/tweets?eids=EjQYN9DC57.EjRXe1BtqC.ElgZl6JxF6.ElhqBY5I1Q.Elhyot1C20.ElhGxGBZoi) ensued which resulted in me kind of accepting a joke/challenge using a meme.

This post is mainly about plotting a graph with nothing but JavaScript and a canvas element, but it also gives me a chance to finally document my tea drinking habits for the world to gaze upon in wonder.

## Aim

I’m going to build a simple plotted graph using JavaScript and a canvas element but without any external dependencies. I’ll try to walk you through each step of the way too, so you can pick and choose which parts of this code you’d like to use in your possibly unrelated canvas project.

This will be self contained within a neat little class, so you’ll easily be able to pull it out and modify it to your hearts content.

## Setup

First I’m going to setup a couple of empty classes. I’m going to have a `LineGraph` class which inherits (using [prototypical inheritance](/prototypical-inheritance-done-right/)) from `Graph`. The base class will contain anything a generic graph would, such as containing data points and rendering.

It will be up to the individual implementations, a `LineGraph` in this case, to turn those data points into something visual.

```
/**
 * Base graph class, handles containment of data points and the overarching
 * interface of all graph classes.
 *
 * @class
 */
function Graph() {
}

/**
 * Line graph, used for plotting a value over time.
 *
 * @class
 * @augments Graph
 */
function LineGraph() {
  Graph.apply(this, arguments);
}

LineGraph.prototype = Object.create(Graph.prototype);
```

## Storing the data

The base `Graph` class needs to take a set of data and store it for later use by it’s child classes. This data could be an array (line graphs) or an object containing simple numbers (pie chart).

We’ll do this by adding a `setDataSource` method which is called from the constructor.

```
/**
 * Base graph class, handles containment of data points and the overarching
 * interface of all graph classes.
 *
 * @class
 * @param {*} [initialDataSource]
 */
function Graph(initialDataSource) {
  this.setDataSource(initialDataSource);
}

/**
 * Updates the current data source. The values contained within are used to
 * render the actual graph.
 *
 * @param {*} dataSource
 */
Graph.prototype.setDataSource = function (dataSource) {
  this._dataSource = dataSource;
};
```

## Creating the canvas

We’ll leave it to the base class to create and prepare the canvas element. It will create the element at a specified size and then store it’s context.

```
/**
 * Base graph class, handles containment of data points and the overarching
 * interface of all graph classes.
 *
 * @class
 * @param {Number} width
 * @param {Number} height
 * @param {*} [initialDataSource]
 */
function Graph(width, height, initialDataSource) {
  this.setDataSource(initialDataSource);
  this.initialiseCanvas(width, height);
}

/**
 * Initialises the canvas element and stores it's context object. It will also
 * set the initial width and height.
 *
 * @param {Number} width
 * @param {Number} height
 */
Graph.prototype.initialiseCanvas = function (width, height) {
  this._canvas = document.createElement('canvas');
  this._context = this._canvas.getContext('2d');
  this.setSize(width, height);
};

/**
 * Updates the current size of the graph.
 *
 * @param {Number} width
 * @param {Number} height
 */
Graph.prototype.setSize = function (width, height) {
  this._canvas.width = this._width = width;
  this._canvas.height = this._height = height;
};
```

We’ll also add a way to fetch the canvas element for later. This will be used to inject the element into the DOM where you see fit.

```
/**
 * Fetches the actual canvas DOM node. This can be used to place the canvas
 * within your page.
 *
 * @return {HTMLElement}
 */
Graph.prototype.getCanvasElement = function () {
  return this._canvas;
};
```

You’ll now be able to create the canvas and inject it into your page with something like this.

```
var g = new Graph(300, 200);
var canvas = g.getCanvasElement();
document.body.appendChild(canvas);
```

## Setting up the data

This isn’t very well defined, it’s kind of up to how the specific graph child class wishes to implement it. Here’s how I’m going to implement the data structure for the `LineGraph` class.

```
var teaGraph = new LineGraph(300, 200, {
  consumptionSpeed: {
      colour: '#FF0000',
      values: [
          0, 0, 0, 0, 0,
          0, 0, 0, 0.1, 0.3,
          0.8, 1, 3, 8, 16, 32
      ]
  },
  temperature: {
      color: '#0000FF',
      values: [
          80, 80, 80, 80, 80,
          79, 78, 76, 72, 60,
          55, 54, 40, 10, 0, 0
      ]
  }
});
```

This allows us to name our plotted lines if we ever wanted to, colour them and specify the actual values they should display. I think the `LineGraph` class should be able to work with that.

## Calculating the bounds

When the data source is set we’re going to want to pre-calculate the maximum amount of positions across the X and Y axis. We will use this count to divide the graph up into the right amount of columns and rows to represent every data point.

This will involve creating a few new functions and adding a call to one of them within `setDataSource`. But because these bound values are only really relevant to line graphs, we need to do this in the `LineGraph` class, not `Graph`.

```
/**
 * Updates the current data source. The values contained within are used to
 * render the actual graph.
 *
 * This will also calculate the bounds for line graph. Overrides the original
 * Graph#setDataSource method.
 *
 * @param {Object} dataSource
 */
LineGraph.prototype.setDataSource = function (dataSource) {
    Graph.prototype.setDataSource.call(this, dataSource);
    this._values = this.getDataSourceItemValues();
    this.calculateDataSourceBounds();
};

/**
 * Flattens all of the value arrays into one single array. This is much easier
 * to iterate over.
 *
 * @return {Number[][]}
 */
LineGraph.prototype.getDataSourceItemValues = function () {
    var dataSource = this._dataSource;
    var values = [];
    var key;

    for (key in dataSource) {
        if (dataSource.hasOwnProperty(key)) {
            values.push(dataSource[key].values);
        }
    }

    return values;
};

/**
 * Calculates the upper X and Y axis bounds for the current data source.
 */
LineGraph.prototype.calculateDataSourceBounds = function () {
    this._bounds = {
        x: this.getLargestDataSourceItemLength(),
        y: this.getLargestDataSourceItemValue()
    };
};

/**
 * Fetches the length of the largest (or longest) data source item. This is the
 * one with the most values within it's values array.
 *
 * @return {Number}
 */
LineGraph.prototype.getLargestDataSourceItemLength = function () {
    var values = this._values;
    var length = values.length;
    var max = 0;
    var currentLength;
    var i;

    for (i = 0; i < length; i++) {
        currentLength = values[i].length;

        if (currentLength > max) {
            max = currentLength;
        }
    }

    return max;
};

/**
 * Fetches the largest value out of all the data source items.
 *
 * @return {Number}
 */
LineGraph.prototype.getLargestDataSourceItemValue = function () {
    var values = this._values;
    var length = values.length;
    var max = 0;
    var currentItem;
    var i;

    for (i = 0; i < length; i++) {
        currentItem = Math.max.apply(Math, values[i]);

        if (currentItem  > max) {
            max = currentItem;
        }
    }

    return max;
};
```

All that block above is doing is calculating the upper bounds for the X and Y axis. It’s very easy to understand because everything is split into it’s own documented function that really doesn’t do that much. I could have probably squashed it down into a quarter of that size, but then you’d never understand it.

Keeping everything in small, well named and focussed functions keeps things testable and above all: clean. Now that our data is prepared, we can move onto rendering our data.

## Rendering the graph

The first step is to add an initial render method to the base `Graph` class. This will call all of the appropriate methods to clean and then render the canvas.

```
/**
 * Renders the current data source onto the canvas.
 */
Graph.prototype.renderGraph = function () {
  this.clearCanvasElement();
  this.drawDataSourceOntoCanvasElement();
};

/**
 * Clears the current canvas state.
 */
Graph.prototype.clearCanvasElement = function () {
  this._context.clearRect(0, 0, this._width, this._height);
};

/**
 * Draws the current data source onto the canvas.
 *
 * @abstract
 */
Graph.prototype.drawDataSourceOntoCanvasElement = function () {};
```

`drawDataSourceOntoCanvasElement` is a noop method that `LineGraph` will override to plot it’s points and lines onto the canvas. By the time it’s called the canvas will be completely clean and ready for drawing.

Now for the final step(s); drawing the points and lines onto the canvas.

```
/**
 * Draws the current data source onto the canvas.
 */
LineGraph.prototype.drawDataSourceOntoCanvasElement = function () {
    var dataSource = this._dataSource;
    var currentItem;
    var key;

    for (key in dataSource) {
        if (dataSource.hasOwnProperty(key)) {
            currentItem = dataSource[key];
            this.plotValuesOntoCanvasElement(currentItem);
        }
    }
};

/**
 * Plots the given data source item onto the canvas.
 *
 * @param {Object} item
 */
LineGraph.prototype.plotValuesOntoCanvasElement = function (item) {
    var context = this._context;
    var points = item.values;
    var length = points.length;
    var currentPosition;
    var previousPosition;
    var i;

    var radius = 2;
    var startAngle = 0;
    var endAngle = Math.PI * 2;

    context.save();
    context.fillStyle = context.strokeStyle = item.colour;
    context.lineWidth = 2;

    for (i = 0; i < length; i++) {
        previousPosition = currentPosition;
        currentPosition = this.calculatePositionForValue(i, points[i]);

        context.beginPath();
        context.arc(currentPosition.x, currentPosition.y, radius, startAngle, endAngle, false);
        context.fill();

        if (previousPosition) {
            context.moveTo(previousPosition.x, previousPosition.y);
            context.lineTo(currentPosition.x, currentPosition.y);
            context.stroke();
        }
    }

    context.restore();
};

/**
 * Calculates the X and Y position for a given column and value (row). Returns
 * the result within an object containing an x and y pixel value.
 *
 * @param {Number} column
 * @param {Number} value
 * @return {Object}
 */
LineGraph.prototype.calculatePositionForValue = function (column, value) {
    return {
        x: this._width / this._bounds.x * column,
        y: this._height - (this._height / this._bounds.y * value)
    };
};
```

That’s it. That last block renders each line onto the canvas taking up all available space using it’s selected colour. Each line is marked with dots along it’s path with a line joining each point.

## Wrapping it all up (_FINALLY!_)

The code above will leave you with two classes that can be used to set up and plot a graph onto a canvas. It is built in such a way that other graph types could descend from the base class to inherit some default functionality (a pie or bar chart for example).

You can pick up the full source and a small example usage in [this gist](https://gist.github.com/Wolfy87/7816213). You can also play with [an interactive version on jsFiddle](http://jsfiddle.net/Wolfy87/yTg9t/2/), which I’ve also embedded below.

Feel free to leave your thoughts and opinions below, they’re appreciated. I hope you’ve found this useful!
