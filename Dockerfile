FROM clojure:tools-deps
WORKDIR /blog
COPY deps.edn .
RUN clojure -Spath
COPY src src
COPY templates templates
COPY base base
COPY posts posts 
RUN clojure -A:build

FROM nginx:alpine
COPY nginx/entrypoint.sh nginx/entrypoint.sh
COPY nginx/default.conf.template /etc/nginx/conf.d/default.conf.template
COPY --from=0 /blog/output /usr/share/nginx/html
ENTRYPOINT ["/nginx/entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
