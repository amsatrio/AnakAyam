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
import com.ams64.anakayam.GameContactListener
import com.ams64.anakayam.GameContactListener.Companion.isCollisionSound
import com.ams64.anakayam.GameContactListener.Companion.isGameOver
import com.ams64.anakayam.GameContactListener.Companion.isJump
import com.ams64.anakayam.GameHud
import com.ams64.anakayam.GameLoad
import com.ams64.anakayam.sprites.GameCharacter
import com.ams64.anakayam.sprites.GameCloud
import com.ams64.anakayam.sprites.GameGround
import com.ams64.anakayam.sprites.GameSmallObstacle
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport

class GamePlayScreen(val game: AnakAyam) : Screen {
    //camera game
    private val orthographicCamera: OrthographicCamera = OrthographicCamera()
    private val viewPort: Viewport

    //box2d
    private var box2dDebugRenderer: Box2DDebugRenderer
    private var world: World = World(Vector2(0F, (-2000*game.scalePixel).toFloat()), true)

    //background
    private var backgroundTexture: Texture = Texture("textures/background.png")
    private var gameCloudList: ArrayList<GameCloud> = ArrayList()
    private var cloudSpawnTime = 0f

    //animation sprites
    private var textureAtlasCharacter: TextureAtlas = TextureAtlas("textures/character.pack")
    private var gameCharacter: GameCharacter = GameCharacter(this)
    private var velocityX: Float = 400f*game.scalePixel.toFloat()

    //animation Ground
    private var gameGroundList: ArrayList<GameGround> = ArrayList()

    //animation small obstacle
    private var gameSmallObstacleList: ArrayList<GameSmallObstacle> = ArrayList()
    private var smallObstacleSpawnTime = 0f

    //HUD
    private var hud: GameHud
    private var timerScore = 0f

    //init save config
    private var configSharedPreferences: Preferences = Gdx.app.getPreferences("AnakAyamConfig")
    //load default pref
    private var musicString = configSharedPreferences.getString("preferencesStringMusic","ON")

    //Asset Manager
    private var gameLoad: GameLoad
    private var musicLoadBoolean: Boolean = false
    private var loadProgress: Float = 0f
    private lateinit var jumpSound: Sound
    private lateinit var bumpSound: Sound
    lateinit var groundTexture: Texture
    lateinit var cloudTexture: Texture
    lateinit var obstacleTexture: Texture

    private var maxScore: Int = 999999

    override fun show() {
    }

    private fun input() {
        if (gameCharacter.body.linearVelocity.y == 0f) {
            if(Gdx.input.isTouched){
                //Jump boolean
                isJump = gameCharacter.body.position.y >= gameGroundList[0].body.position.y + gameGroundList[0].getHeight()*8
                if(!isJump){
                    gameCharacter.body.applyLinearImpulse(Vector2(0f, (game.screenHeight/0.9f).toFloat()), gameCharacter.body.worldCenter, true)
                    if(gameLoad.getAssetManager().update()){
                        playJumpSound()
                    }
                }
            }
        }

    }

