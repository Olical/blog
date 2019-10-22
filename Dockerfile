FROM clojure:tools-deps
WORKDIR /app
COPY deps.edn .
RUN clojure -Spath
COPY src src
COPY templates templates
COPY base base
COPY posts posts 
RUN clojure -A:build

FROM nginx:alpine
WORKDIR /app
COPY server/entrypoint.sh entrypoint.sh
COPY server/default.conf.template /etc/nginx/conf.d/default.conf.template
COPY --from=0 /app/output output
ENTRYPOINT ["/app/entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
