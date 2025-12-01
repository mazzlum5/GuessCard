package com.example.guesscard

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HighscoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highscore)

        val sharedPreferences = getSharedPreferences("game_stats", MODE_PRIVATE)
        val bestScore = sharedPreferences.getInt("high_score", 0)

        val bestScoreTextView = findViewById<TextView>(R.id.bestScoreValueTextView)
        bestScoreTextView.text = bestScore.toString()

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }
}
