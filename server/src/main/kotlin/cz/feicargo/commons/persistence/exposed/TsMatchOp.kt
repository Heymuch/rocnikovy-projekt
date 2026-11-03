package cz.feicargo.commons.persistence.exposed

import org.jetbrains.exposed.v1.core.ComplexExpression
import org.jetbrains.exposed.v1.core.CustomFunction
import org.jetbrains.exposed.v1.core.Expression
import org.jetbrains.exposed.v1.core.FloatColumnType
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.QueryBuilder
import org.jetbrains.exposed.v1.core.TextColumnType
import org.jetbrains.exposed.v1.core.stringLiteral
import org.jetbrains.exposed.v1.core.stringParam

class TsMatchOp(
    private val vector: Expression<*>,
    private val query: Expression<*>,
) : Op<Boolean>(),
    ComplexExpression {
    override fun toQueryBuilder(queryBuilder: QueryBuilder) =
        queryBuilder {
            append(vector)
            append(" @@ ")
            append(query)
        }
}

infix fun Expression<*>.tsMatch(query: Expression<*>) = TsMatchOp(this, query)

fun toTsQuery(query: String): Expression<String> =
    CustomFunction(
        "to_tsquery",
        TextColumnType(),
        stringLiteral("simple"),
        CustomFunction<String>("immutable_unaccent", TextColumnType(), stringParam(query)),
    )

fun tsRank(
    vector: Expression<*>,
    query: Expression<*>,
): Expression<Float> = CustomFunction("ts_rank", FloatColumnType(), vector, query)

// Převod uživatelského vstupu na prefixovou tsquery: "plzen hl" -> "plzen:* & hl:*"
// Dělí na čemkoli kromě písmen a číslic, takže v tokenech nezůstanou znaky se speciálním
// významem v tsquery ani interpunkce; prázdný výsledek = nic k hledání
fun String.toPrefixTsQuery() =
    split(Regex("[^\\p{L}\\p{N}]+"))
        .filter(String::isNotEmpty)
        .joinToString(" & ") { "$it:*" }
