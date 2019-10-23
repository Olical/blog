(ns blog.render
  "Render the blog from the post source."
  (:require [clojure.string :as str]
            [me.raynes.fs :as fs]
            [selmer.parser :as tmpl]
            [taoensso.timbre :as log]
            [com.climate.claypoole :as cp]
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

(defn- tmpl-xml
  "Render a raw XML template without a wrapper."
  [tmpl-name opts]
  (tmpl/render-file
    (str tmpl-name ".xml")
    opts))

(defn- spit-post!
  "Write the given post to the output directory under the appropriate name.
  Assuming it's been run through adoc/parse already and contains the resulting keys."
  [{:keys [title slug html]}]
  (let [prefix (fs/file temp-dir slug)]
    (log/infof "Writing post: %s (%s)" title slug)
    (fs/mkdirs prefix)
    (spit (fs/file prefix "index.html") html)))

(defn- spit-index!
  "Write the index.html file, linking to all of the given posts."
  [posts]
  (log/info "Writing index.")
  (spit (fs/file temp-dir "index.html")
        (tmpl "index"
              {:years (->> posts
                           (group-by
                             (fn [{:keys [date]}]
                               (str/replace date #"-\d\d-\d\d" "")))
                           (map (fn [[year posts]]
                                  {:year year
                                   :posts posts})))})))

(defn- spit-sitemap!
  "Write the sitemap.xml file."
  [posts]
  (log/info "Writing sitemap.")
  (spit (fs/file temp-dir "sitemap.xml")
        (tmpl-xml "sitemap"
                  {:latest-date (->> posts
                                     (map :date)
                                     (sort)
                                     (last))
                   :posts posts})))

(defn- spit-feed!
  "Write the feed.xml file."
  [posts]
  (log/info "Writing feed.")
  (spit (fs/file temp-dir "feed.xml")
        (tmpl-xml "feed"
                  {:latest-date (->> posts
                                     (map :date)
                                     (sort)
                                     (last))
                   :posts posts})))

(defn- spit-404!
  "Write the 404.html file."
  []
  (log/info "Writing 404.")
  (spit (fs/file temp-dir "404.html")
        (tmpl "404" {})))

(defn- source->post
  "Parse and render the post from AsciiDoc source.
  Calculates the slug from the file path."
  [{:keys [file source]}]
  (let [{:keys [title date html] :as post} (adoc/parse source)]
    (-> post
        (merge post
               {:slug (fs/name file)
                :content-html html
                :html (tmpl "post"
                            {:title title
                             :date date
                             :content html})}))))

(defn render!
  "Performs all building of the blog from source."
  []
  (fs/delete-dir temp-dir)
  (fs/copy-dir base-dir temp-dir)
  (let [posts (->> (fs/list-dir posts-dir)
                   (cp/upmap 8 (fn [file]
                                 (log/infof "Parsing: %s" (fs/name file))
                                 (source->post {:file file
                                                :source (slurp file)}))))
        !post-write-results (future
                              (doall
                                (cp/upmap 8 spit-post! posts)))]

    (doall
      (cp/pcalls
        4
        #(spit-index! posts)
        #(spit-sitemap! posts)
        #(spit-feed! posts)
        #(spit-404!)))

    @!post-write-results

    (fs/delete-dir output-dir)
    (fs/copy-dir temp-dir output-dir)

    (log/infof "All done! Processed %d posts." (count posts))))

(comment
  (render!))
