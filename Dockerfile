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
COPY server/default.conf /etc/nginx/conf.d/default.conf
COPY CHECKS .
COPY --from=0 /app/output output
CMD ["nginx", "-g", "daemon off;"]
