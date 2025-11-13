package com.example.zcode.di

import android.content.Context
import com.example.zcode.data.database.AppDatabase
import com.example.zcode.data.database.UserPreferencesDao
import com.example.zcode.data.manager.ThemeManager
import com.example.zcode.fastfetch.FastfetchIntegration
import com.example.zcode.file_explorer.FileExplorer
import com.example.zcode.network.IPAddressHandler
import com.example.zcode.network.NATBridgeManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * DatabaseModule - Hilt module for database and DAO injection
 *
 * Provides singleton instances of the database and DAOs
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provide singleton instance of AppDatabase
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    /**
     * Provide singleton instance of UserPreferencesDao
     */
    @Provides
    @Singleton
    fun provideUserPreferencesDao(database: AppDatabase): UserPreferencesDao {
        return database.userPreferencesDao()
    }
}

/**
 * ManagerModule - Hilt module for manager classes injection
 *
 * Provides singleton instances of business logic managers
 */
@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {

    /**
     * Provide singleton instance of ThemeManager
     */
    @Provides
    @Singleton
    fun provideThemeManager(
        @ApplicationContext context: Context,
        database: AppDatabase
    ): ThemeManager {
        return ThemeManager(context, database)
    }

    /**
     * Provide singleton instance of NATBridgeManager
     */
    @Provides
    @Singleton
    fun provideNATBridgeManager(): NATBridgeManager {
        return NATBridgeManager()
    }
}

/**
 * NetworkModule - Hilt module for network-related classes
 *
 * Provides network service instances
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provide singleton instance of IPAddressHandler
     */
    @Provides
    @Singleton
    fun provideIPAddressHandler(
        @ApplicationContext context: Context
    ): IPAddressHandler {
        return IPAddressHandler(context)
    }
}

/**
 * FileModule - Hilt module for file operations
 *
 * Provides file service instances
 */
@Module
@InstallIn(SingletonComponent::class)
object FileModule {

    /**
     * Provide singleton instance of FileExplorer
     */
    @Provides
    @Singleton
    fun provideFileExplorer(
        @ApplicationContext context: Context
    ): FileExplorer {
        return FileExplorer(context)
    }
}

/**
 * SystemModule - Hilt module for system integration
 *
 * Provides system integration instances
 */
@Module
@InstallIn(SingletonComponent::class)
object SystemModule {

    /**
     * Provide singleton instance of FastfetchIntegration
     */
    @Provides
    @Singleton
    fun provideFastfetchIntegration(
        @ApplicationContext context: Context
    ): FastfetchIntegration {
        return FastfetchIntegration(context)
    }
}

