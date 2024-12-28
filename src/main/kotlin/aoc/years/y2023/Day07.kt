package template

import aoc.common.Day
import aoc.common.printResults

class Day07 : Day(year = 2023, day = 7, test = 6440L to 5905L) {
    override fun part1(input: List<String>): Any {
        return input.parseGame()
            .sortedWith(PokerHandComparator().reversed())
            .mapIndexed { index, pair ->
                (index + 1) * pair.second
            }.sum()
    }

    override fun part2(input: List<String>): Any {
        val hands = input.parseGame()
            .map { Triple(it, it.getType(), it.getType().elevate(it.first)) }
            .sortedWith(PokerHandComparator2().reversed())
        return hands.mapIndexed { index, pair ->
            (index + 1) * pair.first.second
        }.sum()
    }
}

fun main() {
    Day07().execute().printResults()
}

private fun List<String>.parseGame(): List<PokerHand> {
    return map {
        it.parseHand()
    }
}

private fun String.parseHand(): PokerHand {
    val cards = this.split(" ")
    val value = cards[0]
    val suit = cards[1]
    return value to suit.toLong()
}

private typealias PokerHand = Pair<String, Long>

private enum class PokerHandType {
    FIVE_KIND,
    FOUR_KIND,
    FULL_HOUSE,
    THREE_KIND,
    TWO_PAIR,
    ONE_PAIR,
    HIGH_CARD
}

private fun PokerHand.getType(): PokerHandType {
    val cards = first.toCharArray()
        .groupBy { it.toString() }
    if (cards.any { it.value.size == 5 }) {
        return PokerHandType.FIVE_KIND
    }
    if (cards.any { it.value.size == 4 }) {
        return PokerHandType.FOUR_KIND
    }
    if (cards.any { it.value.size == 3 } && cards.any { it.value.size == 2 }) {
        return PokerHandType.FULL_HOUSE
    }
    if (cards.any { it.value.size == 3 }) {
        return PokerHandType.THREE_KIND
    }
    val cardsWithTwo = cards.filter { it.value.size == 2 }
    if (cardsWithTwo.size == 2) {
        return PokerHandType.TWO_PAIR
    }
    if (cardsWithTwo.size == 1) {
        return PokerHandType.ONE_PAIR
    }
    return PokerHandType.HIGH_CARD
}

//private fun PokerHand.getElevatedType(): PokerHandType {
//    val cardsCount = first.toCharArray()
//        .groupBy { it.toString() }
//    val jokerCounts = cardsCount["J"]?.size ?: 0
//
//    if (jokerCounts == 0) {
//        return getType()
//    } else {
//        if (jokerCounts == 5) {
//            return PokerHandType.FIVE_KIND
//        }
//        val cardsCountWithoutJoker = cardsCount.filter { it.key != "J" }
//        val cardWithMost = cardsCountWithoutJoker.maxByOrNull { it.value.size }!!
//        val alternativeHand = PokerHand(
//            first = first.replace("J", cardWithMost.key),
//            second = second
//        )
//        return alternativeHand.getType()
//    }
//}

private fun PokerHandType.elevate(cards: String): PokerHandType {
    val cardsCount = cards.toCharArray()
        .groupBy { it.toString() }
    val jokerCounts = cardsCount["J"]?.size ?: 0
    if (this == PokerHandType.FIVE_KIND) {
        return PokerHandType.FIVE_KIND
    }
    if (this == PokerHandType.FOUR_KIND) {
        return if (jokerCounts == 1) {
            PokerHandType.FIVE_KIND
        } else if (jokerCounts == 4) {
            PokerHandType.FIVE_KIND
        } else {
            PokerHandType.FOUR_KIND
        }
    }
    if (this == PokerHandType.FULL_HOUSE) {
        return if (jokerCounts == 2 || jokerCounts == 3) {
            PokerHandType.FIVE_KIND
        } else {
            PokerHandType.FULL_HOUSE
        }
    }
    if (this == PokerHandType.THREE_KIND) {
        return when (jokerCounts) {
            1 -> PokerHandType.FOUR_KIND
            2 -> PokerHandType.FIVE_KIND
            3 -> {
                val remaining = cardsCount.filter { it.key == "J" }
                if (remaining.any { it.value.size == 2 }) {
                    PokerHandType.FULL_HOUSE
                } else {
                    PokerHandType.FOUR_KIND
                }
            }

            else -> PokerHandType.THREE_KIND
        }
    }
    if (this == PokerHandType.TWO_PAIR) {
        return when (jokerCounts) {
            1 -> PokerHandType.FULL_HOUSE
            2 -> PokerHandType.FOUR_KIND
            else -> PokerHandType.TWO_PAIR
        }
    }
    if (this == PokerHandType.ONE_PAIR) {
        return when (jokerCounts) {
            1 -> PokerHandType.THREE_KIND
            2 -> PokerHandType.THREE_KIND
            else -> PokerHandType.ONE_PAIR
        }
    }
    return if (jokerCounts == 1) {
        PokerHandType.ONE_PAIR
    } else {
        PokerHandType.HIGH_CARD
    }

}

private const val cardOrder = "AKQJT98765432"
private const val cardOrder2 = "AKQT98765432J"

private abstract class BasePokerHandComparator : Comparator<PokerHand> {

    abstract fun getType(hand: PokerHand): PokerHandType
    abstract fun cardOrder(): String

    override fun compare(left: PokerHand, right: PokerHand): Int {
        val leftType = getType(left)
        val rightType = getType(right)
        if (leftType != rightType) {
            return leftType.compareTo(rightType)
        }
        val comparisonCards = left.first.toCharArray()
            .zip(right.first.toCharArray())
        return comparisonCards.dropWhile { it.first == it.second }
            .firstOrNull()?.let {
                cardOrder().indexOf(it.first).compareTo(cardOrder().indexOf(it.second))
            } ?: 0
    }
}

private class PokerHandComparator : BasePokerHandComparator() {
    override fun getType(hand: PokerHand): PokerHandType = hand.getType()

    override fun cardOrder(): String = cardOrder
}

private class PokerHandComparator2 : Comparator<Triple<PokerHand, PokerHandType, PokerHandType>> {

    override fun compare(
        left: Triple<PokerHand, PokerHandType, PokerHandType>?,
        right: Triple<PokerHand, PokerHandType, PokerHandType>?
    ): Int {
        val leftType = left!!.third
        val rightType = right!!.third
        if (leftType != rightType) {
            return leftType.compareTo(rightType)
        }
        val comparisonCards = left.first.first.toCharArray()
            .zip(right.first.first.toCharArray())
        return comparisonCards.dropWhile { it.first == it.second }
            .firstOrNull()?.let {
                cardOrder2.indexOf(it.first).compareTo(cardOrder2.indexOf(it.second))
            } ?: error("No difference found")
    }
}
