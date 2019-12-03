#!/usr/bin/env sh

docker build -t vertx-kotlin-music/postgres .
docker run --network host -it vertx-kotlin-music/postgres
