package com.example.guesscard

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
                timerTextView.text = String.format(
                    Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60
                )
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
        cardImageView.setImageResource(getCardImageResource(currentCard))
    }

    @DrawableRes
    private fun getCardImageResource(card: Card): Int {
        return when (card.suit) {
            "hearts" -> when (card.value) {
                1 -> R.drawable.h1
                2 -> R.drawable.h2
                3 -> R.drawable.h3
                4 -> R.drawable.h4
                5 -> R.drawable.h5
                6 -> R.drawable.h6
                7 -> R.drawable.h7
                8 -> R.drawable.h8
                9 -> R.drawable.h9
                10 -> R.drawable.h10
                11 -> R.drawable.h11
                12 -> R.drawable.h12
                13 -> R.drawable.h13
                else -> R.drawable.card_back
            }
            "diamonds" -> when (card.value) {
                1 -> R.drawable.d1
                2 -> R.drawable.d2
                3 -> R.drawable.d3
                4 -> R.drawable.d4
                5 -> R.drawable.d5
                6 -> R.drawable.d6
                7 -> R.drawable.d7
                8 -> R.drawable.d8
                9 -> R.drawable.d9
                10 -> R.drawable.d10
                11 -> R.drawable.d11
                12 -> R.drawable.d12
                13 -> R.drawable.d13
                else -> R.drawable.card_back
            }
            "clubs" -> when (card.value) {
                1 -> R.drawable.c1
                2 -> R.drawable.c2
                3 -> R.drawable.c3
                4 -> R.drawable.c4
                5 -> R.drawable.c5
                6 -> R.drawable.c6
                7 -> R.drawable.c7
                8 -> R.drawable.c8
                9 -> R.drawable.c9
                10 -> R.drawable.c10
                11 -> R.drawable.c11
                12 -> R.drawable.c12
                13 -> R.drawable.c13
                else -> R.drawable.card_back
            }
            "spades" -> when (card.value) {
                1 -> R.drawable.s1
                2 -> R.drawable.s2
                3 -> R.drawable.s3
                4 -> R.drawable.s4
                5 -> R.drawable.s5
                6 -> R.drawable.s6
                7 -> R.drawable.s7
                8 -> R.drawable.s8
                9 -> R.drawable.s9
                10 -> R.drawable.s10
                11 -> R.drawable.s11
                12 -> R.drawable.s12
                13 -> R.drawable.s13
                else -> R.drawable.card_back
            }
            else -> R.drawable.card_back
        }
    }

    private fun guess(isHigher: Boolean) {
        if (deck.isEmpty()) {
            gameOver()
            return
        }

        val nextCard = deck.removeAt(0)
        val isCorrect = if (isHigher) {
            nextCard.value > currentCard.value
        } else {
            nextCard.value < currentCard.value
        }

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
        val highScoreKey = if (isTimedMode) "timed_highscore" else "high_score"
        val highScore = sharedPreferences.getInt(highScoreKey, 0)
        val isNewHighScore = score > highScore

        val title: String
        val iconRes: Int

        if (isNewHighScore) {
            sharedPreferences.edit {
                putInt(highScoreKey, score)
            }
            title = "New Score!"
            iconRes = android.R.drawable.ic_dialog_info
        } else {
            title = "Game Over"
            iconRes = android.R.drawable.ic_dialog_alert
        }

        // Create a styled message to make the score bold
        val messagePrefix = if (isNewHighScore) "Congrulations Your New Score " else "Your Score: "
        val scoreString = score.toString()
        val spannableMessage = SpannableStringBuilder(messagePrefix + scoreString)

        spannableMessage.setSpan(
            StyleSpan(Typeface.BOLD),
            messagePrefix.length,
            messagePrefix.length + scoreString.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setIcon(iconRes)
            .setMessage(spannableMessage)
            .setPositiveButton("Play Again") { _, _ ->
                val intent = Intent(this, GameActivity::class.java)
                intent.putExtra("TIMED_MODE", isTimedMode)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Menu") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
