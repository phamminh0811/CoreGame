package com.prj1.mysticdungeon.ai

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import com.prj1.mysticdungeon.component.*
import ktx.math.component1
import ktx.math.component2
import ktx.math.vec2

private val TMP_RECT = Rectangle()
data class AiEntity (
    val  entity: Entity,
    val world: World,
    private val stateCmps: ComponentMapper<StateComponent> = world.mapper(),
    private val animationCmps: ComponentMapper<AnimationComponent> = world.mapper(),
    private val moveCmps: ComponentMapper<MoveComponent> = world.mapper(),
    private val attackCmps: ComponentMapper<AttackComponent> = world.mapper(),
    private val lifeCmps: ComponentMapper<LifeComponent> = world.mapper(),
    private val physicCmps: ComponentMapper<PhysicComponent> = world.mapper(),
    private val aiCmps: ComponentMapper<AiComponent> = world.mapper(),
    private val playerCmps: ComponentMapper<PlayerComponent> = world.mapper(),
    ) {

    val location: Vector2
        get() = physicCmps[entity].body.position
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

    fun doAndStartAttack(){
        with(attackCmps[entity]){
            doAttack = true
            startAttack()
        }
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

    fun moveTo(target: Vector2) {
        val  (targetX, targetY) = target
        val physicCmp = physicCmps[entity]
        val (sourceX, sourceY) = physicCmp.body.position

        with(moveCmps[entity]){
            val angleRad = MathUtils.atan2(targetY - sourceY, targetX - sourceX)
            cos = MathUtils.cos(angleRad)
            sin = MathUtils.sin(angleRad)
        }
    }

    fun inRange(range: Float, target: Vector2): Boolean {
        val physicCmp = physicCmps[entity]
        val (sourceX, sourceY) = physicCmp.body.position
        var (sizeX, sizeY) = physicCmp.size

        sizeX += range
        sizeY += range
        TMP_RECT.set(
            sourceX - sizeX * 0.5f,
            sourceY - sizeY * 0.5f,
            sizeX,
            sizeY
        )

        return TMP_RECT.contains(target)
    }

    fun stopMovement() {
        with(moveCmps[entity]){
            cos = 0f
            sin = 0f
        }
    }

    fun canAttack(): Boolean {
        val attackCmp = attackCmps[entity]
        if(!attackCmp.isReady){
            return false
        }

        val enemy = nearByEnemies().firstOrNull()
        if(enemy == null){
            return false
        }

        val enemyPhysicCmp = physicCmps[enemy]
        val (sourceX, sourceY) = enemyPhysicCmp.body.position
        return inRange( 1.5f + attackCmp.frontExtraRange, vec2(sourceX , sourceY))
    }

    fun hasEnemyNearBy(): Boolean = nearByEnemies().isNotEmpty()
    private fun nearByEnemies(): List<Entity> {
        val aiCmp = aiCmps[entity]
        return aiCmp.nearByEntities.filter { it in playerCmps && !lifeCmps[it].isDead }
    }
}