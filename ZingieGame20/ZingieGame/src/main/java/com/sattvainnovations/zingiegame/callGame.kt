package com.sattvainnovations.zingiegame

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MutableLiveData
import java.lang.String
import java.util.*

private var gameView: GameView? = null
private var textViewScore: TextView? = null


class callGame : AppCompatActivity() {

    private var updateScoreMutableLiveData: MutableLiveData<Int> = MutableLiveData()

    private var isGameOver = false

    private var lives = 1

    private var finalScore = 0

    private var isSetNewTimerThreadEnabled = false

    private var setNewTimerThread: Thread? = null

    private var alertDialog: AlertDialog.Builder? = null

    private var mediaPlayer: MediaPlayer? = null

    private var gameMode = 0

    private val TOUCH_MODE = 0x00

    private var timer: Timer? = null

    private val handler: Handler =
    object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(message: Message) {
            when (message.what) {
                UPDATE -> {
                    if (gameView!!.isAlive) {
                        isGameOver = false
                        gameView!!.update()
                    } else {
                        if (lives == 5){
                            Toast.makeText(applicationContext,lives.toString(),Toast.LENGTH_LONG).show()
                            onBackPressed()
                        }
                        if (gameMode == TOUCH_MODE) {
                            // Cancel the timer
                            timer!!.cancel()
                            timer!!.purge()
                        }

                        alertDialog = AlertDialog.Builder(this@callGame)
                        alertDialog!!.setTitle("GAME OVER")
                        alertDialog!!.setMessage(
                            """
                            Score: ${String.valueOf(gameView!!.score)}
                            Lives left: ${(5-lives)}
                            Would you like to Try Again?
                            """.trimIndent()
                        )
                        alertDialog!!.setCancelable(false)
                        alertDialog!!.setPositiveButton(
                            "YES"
                        ) { dialog, which -> this@callGame.restartGame()
                            lives += 1

                        }
                        alertDialog!!.setNegativeButton(
                            "NO"
                        ) { dialog, which -> this@callGame.onBackPressed() }
                        alertDialog!!.show()
                    }
                }
                RESET_SCORE -> {
                    textViewScore!!.text = "0"
                }
                else -> {
                }
            }
        }
    }

    // The what values of the messages
    private val UPDATE = 0x00
    private val RESET_SCORE = 0x01

    fun getScoreMutableLiveData(): MutableLiveData<Int>{
        return this.updateScoreMutableLiveData
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_call_game)


        // Initialize the private views
        initViews()

        // Initialize the MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.sound_score)
        mediaPlayer!!.isLooping = false

        // Get the mode of the game from the StartingActivity

        // Get the mode of the game from the StartingActivity
        if (intent.getStringExtra("Mode") == "Touch") {
            gameMode = TOUCH_MODE
        }


        // Set the Timer

        // Set the Timer
        isSetNewTimerThreadEnabled = true
        setNewTimerThread = Thread {
            try {
                // Sleep for 0.3 seconds for the Surface to initialize
                Thread.sleep(300)
            } catch (exception: Exception) {
                exception.printStackTrace()
            } finally {
                if (isSetNewTimerThreadEnabled) {
                    setNewTimer()
                }
            }
        }
        setNewTimerThread!!.start()

        if (gameMode == TOUCH_MODE) {
            // Jump listener
            gameView!!.setOnTouchListener { view, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> gameView!!.jump()
                    MotionEvent.ACTION_UP -> {
                    }
                    else -> {
                    }
                }
                true
            }
        }
    }

    private fun initViews() {
        gameView = findViewById(R.id.game_view)
        textViewScore = findViewById(R.id.text_view_score)
    }

    /**
     * Sets the Timer to update the UI of the GameView.
     */
    private fun setNewTimer() {
        if (!isSetNewTimerThreadEnabled) {
            return
        }
        timer = Timer()
        timer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                // Send the message to the handler to update the UI of the GameView
                this@callGame.handler.sendEmptyMessage(UPDATE)

                // For garbage collection
                System.gc()
            }
        }, 0, 17)
    }

    override fun onDestroy() {
        if (timer != null) {
            timer!!.cancel()
            timer!!.purge()
        }
        isSetNewTimerThreadEnabled = false
        super.onDestroy()
    }

    override fun onPause() {
        isSetNewTimerThreadEnabled = false
        super.onPause()
    }

    fun updateScore(score: Int) {

        //take the score from here
        textViewScore!!.text = ("Score: $score")
        finalScore = finalScore.plus(score)

    }


    /**
     * Plays the music for score.
     */
     fun playScoreMusic() {
        if (gameMode == TOUCH_MODE) {
            mediaPlayer?.start()
        }
    }

    /**
     * Restarts the game.
     */
    private fun restartGame() {
        // Reset all the data of the over game in the GameView
        gameView!!.resetData()

        // Refresh the TextView for displaying the score
        Thread { handler.sendEmptyMessage(RESET_SCORE) }.start()
        if (gameMode == TOUCH_MODE) {
            isSetNewTimerThreadEnabled = true
            setNewTimerThread = Thread {
                try {
                    // Sleep for 3 seconds
                    Thread.sleep(300)
                } catch (exception: Exception) {
                    exception.printStackTrace()
                } finally {
                    if (isSetNewTimerThreadEnabled) {
                        setNewTimer()
                    }
                }
            }
            setNewTimerThread!!.start()
        }
    }

    override fun onBackPressed() {
        if (timer != null) {
            timer!!.cancel()
            timer!!.purge()
        }
        updateScoreMutableLiveData.postValue(finalScore)
        isSetNewTimerThreadEnabled = false
        super.onBackPressed()

    }




}