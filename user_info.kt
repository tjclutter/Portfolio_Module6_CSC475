package com.example.fitnesstrackertake1

import android.content.Intent
import android.health.connect.datatypes.BoneMassRecord
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.text.isDigitsOnly
import androidx.room.Room
import com.example.fitnesstrackertake1.ui.theme.FitnessTrackerTake1Theme
import com.google.android.material.textfield.TextInputEditText

class user_info : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info)

        val db = Room.databaseBuilder(
            applicationContext,
            Database::class.java, "fitnessTracker"
        ).build()

        //get references to Edit text elements
        var heightInput = findViewById<TextInputEditText>(R.id.HeightInput)
        var weightInput = findViewById<TextInputEditText>(R.id.WeightInput)
        var ageInput = findViewById<TextInputEditText>(R.id.AgeInput)
        var sexInput = findViewById<TextInputEditText>(R.id.SexInput)
        var saveButton = findViewById<Button>(R.id.saveButton)

        //save user info in database if input is in proper format
        saveButton.setOnClickListener {
            //if all input is acceptable calculate BMR and store all the information in the database and navigate to main activity
            if (heightInput.text.toString().isDigitsOnly() && weightInput.text.toString().isDigitsOnly() && ageInput.text
                    .toString().isDigitsOnly() && (sexInput.text.toString() == "M" || sexInput.text.toString() == "F")) {

                var BMR: Double
                if (sexInput.text.toString() == "F"){
                    BMR = 655.1 + (9.563 * weightInput.text.toString().toDouble()) + (1.850 * (heightInput.text.toString().toDouble() * 100)) - (4.676 * ageInput.text.toString().toInt())
                }else {
                    BMR = 66.5 + (13.75 * weightInput.text.toString()
                        .toDouble()) + (5.003 * heightInput.text.toString()
                        .toDouble() * 100) - (6.75 * ageInput.text.toString().toInt())
                }


                Thread {
                    db.UserInfoDao().insertUser(
                        UserInfoEntity(
                            0,
                            "TJ",
                            ageInput.text.toString().toInt(),
                            weightInput.text.toString().toDouble(),
                            heightInput.text.toString().toDouble(),
                            sexInput.text.toString(),
                            BMR


                        )
                    )
                }.start()
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

            }
            else{
                //if input is not valid alert the user
                var warning = findViewById<TextView>(R.id.warning)
                warning.text = "Enter Valid Information"
            }
        }

    }
}