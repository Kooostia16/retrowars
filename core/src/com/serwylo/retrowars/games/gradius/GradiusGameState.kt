package com.serwylo.retrowars.games.gradius

import com.badlogic.gdx.math.Vector2
import com.serwylo.retrowars.games.gradius.entities.Bullet
import com.serwylo.retrowars.games.gradius.entities.Enemy
import com.serwylo.retrowars.games.gradius.entities.Ship
import java.util.LinkedList
import java.util.Queue

class GradiusGameState(worldWidth: Float, worldHeight: Float) {

    companion object {
        public const val MAX_BULLETS = 10
    }

    val ship: Ship = Ship(Vector2(worldWidth/3, worldHeight/2))
    val bullets: LinkedList<Bullet> = LinkedList()
    val enemies: LinkedList<Enemy> = LinkedList()

    var isBasicFire: Boolean = true
}