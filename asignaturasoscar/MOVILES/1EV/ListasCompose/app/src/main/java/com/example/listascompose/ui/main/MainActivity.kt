package com.example.listascompose.ui.main


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listascompose.R
import com.example.listascompose.di.AppModule
import com.example.listascompose.domain.model.Persona
import com.example.listascompose.domain.model.Libro
import com.example.listascompose.ui.theme.AppTheme
import com.example.listascompose.ui.theme.onBarbieDarkMediumContrast
import com.example.listascompose.ui.theme.onPinkyPromiseContainerLight
import com.example.listascompose.ui.theme.pinkyPromiseContainerDark
import com.example.listascompose.ui.theme.pinkyPromiseContainerLight
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                MainPantalla()
            }
        }
    }
}


@Composable
fun MainPantalla(
    viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(
            AppModule.dameListaUseCase,
            AppModule.addLibroUseCase,
            AppModule.deletePersonaUseCase
        )
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember() { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(error)
            }
            viewModel.limpiarError()
        }
    }

    MainPantallaContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onPersonaChange = viewModel::onPersonaChange,
        onDeletePersonaSeleccionada = viewModel::onDeletePersonaSeleccionada,
        onAddLibro = viewModel::onAddLibro,
        onSeleccionarPersona = viewModel::onSeleccionarPersona
    )
}

@Composable
fun MainPantallaContent(
    state: MainState,
    snackbarHostState: SnackbarHostState,
    onPersonaChange: (Persona) -> Unit,
    onDeletePersonaSeleccionada: () -> Unit,
    onAddLibro: () -> Unit,
    onSeleccionarPersona: (Persona) -> Unit
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        )
        {
            Row(
                modifier = Modifier
                    .weight(0.1F)
                    .background(pinkyPromiseContainerLight)
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.school),
                    contentDescription = "school hat",
                    modifier = Modifier
                        .size(40.dp)
                        .weight(0.2F),
                    tint = onPinkyPromiseContainerLight,

                    )
                Text(
                    modifier = Modifier.weight(0.8F),
                    text = "Mi biblioteca",
                    color = onPinkyPromiseContainerLight,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,

                    )
            }
            Row(
                modifier = Modifier
                    .weight(0.45F)
                    .background(pinkyPromiseContainerLight)
                    .fillMaxWidth()
            ) {
                ListaPersonas(
                    state = state,
                    onSeleccionarPersona = onSeleccionarPersona,
                    onDeletePersonaSeleccionada = onDeletePersonaSeleccionada,
                ) {}
            }
            Row(
                modifier = Modifier
                    .weight(0.45F)
                    .background(pinkyPromiseContainerDark)
                    .fillMaxWidth()
                    .fillMaxSize()
            ) {

            }

        }
    }
}


