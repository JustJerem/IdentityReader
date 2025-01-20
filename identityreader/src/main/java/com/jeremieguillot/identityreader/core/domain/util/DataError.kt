package com.sncf.android.internal.identityreader.core.domain.util

import com.jeremieguillot.identityreader.core.domain.util.Error

sealed interface DataError : Error {
    enum class Local : DataError {
        INVALID_DATA
    }
}