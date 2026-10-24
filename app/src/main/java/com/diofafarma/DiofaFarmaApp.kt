package com.diofafarma

import android.app.Application
import com.diofafarma.util.TokenManager

class DiofaFarmaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
    }
}
