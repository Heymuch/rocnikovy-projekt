package cz.feicargo.route.entity

import kotlin.uuid.Uuid

data class RailwayEdge(
    val nodeAId: Uuid,
    val nodeBId: Uuid,
    val distanceMeters: Int,
)
