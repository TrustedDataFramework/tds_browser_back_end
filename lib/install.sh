#!/usr/bin/env bash

CUR=$(dirname $0)

pushd $CUR

mvn install:install-file -Dfile=common-1.0.4.jar -DgroupId=org.tdf -DartifactId=common -Dversion=1.0.4 -Dpackaging=jar

mvn install:install-file -Dfile=crypto-1.0.4.jar -DgroupId=org.tdf -DartifactId=crypto -Dversion=1.0.4 -Dpackaging=jar

mvn install:install-file -Dfile=facade-1.0.4.jar -DgroupId=org.tdf -DartifactId=facade -Dversion=1.0.4 -Dpackaging=jar

mvn install:install-file -Dfile=rlp-1.1.20.jar -DgroupId=org.tdf -DartifactId=rlp -Dversion=1.1.20 -Dpackaging=jar

popd