    override fun render(delta: Float) {
        //UPDATE SECTION------------------------------------------------------
        if(hud.getCurrentScore() >= maxScore){
            isGameOver = true
        }
        //GameOver
        if(isGameOver){
            timerScore += delta
            if(timerScore >= 1f){
                //save score
                val prefs: Preferences = Gdx.app.getPreferences("AnakAyamConfig")
                prefs.putInteger("latestScore", hud.getCurrentScore())
                if(prefs.getInteger("highScore",0) < hud.getCurrentScore()){
                    prefs.putInteger("highScore",hud.getCurrentScore())
                }
                prefs.flush()
            }
            if(timerScore >= 2f){
                isGameOver = false
                if(hud.getCurrentScore() >= maxScore){
                    game.screen = GameTrueEnding(game)
                    dispose()
                }else{
                    game.screen = GameScoreScreen(game)
                    dispose()

                }
                return
            }
            velocityX = 4f
            if(isCollisionSound && gameLoad.getAssetManager().update()){
                playBumpSound()
                isCollisionSound = false
            }

        }
        if(isGameOver){
            gameCharacter.body.isAwake = false
            gameCharacter.body.isActive = false
        }

        //world
        world.step(1 / 60F, 6, 2)

        if(!isGameOver){
            //update camera
            orthographicCamera.position.x = ((gameCharacter.body.position.x+200*game.scalePixel).toFloat())
            orthographicCamera.position.y = orthographicCamera.viewportHeight-orthographicCamera.viewportHeight/2
            orthographicCamera.update()
        }


        //update character and score
        if(!isGameOver){
            //update Score
            timerScore += delta
            if(timerScore >= 1f/5){
                hud.addScore(1)
                timerScore = 0f
            }

            //character
            gameCharacter.update(delta)

            //update velocity level
            if(hud.getCurrentScore() % 100 == 0 && hud.getCurrentScore() > 0){
                velocityX *= 101.0f*game.scalePixel.toFloat()
            }

            //gamecharacter autorun
            if(gameCharacter.body.linearVelocity.x <= velocityX){
                gameCharacter.body.applyLinearImpulse(Vector2((40*game.scalePixel).toFloat(), 0f), gameCharacter.body.worldCenter, true)
            }
        }


        //UPDATE END----------------------------------------------------

        Gdx.gl.glClearColor(1f, 1f, 1f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        //Load Asset manager-----------------------------------------------------
        if(!isGameOver){
            if(!gameLoad.getAssetManager().update()){
                gameLoad.getAssetManager().update()
                loadProgress = gameLoad.getAssetManager().progress
                gameLoad.setLoadProgress(loadProgress)
                gameLoad.stage.draw()
                return
            }else{
                if(!musicLoadBoolean){
                    bumpSound = gameLoad.getAssetManager().get("sounds/bump_short.wav", Sound::class.java)
                    jumpSound = gameLoad.getAssetManager().get("sounds/jump.wav", Sound::class.java)
                    gameLoad.stage.dispose()
                }
                musicLoadBoolean = true
                groundTexture = gameLoad.getAssetManager().get("textures/ground.png", Texture::class.java)
                cloudTexture = gameLoad.getAssetManager().get("textures/cloud.png", Texture::class.java)
                obstacleTexture = gameLoad.getAssetManager().get("textures/obstacle.png", Texture::class.java)

            }
        }


        //End Load Asset------------------------------------------------------

        //update input
        if(!isGameOver){
            input()
        }

        //RENDER SECTION----------------------------------------------------
        game.batch!!.projectionMatrix = orthographicCamera.combined
        game.batch!!.begin()

        //draw background
        game.batch!!.draw(backgroundTexture,orthographicCamera.position.x-orthographicCamera.viewportWidth,orthographicCamera.position.y-(orthographicCamera.viewportHeight/2),orthographicCamera.viewportWidth*2,orthographicCamera.viewportHeight)

        for(i in gameCloudList){
            i.render(game.batch!!)
        }

        //draw ground
        for(i in gameGroundList){
            i.render(game.batch!!)
        }

        //draw obstacle
        for(i in gameSmallObstacleList){
            i.render(game.batch!!)

            //add bonus score if obstacleX < player bodyX
            if(i.body.position.x<gameCharacter.body.position.x && i.body.position.y<gameCharacter.body.position.y && !isGameOver){
                hud.addScore(3)
            }
        }

        //draw character
        gameCharacter.draw(game.batch)

        game.batch!!.end()

        //render HUD
        game.batch!!.projectionMatrix = hud.stage.camera.combined
        hud.stage.draw()

        //RENDER END -------------------------------------------------------

        //spawn texture
        if(!isGameOver){
            spawnTexture(delta)
        }



        //show debug collision box
        //box2dDebugRenderer.render(world, orthographicCamera.combined)

    }


    private fun spawnTexture(delta: Float) {
        //SPAWN SECTION ------------------------------------------------------------------------------------
        //spawn cloud
        cloudSpawnTime += delta*(Math.random()*5).toFloat()
        if(cloudSpawnTime >= 10f){
            gameCloudList.add(GameCloud(this, (orthographicCamera.position.x + orthographicCamera.viewportWidth), orthographicCamera.viewportHeight*(0.7+(Math.random()*0.3)).toFloat()))
            cloudSpawnTime = 0f
        }


        //spawn ground
        if(gameGroundList.size == 0){
            //first ground on the list ground
            gameGroundList.add(GameGround(this, orthographicCamera.position.x-orthographicCamera.viewportWidth/2, 0f))
        }
        for(i in gameGroundList){
            if(gameGroundList.size==1){
                if((i.body.position.x+i.getWidth()/2*game.scalePixel) < (orthographicCamera.position.x+orthographicCamera.viewportWidth)){
                    gameGroundList.add(GameGround(this, (i.body.position.x + i.getWidth()/2 -0.01f), 0f))
                    break
                }
            }
        }

        //spawn small obstacle
        smallObstacleSpawnTime += delta*(2+Math.random()*5).toFloat()
        if(smallObstacleSpawnTime >= 14f){
            gameSmallObstacleList.add(GameSmallObstacle(this, (orthographicCamera.position.x+(5+(Math.random()*10)).toFloat() + orthographicCamera.viewportWidth), orthographicCamera.viewportHeight*0.042f))
            smallObstacleSpawnTime = 0f
        }

        //remove cloud
        val iteratorGameCloud: MutableIterator<GameCloud> = gameCloudList.iterator()
        while(iteratorGameCloud.hasNext()) {
            val gameCloud: GameCloud = iteratorGameCloud.next()
            if ((gameCloud.body.position.x+gameCloud.getWidth()/2) <= (orthographicCamera.position.x-orthographicCamera.viewportWidth/2)) {
                iteratorGameCloud.remove()
                break
            }
        }

        //remove ground
        val iteratorGameGround: MutableIterator<GameGround> = gameGroundList.iterator()
        while(iteratorGameGround.hasNext()) {
            val gameGround: GameGround = iteratorGameGround.next()
            if ((gameGround.body.position.x+gameGround.getWidth()/2) <= (orthographicCamera.position.x-orthographicCamera.viewportWidth/2)) {
                iteratorGameGround.remove()
                break
            }
        }

        //remove small obstacle
        val iteratorGameSmallObstacle: MutableIterator<GameSmallObstacle> = gameSmallObstacleList.iterator()
        while(iteratorGameSmallObstacle.hasNext()) {
            val smallObstacle: GameSmallObstacle = iteratorGameSmallObstacle.next()
            if ((smallObstacle.body.position.x+smallObstacle.getWidth()/2) <= (orthographicCamera.position.x-orthographicCamera.viewportWidth/2)) {
                iteratorGameSmallObstacle.remove()
                break
            }
        }

        //SPAWN END ------------------------------------------------------------------------------------
    }

    override fun resize(width: Int, height: Int) {
        viewPort.update(width, height)
    }

    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
    override fun dispose() {
        hud.dispose()
        for(i in gameGroundList){
            i.dispose()
        }
        for(i in gameSmallObstacleList){
            i.dispose()
        }
        for(i in gameCloudList){
            i.dispose()
        }
        gameLoad.dispose()
    }

    fun getWorld(): World {
        return world
    }

    fun getTextureAtlasCharacter(): TextureAtlas {
        return textureAtlasCharacter
    }

    private fun playJumpSound(){
        if(musicString == "ON"){
            jumpSound.play()
        }
    }
    private fun playBumpSound(){
        if(musicString == "ON"){
            bumpSound.play()
        }
    }

    fun getScalePixel(): Double {
        return game.scalePixel
    }

    init {

        viewPort = FitViewport(game.screenWidth.toFloat(), game.screenHeight.toFloat(), orthographicCamera)

        //CollisionDetection
        world.setContactListener(GameContactListener())

        //box2d
        box2dDebugRenderer = Box2DDebugRenderer()


        //HUD
        hud = GameHud(this,game.batch!!)

        //LOAD-------------------------------------------------
        gameLoad = GameLoad(game.batch!!)
        gameLoad.loadAsset()
    }
}