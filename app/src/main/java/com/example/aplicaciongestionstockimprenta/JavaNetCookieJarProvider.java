//package com.example.aplicaciongestionstockimprenta;
//
//import java.net.CookieManager;
//import java.net.CookiePolicy;
//
//import okhttp3.JavaNetCookieJar;
//import okhttp3.CookieJar;
//
//public class JavaNetCookieJarProvider {
//
//    // ✅ Guardamos el CookieManager para poder acceder luego
//    public static CookieManager cookieManager;
//
//    public static CookieJar getCookieJar() {
//        cookieManager = new CookieManager();
//        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
//        return new JavaNetCookieJar(cookieManager);
//    }
//}
