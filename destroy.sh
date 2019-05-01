#!/usr/bin/env bash

set -x

export APP_NAME=cf-hoover

cf app ${APP_NAME} --guid

if [ $? -eq 0 ]; then
	cf stop $APP_NAME
	cf unbind-service $APP_NAME $APP_NAME-config
	cf unbind-service $APP_NAME $APP_NAME-registry
	cf delete-service $APP_NAME-config -f
	cf delete-service $APP_NAME-registry -f
	cf delete $APP_NAME -r -f
else
    echo "$APP_NAME does not exist"
fi
