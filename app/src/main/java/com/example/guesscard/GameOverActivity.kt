package com.example.guesscard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val finalScore = intent.getIntExtra("SCORE", 0)
        val finalScoreTextView = findViewById<TextView>(R.id.finalScoreTextView)
        finalScoreTextView.text = "Din poäng: $finalScore"

        val sharedPreferences = getSharedPreferences("GuessCard", Context.MODE_PRIVATE)
        val bestScore = sharedPreferences.getInt("BEST_SCORE", 0)

        if (finalScore > bestScore) {
            sharedPreferences.edit().putInt("BEST_SCORE", finalScore).apply()
        }

        val bestScoreTextView = findViewById<TextView>(R.id.bestScoreTextView)
        bestScoreTextView.text = "Bästa poäng: ${sharedPreferences.getInt("BEST_SCORE", 0)}"

        val playAgainButton = findViewById<Button>(R.id.playAgainButton)
        playAgainButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
            finish()
        }

        val menuButton = findViewById<Button>(R.id.menuButton)
        menuButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
