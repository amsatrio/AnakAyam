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


class GameTrueEnding(private val game: AnakAyam) : Screen {
    private val orthographicCamera: OrthographicCamera = OrthographicCamera()
    private val viewPort: Viewport

    private var stage: Stage
    private var table: Table

    private var congratLabel1: Label
    private var congratLabel2: Label
    private var highScoreLabel: Label
    private var latestScoreLabel: Label
    private var backLabel: Label
    private var restartLabel: Label
    private var emptyLabel: Label

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
        //draw here
        game.batch!!.draw(texture,0f,0f,orthographicCamera.viewportWidth,orthographicCamera.viewportHeight)
        game.batch!!.end()

        //stage
        game.batch!!.projectionMatrix = stage.camera.combined
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

    }

    init {
        //mod Font
        bitmapFont1.data.setScale(game.fontSize1)
        //mod Font
        bitmapFont2 = BitmapFont()
        bitmapFont2.data.setScale(game.fontSize2)

        //load default pref
        val highScore = configSharedPreferences.getInteger("highScore",0)
        val latestScore = configSharedPreferences.getInteger("latestScore",0)

        //viewport
        viewPort = FitViewport((game.screenWidth*100).toFloat(), (game.screenHeight*100).toFloat(), orthographicCamera)

        //label
        highScoreLabel = Label("HIGH SCORE: $highScore", Label.LabelStyle(bitmapFont1, Color.WHITE))
        latestScoreLabel = Label("YOUR SCORE: $latestScore", Label.LabelStyle(bitmapFont2, Color.WHITE))
        backLabel = Label("BACK TO MAIN MENU", Label.LabelStyle(bitmapFont1, Color.WHITE))
        restartLabel = Label("RESTART", Label.LabelStyle(bitmapFont1, Color.WHITE))
        emptyLabel = Label("", Label.LabelStyle(bitmapFont1, Color.WHITE))

        congratLabel1 = Label("Congratulation!!", Label.LabelStyle(bitmapFont2, Color.WHITE))
        congratLabel2 = Label("You got the Highest Score", Label.LabelStyle(bitmapFont2, Color.WHITE))

        //initialize Stage and Table Menu
        stage = Stage(viewPort)
        table = Table()
        table.top
        table.setFillParent(true)

        //add label to table
        table.add(congratLabel1).expandX().padTop((0f))
        table.row()
        table.add(congratLabel2).expandX().padTop((0f))
        table.row()
        table.add(latestScoreLabel).expandX().padTop((20f))
        table.row()
        table.add(highScoreLabel).expandX().padTop((0f))
        table.row()
        table.add(emptyLabel).expandX().padTop((40f))
        table.row()
        table.add(backLabel).expandX().padTop((40f))
        table.row()
        table.add(restartLabel).expandX().padTop((40f))

        //add table to stage
        stage.addActor(table)

        //input menu
        Gdx.input.inputProcessor = stage
        backLabel.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                game.screen = GameMenuScreen(game)
                dispose()
                return true
            }
        })
        restartLabel.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                game.screen = GamePlayScreen(game)
                dispose()
                return true
            }
        })

    }
}