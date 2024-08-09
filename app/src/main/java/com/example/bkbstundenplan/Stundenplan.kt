package com.example.bkbstundenplan

import android.os.Parcel
import android.os.Parcelable

import kotlinx.parcelize.Parcelize

@Parcelize
data class Stundenplan(var LoginName: String = "schueler" ,var Passwort: String = "stundenplan"): Parcelable
{

}

