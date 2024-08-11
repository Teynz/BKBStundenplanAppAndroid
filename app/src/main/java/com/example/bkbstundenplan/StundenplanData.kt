package com.example.bkbstundenplan

import android.os.Parcelable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class StundenplanData(var LoginName: @RawValue MutableState<String> = mutableStateOf("schueler"), var Passwort: @RawValue MutableState<String> = mutableStateOf("stundenplan")): Parcelable
{


}

