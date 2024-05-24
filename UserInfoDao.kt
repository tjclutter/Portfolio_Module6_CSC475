package com.example.fitnesstrackertake1

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserInfoDao {
    @Insert
    fun insertUser(vararg id: UserInfoEntity)

    @Query("SELECT * FROM UserInfoEntity WHERE userNumber = :userNumber")
    fun getInfo(userNumber: Int): UserInfoEntity
    @Query("SELECT * FROM UserInfoEntity WHERE userNumber = :userNumber")
    fun exists(userNumber: Int): Boolean
}