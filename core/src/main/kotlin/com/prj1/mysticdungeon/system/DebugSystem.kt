package com.prj1.mysticdungeon.system

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.IntervalSystem
import com.prj1.mysticdungeon.system.AttackSystem.Companion.FRONT_RECT
import ktx.graphics.use

class DebugSystem(
    private val phWorld: World,
    private val stage: Stage
): IntervalSystem(enabled = true) {
    private lateinit var physicRenderer: Box2DDebugRenderer
    private lateinit var shapeRenderer: ShapeRenderer

    init {
        if (enabled){
            physicRenderer = Box2DDebugRenderer()
            shapeRenderer = ShapeRenderer()
        }
    }
    override fun onTick() {
        physicRenderer.render(phWorld, stage.camera.combined)
        shapeRenderer.use(ShapeRenderer.ShapeType.Line, stage.camera.combined){
            it.setColor(1f, 0f, 0f, 0f)
            it.rect(FRONT_RECT.x, FRONT_RECT.y, FRONT_RECT.width - FRONT_RECT.x, FRONT_RECT.height - FRONT_RECT.y)
        }
    }

    override fun onDispose() {
        if (enabled){
            physicRenderer.dispose()
            shapeRenderer.dispose()
        }
    }
}