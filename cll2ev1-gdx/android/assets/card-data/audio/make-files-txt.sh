#!/bin/bash

set -e
set -o pipefail

trap 'echo ERROR; read a' ERR

cd "$(dirname "$0")"
cwd="$(pwd)"

ls -1 | grep -v '.sh$' |grep -v '0_files.txt' > 0_files.txt
for dir in *; do
	cd "$cwd"
	if [ ! -d "$dir" ]; then continue; fi
	cd "$dir"
	ls -1 | grep -v '.sh$' |grep -v '0_files.txt' > 0_files.txt
done

cd "$cwd"


