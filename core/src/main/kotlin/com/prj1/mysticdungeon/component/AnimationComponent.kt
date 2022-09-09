package com.prj1.mysticdungeon.component

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

enum class AnimationModel{
    CHAR, PHANTOM, UNDEFINED;

    var atlasKey: String = this.toString().lowercase()
}

enum class AnimationType{
    IDLE, RUN, HIT, ATTACK, CLIMB, FALLING, DEATH, SHIELD_HIT, SHIELDED_STATIC;

    var atlasKey: String = this.toString().lowercase()
}

enum class DirectionType{
    UP, DOWN, LEFT, RIGHT, NONE;

    var atlasKey: String = this.toString().lowercase()
}

data class AnimationComponent(
    var model: AnimationModel = AnimationModel.UNDEFINED,
    var stateTime: Float = 0f,
    var playMode: Animation.PlayMode = Animation.PlayMode.LOOP
) {
    lateinit var animation: Animation<TextureRegionDrawable>
    var nextAnimation: String = NO_ANIMATION

    fun nextAnimation(model: AnimationModel, type: AnimationType, dir: DirectionType){
        this.model = model
        nextAnimation = if (dir == DirectionType.NONE){
            "${model.atlasKey}_${type.atlasKey}"
        } else {
            "${model.atlasKey}_${type.atlasKey}_${dir.atlasKey}"
        }
    }

    companion object{
        const val NO_ANIMATION = ""
    }
}