package bkb.stundenplan.app.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import bkb.stundenplan.app.ScrapingEMail
import bkb.stundenplan.app.ViewModelStundenplanData

object LehrerEMail {


    @Composable
    fun MainPage(
        modifier: Modifier = Modifier, viewModel: ViewModelStundenplanData, dialogState: Boolean, onDialogStateChange: (Boolean) -> Unit
    ) {
       var filter:String by rememberSaveable{mutableStateOf("")}
        Column()
        {
            TextField(modifier = Modifier.border(
                width = 0.3.dp, color = Color.Gray, shape = RoundedCornerShape(80.dp)
            ),
                value = filter,
                onValueChange = { filter = it },
                label = { Text("Element Suchen") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                ),
                shape = RoundedCornerShape(80.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent, // No underline when unfocused
                    focusedIndicatorColor = Color.Transparent    // No underline when focused
                )
            )

        }



    }


    @Composable
    fun LehrerEMail(modifier:Modifier = Modifier, filter:String, listLehrerEMail:List<ScrapingEMail.LehrerEMail>)
    {
       /* listLehrerEMail.forEach
        {}*/


    }


}