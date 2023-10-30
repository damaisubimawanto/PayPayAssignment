package com.damai.base.networks

import com.damai.base.BaseModel
import com.damai.base.coroutines.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * Created by damai007 on 30/October/2023
 */
abstract class NetworkResource<T: BaseModel>(
    private val dispatcherProvider: DispatcherProvider
) {

    fun asFlow(): Flow<Resource<T>> = flow {
        if (shouldFetchFromRemote()) {
            val remoteResponse = safeApiCall(dispatcher = dispatcherProvider.io()) {
                remoteFetch()
            }

            when (remoteResponse) {
                is ResultWrapper.Success -> {
                    if (shouldSaveToLocal()) {
                        saveLocal(data = remoteResponse.value)
                    }
                    emit(Resource.Success(model = remoteResponse.value))
                }
                is ResultWrapper.GenericError -> {
                    emit(Resource.Error(errorMessage = remoteResponse.message))
                }
            }
        } else {
            /* Get from cache. */
            val localCache = withContext(dispatcherProvider.io()) {
                localFetch()
            }
            if (localCache == null) {
                val remoteResponse = safeApiCall(dispatcher = dispatcherProvider.io()) {
                    remoteFetch()
                }

                when (remoteResponse) {
                    is ResultWrapper.Success -> {
                        if (shouldSaveToLocal()) {
                            saveLocal(data = remoteResponse.value)
                        }
                        emit(Resource.Success(model = remoteResponse.value))
                    }
                    is ResultWrapper.GenericError -> {
                        emit(Resource.Error(errorMessage = remoteResponse.message))
                    }
                }
            } else {
                emit(Resource.Success(model = localCache))
            }
        }
    }

    abstract suspend fun remoteFetch(): T

    open suspend fun localFetch(): T? = null

    open suspend fun saveLocal(data: T) {}

    open fun shouldFetchFromRemote() = true

    open fun shouldSaveToLocal() = false
}