package com.serwylo.retrowars.games.gradius

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector2
import com.serwylo.retrowars.RetrowarsGame
import com.serwylo.retrowars.games.GameScreen
import com.serwylo.retrowars.games.Games
import com.serwylo.retrowars.games.gradius.entities.Bullet
import com.serwylo.retrowars.games.gradius.entities.Colliding
import com.serwylo.retrowars.games.gradius.entities.Enemy
import com.serwylo.retrowars.input.GradiusSoftController

class GradiusGameScreen(game: RetrowarsGame) : GameScreen(game, Games.gradius, 400f, 250f) {

    private val state = GradiusGameState(viewport.worldWidth, viewport.worldHeight)
    private val sounds = GradiusSoundLibrary()
    private var timer = 3f

    override fun getSoundLibrary() = sounds

    override fun updateGame(delta: Float) {

        timer -= delta

        controller!!.update(delta)

        state.ship.setVelocityButtonsState(
            controller.trigger(GradiusSoftController.Buttons.UP),
            controller.trigger(GradiusSoftController.Buttons.DOWN),
            controller.trigger(GradiusSoftController.Buttons.RIGHT),
            controller.trigger(GradiusSoftController.Buttons.LEFT)
        )

        state.ship.update(delta)

        if (controller.trigger(GradiusSoftController.Buttons.FIRE)) {
            fireBasic()
        } else {
            state.isBasicFire = true
        }

        state.bullets.forEach {bullet ->
            bullet.update(delta, viewport.worldWidth.toInt())
            state.enemies.forEach {enemy ->
                if (enemy.collidesWith(bullet)) {
                    enemy.destroy()
                    bullet.destroy()
                    increaseScore(10)
                }
            }
        }

        state.enemies.forEach {
            it.update(delta)
        }

        if (timer <= 0) {
            state.enemies.add(Enemy(Vector2(viewport.worldWidth + 10, (Math.random() * viewport.worldHeight).toFloat())))
            timer = 5f
        }

        state.enemies.removeAll(Enemy::isDestroyed)
        state.bullets.removeAll(Bullet::isDestroyed)
    }

    override fun renderGame(camera: Camera) {
        val shapeRenderer = game.uiAssets.shapeRenderer
        state.ship.render(camera, shapeRenderer)

        state.bullets.forEach {
            it.render(camera, shapeRenderer)
        }
        state.enemies.forEach {
            if (!it.isDestroyed) {
                it.render(camera, shapeRenderer)
            }
        }
    }

    override fun onReceiveDamage(strength: Int) {

    }

    override fun show() {
        Gdx.input.inputProcessor = getInputProcessor()
    }

    fun fireBasic() {
        if (state.isBasicFire && state.bullets.size < GradiusGameState.MAX_BULLETS) {
            state.bullets.add(Bullet(state.ship.position.cpy().add(state.ship.bulletBasicOffset)))
            state.isBasicFire = false
        }
    }

}