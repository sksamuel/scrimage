#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" == 'master' ]; then
  sbt +publish
fi