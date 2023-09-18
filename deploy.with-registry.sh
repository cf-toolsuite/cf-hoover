#!/usr/bin/env bash

set -e

export APP_NAME=cf-hoover
export REGISTRY_NAME=hooverRegistry

cf push -f manifest.with-registry.yml --no-start
cf create-service p.config-server standard $APP_NAME-config -c config/config-server.json
cf create-service p.service-registry standard $REGISTRY_NAME
while [[ $(cf service $APP_NAME-config) != *"succeeded"* ]]; do
  echo "$APP_NAME-config is not ready yet..."
  sleep 5s
done
cf bind-service $APP_NAME $APP_NAME-config
while [[ $(cf service $REGISTRY_NAME) != *"succeeded"* ]]; do
  echo "$REGISTRY_NAME is not ready yet..."
  sleep 5s
done
cf bind-service $APP_NAME $REGISTRY_NAME
cf start $APP_NAME