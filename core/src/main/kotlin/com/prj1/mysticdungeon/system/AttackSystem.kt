package com.prj1.mysticdungeon.system


import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.github.quillraven.fleks.*
import com.prj1.mysticdungeon.MysticDungeon
import com.prj1.mysticdungeon.component.*
import com.prj1.mysticdungeon.component.DirectionType.*
import com.prj1.mysticdungeon.system.EntitySpawnSystem.Companion.HIT_BOX_SENSOR
import ktx.app.gdxError
import ktx.box2d.query
import ktx.math.component1
import ktx.math.component2
import ktx.math.vec2

@AllOf([AttackComponent::class, PhysicComponent::class, ImageComponent::class])
class AttackSystem(
    private val phWorld: World,
    private val atlas: TextureAtlas,
    private val attackCmps: ComponentMapper<AttackComponent>,
    private val physicCmps: ComponentMapper<PhysicComponent>,
    private val imageCmps: ComponentMapper<ImageComponent>,
    private val animCmps: ComponentMapper<AnimationComponent>,
    private val defCmps: ComponentMapper<DefendComponent>,
    private val lifeCmps: ComponentMapper<LifeComponent>
): IteratingSystem() {

    private fun attackSize(dir: DirectionType) : Vector2 {
        val regions = atlas.findRegions("${AnimationModel.CHAR.atlasKey}_${AnimationType.ATTACK.atlasKey}_${dir.atlasKey}")
        if (regions.isEmpty){
            gdxError("There are no regions for attack model")
        }

        val firstFrame = regions.first()
        return vec2(firstFrame.originalWidth * MysticDungeon.UNIT_SCALE, firstFrame.originalHeight * MysticDungeon.UNIT_SCALE)
    }

    private fun updateAttackImage(entity: Entity, dir: DirectionType){
        imageCmps.getOrNull(entity)?.let { imageCmp ->
           if (imageCmp.image?.isChar == true){
               imageCmp.image?.isAttacking = true
               imageCmp.image?.dir = dir
               imageCmp.image?.size = attackSize(dir)
           }
        }
    }

    private fun updateNonAttackImage(entity: Entity){
        imageCmps.getOrNull(entity)?.let { imageCmp ->
            imageCmp.image?.isAttacking = false
            imageCmp.image?.dir = NONE
            imageCmp.image?.size = vec2()
        }
    }

    override fun onTickEntity(entity: Entity) {
        val attackCmp = attackCmps[entity]
        val animation = animCmps[entity]

        if(attackCmp.isReady && !attackCmp.doAttack){
//            entity doesn't want to attack and is not executing an attack
            updateNonAttackImage(entity)
            return
        }

        if(attackCmp.isPrepared && attackCmp.doAttack){
//            attack intention and is ready to attack -> start the attack
            updateAttackImage(entity, animation.dir)
            attackCmp.doAttack = false
            attackCmp.state = AttackState.ATTACKING
            attackCmp.delay = attackCmp.maxDelay
            return
        }

        attackCmp.delay -= deltaTime
        if (attackCmp.delay <= 0f && attackCmp.isAttacking){
//            deal damage to near by enemy
            attackCmp.state = AttackState.DEAL_DAMAGE

//            val image = imageCmps[entity].image
            val physicCmp = physicCmps[entity]
            val (x, y) = physicCmp.body.position
            val (w, h) = physicCmp.size
            val halfW = w * 0.5f
            val halfH = h * 0.5f

            if (animation.dir == LEFT){
                FRONT_RECT.set(
                    x - halfW - attackCmp.frontExtraRange,
                    y - halfH,
                    x + halfW,
                    y + halfH
                )
            }

            if (animation.dir == RIGHT){
                FRONT_RECT.set(
                    x - halfW,
                    y - halfH,
                    x + halfW + attackCmp.frontExtraRange,
                    y + halfH
                )
            }

            if (animation.dir == DOWN){
                FRONT_RECT.set(
                    x - halfW,
                    y - halfH - attackCmp.frontExtraRange,
                    x + halfW,
                    y + halfH
                )
            }

            if (animation.dir == UP){
                FRONT_RECT.set(
                    x - halfW,
                    y - halfH,
                    x + halfW,
                    y + halfH +  attackCmp.frontExtraRange
                )
            }
            phWorld.query(FRONT_RECT.x, FRONT_RECT.y, FRONT_RECT.width, FRONT_RECT.height){ fixture ->  
                if (fixture.userData != HIT_BOX_SENSOR){
                    return@query true
                }

                val fixtureEntity = fixture.entity
                if(fixtureEntity == entity){
                    // not attack ourselves
                    return@query true
                }

                configureEntity(fixtureEntity){
                    lifeCmps.getOrNull(it)?.let { lifeCmp ->
                        lifeCmp.takeDamage += ((attackCmp.damage - (defCmps[it].totalShield /2)) * MathUtils.random(0.9f, 1.2f)).coerceAtLeast(3f)
                    }
                }
                return@query true
            }
        }

        val isDone = animCmps.getOrNull(entity)?.isAnimationDone ?: true
        if (isDone){
            attackCmp.state = AttackState.READY
            updateNonAttackImage(entity)
        }
    }

    companion object{
        val FRONT_RECT = Rectangle()
        val LEFT_RECT = Rectangle()
        val RIGHT_RECT = Rectangle()
    }
}

// Problem: Attack twice can deal damage, attack once is not
