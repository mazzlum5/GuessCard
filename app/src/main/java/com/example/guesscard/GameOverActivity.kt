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
        val isTimedMode = intent.getBooleanExtra("TIMED_MODE", false)

        val finalScoreTextView = findViewById<TextView>(R.id.finalScoreTextView)
        finalScoreTextView.text = "$finalScore"

        val sharedPreferences = getSharedPreferences("GuessCard", Context.MODE_PRIVATE)
        val highscoreKey = if (isTimedMode) "TIMED_HIGHSCORE" else "HIGHSCORE"
        val bestScore = sharedPreferences.getInt(highscoreKey, 0)

        if (finalScore > bestScore) {
            sharedPreferences.edit().putInt(highscoreKey, finalScore).apply()
        }

        val bestScoreTextView = findViewById<TextView>(R.id.bestScoreTextView)
        bestScoreTextView.text = "${sharedPreferences.getInt(highscoreKey, 0)}"

        val playAgainButton = findViewById<Button>(R.id.playAgainButton)
        playAgainButton.setOnClickListener {
            startGame(isTimed = false)
        }

        val timedModeButton = findViewById<Button>(R.id.timedModeButton)
        timedModeButton.setOnClickListener {
            startGame(isTimed = true)
        }

        val menuButton = findViewById<Button>(R.id.menuButton)
        menuButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun startGame(isTimed: Boolean) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("TIMED_MODE", isTimed)
        startActivity(intent)
        finish()
    }
}
