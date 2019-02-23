#!/bin/sh

. ./.env.test_dev
export VCAP_SERVICES=$VCAP_SERVICES

cd skybot-irc
mvn spring-boot:run
cd ..
