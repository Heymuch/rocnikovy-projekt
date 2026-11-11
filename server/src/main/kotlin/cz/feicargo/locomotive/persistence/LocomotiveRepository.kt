package cz.feicargo.locomotive.persistence

import cz.feicargo.locomotive.entity.Locomotive

interface LocomotiveRepository { suspend fun selectByIds(ids: List<Long>): List<Locomotive> }
