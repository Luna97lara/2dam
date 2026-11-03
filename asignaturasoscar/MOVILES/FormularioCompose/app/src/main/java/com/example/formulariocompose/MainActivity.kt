package com.example.formulariocompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.formulariocompose.ui.theme.FormularioComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FormularioComposeTheme {
                MainPantalla()
            }
        }
    }
}

@Composable
fun MainPantalla(
    modifier: Modifier = Modifier,
    viewModel : MainViewModel = viewModel(),
) {

    val state by viewModel.state.collectAsStateWithLifecycle()


    MainPantallaContent(
        modifier = modifier,
        state = state,
        onChangeNombre = viewModel::onChangeNombre,
        onChangeApellidos = viewModel::onChangeApellidos,
        onChangeEmail = viewModel::onChangeEmail,
        onChangeDireccion = viewModel::onChangeDireccion,
    )
}

@Composable
fun MainPantallaContent(
    modifier: Modifier = Modifier,
    state: MainState,
    onChangeNombre : (String) -> Unit,
    onChangeApellidos : (String) -> Unit,
    onChangeEmail : (String) -> Unit,
    onChangeDireccion : (String) -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier= Modifier.padding(innerPadding)) {
            Row {
                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = {onChangeNombre(it)}

                    )
            }
            Row {
                OutlinedTextField(
                    value = state.apellidos,
                    onValueChange = {onChangeApellidos(it)}
                    )
            }
            Row {
                OutlinedTextField(
                    value = state.email,
                    onValueChange = {onChangeEmail(it)}
                    )
            }

            }
            Row {
                OutlinedTextField(
                    value = state.direccion,
                    onValueChange = {onChangeDireccion(it)}


                    )

            }
        }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FormularioComposeTheme {
        Greeting("Android")
    }
}