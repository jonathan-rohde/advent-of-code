#!/bin/zsh

year=$1
day=$2
dayLong=$day
if [ $day -lt 10 ]; then
    dayLong="0$day"
fi

directory="../aoc/years/y$year"

if [ -d $directory ]; then
    echo "Directory already exists"
else
    mkdir -p $directory
fi

cp DayTemplate.kt $directory/Day$dayLong.kt
sed -i '' "s/DayXX/Day$dayLong/g" $directory/Day$dayLong.kt
sed -i '' "s/year = 0/year = $year/g" $directory/Day$dayLong.kt
sed -i '' "s/day = 0/day = $day/g" $directory/Day$dayLong.kt
git add $directory/Day$dayLong.kt
echo "Day $day created"
