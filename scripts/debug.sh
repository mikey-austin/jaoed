#!/bin/bash
CONFIG=$1
/bin/env java -Xdebug \
         -Xrunjdwp:server=y,transport=dt_socket,address=5005,suspend=n \
         -jar target/jaoed-1.0-SNAPSHOT-jar-with-dependencies.jar \
         $CONFIG
