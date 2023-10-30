package com.damai.domain.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.damai.domain.entities.RateEntity

/**
 * Created by damai007 on 30/October/2023
 */
@Dao
interface RateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rateEntity: RateEntity)

    @Query("SELECT * FROM rate_entity ORDER BY code ASC")
    suspend fun getAllRateEntity(): List<RateEntity>
}