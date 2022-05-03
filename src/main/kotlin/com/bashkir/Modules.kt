@file:Suppress("DEPRECATION")

package com.bashkir

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.bashkir.retrofit.Authorizer
import com.bashkir.retrofit.ServiceAccount
import com.bashkir.services.DocumentService
import com.bashkir.services.TaskService
import com.bashkir.services.TemplateService
import com.bashkir.services.UserService
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import io.ktor.application.*
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.FileInputStream
import java.security.interfaces.RSAPrivateKey
import java.util.*

private val serviceModule = module{
    single { UserService() }
    single { TaskService() }
    single { DocumentService() }
    single { TemplateService() }
}

private val serviceAccountModule = module {
    single { (environment: ApplicationEnvironment) ->
        GoogleCredential.fromStream(
            FileInputStream(
                environment.config.property(
                    "ktor.serviceAccount"
                ).getString()
            )
        ).serviceAccountPrivateKey as RSAPrivateKey
    }

    factory(named("jwtToken")) { (env: ApplicationEnvironment) ->
        JWT.create()
            .withIssuer("usersgetter@electronicdocuments.iam.gserviceaccount.com")
            .withClaim(
                "scope",
                "https://www.googleapis.com/auth/admin.directory.user.readonly https://www.googleapis.com/auth/admin.directory.user.security"
            )
            .withAudience("https://oauth2.googleapis.com/token")
            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .withIssuedAt(Date(System.currentTimeMillis()))
            .withSubject(env.config.property(
                "ktor.adminEmail"
            ).getString())
            .sign(
                Algorithm.RSA256(
                    null, get { parametersOf(env) }
                )
            )
    }

    single {
        Retrofit.Builder().baseUrl("https://oauth2.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Authorizer::class.java)
    }

    single {
        Retrofit.Builder().baseUrl("https://admin.googleapis.com/admin/directory/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServiceAccount::class.java)
    }
}

val modulesDI = listOf(serviceModule, serviceAccountModule)
