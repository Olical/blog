(ns blog.adoc-test
  (:require [clojure.test :as t]
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
              :date "2019-10-13"}
             (adoc/parse "= Foo\nOliver Caldwell\n2019-10-13\n\nBar!")))))
