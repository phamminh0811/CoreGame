package com.prj1.mysticdungeon.system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.*
import com.github.quillraven.fleks.collection.compareEntity
import com.prj1.mysticdungeon.MysticDungeon.Companion.UNIT_SCALE
import com.prj1.mysticdungeon.component.ImageComponent
import com.prj1.mysticdungeon.event.MapChangeEvent
import ktx.graphics.use
import ktx.tiled.forEachLayer

@AllOf([ImageComponent::class])
class RenderSystem (
    private var gameStage: Stage,
    @Qualifier("uiStage") private var uiStage: Stage,
    private var imageCmps: ComponentMapper<ImageComponent>
    ): EventListener, IteratingSystem(
    comparator = compareEntity { e1, e2 ->  imageCmps[e1].compareTo(imageCmps[e2])}
        ){

    private var gateLayer = mutableListOf<TiledMapTileLayer>()
    private var fgdLayers = mutableListOf<TiledMapTileLayer>()
    private var bgdLayers = mutableListOf<TiledMapTileLayer>()
    private var mapRenderer = OrthogonalTiledMapRenderer(null, UNIT_SCALE, gameStage.batch)
    private var orthoCam = gameStage.camera as OrthographicCamera

    override fun onTick() {
        super.onTick()

        with(gameStage){
            viewport.apply()

            AnimatedTiledMapTile.updateAnimationBaseTime()
            mapRenderer.setView(orthoCam)

            if (bgdLayers.isNotEmpty()){
                gameStage.batch.use(orthoCam.combined){
                    bgdLayers.forEach{ mapRenderer.renderTileLayer(it)}
                }
            }

            act(deltaTime)
            draw()

            if (fgdLayers.isNotEmpty()){
                gameStage.batch.use(orthoCam.combined){
                    fgdLayers.forEach{ mapRenderer.renderTileLayer(it)}
                }
            }

            if (gateLayer.isNotEmpty()){
                gameStage.batch.use(orthoCam.combined){
                    gateLayer.forEach{ mapRenderer.renderTileLayer(it)}
                }
            }
        }

//        render UI
        with(uiStage){
            viewport.apply()
            act(deltaTime)
            draw()
        }
    }

    override fun onTickEntity(entity: Entity) {
        imageCmps[entity].image?.toFront()
    }

    override fun handle(event: Event?): Boolean {
        when (event) {
            is MapChangeEvent -> {
                bgdLayers.clear()
                gateLayer.clear()
                fgdLayers.clear()

                event.map.forEachLayer<TiledMapTileLayer> { layer ->
                    if (layer.name.startsWith("Ground")){
                        bgdLayers.add(layer)
                    } else if (layer.name.startsWith("Gate")){
                        gateLayer.add(layer)
                    } else {
                        fgdLayers.add(layer)
                    }
                }

                return true
            }
        }
        return false
    }

    override fun onDispose() {
        mapRenderer.dispose()
    }
}