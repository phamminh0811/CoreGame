package com.prj1.mysticdungeon.system


import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.prj1.mysticdungeon.MysticDungeon.Companion.UNIT_SCALE
import com.prj1.mysticdungeon.component.*
import com.prj1.mysticdungeon.component.PhysicComponent.Companion.physicCmpFromImage
import com.prj1.mysticdungeon.event.MapChangeEvent
import ktx.app.gdxError
import ktx.box2d.box
import ktx.math.vec2
import ktx.tiled.layer
import ktx.tiled.type
import ktx.tiled.x
import ktx.tiled.y

@AllOf([SpawnComponent::class])
class EntitySpawnSystem(
    private val phWorld: World,
    private val atlas: TextureAtlas,
    private val spawnCmps: ComponentMapper<SpawnComponent>
): EventListener, IteratingSystem() {
    private val cachedCfgs = mutableMapOf<String, SpawnCfg>()
    private val cachedSizes = mutableMapOf<AnimationModel, Vector2>()

    override fun onTickEntity(entity: Entity) {
        with(spawnCmps[entity]){
            val cfg = spawnCfg(type)
            val relativeSize = size(cfg.model)

            world.entity {
                val imageCmp = add<ImageComponent>{
                    image = Image().apply {
                        setScaling(Scaling.fill)
                        setSize(relativeSize.x, relativeSize.y)
                        setPosition(location.x, location.y)
                    }
                }

                add<AnimationComponent>{
                    nextAnimation(cfg.model, AnimationType.IDLE, DirectionType.LEFT)
                }

                physicCmpFromImage(phWorld, imageCmp.image, DynamicBody){ phCmp, width, height ->
                    val w = width * cfg.physicScaling.x
                    val h = height * cfg.physicScaling.y

                    box(width, height){
                        isSensor = cfg.bodyType != StaticBody
                    }

                    if (cfg.bodyType != StaticBody){
                        // collision box
                        val colH = h*0.4f
                        val collOffset = vec2().apply { set(cfg.physicOffset) }
                        collOffset.y -= h*0.5f - colH * 0.5f
                        box(w,colH, collOffset)
                    }
                }

                if (cfg.speedScaling > 0) {
                    add<MoveComponent> {
                        speed = DEFAULT_SPEED * cfg.speedScaling
                    }
                }

                if (type == "CHAR"){
                    add<PlayerComponent>()
                }

                if (cfg.bodyType != StaticBody){
                    add<CollisionComponent>()
                }
            }
            world.remove(entity)
        }
    }

    private fun size(model: AnimationModel): Vector2 = cachedSizes.getOrPut(model) {
        val regions = atlas.findRegions("${model.atlasKey}_${AnimationType.IDLE.atlasKey}_${DirectionType.LEFT.atlasKey}")
        if (regions.isEmpty){
            gdxError("There are no regions for the idle animation of model $model")
        }

        val firstFrame = regions.first()
        vec2(firstFrame.originalWidth * UNIT_SCALE, firstFrame.originalHeight * UNIT_SCALE)
    }

    private fun spawnCfg(type: String): SpawnCfg = cachedCfgs.getOrPut(type) {
        when (type) {
            "CHAR" -> SpawnCfg(AnimationModel.CHAR)
            "PHANTOM" -> SpawnCfg(AnimationModel.PHANTOM)
            else -> gdxError("Type $type has no SpawnCfg setup")
        }
    }

    override fun handle(event: Event?): Boolean {
        when (event){
            is MapChangeEvent -> {
                val entityLayer = event.map.layer("Entity")
                entityLayer.objects.forEach{mapObject ->
                    val type = mapObject.type ?: gdxError("Map object $mapObject does not have a type!")
                    world.entity {
                        add<SpawnComponent>{
                            this.type = type
                            this.location.set(mapObject.x * UNIT_SCALE, mapObject.y * UNIT_SCALE)
                        }
                    }
                }
                return true
            }
        }

        return false
    }
}