package com.prj1.mysticdungeon.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys.*
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.World
import com.prj1.mysticdungeon.component.*
import ktx.app.KtxInputAdapter

class PlayerKeyboardInputProcessor(
    world: World,
    private var moveCmps: ComponentMapper<MoveComponent> = world.mapper(),
    private var animCmps: ComponentMapper<AnimationComponent> = world.mapper(),
    private var attackCmps: ComponentMapper<AttackComponent> = world.mapper()
) : KtxInputAdapter{
    private var playerSin = 0f
    private var playerCos = 0f
    private val playerEntities = world.family(allOf = arrayOf(PlayerComponent::class))

    init {
        Gdx.input.inputProcessor = this
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode.isMovementKey()) {
            when (keycode){
                UP -> playerSin = 1f
                DOWN -> playerSin = -1f
                RIGHT -> playerCos = 1f
                LEFT -> playerCos = -1f
            }
            updatePlayerMovement()
            return true
        } else if (keycode == SPACE){
            playerEntities.forEach {
                with(attackCmps[it]){
                    doAttack = true
                    startAttack()
                }

            }
            return true
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        if (keycode.isMovementKey()) {
            when (keycode){
                UP -> playerSin = if (Gdx.input.isKeyPressed(DOWN)) -1f else 0f
                DOWN -> playerSin = if (Gdx.input.isKeyPressed(UP)) 1f else 0f
                RIGHT -> playerCos = if (Gdx.input.isKeyPressed(LEFT)) -1f else 0f
                LEFT -> playerCos = if (Gdx.input.isKeyPressed(RIGHT)) 1f else 0f
            }
            updatePlayerMovement()
            return true
        }
        return false
    }

    private fun updatePlayerMovement() {
        playerEntities.forEach { player ->
            with (moveCmps[player]) {
                cos = playerCos
                sin = playerSin

                val playerAnim = animCmps[player]
                if (sin == 0f && cos == 0f){
                    playerAnim.nextAnimation(playerAnim.model, AnimationType.IDLE, playerAnim.dir)
                } else {
                    if (sin > 0){
                        playerAnim.nextAnimation(playerAnim.model, AnimationType.RUN, DirectionType.UP)
                    }
                    if (sin < 0){
                        playerAnim.nextAnimation(playerAnim.model, AnimationType.RUN, DirectionType.DOWN)
                    }
                    if (cos > 0){
                        playerAnim.nextAnimation(playerAnim.model, AnimationType.RUN, DirectionType.RIGHT)
                    }
                    if (cos < 0){
                        playerAnim.nextAnimation(playerAnim.model, AnimationType.RUN, DirectionType.LEFT)
                    }
                }
            }
        }
    }

    private fun Int.isMovementKey(): Boolean {
        return this == UP || this == DOWN || this == RIGHT || this == LEFT
    }
}

