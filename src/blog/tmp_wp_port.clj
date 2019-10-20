(ns blog.tmp-wp-port
  (:require [clojure.string :as str]
            [clojure.java.shell :as shell]
            [hickory.core :as html]
            [hickory.select :as css]
            [hickory.render :as render]
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
     :slug (fs/name (fs/parent path))
     :content (-> (css/select (css/class "entry-content") dom)
                  (first)
                  (update
                    :content
                    (fn [nodes]
                      (map (fn [{:keys [attrs] :as node}]
                             (if (some-> (:class attrs) (str/includes? "crayon-syntax"))
                               (-> (css/select (css/class "crayon-plain") node)
                                   (first)
                                   (assoc :tag :pre))
                               node))
                           nodes)))
                  (render/hickory-to-html)
                  (as-> $
                    (shell/sh "pandoc" "--wrap=none" "-f" "html" "-t" "asciidoc"
                              :in (.getBytes $))
                    (get $ :out)))}))

(defn write-post! [{:keys [title date content slug]}]
  (spit (fs/file "posts" (str slug ".adoc"))
        (str "= " title "\n"
             "Oliver Caldwell\n"
             date "\n\n"
             content)))

(comment
  (->> (legacy-post-paths "../olical.github.io")
       (map ->post-data)
       (run! write-post!)))
