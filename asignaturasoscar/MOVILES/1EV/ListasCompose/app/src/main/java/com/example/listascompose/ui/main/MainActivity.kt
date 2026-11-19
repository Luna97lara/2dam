package com.example.listascompose.ui.main


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listascompose.R
import com.example.listascompose.di.AppModule
import com.example.listascompose.domain.model.Alumno
import com.example.listascompose.domain.model.Boletin
import com.example.listascompose.ui.theme.AppTheme
import com.example.listascompose.ui.theme.barbieDark
import com.example.listascompose.ui.theme.barbieLight
import com.example.listascompose.ui.theme.onBarbieDark
import com.example.listascompose.ui.theme.onBarbieDarkMediumContrast
import com.example.listascompose.ui.theme.onBarbieLight
import com.example.listascompose.ui.theme.onPinkyPromiseContainerLight
import com.example.listascompose.ui.theme.pinkyPromiseContainerLight
import com.example.listascompose.ui.theme.pinkyPromiseLightMediumContrast
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
            AppModule.addAlumnoUseCase,
            AppModule.deleteAlumnoUseCase
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
        onAlumnoChange = viewModel::onAlumnoChange,
        onDeleteAlumnoSeleccionado = viewModel::onDeleteAlumnoSeleccionado,
        onAddAlumno = viewModel::onAddAlumno,
        onSeleccionarAlumno = viewModel::onSeleccionarAlumno
    )
}

@Composable
fun MainPantallaContent(
    state: MainState,
    snackbarHostState: SnackbarHostState,
    onAlumnoChange: (Alumno) -> Unit,
    onDeleteAlumnoSeleccionado: () -> Unit,
    onAddAlumno: () -> Unit,
    onSeleccionarAlumno: (Alumno) -> Unit
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
                    text = "Gestión de alumnos",
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
                ListaAlumnos(
                    state = state,
                    onSeleccionarAlumno = onSeleccionarAlumno

                    )
            }
            Row(
                modifier = Modifier
                    .weight(0.45F)
                    .background(pinkyPromiseContainerLight)
                    .fillMaxWidth()
                    .fillMaxSize()
            ) {
                FormularioAlumnos(
                    state = state,
                    onAlumnoChange = onAlumnoChange,
                    onDeleteAlumnoSeleccionado = onDeleteAlumnoSeleccionado,
                    onAddAlumno = onAddAlumno
                )

            }

        }
    }
}


@Composable
fun ListaAlumnos(
    state: MainState,
    onSeleccionarAlumno: (Alumno) -> Unit
) {
    Column() {
        Text(
            text = "Lista de alumnos (${state.alumnos.size})",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = state.alumnos,
                key = { alumno -> alumno.id },
            ) { alumno ->
                ItemAlumno(alumno = alumno, onSeleccionarAlumno = onSeleccionarAlumno)
            }
        }
    }

}

