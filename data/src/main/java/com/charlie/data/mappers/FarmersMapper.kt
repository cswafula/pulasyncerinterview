package com.charlie.data.mappers

import com.charlie.data.local.entity.FarmerEntity
import com.charlie.domain.model.Farmer

fun Farmer.toEntity() = FarmerEntity(
    remoteId = remoteId,
    name = name,
    phoneNumber = phoneNumber,
    region = region
)

fun FarmerEntity.toDomain() = Farmer(
    remoteId = remoteId,
    name = name,
    phoneNumber = phoneNumber,
    region = region
)