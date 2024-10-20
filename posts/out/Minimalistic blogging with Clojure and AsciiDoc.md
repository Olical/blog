---
alias: minimalistic-blogging-with-clojure-and-asciidoc
tags:
- blog-post
- imported-blog-post
---


I’ve procrastinated my way to yet another blogging stack and I’m very happy with it! I’m going to walk you through how it works and show you a few tricks of prepl based development I found along the way. If you like the idea of this approach feel free to fork [Olical/blog](https://github.com/Olical/blog).

## Previous worlds

My poor blog has been through quite a few iterations, I _think_ this list is exhaustive:

* WordPress
* Octopress (static via Ruby)
* Possibly another I forgot here?
* WordPress
* Cryogen (static via Clojure)

I’ve somehow managed to carry my posts forward from each old blog into my new one each time, they’ve definitely lost some quality along the way (image formatting, syntax highlighting) but the content and URLs have remained functional in one way or another. This means you can still read my old cringe worthy posts that mostly center around outdated JavaScript almost a decade later, hooray!

When I moved from my last WordPress instance to Cryogen I ended up taking a snapshot of my old site’s HTML and storing that as a bunch of static files alongside my new static output. This meant the old URLs would continue to work without the original server and I wouldn’t have to convert them to Markdown Cryogen could work with.

I was hosting the Cryogen blog on GitHub pages so I wasn’t able to perform any redirects. Keeping all of the old URLs the same was an _interesting_ exercise. You could essentially follow a link from the Cryogen site into the frozen WordPress site then browse around from there. They were two static sites hosted within the same GitHub pages though which is pretty odd.

## The new world

What you’re seeing here is static HTML hosted on a [DigitalOcean](https://m.do.co/c/e643aa564b21) $5 Droplet behind [CloudFlare](https://www.cloudflare.com) for caching, compression and HTTPS (take a look at the network tab of your inspector, it’s so fast!). It’s compiled into a [Docker](https://www.docker.com/) container (with [nginx](https://www.nginx.com/) as a server) and executed by [Dokku](http://dokku.viewdocs.io/dokku/), an extremely easy to set up Docker based PaaS. It’s like a tiny private [Heroku](https://www.heroku.com/) (that’s actually Heroku compatible!).

The posts are written in [AsciiDoc](http://asciidoc.org/) and compiled to HTML by a fairly small amount of [Clojure](https://clojure.org/) ([`blog.render`](https://github.com/Olical/blog/blob/4340d5c84fc4777db1ef71db451a059444473acf/src/blog/render.clj)). It uses [Selmer](https://github.com/yogthos/Selmer) templating to stitch the HTML snippets together and executes everything under [Claypoole](https://github.com/TheClimateCorporation/claypoole) for maximum performance.

To publish a post, all I have to do is write a new `.adoc` file in the `posts` directory and execute `make deploy`, that’ll `git push` my changes to Dokku which will build and run the container, swapping out the previous one if the build succeeds.

## Converting WordPress HTML to AsciiDoc

I wrote some Clojure to parse the content out of the static HTML files I saved from my old WordPress blog years ago. I had to update the code block DOM a little since it was wrapped in some plugin specific nodes that worked with a now defunct JavaScript based syntax highlighter.

Here’s the source of that namespace with a bunch of comments to explain some of my thinking as it goes along. You might want to start with the comment at the bottom and then work your way through the callstack.
```clojure
(ns blog.tmp-wp-port
  (:require [clojure.string :as str]
            [clojure.java.shell :as shell]

            ;; I'm using Hickory to parse, alter and render the HTML source.
            [hickory.core :as html]
            [hickory.select :as css]
            [hickory.render :as render]

            ;; I rarely touch the filesystem without fs.
            ;; It makes so many things so very much easier.
            [me.raynes.fs :as fs]))

;; This function returns a sequence of full file paths to each of the post HTML files.
;; It's essentially listing every file in the blog source directory then
;; filtering down to those that look like years.
;; It then lists three levels down each of those year directories to find all of the posts.
(defn legacy-post-paths [root]
  (->> (fs/list-dir root)
       (filter #(re-matches #"\d\d\d\d" (fs/name %)))
       (mapcat fs/list-dir)
       (mapcat fs/list-dir)
       (mapcat fs/list-dir)
       (map #(fs/file % "index.html"))))

;; Given a post HTML path this does all of the work to read, parse and update it.
(defn ->post-data [path]
  (let [dom (-> (slurp path)
                (html/parse)
                (html/as-hickory))]

    ;; The title and date are simple CSS selectors on the parsed DOM.
    {:title (-> (css/select (css/class "entry-title") dom)
                (get-in [0 :content 0]))
     :date (-> (css/select (css/class "entry-date") dom)
               (get-in [0 :attrs :datetime])
               (str/replace #"T.*" ""))

     ;; The slug is just name name, I've dropped the date prefix in my new blog.
     :slug (fs/name (fs/parent path))

     ;; Grabbing the content isn't toooo tricky.
     :content (-> (css/select (css/class "entry-content") dom)
                  (first)
                  ;; The harder part is updating the nodes...
                  (update
                    :content
                    (fn [nodes]
                      (map (fn [{:keys [attrs] :as node}]
                             ;; When we see this highlighting plugin node we extract the plain source code.
                             (if (some-> (:class attrs) (str/includes? "crayon-syntax"))
                               (-> (css/select (css/class "crayon-plain") node)
                                   (first)
                                   (assoc :tag :pre))
                               node))
                           nodes)))

                  ;; Finally we spit that updated DOM into HTML.
                  (render/hickory-to-html)
                  (as-> $
                    ;; And run it through pandoc to convert HTML to AsciiDoc.
                    ;; My headings were all way too deep which threw warnings
                    ;; when I compiled AsciiDoc -> HTML in Clojure. So I had to go
                    ;; through my posts with sed and turn === into == for example.
                    (shell/sh "pandoc" "--wrap=none" "-f" "html" "-t" "asciidoc"
                              :in (.getBytes $))
                    (get $ :out)))}))

;; Writing the actual post in the right format is pretty simple.
;; The first three lines are the title, author (required although not used) and
the publish date.
(defn write-post! [{:keys [title date content slug]}]
  (spit (fs/file "posts" (str slug ".adoc"))
        (str "= " title "\n"
             "Oliver Caldwell\n"
             date "\n\n"
             content)))

;; Finally, we kick everything off by finding posts in a sibling project I have cloned locally.
(comment
  (->> (legacy-post-paths "../olical.github.io")
       (map ->post-data)
       (run! write-post!)))
```

It’s fairly long but quite straightforward, feel free to take this code and adapt it for your own purposes!

## Development workflow

While working on the templates, Clojure or posts I ended up with three terminals running three different `make` commands.

* `make serve` to serve the `output` directory using Python’s `http.server` module.
* `make propel` to start a REPL with a [socket prepl](https://oli.me.uk/clojure-socket-prepl-cookbook) attached, this is nice and easy thanks to [Propel](https://github.com/Olical/propel) ([REPLing into projects with prepl and Propel](https://oli.me.uk/repling-into-projects-with-prepl-and-propel/)).
* `make watch` to start watching source files for changes with [entr](http://eradman.com/entrproject/). Any time a file changes it sends `(blog.render/render!)` to the REPL via the prepl.

This means every time you change the Clojure, HTML or posts they’ll all be rebuilt in an atomic way. Near enough as soon as you’ve written your file you can refresh the browser to see the result.

This entr workflow can be used for a bunch of things including re-running a test or reloading a namespace every time you change a file. If you start your REPL with Propel and the `-w` argument, the port will be written to `.prepl-port` (which I’ve configure [Conjure](https://github.com/Olical/conjure) to connect to on startup), you can then set up entr with something like this.

```bash
find src test -type f | entr bash -c "echo \"(require 'blog.render) (blog.render/render!) :repl/quit\" | netcat localhost \$(cat .prepl-port)"
```

## Reuse

If you like the sound of this simple "compile AsciiDoc with Clojure" approach, go ahead and fork [Olical/blog](https://github.com/Olical/blog). You can easily rewrite the templates and styles after deleting the `posts` directory contents and replacing it with your own "Hello, World!" post.

If a few people started playing with this code and found it really useful I’d even consider ripping the core of it out into some sort of library, I just don’t have the time to do that and steward it right now. If this concept has interested you, come and chat with me on twitter or over email using the links in the footer.
