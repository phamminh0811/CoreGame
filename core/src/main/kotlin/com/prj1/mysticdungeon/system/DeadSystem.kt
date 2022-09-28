package com.prj1.mysticdungeon.system

import com.github.quillraven.fleks.*
import com.prj1.mysticdungeon.ai.DefaultState
import com.prj1.mysticdungeon.component.*


@AllOf([DeadComponent::class])
class DeadSystem(
    private val playerCmps: ComponentMapper<PlayerComponent>,
    private val stateCmps: ComponentMapper<StateComponent>,
    private val lifeCmps: ComponentMapper<LifeComponent>,
    private val deadCmps: ComponentMapper<DeadComponent>,
) : IteratingSystem(){
    override fun onTickEntity(entity: Entity) {
        val deadCmp = deadCmps[entity]

        if (deadCmp.reviveTime ==0f){
            world.remove(entity)
            return
        }

        deadCmp.reviveTime -= deltaTime
        if (deadCmp.reviveTime <=0f){
            with(lifeCmps[entity]){ life = max}
            stateCmps.getOrNull(entity)?.let { stateCmp ->
                stateCmp.nextState = DefaultState.RESURRECT
            }
            configureEntity(entity) {deadCmps.remove(entity)}
        }
    }

}