package com.example.fitnesstrackertake1

import android.content.Intent
import android.icu.text.DecimalFormat
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.example.fitnesstrackertake1.ui.theme.FitnessTrackerTake1Theme
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class display_weekly_activity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.display_weekly_activity)

        val db = Room.databaseBuilder(
            applicationContext,
            Database::class.java, "fitnessTracker"
        ).build()

        //get bundle from previous activity and store date in variable
        val bundle = intent.extras!!
        var date = bundle.getString("Date", LocalDate.now().toString())

Thread {
    //get reference to bar chart
    var barchart = findViewById<BarChart>(R.id.barchart)

    //convert date to days and calculate max and min
    var dateInDays = dateToDays(date)
    var min = dateInDays - 3
    var max = dateInDays + 3
    //get month for workouts
    var month = daysToDate(min).substring(5,7)
    //use variable to display dates if workouts span two months
    var days_passed_month = 1
    //get the year for workouts
    var year = daysToDate(min).substring(0,4)
    //define list to contain chart labels
    var xAxisLabel = mutableListOf<String>()
    //iterate through week
    for (i in 0..7){
        //store day of month in labelInput
        var labelInput = daysToDate(min + i).takeLast(2)
        //if the day goes into new month
        if (daysToDate(min + i).substring(5,7) != month){
            //add days passed month to label list
            xAxisLabel += days_passed_month.toString()
            //increment days passed month
            days_passed_month ++

        }else{
            //add day of month to label list
            xAxisLabel += labelInput
        }
    }
    //get reference to label and store the month in it
    var titleBarChart = findViewById<TextView>(R.id.TitleBarChart)
    runOnUiThread {
        titleBarChart.text = date
    }

    //create lists to store the various attributes of the workout
    var barChartDurationlist = arrayListOf<BarEntry>()
    var barChartDistanceList = arrayListOf<BarEntry>()
    var barChartSpeedList = arrayListOf<BarEntry>()
    var barChartStepsList = arrayListOf<BarEntry>()
    var barChartCaloriesList = arrayListOf<BarEntry>()


    //create lists to store each days workout
    var weeksDuration = mutableListOf<Double>()
    var weeksDistance = mutableListOf<Double>()
    var weeksAverageSpeed = mutableListOf<Double>()
    var weeksCalories = mutableListOf<Double>()
    var weeksSteps = mutableListOf<Int>()

    //initialize variables used to track the days activities
    var dayDuration = 0.0
    var dayDistance = 0.0
    var daysAverageSpeed: Double
    var daysCalories = 0.0
    var daysSteps = 0


    //iterate through the week
    for (i in 0..6) {
        //if a workout was done on this day...
        if (db.workoutDao().dayExists(min + i)) {
            //get the days  workouts
            var daysWorkouts = db.workoutDao().getDay(min + i)
            for (j in daysWorkouts) {
                //add up the various attributes of a days workout
                dayDuration += j.duration
                dayDistance += j.distance
                daysCalories += j.calories
                daysSteps += j.steps
            }
            //add the attributes to the corresponding lists of daily workouts
            daysAverageSpeed = dayDuration.toDouble() / dayDistance
            weeksDuration += dayDuration
            weeksDistance += dayDistance
            weeksAverageSpeed += daysAverageSpeed
            weeksCalories += daysCalories
            weeksSteps += daysSteps

            //set days attributes to zero for next day
            dayDuration = 0.0
            dayDistance = 0.0
            daysCalories = 0.0
            daysSteps = 0

            //add the days workout to barEntry list
            barChartDurationlist += BarEntry(i.toFloat(), weeksDuration[i].toFloat())
            barChartDistanceList += BarEntry(i.toFloat(), "%.4f".format( weeksDistance[i]).toFloat())
            barChartSpeedList += BarEntry(i.toFloat(), "%.4f".format( weeksAverageSpeed[i]).toFloat())
            barChartCaloriesList += BarEntry(i.toFloat(), "%.4f".format( weeksCalories[i]).toFloat())
            barChartStepsList += BarEntry(i.toFloat(), weeksSteps[i].toFloat())
        }else{
            //if no workout exists for the day and zero to barchart and weeks workout lists
            weeksDuration += 0.0
            weeksDistance += 0.0
            weeksAverageSpeed += 0.0
            weeksCalories += 0.0
            weeksSteps += 0
            barChartDurationlist += BarEntry(i.toFloat(), "0".toFloat())
            barChartDistanceList += BarEntry(i.toFloat(), "0".toFloat())
            barChartSpeedList += BarEntry(i.toFloat(), "0".toFloat())
            barChartCaloriesList += BarEntry(i.toFloat(),"0".toFloat())
            barChartStepsList += BarEntry(i.toFloat(), "0".toFloat())
        }
    }

    //create a data set storing the barChartDuration list
    var barDataSet = BarDataSet(barChartDurationlist, "Duration")
    //pass the data set to barData
    var barData = BarData(barDataSet)
    //set barchart.data to barData
    barchart.data = barData
    //change barChart text size
    barDataSet.setValueTextSize(15f)
    //customize xAxis
    val xAxis = barchart.getXAxis()
    xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabel)
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.textSize = 20f
    //pass month to textView below barchart
    var labelXAxis = findViewById<TextView>(R.id.monthAndYear)
    runOnUiThread {
        labelXAxis.text = monthIntToString(month.toInt()) + " " + year
    }
    //remove grid lines
    barchart.setDrawGridBackground(false)
    barchart.xAxis.setDrawGridLines(false)
    barchart.axisLeft.setDrawGridLines(false)
    barchart.axisRight.setDrawGridLines(false)
    barchart.axisLeft.axisMinimum = 0.0F

    //declare buttons to change attribute shown
    var durationButton = findViewById<Button>(R.id.DurationButton)
    var DistanceButton = findViewById<Button>(R.id.DistanceButton)
    var speedButton = findViewById<Button>(R.id.SpeedButton)
    var caloriesButton = findViewById<Button>(R.id.CaloriesButton)
    var stepsButton = findViewById<Button>(R.id.StepsButton)
    var title = findViewById<TextView>(R.id.TitleBarChart)
    var backButton = findViewById<Button>(R.id.backButton)

    //change attribute to duration once button is clicked
    durationButton.setOnClickListener {
        title.text = "Duration (Minutes)"
        barDataSet = BarDataSet(barChartDurationlist, "Duration")
        barData = BarData(barDataSet)
        barchart.data = barData
        barDataSet.setValueTextSize(15f)
        barchart.invalidate()

    }

    //display distance via barchart when button is clicked
    DistanceButton.setOnClickListener {
        title.text = "Distance (Miles)"
        barDataSet = BarDataSet(barChartDistanceList, "Distance")
        barData = BarData(barDataSet)
        barchart.data = barData
        barDataSet.setValueTextSize(15f)
        barchart.invalidate()
    }

    //display speed when buttons clicked
    speedButton.setOnClickListener {
        title.text = "Average Speed (MPH)"
        barDataSet = BarDataSet(barChartSpeedList, "Speed")
        barData = BarData(barDataSet)
        barchart.data = barData
        barDataSet.setValueTextSize(15f)
        barchart.invalidate()
    }

    //display calories once button is clicked
    caloriesButton.setOnClickListener {
        title.text = "Calories"
        barDataSet = BarDataSet(barChartCaloriesList, "Calories")
        barData = BarData(barDataSet)
        barchart.data = barData
        barDataSet.setValueTextSize(15f)
        barchart.invalidate()
    }

    //display steps
    stepsButton.setOnClickListener {
        title.text = "Steps"
        barDataSet = BarDataSet(barChartStepsList, "Steps")
        barData = BarData(barDataSet)
        barchart.data = barData
        barDataSet.setValueTextSize(15f)
        barchart.invalidate()
    }

    //navigate to workout search
    backButton.setOnClickListener {
        var intent = Intent(this, view_workouts::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }




}.start()






    }

    //function checks if date is in desired format
    //if not it returns false and otherwise true
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

    //convert days passed 0 B.C. to date
    fun daysToDate(totalDays: Int): String {
        var year = totalDays / 365
        var daysInYear = totalDays % 365
        var daysInMonth = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31, 1)
        var loopMonthDays = daysInYear
        var monthIndex = 0

        while (loopMonthDays > daysInMonth[monthIndex] && monthIndex <= 11) {

            loopMonthDays = loopMonthDays - daysInMonth[monthIndex]
            monthIndex += 1
        }
        var month = monthIndex
        var days = loopMonthDays

        if (month == 0 && days == 0) {
            year = year - 1
            month = 12
            days = 31
        }


        var returnDays = ""
        if (days < 10) {
            returnDays = "0" + days.toString()
        } else {
            returnDays = days.toString()
        }
        var returnString: String
        if (month < 10) {
            returnString = year.toString() + "-0" + month.toString() + "-" + returnDays
        }else{
            returnString = year.toString() + "-" + month.toString() + "-" + returnDays

        }

        return returnString
    }

    fun monthIntToString(month: Int): String{
        var returnMonth: String
        if (month == 1){
            returnMonth = "January"
        }else if (month == 2){
            returnMonth = "February"
        }else if (month == 3){
            returnMonth = "March"
        }else if (month == 4){
            returnMonth = "April"
        }else if (month == 5){
            returnMonth = "May"
        }else if (month == 6){
            returnMonth = "June"
        }else if (month == 7){
            returnMonth = "July"
        }else if (month == 8){
            returnMonth = "August"
        }else if (month == 9){
            returnMonth = "September"
        }else if (month == 10){
            returnMonth = "October"
        }else if (month == 11){
            returnMonth = "November"
        }else{
            returnMonth = "December"
        }
        return  returnMonth
    }


}