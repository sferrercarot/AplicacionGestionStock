package com.domatix.yevbes.nucleus.core.entities.session.authenticate

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Envoltorio JSON-RPC para authenticate
 */
data class AuthenticateReqBody @JvmOverloads constructor(
        @field:Expose
        @field:SerializedName("jsonrpc")
        val jsonrpc: String = "2.0",

        @field:Expose
        @field:SerializedName("method")
        val method: String = "call",

        @field:Expose
        @field:SerializedName("params")
        var params: AuthenticateParams = AuthenticateParams()
)
