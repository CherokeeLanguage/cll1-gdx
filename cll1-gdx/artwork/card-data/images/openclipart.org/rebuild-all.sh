#!/bin/bash

set -e
set -o pipefail
trap 'echo ERROR; read a' ERR

cd "$(dirname "$0")"

cwd="$(pwd)"

for dir in *; do
	cd "$cwd"
	if [ ! -d "$dir" ]; then continue; fi
	if [ "openclipart.org" = "$dir" ]; then continue; fi
	cd "$dir"
	echo "/pngs/" >> .gitignore
	mv .gitignore .gitignore.tmp
	cat .gitignore.tmp | sort | uniq > .gitignore
	rm .gitignore.tmp
	rm -rf pngs 2> /dev/null || true
	mkdir pngs || true
	for svg in *.svg; do
		if [ ! -f "$svg" ]; then continue; fi
		png="$(echo "$svg"|sed 's/.svg$/.png/')"
		inkscape -z -b=white -y=1.0 -C -d=45 -e="pngs/$png" "$svg"
	done
done

exit 0

