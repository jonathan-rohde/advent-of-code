#!/bin/zsh
set -e

if [ $# -ne 2 ]; then
    echo "Usage: $0 <year> <day>"
    exit 1
fi

year=$1
day=$2
dayLong=$(printf "%02d" $day)

scriptDir=${0:A:h}
directory="$scriptDir/../aoc/years/y$year"
dayFile="$directory/Day$dayLong.kt"
yearFile="$directory/Year$year.kt"

if [ -e "$dayFile" ]; then
    echo "$dayFile already exists"
    exit 1
fi

mkdir -p "$directory"
sed -e "s/DayXX/Day$dayLong/g" \
    -e "s/year = 0/year = $year/" \
    -e "s/day = 0/day = $day/" \
    -e "s/package template/package aoc.years.y$year/" \
    "$scriptDir/DayTemplate.kt" > "$dayFile"
git add "$dayFile"

if [ -f "$yearFile" ] && grep -qE "^ *// *Day$dayLong\(\)" "$yearFile"; then
    sed -i '' -E "s|^( *)//( *)Day$dayLong\(\)|\1\2Day$dayLong()|" "$yearFile"
    echo "Enabled Day$dayLong() in Year$year.kt"
elif ! grep -q "Day$dayLong()" "$yearFile" 2>/dev/null; then
    echo "Add Day$dayLong() to Year$year.kt to run it with the year runner"
fi

echo "Day $day created"
