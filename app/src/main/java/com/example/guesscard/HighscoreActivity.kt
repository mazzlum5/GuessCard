package com.example.guesscard

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HighScoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highscore)

        val sharedPreferences = getSharedPreferences("game_stats", MODE_PRIVATE)

        // Regular HighScore
        val bestScore = sharedPreferences.getInt("high_score", 0)
        var bestScoreName = sharedPreferences.getString("highScore_name", "Player")
        if (bestScoreName.isNullOrBlank()) {
            bestScoreName = "Player"
        }
        val bestScoreTextView = findViewById<TextView>(R.id.bestScoreValueTextView)
        bestScoreTextView.text = getString(R.string.highScore_format, bestScoreName, bestScore)

        // Timed Mode HighScore
        val timedHighScore = sharedPreferences.getInt("timed_highScore", 0)
        var timedHighScoreName = sharedPreferences.getString("timed_highScore_name", "Player")
        if (timedHighScoreName.isNullOrBlank()) {
            timedHighScoreName = "Player"
        }
        val timedHighScoreTextView = findViewById<TextView>(R.id.timedHighScoreValueTextView)
        timedHighScoreTextView.text = getString(R.string.highScore_format, timedHighScoreName, timedHighScore)

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }
}
