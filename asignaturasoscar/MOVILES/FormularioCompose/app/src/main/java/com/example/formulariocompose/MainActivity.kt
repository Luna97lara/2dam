package com.example.formulariocompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.R
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Label
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
        onClickButton = viewModel::onClickButton
    )
}
@Composable
fun MainPantallaContent(
    modifier: Modifier = Modifier,
    state: MainState,
    onChangeNombre : (String) -> Unit,
    onChangeApellidos : (String) -> Unit,
    onChangeEmail : (String) -> Unit,
    onChangeDireccion : (String) -> Unit,
    onClickButton : () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Column(modifier = Modifier.padding(innerPadding)) {
                    Row {
                        Text("Nombre: ")
                    }
                    Row {
                        OutlinedTextField(
                            value = state.nombre,
                            onValueChange = { onChangeNombre(it) }

                        )
                    }
                }
            }
            Row {
                Column(modifier = Modifier.padding(innerPadding)) {
                    Row {
                        Text("Apellidos: ")
                    }
                    Row {
                        OutlinedTextField(
                            value = state.apellidos,
                            onValueChange = { onChangeApellidos(it) }
                        )
                    }
                }
            }
            Row {
                Column(modifier = Modifier.padding(innerPadding)) {
                    Row {
                        Text("Email: ")
                    }
                    Row {
                        OutlinedTextField(
                            value = state.email,
                            onValueChange = { onChangeEmail(it) }
                        )
                    }
                }
            }

            Row {
                Column(modifier = Modifier.padding(innerPadding)) {
                    Row {
                        Text("Dirección: ")
                    }
                    Row {
                        OutlinedTextField(
                            value = state.direccion,
                            onValueChange = { onChangeDireccion(it) }
                        )
                    }
                }
            }

            Row {
                Column(modifier = Modifier.padding(innerPadding)) {
                    Row {
                        Button(onClick = {
                            onClickButton()

                        }) {
                            Text("Enviar")
                        }
                    }
                }
            }
            Row {
                Column(modifier = Modifier.padding(innerPadding)) {
                    Row {
                        Text("${state.textoContador}${state.contador}")
                    }
                }
            }
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
    fun MainPantallaContentPreview() {
        FormularioComposeTheme {
            MainPantallaContent(
                state = MainState(
                    nombre = "Luna",
                    apellidos = "Lara Moreno",
                    email = "luna97@gmail.es",
                    direccion = "Calle Gandhi, 71",
                    contador = 0,
                    textoContador = "Nº envíos: "
                ),
                onChangeNombre = { },
                onChangeApellidos = { },
                onChangeEmail = { },
                onChangeDireccion = { },
                onClickButton = { }
            )
        }
    }