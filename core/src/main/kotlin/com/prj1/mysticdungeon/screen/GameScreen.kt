package com.prj1.mysticdungeon.screen

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.quillraven.fleks.world
import com.prj1.mysticdungeon.component.ImageComponent.Companion.ImageComponentListener
import com.prj1.mysticdungeon.component.PhysicComponent.Companion.PhysicComponentListener
import com.prj1.mysticdungeon.event.MapChangeEvent
import com.prj1.mysticdungeon.event.fire
import com.prj1.mysticdungeon.input.PlayerKeyboardInputProcessor
import com.prj1.mysticdungeon.system.*
import ktx.app.KtxScreen
import ktx.box2d.createWorld
import ktx.log.logger
import ktx.math.vec2

class GameScreen : KtxScreen{
    private var stage: Stage = Stage(ExtendViewport(16f,9f))
    private var textureAtlas =  TextureAtlas("graphics/gameObject.atlas")
    private var currentMap: TiledMap? = null

    private val phWorld = createWorld(gravity = vec2()).apply {
        autoClearForces = false
    }

    private var eWorld = world {
        injectables {
            add(phWorld)
            add(stage)
            add(textureAtlas)
        }

        components {
            add<PhysicComponentListener>()
            add<ImageComponentListener>()
        }

        systems {
            add<EntitySpawnSystem>()
            add<CollisionSpawnSystem>()
            add<CollisionDespawnSystem>()
            add<PhysicSystem>()
            add<AnimationSystem>()
            add<MoveSystem>()
            add<AttackSystem>()
            add<DeadSystem>()
            add<LifeSystem>()
            add<CameraSystem>()
            add<RenderSystem>()
            add<DebugSystem>()
        }
    }

    override fun show() {
        log.debug { "Game Screen get shown" }

        eWorld.systems.forEach { system ->
            if (system is EventListener) {
                stage.addListener(system)
            }
        }

        val currentMap = TmxMapLoader().load("map/map1.tmx")
        stage.fire(MapChangeEvent(currentMap!!))

        PlayerKeyboardInputProcessor(eWorld)
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        eWorld.update(delta.coerceAtMost(0.25f))
    }

    override fun dispose() {
        stage.dispose()
        textureAtlas.dispose()
        eWorld.dispose()
        currentMap?.dispose()
        phWorld.dispose()
    }

    companion object{
        private val log = logger<GameScreen>()
    }
}