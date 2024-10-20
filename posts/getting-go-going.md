---
tags:
  - blog-post
  - imported-blog-post
---
# Getting Go Going

I recently had to compile and run a [go](https://golang.org/) project, I found it thoroughly confusing initially and struggled to find a comprehensive guide on how to manage the repository in an idiomatic way. I’m going to show you how to get go projects up and running on your machine, I hope it’s the correct way, correct me if I’m wrong!

## Packages

You’re going to want to fetch two packages, ideally with your package manager of choice. That’s probably going to be pacman, apt-get or brew, for example. If you don’t use a package manager: do.

* [go](https://golang.org/)
* [godep](https://github.com/tools/godep) (there’s loads of alternatives, but I’ve tried this and it works well)

That’s all we’re going to need dependency-wise. Hopefully your project specifies it’s dependencies with a Godep file somewhere. If not, you may need to look up another dependency manager.

## GOPATH

This is the thing that confused me, I thought it was just somewhere go will check for dependencies, like changing your path for .m2 or something, it’s not. It’s nothing of the sort.

The GOPATH is essentially where you want to keep ALL of your go repositories, it’s more like an IDE project directory path. So go will store a few auxiliary directories in the path you specify as well as all of your go projects under the _src_ directory. So if you have a project called _foo_ in your GitHub user _bar_ it will be stored in _$GOPATH/src/github.com/bar/foo_.

I have a _~/repos_ directory where I keep all my projects, so I created a sub-directory of that called _go_ that now houses all of the go repositories. Feel free to adapt this to how you like to manage your repositories.

You can use _go get github.com/bar/foo_ to fetch something, or just copy the directory into the appropriate path. It should be self explanatory now that you understand the whole project directory idea (I hope).

## Building and running

If your project has dependencies specified with godep, you can just run _godep restore_ to fetch everything specified in the Godep file. I think there are ways built into go now too, although I’m not sure about those. This will populate your _$GOPATH/src/*_ directories with your dependencies.

You can now run _go build_ which will compile your project into your current directory. If you are working on your _foo_ project you should have a binary called _foo_, feel free to execute it! Unless it boots Skynet, in which case, don’t.

I hope this helps those of you that are confused and just need to get the damn thing running.
