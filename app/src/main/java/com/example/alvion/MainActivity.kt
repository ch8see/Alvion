package com.example.alvion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.alvion.ui.theme.ALVIONTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PreviewMessageCard()
        }
    }
}

data class Message(val author: String, val body: String)

@Composable
fun MessageCard(msg: Message) {
    Row {
//        Image(
//            painter = painterResource(R.drawable.randomscreenshot),
//            contentDescription = "Contact profile picture"
//        )
        Column {
            Text(text = msg.author)
            Text(text = msg.body)
        }
    }
}

@Preview
@Composable
fun PreviewMessageCard(){
    MessageCard(msg = Message("Tairq", "testing testing testing"))
}
