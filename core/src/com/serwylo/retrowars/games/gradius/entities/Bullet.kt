package com.serwylo.retrowars.games.gradius.entities

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2

class Bullet(
    initialPosition: Vector2
): Colliding {

    private val shape: Polygon = Polygon(floatArrayOf(
        0f, 0f,
        0f, 6f,
        5f, 6f,
        5f, 0f,
        0f, 0f
    ))
    private var position: Vector2 = initialPosition.cpy()
    private val velocity: Vector2 = Vector2(100f, 0f)

    var isDestroyed: Boolean = false
        private set

    override fun getBoundingCircle(): Polygon = shape

    override fun collidesWith(other: Colliding): Boolean = shape.boundingRectangle.overlaps(other.getBoundingCircle().boundingRectangle)

    private fun isOutsideView(worldWidth: Int): Boolean {
        return position.x > worldWidth
    }

    fun render(camera: Camera, r: ShapeRenderer) {
        if (!isDestroyed) {
            r.projectionMatrix = camera.combined
            r.begin(ShapeRenderer.ShapeType.Line)
            r.identity()
            r.polygon(shape.transformedVertices)
            r.end()
        }
    }

    fun destroy() {
        isDestroyed = true
    }

    fun update(delta: Float, worldWidth: Int) {
        position = position.mulAdd(velocity, delta)

        shape.setPosition(position.x, position.y)

        if (isOutsideView(worldWidth)) {
            isDestroyed = true
        }
    }
}