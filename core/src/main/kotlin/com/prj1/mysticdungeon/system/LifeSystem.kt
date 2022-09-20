package com.prj1.mysticdungeon.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.github.quillraven.fleks.*
import com.prj1.mysticdungeon.component.DeadComponent
import com.prj1.mysticdungeon.component.FloatingTextComponent
import com.prj1.mysticdungeon.component.LifeComponent
import com.prj1.mysticdungeon.component.PhysicComponent
import kotlin.math.roundToInt

@AllOf([LifeComponent::class])
@NoneOf([DeadComponent::class])
class LifeSystem(
    private val lifeCmps: ComponentMapper<LifeComponent>,
    private val deadCmps: ComponentMapper<DeadComponent>,
    private val physicCmps: ComponentMapper<PhysicComponent>,
) : IteratingSystem(){
    private val damageFont = BitmapFont(Gdx.files.internal("damage.fnt"))
    private val floatingTextStyle = LabelStyle(damageFont, Color.WHITE)
    override fun onTickEntity(entity: Entity) {
        val lifeCmp = lifeCmps[entity]
        lifeCmp.life = (lifeCmp.life + lifeCmp.regeneration * deltaTime).coerceAtMost(lifeCmp.max)

        if (lifeCmp.takeDamage > 0f){
            val physicCmp = physicCmps[entity]
            lifeCmp.life -= lifeCmp.takeDamage
            floatingText("-"  + ((lifeCmp.takeDamage * 10).roundToInt().toFloat() /10f).toString() + " HP", physicCmp.body.position, physicCmp.size)
            lifeCmp.takeDamage = 0f
        }

        if (lifeCmp.isDead){
            configureEntity(entity){
                deadCmps.add(it)
            }
        }
    }

    private fun floatingText(text: String, position: Vector2, size: Vector2) {
        world.entity {
            add<FloatingTextComponent>{
                txtLocation.set(position.x, position.y - size.y *0.5f)
                lifeSpan = 1.5f
                label = Label(text, floatingTextStyle)
            }
        }
    }

    override fun onDispose() {
        damageFont.dispose()
    }
}