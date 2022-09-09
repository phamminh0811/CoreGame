package com.prj1.mysticdungeon.system

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.prj1.mysticdungeon.component.PhysicComponent
import com.prj1.mysticdungeon.component.PhysicComponent.Companion.physicCmpFromShape2D
import com.prj1.mysticdungeon.event.MapChangeEvent
import ktx.box2d.body
import ktx.box2d.loop
import ktx.math.vec2
import ktx.tiled.*
import kotlin.math.max

@AllOf([PhysicComponent::class])
class CollisionSpawnSystem(
    private val phWorld: World
) : EventListener, IteratingSystem() {
    private fun TiledMapTileLayer.forEachCell(
        startX: Int,
        startY: Int,
        size: Int,
        action: (TiledMapTileLayer.Cell, Int, Int) -> Unit
    ){
        for (x in startX-size  ..  startX+size){
            for (y in startY-size .. startY+size){
                this.getCell(x,y)?.let { action(it,x ,y) }
            }
        }
    }

    override fun onTickEntity(entity: Entity) {

    }

    override fun handle(event: Event?): Boolean {
        when (event){
            is MapChangeEvent -> {
                event.map.forEachLayer<TiledMapTileLayer> { layer ->
                    layer.forEachCell(0, 0, max(event.map.width, event.map.height)){ cell, x, y ->
                        if (cell.tile.objects.isEmpty()){
//                            cell not link to object -> do nothing
                            return@forEachCell
                        }

                        cell.tile.objects.forEach{ mapObject ->
                            world.entity {
                                physicCmpFromShape2D(phWorld, x, y, mapObject.shape)
                            }
                        }
                    }
                }

                world.entity {
                    val w = event.map.width.toFloat()
                    val h = event.map.height.toFloat()

                    add<PhysicComponent>{
                        body = phWorld.body(StaticBody){
                            position.set(0f, 0f)
                            fixedRotation= true
                            allowSleep = false
                            loop(
                                vec2(0f, 0f),
                                vec2(w, 0f),
                                vec2(w, h),
                                vec2(0f, h)
                            )
                        }
                    }
                }
                return true
            }
        }

        return false
    }
}
