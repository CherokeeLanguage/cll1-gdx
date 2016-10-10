#!/bin/bash

set -e
set -o pipefail
trap 'echo ERROR; read a' ERR

cd "$(dirname "$0")"

DPI=90
DPI=45
DPI=35

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
		if [ ! -f "$svg" ]; then continue; fi
		png="pngs/$(echo "$svg"|sed 's/.svg$/.png/')"
		if [ -f "$png" ]; then continue; fi
		echo "=== $png"
		PNGS="${PNGS}\t${png}, "
		inkscape -z -b=white -y=1.0 -C -d="$DPI" -e="$png" "$svg"
		mv "$png" "$png".crush
		pngcrush -s "$png".crush "$png"
		if [ ! -f "$png" ]; then
			cp -v "$png".crush "$png"
		fi
		rm "$png".crush
	done
done

cd "$cwd"
cd audio
bash fix-audio.sh

echo "SYNCING IMAGE ASSETS"
ASSETS_IMAGES="../../../android/assets/card-data/images"
cd "$cwd"/images
for dir in *; do
	if [ "openclipart.org" = "$dir" ]; then continue; fi
	cd "$cwd"/images
	if [ ! -d "$dir" ]; then continue; fi
	if [ ! -d "${ASSETS_IMAGES}/${dir}" ]; then mkdir "${ASSETS_IMAGES}/${dir}"; fi
	cd "${ASSETS_IMAGES}/${dir}"
	adir="$(pwd)"
	cd "$cwd"/images/"$dir"
	for png in "${adir}"/*.png; do
		if [ ! -f "$png" ]; then continue; fi
		bpng="$(basename $png)"
		if [ ! -f "pngs/$bpng" ]; then rm -v "$png"; fi
	done
	for png in "pngs/"*.png; do
		if [ ! -f "$png" ]; then continue; fi
		bpng="$(basename $png)"
		if [ ! -f "${adir}/$bpng" ]; then cp -v "$png" "${adir}/$bpng"; fi
		if [ "${adir}/$bpng" -ot "$png" ]; then cp -v "$png" "${adir}/$bpng"; fi
	done
done

echo "SYNCING AUDIO ASSETS"
ASSETS_AUDIO="../../../android/assets/card-data/audio"
cd "$cwd"/audio
for dir in *; do
	if [ "original" = "$dir" ]; then continue; fi
	cd "$cwd"/audio
	if [ ! -d "$dir" ]; then continue; fi
	if [ ! -d "${ASSETS_AUDIO}/${dir}" ]; then mkdir "${ASSETS_AUDIO}/${dir}"; fi
	cd "${ASSETS_AUDIO}/${dir}"
	adir="$(pwd)"
	cd "$cwd"/audio/"$dir"
	for mp3 in "${adir}"/*.mp3; do
		if [ ! -f "$mp3" ]; then continue; fi
		bmp3="$(basename $mp3)"
		if [ ! -f "$bmp3" ]; then rm -v "$mp3"; fi
	done
	for mp3 in *.mp3; do
		if [ ! -f "$mp3" ]; then continue; fi
		if [ ! -f "${adir}/$mp3" ]; then cp -v "$mp3" "${adir}/$mp3"; fi
		if [ "$mp3" -nt "${adir}/$mp3" ]; then cp -pv "$mp3" "${adir}/$mp3"; fi
	done
done

printf "\nDONE: "
read a
exit 0

