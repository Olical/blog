(ns blog.adoc
  "Wrapper around the AsciidoctorJ Java library."
  (:import [org.asciidoctor Asciidoctor$Factory]))

;; JUXT's wrapper is a good source of information on this.
;; https://github.com/juxt/adoc/blob/master/src/juxt/adoc/core.clj

(defonce adoc
  (Asciidoctor$Factory/create))

(defn parse
  "Convert the given source from AsciiDoc to the title string and HTML
  body."
  [source]
  (when source
    (let [header (.readDocumentHeader adoc source)]
      (merge {:html (.convert adoc source {})}
             (when header
               (let [title (.getDocumentTitle header)
                     author (.getAuthor header)
                     revision (.getRevisionInfo header)]
                 (merge {}
                        (when-let [combined (some-> title .getCombined)]
                          {:title combined})
                        (when-let [full-name (some-> author .getFullName)]
                          {:author full-name})
                        (when-let [date (some-> revision .getDate)]
                          {:date date}))))))))
