---
tags:
  - blog-post
  - imported-blog-post
---
# Clojure and ClojureScript tests on Travis

As far as I can tell, there isn’t a canonical way to run your Clojure(Script) tests on [Travis](http://travis-ci.org/) through the Clojure CLI.
I think it’s slightly easier for those of you using `lein`, but here’s how to do it with `clj`.

## Dependencies

You’ll need a couple of deps to execute your tests, here’s an example `deps.edn`:

```clojure
{:deps {org.clojure/clojure {:mvn/version "1.9.0"}
        org.clojure/clojurescript {:mvn/version "1.10.238"} }
 :aliases {:test-clj {:extra-paths ["test"]
                      :extra-deps {com.cognitect/test-runner {:git/url "https://github.com/Olical/test-runner.git"
                                                              :sha "7c4f5bd4987ec514889c7cd7e3d13f4ef95f256b"}}
                      :main-opts ["-m" "cognitect.test-runner"]}
           :test-cljs {:extra-deps {olical/cljs-test-runner {:mvn/version "0.1.1"}}
                       :main-opts ["-m" "cljs-test-runner.main"]}}}
```

This pulls down my patched version of the cognitect test-runner and my ClojureScript (loose) clone of the same library.
The current cognitect test-runner doesn’t report non-zero exit statuses when tests fail, my patch adds them, I’m hoping to get it merged in some form or another soon.

Without the exit status patch Travis won’t know that your tests actually failed.

## Convenience

Here’s a `Makefile` that wraps up the test commands, it makes it a little easier to call:

```Makefile
.PHONY: test test-clj test-cljs

test: test-clj test-cljs

test-clj:
	clojure -Atest-clj

test-cljs:
	clojure -Atest-cljs
```

This is obviously optional, feel free to avoid it if you want to.

## Travis configuration

Now we have some tools to test our code, let’s configure Travis to execute them with your `.travis.yml`:

```yaml
sudo: true
language: java
script: make test
install:
  - curl -O https://download.clojure.org/install/linux-install-1.9.0.358.sh
  - chmod +x linux-install-1.9.0.358.sh
  - sudo ./linux-install-1.9.0.358.sh
jdk:
  - oraclejdk8
cache:
  directories:
    - $HOME/.m2
    - $HOME/.cljs
    - $HOME/.gitlibs
    - node_modules
    - .cpcache
    - cljs-test-runner-out
```

This configuration will set up the Clojure CLI, cache all of the appropriate directories and run your test suites.
Feel free to take parts of this that you find useful and ignore others.

You should now connect your repo to your Travis account, it will report to you whenever your tests start failing.
Enjoy!
