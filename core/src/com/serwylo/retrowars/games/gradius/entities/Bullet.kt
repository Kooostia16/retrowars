package com.serwylo.retrowars.games.gradius.entities

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

open class Bullet(
    initialPosition: Vector2,
    initialVelocity: Vector2 = Vector2(100f, 0f),
): Colliding() {

    private val shape: Polygon = Polygon(floatArrayOf(
        0f, 0f,
        0f, 2f,
        5f, 2f,
        5f, 0f,
        0f, 0f,
    ))
    private val collisionShape: Rectangle by lazy {
        shape.boundingRectangle
    }
    private var position: Vector2 = initialPosition.cpy()
    private val velocity: Vector2 = initialVelocity.cpy()

    override fun getBoundingRectangle(): Rectangle = collisionShape

    fun isOutsideView(worldWidth: Int): Boolean {
        return position.x > worldWidth
    }

    fun render(camera: Camera, r: ShapeRenderer) {
//        r.projectionMatrix = camera.combined
//        r.begin(ShapeRenderer.ShapeType.Line)
        r.identity()
        r.translate(position.x, position.y, 0f)
        r.polygon(shape.vertices)
//        r.end()
//        r.identity()
    }

    fun update(delta: Float) {
        position = position.mulAdd(velocity, delta)
        collisionShape.setPosition(position.x, position.y)

        if (position.y - collisionShape.height < 0) {
            position.y -= velocity.y * delta
            velocity.y = 0f
        }

    }
}