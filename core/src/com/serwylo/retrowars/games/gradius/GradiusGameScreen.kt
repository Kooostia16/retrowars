package com.serwylo.retrowars.games.gradius

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Label
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
        addGameScoreToHUD(livesAndPowerupContainer)
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

        if (controller.trigger(GradiusSoftController.Buttons.SECONDARY)) {
            powerUp()
        }

        val bulletsIt = state.bullets.iterator()

        while (bulletsIt.hasNext()) {
            val bullet = bulletsIt.next()
            bullet.update(delta, viewport.worldHeight.toInt())
            if (bullet.isOutsideView(viewport.worldWidth.toInt())) {
                bulletsIt.remove()
            }
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
                    break
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
                increasePowerUp()
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

        if (state.powerUpDouble) {
            state.powerUpDoubleTime -= delta
        }

        if (state.powerUpRocket) {
            state.powerUpRocketTime -= delta
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

    fun powerUp() {
        updateIndexIfPowerUp(state.powerUpIndex)

        state.powerUpIndex = -1

        updatePowerUpHud()
    }

    fun updateIndexIfPowerUp(index: Int) {
        if (index == 0 && state.ship.velocityMod < 2f) {
            state.ship.velocityMod += 0.5f
        } else if (index == 1 && !state.powerUpDouble) {
            state.powerUpDouble = true
        } else if (index == 2 && !state.powerUpRocket) {
            state.powerUpRocket = true
        }
    }

    fun increasePowerUp() {
        val powerups = listOf(
            if (state.ship.velocityMod < 2f) 0 else -1,
            if (state.powerUpDouble) -1 else 1,
            if (state.powerUpRocket) -1 else 2
        ).filter { it != -1 }

        val currentIndex = powerups.indexOf(state.powerUpIndex)

        if (currentIndex < powerups.size - 1) {
            state.powerUpIndex = powerups[currentIndex + 1]
        }

        if (powerups.isEmpty()) {
            increaseScore(100)
        }

        updatePowerUpHud()
    }

    fun updatePowerUpHud() {
        val powerups = listOf(state.ship.velocityMod > 1.5f, state.powerUpDouble, state.powerUpRocket)

        livesAndPowerupContainer.clear()
        for (i in 0..2) {
            var powerUpNumber = (i + 1).toString()
            if (powerups[i]) {
                powerUpNumber = "0"
            } else if (state.powerUpIndex == i) {
                powerUpNumber += "'"
            }
            livesAndPowerupContainer.addActor(Label(powerUpNumber, game.uiAssets.getStyles().label.large))
        }
    }

    override fun show() {
        Gdx.input.inputProcessor = getInputProcessor()
    }

    fun fireBasic() {
        if (state.isBasicFire && state.bullets.size < GradiusGameState.MAX_BULLETS) {
            state.bullets.add(Bullet(state.ship.position.cpy().add(state.ship.bulletBasicOffset)))

            if (state.powerUpDouble) {
                if (state.powerUpDoubleTime <= 0) {
                    state.bullets.add(Bullet(state.ship.position.cpy().add(state.ship.bulletBasicOffset), Vector2(100f, 50f)))
                    state.powerUpDoubleTime = 0.5f
                }
            }

            if (state.powerUpRocket) {
                if (state.powerUpRocketTime <= 0) {
                    state.bullets.add(Bullet(state.ship.position.cpy().add(state.ship.bulletBasicOffset), Vector2(100f, -50f)))
                    state.powerUpRocketTime = 1f
                }
            }

            state.isBasicFire = false
        }
    }

}