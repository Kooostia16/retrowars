package com.serwylo.retrowars.games.gradius.entities

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.FloatArray

class Ship(initialPosition: Vector2): Colliding {

    companion object {
        const val HOR_VELOCITY = 80f
        const val VER_VELOCITY = 80f
    }

    private val shape: Polygon = Polygon(floatArrayOf(
        0.0f, 0.0f,
        0.0f, 10.0f,
        20.0f, 0.0f,
        0.0f, 0.0f,
    ))

    var bulletBasicOffset: Vector2 = Vector2(20f, 0f)

    var position: Vector2 = initialPosition.cpy()
    private var velocity: Vector2 = Vector2(0f, 0f)

    override fun getBoundingCircle(): Polygon = shape

    override fun collidesWith(other: Colliding): Boolean =
        Intersector.intersectPolygons(other.getBoundingCircle(), shape, null)

    fun setVelocityButtonsState(up: Boolean, down: Boolean, right: Boolean, left: Boolean) {
        val hor = (if (right) HOR_VELOCITY else 0f) - (if (left) HOR_VELOCITY else 0f)
        val ver = (if (up) VER_VELOCITY else 0f) - (if (down) VER_VELOCITY else 0f)

        velocity = Vector2(hor, ver)
    }

    fun render(camera: Camera, r: ShapeRenderer) {
        r.projectionMatrix = camera.combined
        r.begin(ShapeRenderer.ShapeType.Line)
        r.identity()
        r.translate(position.x, position.y, 0.0f)
        r.polygon(shape.vertices)
        r.end()
        r.identity()
    }

    fun update(delta: Float) {
        position.mulAdd(velocity, delta)
    }
}