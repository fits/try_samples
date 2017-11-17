#!/bin/sh

ua="Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.79 Safari/535.11"

dest=$1
year=$2

for i in {1..52}; do
	csv=$(printf "${year}-%02d-teiten.csv" $i)
	url=$(printf "https://www.niid.go.jp/niid/images/idwr/sokuho/idwr-${year}/${year}%02d/$csv" $i)

	curl -H "User-Agent: $ua" -o "$dest/$csv" $url
done
