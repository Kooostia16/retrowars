package com.serwylo.retrowars.games.gradius.entities

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import kotlin.math.abs
import kotlin.math.sign

class FollowingEnemy(
    initialPosition: Vector2
): Enemy(initialPosition) {

    companion object {
        const val FOLLOW_TIME_DELAY = 2f
        const val FOLLOW_TIME = 1f
    }

    private var followTimerDelay = FOLLOW_TIME_DELAY
    private var followTimer = FOLLOW_TIME
    private var playerPosition: Vector2 = Vector2.Zero.cpy()

    override var shape: Polygon = Polygon(floatArrayOf(
            0f, 0f,
            10f, -5f,
            10f, 5f,
            0f, 0f,
        )
    )

    override fun render(camera: Camera, r: ShapeRenderer) {
        var poly = Polygon(shape.vertices.clone())
        val playerOffset = position.y - playerPosition.y
        r.projectionMatrix = camera.combined
        r.begin(ShapeRenderer.ShapeType.Line)
        if (followTimer > 0 && abs(playerOffset) > 0.5f) {
            poly.rotate(45 * sign(playerOffset))
//            r.rotate(0f, 0f, 1f, 45 * sign(position.y - playerPosition.y))
        }
        poly.setPosition(position.x, position.y)
        r.polygon(poly.transformedVertices)
        r.end()
        r.identity()
    }

    override fun update(delta: Float, playerPosition: Vector2) {
        val playerOffset = position.y - playerPosition.y

        this.playerPosition = playerPosition
        position.x += velocity.x * delta

        if (followTimer > 0 && abs(playerOffset) > 0.1f) {
            position.y += velocity.x * sign(playerOffset) * delta
            followTimer -= delta
        } else {
            if (followTimerDelay <= 0) {
                followTimer = FOLLOW_TIME
                followTimerDelay = FOLLOW_TIME_DELAY
            }
            followTimerDelay -= delta
        }
        shape.setPosition(position.x, position.y)
    }
}