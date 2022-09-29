package com.prj1.mysticdungeon.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.quillraven.fleks.world
import com.prj1.mysticdungeon.MysticDungeon
import com.prj1.mysticdungeon.component.AiComponent
import com.prj1.mysticdungeon.component.AiComponent.Companion.AiComponentListener
import com.prj1.mysticdungeon.component.FloatingTextComponent.Companion.FloatingTextComponentListener
import com.prj1.mysticdungeon.component.ImageComponent.Companion.ImageComponentListener
import com.prj1.mysticdungeon.component.PhysicComponent.Companion.PhysicComponentListener
import com.prj1.mysticdungeon.component.StateComponent.Companion.StateComponentListener
import com.prj1.mysticdungeon.event.MapChangeEvent
import com.prj1.mysticdungeon.event.fire
import com.prj1.mysticdungeon.input.PlayerKeyboardInputProcessor
import com.prj1.mysticdungeon.system.*
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.assets.disposeSafely
import ktx.box2d.createWorld
import ktx.log.logger
import ktx.math.vec2
import ktx.scene2d.actors
import ktx.scene2d.label
import ktx.scene2d.stack
import ktx.scene2d.table

class GameScreen() : KtxScreen{

    private var gameStage: Stage = Stage(ExtendViewport(16f,9f))
    private var uiStage: Stage = Stage(ExtendViewport(1280f, 720f))
    private var textureAtlas =  TextureAtlas("graphics/gameObject.atlas")
    private var currentMap: TiledMap? = null

    private val phWorld = createWorld(gravity = vec2()).apply {
        autoClearForces = false
    }

    private var eWorld = world {
        injectables {
            add(phWorld)
            add(gameStage)
            add("uiStage",uiStage)
            add(textureAtlas)
        }

        components {
            add<PhysicComponentListener>()
            add<ImageComponentListener>()
            add<FloatingTextComponentListener>()
            add<StateComponentListener>()
            add< AiComponentListener>()
        }

        systems {
            add<EntitySpawnSystem>()
            add<CollisionSpawnSystem>()
            add<CollisionDespawnSystem>()
            add<MoveSystem>()
            add<AttackSystem>()
            add<DeadSystem>()
            add<LifeSystem>()
            add<PhysicSystem>()
            add<AnimationSystem>()
            add<StateSystem>()
            add<AiSystem>()
            add<LootSystem>()
            add<CameraSystem>()
            add<FloatingTextSystem>()
            add<RenderSystem>()
            add<DebugSystem>()
        }
    }

    override fun show() {
        log.debug { "Game Screen get shown" }

        eWorld.systems.forEach { system ->
            if (system is EventListener) {
                gameStage.addListener(system)
            }
        }

        val currentMap = TmxMapLoader().load("map/map1.tmx")
        gameStage.fire(MapChangeEvent(currentMap!!))

        PlayerKeyboardInputProcessor(eWorld)
    }


    override fun resize(width: Int, height: Int) {
        gameStage.viewport.update(width, height, true)
        uiStage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        val dt = delta.coerceAtMost(0.25f)
        GdxAI.getTimepiece().update(dt)
        eWorld.update(dt)
    }

    override fun dispose() {
        gameStage.disposeSafely()
        uiStage.disposeSafely()
        textureAtlas.disposeSafely()
        eWorld.dispose()
        currentMap?.disposeSafely()
        phWorld.disposeSafely()
    }

    companion object{
        private val log = logger<GameScreen>()
    }
}