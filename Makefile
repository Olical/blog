.PHONY: build propel depot test serve watch build-container run-container setup-deploy deploy

output: $(shell find templates src posts -type f)
	clojure -A:build

build: output

propel:
	clj -A:propel

depot:
	clojure -A:depot

test:
	clojure -A:test

serve:
	python3 -m http.server -d output

watch:
	find templates src posts -type f | entr bash -c "echo \"(require 'blog.render) (blog.render/render!) :repl/quit\" | netcat localhost $(shell cat .prepl-port)"

build-container:
	docker build -t blog .

run-container:
	docker run --rm -e PORT=9898 -p 9898:9898 blog

setup-deploy:
	git remote add dokku dokku@host.oli.me.uk:oli.me.uk

deploy:
	git push dokku master
