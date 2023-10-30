package com.damai.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel

/**
 * Created by damai007 on 30/October/2023
 */
abstract class BaseViewModel(
    app: Application
) : AndroidViewModel(application = app)