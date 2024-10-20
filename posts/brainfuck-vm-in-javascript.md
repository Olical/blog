---
tags:
  - blog-post
  - imported-blog-post
---
# Brainfuck VM in JavaScript

Welcome to 2015, I hope you had a good break! Some people may now be taking on resolutions for the year, be that to lose weight or to socialise more. I’ve opted to [learn even more than usual](/my-2015-bucket-set/), so that means [more coursera courses](https://www.coursera.org/course/algs4partI) and toy projects in various languages. This is where [my brainfuck virtual machines](https://github.com/Wolfy87/brainfucks) come in. I’ll be writing them in every language I find interesting, starting with my trusty (see: sarcasm) friend, JavaScript.

This took me far longer than I wished it would and actually involved pretty much starting again at one point. Everything was fairly easy to implement up until the point where I had to synchronously read one character off of stdin. After hours of research, this proved to be nigh on impossible so I had to settle for some awkward callback based hell with two implementations because of an edge case. This is easy in C yet hard in JavaScript. What?

Besides that, the actual tokenise, parse and execute phases went fairly well. You can find the full code inside my [JavaScript implementation directory](https://github.com/Wolfy87/brainfucks/tree/master/implementations/javascript). I opted to turn the source file into a clean array first then to run through that array of tokens mapping them to an object of command functions. Fairly simple and extensible, I feel. I also performed all of the loop operator matching up front during the parsing, so I didn’t have to go hunting for _]_ or _[_ during the execution of the program.

I relied on [async](https://github.com/caolan/async) to take some of the pain out of iterating through asynchronous steps. The only reason the steps were asynchronous was because the one stdin reading command required it. If it wasn’t for node being really awkward on that front, this would have been far simpler.

Expect a Clojure implementation soon.
