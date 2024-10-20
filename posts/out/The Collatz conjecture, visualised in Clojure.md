---
alias: the-collatz-conjecture-visualised-in-clojure
tags:
- blog-post
- imported-blog-post
---


Before I begin, watch [the video](https://www.youtube.com/watch?v=LqKpkdRRLZw) that prompted me to do this in the first place.

Okay, now you know what I’m talking about, the [Collatz conjecture](https://en.wikipedia.org/wiki/Collatz_conjecture). It’s scarily simple, you take a number, if it’s even you halve it, if it’s odd you multiply it by three and add one. Repeat this and you will end up at one, every time. Well, that’s the conjecture, maybe it doesn’t end up at one for some numbers, we just haven’t been able to prove it.

The video shows a beautiful way of visualising this problem and I felt it was a nice thing to try and render with some code. If you’ve read any of my previous things or know me even a little, Clojure is not a surprising choice of tool, I love the language and think it is well suited to most, if not all, problems I face.

I must warn you that I never reproduced the one from the video, just something that follows the same ideas and looks kind of similar from a distance. I honestly can’t work out what magic was used to get it to look that good, maybe some special rendering techniques that are just beyond me at the moment. Maybe you’ll be able to take my repository and perfect it!

## Collatz in Clojure

Before I even attempt to render this, I’ll need some functions that generate the Collatz conjecture numbers. I will refer to these as a Collatz sequence or Collatz seqs. You can find the full code in [Olical/collatz](https://github.com/Olical/collatz), but I will be breaking it down here for you.

```
(defn number-gt-zero?
  "Checks if n is a number that is greater than zero."
  [n]
  (and (number? n)
       (> n 0)))

(defn next-collatz
  "Returns the next step in the Collatz sequence."
  [n]
  {:pre [(number-gt-zero? n)]}
  (cond
    (even? n) (-> n (/ 2))
    (odd? n)  (-> n (* 3) (inc))))

(defn collatz
  "Generate a lazy-seq of Collatz conjecture numbers starting at the given number."
  [n]
  {:pre [(number-gt-zero? n)]}
  (lazy-seq
   (cons n
         (when (> n 1)
           (collatz (next-collatz n))))))
```

This function provides a lazy sequence abstraction on top of the ideas the Collatz conjecture provides. It allows us to build more interesting things on top of the seq abstraction without worrying about memory or implementation details.

```
;; Get the first 10 numbers in the Collatz seq starting at 1000000.
(take 10 (collatz 1000000))

;; The first number in the seq will always be the argument you provided.
;; The last will always be 1.

(last (collatz 1000000))

;; 1. We hope.
```

The next logical step from here, in my opinion, is to create a _lazy-seq_ of Collatz seqs. So if I ask for _(collatz-tree 10000)_ I will get a seq of seqs. The first item is the same as _(collatz 10000)_, the second is _(collatz 9999)_ and the third being _(collatz 9998)_. You get the idea. What we are left with is a seq abstraction which, if fully realised, would be pretty huge. Luckily, thanks to the magic of lazy sequences, almost nothing will actually be in memory at any one time.

```
(defn collatz-tree
  "Generate a lazy-seq of lazy-seqs from the collatz function. Starts the seqs at (collatz n), counts down until (collatz 1)."
  [n]
  {:pre [(number-gt-zero? n)]}
  (lazy-seq
   (cons (collatz n)
         (when (> n 1)
           (collatz-tree (dec n))))))
```

We can walk this tree, or seq of seqs, to render the visualisation you saw in the video. Or something close to it I hope, I’m no expert with [Quil](https://github.com/quil/quil), but I’ll try my best.

```
(collatz-tree 4)

;; Yields: ((4 2 1) (3 10 5 16 8 4 2 1) (2 1) (1))
```

The commit at this point was [4a155ed](https://github.com/Olical/collatz/commit/4a155ed3a80e177655cbe41ba38e783978f17cb7). Feel free to take this abstraction and do what you want with it, copy and paste it into your project if that’s easiest.

## Visualising the tree

Now for the pretty part. I hope. I’m starting with the default Quil setup the lein template provides you with, this includes the functional middleware which makes it a bit nicer to work with (although I found I wasn’t really using the state management very much at all). After a little bit of tinkering I ended up with this rough attempt.

![[/Attachments/imported-blog-posts/legacy-images/2017/03/Screenshot_2017-03-31_17-13-04]]

Although if you squint, this sort of looks similar, I’m not very happy with it. For starters, my use of the Quil API is a bit questionable and it definitely doesn’t follow the same rules as the one in the video. My ideal goal is to basically mimic the original material including random colours. I really hope this doesn’t breach copyright or something, if so, I’m very sorry, send me an email.

Here’s what I had after some more tinkering.

![[/Attachments/imported-blog-posts/legacy-images/2017/03/Screenshot_2017-03-31_19-28-43.png]].,width=804,height=802]

> Although I’m generating the bordered lines with a sort of hack (one bigger black line with a smaller coloured line on top of it), it actually leads to this neat hand drawn effect. So, although it’s not right, I actually like the outcome. It feels more organic than hard, anti-aliased, machine cut edges. To me, anyway.

It definitely looks better now, but it’s still not true to the original. A huge problem with this is that I’m drawing back over lines so many times, I need to optimise the tree so I don’t repeat myself, this requires a different approach to rendering though, I need a sort of linked list I can follow so I know when I’m back to somewhere I’ve been before and can stop rendering that path.

That’s going to mean forgoing a bit of laziness and building a big data structure that I can use as a lookup table, I think it’s worth it for the rendering optimisations. That should allow me to render the branches in different orders too instead of largest to smallest.

## Epiphany time

Two things happened while developing this project and writing this post (I’ve been writing it as I developed it to capture every step, so it may seem a bit jumbled in places).

First, I realised that the tree was upside down. The end of any Collatz seq is always one (we think?), if you remember my code from earlier, I iterate over these sequences and draw the segments of the branch one at a time. This means every branch _ends_ with one, but it needs to _start_ with one.

The other thing that happened was one of the authors of [the book](http://www.bloomsbury.com/uk/visions-of-numberland-9781408888988/) that inspired the video that inspired me, [Edmund Harriss](https://twitter.com/Gelada), replied to one of my tweets with a couple of tips I’ll probably need after I fix the whole upside down problem.

> [@OliverCaldwell](https://twitter.com/OliverCaldwell) The key is balancing the left and right rotation so you go in a straight line if not growing.
>
> — Edmund Harriss (@Gelada) [April 1, 2017](https://twitter.com/Gelada/status/848020385459318784)

> [@OliverCaldwell](https://twitter.com/OliverCaldwell) of course, it is fun to play with. The branches represent either a growth (x2) or a shrink (-1 and /3) the rotations can reflect that.
>
> — Edmund Harriss (@Gelada) [April 1, 2017](https://twitter.com/Gelada/status/848158534546206721)

Just as a reminder, this is what I want it to look like.

![[/Attachments/imported-blog-posts/legacy-images/2017/04/C8FvTBiW0AAFGpG-809x1024.jpg]].,width=792,height=1002]

## Flipping the tree

So I want to get it looking semi-accurate before I try to optimise, maybe the optimisations won’t actually be required it it’s “good enough”. I’m going to flip the tree by reversing the Collatz sequences that comprise my “Collatz tree” sequence.

Sadly, even after flipping the tree over and playing around with more parameters, I just couldn’t match the awesome original design. I guess this is a testament to how good the original authors are at creating visualisations from math alone! Here’s a few things I ended up with to wrap up my stumbling in the dark.

![[/Attachments/imported-blog-posts/legacy-images/2017/04/Screenshot_2017-04-01_12-22-38.png]].,width=804,height=802]

![[/Attachments/imported-blog-posts/legacy-images/2017/04/Screenshot_2017-04-04_21-42-51.png]].,width=804,height=802]

![[/Attachments/imported-blog-posts/legacy-images/2017/04/Screenshot_2017-04-04_21-46-43.png]].,width=804,height=802]

![[/Attachments/imported-blog-posts/legacy-images/2017/04/Screenshot_2017-04-04_21-52-49]]

From the thinner versions you can quite clearly see the need for deduplication, if you just draw _everything_ over the top of each other, not only is it slow, but it also looks messy. I definitely needed to prepare my data a little better, but this post that was supposed to be a small little experiment was beginning to drag on by then.

## Close but no cigar

I’m disappointed that I couldn’t get it quite right, although I think I probably could if I just put more time into it. Sadly, visualisations aren’t really my forte or main interest. I’m more of a “programming languages, data structures and text editors” kind of programmer. It’s a little bit niche, okay.

I may revisit this some day and attempt to deduplicate that tree since I think there’s value there in performance and style. Until then feel free to rip the repository to pieces, [Olical/collatz](https://github.com/Olical/collatz), if you didn’t spot it earlier. I’ll post the visualisation code below too, just so you don’t need to go elsewhere to see how badly I messed up, I’m sure this is obvious to _someone_ out there in the wide and wonderful world.

I hope you found this slightly interesting, and at the very least it has passed on the inspiration I had to do something far better than I produced.

```
(ns collatz.visualisation
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [collatz.core :as c])
  (:gen-class))

(defn reversed-indexed
  "Returns the lazy sequence but each item is now a vector pair. The first value
  is the index, the second is the original value from the seq. The sequence is
  also reversed."
  [items]
  (map-indexed (fn [n v] [n v]) (reverse items)))

(defn gen-tree
  "Generates a full Collatz tree by building the lazy-seqs and reversing them all."
  [n]
  (->> n c/collatz-tree (map reversed-indexed) reversed-indexed))

(def size {:x 800 :y 800})
(def tree (gen-tree 10000))
(def part-size 8)

(defn render-branch
  "Render a single Collatz branch."
  [[bn branch]]
  (q/push-matrix)
  (doseq [[pn part] branch]
    (q/stroke 0)
    (q/stroke-weight 15)
    (q/line 0 0 0 part-size)

    (q/stroke (+ 155 (mod bn 100)) 100 100)
    (q/stroke-weight 13)
    (q/line 0 (if (= pn 0) 0 -3) 0 part-size)

    (q/translate 0 part-size)
    (q/rotate (q/radians (if (even? part) 4 -4))))
  (q/pop-matrix))

(defn setup
  "Set up the context and state."
  []
  (q/frame-rate 25)
  {:tree tree
   :render? false})

(defn update-state
  "Perform modifications to the state for the next render."
  [state]
  {:tree tree
   :render? (:should-render? state)})

(defn draw-state
  "Render the current state."
  [state]
  (when (:render? state)
    (q/background 255 255 255)
    (q/translate 200 (-> size :y (- 20)))
    (q/rotate (q/radians 110))
    (doseq [branch (:tree state)]
      (render-branch branch))))

(defn key-pressed
  "Handle a key press event."
  [state event]
  (case (:key-code event)
    10 (assoc state :should-render? true)
    state))

(defn -main
  "Initialise the sketch."
  []
  (q/sketch
   :title "Collatz in Clojure"
   :size (map size [:x :y])
   :setup #'setup
   :update #'update-state
   :draw #'draw-state
   :features []
   :middleware [m/fun-mode]
   :key-pressed key-pressed))
```
