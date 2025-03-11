package com.serwylo.retrowars.games.gradius

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.serwylo.retrowars.RetrowarsGame
import com.serwylo.retrowars.games.GameScreen
import com.serwylo.retrowars.games.Games
import com.serwylo.retrowars.games.gradius.entities.*
import com.serwylo.retrowars.input.GradiusSoftController
import com.serwylo.retrowars.ui.UI_SPACE

class GradiusGameScreen(game: RetrowarsGame) : GameScreen(game, Games.gradius, 400f, 250f) {

    private val state = GradiusGameState(viewport.worldWidth, viewport.worldHeight)
    private val sounds = GradiusSoundLibrary()
    private var timer = 3f
    private val livesAndPowerupContainer = HorizontalGroup().apply { space(UI_SPACE) }

    init {
        addGameOverlayToHUD(livesAndPowerupContainer)
    }

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
        }

        val enemiesIt = state.enemies.iterator()

        while (enemiesIt.hasNext()) {
            val enemy = enemiesIt.next()
            enemy.update(delta, state.ship.position)

            val bulletsIt = state.bullets.iterator()
            while (bulletsIt.hasNext()) {
                val bullet = bulletsIt.next()

                if (enemy.collidesWith(bullet)) {
                    increaseScore(10)
                    state.powerups.add(Item(enemy.position))
                    bulletsIt.remove()
                    enemiesIt.remove()
                }
            }

            if (state.ship.collidesWith(enemy)) {
                enemiesIt.remove()
            }
        }

        val powerupsIt = state.powerups.iterator()

        while (powerupsIt.hasNext()) {
            val powerUp = powerupsIt.next()
            powerUp.update(delta, state.ship.position)
            if (powerUp.collidesWith(state.ship)) {
                powerupsIt.remove()
            }
        }

        if (timer <= 0) {
            val enemyInitPosition = Vector2(viewport.worldWidth + 10, (Math.random() * viewport.worldHeight).toFloat())
            state.enemies.add(
                when (Math.random()) {
                    in 0f..0.1f -> Enemy(enemyInitPosition)
                    in 0.1f..1.0f -> FollowingEnemy(enemyInitPosition, state.ship.position)
                    else -> Enemy(Vector2(viewport.worldWidth + 10, (Math.random() * viewport.worldHeight).toFloat()))
                }
            )
            timer = 2f
        }
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
        state.powerups.forEach {
            it.render(camera, shapeRenderer)
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