@Composable
fun ListaPersonas(
    state: MainState,
    onSeleccionarPersona: (Persona) -> Unit,
    onDeletePersonaSeleccionada: () -> Unit,
    function: () -> Unit,
) {
    Column() {
        Row() {
            Column() {
                Icon(
                    painter = painterResource(id = R.drawable.personadd),
                    contentDescription = "person",
                    modifier = Modifier
                        .size(30.dp),
                    tint = onPinkyPromiseContainerLight,
                )
            }
            Column() {
                Text(
                    text = "Personas",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column() {
                ElevatedButton(
                    onClick = { onDeletePersonaSeleccionada() },
                    enabled = state.personaSeleccionado != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                    ),
                    modifier = Modifier
                        .background(Color.Red)


                ) {
                    Text(
                        text = "Borrar persona",
                        color = Color.White
                    )
                }
            }
        }
        Row() {
            LazyRow(
            ) {
                items(
                    items = state.personas,
                    key = { persona -> persona.id },
                ) { persona ->
                    ItemPersona(persona = persona, onSeleccionarPersona = onSeleccionarPersona)
                }
            }
        }
        Row() {
            LazyColumn(){
                items(
                    items = state.personas,
                    key = { persona -> persona.id },
            ) { persona ->
               ItemLibro(persona = persona, libro = Libro("", "", "", false))
            }}
        }
    }
}


@Composable
fun ItemPersona(
    persona: Persona,
    onSeleccionarPersona: (Persona) -> Unit,
) {
    Card(
        onClick = { onSeleccionarPersona(persona) },
        modifier = Modifier

            .padding(2.dp)
    ) {
        Column() {
            Row(

            ) {
                Button(
                    onClick = { Unit },
                    shape = CircleShape,
                    modifier = Modifier.size(70.dp)
                ) {
                    Text(
                        text = persona.nombre[0].toString()
                    )
                }
            }
            Row(

            ) {
                Text(
                    text = "${persona.nombre}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(

            ) {
                Text(
                    text = "${persona.nombre}",
                    fontSize = 15.sp,
                )
            }
        }
    }
}

@Composable
fun ItemLibro(
    persona: Persona,
    libro: Libro
) {
    Text(
        text = "Lista de ${persona.nombre}"
    )
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)

    ) {
        Row()
        {
            Text(
                text = libro.nombre
            )
        }
        Row() {
            Text(
                text = libro.autor
            )
        }
        Row() {
            Column() {
                Button(
                    onClick = { Unit },
                    modifier = Modifier.size(20.dp)
                ) {
                    Text(
                        text = if (libro.leido) {
                            "Leído"
                        } else {
                            "No leído"
                        }
                    )
                }
            }
            Column() {
                Button(
                    onClick = { Unit },
                    modifier = Modifier.size(20.dp)
                ) {
                    Text(
                        text = libro.genero
                    )
                }
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {
        MainPantallaContent(
            state = MainState(
                persona = Persona("", false, mutableListOf()),
                personas = listOf(
                    Persona(
                        "Lucía Álvarez", true, mutableListOf(
                            Libro(
                                "El nombre del viento",
                                "Patrick Rothfuss",
                                "Fantasía",
                                true
                            ),
                            Libro("Dune", "Frank Herbert", "Ciencia ficción", false),
                            Libro("Fundación", "Isaac Asimov", "Aventura", true),
                            Libro("El Hobit", "J.R.R. Tolkien", "Fantasía", false),
                            Libro("Neuromante", "William Gibson", "Aventura", true),
                        )
                    ),
                    Persona(
                        "Marco Castillo", true, mutableListOf(
                            Libro(
                                "El nombre del viento",
                                "Patrick Rothfuss",
                                "Fantasía",
                                true
                            ),
                            Libro("Dune", "Frank Herbert", "Ciencia ficción", false),
                        )
                    ),
                    Persona(
                        "Sofía Pérez", true, mutableListOf(
                            Libro(
                                "El nombre del viento",
                                "Patrick Rothfuss",
                                "Fantasía",
                                true
                            ),
                            Libro("Dune", "Frank Herbert", "Ciencia ficción", false),
                            Libro("Fundación", "Isaac Asimov", "Aventura", true),
                            Libro("El Hobit", "J.R.R. Tolkien", "Fantasía", false),
                            Libro("Crepúsculo", "Stephanie Meyer", "Fantasía", true),
                            Libro("Luna Nueva", "Stephanie Meyer", "Fantasía", true),
                            Libro("Eclipse", "Stephanie Meyer", "Fantasía", true),
                        )
                    ),
                    Persona(
                        "Daniel Rodríguez", true, mutableListOf(
                            Libro(
                                "El nombre del viento",
                                "Patrick Rothfuss",
                                "Fantasía",
                                true
                            ),
                        )
                    ),
                ),
                personaSeleccionado = null,
                selected = false,
                error = null
            ),
            snackbarHostState = SnackbarHostState(),
            onPersonaChange = {},
            onDeletePersonaSeleccionada = {},
            onSeleccionarPersona = {},
            onAddLibro = {}
        )
    }
}
