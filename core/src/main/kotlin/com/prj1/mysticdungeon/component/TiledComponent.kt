package com.prj1.mysticdungeon.component

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.github.quillraven.fleks.Entity

class TiledComponent {
    lateinit var cell: Cell
    var nearByEntities = mutableSetOf<Entity>()
}