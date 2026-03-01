package com.charlie.data.repository

import com.charlie.data.local.dao.FarmerDao
import com.charlie.data.mappers.toDomain
import com.charlie.data.mappers.toEntity
import com.charlie.domain.model.Farmer
import com.charlie.domain.repository.FarmerRepository
import javax.inject.Inject

class FarmerRepositoryImpl @Inject constructor(
    private val farmerDao: FarmerDao
) : FarmerRepository {
    override suspend fun syncFarmers(farmers: List<Farmer>) {
        farmerDao.insertAll(farmers.map { it.toEntity() })
    }

    override suspend fun getFarmerById(farmerId: Int): Farmer? {
        return farmerDao.getFarmerById(farmerId)?.toDomain()
    }

    override suspend fun getAllFarmers(): List<Farmer> {
        return farmerDao.getAllFarmers().map { it.toDomain() }
    }

}
