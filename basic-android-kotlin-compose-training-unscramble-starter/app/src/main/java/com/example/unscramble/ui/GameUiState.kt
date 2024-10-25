package com.example.unscramble.ui

data class GameUiState(
    val scramblesWord: String = "",
    val isGuessedWordWrong: Boolean = false
)
