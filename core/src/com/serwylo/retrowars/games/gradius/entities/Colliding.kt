package com.serwylo.retrowars.games.gradius.entities

import com.badlogic.gdx.math.Rectangle

abstract class Colliding {
    abstract fun getBoundingRectangle(): Rectangle
    fun collidesWith(other: Colliding): Boolean = getBoundingRectangle().overlaps(other.getBoundingRectangle())
}