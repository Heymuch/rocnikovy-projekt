package cz.feicargo.station.persistence

import cz.feicargo.commons.persistence.Repository
import cz.feicargo.station.entity.Station

interface StationRepository : Repository.Read<Station> {
    suspend fun selectByIds(ids: List<Station.Id>): List<Station>
}
