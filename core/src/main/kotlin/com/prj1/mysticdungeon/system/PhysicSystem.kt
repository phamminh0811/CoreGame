package com.prj1.mysticdungeon.system

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.*
import com.badlogic.gdx.physics.box2d.World
import com.github.quillraven.fleks.*
import com.prj1.mysticdungeon.component.ImageComponent
import com.prj1.mysticdungeon.component.PhysicComponent
import ktx.log.logger
import ktx.math.component1
import ktx.math.component2

@AllOf([PhysicComponent::class, ImageComponent::class])
class PhysicSystem(
    private val phWorld: World,
    private val physicCmps: ComponentMapper<PhysicComponent>,
    private val imageCmps: ComponentMapper<ImageComponent>,
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
        imageCmp.image.run{
            setPosition(
                MathUtils.lerp(preX, bodyX, alpha) - width * 0.5f ,
                MathUtils.lerp(preY, bodyY, alpha) - height * 0.5f)
        }
    }
    companion object{
        private val LOG = logger<PhysicSystem>()
    }

    override fun beginContact(contact: Contact?) {

    }

    override fun endContact(contact: Contact?) {
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