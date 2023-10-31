package com.damai.paypayexchangerates.application

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.damai.base.utils.Constants.ROOM_DATABASE_NAME
import com.damai.domain.daos.CurrencyNameDao
import com.damai.domain.daos.RateDao
import com.damai.domain.entities.CurrencyNameEntity
import com.damai.domain.entities.RateEntity

/**
 * Created by damai007 on 30/October/2023
 */
@Database(
    entities = [
        RateEntity::class,
        CurrencyNameEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun rateDao(): RateDao

    abstract fun currencyNameDao(): CurrencyNameDao

    companion object {

        fun buildDatabase(application: Application): AppDatabase {
            return Room.databaseBuilder(
                application,
                AppDatabase::class.java,
                ROOM_DATABASE_NAME
            ).fallbackToDestructiveMigration().build()
        }
    }
}