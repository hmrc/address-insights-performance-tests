#!/usr/bin/env bash

SMOKE=${1:-true}
LOCAL=${2:-true}

sbt -Dperftest.runSmokeTest=${SMOKE} -DrunLocal=${LOCAL} gatling:test