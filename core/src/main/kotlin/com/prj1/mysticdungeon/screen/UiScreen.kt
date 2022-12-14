package com.prj1.mysticdungeon.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.app.KtxScreen

class UiScreen: KtxScreen {
    private val stage: Stage = Stage(ExtendViewport(180f, 320f))

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun show() {
        stage.clear()
    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)){
            hide()
            show()
        }
        stage.act()
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }
}