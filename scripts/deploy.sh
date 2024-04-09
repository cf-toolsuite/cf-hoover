#!/usr/bin/env bash

set -e

export APP_NAME=cf-hoover

cf push --no-start
cf create-service p.config-server standard $APP_NAME-config -c config/config-server.json
while [[ $(cf service $APP_NAME-config) != *"succeeded"* ]]; do
  echo "$APP_NAME-config is not ready yet..."
  sleep 5
done
cf bind-service $APP_NAME $APP_NAME-config
cf start $APP_NAME