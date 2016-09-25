#!/bin/bash

cd "$(dirname "$0")"

length="0:2:00"

wmctrl -c "CLL2EV1 - Cherokee Language Lessons 2nd Edition Volume 1"
id="$(wmctrl -l | grep Cherokee | grep Session | cut -f 1 -d ' ' | head -n 1)"
if [ "$id"x != x ]; then wmctrl -i -c "$id"; fi

OUT="CLL2EV1 - Cherokee Language Lessons 2nd Edition Volume 1-2016-09-24.mkv"
if [ -f "$OUT" ]; then rm "$OUT"; fi

(
	cd ~/git/cll2ev1-gdx/cll2ev1-gdx
	gradle desktop:run
) &

sleep 1
count=10
nextwindow=0
while [ "$(wmctrl -l | grep 'CLL2EV1 - Cherokee Language Lessons 2nd Edition Volume 1')"x = x ]; do
	sleep 1;
	count=$(($count-1))
	if [ "$count" = "0" ]; then
		nextwindow=1
		break
	fi
done
id="$(wmctrl -l | grep 'CLL2EV1 - Cherokee Language Lessons 2nd Edition Volume 1' | cut -f 1 -d ' ' | head -n 1)"
wmctrl -i -r "$id" -e 0,2560,0,1278,718

#pactl list sources|less
AUDIO1="alsa_input.pci-0000_00_1b.0.analog-stereo"
AUDIO2="alsa_output.pci-0000_00_1b.0.analog-stereo.monitor"
RESYNC1="aresample=async=1:min_hard_comp=0.100000:first_pts=0"
RESYNC2="aresample=async=10000"
pacmd set-default-source "${AUDIO2}"

#https://launchpad.net/~jon-severinsson/+archive/ffmpeg

ffmpeg -f alsa -ac 2 -i pulse -f x11grab -acodec pcm_s16le -r 30 -s 1280x720 -i "${DISPLAY}+2560x0" -vcodec libx264 -preset ultrafast -threads 0 -af "$RESYNC2" -t "$length" "$OUT"

wmctrl -i -c "$id"
id="$(wmctrl -l | grep 'CLL2EV1 - Cherokee Language Lessons 2nd Edition Volume 1' | cut -f 1 -d ' ' | head -n 1)"
if [ "$id"x != x ]; then wmctrl -i -c "$id"; fi

done; done

