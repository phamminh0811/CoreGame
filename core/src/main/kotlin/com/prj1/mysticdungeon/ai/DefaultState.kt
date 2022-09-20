package com.prj1.mysticdungeon.ai

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.prj1.mysticdungeon.component.AnimationType


enum class DefaultState : EntityState{
    IDLE{
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.IDLE, entity.animCmp.dir)
        }

        override fun update(entity: AiEntity) {
            when{
                entity.wantsToAttack -> entity.state(ATTACK)
                entity.wantsToRun ->  entity.state(RUN)
            }
        }
        },
    RUN{
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.RUN, entity.animCmp.dir)
        }

        override fun update(entity: AiEntity) {
            when{
                entity.wantsToAttack -> entity.state(ATTACK)
                !entity.wantsToRun -> entity.state(IDLE)
            }
        }
       },
    ATTACK{
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.ATTACK, entity.animCmp.dir, PlayMode.NORMAL)
            entity.root(true)
            entity.startAttack()
        }

        override fun exit(entity: AiEntity) {
            entity.root(false)
        }

        override fun update(entity: AiEntity) {
            val attackCmp = entity.attackCmp
            if(attackCmp.isReady && !attackCmp.doAttack){
                entity.changeToPreviousState()
            } else if(attackCmp.isReady){
//                start another attack
                entity.animation(AnimationType.ATTACK, entity.animCmp.dir, PlayMode.NORMAL, true)
            }
        }

          },
    HIT,
    DEAD,
    SHIELD_STATIC,
    SHIELD_HIT,
    CLIMB,
    FALLING
}

enum class DefaultGlobalState: EntityState{
    CHECK_ALIVE, CHECK_SHIELD, CHECK_HIT
}