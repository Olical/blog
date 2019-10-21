.PHONY: build propel depot test serve watch

output: $(shell find templates src posts -type f)
	clojure -A:build

build: output

propel:
	clojure -A:propel

depot:
	clojure -A:depot

test:
	clojure -A:test

serve:
	python -m http.server -d output

watch:
	find templates src posts -type f | entr bash -c "echo \"(require 'blog.render) (blog.render/render!) :repl/quit\" | netcat localhost $(shell cat .prepl-port)"
