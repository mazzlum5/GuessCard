package com.example.guesscard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private lateinit var cardImageView: ImageView
    private lateinit var higherButton: Button
    private lateinit var lowerButton: Button
    private lateinit var scoreTextView: TextView

    private val deck = mutableListOf<Card>()
    private lateinit var currentCard: Card
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        cardImageView = findViewById(R.id.cardImageView)
        higherButton = findViewById(R.id.higherButton)
        lowerButton = findViewById(R.id.lowerButton)
        scoreTextView = findViewById(R.id.scoreTextView)

        createDeck()
        shuffleDeck()
        drawNextCard()

        higherButton.setOnClickListener { guess(isHigher = true) }
        lowerButton.setOnClickListener { guess(isHigher = false) }
        updateScore()
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
        val intent = Intent(this, GameOverActivity::class.java)
        intent.putExtra("SCORE", score)
        startActivity(intent)
        finish()
    }
}
