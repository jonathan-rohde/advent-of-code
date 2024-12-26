#!/bin/zsh

year=$1
day=$2
dayLong=$day
if [ $day -lt 10 ]; then
    dayLong="0$day"
fi

directory="../aoc/years/y$year"
resourceDirectory="../../resources/data/y$year"

if ! [ -d $directory ]; then
    mkdir -p $directory
fi

if ! [ -d $resourceDirectory ]; then
    mkdir -p $resourceDirectory
fi

cp DayTemplate.kt $directory/Day$dayLong.kt
sed -i '' "s/DayXX/Day$dayLong/g" $directory/Day$dayLong.kt
sed -i '' "s/year = 0/year = $year/g" $directory/Day$dayLong.kt
sed -i '' "s/day = 0/day = $day/g" $directory/Day$dayLong.kt
git add $directory/Day$dayLong.kt

touch $resourceDirectory/Day$dayLong.txt
touch $resourceDirectory/Day${dayLong}_test.txt


echo "Day $day created"
