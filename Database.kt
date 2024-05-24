package com.example.fitnesstrackertake1

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(UserInfoEntity::class, WorkoutEntity::class), version = 1)
abstract class Database : RoomDatabase(){
    abstract fun UserInfoDao(): UserInfoDao
    abstract fun workoutDao(): workoutDao
}