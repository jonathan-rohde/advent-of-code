package aoc.years.y2015

import aoc.common.Day
import aoc.common.Part
import aoc.common.printResults
import kotlin.math.max

fun main() {
    Day22().execute().printResults()
}

private val testInput = """
    Hit Points: 12
    Damage: 7
""".trimIndent()

class Day22 : Day(
    year = 2015,
    day = 22,
    part1 = Part(test = null, testInput = testInput),
    part2 = Part(test = null, testInput = testInput),
) {
    override fun part1(input: List<String>): Any {
        val hero = Hero(hitpoints = 50, mana = 500)
        val boss = input.parseBoss()

        return (executePlayerMove(hero, boss) ?: -1).also {
            executePlayerCache.clear()
            executeBossCache.clear()
        }
    }

    override fun part2(input: List<String>): Any {
        val hero = Hero(hitpoints = 50, mana = 500)
        val boss = input.parseBoss()

        return (executePlayerMove(hero, boss, hardmode = true) ?: -1).also {
            executePlayerCache.clear()
            executeBossCache.clear()
        }
    }
}

private fun List<String>.parseBoss(): Boss {
    return Boss(
        hitponts = this[0].takeLastWhile { it.isDigit() }.toInt(),
        damage = this[1].takeLastWhile { it.isDigit() }.toInt()
    )
}

private val executePlayerCache: MutableMap<Pair<Hero, Boss>, Int?> = mutableMapOf()
private fun executePlayerMove(hero: Hero, boss: Boss, hardmode: Boolean = false): Int? {
    return executePlayerCache.getOrPut(hero to boss) {
        var hero = if (hardmode) hero.copy(hitpoints = hero.hitpoints - 1) else hero
        if (boss.hitponts <= 0) {
            return@getOrPut 0
        }
        if (hero.hitpoints <= 0) return@getOrPut null

        val boss = boss.handleTick()
        if (boss.hitponts <= 0) {
            return@getOrPut 0
        }
        hero = hero.handleTick()
        if (hero.hitpoints <= 0) return@getOrPut null

        val magicMissleCast = if (hero.mana >= 53) {
            executeBossMove(
                hero.copy(mana = hero.mana - 53),
                boss.copy(hitponts = boss.hitponts - 4),
                hardmode
            )?.plus(53)
        } else null

        val drainCast = if (hero.mana >= 73) {
            executeBossMove(
                hero.copy(hitpoints = hero.hitpoints + 2, mana = hero.mana - 73),
                boss.copy(hitponts = boss.hitponts - 2),
                hardmode
            )?.plus(73)
        } else null

        val shieldCast = if (hero.mana >= 113 && hero.shieldTimer == 0) {
            executeBossMove(
                hero.copy(shieldTimer = 6, mana = hero.mana - 113),
                boss.copy(),
                hardmode
            )?.plus(113)
        } else null

        val poisonCast = if (hero.mana >= 173 && boss.poisonTimer == 0) {
            executeBossMove(
                hero.copy(mana = hero.mana - 173),
                boss.copy(poisonTimer = 6),
                hardmode
            )?.plus(173)
        } else null

        val rechargeCast = if (hero.mana >= 229 && hero.rechargeTimer == 0) {
            executeBossMove(
                hero.copy(rechargeTimer = 5, mana = hero.mana - 229),
                boss.copy(),
                hardmode
            )?.plus(229)
        } else null

        val rec = listOfNotNull(magicMissleCast, drainCast, shieldCast, poisonCast, rechargeCast)
            .ifEmpty { return@getOrPut null }
       return@getOrPut rec.min()
    }

}

private val executeBossCache: MutableMap<Pair<Hero, Boss>, Int?> = mutableMapOf()
private fun executeBossMove(hero: Hero, boss: Boss, hardmode: Boolean): Int? {
    return executeBossCache.getOrPut(hero to boss) {
        if (boss.hitponts <= 0) {
            return@getOrPut 0
        if (hero.hitpoints <= 0) return@getOrPut null
        }

        val hero = hero.handleTick()
        if (hero.hitpoints <= 0) return@getOrPut null
        val boss = boss.handleTick()
        if (boss.hitponts <= 0) {
            return@getOrPut 0
        }

        return@getOrPut executePlayerMove(hero.hit(boss.damage), boss.copy(), hardmode)
    }
}

private data class Hero(
    val hitpoints: Int,
    val armor: Int = 0,
    val mana: Int,

    val shieldTimer: Int = 0,
    val rechargeTimer: Int = 0
) {
    fun handleTick(): Hero {
        return Hero(
            hitpoints = hitpoints,
            armor = 7.takeIf { shieldTimer > 0 } ?: 0,
            shieldTimer = max(shieldTimer - 1, 0),
            mana = mana + (101.takeIf { rechargeTimer > 0 } ?: 0),
            rechargeTimer = max(rechargeTimer - 1, 0)
        )
    }

    fun hit(damage: Int): Hero {
        return copy(
            hitpoints = hitpoints - max(damage - armor, 1)
        )
    }
}

private data class Boss(
    val hitponts: Int,
    val damage: Int,
    val poisonTimer: Int = 0
) {
    fun handleTick(): Boss {
        return copy(
            hitponts = hitponts - (3.takeIf { poisonTimer > 0} ?: 0),
            poisonTimer = max(poisonTimer - 1, 0)
        )
    }
}
