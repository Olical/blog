(ns blog.main
  "CLI handling."
  (:require [blog.render :as render]))

(defn -main
  "Entry point for the CLI, simply executes blog.render/render!"
  []
  (render/render!)
  (shutdown-agents))
