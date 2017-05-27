#!/bin/bash

set -e
set -o pipefail

trap 'echo ERROR; read a' ERR

cp /dev/null files.txt
echo -n 0 > counter.txt

find . -name '*.svg' | grep -v _backup.svg | while read f; do


   		c="$(cat counter.txt)"
		echo "$c: $f";
		#inkscape "$f" --verb=FileSave --verb=FileQuit &
		inkscape "$f" --verb=ZoomPage 1> /dev/null 2>&1 &
		echo -n "$(($c+1))" > counter.txt
       	if [ $c -gt 5 ]; then echo "==="; echo -n 0 > counter.txt; wait; fi

done

echo "OK"
