package com.example.bkbstundenplan

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale


@Parcelize
data class StundenplanData(
        var loginName: @RawValue MutableState<String> = mutableStateOf("schueler"),
        var passwort: @RawValue MutableState<String> = mutableStateOf("stundenplan")
                          ) : Parcelable
{




    @RequiresApi(Build.VERSION_CODES.O)
    fun firstMondayofWeek(): LocalDate
    {
        val now = LocalDate.now()
        val fieldISO = WeekFields.of(Locale.GERMANY).dayOfWeek()
        return(now.with(fieldISO, 1))



    }

}


