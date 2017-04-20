#!/bin/bash

cp /dev/null files.txt
echo -n 0 > counter.txt
for x in ???; do
	c="$(cat counter.txt)"
	if [ $c -gt 15 ]; then continue; fi
	echo "$x"
	find "$x" -name '*.svg' | grep -v _backup.svg | while read f; do
		g="$(echo "$f"| sed 's/.svg/_backup.svg/g')"
       	if [ ! -f "$g" ]; then
       		c="$(cat counter.txt)"
	       	if [ $c -gt 15 ]; then continue; fi
			echo "$c: $f";
			inkscape "$f" --verb=FileSave --verb=FileQuit &
			echo -n "$(($c+1))" > counter.txt
       	fi
	done
done
