package com.example.fitnesstrackertake1

import android.content.Intent
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
import kotlin.concurrent.thread

class review_current_workout : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review_current_workout)

        val db = Room.databaseBuilder(
            applicationContext,
            Database::class.java, "fitnessTracker"
        ).build()
        Thread {

            //get references to textviews that will store workout info
            var durationText = findViewById<TextView>(R.id.DurationText)
            var disText = findViewById<TextView>(R.id.DistanceText)
            var speedText = findViewById<TextView>(R.id.SpeedText)
            var calText = findViewById<TextView>(R.id.CalText)
            var stepInput = findViewById<TextInputEditText>(R.id.StepsInput)
            var workoutEntity: WorkoutEntity

            //get the current workout from the database
            workoutEntity = db.workoutDao().getWorkout(db.workoutDao().getMaxWorkoutNumber())
            runOnUiThread {
                //populate textViews with workout data
                durationText.text = "%.2f".format(workoutEntity.duration)
                disText.text = "%.2f".format(workoutEntity.distance)
                speedText.text = "%.2f".format(workoutEntity.speed)
                calText.text = "%.2f".format(workoutEntity.calories)
            }

            //declare save button that will update steps if there and navigate to main activity
            var saveButton = findViewById<Button>(R.id.saveButton)
            saveButton.setOnClickListener {
                if (stepInput.text!!.isNotEmpty() && stepInput.text!!.isDigitsOnly()) {
                    Thread {
                        db.workoutDao()
                            .addSteps(
                                stepInput.text.toString().toInt(),
                                workoutEntity.workoutNumber
                            )
                    }.start()

                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    //if step input is not a digit warn the user
                } else if (stepInput.text!!.isNotEmpty() && !stepInput.text!!.isDigitsOnly()) {
                    var warning = findViewById<TextView>(R.id.warningSteps)
                    warning.text = "Enter an Integer."

                } else {
                    //if step input is empty navigate to main activity
                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }.start()

    }
}