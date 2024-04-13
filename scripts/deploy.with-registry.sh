#!/usr/bin/env bash

set -e

export APP_NAME=cf-hoover
export REGISTRY_NAME=hooverRegistry

cf push -f manifest.with-registry.yml --no-start "$1"
cf create-service p.config-server standard $APP_NAME-config -c config/config-server.json
cf create-service p.service-registry standard $REGISTRY_NAME
for (( i = 0; i < 90; i++ )); do
  if [[ $(cf service $APP_NAME-config) != *"succeeded"* ]]; then
    echo "$APP_NAME-config is not ready yet..."
    sleep 10
  else
    break
  fi
done
cf bind-service $APP_NAME $APP_NAME-config
for (( i = 0; i < 90; i++ )); do
  if [[ $(cf service $REGISTRY_NAME) != *"succeeded"* ]]; then
    echo "$REGISTRY_NAME is not ready yet..."
    sleep 10
  else
    break
  fi
done
cf bind-service $APP_NAME $REGISTRY_NAME
cf start $APP_NAME