#!/bin/bash

set -e
set -o pipefail
trap 'echo ERROR; read a' ERR

cd "$(dirname "$0")"
cd images


for a in */*.svg; do
	if [ -f "$a" ]; then
		touch "$a";
	fi;
done

echo "DONE"
sleep 1
exit 0
