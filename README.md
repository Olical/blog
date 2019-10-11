# Blog (work in progress)

My personal blog, hosted at [oli.me.uk][blog]. Written in [Clojure][] using [Conjure][] and [Neovim][].

It's a static site rendered from [AsciiDoc][], feel free to copy and modify the code if you think you'll find it useful!

## Tools

 * `clj -A:build` - render the blog to static HTML in `output`.
 * `clj -A:propel` - start a prepl server with [Propel][] and write the port to `.prepl-port`.
 * `clj -A:depot` - check for outdated dependencies with [Depot][].

## Unlicenced

The posts are mine and you should not republish them without asking permission or attribution, the code is fair game.

Find the full [unlicense][] in the `UNLICENSE` file, but here's a snippet.

>This is free and unencumbered software released into the public domain.
>
>Anyone is free to copy, modify, publish, use, compile, sell, or distribute this software, either in source code form or as a compiled binary, for any purpose, commercial or non-commercial, and by any means.

[unlicense]: http://unlicense.org/
[clojure]: https://clojure.org/
[conjure]: https://github.com/Olical/conjure
[neovim]: https://neovim.io/
[blog]: https://oli.me.uk
[propel]: https://github.com/Olical/propel
[depot]: https://github.com/Olical/depot
[asciidoc]: http://asciidoc.org/
