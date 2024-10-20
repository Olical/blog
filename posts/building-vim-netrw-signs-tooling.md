# Building vim-netrw-signs: Tooling

In my last post I focused on [getting the repository and main files up and running](/building-vim-netrw-signs-introduction/), here I’ll briefly touch on the satellite tooling I’m going to hook into to make the development a bit easier. Oh, also, I’m writing this from a cruise ship somewhere around Gibraltar.

## Travis

So I created my `.travis.yml` file last time which gives me all the configuration I need to activate continuous integration testing. Because I went onto the [Travis](https://travis-ci.org/) website and activated the repository, every time I or any other push it will run the test suite.

So this will allow me to check my code on another environment, which is always fun and games when it comes to Vim, but also have pull requests pre-vetted for me. All PRs will be built and have their results displayed within the actual PR page. The Travis integration actually goes both ways.

## Waffle

This is a tool from the guys behind [Rally](https://www.rallydev.com/), which is basically enterprise task management. [Waffle](https://waffle.io/) is the free and open source oriented version which allows me to control my GitHub issues as to do lists on a sort of Kanban board. I’ve already used their automated PR generator that asks to add the “ready” task count to my readme. This will allow anyone viewing the GitHub repository to see what tasks are researched, locked down and ready to be worked on by anyone and everyone.

## Tools in summary

So I have Travis executing tests on my remote GitHub repository using Vader and Waffle hooking into the GitHub issues system to provide task boards and organisation. Now I can plan out tasks, drag them around their various states and tag them then build them.

I’ll probably open long running tasks (this should be most feature branches) up as pull requests so that I get diff, comment and CI tooling all on one page. This gives other developers the chance to see my thought processes and comments as I go about building a feature. It should also show how many times I forget to push without running the test suite.

## Next up

Diving into the VimL and actually building this beast. I’ll keep popping up for air and writing little bits about the development, but it’s going to be fairly high level on the whole with a lot of links to commits. This is because it would take about five years to complete if I talked about every line I wrote.

So the next post will be massive and code heavy but should show the actual construction of a Vim plugin, not just the tooling and extra services.
