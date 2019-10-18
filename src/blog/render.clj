(ns blog.render
  "Render the blog from the post source."
  (:require [me.raynes.fs :as fs]
            [selmer.parser :as tmpl]
            [blog.adoc :as adoc]))

(defonce ^:private temp-dir (fs/temp-dir "blog"))
(def ^:private output-dir (fs/file "output"))
(def ^:private posts-dir (fs/file "posts"))
(def ^:private base-dir (fs/file "base"))

(defn- tmpl
  "Render a template within the base template with the provided options."
  [tmpl-name opts]
  (tmpl/render-file
    "base.html"
    (assoc opts
           :body (tmpl/render-file
                   (str tmpl-name ".html")
                   opts))))

(defn- spit-post!
  "Write the given post to the output directory under the appropriate name.
  Assuming it's been run through adoc/parse already and contains the resulting keys."
  [{:keys [title slug html]}]
  (let [prefix (fs/file temp-dir slug)]
    (println "Writing post:" title (str "(" slug ")"))
    (fs/mkdirs prefix)
    (spit (fs/file prefix "index.html") html)))

(defn- spit-index!
  "Write the index.html file, linking to all of the given posts."
  [posts]
  (println "Writing index containing" (count posts) "posts.")
  (spit (fs/file temp-dir "index.html")
        (tmpl "index" {:posts posts})))

(defn- source->post
  "Parse and render the post from AsciiDoc source.
  Calculates the slug from the file path."
  [{:keys [file source]}]
  (let [{:keys [title date] :as post} (adoc/parse source)]
    (-> post
        (assoc :slug (fs/name file))
        (update :html
                (fn [html]
                  (tmpl "post"
                        {:title title
                         :date date
                         :content html}))))))

(defn render!
  "Performs all building of the blog from source."
  []
  (fs/delete-dir temp-dir)
  (fs/copy-dir base-dir temp-dir)
  (let [posts (->> (fs/list-dir posts-dir)
                   (pmap (fn [file]
                           (source->post {:file file
                                          :source (slurp file)}))))]
    (spit-index! posts)
    (run! spit-post! posts))
  (fs/delete-dir output-dir)
  (fs/copy-dir temp-dir output-dir)
  (println "Done!"))

(comment
  (render!))
