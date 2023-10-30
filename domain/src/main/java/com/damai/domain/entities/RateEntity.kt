package com.damai.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by damai007 on 30/October/2023
 */
@Entity(tableName = "rate_entity")
data class RateEntity(
    @PrimaryKey val code: String,
    val name: String,
    val value: Double
)
