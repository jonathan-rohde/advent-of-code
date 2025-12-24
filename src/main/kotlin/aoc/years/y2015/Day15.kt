package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import kotlin.math.max

fun main() {
    Day15().execute().printResults()
}

private val testInput = """
    Butterscotch: capacity -1, durability -2, flavor 6, texture 3, calories 8
    Cinnamon: capacity 2, durability 3, flavor -2, texture -1, calories 3
""".trimIndent()

class Day15 : Day(
    year = 2015,
    day = 15,
    part1 = Part(test = 62842880L, testInput = testInput),
    part2 = Part(test = 57600000, testInput = testInput),
) {

    override fun part1(input: List<String>): Any {
//        val data = input.toIngredients()
//        return (data[0] * 44 + data[1] * 56).score()
        val data = input.toIngredients()
        val cookie = data.highestValueCookie()

        return cookie
    }

    override fun part2(input: List<String>): Any {
        val data = input.toIngredients()
        val cookie = data.highestValueCookie(maxCal = true)

        return cookie
    }
}

private data class Ingredient(
    val capacity: Long,
    val durability: Long,
    val flavor: Long,
    val texture: Long,
    val calories: Long
) {
    infix operator fun times(amount: Int): Ingredient {
        return Ingredient(
            capacity = amount * capacity,
            durability = amount * durability,
            flavor = amount * flavor,
            texture = amount * texture,
            calories = amount * calories
        )
    }

    infix operator fun plus(other: Ingredient): Ingredient {
        return Ingredient(
            capacity = capacity + other.capacity,
            durability = durability + other.durability,
            flavor = flavor + other.flavor,
            texture = texture + other.texture,
            calories = calories + other.calories
        )
    }

    fun score(): Long = max(capacity, 0) * max(durability, 0) * max(flavor, 0) * max(texture, 0)
}

private fun String.toIngredient(): Ingredient {
    val data = split(": |, ".toRegex())
    return Ingredient(
        data[1].drop("capacity ".length).toLong(),
        data[2].drop("durability ".length).toLong(),
        data[3].drop("flavor ".length).toLong(),
        data[4].drop("texture ".length).toLong(),
        data[5].drop("calories ".length).toLong()
    )
}

private fun List<String>.toIngredients(): List<Ingredient> {
    return map { it.toIngredient() }
}

private val smallest = Ingredient(
    Int.MIN_VALUE.toLong(),
    Int.MIN_VALUE.toLong(),
    Int.MIN_VALUE.toLong(),
    Int.MIN_VALUE.toLong(),
    Int.MIN_VALUE.toLong()
)

private fun List<Ingredient>.highestValueCookie(maxCal: Boolean = false): Long {
    var max = 0L
    var maxCalories = 0L
    if (size == 2) {
        (1..99).forEach { i1 ->
            (1..99).forEach { i2 ->
                val ingredient = this[0] * i1 + this[1] * i2
                if (!maxCal || ingredient.calories == 500L) {
                    if (i1 + i2 == 100) {
                        val score = ingredient.score()
                        if (score > max) {
                            max = score
                        }
                    }
                }
            }
        }
    }
    if (size == 4) {
        (1..99).forEach { i1 ->
            (1..99).forEach { i2 ->
                (1..99).forEach { i3 ->
                    (1..99).forEach { i4 ->
                        val ingredient = this[0] * i1 + this[1] * i2 + this[2] * i3 + this[3] * i4
                        if (!maxCal || ingredient.calories == 500L) {
                            if (i1 + i2 + i3 + i4 == 100) {
                                val score = ingredient.score()
                                if (score > max) {
                                    max = score
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    return max
}