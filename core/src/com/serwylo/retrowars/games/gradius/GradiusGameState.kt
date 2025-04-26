package com.serwylo.retrowars.games.gradius

import com.badlogic.gdx.math.Vector2
import com.serwylo.retrowars.games.gradius.entities.Bullet
import com.serwylo.retrowars.games.gradius.entities.Enemy
import com.serwylo.retrowars.games.gradius.entities.Item
import com.serwylo.retrowars.games.gradius.entities.Ship
import java.util.LinkedList
import java.util.Queue

data class EnemyType(
    val type: String,
    val probability: Float,
    val isSwarm: Boolean,
)

data class LevelZone(
    val enemyProbabilityMap: List<EnemyType>,
)

class GradiusGameState(worldWidth: Float, worldHeight: Float) {

    companion object {
        const val MAX_BULLETS = 50
    }

    val ship: Ship = Ship(Vector2(worldWidth/3, worldHeight/2))
    val bullets: LinkedList<Bullet> = LinkedList()
    val enemies: LinkedList<Enemy> = LinkedList()
    val powerups: LinkedList<Item> = LinkedList()

    var powerUpRocket = false
    var powerUpDouble = false

    var powerUpRocketTime = 1f
    var powerUpDoubleTime = 1f

    var powerUpIndex = 0

    var levelZones = emptyList<LevelZone>()
    var levelZone = 0

    var levelMaxEnemies = 0

    var isBasicFire: Boolean = true


    fun initLevelZones() {
        levelZones = listOf(
            initLevelZone(),
            initLevelZone(),
            initLevelZone(),
        )
    }

    fun chooseNextEnemy(): EnemyType {
        val currentZone = levelZones[levelZone]

        val prob = Math.random()
        var currentProb: Float = 0f
        for (i in currentZone.enemyProbabilityMap) {
            if (prob in currentProb..(currentProb + i.probability)) {
                return i
            }
            currentProb += i.probability
        }

        // Should never happen, but just in case
        return if (currentZone.enemyProbabilityMap.isNotEmpty()) currentZone.enemyProbabilityMap[0] else EnemyType("Basic", 0f, false)
    }

    private fun initLevelZone(): LevelZone {
        val distribution = mutableListOf<Float>()

        var lastProb: Float = 1f
        var sum: Float = 0f
        for (i in 0..1) {
            val currentProb = (Math.random() * lastProb * 0.8).toFloat()
            distribution.add(currentProb)
            lastProb -= currentProb
            sum += currentProb
        }
        distribution.add(1 - sum)
        distribution.shuffle()

        return LevelZone(listOf(
            EnemyType("Follow", distribution[0], false),
            EnemyType("Basic", distribution[1], false),
            EnemyType("Follow", distribution[2], true),
        ))
    }
}