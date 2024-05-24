package com.example.fitnesstrackertake1

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "WorkoutEntity")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) var workoutNumber: Int,
    @ColumnInfo(name = "userNumber") var userNumber: Int,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "dateInDays") var dateInDays: Int,
    @ColumnInfo(name = "duration") var duration: Double,
    @ColumnInfo(name = "distance") var distance: Double,
    @ColumnInfo(name = "speed") var speed: Double,
    @ColumnInfo(name = "calories") var calories: Double,
    @ColumnInfo(name = "steps") var steps: Int,



)
