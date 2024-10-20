# Taming clojure.spec with expound

From Clojure 1.9 and up, the core language is guarded by various [clojure.spec](https://clojure.org/about/spec) definitions. This means, even if you’re not using spec directly yourself, you will potentially encounter spec derived errors.

These errors, much like all errors in Clojure, are extremely verbose and make it hard to discern what the actual problem is at a glance. These errors are great for machines but not so great for humans, who happen to be the primary consumer of these errors. I consider this to be a bit of a design flaw within spec, maybe this will improve over time.

Until such potential improvements are implemented, thanks to the power and wonders of Clojure, we can use a library called [expound](https://github.com/bhb/expound) to make our lives easier. This tool is inspired by Elm and it shows. Without expound and this incorrect syntax:

```
(let [{:a b} {:a 10}]
  b)
```

We get this beauty of an error from Clojure itself:

```
CompilerException clojure.lang.ExceptionInfo: Call to clojure.core/let did not conform to spec:
In: [0 0 0 0] val: :a fails spec: :clojure.core.specs.alpha/local-name at: [:args :bindings :binding :map :mb 0 :sym] predicate: simple-symbol?
In: [0 0 0 0] val: :a fails spec: :clojure.core.specs.alpha/seq-binding-form at: [:args :bindings :binding :map :mb 0 :seq] predicate: vector?
In: [0 0 0 0] val: :a fails spec: :clojure.core.specs.alpha/map-bindings at: [:args :bindings :binding :map :mb 0 :map] predicate: coll?
In: [0 0 0 0] val: :a fails spec: :clojure.core.specs.alpha/map-special-binding at: [:args :bindings :binding :map :mb 0 :map] predicate: map?
In: [0 0 0 0] val: :a fails spec: :clojure.core.specs.alpha/ns-keys at: [:args :bindings :binding :map :nsk 0] predicate: qualified-keyword?
In: [0 0 0 1] val: b fails spec: :clojure.core.specs.alpha/ns-keys at: [:args :bindings :binding :map :nsk 1] predicate: vector?
In: [0 0 0 0] val: :a fails spec: :clojure.core.specs.alpha/map-bindings at: [:args :bindings :binding :map :msb 0] predicate: #{:as :or :syms :keys :strs}
In: [0 0] val: {:a b} fails spec: :clojure.core.specs.alpha/local-name at: [:args :bindings :binding :sym] predicate: simple-symbol?
In: [0 0] val: {:a b} fails spec: :clojure.core.specs.alpha/seq-binding-form at: [:args :bindings :binding :seq] predicate: vector?
:clojure.spec.alpha/spec  #object[clojure.spec.alpha$regex_spec_impl$reify__1200 0x6ef7c8e9 "clojure.spec.alpha$regex_spec_impl$reify__1200@6ef7c8e9"]
:clojure.spec.alpha/value  ([{:a b} {:a 10}] b)
:clojure.spec.alpha/args  ([{:a b} {:a 10}] b)
 #:clojure.spec.alpha{:problems ({:path [:args :bindings :binding :sym], :pred clojure.core/simple-symbol?, :val {:a b}, :via [:clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/local-name], :in [0 0]} {:path [:args :bindings :binding :seq], :pred clojure.core/vector?, :val {:a b}, :via [:clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/seq-binding-form], :in [0 0]} {:path [:args :bindings :binding :map :mb 0 :sym], :pred clojure.core/simple-symbol?, :val :a, :via [:clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/map-binding-form :clojure.core.specs.alpha/map-bindings :clojure.core.specs.alpha/map-binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/local-name], :in [0 0 0 0]} {:path [:args :bindings :binding :map :mb 0 :seq], :pred clojure.core/vector?, :val :a, :via [:clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/map-binding-form :clojure.core.specs.alpha/map-bindings :clojure.core.specs.alpha/map-binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/seq-binding-form], :in [0 0 0 0]} {:path [:args :bindings :binding :map :mb 0 :map], :pred clojure.core/coll?, :val :a, :via [:clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/map-binding-form :clojure.core.specs.alpha/map-bindings :clojure.core.specs.alpha/map-binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/map-binding-form :clojure.core.specs.alpha/map-bindings], :in [0 0 0 0]} {:path [:args :bindings :binding :map :mb 0 :map], :pred map?, :val :a, :via [:clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/map-binding-form :clojure.core.specs.alpha/map-bindings :clojure.core.specs.alpha/map-binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/map-binding-form :clojure.core.specs.alpha/map-special-binding], :in [0 0 0 0]} {:path [:args :bindings :binding :map :nsk 0], :pred clojure.core/qualified-keyword?, :val :a, :via [:clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/map-binding-form :clojure.core.specs.alpha/map-bindings :clojure.core.specs.alpha/ns-keys], :in [0 0 0 0]} {:path [:args :bindings :binding :map :nsk 1], :pred clojure.core/vector?, :val b, :via [:clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/map-binding-form :clojure.core.specs.alpha/map-bindings :clojure.core.specs.alpha/ns-keys], :in [0 0 0 1]} {:path [:args :bindings :binding :map :msb 0], :pred #{:as :or :syms :keys :strs}, :val :a, :via [:clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/map-binding-form :clojure.core.specs.alpha/map-bindings], :in [0 0 0 0]}), :spec #object[clojure.spec.alpha$regex_spec_impl$reify__1200 0x6ef7c8e9 "clojure.spec.alpha$regex_spec_impl$reify__1200@6ef7c8e9"], :value ([{:a b} {:a 10}] b), :args ([{:a b} {:a 10}] b)}, compiling:(/home/ollie/code.clj:20:1) 
```

Can you tell what’s going on? Probably after a little bit of time if you know what you’re looking for. Imagine this in a much more complex case though, let’s say within a few layers of macros. Now here’s the same response through expound:

```
CompilerException clojure.lang.ExceptionInfo: Call to clojure.core/let did not conform to spec:
-- Spec failed --------------------

  ([[:a ...] ...] ...)
     ^^

should satisfy

  simple-symbol?

or

  vector?

or

  coll?

or

  map?

or

  qualified-keyword?

-- Relevant specs -------

:clojure.core.specs.alpha/ns-keys:
  (clojure.spec.alpha/tuple
   (clojure.spec.alpha/and
    clojure.core/qualified-keyword?
    (fn*
     [p1__45#]
     (clojure.core/-> p1__45# clojure.core/name #{"syms" "keys"})))
   (clojure.spec.alpha/coll-of
    clojure.core/simple-symbol?
    :kind
    clojure.core/vector?))
:clojure.core.specs.alpha/map-special-binding:
  (clojure.spec.alpha/keys
   :opt-un
   [:clojure.core.specs.alpha/as
    :clojure.core.specs.alpha/or
    :clojure.core.specs.alpha/keys
    :clojure.core.specs.alpha/syms
    :clojure.core.specs.alpha/strs])
:clojure.core.specs.alpha/seq-binding-form:
  (clojure.spec.alpha/and
   clojure.core/vector?
   (clojure.spec.alpha/cat
    :elems
    (clojure.spec.alpha/* :clojure.core.specs.alpha/binding-form)
    :rest
    (clojure.spec.alpha/?
     (clojure.spec.alpha/cat
      :amp
      #{'&}
      :form
      :clojure.core.specs.alpha/binding-form))
    :as
    (clojure.spec.alpha/?
     (clojure.spec.alpha/cat
      :as
      #{:as}
      :sym
      :clojure.core.specs.alpha/local-name))))
:clojure.core.specs.alpha/local-name:
  (clojure.spec.alpha/and
   clojure.core/simple-symbol?
   (clojure.core/fn [%] (clojure.core/not= '& %)))
:clojure.core.specs.alpha/map-binding:
  (clojure.spec.alpha/tuple
   :clojure.core.specs.alpha/binding-form
   clojure.core/any?)
:clojure.core.specs.alpha/map-bindings:
  (clojure.spec.alpha/every
   (clojure.spec.alpha/or
    :mb
    :clojure.core.specs.alpha/map-binding
    :nsk
    :clojure.core.specs.alpha/ns-keys
    :msb
    (clojure.spec.alpha/tuple
     #{:as :or :syms :keys :strs}
     clojure.core/any?))
   :into
   {})
:clojure.core.specs.alpha/map-binding-form:
  (clojure.spec.alpha/merge
   :clojure.core.specs.alpha/map-bindings
   :clojure.core.specs.alpha/map-special-binding)
:clojure.core.specs.alpha/binding-form:
  (clojure.spec.alpha/or
   :sym
   :clojure.core.specs.alpha/local-name
   :seq
   :clojure.core.specs.alpha/seq-binding-form
   :map
   :clojure.core.specs.alpha/map-binding-form)
:clojure.core.specs.alpha/binding:
  (clojure.spec.alpha/cat
   :binding
   :clojure.core.specs.alpha/binding-form
   :init-expr
   clojure.core/any?)
:clojure.core.specs.alpha/bindings:
  (clojure.spec.alpha/and
   clojure.core/vector?
   (clojure.spec.alpha/* :clojure.core.specs.alpha/binding))

-- Spec failed --------------------

  ([[... b] ...] ...)
         ^

should satisfy

  vector?

-- Relevant specs -------

:clojure.core.specs.alpha/ns-keys:
  (clojure.spec.alpha/tuple
   (clojure.spec.alpha/and
    clojure.core/qualified-keyword?
    (fn*
     [p1__45#]
     (clojure.core/-> p1__45# clojure.core/name #{"syms" "keys"})))
   (clojure.spec.alpha/coll-of
    clojure.core/simple-symbol?
    :kind
    clojure.core/vector?))
:clojure.core.specs.alpha/map-bindings:
  (clojure.spec.alpha/every
   (clojure.spec.alpha/or
    :mb
    :clojure.core.specs.alpha/map-binding
    :nsk
    :clojure.core.specs.alpha/ns-keys
    :msb
    (clojure.spec.alpha/tuple
     #{:as :or :syms :keys :strs}
     clojure.core/any?))
   :into
   {})
:clojure.core.specs.alpha/map-binding-form:
  (clojure.spec.alpha/merge
   :clojure.core.specs.alpha/map-bindings
   :clojure.core.specs.alpha/map-special-binding)
:clojure.core.specs.alpha/binding-form:
  (clojure.spec.alpha/or
   :sym
   :clojure.core.specs.alpha/local-name
   :seq
   :clojure.core.specs.alpha/seq-binding-form
   :map
   :clojure.core.specs.alpha/map-binding-form)
:clojure.core.specs.alpha/binding:
  (clojure.spec.alpha/cat
   :binding
   :clojure.core.specs.alpha/binding-form
   :init-expr
   clojure.core/any?)
:clojure.core.specs.alpha/bindings:
  (clojure.spec.alpha/and
   clojure.core/vector?
   (clojure.spec.alpha/* :clojure.core.specs.alpha/binding))

-- Spec failed --------------------

  ([[:a ...] ...] ...)
     ^^

should be one of: `:as`,`:or`,`:syms`,`:keys`,`:strs`

-- Relevant specs -------

:clojure.core.specs.alpha/map-bindings:
  (clojure.spec.alpha/every
   (clojure.spec.alpha/or
    :mb
    :clojure.core.specs.alpha/map-binding
    :nsk
    :clojure.core.specs.alpha/ns-keys
    :msb
    (clojure.spec.alpha/tuple
     #{:as :or :syms :keys :strs}
     clojure.core/any?))
   :into
   {})
:clojure.core.specs.alpha/map-binding-form:
  (clojure.spec.alpha/merge
   :clojure.core.specs.alpha/map-bindings
   :clojure.core.specs.alpha/map-special-binding)
:clojure.core.specs.alpha/binding-form:
  (clojure.spec.alpha/or
   :sym
   :clojure.core.specs.alpha/local-name
   :seq
   :clojure.core.specs.alpha/seq-binding-form
   :map
   :clojure.core.specs.alpha/map-binding-form)
:clojure.core.specs.alpha/binding:
  (clojure.spec.alpha/cat
   :binding
   :clojure.core.specs.alpha/binding-form
   :init-expr
   clojure.core/any?)
:clojure.core.specs.alpha/bindings:
  (clojure.spec.alpha/and
   clojure.core/vector?
   (clojure.spec.alpha/* :clojure.core.specs.alpha/binding))

-------------------------
Detected 3 errors
 #:clojure.spec.alpha{:problems ({:path [:args :bindings :binding :sym], :pred clojure.core/simple-symbol?, :val {:a b}, :via [:clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/local-name], :in [0 0]} {:path [:args :bindings :binding :seq], :pred clojure.core/vector?, :val {:a b}, :via [:clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/seq-binding-form], :in [0 0]} {:path [:args :bindings :binding :map :mb 0 :sym], :pred clojure.core/simple-symbol?, :val :a, :via [:clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/map-binding-form :clojure.core.specs.alpha/map-bindings :clojure.core.specs.alpha/map-binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/local-name], :in [0 0 0 0]} {:path [:args :bindings :binding :map :mb 0 :seq], :pred clojure.core/vector?, :val :a, :via [:clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/map-binding-form :clojure.core.specs.alpha/map-bindings :clojure.core.specs.alpha/map-binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/seq-binding-form], :in [0 0 0 0]} {:path [:args :bindings :binding :map :mb 0 :map], :pred clojure.core/coll?, :val :a, :via [:clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/map-binding-form :clojure.core.specs.alpha/map-bindings :clojure.core.specs.alpha/map-binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/map-binding-form :clojure.core.specs.alpha/map-bindings], :in [0 0 0 0]} {:path [:args :bindings :binding :map :mb 0 :map], :pred map?, :val :a, :via [:clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/map-binding-form :clojure.core.specs.alpha/map-bindings :clojure.core.specs.alpha/map-binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/map-binding-form :clojure.core.specs.alpha/map-special-binding], :in [0 0 0 0]} {:path [:args :bindings :binding :map :nsk 0], :pred clojure.core/qualified-keyword?, :val :a, :via [:clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/map-binding-form :clojure.core.specs.alpha/map-bindings :clojure.core.specs.alpha/ns-keys], :in [0 0 0 0]} {:path [:args :bindings :binding :map :nsk 1], :pred clojure.core/vector?, :val b, :via [:clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/map-binding-form :clojure.core.specs.alpha/map-bindings :clojure.core.specs.alpha/ns-keys], :in [0 0 0 1]} {:path [:args :bindings :binding :map :msb 0], :pred #{:as :or :syms :keys :strs}, :val :a, :via [:clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/bindings :clojure.core.specs.alpha/binding :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/binding-form :clojure.core.specs.alpha/map-binding-form :clojure.core.specs.alpha/map-bindings], :in [0 0 0 0]}), :spec #object[clojure.spec.alpha$regex_spec_impl$reify__1200 0x54ba9618 "clojure.spec.alpha$regex_spec_impl$reify__1200@54ba9618"], :value ([{:a b} {:a 10}] b), :args ([{:a b} {:a 10}] b)}, compiling:(/home/ollie/code.clj:20:1) 
```

That’s much better! Suddenly you don’t have to run a REPL in your brain to understand what went wrong, the machine is telling you exactly what is wrong, where and what you can do instead. It may not be as succinct as Elm, but the information at the start is just as useful.

## Integration

Luckily, expound happens to be extremely easy to use. Hopefully we can make that easier by including it by default in a lot of beginner friendly code too. The README does a great job of explaining how to use it.

> Replace calls to `clojure.spec.alpha/explain` with `expound.alpha/expound` and to `clojure.spec.alpha/explain-str` with `expound.alpha/expound-str`.

If you don’t use _explain_ directly and you’d like _all_ spec errors to be run through expound (including those from Clojure the language), then you can hook it in globally like the following snippet. I’d recommend running this within your _(ns user)_ before your REPL loads or in the _main_ ns of your application before it starts up.

```
(ns user
  (:require [clojure.spec.alpha :as s]
            [expound.alpha :as expound]))
 
;; If you're doing this in the user ns for your repl...
(alter-var-root #'s/*explain-out* (constantly expound/printer))

;; Otherwise, you can use the method from the README.
(set! s/*explain-out* expound/printer)
```

Now any spec error generated from here on out will be formatted for human consumption by expound, excellent!

I doubt I’m alone in thinking that I’d love this to be the default within Clojure or at least extremely widespread in it’s usage. Much like figwheel for ClojureScript projects, we would always use expound alongside our specs. Maybe CIDER could be a good entry point for this addition.
