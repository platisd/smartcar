#!/bin/bash
# script to save RPi's SSID and IP to two different text files named "ssid" and "ip" (if enabled)
# and start up the camera and the java program to remote control the vehicle

# mySSID=$(iwgetid -r)
# myIP=$(ifconfig wlan0 | grep 'inet addr:' | cut -d: -f2 | awk '{ print $1}')
# echo $mySSID > ssid
# echo $myIP > ip


sudo mkdir /dev/shm/www/
exec  java -Djava.library.path="/home/pi/lib/" -jar /home/pi/socketIO.jar &
sudo raspimjpeg -w 320 -h 240 -d 1 -of /dev/shm/www/pic.jpg &
