//package com.domatix.yevbes.nucleus.core.utils
//
//import android.util.Log
//import okhttp3.*
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.converter.scalars.ScalarsConverterFactory
//import java.util.concurrent.TimeUnit
//
//class Retrofit2Helper(
//    var protocol: Protocol,
//    var host: String
//) {
//    enum class Protocol {
//        HTTP, HTTPS
//    }
//
//    private var _retrofit: Retrofit? = null
//
//    val retrofit: Retrofit
//        get() {
//            if (_retrofit == null) {
//                _retrofit = Retrofit.Builder()
//                    .baseUrl(when (protocol) {
//                        Protocol.HTTP -> "http://"
//                        Protocol.HTTPS -> "https://"
//                    } + host)
//                    .client(client)
//                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                    .addConverterFactory(ScalarsConverterFactory.create())
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build()
//            }
//            return _retrofit!!
//        }
//
//    val client: OkHttpClient
//        get() = OkHttpClient.Builder()
//            .connectTimeout(1900, TimeUnit.SECONDS)
//            .readTimeout(1900, TimeUnit.SECONDS)
//            .writeTimeout(1900, TimeUnit.SECONDS)
//            .addInterceptor { chain ->
//                val original = chain.request()
//                val request = original.newBuilder()
//                    .header("User-Agent", android.os.Build.MODEL)
//                    .method(original.method, original.body)
//                    .build()
//                chain.proceed(request)
//            }
//            .addInterceptor(
//                HttpLoggingInterceptor {
//                    Log.d("OkHttp", it)
//                }.apply {
//                    level = HttpLoggingInterceptor.Level.BODY
//                }
//            )
//            .build()
//}
