#!/bin/bash

set -e
set -o pipefail

export LC_ALL=C

trap 'echo ERROR; read a' ERR

cd "$(dirname "$0")"

if [ "$1"x = x ]; then
	echo "FILE TO MANGLE NOT SPECIFIED!"
	exit -1
fi

if [ ! -f "$1" ]; then
	echo "FILE TO MANGLE DOES NOT EXIST!"
	exit -1
fi

m1="/tmp/$$.mangle1"
m2="/tmp/$$.mangle2"

cp "$1" "$m1"

#no English
perl -i -p -e 's/[A-Za-z][\/A-Za-z\s]*[.?!,:]//g' "$m1"
#break apart into sentences
perl -i -p -e 's/\t\s*/\n/g' "$m1"
#remove speaker indicators
perl -i -p -e 's/\[.*?\]\s*//g' "$m1"
#break apart into sentences
perl -i -p -e 's/:\s*//g' "$m1"
#break apart into sentences
perl -i -p -e 's/\./.\n/g' "$m1"
#break apart into sentences
perl -i -p -e 's/\?/?\n/g' "$m1"
#break apart into sentences
perl -i -p -e 's/!/!\n/g' "$m1"
#remove leading space
perl -i -p -e 's/^\s*//g' "$m1"
#remove empty lines
perl -i -p -e 's/\n+/\n/g' "$m1"
#remove digit marks
perl -i -p -e 's/^\d+\n//g' "$m1"

#random removals based on 4 lead uniq
for x in $(seq 1 100); do
	shuf "$m1" | uniq -w 4 > "$m2"
	mv "$m2" "$m1"
done

#do a final full uniq then a final shuffling
sort -k1.1,1.3 "$m1" | uniq -w 3 > "$m2"
mv "$m2" "$m1"

shuf "$m1" | head -n 10 > "$m2"
mv "$m2" "$m1"

mv "$m1" "$1".mangle

xdg-open "$1".mangle; exit 0

