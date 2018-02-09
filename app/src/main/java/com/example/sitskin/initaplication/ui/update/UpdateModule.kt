package com.example.sitskin.initaplication.ui.update

import dagger.Module
import dagger.Provides

@Module
class UpdateModule {

    @Provides
    fun provideUpdatePresenter(presenter: UpdatePresenter): Update.Presenter {
        return presenter
    }

}
