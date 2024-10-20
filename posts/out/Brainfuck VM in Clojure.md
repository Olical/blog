---
alias: brainfuck-vm-in-clojure
tags:
- blog-post
- imported-blog-post
---


This is my second implementation for my repository of [brainfuck interpretors](https://github.com/Wolfy87/brainfucks), this time in the wonderful language Clojure (the first was [JavaScript](/brainfuck-vm-in-javascript/)). It largely follows the same format of my initial machine but takes a route that I want to eventually refactor my JavaScript implementation to use as well. It builds a list of token hash-maps that each contain any required meta data. The JavaScript implementation (at the time of writing) uses two arrays, one for the tokens and one for the jump (loop) meta data.

## The same but better

I feel that the approach I have taken with my Clojure version is far superior. This is because it allows me to encode an optimisation in the future extremely easily. I wish to detect batches of characters, such as “+++++”, and transform it into a single “+” token with a multiplier attribute that causes the “+” token handler to increment by the multiplier, not just one. This is small but potentially significant if there’s a lot of loops and batches of tokens. It may save quite a few iterations.

I’d also like to eliminate dead code such as “[]” and “+++—” which may crop up by accident sometimes. Basically I want my program to optimise the given source as much as possible, I know a few ways and I will probably implement more of them as I develop more implementations. Speaking of which, my next one will hopefully be in Haskell. I may not survive that one.

## My desired flow

Here’s my ideal scenario for a brainfuck interpreter as it stands. The Clojure version isn’t quite there, but it’s still a good example.

1. Read the provided brainfuck source file into memory.
2. Check if the square braces are balanced, exit early if not.
3. Filter the source down to the valid characters.
4. Eliminate noops such as “[]” and “+++—“
5. Map those characters into objects with a token attribute.
6. Condense chains of tokens (such as “+++++”) down to a single token with a multiplier attribute.
7. Find all jumps between square braces and assign that data to the token objects.

Now we have the compiled program we can execute it in the obvious way.

1. Initialise a state object containing a state and memory pointer as well as a blank memory array.
2. Iterate or recurse through the tokens passing the state and program values through the appropriate handler function for the token.
3. Increment the program pointer after each step.
4. Exit if the program pointer is equal to the length of the program.

This is a fairly complex beast for such a simple language and problem, I just want to set the bar high so I really have to learn these languages in order to solve the problem with them. The harder I make it for myself the more I learn about JavaScript, Clojure, Haskell and, strangely, brainfuck. I currently have 10 more languages in my “to do” list for this project. I have no idea if I’ll ever hit that number but I can at least give it a go.

## Multimethodmadness

I relied on Clojure’s multimethods for the “select a handler for this token” part, which worked really well. Here’s **all** of the execution code.

```
(def console-reader (ConsoleReader.))

(defn read-character []
  "Reads a character from STDIN"
  (.readCharacter console-reader))

(defn safe-inc [n]
  "Treat nil values as zero."
  (inc (if (= n nil) 0 n)))

(defn safe-dec [n]
  "Treat nil values as zero."
  (dec (if (= n nil) 0 n)))

(defn current-memory-zero? [state]
  "Returns true if the current memory item of the state is 0."
  (let [value (get-in state [:memory (:memory-pointer state)])]
    (= 0 (if (= nil value) 0 value))))

(defmulti step (fn [program state] (:token (nth program (:program-pointer state)))))
(defmethod step \> [program state] (update-in state [:memory-pointer] inc))
(defmethod step \< [program state] (update-in state [:memory-pointer] dec))
(defmethod step \+ [program state] (update-in state [:memory (:memory-pointer state)] safe-inc))
(defmethod step \- [program state] (update-in state [:memory (:memory-pointer state)] safe-dec))
(defmethod step \. [program state] (print (char (get-in state [:memory (:memory-pointer state)]))) (flush) state)
(defmethod step \, [program state] (assoc-in state [:memory (:memory-pointer state)] (read-character)))
(defmethod step \[ [program state] (if (current-memory-zero? state)
                                     (update-in state [:program-pointer] #(:destination (nth program %)))
                                     state))
(defmethod step \] [program state] (if (not (current-memory-zero? state))
                                     (update-in state [:program-pointer] #(:destination (nth program %)))
                                     state))

(defn execute [program]
  "Executes a compiled brainfuck program."
  (let [program-length (count program)]
    (loop [state {:memory (hash-map 0 0)
                  :memory-pointer 0
                  :program-pointer 0}]
      (if (< (:program-pointer state) program-length)
        (recur (update-in (step program state) [:program-pointer] inc))))))
```

You can find the rest of the source in the [Clojure implementation directory](https://github.com/Wolfy87/brainfucks/blob/master/implementations/clojure/src/brainfuck/core.clj) of my project.
