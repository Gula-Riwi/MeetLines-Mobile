package com.meetline.app.di

import com.meetline.app.data.repository.DefaultAppointmentRepository
import com.meetline.app.data.repository.DefaultAuthRepository
import com.meetline.app.data.repository.DefaultBusinessRepository
import com.meetline.app.domain.repository.AppointmentRepository
import com.meetline.app.domain.repository.AuthRepository
import com.meetline.app.domain.repository.BusinessRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: DefaultAuthRepository
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindBusinessRepository(
        businessRepositoryImpl: DefaultBusinessRepository
    ): BusinessRepository

    @Binds
    @Singleton
    abstract fun bindAppointmentRepository(
        appointmentRepositoryImpl: DefaultAppointmentRepository
    ): AppointmentRepository
}
