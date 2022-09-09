package com.prj1.mysticdungeon.android.screen

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.app.KtxScreen
import ktx.log.logger

class GameScreen : KtxScreen{
    private var stage: Stage = Stage(ExtendViewport(16f,9f))
    private var texture: Texture = Texture("char_spritesheet.png")

    override fun show() {
        log.debug { "Game Screen get shown" }
        stage.addActor(
            Image(texture).apply {
                setPosition(1f, 1f)
                setSize(1f, 1f)
                setScaling(Scaling.fill)
            }
        )
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        with(stage){
            act(delta)
            draw()
        }
    }

    override fun dispose() {
        stage.dispose()
        texture.dispose()
    }

    companion object{
        private val log = logger<GameScreen>()
    }
}