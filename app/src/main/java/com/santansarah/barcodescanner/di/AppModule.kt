package com.santansarah.barcodescanner.di

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.santansarah.barcodescanner.data.remote.FoodApi
import com.santansarah.barcodescanner.domain.OFFAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object AppModules {

    @Singleton
    @Provides
    @IoDispatcher
    fun provideIoDispatcher() = Dispatchers.IO

    @Provides
    fun provideKtorClient() = HttpClient(Android) {
        engine {
            connectTimeout = 3000
        }
        expectSuccess = true
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Timber.tag("HTTP Client").d(message)
                }
            }
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    fun provideRetrofit(): FoodApi {
        val contentType: MediaType = "application/json".toMediaType()
        val jsonBuilder = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            explicitNulls = false
        }

        val gson: Gson = GsonBuilder()
            //.serializeNulls()
            .setPrettyPrinting()
            .create()


        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient =
            OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build()

        return Retrofit.Builder()
            .baseUrl(OFFAPI)
            /*.addConverterFactory(jsonBuilder
                .asConverterFactory(contentType))*/
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(FoodApi::class.java)
    }

    @Provides
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader =
        ImageLoader.Builder(context)
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .build()

}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class IoDispatcher
