package com.jeremieguillot.identityreader.core.domain.util

sealed interface DataError: Error {
    enum class Local: DataError {
        INVALID_DATA
    }
}