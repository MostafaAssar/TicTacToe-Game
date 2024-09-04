package com.example.tictactoe.TicTacToe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

object TicTacToeGame {
    private var _ticTacToeGameData = MutableLiveData<TicTacToeGameData>()
    var ticTacToeGameData: LiveData<TicTacToeGameData> = _ticTacToeGameData

    var myID = ""

    fun savTicTacToeGameData(ticTacToeGameData: TicTacToeGameData) {
        _ticTacToeGameData.postValue(ticTacToeGameData)

        if (ticTacToeGameData.gameId != "-1") {
            Firebase.firestore.collection("TicTacToeGame")
                .document(ticTacToeGameData.gameId)
                .set(ticTacToeGameData)
        }
    }

    fun fetchTicTacToeGameData() {
        ticTacToeGameData.value?.apply {
            if (gameId != "-1") {
                Firebase.firestore.collection("TicTacToeGame")
                    .document(gameId)
                    .addSnapshotListener { value, _ ->
                        val ticTacToeGameData =
                            value?.toObject(TicTacToeGameData::class.java)
                        if (ticTacToeGameData != null)
                            _ticTacToeGameData.postValue(ticTacToeGameData!!)
                    }
            }
        }
    }
}

data class TicTacToeGameData(
    var gameId: String = "-1",
    var turn: String = listOf("X", "O")[Random.nextInt(2)],
    var gameStatus: GameStatus = GameStatus.Created,
    var gameMoves: MutableList<String> = mutableListOf("", "", "", "", "", "", "", "", ""),
    var whoWin: String = ""
)

enum class GameStatus {
    Created,
    Ready,
    Start,
    Finished
}