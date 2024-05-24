package com.example.fitnesstrackertake1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.example.fitnesstrackertake1.ui.theme.FitnessTrackerTake1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        //get references to buttons for navigation
        var recordButton = findViewById<Button>(R.id.recordButton)
        var userInfoButton = findViewById<Button>(R.id.userInfoButton)
        var viewWorkoutButton = findViewById<Button>(R.id.viewWorkoutsButton)

        //get reference to database
        val db = Room.databaseBuilder(
            applicationContext,
            Database::class.java, "fitnessTracker"
        ).build()
        var exists = false
        Thread{
            exists = db.UserInfoDao().exists(1)
        }.start()

        //navigate to record workout activity
        recordButton.setOnClickListener {
            //if user info exists
            if (exists) {
                var intent = Intent(this, record_workout::class.java)
                startActivity(intent)
            }else {
                //else tell user to enter user info
                var warning = findViewById<TextView>(R.id.warning)
                warning.text = "Enter User Data First"
            }
        }

        //navigate to user info activity
        userInfoButton.setOnClickListener {
            var intent = Intent(this, user_info::class.java)
            startActivity(intent)
        }

        //navigate to view workout activity
        viewWorkoutButton.setOnClickListener {
            var intent = Intent(this, view_workouts::class.java)
            startActivity(intent)
        }



    }
}