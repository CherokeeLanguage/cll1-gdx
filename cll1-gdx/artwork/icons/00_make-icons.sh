#!/bin/bash

export FILTER=Lanczos

set -e
set -o pipefail

trap 'echo ERROR; read a' ERR

cd "$(dirname "$0")"

rm icon.png 2> /dev/null || true
inkscape -z -d 300 -C --export-background=white --export-background-opacity=1 -e=icon.png icon.svg

mkdir apkcons 2> /dev/null || true

gm convert -filter ${FILTER} icon.png -resize 48x48 apkcons/ic_launcher-mdpi.png
gm convert -filter ${FILTER} icon.png -resize 72x72 apkcons/ic_launcher-hdpi.png
gm convert -filter ${FILTER} icon.png -resize 96x96 apkcons/ic_launcher-xhdpi.png
gm convert -filter ${FILTER} icon.png -resize 144x144 apkcons/ic_launcher-xxhdpi.png
gm convert -filter ${FILTER} icon.png -resize 192x192 apkcons/ic_launcher-xxxhdpi.png
gm convert -filter ${FILTER} icon.png -resize 512x512 hi-res-icon-google-play.png

for size in mdpi hdpi xhdpi xxhdpi xxxhdpi; do
	mkdir ../../android/res/drawable-"${size}" 2> /dev/null || true
	cp -v apkcons/ic_launcher-"${size}".png ../../android/res/drawable-"${size}"/ic_launcher.png
done

mkdir deskcons 2> /dev/null || true 

gm convert -filter ${FILTER} icon.png -resize 128x128 deskcons/icon-128.png
gm convert -filter ${FILTER} icon.png -resize 64x64 deskcons/icon-64.png
gm convert -filter ${FILTER} icon.png -resize 32x32 deskcons/icon-32.png
gm convert -filter ${FILTER} icon.png -resize 16x16 deskcons/icon-16.png

cp -v deskcons/*.png ../../android/assets/icons/

mkdir applecons 2> /dev/null || true 

gm convert -filter ${FILTER} icon.png -resize 72x72 applecons/Icon-72.png
gm convert -filter ${FILTER} icon.png -resize 144x144 applecons/Icon-72@2x.png
gm convert -filter ${FILTER} icon.png -resize 57x57 applecons/Icon.png
gm convert -filter ${FILTER} icon.png -resize 114x114 applecons/Icon@2x.png

gm convert -filter ${FILTER} icon.png -resize 76x76 applecons/Icon-76.png
gm convert -filter ${FILTER} icon.png -resize 152x152 applecons/Icon-76@2x.png

gm convert -filter ${FILTER} icon.png -resize 120x120 applecons/Icon-120.png
gm convert -filter ${FILTER} icon.png -resize 240x240 applecons/Icon-120@2x.png

gm convert -filter ${FILTER} icon.png -resize 152x152 applecons/Icon-152.png
gm convert -filter ${FILTER} icon.png -resize 304x304 applecons/Icon-152@2x.png

cp -v applecons/*.png ../../ios/data/


