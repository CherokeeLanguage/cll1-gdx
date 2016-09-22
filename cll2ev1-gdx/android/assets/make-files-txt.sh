#!/bin/bash

set -e
set -o pipefail

trap 'echo ERROR; read a' ERR

if [ "$1"x = ""x ]; then
	echo "Start up"
	cd "$(dirname "$0")"
	bash "$(pwd)"/"$(basename "$0")" "$(pwd)"
	exit $?
fi

cd "$1"
cwd="$(pwd)"

if [ ! -f .gitignore ]; then touch .gitignore; fi
grep "/0_files.txt" .gitignore > /dev/null 2>&1 || echo "/0_files.txt" >> .gitignore  

echo "... $cwd"

cp /dev/null 0_files.txt
for dir in *; do
	if [ -d "$dir" ]; then bash "$0" "$dir" || exit $?; continue; fi
	dir="$(basename "$dir")"
	if [ "$dir" = "0_files.txt" ]; then continue; fi
	echo "$(basename "$dir")" >> 0_files.txt
done

