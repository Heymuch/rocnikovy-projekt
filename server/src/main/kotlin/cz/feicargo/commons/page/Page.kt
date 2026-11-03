package cz.feicargo.commons.page

import cz.feicargo.commons.entity.Entity

data class Page<T : Entity>(
    val items: Iterable<T>,
    val number: Page.Number,
    val size: Page.Size,
    val totalItems: Page.TotalItems,
) {
    @JvmInline
    value class Size(
        val value: UInt,
    )

    @JvmInline
    value class Number(
        val value: UInt,
    )

    @JvmInline
    value class TotalItems(
        val value: UInt,
    )
}
