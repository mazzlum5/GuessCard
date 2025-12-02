package com.example.guesscard

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import java.util.Locale
import java.util.concurrent.TimeUnit

class GameActivity : AppCompatActivity() {

    private lateinit var cardImageView: ImageView
    private lateinit var higherButton: Button
    private lateinit var lowerButton: Button
    private lateinit var scoreTextView: TextView
    private lateinit var timerTextView: TextView

    private val deck = mutableListOf<Card>()
    private lateinit var currentCard: Card
    private var score = 0
    private var isTimedMode = false
    private var timer: CountDownTimer? = null

    private val cardResources = mapOf(
        "h1" to R.drawable.h1, "h2" to R.drawable.h2, "h3" to R.drawable.h3, "h4" to R.drawable.h4, "h5" to R.drawable.h5, "h6" to R.drawable.h6, "h7" to R.drawable.h7, "h8" to R.drawable.h8, "h9" to R.drawable.h9, "h10" to R.drawable.h10, "h11" to R.drawable.h11, "h12" to R.drawable.h12, "h13" to R.drawable.h13,
        "d1" to R.drawable.d1, "d2" to R.drawable.d2, "d3" to R.drawable.d3, "d4" to R.drawable.d4, "d5" to R.drawable.d5, "d6" to R.drawable.d6, "d7" to R.drawable.d7, "d8" to R.drawable.d8, "d9" to R.drawable.d9, "d10" to R.drawable.d10, "d11" to R.drawable.d11, "d12" to R.drawable.d12, "d13" to R.drawable.d13,
        "c1" to R.drawable.c1, "c2" to R.drawable.c2, "c3" to R.drawable.c3, "c4" to R.drawable.c4, "c5" to R.drawable.c5, "c6" to R.drawable.c6, "c7" to R.drawable.c7, "c8" to R.drawable.c8, "c9" to R.drawable.c9, "c10" to R.drawable.c10, "c11" to R.drawable.c11, "c12" to R.drawable.c12, "c13" to R.drawable.c13,
        "s1" to R.drawable.s1, "s2" to R.drawable.s2, "s3" to R.drawable.s3, "s4" to R.drawable.s4, "s5" to R.drawable.s5, "s6" to R.drawable.s6, "s7" to R.drawable.s7, "s8" to R.drawable.s8, "s9" to R.drawable.s9, "s10" to R.drawable.s10, "s11" to R.drawable.s11, "s12" to R.drawable.s12, "s13" to R.drawable.s13
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        cardImageView = findViewById(R.id.cardImageView)
        higherButton = findViewById(R.id.higherButton)
        lowerButton = findViewById(R.id.lowerButton)
        scoreTextView = findViewById(R.id.scoreTextView)
        timerTextView = findViewById(R.id.timerTextView)

        isTimedMode = intent.getBooleanExtra("TIMED_MODE", false)

        createDeck()
        shuffleDeck()
        drawNextCard()

        if (isTimedMode) {
            startTimer()
        } else {
            timerTextView.visibility = TextView.GONE
        }

        higherButton.setOnClickListener { guess(isHigher = true) }
        lowerButton.setOnClickListener { guess(isHigher = false) }
        updateScore()
    }

    private fun startTimer() {
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                timerTextView.text = String.format(Locale.US, "%02d:%02d", seconds / 60, seconds % 60)
            }

            override fun onFinish() {
                gameOver()
            }
        }.start()
    }

    private fun createDeck() {
        val suits = listOf("hearts", "diamonds", "clubs", "spades")
        for (suit in suits) {
            for (value in 1..13) {
                deck.add(Card(suit, value))
            }
        }
    }

    private fun shuffleDeck() {
        deck.shuffle()
    }

    private fun drawNextCard() {
        if (deck.isNotEmpty()) {
            currentCard = deck.removeAt(0)
            updateCardImage()
        } else {
            gameOver()
        }
    }

    private fun updateCardImage() {
        val suitChar = currentCard.suit.first()
        val imageName = "$suitChar${currentCard.value}"
        cardResources[imageName]?.let { resourceId ->
            cardImageView.setImageResource(resourceId)
        }
    }

    private fun guess(isHigher: Boolean) {
        if (deck.isEmpty()) {
            gameOver()
            return
        }

        val nextCard = deck.removeAt(0)
        val isCorrect = if (isHigher) nextCard.value > currentCard.value else nextCard.value < currentCard.value

        if (isCorrect) {
            score++
            currentCard = nextCard
            updateCardImage()
            updateScore()
        } else {
            gameOver()
        }
    }

    private fun updateScore() {
        scoreTextView.text = getString(R.string.score_label, score)
    }

    private fun gameOver() {
        timer?.cancel()
        val sharedPreferences = getSharedPreferences("game_stats", MODE_PRIVATE)
        val highScoreKey = if (isTimedMode) "timed_highScore" else "high_score"
        val highScoreNameKey = if (isTimedMode) "timed_highScore_name" else "highScore_name"
        val highScore = sharedPreferences.getInt(highScoreKey, 0)

        if (score > highScore) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.new_highScore_title))
            builder.setMessage(getString(R.string.enter_name_message))
            val input = EditText(this)
            builder.setView(input)
            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                val playerName = input.text.toString()
                if (playerName.isNotBlank()) {
                    sharedPreferences.edit {
                        putInt(highScoreKey, score)
                        putString(highScoreNameKey, playerName)
                    }
                }
                goToGameOverScreen()
            }
            builder.setCancelable(false)
            builder.show()
        } else {
            goToGameOverScreen()
        }
    }

    private fun goToGameOverScreen() {
        val intent = Intent(this, GameOverActivity::class.java)
        intent.putExtra("SCORE", score)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
