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

        // Regular Highscore
        val bestScore = sharedPreferences.getInt("high_score", 0)
        var bestScoreName = sharedPreferences.getString("highscore_name", "Player")
        if (bestScoreName.isNullOrBlank()) {
            bestScoreName = "Player"
        }
        val bestScoreTextView = findViewById<TextView>(R.id.bestScoreValueTextView)
        bestScoreTextView.text = getString(R.string.highscore_format, bestScoreName, bestScore)

        // Timed Mode Highscore
        val timedHighscore = sharedPreferences.getInt("timed_highscore", 0)
        var timedHighscoreName = sharedPreferences.getString("timed_highscore_name", "Player")
        if (timedHighscoreName.isNullOrBlank()) {
            timedHighscoreName = "Player"
        }
        val timedHighscoreTextView = findViewById<TextView>(R.id.timedHighscoreValueTextView)
        timedHighscoreTextView.text = getString(R.string.highscore_format, timedHighscoreName, timedHighscore)

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }
}
