package com.prj1.mysticdungeon.system

import com.github.quillraven.fleks.*
import com.prj1.mysticdungeon.component.DeadComponent
import com.prj1.mysticdungeon.component.LifeComponent
import com.prj1.mysticdungeon.component.PlayerComponent

@AllOf([LifeComponent::class])
@NoneOf([DeadComponent::class])
class LifeSystem(
    private val lifeCmps: ComponentMapper<LifeComponent>,
    private val deadCmps: ComponentMapper<DeadComponent>,
    private val playerCmps: ComponentMapper<PlayerComponent>
) : IteratingSystem(){
    override fun onTickEntity(entity: Entity) {
        val lifeCmp = lifeCmps[entity]
        lifeCmp.life = (lifeCmp.life + lifeCmp.regeneration * deltaTime).coerceAtMost(lifeCmp.max)

        if (lifeCmp.takeDamage > 0f){
            lifeCmp.life -= lifeCmp.takeDamage
            lifeCmp.takeDamage = 0f
        }

        if (lifeCmp.isDead){
            configureEntity(entity){
                deadCmps.add(it)
            }
        }
    }
}