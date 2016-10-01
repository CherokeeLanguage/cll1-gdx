#!/bin/bash

set -e
set -o pipefail
trap 'echo ERROR; read a' ERR

cd "$(dirname "$0")"

cwd="$(pwd)"
PNGS=""
cd "images"
for dir in *; do
	cd "$cwd"/images
	if [ ! -d "$dir" ]; then continue; fi
	if [ "openclipart.org" = "$dir" ]; then continue; fi
	cd "$dir"
	echo "/pngs/" >> .gitignore
	mv .gitignore .gitignore.tmp
	cat .gitignore.tmp | sort | uniq > .gitignore
	rm .gitignore.tmp
	if [ ! -d "pngs" ]; then mkdir "pngs"; fi
	for png in pngs/*.png; do
		if [ ! -f "$png" ]; then continue; fi
		svg="$(basename "$png"|sed 's/.png$/.svg/')"
		if [ ! -f "$svg" ]; then rm "$png"; fi
		if [ "$png" -ot "$svg" ]; then rm "$png"; fi
	done
	for svg in *.svg; do
		png="pngs/$(echo "$svg"|sed 's/.svg$/.png/')"
		if [ -f "$png" ]; then continue; fi
		echo "=== $png"
		PNGS="${PNGS}\t${png}, "
		inkscape -z -b=white -y=1.0 -C -d=45 -e="$png" "$svg"
	done
done

cd "$cwd"
cd audio
bash fix-audio.sh

printf "NEW/MODIFIED PIX: ${PNGS}\n"

printf "DONE: "
read a
exit 0

