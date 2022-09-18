package com.prj1.mysticdungeon.system

import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.prj1.mysticdungeon.component.DeadComponent
import com.prj1.mysticdungeon.component.PlayerComponent

@AllOf([DeadComponent::class])
class DeadSystem(
    private val playerCmps: ComponentMapper<PlayerComponent>
) : IteratingSystem(){
    override fun onTickEntity(entity: Entity) {
        if (entity in playerCmps){
//            TODO: Game Over
            return
        }

        world.remove(entity)
    }
}