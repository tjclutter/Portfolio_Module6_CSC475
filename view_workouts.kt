package com.example.fitnesstrackertake1

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.core.text.isDigitsOnly
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.max

class view_workouts : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_workouts)

        //if the date exists in the bundle set the date field to that date
        var bundle = intent.extras
        var date = bundle?.getString("Date", "")
        var dateInput = findViewById<TextInputEditText>(R.id.DateInput)
        dateInput.setText(date)




        val db = Room.databaseBuilder(
            applicationContext,
            Database::class.java, "fitnessTracker"
        ).build()

        //declare a button that will open a menu once clicked
        var GroupByButton = findViewById<Button>(R.id.GroupByButton)
        GroupByButton.setOnClickListener {
            val popupMenu = PopupMenu(this, GroupByButton)
            popupMenu.menuInflater.inflate(R.menu.group_by, popupMenu.menu)
            popupMenu.show()

            //if a menu item is clicked set the buttons text equal to the menu item clicked
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.Spe ->
                        GroupByButton.text = "Speed"
                    R.id.Dis ->
                        GroupByButton.text = "Distance"
                    R.id.Step ->
                        GroupByButton.text = "Steps"
                    R.id.Dur ->
                        GroupByButton.text = "Duration"
                    R.id.Cal ->
                        GroupByButton.text = "Calories"
                }
                true
            })
        }

        //create orderByButton to open menu once clicked
        var OrderByButton = findViewById<Button>(R.id.OrderByButton)
        OrderByButton.setOnClickListener {
            val popupMenu = PopupMenu(this, OrderByButton)
            popupMenu.menuInflater.inflate(R.menu.asc_des, popupMenu.menu)
            popupMenu.show()

            //once a menu item is selected update the button with the menu items title
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.Asc ->
                        OrderByButton.text = "Ascending"
                    R.id.Des ->
                        OrderByButton.text = "Descending"
                }
                true
            })
        }

        //declare list view to store workouts
        val listView = findViewById<ListView>(R.id.WorkoutListView)
        //declare listString that will be written to and displayed on the list view
        var listString = mutableListOf<String>()
        //create list to store workouts within the date range
        var workOutEntityList: List<WorkoutEntity>
        //declare search button for searching database
        var searchButton = findViewById<Button>(R.id.searchButton)
        //declare warning text to alert user if input is invalid
        var warning = findViewById<TextView>(R.id.warningText)

        //once clicked the search process will begin
        searchButton.setOnClickListener {
            //declare edit text for input and set min/max to zero
            var minInput = findViewById<TextInputEditText>(R.id.MinInput)
            var maxInput = findViewById<TextInputEditText>(R.id.MaxInput)
            var min = 0
            var max = 0

            //if date and range are valid populate min max and date with input
            if (minInput.text!!.isNotEmpty() && minInput.text!!.isDigitsOnly() && maxInput.text!!.isNotEmpty() && maxInput.text!!.isDigitsOnly() && checkDate(dateInput.text.toString())) {
                    min = dateToDays(dateInput.text.toString()) - minInput.text.toString().toInt()
                    max = dateToDays(dateInput.text.toString()) + maxInput.text.toString().toInt()
                    date = dateInput.text.toString()
                    warning.text = ""

                //if range doesn't exist populate min and max for a total range of 7 days around the given date
                }else if (minInput.text!!.isEmpty() && maxInput.text!!.isEmpty() && checkDate(dateInput.text.toString())){
                    min = dateToDays(dateInput.text.toString()) - 3
                    max = dateToDays(dateInput.text.toString()) - 3
                    date = dateInput.text.toString()
                    warning.text = ""
                //if dateInput isn't in proper format alert the user
                }else if (!checkDate(dateInput.text.toString())){
                    warning.text = "Enter Valid Date"
                //if range isn't in proper format alert the user
                }else if (!minInput.text!!.toString().isDigitsOnly() || !maxInput.text!!.toString().isDigitsOnly()){
                    warning.text = "Enter Valid Range"
            }

            //populate the workoutEntityList by calling the database with the given min and max values
            //based off the input to the groupBy and OrderBy button choose the right query to return
            //data in the desired order
            Thread {
                if (GroupByButton.text == "Speed" && OrderByButton.text == "Ascending") {
                    workOutEntityList = db.workoutDao().getRangeOrderSpeed(min, max)
                }
                if (GroupByButton.text == "Speed" && OrderByButton.text == "Descending") {
                    workOutEntityList = db.workoutDao().getRangeOrderSpeedDes(min, max)
                }
                if (GroupByButton.text == "Distance" && OrderByButton.text == "Ascending") {
                    workOutEntityList = db.workoutDao().getRangeOrderDistance(min, max)
                }
                if (GroupByButton.text == "Distance" && OrderByButton.text == "Descending") {
                    workOutEntityList = db.workoutDao().getRangeOrderDistanceDes(min, max)
                }
                if (GroupByButton.text == "Duration" && OrderByButton.text == "Ascending") {
                    workOutEntityList = db.workoutDao().getRangeOrderDuration(min, max)
                }
                if (GroupByButton.text == "Duration" && OrderByButton.text == "Descending") {
                    workOutEntityList = db.workoutDao().getRangeOrderDurationDes(min, max)
                }
                if (GroupByButton.text == "Calories" && OrderByButton.text == "Ascending") {
                    workOutEntityList = db.workoutDao().getRangeOrderCal(min, max)
                }
                if (GroupByButton.text == "Calories" && OrderByButton.text == "Descending") {
                    workOutEntityList = db.workoutDao().getRangeOrderCalDes(min, max)
                } else {
                    workOutEntityList = db.workoutDao().getRange(min, max)
                }

                //for each workout append its attributes to the listString
                runOnUiThread {
                    for (i in workOutEntityList) {
                        var strBuilder = StringBuilder()
                        strBuilder.appendLine("Date: " + i.date)
                        strBuilder.appendLine("Duration: " + "%.2f".format(i.duration))
                        strBuilder.appendLine("Distance: " + "%.2f".format(i.distance))
                        strBuilder.appendLine("Average Speed: " + "%.2f".format(i.speed))
                        strBuilder.appendLine("Calories: " + "%.2f".format(i.calories))
                        strBuilder.appendLine("Steps: " + i.steps.toString())
                        listString += strBuilder.toString()
                    }
                }


                //create an ArrayAdapter and pass the parameters
                // for the current activity, layout of items, and list to take data from
                var arrayAdapter: ArrayAdapter<*>
                arrayAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    listString
                )
                runOnUiThread {
                    //pass the adapter to the list view
                    listView.adapter = arrayAdapter
                }


            }.start()
        }


        //create button and set listener to open the display_weekly_activity activity
        //create a bundle for the date and pass it to intent for use when the user navigates back to
        //view_workouts activity
        var toBarChart = findViewById<Button>(R.id.toBarChart)
        toBarChart.setOnClickListener {
            var intent = Intent(this, display_weekly_activity::class.java)
            if (checkDate(dateInput.text.toString())){
                date = dateInput.text.toString()
            }
            intent.putExtra("Date", date)
            startActivity(intent)
        }




    }

    //funciton checks if date is in proper format
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

    //function converts date to days passed 0 B.C.
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