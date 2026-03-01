package com.charlie.data.repository

import com.charlie.data.local.dao.FarmerDao
import com.charlie.domain.model.Farmer
import com.charlie.domain.repository.FarmerRepository
import javax.inject.Inject

class FarmerRepositoryImpl @Inject constructor(
    private val farmerDao: FarmerDao
) : FarmerRepository {
    override suspend fun syncFarmers(farmers: List<Farmer>) {
        TODO("Not yet implemented")
    }

    override suspend fun getFarmerById(farmerId: Int): Farmer? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllFarmers(): List<Farmer> {
        TODO("Not yet implemented")
    }

}
