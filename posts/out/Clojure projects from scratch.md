---
alias: clojure-projects-from-scratch
tags:
- blog-post
- imported-blog-post
---


This post is intended _primarily_ for two groups of people:

1. People just starting out in Clojure, who know parts of the language but don’t know how to begin structuring a real project.
2. Seasoned Clojurians who wish to see how to structure a project with the new Clojure CLI + `deps.edn` and not [lein](https://leiningen.org/) or [boot](http://boot-clj.com/) (even though they’re still awesome).

My goal is to teach you how to go from an empty directory, to a project you can run, test, compile and (if you wish to) publish with ease.
We’re going to get there through a series of relatively small steps so you can understand all the tools you’re using.

There won’t be an awful lot of Clojure code here, so don’t worry if you’re still getting your head around the language itself.
The only code example will be a "Hello, World!", if that helps.

**📌 NOTE**\
This post assumes usage of Linux, OSX or similar, I’m afraid it isn’t intended for Windows users since I just don’t have the knowledge to help you there.
Some of the information will apply, but you’ll have to adapt things, I’m sure you can find Windows specific guides for the parts that don’t fit.

I’m not going into what editor you should use because that’s a book in itself.
If you’re totally at a loss, check out [Cursive](https://cursive-ide.com/), although I use [Spacemacs](http://spacemacs.org/) because I can’t survive without good Vim emulation.
There’s probably a great plugin for your editor of choice and instructions on getting started, have a Google.

A lot of what I’m going to be talking about can be found in practice in [github.com/robert-stuttaford/bridge](https://github.com/robert-stuttaford/bridge), you may want to have a peruse at some point.

## Installing the Clojure CLI

To run Clojure you’ll need the command line tool (introduced around the time of Clojure 1.9) that manages dependencies and allows you execute code.

If you’re on OSX, you can use `brew` to install the CLI.

```bash
$ brew install clojure
```

I have found that I could install it through the Arch Linux package manager although it was slightly out of date at the time of writing, so I don’t recommend this just yet.
If you’re on Linux you can run the manual installer easily enough.

```bash
$ curl -O https://download.clojure.org/install/linux-install-1.9.0.326.sh
$ sudo bash linux-install-1.9.0.326.sh
```

To update, use the package manager you used for the installation or find the latest Linux installer URL on the [getting started](https://clojure.org/guides/getting_started) page.

You should now be able to drop into a Clojure REPL with one command.
You can run `clojure` or `clj` in your terminal, the latter has a slightly better editing experience but requires you to have `rlwrap` installed.

```bash
$ clj
Clojure 1.9.0
user=> (+ 10 15)
25
```

## Initial files

Presuming our project is called `hey`, let’s go ahead and create these directories and files:

```bash
$ mkdir -p hey/{src/hey,test/hey}
$ cd hey
$ touch src/hey/core.clj test/hey/core_test.clj
```

This provides us with the following directory structure:

```console
$ tree
.
├── src
│   └── hey
│       └── core.clj
└── test
    └── hey
        └── core_test.clj

4 directories, 2 files
```

Let’s insert some content into these files:

### src/hey/core.clj

```clojure
(ns hey.core)

(defn -main []
  (println "Hello, World!"))
```

### test/hey/core_test.clj

```clojure
(ns hey.core-test
  (:require [clojure.test :as t]
            [hey.core :as sut]))

(t/deftest basic-tests
  (t/testing "it says hello to everyone"
    (t/is (= (with-out-str (sut/-main)) "Hello, World!\n"))))
```

The main namespace simply prints "Hello, World!" when executed and the test confirms that functionality.

## Running your code

Now that we have a bare bones program and test file in our project directory, we’re probably going to want to run it.
We can do that with the Clojure CLI, go ahead and execute the following:

```bash
$ clj -m hey.core
```

You should see "Hello, World!" printed in your terminal.
Let’s try jumping into a REPL so we can interact with our code directly:

```bash
$ clj
Clojure 1.9.0
user=> (load "hey/core")
nil
user=> (in-ns 'hey.core)
#object[clojure.lang.Namespace 0x2072acb2 "hey.core"]
hey.core=> (-main)
Hello, World!
nil
```

If your editor supports Clojure, you can probably connect a REPL and interact with your code through there too.
With spacemacs I would type `,'` to "jack in" with CIDER.
I can then use `,ee` to evaluate expressions as I work.

## Testing

We have a test file but no way to run it.
We could create our own test runner namespace that executed `clojure.test/run-all-tests`, but that requires telling it about every testing namespace we have in our project.
It gets tedious after a while, so let’s get something that does it for us.

Create a file called `deps.edn` at the top of your project and add the following to it:

### deps.edn

```clojure
{:deps
 {org.clojure/clojure {:mvn/version "1.9.0"}}

 :aliases
 {:test
  {:extra-paths ["test"]
   :extra-deps
   {com.cognitect/test-runner {:git/url "https://github.com/cognitect-labs/test-runner"
                               :sha "5f2b5c2efb444df76fb5252102b33f542ebf7f58"}}
   :main-opts ["-m" "cognitect.test-runner"]}}}
```

Let’s break this down:

* `:deps` is where we specify our dependencies, right now all we’re depending on is Clojure 1.9.0.
* `:aliases` is where we specify special overrides that we can apply with the `-A` argument to the CLI.
* `:test` is the name of our alias, it adds the `test` directory to the paths list and `com.cognitect/test-runner` to the dependencies.
* `:main-opts` instructs Clojure that we want these arguments applied when the alias is active.
In this case, we’re using `-m` to specify which namespace to execute.

The usage of `deps.edn` is documented further in [the deps guide](https://clojure.org/guides/deps_and_cli).

This will discover and run our test for us, let’s run it now:

```bash
$ clj -Atest

Running tests in #{"test"}

Testing hey.core-test

Ran 1 tests containing 1 assertions.
0 failures, 0 errors.
```

Hopefully you see the same success message as myself.
You can see that we applied the values specified in our alias with the `-Atest` argument.

## Building executable jars

In this section we’re going to build an "uberjar" containing your application alongside all of the dependencies it requires to run.

> You only need to bother with uberjars if you’re writing an application you wish to deploy and run somewhere.
> If you’re building a library for others to depend on you probably won’t need this.

Compiling your project into an uberjar will involve similar steps to getting your tests running, we’re going to add another alias with another dependency which does the job for us.

Go ahead and add this new alias to the `:aliases` section of your `deps.edn` file, next to the `:test` alias:

```clojure
:uberjar
{:extra-deps
 {pack/pack.alpha
  {:git/url "https://github.com/juxt/pack.alpha.git"
   :sha     "e6d0691c5f58135e1ef6fb1c9dda563611d36205"}}
 :main-opts ["-m" "mach.pack.alpha.capsule" "deps.edn" "dist/hey.jar"]}
```

We can now build a jar that we can execute directly through the `java` program, without the Clojure CLI:

```bash
$ clj -Auberjar
$ java -jar dist/hey.jar # Drops us into a Clojure REPL.
$ java -jar dist/hey.jar -m hey.core # Executes our "Hello, World!".
```

Please note that your code has not been AOT (ahead of time) compiled, it’s still just plain Clojure that’s compiled as and when it’s required at run time.
This can mean very slightly slower startup times when you’re working with a large codebase.

If this becomes an issue for you you’ll have to work out how to perform AOT compilation as you build your uberjar.
By then, pack may even support it as a core feature.

## Publishing to Clojars

In this section we’re going to publish a small jar file to [Clojars](https://clojars.org/) containing only your source code, we’ll be using maven to perform the deploy.

> This is intended for libraries that others will depend on and use, you won’t need to worry about this section if you’re building an application you’ll be running.

First, we’re going to add your Clojars login to `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>clojars</id>
      <username>username</username>
      <password>password</password>
    </server>
  </servers>
</settings>
```

Now we’re going to generate your base `pom.xml` file, you should run this command whenever you’re going to publish so the dependencies get updated:

```bash
$ clj -Spom
```

Here’s my example version, I’ve annotated each section.
There’s some you’ll want to change as well as a couple of parts you’ll want to add and update:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <!-- Here by default, but will need updating -->
  <groupId>org.clojars.olical</groupId>
  <artifactId>hey</artifactId>
  <version>2.1.0-SNAPSHOT</version>
  <name>hey</name>

  <!-- Here by default, updated by `clj -Spom` -->
  <dependencies>
    <dependency>
      <groupId>org.clojure</groupId>
      <artifactId>clojure</artifactId>
      <version>1.9.0</version>
    </dependency>
  </dependencies>
  <build>
    <sourceDirectory>src</sourceDirectory>

    <!-- Essential for bundling your source files into the JAR -->
    <resources>
      <resource>
        <directory>src</directory>
      </resource>
    </resources>
  </build>
  <repositories>
    <repository>
      <id>clojars</id>
      <url>https://clojars.org/repo</url>
    </repository>
  </repositories>

  <!-- Essential for pushing to Clojars -->
  <distributionManagement>
    <repository>
      <id>clojars</id>
      <name>Clojars repository</name>
      <url>https://clojars.org/repo</url>
    </repository>
  </distributionManagement>

  <!-- Optional extras for Clojars -->
  <description>Just a Hello, World!</description>
  <url>https://github.com/Olical/clojure-hey-example</url>
  <licenses>
    <license>
      <name>Unlicense</name>
      <url>https://unlicense.org/</url>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/Olical/clojure-hey-example</url>
  </scm>
</project>
```

We can now tell maven to build and deploy a jar to Clojars:

```bash
$ mvn deploy
```

A lot of this information comes from [Clojar’s guide to pushing](https://github.com/clojars/clojars-web/wiki/Pushing) and [Maven’s guide to deploying 3rd party jars](https://maven.apache.org/guides/mini/guide-3rd-party-jars-remote.html).

If everything went to plan, your Clojars account should now contain a fresh new jar.
Note that this is _not_ an uberjar, it only contains your source files and dependency information, not the actual dependencies themselves.
The dependencies will be resolved by a tool such as the Clojure CLI.

## Ergonomics

As it stands, to deploy our jar to Clojars we’ll want to take the following steps:

* Update the version number in our `pom.xml`.
* Run the tests with `clj -Atest`.
* Run `clj -Spom` to update our `pom.xml` with any dependency changes.
* Run `mvn deploy`.

This isn’t particularly catchy, so we’ll wrap everything we’ve seen so far in a pretty little `Makefile`:

```makefile
.PHONY: run test uberjar deploy

run:
	clj -m hey.core

test:
	clj -Atest

uberjar:
	clj -Auberjar

deploy: test
	clj -Spom
	mvn deploy
```

Now all you need to do when you wish to deploy is bump the version number in your `pom.xml` and execute `make deploy`.

## Thanks!

I really hope this post has helped you out!
You can find the example project I built during the writing of this post at [github.com/Olical/clojure-hey-example](https://github.com/Olical/clojure-hey-example) and the [Clojars page here](https://clojars.org/org.clojars.olical/hey/versions/2.1.0-SNAPSHOT).

Happy Clojuring!
