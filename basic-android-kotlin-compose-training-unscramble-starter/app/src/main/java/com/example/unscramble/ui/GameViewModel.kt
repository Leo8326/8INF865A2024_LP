package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState : StateFlow<GameUiState> = _uiState.asStateFlow()

    private lateinit var currentWord : String
    var userGuess by mutableStateOf("")
        private set
    // Set of words used in the game
    private var usedWords: MutableSet<String> = mutableSetOf()

    private fun pickRandomWordAndShuffle(): String{
        if (allWords.size == usedWords.size) return ""
        currentWord = allWords.random()
        while (usedWords.contains(currentWord)){
            currentWord = allWords.random()
        }
        usedWords.add(currentWord)
        return shuffleCurrentWord(currentWord)
    }

    private fun shuffleCurrentWord(word : String): String{
        val tempWord = word.toCharArray()
        while ( String(tempWord) == word) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    fun updateUserGuess(guess : String){
        userGuess = guess
    }

    fun checkUserGuess(){
        if (userGuess.equals(currentWord, ignoreCase = true)){
            val newScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(newScore)
        } else {
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
        updateUserGuess("")
    }

    private fun updateGameState(updatedScore: Int){
        if (usedWords.size == MAX_NO_OF_WORDS){
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
        }
        else{
            _uiState.update { currentState ->
                currentState.copy(
                    scramblesWord = pickRandomWordAndShuffle(),
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    currentWordCount = currentState.currentWordCount.inc()
                )
            }
        }
    }

    fun skipWord(){
        updateGameState(_uiState.value.score)
        updateUserGuess("")
    }

    fun resetGame(){
        usedWords.clear()
        _uiState.value = GameUiState(pickRandomWordAndShuffle())
    }

    init {
        resetGame()
    }
}
