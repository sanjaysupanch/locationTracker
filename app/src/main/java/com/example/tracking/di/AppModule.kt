package com.example.tracking.di

import android.content.Context
import androidx.room.Room
import com.example.tracking.data.local.AppDatabase
import com.example.tracking.data.local.LocationDao
import com.example.tracking.data.remote.ApiService
import com.example.tracking.data.repository.LocationRepository
import com.example.tracking.util.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, "kredily_db"
        ).build()
    }

    @Provides
    fun provideLocationDao(db: AppDatabase) = db.locationDao()

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://webhook.site/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRepository(dao: LocationDao, api: ApiService): LocationRepository {
        return LocationRepository(dao, api)
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context)
    }
}