(ns blog.tmp-wp-port
  (:require [clojure.string :as str]
            [hickory.core :as html]
            [hickory.select :as css]
            [me.raynes.fs :as fs]))

(defn legacy-post-paths [root]
  (->> (fs/list-dir root)
       (filter #(re-matches #"\d\d\d\d" (fs/name %)))
       (mapcat fs/list-dir)
       (mapcat fs/list-dir)
       (mapcat fs/list-dir)
       (map #(fs/file % "index.html"))))

(defn ->post-data [path]
  (let [dom (-> (slurp path)
                (html/parse)
                (html/as-hickory))]
    {:title (-> (css/select (css/class "entry-title") dom)
                (get-in [0 :content 0]))
     :date (-> (css/select (css/class "entry-date") dom)
               (get-in [0 :attrs :datetime])
               (str/replace #"T.*" ""))
     #_#_:content (css/select (css/class "entry-content") dom)}))

(-> (legacy-post-paths "../olical.github.io")
    (first)
    (->post-data))
