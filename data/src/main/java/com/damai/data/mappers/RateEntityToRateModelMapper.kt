package com.damai.data.mappers

import com.damai.base.BaseMapper
import com.damai.domain.entities.RateEntity
import com.damai.domain.models.RateModel

/**
 * Created by damai007 on 31/October/2023
 */
class RateEntityToRateModelMapper : BaseMapper<RateEntity, RateModel>() {

    override fun map(value: RateEntity): RateModel {
        return RateModel(
            code = value.code,
            name = "",
            value = value.value
        )
    }
}