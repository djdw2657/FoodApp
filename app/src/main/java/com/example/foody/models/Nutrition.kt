package com.example.foody.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Nutrition(
    @SerializedName("nutrients")
    val nutrients: List<NutrientX>,
): Parcelable