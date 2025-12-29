package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import kotlin.math.max

fun main() {
    Day21().execute().printResults()
}

private val testInput = """
    Hit Points: 12
    Damage: 7
    Armor: 2
""".trimIndent()

class Day21 : Day(
    year = 2015,
    day = 21,
    part1 = Part(test = null, testInput = testInput),
    part2 = Part(test = null, testInput = testInput),
) {

    override fun part1(input: List<String>): Any {
        val boss = input.toBoss()

        val heroes = generateHeroes()
        val winners = heroes.filter {
            executeGame(it.first, boss.copy())
        }
        val cheap = winners.minOf { it.second }

        return cheap
    }

    override fun part2(input: List<String>): Any {
        val boss = input.toBoss()

        val heroes = generateHeroes()
        val losers = heroes.filter {
            !executeGame(it.first, boss.copy())
        }
        if (losers.isEmpty()) return -1
        val expensive = losers.maxOf { it.second }

        return expensive
    }
}

private fun generateHeroes(): List<Pair<Participant, Int>> {
    val result = mutableListOf<Pair<Participant, Int>>()
    weapons().forEach { weapon ->
        result.add(hero(weapon.damage) to weapon.cost)
        armors().forEach { armor ->
            result.add(
                hero(
                    damage = weapon.damage,
                    armor = armor.armor
                ) to weapon.cost + armor.cost
            )
            rings().forEach { ring ->
                result.add(
                    hero(
                        damage = weapon.damage + ring.damage,
                        armor = armor.armor + ring.armor
                    ) to weapon.cost + armor.cost + ring.cost
                )
            }
            doubleRings().forEach { ring ->
                result.add(
                    hero(
                        damage = weapon.damage + ring.damage,
                        armor = armor.armor + ring.armor
                    ) to weapon.cost + armor.cost + ring.cost
                )
            }
        }
        rings().forEach { ring ->
            result.add(
                hero(
                    damage = weapon.damage + ring.damage,
                    armor = ring.armor
                ) to weapon.cost + ring.cost
            )
        }
        doubleRings().forEach { ring ->
            result.add(
                hero(
                    damage = weapon.damage + ring.damage,
                    armor = ring.armor
                ) to weapon.cost + ring.cost
            )
        }

    }
    return result
}

private fun executeGame(hero: Participant, boss: Participant): Boolean {
    while (true) {
        boss.getHit(hero.damage)
        if (!boss.alive) return true
        hero.getHit(boss.damage)
        if (!hero.alive) return false
    }
}

private fun hero(damage: Int, armor: Int = 0): Participant {
    return Participant(100, damage, armor)
}

private fun List<String>.toBoss(): Participant {
    return Participant(
        hitpoint = this[0].takeLastWhile { it.isDigit() }.toInt(),
        damage = this[1].takeLastWhile { it.isDigit() }.toInt(),
        armor = this[2].takeLastWhile { it.isDigit() }.toInt()
    )
}

private data class Weapon(val damage: Int, val cost: Int)
private fun weapons(): List<Weapon> {
    return listOf(
        Weapon(4, 8),
        Weapon(5, 10),
        Weapon(6, 25),
        Weapon(7, 40),
        Weapon(8, 74)
    )
}

private data class Armor(val armor: Int, val cost: Int)
private fun armors(): List<Armor> {
    return listOf(
        Armor(1, 13),
        Armor(2, 31),
        Armor(3, 53),
        Armor(4, 75),
        Armor(5, 102)
    )
}

private data class Ring(val damage: Int, val armor: Int, val cost: Int)
private fun rings(): List<Ring> {
    return listOf(
        Ring(1, 0, 25),
        Ring(2, 0, 50),
        Ring(3, 0, 100),
        Ring(0, 1, 20),
        Ring(0, 2, 40),
        Ring(0, 3, 80)
    )
}

private fun doubleRings(): List<Ring> {
    val result = mutableSetOf<Ring>()
    rings().forEach { ring1 ->
        rings().filter { it != ring1 }.forEach { ring2 ->
            result.add(Ring(
                damage = ring1.damage + ring2.damage,
                armor = ring1.armor + ring2.armor,
                cost = ring1.cost + ring2.cost
            ))
        }
    }

    return result.toList()
}

private data class Participant(
    var hitpoint: Int, val damage: Int, val armor: Int
) {
    val alive: Boolean get() = hitpoint > 0
    fun getHit(damage: Int) {
        hitpoint -= max(damage - armor, 1)
    }
}
