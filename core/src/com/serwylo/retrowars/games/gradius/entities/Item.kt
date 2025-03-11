package com.serwylo.retrowars.games.gradius.entities

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

open class Item(
    initialPosition: Vector2
): Colliding {

    protected open val shape: Polygon = Polygon(floatArrayOf(
        0f, 0f,
        0f, 10f,
        10f, 10f,
        10f, 0f,
        0f, 0f,
        10f, 10f,
        10f, 0f,
        0f, 10f
    ))
    protected open val collisionShape: Rectangle by lazy {
        shape.boundingRectangle
    }

    protected var position: Vector2 = initialPosition.cpy()
    protected var velocity: Vector2 = Vector2(-50f, 0f)

    var isDestroyed: Boolean = false
        private set

    override fun getBoundingRectangle(): Rectangle = collisionShape

    override fun collidesWith(other: Colliding): Boolean = collisionShape.overlaps(other.getBoundingRectangle())

    open fun render(camera: Camera, r: ShapeRenderer) {
        r.projectionMatrix = camera.combined
        r.begin(ShapeRenderer.ShapeType.Line)
        r.identity()
        r.translate(position.x, position.y, 0f)
        r.polygon(shape.vertices)
        //TODO remove, was used for debug
//        r.identity()
//        r.translate(position.x, position.y, 0f)
//        r.rect(collisionShape.x, collisionShape.y, collisionShape.width, collisionShape.height)
        r.end()
        r.identity()
    }

    fun destroy() {
        isDestroyed = true
    }

    open fun update(delta: Float, playerPosition: Vector2) {
        position.mulAdd(velocity, delta)
        collisionShape.setPosition(position.x, position.y)
    }
}