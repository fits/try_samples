@echo off

echo ŠJn - %time%

gm convert -define jpeg:size=100 -thumbnail 100 sample.jpg sample_4.jpg

echo I—¹ - %time%