package com.example.guesscard

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.TimeUnit
import android.app.AlertDialog
import android.widget.EditText

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
                timerTextView.text = String.format("%02d:%02d", seconds / 60, seconds % 60)
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
        val resourceId = resources.getIdentifier(imageName, "drawable", packageName)
        cardImageView.setImageResource(resourceId)
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
        val highscoreKey = if (isTimedMode) "timed_highscore" else "high_score"
        val highscoreNameKey = if (isTimedMode) "timed_highscore_name" else "highscore_name"
        val highScore = sharedPreferences.getInt(highscoreKey, 0)

        if (score > highScore) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("New High Score!")
            builder.setMessage("Please enter your name:")
            val input = EditText(this)
            builder.setView(input)
            builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                val playerName = input.text.toString()
                sharedPreferences.edit()
                    .putInt(highscoreKey, score)
                    .putString(highscoreNameKey, playerName)
                    .apply()
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