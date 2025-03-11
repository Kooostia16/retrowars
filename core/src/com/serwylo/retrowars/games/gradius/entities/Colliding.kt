package com.serwylo.retrowars.games.gradius.entities

import com.badlogic.gdx.math.Rectangle

interface Colliding {
    fun getBoundingRectangle(): Rectangle
    fun collidesWith(other: Colliding): Boolean
}