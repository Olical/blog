(ns blog.adoc-test
  (:require [clojure.test :as t]
            [java-time :as jt]
            [blog.adoc :as adoc]))

(t/deftest parse
  (t/testing "empty -> empty"
    (t/is (= nil (adoc/parse nil)))
    (t/is (= {:html ""} (adoc/parse ""))))

  (t/testing "basic syntax"
    (t/is (= {:html "<div class=\"paragraph\">\n<p>foo <strong>bar</strong></p>\n</div>"}
             (adoc/parse "foo *bar*"))))

  (t/testing "header"
    (t/is (= {:title "Foo"
              :html "<div class=\"paragraph\">\n<p>Bar!</p>\n</div>"
              :author "Oliver Caldwell"
              :date (jt/local-date "2019-10-13")}
             (adoc/parse "= Foo\nOliver Caldwell\n2019-10-13\n\nBar!"))))

  (t/testing "syntax highlighting"
    (t/is (= {:html "<div class=\"listingblock\">\n<div class=\"content\">\n<pre class=\"rouge highlight\"><code data-lang=\"clojure\"><span class=\"p\">(</span><span class=\"nb\">+</span><span class=\"w\"> </span><span class=\"mi\">10</span><span class=\"w\"> </span><span class=\"mi\">20</span><span class=\"p\">)</span></code></pre>\n</div>\n</div>"}
             (adoc/parse "[source,clojure]\n----\n(+ 10 20)\n----")))))