@Composable
fun ItemAlumno(
    alumno: Alumno,
    onSeleccionarAlumno: (Alumno) -> Unit
) {
    Card(
        onClick = {onSeleccionarAlumno(alumno)},
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)

    ) {
        Row()
        {
            Column(
                modifier = Modifier.weight(0.2F)
            ) {
                Button(
                    onClick = { Unit},
                    shape = CircleShape,
                    modifier = Modifier.size(70.dp)
                ) {
                    Text(
                        text = alumno.nombre[0].toString() + alumno.apellidos[0].toString(),
                    )
                }
            }
            Column(
                modifier = Modifier.weight(0.6F)
            ) {
                Text(
                    text = "${alumno.nombre} ${alumno.apellidos}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${alumno.curso} · ${alumno.edad} años",
                    fontSize = 15.sp,
                )
                Text(
                    text = alumno.correo,
                )
            }
            Column(modifier = Modifier.weight(0.15F),) {
                Card(
                    colors = CardColors(
                        containerColor = if (alumno.activo) {
                            barbieDark
                        } else {
                            barbieLight
                        },
                        contentColor = if (alumno.activo) {
                            onBarbieDark
                        } else {
                            onBarbieLight
                        },
                        disabledContentColor = Color.Yellow,
                        disabledContainerColor = Color.Yellow
                    ),
                ){
                    Text(
                        text = if(alumno.activo){
                            "Activo"
                        } else {
                            "Inactivo"
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FormularioAlumnos(
    state: MainState,
    onAlumnoChange: (Alumno) -> Unit,
    onDeleteAlumnoSeleccionado: () -> Unit,
    onAddAlumno: () -> Unit
    ) {
    Column() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(0.2F)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.personadd),
                contentDescription = "school hat",
                modifier = Modifier
                    .size(40.dp)
                    .weight(0.2F),
                tint = onBarbieDarkMediumContrast
            )
            Text(
                text = "Añadir nuevo alumno",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = onBarbieDarkMediumContrast,
                modifier = Modifier.weight(0.8F)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(0.25F)
        ) {
            OutlinedTextField(
                value = state.alumno.nombre,
                onValueChange = { nombre: String -> onAlumnoChange(state.alumno.copy(nombre = nombre)) },
                label = { Text("Nombre del Alumno") },
                modifier = Modifier.weight(0.5F)
            )
            OutlinedTextField(
                value = state.alumno.apellidos,
                onValueChange = { apellidos: String -> onAlumnoChange(state.alumno.copy(apellidos = apellidos)) },
                label = { Text("Apellidos") },
                modifier = Modifier.weight(0.5F)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(0.25F)
        ) {
            OutlinedTextField(
                value = state.alumno.curso,
                onValueChange = { curso: String -> onAlumnoChange(state.alumno.copy(curso = curso)) },
                label = { Text("Curso") },
                modifier = Modifier.weight(0.5F)
            )
            OutlinedTextField(
                value = state.alumno.edad,
                onValueChange = { edad: String ->
                    onAlumnoChange(state.alumno.copy(edad = edad))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                label = { Text("Edad") },
                modifier = Modifier.weight(0.5F)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(0.25F),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = state.alumno.correo,
                onValueChange = { correo: String -> onAlumnoChange(state.alumno.copy(correo = correo)) },
                label = { Text("Correo") },
                modifier = Modifier.weight(0.53F)
            )

            Checkbox(
                checked = state.alumno.activo,
                onCheckedChange = {checked: Boolean -> onAlumnoChange(state.alumno.copy(activo=checked))},
                modifier = Modifier.weight(0.1F)
            )
            Text(
                text = "Alumno activo",
                fontSize = 15.sp,
                modifier = Modifier.weight(0.4F)
            )

        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(0.25F),
            ) {
            ElevatedButton(
                onClick = {onAddAlumno()},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                ),
                modifier = Modifier.background(pinkyPromiseLightMediumContrast)

            ) {
                Text(
                    text = "Añadir Alumno",
                    color = Color.White
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(0.25F)
        ) {
            ElevatedButton(
                onClick = {onDeleteAlumnoSeleccionado()},
                enabled = state.alumnoSeleccionado!=null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    disabledContainerColor = Color.Blue
                ),
                modifier = Modifier.fillMaxSize()
                    .background(Color.Red)

            ) {
                Text(
                    text = "Borrar Alumno Seleccionado",
                    color = Color.White
                )
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
                alumno = Alumno("", "", "", "0", "", false,mutableListOf()),
                alumnos = listOf(
                    Alumno(
                        "Lucía",
                        "Álvarez García",
                        "1ºDAM",
                        "18",
                        "lucia.alvarez@iesquevedo.es", true,
                        mutableListOf(
                            Boletin("Programación", 3),
                            Boletin("Bases de Datos", 7),
                            Boletin("Lenguaje de Marcas", 8),
                            Boletin("Sistemas Informáticos", 5),
                        )
                    ),
                    Alumno(
                        "Marco",
                        "Castillo Ruiz",
                        "2ºASIR",
                        "19",
                        "marco.castillo@iesquevedo.es", true,
                        mutableListOf(
                            Boletin("Seguridad", 3),
                            Boletin("Empresa", 7),
                            Boletin("Lenguaje de Marcas", 8),
                            Boletin("Sistemas Informáticos", 5)
                        )
                    ),
                    Alumno(
                        "Sofía",
                        "Pérez Martín",
                        "1ºSMR",
                        "17",
                        "sofia.perez@iesquevedo.es", false,
                        mutableListOf(
                            Boletin("FOL", 3),
                            Boletin("Inglés", 7),
                            Boletin("Redes", 8),
                            Boletin("Sistemas Informáticos", 5)
                        )
                    ),
                    Alumno(
                        "Daniel",
                        "Rodríguez López",
                        "2ºDAM",
                        "20",
                        "daniel.rodriguez@iesquevedo.es", true,
                        mutableListOf(
                            Boletin("PSP", 3),
                            Boletin("Acceso a Datos", 2),
                            Boletin("Móviles", 8),
                            Boletin("SGE", 7)
                        )
                    ),
                    Alumno(
                        "Elena",
                        "Moreno Sánchez",
                        "1ºASIR",
                        "18",
                        "elena.moreno@iesquevedo.es", true,
                        mutableListOf(
                            Boletin("Redes", 3),
                            Boletin("Sistemas Informáticos", 7),
                            Boletin("FOL", 8),
                            Boletin("Bases de Datos", 5)
                        )
                    )
                ),
                alumnoSeleccionado = null,
                selected = false,
                error = null
            ),
            snackbarHostState = SnackbarHostState(),
            onAlumnoChange = {},
            onDeleteAlumnoSeleccionado = {},
            onSeleccionarAlumno = {},
            onAddAlumno = {}
        )
    }
}
