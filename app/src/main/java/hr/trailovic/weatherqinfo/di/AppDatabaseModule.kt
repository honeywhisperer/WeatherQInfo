package hr.trailovic.weatherqinfo.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hr.trailovic.weatherqinfo.database.AppDatabase
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object AppDatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room
            .databaseBuilder(context, AppDatabase::class.java, "weather-q-app")
            .build()
}