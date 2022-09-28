package com.prj1.mysticdungeon.system

import com.github.quillraven.fleks.*
import com.prj1.mysticdungeon.component.AiComponent
import com.prj1.mysticdungeon.component.DeadComponent

@AllOf([AiComponent::class])
@NoneOf([DeadComponent::class])
class AiSystem (
    private val aiCmps: ComponentMapper<AiComponent>
    ): IteratingSystem(){

    override fun onTickEntity(entity: Entity) {
        with(aiCmps[entity]){
            behaviorTree.step()
        }
    }
}