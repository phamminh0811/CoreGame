package com.prj1.mysticdungeon.component

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Shape2D
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.EntityCreateCfg
import com.prj1.mysticdungeon.MysticDungeon.Companion.UNIT_SCALE
import com.prj1.mysticdungeon.system.CollisionSpawnSystem.Companion.SPAWN_AREA_SIZE
import ktx.app.gdxError
import ktx.box2d.BodyDefinition
import ktx.box2d.body
import ktx.box2d.circle
import ktx.box2d.loop
import ktx.math.vec2

class PhysicComponent(
    val impulse: Vector2 = vec2(),
    val size: Vector2 = vec2(),
) {
    val prevPos = vec2()

    lateinit var body: Body

    companion object{
        fun EntityCreateCfg.physicCmpFromShape2D(
            world: World,
            x: Int,
            y: Int,
            shape: Shape2D
        ) : PhysicComponent {
            when (shape){
                is Rectangle -> {
                    val bodyX = x + shape.x * UNIT_SCALE
                    val bodyY = y + shape.y * UNIT_SCALE
                    val bodyW = shape.width * UNIT_SCALE
                    val bodyH = shape.height * UNIT_SCALE


                    return add{
                        body = world.body(BodyType.StaticBody) {
                            position.set(bodyX, bodyY)
                            fixedRotation = true
                            allowSleep=false
                            loop(
                                vec2(0f, 0f),
                                vec2(bodyW, 0f),
                                vec2(bodyW, bodyH),
                                vec2(0f, bodyH)
                            )

                        }
                    }
                }
                else -> gdxError("Shape $shape is not supported!")
            }
        }

        fun EntityCreateCfg.physicCmpFromImage(
            world: World,
            image: Image,
            bodyType: BodyType,
            fixtureAction: BodyDefinition.(PhysicComponent, Float, Float) -> Unit
        ): PhysicComponent {
            val x = image.x
            val y = image.y
            val w = image.width
            val h = image.height

            return add{
                body = world.body(bodyType){
                    position.set(x+ w*0.5f, y+ h*0.5f)
                    fixedRotation=true
                    allowSleep=false
                    this.fixtureAction(this@add,w, h)
                }
            }
        }

        class PhysicComponentListener : ComponentListener<PhysicComponent> {
            override fun onComponentAdded(entity: Entity, component: PhysicComponent) {
                component.body.userData = entity
            }

            override fun onComponentRemoved(entity: Entity, component: PhysicComponent) {
                val body = component.body
                body.world.destroyBody(component.body)
                body.userData = null

            }
        }
    }
}