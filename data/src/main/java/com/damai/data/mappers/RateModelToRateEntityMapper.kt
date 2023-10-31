package com.damai.data.mappers

import com.damai.base.BaseMapper
import com.damai.domain.entities.RateEntity
import com.damai.domain.models.RateModel

/**
 * Created by damai007 on 31/October/2023
 */
class RateModelToRateEntityMapper : BaseMapper<RateModel, RateEntity>() {

    override fun map(value: RateModel): RateEntity {
        return RateEntity(
            code = value.code,
            value = value.value
        )
    }
}