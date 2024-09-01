package com.example.tictactoe.TicTacToe

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.tictactoe.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TicTacToeFragment : Fragment() {

    private lateinit var butt0: Button
    private lateinit var butt1: Button
    private lateinit var butt2: Button
    private lateinit var butt3: Button
    private lateinit var butt4: Button
    private lateinit var butt5: Button
    private lateinit var butt6: Button
    private lateinit var butt7: Button
    private lateinit var butt8: Button
    private lateinit var startButt: Button
    private lateinit var gameStatusText: TextView
    private var ticTacToeGameData: TicTacToeGameData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tic_tac_toe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        TicTacToeGame.fetchTicTacToeGameData()

        butt0 = view.findViewById(R.id.butt_0)
        butt1 = view.findViewById(R.id.butt_1)
        butt2 = view.findViewById(R.id.butt_2)
        butt3 = view.findViewById(R.id.butt_3)
        butt4 = view.findViewById(R.id.butt_4)
        butt5 = view.findViewById(R.id.butt_5)
        butt6 = view.findViewById(R.id.butt_6)
        butt7 = view.findViewById(R.id.butt_7)
        butt8 = view.findViewById(R.id.butt_8)
        startButt = view.findViewById(R.id.start_butt)
        gameStatusText = view.findViewById(R.id.game_status)

        TicTacToeGame.ticTacToeGameData.observe(viewLifecycleOwner) {
            ticTacToeGameData = it
            initiatingUI()
        }

        butt0.setOnClickListener {
            clicking(butt0.tag.toString().toInt())
        }
        butt1.setOnClickListener {
            clicking(butt1.tag.toString().toInt())
        }
        butt2.setOnClickListener {
            clicking(butt2.tag.toString().toInt())
        }
        butt3.setOnClickListener {
            clicking(butt3.tag.toString().toInt())
        }
        butt4.setOnClickListener {
            clicking(butt4.tag.toString().toInt())
        }
        butt5.setOnClickListener {
            clicking(butt5.tag.toString().toInt())
        }
        butt6.setOnClickListener {
            clicking(butt6.tag.toString().toInt())
        }
        butt7.setOnClickListener {
            clicking(butt7.tag.toString().toInt())
        }
        butt8.setOnClickListener {
            clicking(butt8.tag.toString().toInt())
        }

        startButt.setOnClickListener {
            start()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if(TicTacToeGame.ticTacToeGameData.value?.gameId != "-1") {
                        showExitConfirmationDialog()
                    }else{
                        findNavController().popBackStack()
                    }
                }
            })

    }

    private fun initiatingUI() {
        ticTacToeGameData?.apply {
            startButt.visibility = View.VISIBLE
            butt0.text = gameMoves[0]
            butt1.text = gameMoves[1]
            butt2.text = gameMoves[2]
            butt3.text = gameMoves[3]
            butt4.text = gameMoves[4]
            butt5.text = gameMoves[5]
            butt6.text = gameMoves[6]
            butt7.text = gameMoves[7]
            butt8.text = gameMoves[8]
            gameStatusText.text =
                when (gameStatus) {
                    GameStatus.Created -> {
                        startButt.visibility = View.GONE
                        "Game ID: $gameId"
                    }

                    GameStatus.Ready -> {
                        "Click Start to start game"
                    }

                    GameStatus.Start -> {
                        startButt.visibility = View.GONE
                        when (TicTacToeGame.myID) {
                            turn -> {
                                "Your Turn"
                            }

                            else -> {
                                "$turn Turn"
                            }
                        }
                    }

                    GameStatus.Finished -> {
                        if (whoWin.isNotEmpty()) {
                            when (TicTacToeGame.myID) {
                                whoWin -> {
                                    "You Won!"
                                }

                                else -> {
                                    "$whoWin Won!"
                                }
                            }
                        } else "DRAW"
                    }
                }
        }
    }

    private fun start() {
        ticTacToeGameData?.apply {
            updateGame(
                TicTacToeGameData(
                    gameId = gameId,
                    gameStatus = GameStatus.Start
                )
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun clicking(tag: Int) {
        ticTacToeGameData?.apply {
            if (gameStatus != GameStatus.Start) {
                Toast.makeText(requireContext(), "Game not started yet", Toast.LENGTH_LONG).show()
            } else {

                if (gameId != "-1" && turn != TicTacToeGame.myID) {
                    Toast.makeText(requireContext(), "It is not your turn", Toast.LENGTH_LONG)
                        .show()
                    return
                }

                if (gameMoves[tag].isNotEmpty()) {
                    return
                }

                if (gameMoves[tag].isEmpty()) gameMoves[tag] = turn
                turn = if (turn == "X") "O" else "X"
                checkWin()
                updateGame(this)
            }
        }
    }

    private fun checkWin() {
        val winProb = listOf(
            listOf(0, 1, 2),
            listOf(3, 4, 5),
            listOf(6, 7, 8),
            listOf(0, 3, 6),
            listOf(1, 4, 7),
            listOf(2, 5, 8),
            listOf(0, 4, 8),
            listOf(2, 4, 6)
        )

        ticTacToeGameData?.apply {
            for (i in winProb) {
                if (gameMoves[i[0]] == gameMoves[i[1]] && gameMoves[i[1]] == gameMoves[i[2]] && gameMoves[i[0]].isNotEmpty()) {
                    gameStatus = GameStatus.Finished
                    whoWin = gameMoves[i[0]]
                }
            }

            if (gameMoves.none { it.isEmpty() }) gameStatus = GameStatus.Finished

            updateGame(this)
        }
    }

    private fun updateGame(ticTacToeGameData: TicTacToeGameData) {
        TicTacToeGame.savTicTacToeGameData(ticTacToeGameData)
    }

    private fun showExitConfirmationDialog() {
        // Create and show the confirmation dialog
        AlertDialog.Builder(requireContext())
            .setMessage("Are you sure you want to exit the game?")
            .setPositiveButton("Yes") { _, _ ->
                // If user presses "Yes", start newGame
                findNavController().popBackStack()
                if (TicTacToeGame.myID == "X") {
                    Firebase.firestore.collection("TicTacToeGame")
                        .document(TicTacToeGame.ticTacToeGameData.value?.gameId.toString())
                        .delete()
                } else {
                    Firebase.firestore.collection("TicTacToeGame")
                        .document(TicTacToeGame.ticTacToeGameData.value?.gameId.toString())
                        .get()
                        .addOnSuccessListener {
                            val ticTacToeGameData = it?.toObject(TicTacToeGameData::class.java)
                            ticTacToeGameData?.gameStatus = GameStatus.Created
                            TicTacToeGame.savTicTacToeGameData(ticTacToeGameData!!)
                        }
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                // If user presses "No", dismiss the dialog
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

}