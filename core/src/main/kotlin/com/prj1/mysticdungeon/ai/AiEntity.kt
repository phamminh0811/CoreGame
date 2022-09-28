package com.prj1.mysticdungeon.ai

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import com.prj1.mysticdungeon.component.*

data class AiEntity (
    val world: World,
    val  entity: Entity,
    private val stateCmps: ComponentMapper<StateComponent> = world.mapper(),
    private val animationCmps: ComponentMapper<AnimationComponent> = world.mapper(),
    private val moveCmps: ComponentMapper<MoveComponent> = world.mapper(),
    private val attackCmps: ComponentMapper<AttackComponent> = world.mapper(),
    private val lifeCmps: ComponentMapper<LifeComponent> = world.mapper(),
) {
    val isAnimationDone: Boolean
        get() = animCmp.isAnimationDone
    val isDead: Boolean
        get() = lifeCmps[entity].isDead

    val wantsToRun: Boolean
        get() {
            val moveCmp = moveCmps[entity]
            return moveCmp.cos != 0f || moveCmp.sin != 0f
        }

    val wantsToAttack: Boolean
        get() = attackCmps.getOrNull(entity)?.doAttack ?: false

    val animCmp: AnimationComponent
        get() = animationCmps[entity]

    val attackCmp: AttackComponent
        get() = attackCmps[entity]

    fun animation(type: AnimationType, dir: DirectionType, mode: PlayMode = PlayMode.LOOP, resetAnimation: Boolean = false) {
        with(animationCmps[entity]) {
            nextAnimation(this.model, type, dir)
            this.playMode = mode
            if (resetAnimation){
                stateTime = 0f
            }
        }
    }

    fun resetAnimation() {
        animationCmps[entity].stateTime = 0f
    }

    fun state(newState: EntityState, immediateChange: Boolean = false) {
        with (stateCmps[entity]){
            nextState = newState
            if (immediateChange){
                stateMachine.changeState(nextState)
            }
        }
    }

    fun changeToPreviousState() {
        with(stateCmps[entity]) {
            nextState = stateMachine.previousState
        }
    }

    fun startAttack() {
        attackCmps[entity].startAttack()
    }

    fun root(enable: Boolean) {
        with(moveCmps[entity]){
            root = enable
        }
    }

    fun enableGlobalState(enable: Boolean) {
        with(stateCmps[entity]){
            if (enable){
                stateMachine.globalState = DefaultGlobalState.CHECK_ALIVE
            } else {
                stateMachine.globalState = null
            }
        }
    }
}