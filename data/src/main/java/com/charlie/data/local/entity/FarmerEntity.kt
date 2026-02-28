package com.charlie.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Farmer Entity - Farmer KYC. Base of Survey start.
 * Model Assumption, the local Farmer DB will be preloaded during remote sync on login to fetch registered farmers.
 * @param remoteId is the key identifier of the farmer on remote.
 * */
@Entity(tableName = "farmers")
data class FarmerEntity(
    @PrimaryKey
    val remoteId: Int,
    val name: String,
    val phoneNumber: String,
    val region: String
)
