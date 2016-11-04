#!/bin/bash

set -e
set -o pipefail

export LC_ALL=C

trap 'echo ERROR; read a' ERR

cd "$(dirname "$0")"

va="$(grep '^VERSION=' gradle.properties | cut -f 2 -d '=' | head -n 1)"
vb="$(echo "$va + .01" | bc -l)"
vc="$(echo "$vb * 100" | bc -l | cut -f 1 -d '.')"

perl -i -p -e "s/^VERSION=.*\n/VERSION=$vb\n/g" gradle.properties
perl -i -p -e "s/^app.version=.*\n/app.version==$vb\n/g" ios/robovm.properties

perl -i -p -e "s/android:versionCode=\".*?\"/android:versionCode=\"$vc\"/g" android/AndroidManifest.xml
perl -i -p -e "s/android:versionName=\".*?\"/android:versionName=\"$vb\"/g" android/AndroidManifest.xml

echo "---"
echo "$vb"
echo "---"

