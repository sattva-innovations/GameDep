package com.sattvainnovations.zingiegame

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.sattvainnovations.zingiegame.domain.Pipe
import java.util.*

class GameView : SurfaceView, SurfaceHolder.Callback {
    private var measuredWidth = 0f
    private var measuredHeight = 0f
    private var surfaceHolder: SurfaceHolder? = null
    private var paint: Paint? = null
    private var bitmap: Bitmap? = null


    // The current score
    var score = 0
        private set
    private val Life = 0

    // For the bird
    private var positionX = 0.0f
    private var positionY = 0.0f
    private var velocityX = 0.0f
    private var velocityY = 0.0f
    private var accelerationX = 0.0f
    private var accelerationY = 0.7f

    // For the pipes
    private var iteratorInt = 0
    private val pipeWidth = 100.0f
    private var pipeList: MutableList<Pipe>? = null

    constructor(context: Context?) : super(context) {

        // Initialize
        init()
    }

    constructor(context: Context?, a: AttributeSet?) : super(context, a) {

        // Initialize
        init()
    }

    constructor(context: Context?, a: AttributeSet?, b: Int) : super(context, a, b) {

        // Initialize
        init()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceCreated(holder: SurfaceHolder) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) {}
    private fun init() {
        surfaceHolder = holder
        holder.addCallback(this)
        setZOrderOnTop(true)
        holder.setFormat(PixelFormat.TRANSPARENT)
        paint = Paint()
        paint!!.isAntiAlias = true

        // For the player
        bitmap = getBitmapFromVectorDrawable(context, R.drawable.player_1)
        bitmap = Bitmap.createScaledBitmap(bitmap!!, 150, 150, false)

        // For the pipes
        pipeList = ArrayList()
        keepScreenOn = true
    }

    /**
     * Updates the UI.
     */
    fun update() {
        paint!!.style = Paint.Style.FILL_AND_STROKE
        val canvas = surfaceHolder!!.lockCanvas()

        // Clear the canvas
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        // Draw the bird
        canvas.drawBitmap(bitmap!!, positionX - 150.0f / 2.0f, positionY - 150.0f / 2.0f, null)

        // Draw the pipes
        paint!!.color = colorPipe
        val removeList: MutableList<Int> = ArrayList()
        val size = pipeList!!.size
        for (index in 0 until size) {
            val pipe = pipeList!![index]
            if (isPipeOut(pipe)) {
                removeList.add(index)
            } else {
                // Draw the upper part of the pipe
                canvas.drawRect(
                    pipe.positionX - pipeWidth / 2.0f,
                    0.0f,
                    pipe.positionX + pipeWidth / 2.0f,
                    measuredHeight - pipe.height - gap,
                    paint!!
                )

                // Draw the lower part of the pipe
                canvas.drawRect(
                    pipe.positionX - pipeWidth / 2.0f,
                    measuredHeight - pipe.height,
                    pipe.positionX + pipeWidth / 2.0f,
                    measuredHeight,
                    paint!!
                )
            }
        }
        removeItemsFromPipeList(removeList)
        surfaceHolder!!.unlockCanvasAndPost(canvas)

        // Update the data for the bird
        positionX += velocityX
        positionY += velocityY
        velocityX += accelerationX
        //        velocityY += accelerationY;
        // Only accelerate velocityY when it is not too large
        if (velocityY <= 10.0f) {
            velocityY += accelerationY
        }

        // Update the data for the pipes
        for (pipe in pipeList!!) {
            pipe.positionX = pipe.positionX - pipeVelocity
        }
        if (iteratorInt == interval) {
            addPipe()
            iteratorInt = 0
        } else {
            iteratorInt++
        }
    }

    public override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Get the measured size of the view
        measuredWidth = getMeasuredWidth().toFloat()
        measuredHeight = getMeasuredHeight().toFloat()

        // Set the initial position
        setPosition(measuredWidth / 2.0f, measuredHeight / 2.0f)

        // Add the initial pipe
        addPipe()
    }

    fun jump() {
        velocityY = -10.0f
        var c = 0
        while (c <= score) {
            velocityY = velocityY - 0.3f
            c = c + 1
        }
    }

    fun setPosition(positionX: Float, positionY: Float) {
        this.positionX = positionX
        this.positionY = positionY
    }// Update the score in MainActivity

    // Check if the bird goes beyond the border
// Check if the player hits the pipes
    /**
     * Returns true if the player is still alive, false otherwise.
     *
     * @return True if the player is still alive, false otherwise.
     */
    val isAlive: Boolean
        get() {
            // Check if the player hits the pipes
            for (pipe in pipeList!!) {
                if (pipe.positionX >= measuredWidth / 2.0f - pipeWidth / 2.0f - 100.0f / 2.0f &&
                    pipe.positionX <= measuredWidth / 2.0f + pipeWidth / 2.0f + 100.0f / 2.0f
                ) {
                    if (positionY <= measuredHeight - pipe.height - gap + 50.0f / 2.0f ||
                        positionY >= measuredHeight - pipe.height - 50.0f / 2.0f
                    ) {
                        return false
                    } else {
                        if (pipe.positionX - pipeVelocity <
                            measuredWidth / 2.0f - pipeWidth / 2.0f - 100.0f / 2.0f
                        ) {
                            score++

                            // Update the score in MainActivity

                            // Update the score in MainActivity
                            val context = context
                            if (context is callGame) {
                                context.updateScore(score)
                                context.playScoreMusic()
                            }
                        }
                    }
                }
            }

            // Check if the bird goes beyond the border
            return if (positionY < 0.0f + 100.0f / 2.0f || positionY > measuredHeight - 100.0f / 2.0f) {
                false
            } else true
        }

    private fun isPipeOut(pipe: Pipe): Boolean {
        return pipe.positionX + pipeWidth / 2.0f < 0.0f
    }

    private fun removeItemsFromPipeList(removeList: MutableList<Int>) {
        val newList: MutableList<Pipe> = ArrayList()
        val size = pipeList!!.size
        for (index in 0 until size) {
            if (!removeList.remove(Integer.valueOf(index))) {
                newList.add(pipeList!![index])
            }
        }
        pipeList = newList
    }

    /**
     * Resets all the data of the over game.
     */
    fun resetData() {
        // For the player
        positionX = 0.0f
        positionY = 0.0f
        velocityX = 0.0f
        velocityY = 0.0f
        accelerationX = 0.0f
        accelerationY = 0.7f

        // For the pipes
        iteratorInt = 0
        pipeList = ArrayList()
        score = 0

        // Set the initial position
        setPosition(measuredWidth / 2.0f, measuredHeight / 2.0f)

        // Add the initial pipe
        addPipe()
    }

    /**
     * Adds a pipe into the list of pipes.
     */
    private fun addPipe() {
        pipeList!!.add(
            Pipe(
                measuredWidth + pipeWidth / 2.0f,
                base + (measuredHeight - 2 * base - gap) * Random().nextFloat()
            )
        )
    }

    companion object {
        // The colors
        private val colorPipe = Color.parseColor("#C0C0C0")
        private const val interval = 150
        private const val gap = 450.0f
        private const val base = 100.0f
        private const val pipeVelocity = 3.0f
        fun getBitmapFromVectorDrawable(context: Context?, drawableId: Int): Bitmap {
            var drawable = ContextCompat.getDrawable(context!!, drawableId)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                drawable = DrawableCompat.wrap(drawable!!).mutate()
            }
            val bitmap = Bitmap.createBitmap(
                drawable!!.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }
    }
}