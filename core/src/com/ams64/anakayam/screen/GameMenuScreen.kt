/*
 * MIT License
 *
 * Copyright (c) 2021 A M Satrio <ams64.digital@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.ams64.anakayam.screen

import com.ams64.anakayam.AnakAyam
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport

class GameMenuScreen(private val game: AnakAyam) : Screen {
    private val orthographicCamera: OrthographicCamera = OrthographicCamera()
    private val viewPort: Viewport

    private var stage: Stage
    private var table: Table

    private var preferencesLabel: Label
    private var playLabel: Label
    private var exitLabel: Label
    private var emptyLabel: Label
    private var emptyPlayLabel: Label

    private var bitmapFont1: BitmapFont = BitmapFont()

    private var texture: Texture = Texture("textures/main_background.png")

    override fun show() {}
    private fun input() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            game.screen = GamePlayScreen(game)
            dispose()
        }
    }

    override fun render(delta: Float) {
        input()
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch!!.projectionMatrix = orthographicCamera.combined
        game.batch!!.projectionMatrix = stage.camera.combined
        game.batch!!.begin()
        game.batch!!.draw(texture,0f,0f,orthographicCamera.viewportWidth,orthographicCamera.viewportHeight)
        game.batch!!.end()

        //stage
        stage.draw()
        stage.act(delta)

        //update camera
        orthographicCamera.update()
    }

    override fun resize(width: Int, height: Int) {
        viewPort.update(width, height)
    }

    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
    override fun dispose() {
        stage.dispose()
        texture.dispose()
        bitmapFont1.dispose()
    }

    init {
        Gdx.app.log("","${game.screenWidth}")
        bitmapFont1.data.setScale(game.fontSize2)

        viewPort = FitViewport((game.screenWidth*100).toFloat(), (game.screenHeight*100).toFloat(), orthographicCamera)

        //label
        playLabel = Label("PLAY", Label.LabelStyle(bitmapFont1, Color.WHITE))
        preferencesLabel = Label("SETTING", Label.LabelStyle(bitmapFont1, Color.WHITE))
        exitLabel = Label("EXIT", Label.LabelStyle(bitmapFont1, Color.WHITE))
        emptyLabel = Label("_______", Label.LabelStyle(bitmapFont1, Color.WHITE))
        emptyPlayLabel = Label("______", Label.LabelStyle(bitmapFont1, Color.WHITE))
        emptyLabel.isVisible = false
        emptyPlayLabel.isVisible=false

        //initialize Stage and Table Menu
        stage = Stage(viewPort)
        table = Table()
        table.top
        table.setFillParent(true)

        //add label to table

        table.add(emptyPlayLabel).expandX().padTop((0).toFloat())
        table.add(playLabel).expandX().padTop((-game.screenHeight).toFloat())
        table.add(emptyPlayLabel).expandX().padTop((0).toFloat())
        table.row()
        table.add(emptyLabel).expandX().padTop((0).toFloat())
        table.add(preferencesLabel).expandX().padTop((game.screenHeight*9).toFloat())
        table.add(emptyLabel).expandX().padTop((0).toFloat())
        table.row()
        table.add(emptyLabel).expandX().padTop((0).toFloat())
        table.add(exitLabel).expandX().padTop((game.screenHeight*9).toFloat())
        table.add(emptyLabel).expandX().padTop((0).toFloat())

        //add table to stage
        stage.addActor(table)

        //input menu
        Gdx.input.inputProcessor = stage
        playLabel.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                game.screen = GamePlayScreen(game)
                dispose()
                return true
            }
        })
        emptyPlayLabel.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                game.screen = GamePlayScreen(game)
                dispose()
                return true
            }
        })
        preferencesLabel.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                game.screen = GamePreferencesScreen(game)
                dispose()
                return true
            }
        })
        exitLabel.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                Gdx.app.exit()
                return true
            }
        })
    }
}