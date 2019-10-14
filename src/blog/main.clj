(ns blog.main
  (:require [me.raynes.fs :as fs]
            [selmer.parser :as tmpl]
            [blog.adoc :as adoc]))

(defn- spit-post!
  "Write the given post to the output directory under the appropriate name.
  Assuming it's been run through adoc/parse already and contains the resulting keys."
  [{:keys [slug html]}]
  (let [output-dir (fs/file "output" slug)]
    (fs/mkdirs output-dir)
    (spit (str (fs/file output-dir "index.html")) html)))

(defn- tmpl
  "Render a template within the base template with the provided options."
  [tmpl-name opts]
  (tmpl/render-file
    "base.html"
    (assoc opts
           :body (tmpl/render-file
                   (str tmpl-name ".html")
                   opts))))

(defn -main []
  (fs/delete-dir "output")
  (fs/mkdirs "output")
  (->> (fs/list-dir "posts")
       (map (fn [file]
              (-> (adoc/parse (slurp file))
                  (assoc :slug (fs/name file)))))
       (map (fn [{:keys [title] :as post}]
              (-> post
                  (update :html
                          #(tmpl "post"
                                 {:title title
                                  :content %})))))
       (run! spit-post!))
  (fs/copy-dir "assets" "output"))

(comment
  (-main))
