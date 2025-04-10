package com.example.timekeeping.di

import com.example.timekeeping.repositories.GroupRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideGroupRepository(): GroupRepository {
        return GroupRepository(
            FirebaseFirestore.getInstance(),
            FirebaseAuth.getInstance()
        )
    }
}
