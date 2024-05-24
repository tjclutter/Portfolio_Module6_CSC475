package com.example.fitnesstrackertake1

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserInfoEntity")
data class UserInfoEntity(
    @PrimaryKey(autoGenerate = true) var userNumber: Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "age") var age: Int,
    @ColumnInfo(name = "weight") var weight: Double,
    @ColumnInfo(name = "height") var height: Double,
    @ColumnInfo(name = "sex") var sex: String,
    @ColumnInfo(name = "BMR") var BMR: Double
)
