package com.serwylo.retrowars.games.gradius

import com.badlogic.gdx.math.Vector2
import com.serwylo.retrowars.games.gradius.entities.Bullet
import com.serwylo.retrowars.games.gradius.entities.Enemy
import com.serwylo.retrowars.games.gradius.entities.Item
import com.serwylo.retrowars.games.gradius.entities.Ship
import java.util.LinkedList
import java.util.Queue

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

    var isBasicFire: Boolean = true
}