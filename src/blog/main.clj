(ns blog.main
  (:require [me.raynes.fs :as fs]
            [selmer.parser :as tmpl]
            [blog.adoc :as adoc]))

(def ^:private output-dir (fs/file "output"))
(def ^:private posts-dir (fs/file "posts"))
(def ^:private base-dir (fs/file "base"))

(defn- spit-post!
  "Write the given post to the output directory under the appropriate name.
  Assuming it's been run through adoc/parse already and contains the resulting keys."
  [{:keys [slug html]}]
  (let [prefix (fs/file output-dir slug)]
    (fs/mkdirs prefix)
    (spit (str (fs/file prefix "index.html")) html)))

(defn- tmpl
  "Render a template within the base template with the provided options."
  [tmpl-name opts]
  (tmpl/render-file
    "base.html"
    (assoc opts
           :body (tmpl/render-file
                   (str tmpl-name ".html")
                   opts))))

(defn -main
  "Performs all building of the blog from source."
  []
  (fs/delete-dir output-dir)
  (fs/copy-dir base-dir output-dir)
  (let [posts (->> (fs/list-dir posts-dir)
                   (map (fn [file]
                          (-> (adoc/parse (slurp file))
                              (assoc :slug (fs/name file)))))
                   (map (fn [{:keys [title] :as post}]
                          (-> post
                              (update :html
                                      #(tmpl "post"
                                             {:title title
                                              :content %}))))))]
    (run! spit-post! posts)))

(comment
  (time (-main)))
