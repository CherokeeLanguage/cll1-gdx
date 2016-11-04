#!/bin/bash

for size in 32 40 46 52 60 68 78; do
	cat free-serif.hiero | sed 's/font.size=48/font.size='$size'/g' > free-serif-${size}.hiero
done
