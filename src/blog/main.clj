(ns blog.main
  (:require [me.raynes.fs :as fs]
            [blog.adoc :as adoc]))

(defn- spit-post!
  "Write the given post to the output directory under the appropriate name.
  Assuming it's been run through adoc/parse already and contains the resulting keys."
  [{:keys [file html]}]
  (let [output-dir (fs/file "output" (fs/name file))]
    (fs/mkdirs output-dir)
    (spit (str (fs/file output-dir "index.html")) html)))

(defn -main []
  (->> (fs/list-dir "posts")
       (sequence
         (comp
           (map (juxt identity slurp))
           (map #(zipmap [:file :source] %))
           (map #(merge % (adoc/parse (:source %))))))
       (run! spit-post!)))

(comment
  (-main))
