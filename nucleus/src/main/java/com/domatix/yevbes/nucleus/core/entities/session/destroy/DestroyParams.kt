package com.domatix.yevbes.nucleus.core.entities.session.destroy

import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DestroyParams(

        @field:Expose
        @field:SerializedName("context")
        val context: JsonObject = JsonObject()
)
