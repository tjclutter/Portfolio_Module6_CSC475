package com.example.fitnesstrackertake1

import androidx.core.text.util.LocalePreferences.FirstDayOfWeek.Days
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface workoutDao {
    @Insert
    fun addWorkout(vararg id: WorkoutEntity)

    @Query("UPDATE WorkoutEntity SET steps = :steps WHERE workoutNumber = :workoutNumber")
    fun addSteps(steps: Int, workoutNumber: Int)

    @Query("SELECT * FROM WorkoutEntity WHERE workoutNumber = :workoutNumber")
    fun getWorkout(workoutNumber: Int): WorkoutEntity

    @Query("SELECT MAX(workoutNumber) FROM WorkoutEntity")
    fun getMaxWorkoutNumber(): Int

    @Query("SELECT * FROM Workoutentity WHERE dateInDays BETWEEN :min AND :max")
    fun getRange(min: Int, max: Int): List<WorkoutEntity>

    @Query("SELECT * FROM WorkoutEntity WHERE dateInDays BETWEEN :min AND :max ORDER BY duration ASC")
    fun getRangeOrderDuration(min: Int, max: Int): List<WorkoutEntity>

    @Query("SELECT * FROM WorkoutEntity WHERE dateInDays BETWEEN :min AND :max  ORDER BY duration DESC")
    fun getRangeOrderDurationDes(min: Int, max: Int): List<WorkoutEntity>

    @Query("SELECT * FROM WorkoutEntity WHERE dateInDays BETWEEN :min AND :max ORDER BY distance ASC")
    fun getRangeOrderDistance(min: Int, max: Int): List<WorkoutEntity>

    @Query("SELECT * FROM WorkoutEntity WHERE dateInDays BETWEEN :min AND :max  ORDER BY distance DESC")
    fun getRangeOrderDistanceDes(min: Int, max: Int): List<WorkoutEntity>

    @Query("SELECT * FROM WorkoutEntity WHERE dateInDays BETWEEN :min AND :max ORDER BY speed ASC")
    fun getRangeOrderSpeed(min: Int, max: Int): List<WorkoutEntity>

    @Query("SELECT * FROM WorkoutEntity WHERE dateInDays BETWEEN :min AND :max  ORDER BY speed DESC")
    fun getRangeOrderSpeedDes(min: Int, max: Int): List<WorkoutEntity>

    @Query("SELECT * FROM WorkoutEntity WHERE dateInDays BETWEEN :min AND :max ORDER BY calories ASC")
    fun getRangeOrderCal(min: Int, max: Int): List<WorkoutEntity>

    @Query("SELECT * FROM WorkoutEntity WHERE dateInDays BETWEEN :min AND :max  ORDER BY calories DESC")
    fun getRangeOrderCalDes(min: Int, max: Int): List<WorkoutEntity>

    @Query("SELECT * FROM WorkoutEntity WHERE dateInDays BETWEEN :min AND :max ORDER BY steps ASC")
    fun getRangeOrderSteps(min: Int, max: Int): List<WorkoutEntity>

    @Query("SELECT * FROM WorkoutEntity WHERE dateInDays BETWEEN :min AND :max  ORDER BY steps DESC")
    fun getRangeOrderStepsDes(min: Int, max: Int): List<WorkoutEntity>
    @Query("SELECT * FROM WorkoutEntity WHERE dateInDays = :dateInDays")
    fun getDay(dateInDays: Int): List<WorkoutEntity>
    @Query("SELECT * FROM WorkoutEntity WHERE dateInDays = :dateInDays")
    fun dayExists(dateInDays: Int): Boolean


}