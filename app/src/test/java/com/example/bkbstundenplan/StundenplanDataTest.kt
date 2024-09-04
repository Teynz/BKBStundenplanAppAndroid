package com.example.bkbstundenplan

import org.junit.Test

class StundenplanDataTest {

    @Test
    fun UpdateURLStundenplanTest()
    {
        var login = StundenplanData()

        login.UpdateURLStundenplan()

        System.out.println(login.URLStundenplan.value)


    }
}