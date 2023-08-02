package org.xsafter.xmtpmessenger

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.xmtp.android.library.Client
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideClient(): Client {
        // Return your Client instance here
    }

    @Provides
    fun provideContext(@ApplicationContext appContext: Context): Context {
        return appContext
    }
}
