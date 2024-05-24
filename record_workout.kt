package com.example.fitnesstrackertake1

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.text.DecimalFormat
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
//import androidx.compose.ui.tooling.data.EmptyGroup.location
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class record_workout : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.record_workout)




        //************************************************************************************************
        // * Title: Request Location Permissions
        // * Author: Android Developers
        // * Date: 2024-05-20
        // * Code version: N/A
        // * Availability: https://developer.android.com/develop/sensors-and-location/location/permissions
        //*************************************************************************************************

        //call registerForActivityResult and store output in locationPermissionRequest
        val locationPermissionRequest = registerForActivityResult(
            //contract for requesting permissions
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                //declare permissions to be granted
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                }
            }
        }

        //checks if permissions granted if not launches permission request
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))



        //checks if permissions are available for use by fused location client
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }


        //define boolean to pause and start record
        var record = true
        //initiate longitude and latitude variables
        var latitude = 0.0
        var longitude = 0.0

        //create list to store long and lat recordings
        var latitudeRecording = mutableListOf<Double>()
        var longitudeRecording = mutableListOf<Double>()

        //get reference to buttons for starting stopping pausing and resuming recordings
        var stopRecordingButton = findViewById<Button>(R.id.endButton)
        var startButton = findViewById<Button>(R.id.startButton)
        var resumeButton = findViewById<Button>(R.id.resumeButton)
        var pauseButton = findViewById<Button>(R.id.pauseButton)

        //define time for storing time and text for displaying time
        var time = 0
        var timeText = findViewById<TextView>(R.id.timeText)

        //get reference to fused location client that will be used to get location
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)





        //execute code to record workout when user clicks button
        startButton.setOnClickListener {

            Thread{
                while (record) {
                   //***********************************************************
                   // * Title: How to get location using "fusedLocationClient.getCurrentLocation" method in Kotlin?
                   // * Author: Flutter Fixes
                   // * Date: 2024-05-20
                   // * Code Version: N/A
                   // * Availability: https://flutterfixes.com/how-to-get-location-using-fusedlocationclient-getcurrentlocation-method-in-kotlin/
                   //***********************************************************
                   //use fused locationClient to get current location
                    fusedLocationClient.getCurrentLocation(
                        //define priority of location request
                        LocationRequest.PRIORITY_HIGH_ACCURACY,
                        //pass cancelation token to cancel request if needed
                        CancellationTokenSource().token
                    ).addOnSuccessListener { location: Location? ->
                        //store lat and long of current location in variables
                        latitude = location?.latitude!!
                        longitude = location?.longitude!!
                    }


                    //if latitude is equal to zero location isn't being tracked yet
                    //once location initiated add long and lat to lists keeping track of them
                    if (latitude !=0.0) {
                        latitudeRecording += latitude
                        longitudeRecording += longitude
                        //update timeText with current time
                        timeText.text = time.toString()
                    }

                    //format the timeText based on the current time
                    if (time < 60 * 60) {
                        var minutes = time / 60
                        var seconds = time % 60
                        if (seconds < 10) {
                            timeText.text = String.format("%d:0%d", minutes, seconds)
                        }else{
                            timeText.text = String.format("%d:%d", minutes, seconds)
                        }
                    }

                    //iterate time by one after every iteration of the while loop which takes place every second
                    time += 1
                    //pause the program and the while loop
                    Thread.sleep(100)



                }
            }.start()
        }

        //set record to false so while loop stops iterating
        pauseButton.setOnClickListener {
            record = false
        }


        //same as start button but labeled differently to limit confusion
        resumeButton.setOnClickListener {
            record = true
            Thread{
                while (record) {

                    fusedLocationClient.getCurrentLocation(
                        LocationRequest.PRIORITY_HIGH_ACCURACY,
                        CancellationTokenSource().token
                    ).addOnSuccessListener { location: Location? ->
                        // Use the location data (latitude and longitude)
                        latitude = location?.latitude!!
                        longitude = location.longitude

                        // Do something with lat and lon
                    }

                    latitudeRecording += latitude
                    longitudeRecording += longitude


                   if (time < 60 * 60) {
                       var minutes = time / 60
                       var seconds = time % 60
                       if (seconds < 10) {
                           timeText.text = String.format("%d:0%d", minutes, seconds)
                       }else{
                           timeText.text = String.format("%d:%d", minutes, seconds)
                       }
                   }









                    Thread.sleep(500)
                    time += 1


                }
            }.start()

        }

        //get reference to database
        val db = Room.databaseBuilder(
            applicationContext,
            Database::class.java, "fitnessTracker"
        ).build()


        //button when clicked stops recording and adds data to database
        stopRecordingButton.setOnClickListener {
            record = false
            if (time < 60 * 60) {
                var minutes = time / 60
                var seconds = time % 60
                if (seconds < 10) {
                    timeText.text = String.format("%d:0%d", minutes, seconds)
                }else{
                    timeText.text = String.format("%d:%d", minutes, seconds)
                }
            }

            //calculate distance and speed using functions
            var distance = calculateTotalDistance(latitudeRecording, longitudeRecording)
            var speed = calculateSpeed(distance, time)



            Thread {
                //add workout to database if time is greater than zero
                if (time != 0) {
                    db.workoutDao().addWorkout(
                        WorkoutEntity(
                            0,
                            0,
                            LocalDate.now().toString(),
                            dateToDays(LocalDate.now().toString()),
                            time/60.0,
                            distance,
                            speed,
                            calculateCalories(speed, time),
                            0,

                        )
                    )
                }
            }.start()

            //navigate to review_current_workout activity
            var intent = Intent(this, review_current_workout::class.java)
            startActivity(intent)

           // var title = findViewById<TextView>(R.id.titlerecord)
            //title.text = longitudeRecording.toString()


        }


        }

    //function that calculates the distance between two longitude and latitude points
    fun calculateDistanceBetweenPoints(lat1: Double, long1: Double, lat2: Double, long2: Double): Double{
        //initialize variables containing distance radius of earch and lat and long in radians
        var distance = 0.0
        val radius = 3959
        val latitude1 = Math.toRadians(lat1)
        val latitude2 = Math.toRadians(lat2)
        val longitude1 = Math.toRadians(long1)
        val longitude2 = Math.toRadians(long2)

        //calculate distance using Haversine Formula
        var part1 = sin((latitude2-latitude1)/2) * sin((latitude2-latitude1)/2)
        var part2 = cos(latitude2) * cos(latitude1) * sin((longitude2-longitude1)/2) * sin((longitude2-longitude1)/2)
        distance = 2 * radius * asin(sqrt(part1 + part2))
        //return distance between points
        return distance
    }

    //calculates total distance by adding up distances between two points
    fun calculateTotalDistance(latitudeArray: MutableList<Double>, longitudeArray: MutableList<Double>): Double{
        var totalDistance = 0.0
        //iterate through long and lat array adding up the distance between on entry and the next
        for (i in 0..latitudeArray.size - 2){
            totalDistance += calculateDistanceBetweenPoints(latitudeArray[i], longitudeArray[i], latitudeArray[i+1], longitudeArray[i+1])

        }
        //return total distance
        return totalDistance

    }

    //calculate speed given total distance and time
    fun calculateSpeed(totalDistance: Double, time: Int): Double{
        var time_hours = time * 0.00027778
        var speed = totalDistance/time_hours
        return speed
    }

    //calculate calories given speed, time, and user info from database
    fun calculateCalories(speed: Double, time: Int): Double{
        val db = Room.databaseBuilder(
            applicationContext,
            Database::class.java, "fitnessTracker"
        ).build()
        val user = db.UserInfoDao().getInfo(1)
        var calories = user.BMR * (calculateMET(speed)/24.0) * time * 0.00027778
        return calories
    }

    //calculate MET for various speeds
    fun calculateMET(speed: Double): Double{
        var MET = 0.0
        if (speed < 2) {
            MET = 2.0
        }else if(speed < 3) {
            MET = 3.0
        }else if (speed < 4) {
            MET = 5.0
        }else if (speed < 6) {
            MET = 8.0
        }else if (speed < 7) {
            MET = 11.0
        }else {
            MET = 12.0
        }
        return MET


        }

    //check if date is in valid format
    @RequiresApi(Build.VERSION_CODES.O)
    fun checkDate(inputString: String): Boolean {
        val format = DateTimeFormatter.ofPattern("YYYY-MM-DD")
        try {
            val date = format.parse(inputString)
            return true
        }catch (e: Exception){
            return false
        }

    }

    //convert date to days passed 0 B.C.
    @RequiresApi(Build.VERSION_CODES.O)
    fun dateToDays(dateString: String): Int {
        var totalDays = 0
        if (checkDate(dateString)) {
            var yearString = dateString.substring(0, 4)
            var yearInt = yearString.toInt() * 365
            var monthString = dateString.substring(5, 7)
            var monthInt = monthString.toInt()
            var dayString = dateString.substring(8, 10)
            var dayInt = dayString.toInt()
            var monthTotal = 0
            var daysInMonth = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31, 0)
            for (i in 0..<monthInt) {
                monthTotal += daysInMonth[i]

            }
            //if (monthInt == 12){
            //  monthTotal = monthTotal - 31
            //}



            totalDays = yearInt + monthTotal + dayInt


        }
        return totalDays

    }



    }








        //long.text = longRec.toString()


