package com.example.sitskin.initaplication.ui.main

import dagger.Module
import dagger.Provides

@Module
class MainModule {
    @Provides
    fun provideMainPresenter( presenter: MainPresenter ): Main.Presenter {
        return presenter
    }
}