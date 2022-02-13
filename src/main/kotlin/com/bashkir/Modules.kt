package com.bashkir

import com.bashkir.services.DocumentService
import com.bashkir.services.TaskService
import com.bashkir.services.UserService
import org.koin.dsl.module

val serviceModule = module{
    single { UserService() }
    single { TaskService() }
    single { DocumentService() }
}