package com.prj1.mysticdungeon.event

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.Entity
import com.prj1.mysticdungeon.component.DirectionType

fun Stage.fire(event: Event){
    this.root.fire(event)
}

data class MapChangeEvent(val map: TiledMap) : Event()
class CollisionDespawnEvent(val cell: Cell) : Event()