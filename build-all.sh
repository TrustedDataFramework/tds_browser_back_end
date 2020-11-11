#!/usr/bin/env bash

CUR=$(dirname $0)

pushd $CUR/eureka-server
mvn clean
mvn package docker:build
popd

pushd $CUR/sync-service
mvn clean
mvn package docker:build
popd

pushd $CUR/tds-web
mvn clean
mvn package docker:build
popd