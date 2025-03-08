package com.serwylo.retrowars.games.gradius.entities

import com.badlogic.gdx.math.Polygon

interface Colliding {
    fun getBoundingCircle(): Polygon
    fun collidesWith(other: Colliding): Boolean
}