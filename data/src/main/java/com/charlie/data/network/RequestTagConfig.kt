package com.charlie.data.network

import kotlin.collections.get

/**
 * This feature will help with developer market support
 * Logic: Request will be tagged with a shortcode amd the Http response
 * Used purely for UI error display.
 */
object RequestTagConfig {

    private val tagMap = mapOf(
        "v1/survey/uploadResponse" to "UPLD",
    )

    fun getTag(urlPath: String): String {
        val matchedKey = tagMap.keys.firstOrNull { urlPath.contains(it) }
        return tagMap[matchedKey] ?: "UNKNOWN"
    }

    fun formatError(urlPath: String, httpCode: Int): String {
        return "${getTag(urlPath)}_$httpCode"
    }
}