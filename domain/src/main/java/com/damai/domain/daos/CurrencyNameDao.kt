package com.damai.domain.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.damai.domain.entities.CurrencyNameEntity

/**
 * Created by damai007 on 31/October/2023
 */
@Dao
interface CurrencyNameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currencyNameEntity: CurrencyNameEntity)

    @Query("SELECT * FROM currency_name_entity")
    suspend fun getAllCurrencyNameEntityList(): List<CurrencyNameEntity>
}