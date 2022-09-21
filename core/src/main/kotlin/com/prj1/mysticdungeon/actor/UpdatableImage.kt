package com.prj1.mysticdungeon.actor

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable
import com.prj1.mysticdungeon.component.DirectionType
import ktx.app.gdxError
import ktx.math.vec2

class UpdatableImage: Image() {
    var isAttacking = false
    var size = vec2()
    var dir = DirectionType.NONE

    override fun draw(batch: Batch, parentAlpha: Float) {
        validate()

        val color = color
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)

        val x = x
        val y = y
        val scaleX = scaleX
        val scaleY = scaleY

        if (drawable is TransformDrawable) {
            val rotation = rotation
            if (scaleX != 1f || scaleY != 1f || rotation != 0f) {
                (drawable as TransformDrawable).draw(
                    batch,
                    if (isAttacking){
                        val a : Float
                        when (dir) {
                            DirectionType.LEFT -> {
                                a =  x+ imageX - imageWidth
                            }
                            DirectionType.RIGHT -> {
                                a =  x+ imageX
                            }
                            DirectionType.UP -> {
                                a = x+ imageX - imageHeight
                            }
                            DirectionType.DOWN -> {
                                a = x+ imageX - imageHeight
                            }
                            DirectionType.NONE -> {
                                gdxError("Direction is none while attacking")
                            }
                        }
                        a
                    } else x+ imageX,
                    if (isAttacking){
                        val b : Float
                        when (dir) {
                            DirectionType.LEFT -> {
                                b =  y + imageY - imageWidth
                            }
                            DirectionType.RIGHT -> {
                                b =  y + imageY - imageWidth
                            }
                            DirectionType.UP -> {
                                b = y + imageY
                            }
                            DirectionType.DOWN -> {
                                b = y + imageY - imageHeight
                            }
                            DirectionType.NONE -> {
                                gdxError("Direction is none while attacking")
                            }
                        }
                        b
                    } else y + imageY,
                    originX - imageX,
                    originY - imageY,
                    if (isAttacking) size.x else imageWidth,
                    if (isAttacking) size.y else imageHeight,
                    scaleX,
                    scaleY,
                    rotation
                )
                return
            }
        }
        if (drawable != null) drawable.draw(
            batch,
            if (isAttacking){
                val a : Float
                when (dir) {
                    DirectionType.LEFT -> {
                        a =  x+ imageX - imageWidth
                    }
                    DirectionType.RIGHT -> {
                        a =  x+ imageX
                    }
                    DirectionType.UP -> {
                        a = x+ imageX - imageHeight
                    }
                    DirectionType.DOWN -> {
                        a = x+ imageX - imageHeight
                    }
                    DirectionType.NONE -> {
                        gdxError("Direction is none while attacking")
                    }
                }
                a
            } else x+ imageX,
            if (isAttacking){
                val b : Float
                when (dir) {
                    DirectionType.LEFT -> {
                        b =  y + imageY - imageWidth
                    }
                    DirectionType.RIGHT -> {
                        b =  y + imageY - imageWidth
                    }
                    DirectionType.UP -> {
                        b = y + imageY
                    }
                    DirectionType.DOWN -> {
                        b = y + imageY - imageHeight
                    }
                    DirectionType.NONE -> {
                        gdxError("Direction is none while attacking")
                    }
                }
                b
            } else y + imageY,
            if (isAttacking) size.x * scaleX else imageWidth * scaleX,
            if (isAttacking) size.y * scaleY else imageHeight * scaleY
        )
    }
}