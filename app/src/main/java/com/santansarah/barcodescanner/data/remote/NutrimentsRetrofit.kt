package com.santansarah.barcodescanner.data.remote

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NutrimentsRetrofit(
    @SerialName("carbohydrates_serving") val carbohydrates: StringDouble = StringDouble(),
    @SerialName("energy-kcal_serving") val calories: StringDouble = StringDouble(),
    @SerialName("cholesterol_serving") val cholesterol: StringDouble? = null,
    @SerialName("fat_serving") val fat: StringDouble? = null,
    @SerialName("saturated-fat_serving") val saturatedFat: StringDouble? = null,
    @SerialName("monounsaturated-fat_serving") val monounsaturatedFat: StringDouble? = null,
    @SerialName("polyunsaturated-fat_serving") val polyunsaturatedFat: StringDouble? = null,
    @SerialName("proteins_serving") val protein: StringDouble? = null,
    @SerialName("fiber_serving") val fiber: StringDouble? = null,
    @SerialName("sodium_serving") val sodium: StringDouble? = null,
    @SerialName("sugars_serving") val sugar: StringDouble? = null,
    @SerialName("potassium_serving") val potassium: StringDouble? = null,
)


@Serializable
@JvmInline
value class StringDouble(private val jsonValue: String = "0.00") {
    val asDouble: Double
        get() = try {
            jsonValue.toDouble()
        } catch (e: Exception) {
            0.00
        }
}

fun StringDouble?.formatToGrams(): String = "%.1f".format(this?.asDouble ?: 0.00)
fun StringDouble?.formatToMgs(): String = "%.1f".format((this?.asDouble ?: 0.00).times(1000))

