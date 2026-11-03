package cz.feicargo.commons.persistence.exposed

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ColumnType
import org.jetbrains.exposed.v1.core.Table

class TsVectorColumnType : ColumnType<String>() {
    override fun sqlType() = "TSVECTOR"

    override fun valueFromDB(value: Any) = value.toString()
}

fun Table.tsVector(name: String): Column<String> = registerColumn(name, TsVectorColumnType()).databaseGenerated()
