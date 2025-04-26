package com.serwylo.retrowars.games.gradius.entities

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

open class Enemy(
    initialPosition: Vector2,
    worldY: Float
): Colliding() {

    private var time: Float = 0f
    private val movementTemplate: String by lazy {
        if (Math.random() > 0.5) {
            "basic"
        } else {
            "sine"
        }
    }

    // Integrating cos*a gives sin*a which has max a, thus we can take the minimum to not go off the screen
    private val movementMaxY: Float by lazy {
        initialPosition.y.coerceAtMost((worldY - initialPosition.y).coerceAtMost(30f))
    }

    protected open val shape: Polygon by lazy {
        if (Math.random() > 0) {
            Polygon(floatArrayOf(
                0f,   0f,
                20f,   10f,
                20f, 0f,
                0f,   0f,
            ))
        } else {
            Polygon(floatArrayOf(
                0f, 0f,
                20f, 0f,
                20f,  10f,
                0f, 10f,
                -5f, 5f,
                0f, 0f
            ))
        }
    }

    protected open val collisionShape: Rectangle by lazy {
        shape.boundingRectangle
    }

    var position: Vector2 = initialPosition.cpy()
    protected var velocity: Vector2 = Vector2(-50f, 0f)

    override fun getBoundingRectangle(): Rectangle = collisionShape

    open fun render(camera: Camera, r: ShapeRenderer) {
//        r.projectionMatrix = camera.combined
//        r.begin(ShapeRenderer.ShapeType.Line)
        r.identity()
        r.translate(position.x, position.y, 0f)
        r.polygon(shape.vertices)
        //TODO remove, was used for debug
        r.identity()
        r.rect(collisionShape.x, collisionShape.y, collisionShape.width, collisionShape.height)
//        r.end()
//        r.identity()
    }

    open fun update(delta: Float, playerPosition: Vector2) {
        if (movementTemplate == "sine") {
            position.mulAdd(velocity, delta)
            velocity.y = Math.cos(time.toDouble()).toFloat() * movementMaxY
        } else {
            position.mulAdd(velocity, delta)
        }
        time += delta
        collisionShape.setPosition(position.x, position.y)
    }
}