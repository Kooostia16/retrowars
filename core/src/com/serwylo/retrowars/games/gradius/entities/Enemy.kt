package com.serwylo.retrowars.games.gradius.entities

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.FloatArray

open class Enemy(
    initialPosition: Vector2
): Colliding {

    protected open var shape: Polygon = Polygon(floatArrayOf(
        0.0f, 0.0f,
        0.0f, 10.0f,
        -20.0f, 0.0f,
        0.0f, 0.0f,
    ))

    protected var position: Vector2 = initialPosition.cpy()
    protected var velocity: Vector2 = Vector2(-50f, 0f)

    var isDestroyed: Boolean = false
        private set

    override fun getBoundingCircle(): Polygon = shape

    override fun collidesWith(other: Colliding): Boolean = shape.boundingRectangle.overlaps(other.getBoundingCircle().boundingRectangle)

    open fun render(camera: Camera, r: ShapeRenderer) {
        r.projectionMatrix = camera.combined
        r.begin(ShapeRenderer.ShapeType.Line)
        r.polygon(shape.transformedVertices)
        r.end()
    }

    fun destroy() {
        isDestroyed = true
    }

    open fun update(delta: Float, playerPosition: Vector2) {
        position.mulAdd(velocity, delta)
        shape.setPosition(position.x, position.y)
    }
}