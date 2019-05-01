#!/usr/bin/env bash

set -e

export APP_NAME=cf-hoover

cf push --no-start
cf create-service p-config-server standard $APP_NAME-config -c config/config-server.json
cf create-service p-service-registry standard $APP_NAME-registry
cf bind-service $APP_NAME $APP_NAME-config
cf bind-service $APP_NAME $APP_NAME-registry
cf start $APP_NAME