package com.example.timekeeping.di

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.example.timekeeping.repositories.AssignmentRepo
import com.example.timekeeping.repositories.AttendanceRepo
import com.example.timekeeping.repositories.AuthRepository
import com.example.timekeeping.repositories.EmployeeRepository
import com.example.timekeeping.repositories.GroupRepository
import com.example.timekeeping.repositories.PaymentRepo
import com.example.timekeeping.repositories.SalaryRepo
import com.example.timekeeping.repositories.ShiftRepository
import com.example.timekeeping.repositories.TeamRepository
import com.example.timekeeping.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
            FirebaseAuth.getInstance(),
        )
    }

    @Provides
    @Singleton
    fun provideEmployeeRepository(): EmployeeRepository {
        return EmployeeRepository(
            FirebaseFirestore.getInstance(),
        )
    }

    @Provides
    @Singleton
    fun provideShiftRepository(): ShiftRepository {
        return ShiftRepository(
            FirebaseFirestore.getInstance(),
        )
    }

    @Provides
    @Singleton
    fun provideTeamRepository(): TeamRepository {
        return TeamRepository(
            FirebaseFirestore.getInstance(),
        )
    }

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepository(
            FirebaseAuth.getInstance(),
        )
    }

    @Provides
    @Singleton
    fun provideAssignmentRepository(): AssignmentRepo {
        return AssignmentRepo(
            FirebaseFirestore.getInstance(),
        )
    }

    @Provides
    @Singleton
    fun provideAttendanceRepository(): AttendanceRepo {
        return AttendanceRepo(
            FirebaseFirestore.getInstance(),
        )
    }

    @Provides
    @Singleton
    fun provideSalaryRepository() : SalaryRepo {
        return SalaryRepo(
            FirebaseFirestore.getInstance(),
        )
    }

    @Provides
    @Singleton
    fun providePaymentRepository() : PaymentRepo {
        return PaymentRepo(
            FirebaseFirestore.getInstance(),
        )
    }
}
