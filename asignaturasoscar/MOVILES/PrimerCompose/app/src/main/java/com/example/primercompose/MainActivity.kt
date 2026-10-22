package com.example.primercompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices.NEXUS_7
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.primercompose.ui.theme.PrimerComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent ( content = {
            PrimerComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(verticalArrangement = Arrangement.spacedBy(100.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize())
                    {
                        Greeting(
                            name = "Android",
                            modifier = Modifier.padding(innerPadding)
                        )

                        Greeting(
                            name = "Tus muertos",
                            modifier = Modifier.padding(innerPadding)
                        )

                        Greeting(
                            name = "Tus muertos 2: esta vez es personal",
                            modifier = Modifier.padding(innerPadding)
                        )

                    }
                }
            }
        })
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true, device = "spec:parent=Nexus 7,orientation=portrait",
    showSystemUi = true)
@Composable
fun GreetingPreview() {
    PrimerComposeTheme {
        Greeting("Android")
        Greeting("Tu puta madre")
        Greeting("Tu puta madre 2")
    }
}