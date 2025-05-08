package com.domatix.yevbes.nucleus.core.entities.session.authenticate

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Parámetros de la llamada JSON-RPC /web/session/authenticate
 */
data class AuthenticateParams @JvmOverloads constructor(
        @field:Expose
        @field:SerializedName("db")
        var db: String = "",

        @field:Expose
        @field:SerializedName("login")
        var login: String = "",

        @field:Expose
        @field:SerializedName("password")
        var password: String = ""
)
