#!/usr/bin/env bash
# SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
# SPDX-License-Identifier: MIT
while true; do
    echo "Running integration tests"
    java --version
    if ! mvn clean install -DskipTests; then
      break
    fi
done
