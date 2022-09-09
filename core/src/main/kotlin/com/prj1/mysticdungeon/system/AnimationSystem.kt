package com.prj1.mysticdungeon.system

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.prj1.mysticdungeon.component.AnimationComponent
import com.prj1.mysticdungeon.component.AnimationComponent.Companion.NO_ANIMATION
import com.prj1.mysticdungeon.component.ImageComponent
import ktx.app.gdxError
import ktx.log.logger
import ktx.collections.map

@AllOf([AnimationComponent::class, ImageComponent::class])
class AnimationSystem (
    private var textureAtlas: TextureAtlas,
    private var animationCmps: ComponentMapper<AnimationComponent>,
    private var imageCmps: ComponentMapper<ImageComponent>
) : IteratingSystem(){
    private var cacherAnimation = mutableMapOf<String,Animation<TextureRegionDrawable>>()

    override fun onTickEntity(entity: Entity) {
        var aniCmp = animationCmps[entity]

        if (aniCmp.nextAnimation == NO_ANIMATION){
            aniCmp.stateTime += deltaTime
        } else {
            aniCmp.animation = animation(aniCmp.nextAnimation)
            aniCmp.stateTime = 0f
            aniCmp.nextAnimation = NO_ANIMATION
        }

        aniCmp.animation.playMode = aniCmp.playMode
        imageCmps[entity].image.drawable  = aniCmp.animation.getKeyFrame(aniCmp.stateTime)
    }

    private fun animation(aniKeyPath: String): Animation<TextureRegionDrawable> {
        return cacherAnimation.getOrPut(aniKeyPath){
            log.debug {"New animation is created for '$aniKeyPath'"}
            val regions = textureAtlas.findRegions(aniKeyPath)
            if (regions.isEmpty){
                gdxError("No texture region for $aniKeyPath")
            }
            Animation(1/8f, regions.map { TextureRegionDrawable(it) })
        }
    }

    companion object {
        private val log = logger<AnimationSystem>()
        private const val DEFAULT_FRAME_DURATION = 1/8f
    }
}