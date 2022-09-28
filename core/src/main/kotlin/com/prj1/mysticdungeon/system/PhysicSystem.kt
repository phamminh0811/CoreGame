package com.prj1.mysticdungeon.system

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.*
import com.badlogic.gdx.physics.box2d.World
import com.github.quillraven.fleks.*
import com.prj1.mysticdungeon.component.*
import com.prj1.mysticdungeon.system.EntitySpawnSystem.Companion.AI_SENSOR
import ktx.log.logger
import ktx.math.component1
import ktx.math.component2

val Fixture.entity: Entity
    get() = this.body.userData as Entity

@AllOf([PhysicComponent::class, ImageComponent::class])
class PhysicSystem(
    private val phWorld: World,
    private val physicCmps: ComponentMapper<PhysicComponent>,
    private val tiledCmps: ComponentMapper<TiledComponent>,
    private val collisionCmps: ComponentMapper<CollisionComponent>,
    private val imageCmps: ComponentMapper<ImageComponent>,
    private val aiCmps: ComponentMapper<AiComponent>
): ContactListener, IteratingSystem(interval = Fixed(1/60f)) {
    init {
        phWorld.setContactListener(this)
    }

    override fun onUpdate() {
        if (phWorld.autoClearForces){
            LOG.error { "Auto clear forces must be set to false to guarantee a correct physic simulation." }
            phWorld.autoClearForces = false
        }
        super.onUpdate()
        phWorld.clearForces()
    }

    override fun onTick() {
        super.onTick()
        phWorld.step(deltaTime, 6, 2)
    }

    override fun onTickEntity(entity: Entity) {
        val physicCmp = physicCmps[entity]

        physicCmp.prevPos.set(physicCmp.body.position)

        if (!physicCmp.impulse.isZero){
            physicCmp.body.applyLinearImpulse(physicCmp.impulse, physicCmp.body.worldCenter, true)
            physicCmp.impulse.setZero()
        }
    }

    override fun onAlphaEntity(entity: Entity, alpha: Float) {
        val imageCmp = imageCmps[entity]
        val physicCmp = physicCmps[entity]

        val (preX, preY) = physicCmp.prevPos

//        center of box
        val (bodyX, bodyY) = physicCmp.body.position

//        bottom left of box
        imageCmp.image?.run{
            setPosition(
                MathUtils.lerp(preX, bodyX, alpha) - width * 0.5f ,
                MathUtils.lerp(preY, bodyY, alpha) - height * 0.5f)
        }
    }
    companion object{
        private val LOG = logger<PhysicSystem>()
    }

    override fun beginContact(contact: Contact) {
        val entityA: Entity = contact.fixtureA.entity
        val entityB: Entity = contact.fixtureB.entity
        val isEntityATiledCollisionSensor = entityA in tiledCmps && contact.fixtureA.isSensor
        val isEntityACollsionFixture = entityA in collisionCmps && !contact.fixtureA.isSensor
        val isEntityBTiledCollisionSensor = entityB in tiledCmps && contact.fixtureB.isSensor
        val isEntityBCollsionFixture = entityB in collisionCmps && !contact.fixtureB.isSensor
        val isEntityAAiSensor = entityA in aiCmps && contact.fixtureA.isSensor && contact.fixtureA.userData == AI_SENSOR
        val isEntityBAiSensor = entityB in aiCmps && contact.fixtureB.isSensor && contact.fixtureB.userData == AI_SENSOR

        when {
            isEntityATiledCollisionSensor && isEntityBCollsionFixture-> {
                tiledCmps[entityA].nearByEntities += entityB
            }

            isEntityBTiledCollisionSensor && isEntityACollsionFixture -> {
                tiledCmps[entityB].nearByEntities += entityA
            }

            isEntityAAiSensor && isEntityBCollsionFixture -> {
                aiCmps[entityA].nearByEntities += entityB
            }
            isEntityBAiSensor && isEntityACollsionFixture -> {
                aiCmps[entityB].nearByEntities += entityA
            }
        }
    }

    override fun endContact(contact: Contact) {
        val entityA: Entity = contact.fixtureA.entity
        val entityB: Entity = contact.fixtureB.entity
        val isEntityATiledCollisionSensor = entityA in tiledCmps && contact.fixtureA.isSensor
        val isEntityBTiledCollisionSensor = entityB in tiledCmps && contact.fixtureB.isSensor
        val isEntityAAiSensor = entityA in aiCmps && contact.fixtureA.isSensor && contact.fixtureA.userData == AI_SENSOR
        val isEntityBAiSensor = entityB in aiCmps && contact.fixtureB.isSensor && contact.fixtureB.userData == AI_SENSOR

        when {
            isEntityATiledCollisionSensor && !contact.fixtureB.isSensor-> {
                tiledCmps[entityA].nearByEntities -= entityB
            }

            isEntityBTiledCollisionSensor && !contact.fixtureA.isSensor -> {
                tiledCmps[entityB].nearByEntities -= entityA
            }

            isEntityAAiSensor && !contact.fixtureB.isSensor -> {
                aiCmps[entityA].nearByEntities -= entityB
            }
            isEntityBAiSensor && !contact.fixtureA.isSensor -> {
                aiCmps[entityB].nearByEntities -= entityA
            }
        }
    }

    private fun Fixture.isStaticBody() = this.body.type == StaticBody

    private fun Fixture.isDynamicBody() = this.body.type == DynamicBody

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
        contact.isEnabled = (contact.fixtureA.isDynamicBody()
                && contact.fixtureB.isStaticBody())
                || (contact.fixtureA.isStaticBody()
                && contact.fixtureB.isDynamicBody())
    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) = Unit
}