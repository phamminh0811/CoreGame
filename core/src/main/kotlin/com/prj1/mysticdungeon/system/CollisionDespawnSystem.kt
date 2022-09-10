package com.prj1.mysticdungeon.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.prj1.mysticdungeon.component.TiledComponent
import com.prj1.mysticdungeon.event.CollisionDespawnEvent
import com.prj1.mysticdungeon.event.fire

@AllOf([TiledComponent::class])
class CollisionDespawnSystem(
    private val tiledCmps: ComponentMapper<TiledComponent>,
    private val stage: Stage
): IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        with (tiledCmps[entity]){
            if (tiledCmps[entity].nearByEntities.isEmpty()){
                stage.fire(CollisionDespawnEvent(cell))
                world.remove(entity)
            }
        }
    }
}