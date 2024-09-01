package com.example.tictactoe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.example.tictactoe.TicTacToe.GameStatus
import com.example.tictactoe.TicTacToe.TicTacToeGame
import com.example.tictactoe.TicTacToe.TicTacToeGameData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.random.Random
import kotlin.random.nextInt

class HomeFragment : Fragment() {

    private lateinit var offlineButt: Button
    private lateinit var createRoomButt: Button
    private lateinit var joinRoomButt: Button
    private lateinit var gameIDInput: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        offlineButt = view.findViewById(R.id.offline_butt)
        createRoomButt = view.findViewById(R.id.create_room_butt)
        joinRoomButt = view.findViewById(R.id.join_room_butt)
        gameIDInput = view.findViewById(R.id.room_id_input)

        offlineButt.setOnClickListener {
            startOffline()
        }

        createRoomButt.setOnClickListener {
            createRoom()
        }

        joinRoomButt.setOnClickListener {
            joinRoom()
        }

    }

    private fun startOffline() {
        TicTacToeGame.savTicTacToeGameData(
            TicTacToeGameData(gameStatus = GameStatus.Ready)
        )
        startFragment()
    }

    private fun createRoom() {
        TicTacToeGame.myID = "X"
        TicTacToeGame.savTicTacToeGameData(
            TicTacToeGameData(
                gameId = Random.nextInt(1000..99999).toString(),
                gameStatus = GameStatus.Created
            )
        )
        startFragment()
    }

    private fun joinRoom() {
        val roomID = gameIDInput.text.toString()
        if (roomID.isEmpty()) {
            gameIDInput.error = "Please enter The room ID"
        } else {
            TicTacToeGame.myID = "O"
            Firebase.firestore.collection("TicTacToeGame")
                .document(roomID)
                .get()
                .addOnSuccessListener {
                    val ticTacToeGameData = it?.toObject(TicTacToeGameData::class.java)
                    if (ticTacToeGameData == null) {
                        gameIDInput.error = "Please enter a valid room ID"
                    } else {
                        ticTacToeGameData.gameStatus = GameStatus.Ready
                        TicTacToeGame.savTicTacToeGameData(ticTacToeGameData)
                        startFragment()
                    }
                }
        }
    }

    private fun startFragment() {
        val action = HomeFragmentDirections.actionGlobalTicTacToeFragment(0) // for offline mode
        findNavController().navigate(action)
    }

}