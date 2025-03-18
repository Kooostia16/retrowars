package com.serwylo.retrowars.games.gradius.entities

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import kotlin.math.sign

class Ship(initialPosition: Vector2): Colliding() {

    companion object {
        const val VELOCITY = 80f
    }

    private val shape: Polygon = Polygon(floatArrayOf(
        0.0f, 0.0f,
        0.0f, 10.0f,
        20.0f, 0.0f,
        0.0f, 0.0f,
    ))
    private val collisionShape: Rectangle = shape.boundingRectangle

    private val fireShape: Polygon = Polygon(floatArrayOf(
        0f, 2.5f,
        -5f, 5f,
        0f, 7.5f,
        0f, 2.5f,
    ))

    var bulletBasicOffset: Vector2 = Vector2(20f, 0f)

    var velocityMod = 1f

    var position: Vector2 = initialPosition.cpy()
    private var velocity: Vector2 = Vector2(0f, 0f)

    override fun getBoundingRectangle(): Rectangle = collisionShape

    fun setVelocityButtonsState(up: Boolean, down: Boolean, right: Boolean, left: Boolean) {
        val hor = (if (right) 1f else 0f) - (if (left) 1f else 0f)
        val ver = (if (up) 1f else 0f) - (if (down) 1f else 0f)

        velocity = Vector2(hor, ver).nor().scl(VELOCITY)
    }

    fun render(camera: Camera, r: ShapeRenderer) {
//        r.projectionMatrix = camera.combined
//        r.begin(ShapeRenderer.ShapeType.Line)
        r.identity()
        r.translate(position.x, position.y, 0.0f)
        r.polygon(shape.vertices)
        fireShape.vertices[2] = -10f + (Math.random() * 2f).toFloat() - sign(velocity.x) * 2f
        fireShape.vertices[3] = 4f + (Math.random() * 2f).toFloat()
        r.polygon(fireShape.vertices)
//        r.end()
//        r.identity()
    }

    fun update(delta: Float) {
        position.mulAdd(velocity, delta*velocityMod)
        collisionShape.setPosition(position.x, position.y)
    }
}