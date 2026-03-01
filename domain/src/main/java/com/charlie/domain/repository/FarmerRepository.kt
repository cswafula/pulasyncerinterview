package com.charlie.domain.repository

import com.charlie.domain.model.Farmer

interface FarmerRepository {
    suspend fun syncFarmers(farmers: List<Farmer>)
    suspend fun getFarmerById(farmerId: Int): Farmer?
    suspend fun getAllFarmers(): List<Farmer>
}