package bkb.stundenplan.app.ui


import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bkb.stundenplan.app.R
import bkb.stundenplan.app.ScrapingEMail
import coil3.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object LehrerEMailPage {


    @Composable
    fun MainPage(
        modifier: Modifier = Modifier
    ) {
        var filter: String by rememberSaveable { mutableStateOf("") }

        var listLehrerEMail: List<ScrapingEMail.LehrerEMail>? by rememberSaveable {
            mutableStateOf(
                listOf()
            )
        }

        var isLoading: Boolean by remember { mutableStateOf(true) } // State to track loading
        val scrapingEMail = ScrapingEMail()

        LaunchedEffect(key1 = Unit) {
            isLoading = true
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    listLehrerEMail = scrapingEMail.getLehrerEMail()
                    isLoading = false
                }
            } catch (e: Exception) {

                println("EMails konnten nicht geladen werden")
                isLoading = false

            }
        }





        Column(modifier = modifier) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                TextField(
                    modifier = Modifier.border(
                        width = 0.3.dp, color = Color.Gray, shape = RoundedCornerShape(80.dp)
                    ),
                    value = filter,
                    onValueChange = { filter = it },
                    label = { Text(stringResource(R.string.element_suchen)) },
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

            if (!isLoading) {
                LehrerEMail(listLehrerEMail = listLehrerEMail?.filter {
                    it.mail.contains(other = filter, ignoreCase = true) || it.pictureLink.contains(
                        other = filter, ignoreCase = true
                    )
                })

            }
        }


    }


    //Source for sendMail : https://stackoverflow.com/questions/72731148/how-can-i-open-gmail-when-click-the-button-in-jetpack-compose
    fun Context.sendMail(to: String) {
        try {
            // 1. Include the recipient directly in the mailto URI
            val mailUri = Uri.parse("mailto:$to?subject=Hello&body=Message content")

            // 2. Create intent with ACTION_SENDTO and the complete URI
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            }

            // 3. Verify app availability
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                // 4. Handle no email app scenario
                Toast.makeText(this, "No email app installed.", Toast.LENGTH_SHORT).show()
                Log.w("SendEmail", "No email app available for $mailUri")
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error launching email", Toast.LENGTH_SHORT).show()
            Log.e("SendEmail", "Failed to send email to $to", e)
        }
        }


    fun String.filterEMailString(): String {
        return this.trim().replace("E-Mail: ", "")

    }

    @Composable
    fun LehrerEMail(
        modifier: Modifier = Modifier, listLehrerEMail: List<ScrapingEMail.LehrerEMail>?
    ) {

        listLehrerEMail?.let() { listLehrerEMail ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                items(listLehrerEMail.size) { index ->

                    val context = LocalContext.current
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clickable { context.sendMail(listLehrerEMail[index].mail.filterEMailString()) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        AsyncImage(
                            modifier = Modifier
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(10.dp)),
                            model = listLehrerEMail[index].pictureLink,
                            contentDescription = null,

                            )
                        Text(
                            text = listLehrerEMail[index].mail,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.size(10.dp))

                }


            }
        }


    }


}