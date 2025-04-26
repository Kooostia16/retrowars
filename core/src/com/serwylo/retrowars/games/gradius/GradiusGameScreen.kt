package com.serwylo.retrowars.games.gradius

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
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
    private var currentEnemyType = EnemyType("", 0f, false)
    private var swarmCount = 0
    private val livesAndPowerupContainer = HorizontalGroup().apply { space(UI_SPACE) }

    init {
        addGameScoreToHUD(livesAndPowerupContainer)
        state.levelMaxEnemies = (Math.random() * 25 + 25).toInt()
        state.initLevelZones()
    }

    override fun getSoundLibrary() = sounds

    override fun updateGame(delta: Float) {

        state.ship.update(delta)

        processInput(delta)
        processCollisions(delta)

        maybeSpawnEnemies(delta)

        if (state.powerUpDouble) {
            state.powerUpDoubleTime -= delta
        }

        if (state.powerUpRocket) {
            state.powerUpRocketTime -= delta
        }
    }

    override fun renderGame(camera: Camera) {
        val shapeRenderer = game.uiAssets.shapeRenderer
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        state.ship.render(camera, shapeRenderer)

        state.bullets.forEach {
            it.render(camera, shapeRenderer)
        }
        state.enemies.forEach {
            it.render(camera, shapeRenderer)
        }
        state.powerups.forEach {
            it.render(camera, shapeRenderer)
        }
        shapeRenderer.end()
        shapeRenderer.identity()
    }

    override fun onReceiveDamage(strength: Int) {

    }

    override fun show() {
        Gdx.input.inputProcessor = getInputProcessor()
    }

    private fun powerUp() {
        updateIndexIfPowerUp(state.powerUpIndex)

        state.powerUpIndex = -1

        updatePowerUpHud()
    }

    private fun updateIndexIfPowerUp(index: Int) {
        if (index == 0 && state.ship.velocityMod < 2f) {
            state.ship.velocityMod += 0.5f
        } else if (index == 1 && !state.powerUpDouble) {
            state.powerUpDouble = true
        } else if (index == 2 && !state.powerUpRocket) {
            state.powerUpRocket = true
        }
    }

    private fun increasePowerUp() {
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

    private fun updatePowerUpHud() {
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

    private fun maybeSpawnEnemies(delta: Float) {
        if (timer < 0f) {
            val enemyInitPosition = Vector2(viewport.worldWidth + 30f, (Math.random() * (viewport.worldHeight - 30f)).toFloat())
            if (swarmCount <= 0) {
                val nextEnemy = state.chooseNextEnemy()
                currentEnemyType = nextEnemy
                if (nextEnemy.isSwarm) {
                    swarmCount = (Math.random() * 5 + 10).toInt()
                }
                timer = 2f
            } else {
                swarmCount -= 1
                timer = 0.5f
            }

            state.enemies.add(
                when (currentEnemyType.type) {
                    "Basic" -> Enemy(enemyInitPosition, viewport.worldHeight)
                    "Follow" -> FollowingEnemy(enemyInitPosition, state.ship.position)
                    else -> Enemy(Vector2(viewport.worldWidth + 10, (Math.random() * viewport.worldHeight).toFloat()), viewport.worldHeight)
                }
            )
        }

        timer -= delta
    }

    private fun processInput(delta: Float) {
        controller!!.update(delta)

        state.ship.setVelocityButtonsState(
            controller.trigger(GradiusSoftController.Buttons.UP),
            controller.trigger(GradiusSoftController.Buttons.DOWN),
            controller.trigger(GradiusSoftController.Buttons.RIGHT),
            controller.trigger(GradiusSoftController.Buttons.LEFT)
        )

        if (controller.trigger(GradiusSoftController.Buttons.FIRE)) {
            fireBasic()
        } else {
            state.isBasicFire = true
        }

        if (controller.trigger(GradiusSoftController.Buttons.SECONDARY)) {
            powerUp()
        }
    }

    private fun processCollisions(delta: Float) {
        val bulletsIt = state.bullets.iterator()

        while (bulletsIt.hasNext()) {
            val bullet = bulletsIt.next()
            bullet.update(delta)
            if (bullet.isOutsideView(viewport.worldWidth.toInt())) {
                bulletsIt.remove()
            }
        }

        val enemiesIt = state.enemies.iterator()

        while (enemiesIt.hasNext()) {
            val enemy = enemiesIt.next()
            enemy.update(delta, state.ship.position)
            var removeEnemy = false
            val bulletsIt = state.bullets.iterator()

            while (bulletsIt.hasNext()) {
                val bullet = bulletsIt.next()

                if (enemy.collidesWith(bullet)) {
                    increaseScore(10)
                    state.powerups.add(Item(enemy.position))
                    bulletsIt.remove()
                    removeEnemy = true
                    break
                }
            }

            if (state.ship.collidesWith(enemy) || removeEnemy) {
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
    }

    private fun fireBasic() {
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