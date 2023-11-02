package com.damai.data.mappers

import com.damai.base.BaseMapper
import com.damai.base.utils.Constants.EMPTY_CODE
import com.damai.base.utils.Constants.MAP_KEY_ERROR
import com.damai.base.utils.Constants.MAP_KEY_MESSAGE
import com.damai.base.utils.Constants.MAP_KEY_STATUS_CODE
import com.damai.base.utils.Constants.SUCCESS_CODE
import com.damai.domain.models.CurrencyNamesModel

/**
 * Created by damai007 on 31/October/2023
 */
class CurrencyNamesResponseToCurrencyNamesModelMapper : BaseMapper<Map<String, String>, CurrencyNamesModel>() {

    override fun map(value: Map<String, String>): CurrencyNamesModel {
        return CurrencyNamesModel(
            currencyList = value.map {
                Pair(it.key, it.value)
            }
        ).also {
            if (value.containsKey(MAP_KEY_ERROR)) {
                if (value.containsKey(MAP_KEY_STATUS_CODE)) {
                    it.status = value[MAP_KEY_STATUS_CODE]?.toIntOrNull() ?: EMPTY_CODE
                } else {
                    it.status = EMPTY_CODE
                }

                if (value.containsKey(MAP_KEY_MESSAGE)) {
                    it.message = value[MAP_KEY_MESSAGE]
                }
            } else {
                it.status = SUCCESS_CODE
            }
        }
    }
}