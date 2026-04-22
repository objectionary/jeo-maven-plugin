#!/bin/bash
#
# SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
# SPDX-License-Identifier: MIT

set -e

if [ $# -ne 2 ]; then
    echo "Usage: $0 <input_directory> <output_directory>"
    echo "Example: $0 target/generated-sources/jeo-disassemble target/generated-sources/eo-unphi"
    exit 1
fi

INPUT_DIR="$1"
OUTPUT_DIR="$2"

echo "phino xmir->xmir: input=$INPUT_DIR output=$OUTPUT_DIR"

mkdir -p "$OUTPUT_DIR"

find "$INPUT_DIR" -name "*.xmir" | while IFS= read -r input_file; do
    relative="${input_file#${INPUT_DIR}/}"
    output_file="${OUTPUT_DIR}/${relative}"
    mkdir -p "$(dirname "$output_file")"
    echo "Transforming: $input_file -> $output_file"
    phino rewrite --input=xmir --output=xmir "$input_file" -t "$output_file"
done

echo "phino xmir->xmir: done, output in $OUTPUT_DIR"
