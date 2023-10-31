package com.damai.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by damai007 on 31/October/2023
 */
@Entity(tableName = "currency_name_entity")
data class CurrencyNameEntity(
    @PrimaryKey val code: String,
    val name: String
)
