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

package com.ams64.anakayam.sprites

import com.ams64.anakayam.screen.GamePlayScreen
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array
import kotlin.experimental.or

class GameCharacter(private val gamePlayScreen: GamePlayScreen): Sprite(gamePlayScreen.getTextureAtlasCharacter().findRegion("run_right")) {
    private var world: World = gamePlayScreen.getWorld()
    lateinit var body: Body

    enum class State {FALLING, JUMPING, STANDING, RUNNING}
    private var currentState: State
    private var previousState: State
    private var animationRun: Animation<TextureRegion>
    private var animationJump: Animation<TextureRegion>
    private var animationStand: TextureRegion
    private var stateTimer: Float
    private val scaleObjectXY: Float = 4.8f

    init {
        defineCharacter()

        //initialize animation
        currentState = State.RUNNING
        previousState = State.RUNNING
        stateTimer = 0F
        val frames: Array<TextureRegion> = Array()

        //animation stand
        animationStand = TextureRegion(texture, 0, 0, 32, 32)

        //scale the image
        setBounds(0F, 0F, 32*scaleObjectXY*gamePlayScreen.getScalePixel().toFloat(), 32*scaleObjectXY*gamePlayScreen.getScalePixel().toFloat())
        setRegion(animationStand)

        //animation running
        for(i in 0..2){
            frames.add(TextureRegion(texture, i*32, 0, 32, 32))
        }
        frames.add(TextureRegion(texture, 0, 0, 32, 32))
        animationRun = Animation(0.1F, frames)
        frames.clear()

        //animation jump
        frames.add(TextureRegion(texture, 0, 0, 32, 32))
        animationJump = Animation(0.1F, frames)
        frames.clear()
    }

    private fun defineCharacter() {
        val bodyDef = BodyDef()
        bodyDef.position.set(8F*gamePlayScreen.getScalePixel().toFloat(), 128F*gamePlayScreen.getScalePixel().toFloat())
        bodyDef.type = BodyDef.BodyType.DynamicBody

        body = world.createBody(bodyDef)

        val fixtureDef = FixtureDef()
        val polygonShape = CircleShape()
        polygonShape.radius=(12*gamePlayScreen.getScalePixel()*scaleObjectXY).toFloat()
        //polygonShape.setAsBox(6*gamePlayScreen.getScalePixel().toFloat(),12*gamePlayScreen.getScalePixel().toFloat())
        fixtureDef.filter.categoryBits = gamePlayScreen.game.gameCharacterBit
        fixtureDef.filter.maskBits = gamePlayScreen.game.gameObstacleBit or gamePlayScreen.game.gameGroundBit

        fixtureDef.shape = polygonShape
        body.createFixture(fixtureDef)
    }

    fun update(deltaTime: Float){
        setPosition((body.position.x- width /2),(body.position.y- height /2))
        setRegion(getFrame(deltaTime))
    }

    private fun getFrame(deltaTime: Float): TextureRegion {
        currentState = when {
            body.linearVelocity.y > 0 -> {
                State.JUMPING
            }
            body.linearVelocity.y < 0 -> {
                State.FALLING
            }
            body.linearVelocity.x == 0f -> {
                State.RUNNING
            }
            else -> {
                State.RUNNING
            }
        }

        val textureRegion: TextureRegion = when (currentState) {
            State.JUMPING -> animationJump.getKeyFrame(stateTimer)
            State.RUNNING -> animationRun.getKeyFrame(stateTimer, true)
            State.FALLING, State.STANDING -> animationStand
        }

        stateTimer = if (currentState === previousState) stateTimer + deltaTime else 0.toFloat()
        previousState = currentState

        return textureRegion
    }
}
