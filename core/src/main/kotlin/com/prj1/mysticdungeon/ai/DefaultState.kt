package com.prj1.mysticdungeon.ai

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.prj1.mysticdungeon.component.AnimationType
import com.prj1.mysticdungeon.component.DirectionType


enum class DefaultState : EntityState{
    IDLE{
        override fun enter(entity: AiEntity) {
            if (entity.animCmp.dir == DirectionType.NONE){
                entity.animation(AnimationType.IDLE, DirectionType.RIGHT)
            } else{
                entity.animation(AnimationType.IDLE, entity.animCmp.dir)
            }
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
            if (entity.animCmp.dir == DirectionType.NONE){
                entity.animation(AnimationType.RUN, DirectionType.RIGHT)
            } else{
                entity.animation(AnimationType.RUN, entity.animCmp.dir)
            }
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
                entity.resetAnimation()
                attackCmp.startAttack()
            }
        }

          },
    HIT{
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.HIT, entity.animCmp.dir)
        }
       },

    DEAD{
        override fun enter(entity: AiEntity) {
            entity.root(true)
        }

        },
    RESURRECT{
        override fun enter(entity: AiEntity) {
            entity.enableGlobalState(true)
            entity.animation(AnimationType.DEAD,DirectionType.NONE, PlayMode.REVERSED, true)
        }

        override fun update(entity: AiEntity) {
            if (entity.isAnimationDone){
                entity.state(IDLE)
                entity.root(false)
            }
        }
    },
    SHIELD_STATIC,
    SHIELD_HIT,
    CLIMB,
    FALLING
}

enum class DefaultGlobalState: EntityState{
    CHECK_ALIVE{
        override fun update(entity: AiEntity) {
            if(entity.isDead){
                entity.enableGlobalState(false)
                entity.state(DefaultState.DEAD, true)
            }
        }
               }, CHECK_SHIELD, CHECK_HIT
}