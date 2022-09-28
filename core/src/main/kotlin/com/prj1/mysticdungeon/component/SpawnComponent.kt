package com.prj1.mysticdungeon.component

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import ktx.math.vec2

const val DEFAULT_SPEED = 3f
const val DEFAULT_ATTACK_DAMAGE = 5f
const val DEFAULT_LIFE= 20f
const val DEFAULT_DEFEND = 5f

data class SpawnCfg(
    val model: AnimationModel,
    val speedScaling: Float = 1f,
    val canAttack: Boolean = true,
    val attackScaling: Float = 1f,
    val attackDelay: Float = 0.2f,
    val attackExtraFrontRange: Float = 0f,
    val attackExtraSideRange: Float = 0f,
    val defendScaling: Float = 1f,
    val defendShield: Int = 0,
    val lifeScaling: Float = 1f,
    val aiTreePath: String = "",
    val physicScaling: Vector2 = vec2(1f, 1f),
    val physicOffset: Vector2 = vec2(0f, 0f),
    val bodyType: BodyType = BodyType.DynamicBody
)
data class SpawnComponent(
    var type: String = "",
    var location: Vector2 =  vec2()
)