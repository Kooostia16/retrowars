package com.serwylo.retrowars.games.gradius.entities

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import kotlin.math.abs
import kotlin.math.sign

class FollowingEnemy(
    initialPosition: Vector2,
    playerPosition: Vector2,
): Enemy(initialPosition, 0f) {

    companion object {
        const val FOLLOW_TIME_DELAY = 2f
        const val FOLLOW_TIME = 1f
    }

    private var followTimerDelay = FOLLOW_TIME_DELAY
    private var followTimer = FOLLOW_TIME
    private var playerPosition: Vector2 = playerPosition.cpy()

    override val shape: Polygon = Polygon(floatArrayOf(
            0f, 0f,
            10f, -5f,
            10f, 5f,
            0f, 0f,
        )
    )
    override val collisionShape: Rectangle by lazy {
        shape.boundingRectangle
    }


    override fun render(camera: Camera, r: ShapeRenderer) {
        val playerOffset = position.y - playerPosition.y
//        r.projectionMatrix = camera.combined
//        r.begin(ShapeRenderer.ShapeType.Line)
        r.identity()
        r.translate(position.x, position.y, 0f)
        if (followTimer > 0 && abs(playerOffset) > 0.5f) {
            r.rotate(0f, 0f, 1f, 45 * sign(position.y - playerPosition.y))
        }
        r.polygon(shape.vertices)
        //TODO remove, was used for debug
//        r.identity()
//        r.rect(collisionShape.x, collisionShape.y, collisionShape.width, collisionShape.height)
//        r.end()
//        r.identity()
    }

    override fun update(delta: Float, playerPosition: Vector2) {
        val playerOffset = position.y - this.playerPosition.y

        position.x += velocity.x * delta

        if (followTimer > 0) {
            if (abs(playerOffset) > 0.5f) {
                position.y += velocity.x * sign(playerOffset) * delta
            }
            followTimer -= delta
        } else {
            followTimerDelay -= delta
            if (followTimerDelay <= 0) {
                followTimer = FOLLOW_TIME
                followTimerDelay = FOLLOW_TIME_DELAY
                this.playerPosition = playerPosition.cpy()
            }
        }
        collisionShape.setPosition(position.x, position.y - 5f)
    }
}