#!/usr/bin/env bash
set -euo pipefail

if [ $# -ne 2 ]; then
    echo "Usage: $0 <year> <day>"
    exit 1
fi

year=$1
day=$2
dayLong=$(printf "%02d" "$((10#$day))")

scriptDir=$(cd "$(dirname "$0")" && pwd)
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
    # write via temp file instead of `sed -i` (GNU and BSD sed disagree on its syntax)
    tmpFile=$(mktemp)
    sed -E "s|^( *)//( *)Day$dayLong\(\)|\1\2Day$dayLong()|" "$yearFile" > "$tmpFile"
    cat "$tmpFile" > "$yearFile"
    rm -f "$tmpFile"
    echo "Enabled Day$dayLong() in Year$year.kt"
elif ! grep -q "Day$dayLong()" "$yearFile" 2>/dev/null; then
    echo "Add Day$dayLong() to Year$year.kt to run it with the year runner"
fi

echo "Day $day created"
