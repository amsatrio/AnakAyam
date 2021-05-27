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
import com.badlogic.gdx.Preferences
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

class GamePreferencesScreen(private val game: AnakAyam) : Screen {
    private val orthographicCamera: OrthographicCamera = OrthographicCamera()
    private val viewPort: Viewport

    private var stage: Stage
    private var table: Table

    private var musicLabel: Label
    private var defaultLabel: Label
    private var cancelLabel: Label
    private var okLabel: Label

    private var bitmapFont1: BitmapFont = BitmapFont()
    private var bitmapFont2: BitmapFont

    private var texture: Texture = Texture("textures/main_background.png")

    private var configSharedPreferences: Preferences = Gdx.app.getPreferences("AnakAyamConfig")

    override fun show() {}
    private fun inputHandling() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            game.screen = GamePlayScreen(game)
            dispose()
        }
    }

    override fun render(delta: Float) {
        inputHandling()
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        game.batch!!.projectionMatrix = orthographicCamera.combined
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
        //GameSound().dispose()
    }

    init {
        bitmapFont1.data.setScale(game.fontSize1)
        //mod Font
        bitmapFont2 = BitmapFont()
        bitmapFont2.data.setScale(game.fontSize2)
        //load default pref
        var musicString = configSharedPreferences.getString("preferencesStringMusic","ON")

        //viewport
        viewPort = FitViewport((game.screenWidth*100).toFloat(), (game.screenHeight*100).toFloat(), orthographicCamera)

        //label
        defaultLabel = Label("DEFAULT", Label.LabelStyle(bitmapFont2, Color.WHITE))
        musicLabel = Label("MUSIC: $musicString", Label.LabelStyle(bitmapFont2, Color.WHITE))
        cancelLabel = Label("CANCEL", Label.LabelStyle(bitmapFont2, Color.WHITE))
        okLabel = Label("OK", Label.LabelStyle(bitmapFont2, Color.WHITE))

        //initialize Stage and Table Menu
        stage = Stage(viewPort)
        table = Table()
        table.top
        table.setFillParent(true)

        //add label to table
        table.add(defaultLabel).expandX().padTop((1 / 1).toFloat())
        table.row()
        table.add(musicLabel).expandX().padTop((game.screenHeight*9).toFloat())
        table.row()
        table.add(cancelLabel).expandX().padTop((game.screenHeight*9).toFloat())
        table.row()
        table.add(okLabel).expandX().padTop((game.screenHeight*9).toFloat())

        //add table to stage
        stage.addActor(table)

        //input menu
        Gdx.input.inputProcessor = stage
        defaultLabel.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {

                musicLabel.setText("MUSIC: ON")
                configSharedPreferences.putString("preferencesStringMusic","ON")
                configSharedPreferences.flush()
                return true
            }
        })
        musicLabel.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
               if(musicString == "ON"){
                   musicString = "OFF"
                   //GameSound().stop()
                } else{
                   musicString = "ON"
                   //GameSound().play()
                }
                musicLabel.setText("MUSIC: ${musicString}")
                return true
            }
        })
        cancelLabel.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                game.screen = GameMenuScreen(game)
                dispose()
                return true
            }
        })
        okLabel.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {

                configSharedPreferences.putString("preferencesStringMusic", musicString)
                configSharedPreferences.flush()

                game.screen = GameMenuScreen(game)
                dispose()
                return true
            }
        })
    }
}