package com.domatix.yevbes.nucleus.core

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser

fun String.decryptAES(): String {
    // Esta es una función de ejemplo, adapta según tu necesidad
    return this.reversed()
}

fun String.toJsonObject(): JsonObject {
    return JsonParser().parse(this).asJsonObject
}

fun String.toJsonElement(): JsonElement {
    return JsonParser.parseString(this)
}