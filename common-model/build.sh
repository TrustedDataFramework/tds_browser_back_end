#!/usr/bin/env bash

CUR=$(dirname $0)

mvn clean package

pushd $CUR/target

mvn install:install-file -Dfile=common-model-1.0.0.jar -DgroupId=org.wisdom -DartifactId=common-model -Dversion=1.0.0 -Dpackaging=jar

popd
