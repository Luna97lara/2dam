# Guía práctica: cómo se arma una app Compose (arquitectura de pantallas)

Esta guía complementa a las anteriores (layout, iconos/imágenes/tipografía) y se centra en **cómo se compone una app completa**: dónde va el tema, qué es `Surface`, qué es `Scaffold`, cómo se conecta cada pantalla con su `ViewModel`, cómo se gestionan eventos, cómo se monta la navegación entre pantallas, y dónde encajan las previews en todo esto.

---

## Índice

1. [La jerarquía completa, de fuera hacia dentro](#1-la-jerarquía-completa-de-fuera-hacia-dentro)
2. [`MaterialTheme`: dónde va y qué envuelve](#2-materialtheme-dónde-va-y-qué-envuelve)
3. [`Surface`: para qué sirve realmente](#3-surface-para-qué-sirve-realmente)
4. [`Scaffold`: el esqueleto de una pantalla](#4-scaffold-el-esqueleto-de-una-pantalla)
5. [TopAppBar: tipos y cuándo usar cada uno](#5-topappbar-tipos-y-cuándo-usar-cada-uno)
6. [Bottom bar: NavigationBar vs BottomAppBar](#6-bottom-bar-navigationbar-vs-bottomappbar)
7. [FAB, Snackbar y otros slots del Scaffold](#7-fab-snackbar-y-otros-slots-del-scaffold)
8. [El patrón Screen + Content: separar ViewModel de UI](#8-el-patrón-screen--content-separar-viewmodel-de-ui)
9. [¿Dónde se crea el ViewModel? Scope y ciclo de vida](#9-dónde-se-crea-el-viewmodel-scope-y-ciclo-de-vida)
10. [State hoisting: la regla completa](#10-state-hoisting-la-regla-completa)
11. [Eventos: de la UI al ViewModel, y la respuesta inversa](#11-eventos-de-la-ui-al-viewmodel-y-la-respuesta-inversa)
12. [Navegación entre pantallas](#12-navegación-entre-pantallas)
13. [Navigation 3: la alternativa moderna a Navigation Compose](#13-navigation-3-la-alternativa-moderna-a-navigation-compose)
15. [Dónde vive la TopBar/BottomBar cuando hay navegación](#15-dónde-vive-la-topbarbottombar-cuando-hay-navegación)
16. [Previews: de la pantalla completa a sus piezas](#16-previews-de-la-pantalla-completa-a-sus-piezas)
17. [Ejemplo completo: app con 3 pantallas, bottom bar y top bar](#17-ejemplo-completo-app-con-3-pantallas-bottom-bar-y-top-bar)
18. [Estructura de carpetas recomendada](#18-estructura-de-carpetas-recomendada)
19. [Errores comunes](#19-errores-comunes)
20. [Checklist final](#20-checklist-final)

---

## 1. La jerarquía completa, de fuera hacia dentro

Antes de entrar en detalle, este es el esqueleto que vas a ver una y otra vez en cualquier app Compose, de fuera hacia dentro:

```
MainActivity (setContent)
 └── MiAppTheme                    ← tema: colores, tipografía, formas
      └── NavHost                  ← controla qué pantalla se muestra
           └── Scaffold (por pantalla) ← topBar, bottomBar, FAB, snackbar, contenido
                └── PantallaXScreen (conectada al ViewModel)
                     └── PantallaXContent (solo UI, sin ViewModel)
                          └── Composables reutilizables (botones, tarjetas, campos...)
```

Cada capa tiene una responsabilidad y no se salta: el tema no sabe de navegación, la navegación no sabe de ViewModels concretos (solo de qué pantalla mostrar), y el contenido visual no sabe que existe un ViewModel. Vamos pieza por pieza.

---

## 2. `MaterialTheme`: dónde va y qué envuelve

`MaterialTheme` se coloca **una sola vez, en la raíz de la app**, nunca dentro de cada pantalla. Envuelve absolutamente todo lo que se vaya a dibujar.

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiAppTheme {               // ← el tema envuelve todo, se declara una vez
                MiApp()                 // ← aquí dentro ya está Surface, NavHost, etc.
            }
        }
    }
}
```

```kotlin
// theme/Theme.kt
@Composable
fun MiAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(LocalContext.current)
            else dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
```

- `MaterialTheme` no dibuja nada por sí mismo: solo **publica valores** (`MaterialTheme.colorScheme`, `.typography`, `.shapes`) a través de `CompositionLocal`, para que cualquier composable por debajo pueda leerlos sin que se los pases a mano uno por uno.
- Nunca se anida un segundo `MaterialTheme` dentro de una pantalla "para cambiar el tema solo ahí", salvo un caso muy puntual (una sección con marca distinta); si lo haces, hazlo de forma consciente y documentada.

---

## 3. `Surface`: para qué sirve realmente

`Surface` es el equivalente conceptual al fondo de una `Activity`/`Window` en XML: **establece el color de fondo y el color de contenido por defecto** para todo lo que esté dentro, y sirve de base para elevación, bordes y sombras.

```kotlin
@Composable
fun MiApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(/* ... */)
    }
}
```

### ¿Dónde va exactamente?

- **Una vez, justo dentro del tema**, envolviendo toda la app (como arriba) — esto sustituye al `android:background` que ponías en el `windowBackground` del tema XML.
- `Scaffold` (sección 4) **ya incluye un `Surface` internamente** para su `containerColor`, así que si usas `Scaffold` en cada pantalla no hace falta que pongas otro `Surface` alrededor del contenido de esa pantalla; sería redundante.
- Se puede usar también en componentes concretos (una `Card` es, por dentro, un `Surface` con elevación y forma), pero eso ya lo resuelven los componentes de Material 3 por ti.

> Regla práctica: **un `Surface` grande en la raíz de la app, y confía en que `Scaffold` gestione el resto por pantalla.**

---

## 4. `Scaffold`: el esqueleto de una pantalla

`Scaffold` es el composable que organiza las piezas estructurales de **una pantalla**: barra superior, barra inferior, FAB, snackbar y el contenido, dejando que Compose calcule los huecos (`padding`) para que nada se superponga.

```kotlin
@Composable
fun PantallaEjemploScaffold() {
    Scaffold(
        topBar = { /* TopAppBar */ },
        bottomBar = { /* NavigationBar */ },
        floatingActionButton = { /* FAB */ },
        snackbarHost = { /* SnackbarHost */ }
    ) { innerPadding ->
        // El contenido SIEMPRE debe respetar innerPadding,
        // si no, queda tapado por la topBar/bottomBar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // contenido de la pantalla
        }
    }
}
```

### El `innerPadding` no es opcional

Es el error más común al empezar con `Scaffold`: **si no aplicas el `innerPadding` al contenido, la barra superior tapa el principio de la lista y la inferior tapa el final.** `Scaffold` no recorta el contenido automáticamente; solo te dice cuánto espacio reservan sus barras, y tú decides cómo aplicarlo (normalmente con `.padding(innerPadding)` en el contenedor raíz del contenido).

### ¿Uno por pantalla o uno para toda la app?

Depende de si tienes **barras que cambian por pantalla** o **barras que se mantienen fijas** mientras navegas:

| Patrón | Cuándo usarlo |
|---|---|
| **Un `Scaffold` por pantalla** (cada `XScreen` tiene el suyo) | La TopBar cambia de título/acciones según la pantalla, o algunas pantallas no llevan bottom bar (ej. una pantalla de detalle a pantalla completa) |
| **Un `Scaffold` único envolviendo el `NavHost`** | La bottom bar es siempre la misma en varias pantallas principales (home, buscar, perfil) y no quieres que parpadee al navegar entre ellas |

Lo habitual en apps con pestañas inferiores es la **segunda opción** para la bottom bar, y dejar que cada pantalla gestione su propia TopBar por dentro (ver sección 13, donde se detalla esta combinación).

---

## 5. TopAppBar: tipos y cuándo usar cada uno

Material 3 ofrece varias variantes, todas dentro del parámetro `topBar` del `Scaffold`:

| Composable | Uso |
|---|---|
| `TopAppBar` | Barra estándar, altura fija — la mayoría de pantallas |
| `CenterAlignedTopAppBar` | Título centrado — pantallas de nivel superior (home) |
| `MediumTopAppBar` | Título grande que se encoge al hacer scroll |
| `LargeTopAppBar` | Como el anterior pero más alto — pantallas destacadas, tipo cabecera de artículo |

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiTopBar(
    titulo: String,
    onNavigateBack: (() -> Unit)? = null,
    acciones: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(titulo) },
        navigationIcon = {
            onNavigateBack?.let {
                IconButton(onClick = it) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        },
        actions = acciones,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
```

- `navigationIcon`: normalmente la flecha de volver, o el icono de menú (hamburguesa) en la pantalla raíz con un `Drawer`.
- `actions`: iconos de acción a la derecha (buscar, más opciones...).
- **Scroll que encoge la barra** (`MediumTopAppBar`/`LargeTopAppBar`): necesitan conectarse con `TopAppBarScrollBehavior` y el `Modifier.nestedScroll()` de la lista, para que se encojan al hacer scroll hacia abajo.

```kotlin
val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

Scaffold(
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
        LargeTopAppBar(
            title = { Text("Detalle") },
            scrollBehavior = scrollBehavior
        )
    }
) { innerPadding ->
    LazyColumn(modifier = Modifier.padding(innerPadding)) { /* ... */ }
}
```

---

## 6. Bottom bar: NavigationBar vs BottomAppBar

| Composable | Uso |
|---|---|
| `NavigationBar` | Pestañas de navegación entre secciones principales (Home / Buscar / Perfil) — equivalente a `BottomNavigationView` en XML |
| `BottomAppBar` | Barra de acciones en la parte inferior (no navegación entre pantallas, sino botones de acción), a veces con un FAB incrustado |

```kotlin
@Composable
fun MiBottomBar(
    destinoActual: Destino,
    onDestinoClick: (Destino) -> Unit
) {
    NavigationBar {
        Destino.entries.forEach { destino ->
            NavigationBarItem(
                selected = destino == destinoActual,
                onClick = { onDestinoClick(destino) },
                icon = {
                    Icon(destino.icono, contentDescription = null)
                },
                label = { Text(stringResource(destino.tituloRes)) }
            )
        }
    }
}
```

- El **estado de selección** (qué pestaña está activa) normalmente viene del `NavController` (ver sección 12), comparando la ruta actual con cada destino — no se guarda como un `mutableStateOf` aparte, porque la fuente de verdad de "dónde estoy" ya es la pila de navegación.
- `label` es opcional en Material 3, pero es buena práctica para accesibilidad y claridad, salvo en apps con 4-5 pestañas muy reconocibles por icono.

---

## 7. FAB, Snackbar y otros slots del Scaffold

### Floating Action Button

```kotlin
Scaffold(
    floatingActionButton = {
        FloatingActionButton(onClick = { /* acción principal de la pantalla */ }) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_item))
        }
    },
    floatingActionButtonPosition = FabPosition.End
) { /* ... */ }
```

### Snackbar (mensajes temporales)

El `Scaffold` necesita un `SnackbarHostState`, que normalmente vive en el `ViewModel` o se crea con `remember` en la pantalla si es puramente de UI (ej. "no se pudo copiar al portapapeles").

```kotlin
val snackbarHostState = remember { SnackbarHostState() }
val scope = rememberCoroutineScope()

Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) }
) { innerPadding ->
    Button(onClick = {
        scope.launch {
            snackbarHostState.showSnackbar("Elemento guardado")
        }
    }) {
        Text("Guardar")
    }
}
```

Cuando el mensaje depende de un resultado del `ViewModel` (ej. error de red), es más correcto exponer un **evento de una sola vez** desde el ViewModel (ver sección 11) y que la pantalla reaccione mostrando el snackbar, en vez de que el `ViewModel` conozca el `SnackbarHostState` directamente (el ViewModel no debe depender de tipos de UI de Compose).

---

## 8. El patrón Screen + Content: separar ViewModel de UI

Esta es la convención más importante de toda la guía. Cada pantalla se divide en **dos composables**:

1. **`XScreen`** (o `XRoute`): el que recibe el `ViewModel`, lee el estado, y pasa datos + lambdas hacia abajo. **No dibuja nada directamente**, solo conecta.
2. **`XContent`**: el que recibe **solo datos planos y lambdas**, dibuja la UI, y no sabe que existe un `ViewModel`. Es el que se previsualiza (sección 14) y el que se podría reutilizar en otro contexto (otra pantalla, un test, un componente embebido).

```kotlin
// 1. Screen: el "pegamento" con el ViewModel
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel = viewModel(),
    onNavigateToAjustes: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PerfilContent(
        uiState = uiState,
        onEditarClick = viewModel::onEditarClick,
        onGuardarClick = viewModel::onGuardarClick,
        onAjustesClick = onNavigateToAjustes
    )
}

// 2. Content: solo UI, fácil de previsualizar y testear
@Composable
fun PerfilContent(
    uiState: PerfilUiState,
    onEditarClick: () -> Unit,
    onGuardarClick: () -> Unit,
    onAjustesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile)) },
                actions = {
                    IconButton(onClick = onAjustesClick) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            Text(uiState.nombre, style = MaterialTheme.typography.headlineMedium)
            Button(onClick = onEditarClick) { Text(stringResource(R.string.edit)) }
        }
    }
}
```

### ¿Por qué separar en dos?

- **`XContent` se puede previsualizar con `@Preview`** con datos falsos, sin tener que instanciar un `ViewModel` real (que necesita contexto de Android, repositorios, etc.).
- **`XContent` es reutilizable**: si mañana necesitas la misma UI en un `BottomSheet`, en un widget o en un test de UI, la usas tal cual.
- **Separación de responsabilidades clara**: cualquiera que lea `XScreen` ve de un vistazo qué eventos expone el ViewModel; cualquiera que lea `XContent` ve de un vistazo cómo se ve la pantalla, sin ruido de ciclo de vida.

---

## 9. ¿Dónde se crea el ViewModel? Scope y ciclo de vida

El `ViewModel` **se solicita desde el composable `XScreen`**, nunca se instancia con `PerfilViewModel()` directamente (perderías la supervivencia a cambios de configuración y la inyección de dependencias).

```kotlin
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel = viewModel() // ← Factory por defecto de Compose
) { /* ... */ }
```

### Con inyección de dependencias (Hilt, el caso más común en apps reales)

```kotlin
@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val repositorio: UsuarioRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    fun onEditarClick() { /* ... */ }
    fun onGuardarClick() { /* ... */ }
}
```

```kotlin
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel = hiltViewModel() // ← en vez de viewModel()
) { /* ... */ }
```

### ¿En qué "nivel" vive el ViewModel?

| Scope | Cuándo usarlo |
|---|---|
| `viewModel()`/`hiltViewModel()` **sin argumentos**, pedido dentro de la propia pantalla de navegación (`composable("perfil") { PerfilScreen() }`) | El caso normal: el ViewModel vive y muere con esa entrada del `NavHost` — se destruye al salir de la pantalla y se vuelve a crear si regresas |
| `hiltViewModel(parentEntry)` apuntando a una entrada de navegación **padre** (un grafo anidado) | Varias pantallas de un mismo flujo (ej. los 3 pasos de un formulario de registro) necesitan **compartir el mismo ViewModel** mientras dura el flujo completo |
| ViewModel a nivel de `Activity` (`viewModel(activityOwner)`) | Muy poco común hoy; solo si de verdad necesitas estado compartido entre pantallas que no están en el mismo grafo de navegación |

> **Nunca** se crea el ViewModel en `XContent` ni se pasa el `ViewModel` entero como parámetro a `XContent`: `XContent` solo debe conocer datos y lambdas (sección 8), nunca el tipo `ViewModel`.

---

## 10. State hoisting: la regla completa

Ya se introdujo en la guía general de Compose; aquí se aplica a nivel de **pantalla completa**, no solo de un componente suelto.

### La cadena de hoisting en una pantalla típica

```
ViewModel (StateFlow<UiState>)
   │  expone el estado hacia arriba del árbol de composición
   ▼
XScreen (lee el StateFlow con collectAsStateWithLifecycle)
   │  pasa el estado hacia abajo como parámetros inmutables
   ▼
XContent (recibe UiState + lambdas, sin estado propio de negocio)
   │  puede tener remember { } SOLO para estado de UI efímero
   ▼
Componentes hijos (TextField, Botón...) con su propio estado visual momentáneo
```

### Qué es "estado de negocio" (sube al ViewModel) vs "estado de UI efímero" (se queda en `remember`)

| Tipo de estado | Dónde vive | Ejemplo |
|---|---|---|
| Datos que vienen de una fuente (red, base de datos) | `ViewModel` (`StateFlow`) | Lista de productos, perfil del usuario |
| Datos que el usuario introduce y deben sobrevivir a rotación/navegación | `ViewModel` | Texto de un formulario a medio rellenar |
| Estado puramente visual, sin significado fuera de esa composición | `remember { mutableStateOf(...) }` en el propio composable | Si un desplegable está abierto, si se muestra el icono de "ojo" en un campo de contraseña |
| Estado visual que debe sobrevivir a rotación pero no a navegar fuera | `rememberSaveable { mutableStateOf(...) }` | Posición de scroll, pestaña seleccionada en un `TabRow` interno |

```kotlin
@Composable
fun CampoPassword(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Esto es estado de UI puro: no le importa al ViewModel si el ojo está abierto
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = value,                 // ← estado de negocio: viene de fuera (hoisted)
        onValueChange = onValueChange, // ← evento hacia fuera
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
        modifier = modifier
    )
}
```

---

## 11. Eventos: de la UI al ViewModel, y la respuesta inversa

### Dirección 1: UI → ViewModel (acciones del usuario)

El patrón más mantenible es tener **una función (o una sola clase sellada de eventos) por acción posible**, nunca exponer `MutableStateFlow` directamente a la UI para que lo modifique desde fuera.

**Opción simple (funciones sueltas, suficiente en la mayoría de pantallas):**

```kotlin
class PerfilViewModel : ViewModel() {
    fun onNombreChanged(nuevo: String) { /* ... */ }
    fun onGuardarClick() { /* ... */ }
}
```

```kotlin
PerfilContent(
    onNombreChanged = viewModel::onNombreChanged,
    onGuardarClick = viewModel::onGuardarClick
)
```

**Opción con clase sellada de eventos (mejor en pantallas con muchas acciones, más testeable):**

```kotlin
sealed interface PerfilEvent {
    data class NombreCambiado(val valor: String) : PerfilEvent
    object GuardarClick : PerfilEvent
    object EditarClick : PerfilEvent
}

class PerfilViewModel : ViewModel() {
    fun onEvent(event: PerfilEvent) {
        when (event) {
            is PerfilEvent.NombreCambiado -> { /* ... */ }
            PerfilEvent.GuardarClick -> { /* ... */ }
            PerfilEvent.EditarClick -> { /* ... */ }
        }
    }
}
```

```kotlin
PerfilContent(onEvent = viewModel::onEvent)
```

### Dirección 2: ViewModel → UI (eventos de una sola vez)

El `StateFlow` de `uiState` es perfecto para **estado continuo** (lo que se ve en pantalla ahora mismo), pero **no sirve bien para eventos puntuales** como "navega a otra pantalla" o "muestra este snackbar", porque si la pantalla se recompone o vuelve del fondo, el mismo valor de estado se volvería a procesar.

Para eso se usa un canal de eventos de una sola vez, normalmente con `Channel`/`SharedFlow`:

```kotlin
class PerfilViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    private val _eventos = Channel<PerfilEventoUnico>()
    val eventos = _eventos.receiveAsFlow()

    fun onGuardarClick() {
        viewModelScope.launch {
            val exito = guardarPerfil()
            if (exito) {
                _eventos.send(PerfilEventoUnico.MostrarMensaje("Guardado"))
                _eventos.send(PerfilEventoUnico.NavegarAtras)
            } else {
                _eventos.send(PerfilEventoUnico.MostrarMensaje("Error al guardar"))
            }
        }
    }
}

sealed interface PerfilEventoUnico {
    data class MostrarMensaje(val texto: String) : PerfilEventoUnico
    object NavegarAtras : PerfilEventoUnico
}
```

```kotlin
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventos.collect { evento ->
            when (evento) {
                is PerfilEventoUnico.MostrarMensaje -> snackbarHostState.showSnackbar(evento.texto)
                PerfilEventoUnico.NavegarAtras -> onNavigateBack()
            }
        }
    }

    PerfilContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onGuardarClick = viewModel::onGuardarClick
    )
}
```

> `LaunchedEffect(Unit)` suscribe el `collect` una sola vez mientras el composable está en composición; es el sitio correcto para "escuchar" eventos de una sola vez, nunca dentro del cuerpo normal del composable (eso se ejecutaría en cada recomposición).

---

## 12. Navegación entre pantallas

Se usa la librería `androidx.navigation:navigation-compose`. El patrón recomendado (Navigation 2.8+) es definir las rutas como **objetos/clases serializables** en vez de strings sueltos, para tener seguridad de tipos.

```kotlin
// navigation/Destinos.kt
@Serializable object Home
@Serializable object Buscar
@Serializable object Perfil
@Serializable data class DetalleProducto(val productoId: String)
```

```kotlin
// navigation/AppNavHost.kt
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier
    ) {
        composable<Home> {
            HomeScreen(
                onProductoClick = { id ->
                    navController.navigate(DetalleProducto(productoId = id))
                }
            )
        }
        composable<Buscar> {
            BuscarScreen()
        }
        composable<Perfil> {
            PerfilScreen(
                onNavigateToAjustes = { navController.navigate(Ajustes) }
            )
        }
        composable<DetalleProducto> { backStackEntry ->
            val ruta: DetalleProducto = backStackEntry.toRoute()
            DetalleProductoScreen(
                productoId = ruta.productoId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
```

- `navController.navigate(Destino)`: avanza a una pantalla, añadiéndola a la pila.
- `navController.popBackStack()`: vuelve atrás (equivalente al botón "atrás").
- Los **argumentos** (como `productoId`) viajan como propiedades del propio objeto de ruta, con seguridad de tipos — nada de construir strings tipo `"detalle/{id}"` a mano.
- El `NavController` se crea una vez, arriba del todo: `val navController = rememberNavController()`.

---

## 13. Navigation 3: la alternativa moderna a Navigation Compose

Todo lo anterior (sección 12) es **Navigation Compose**, construida sobre la librería clásica `androidx.navigation` (la "Navigation 2", con `NavHost`/`NavController`/`composable<T>`). Sigue siendo totalmente válida y es la que más documentación y ejemplos tiene hoy. Pero desde hace unos meses existe **Navigation 3** (`androidx.navigation3`), una librería **nueva y separada** — no es una versión que sustituya a la anterior dentro del mismo paquete, sino otra forma de hacer navegación, pensada desde cero para Compose.

A fecha de octubre de 2026, **Navigation 3 ya es estable** (versión 1.2.0).

### La diferencia de fondo

| | Navigation Compose (2.x) | Navigation 3 |
|---|---|---|
| Quién posee la pila de navegación | El `NavController`, internamente (una caja negra) | **Tu propio código**: la pila es una `List` normal que tú guardas y mutas |
| Cómo se navega | `navController.navigate(Destino)` | `backStack.add(Destino)` / `backStack.removeLastOrNull()` — operaciones de lista, sin API especial |
| Cómo se declaran las pantallas | Un grafo (`NavHost { composable<T> { ... } }`) | Un mapa de "qué componer para cada tipo de clave" (`entryProvider { entry<T> { ... } }`) sobre un `NavDisplay` |
| Layouts adaptables (maestro-detalle en tablet) | Hay que montarlo a mano (como en la sección 11 de la guía de layouts) | Soporte de primera clase con "scene strategies": puedes mostrar varias entradas de la pila a la vez de forma declarativa |
| Madurez | Años de uso, mucha documentación | Más reciente, pero ya estable (1.2.0) |

> La ventaja real de Navigation 3 no es solo "es más nuevo": es que **tú tienes control directo de la pila como estructura de datos**, lo cual simplifica mucho casos que con Navigation 2 requerían rodeos (compartir estado entre pantallas de un flujo, lógica de navegación condicional compleja, layouts adaptables con varias pantallas visibles a la vez).

### Dependencias

```kotlin
// build.gradle.kts (módulo app)
dependencies {
    implementation("androidx.navigation3:navigation3-runtime:1.2.0")
    implementation("androidx.navigation3:navigation3-ui:1.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-navigation3:<versión del tren de lifecycle>")
}
```

> Comprueba la versión exacta de `lifecycle-viewmodel-navigation3` en las notas de la librería `lifecycle` de tu proyecto, ya que se publica junto al resto de artefactos de `androidx.lifecycle` y su numeración no sigue la de `navigation3`.

### Conceptos clave

- **`NavKey`**: interfaz marcadora para las claves de destino (el equivalente al objeto de ruta de la sección 12). Normalmente `@Serializable`, igual que antes.
- **`rememberNavBackStack(...)`**: crea la pila de navegación como una lista recordada (sobrevive a recomposición y, si las claves son serializables, a rotación).
- **`NavDisplay`**: el composable que **pinta** la pila actual — el equivalente a `NavHost`, pero mirando una lista en vez de un grafo.
- **`entryProvider { entry<Clave> { ... } }`**: asocia cada tipo de clave con el composable que debe mostrarse — el equivalente a `composable<Clave> { ... }`.
- **`NavEntry`**: una entrada individual de la pila (clave + contenido), con su propio scope (ver ViewModels más abajo).

### Ejemplo equivalente al de la sección 12, pero con Navigation 3

```kotlin
// navigation/Destinos.kt — las claves, igual que antes
@Serializable data object Home : NavKey
@Serializable data object Buscar : NavKey
@Serializable data object Perfil : NavKey
@Serializable data class DetalleProducto(val productoId: String) : NavKey
```

```kotlin
// MiApp.kt
@Composable
fun MiApp() {
    val backStack = rememberNavBackStack(Home)

    Scaffold(
        bottomBar = {
            if (backStack.last() in listOf(Home, Buscar, Perfil)) {
                MiBottomBar(
                    destinoActual = backStack.last(),
                    onDestinoClick = { destino ->
                        // Navigation 3 no tiene "popUpTo"/"launchSingleTop" como tal:
                        // tú decides la política de pila directamente
                        backStack.clear()
                        backStack.add(destino)
                    }
                )
            }
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            modifier = Modifier.padding(innerPadding),
            entryProvider = entryProvider {
                entry<Home> {
                    HomeScreen(
                        onProductoClick = { id -> backStack.add(DetalleProducto(id)) }
                    )
                }
                entry<Buscar> { BuscarScreen() }
                entry<Perfil> {
                    PerfilScreen(
                        onNavigateToAjustes = { backStack.add(Ajustes) }
                    )
                }
                entry<DetalleProducto> { clave ->
                    DetalleProductoScreen(
                        productoId = clave.productoId,
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }
            }
        )
    }
}
```

Puntos a notar frente al ejemplo de Navigation 2:

- **No hay `navController.navigate(...)`**: navegar es literalmente **añadir un elemento a una lista** (`backStack.add(...)`), y volver atrás es **quitar el último** (`backStack.removeLastOrNull()`).
- **No hay "popUpTo"/"launchSingleTop"/"restoreState"** como conceptos de API: si quieres esa política (no acumular pantallas duplicadas de la bottom bar), la implementas tú mismo con operaciones de lista (`clear()` + `add()` en el ejemplo), lo cual es más verboso para este caso concreto, pero mucho más flexible para casos no estándar.
- `onBack` en `NavDisplay` es lo que conecta con el botón "atrás" del sistema; tú decides qué significa "ir atrás" para tu pila (normalmente, quitar el último elemento).

### ViewModels con Navigation 3

Cada `NavEntry` tiene su propio scope de `ViewModelStoreOwner`, igual que cada entrada del `NavHost` en Navigation 2 — con la libraría `lifecycle-viewmodel-navigation3`, un ViewModel pedido dentro de un `entry<Clave> { ... }` vive mientras esa clave siga en la pila, y se limpia cuando se saca (igual que en la sección 9 de esta guía, solo que el "contenedor" ya no es un grafo de navegación sino la propia lista del back stack).

```kotlin
entry<Perfil> {
    val viewModel: PerfilViewModel = viewModel() // mismo scope-por-entrada que en Navigation 2
    PerfilScreenContent(viewModel)
}
```

### Layouts adaptables: la razón de ser de Navigation 3

Donde Navigation 3 realmente se nota frente a Navigation 2 es en pantallas tipo maestro-detalle (como el ejemplo de `WindowSizeClass` de la guía de layouts adaptables): con **scene strategies**, puedes declarar que, en pantallas anchas, `NavDisplay` muestre **dos entradas de la pila a la vez** (lista + detalle) en vez de solo la última, sin tener que montar tú mismo la lógica de "¿estoy en modo compacto o expandido?" en cada pantalla como se hacía en Navigation 2.

```kotlin
NavDisplay(
    backStack = backStack,
    sceneStrategy = ListDetailSceneStrategy(), // ejemplo conceptual: una "scene strategy" adaptable
    entryProvider = entryProvider { /* ... */ }
)
```

> El detalle exacto de las *scene strategies* (nombres de clase, cómo se combinan con `WindowSizeClass`) está evolucionando con las versiones de la librería; consulta la [documentación oficial de Navigation 3](https://developer.android.com/guide/navigation/navigation-3) para la API vigente en el momento de implementarlo, en vez de fiarte de un ejemplo fijo aquí.

### ¿Cuál elegir en un proyecto nuevo?

| Situación | Recomendación |
|---|---|
| Proyecto nuevo, sin prisa, y quieres control fino de la pila o layouts adaptables maestro-detalle complejos | **Navigation 3** |
| Proyecto existente ya con Navigation Compose funcionando bien | Quedarte en **Navigation 2** — migrar no aporta nada si no tienes el problema que Navigation 3 resuelve |
| Equipo que necesita máxima cantidad de documentación/ejemplos/Stack Overflow disponibles ahora mismo | **Navigation 2** (todavía tiene más tutoriales y respuestas en la comunidad) |
| Apps con navegación muy estándar (unas pocas pantallas, bottom bar, sin maestro-detalle) | Cualquiera de las dos funciona bien; Navigation 2 es ligeramente menos código para este caso simple |

---

## 15. Dónde vive la TopBar/BottomBar cuando hay navegación

Este es el punto donde más dudas surgen: **¿el `Scaffold` va dentro del `NavHost` (por pantalla) o fuera (envolviéndolo)?**

### Patrón recomendado para bottom bar fija + top bar variable

Un único `Scaffold` "de app" envuelve el `NavHost` y pone la **bottom bar** (que no cambia entre Home/Buscar/Perfil); cada pantalla gestiona su **propia TopBar** dentro de su propio contenido (sin otro `Scaffold` anidado, usando solo `Column` + la TopBar como primer elemento, o un `Scaffold` interno sin bottomBar si prefieres mantener el patrón consistente).

```kotlin
@Composable
fun MiApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            // Solo se muestra en las pantallas principales, no en Detalle
            if (esDestinoPrincipal(rutaActual)) {
                MiBottomBar(
                    destinoActual = destinoDesdeRuta(rutaActual),
                    onDestinoClick = { destino ->
                        navController.navigate(destino) {
                            // Evita acumular pantallas duplicadas al tocar varias veces la misma pestaña
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
```

Y cada pantalla pone su propia TopBar con su propio `Scaffold` interno (sin bottomBar, porque ya la pone el de arriba):

```kotlin
@Composable
fun HomeContent(/* ... */) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(stringResource(R.string.home)) })
        }
    ) { innerPaddingInterno ->
        // contenido de Home
    }
}
```

> Sí, esto significa **dos `Scaffold` anidados** (uno de app para la bottom bar, uno por pantalla para la top bar), y es el patrón oficial recomendado por Google para este caso — cada `Scaffold` resuelve el padding de lo que él mismo gestiona, así que no chocan entre sí mientras cada uno solo declare las barras que le tocan.

### Alternativa más simple (apps pequeñas, pocas pantallas)

Si la TopBar también es más o menos constante (incluso si cambia el título), puedes poner **ambas barras en el `Scaffold` de app** y pasar el título como un estado observado desde el backstack o desde cada pantalla vía un callback; es menos flexible pero evita el anidamiento si tu app es sencilla.

---

## 16. Previews: de la pantalla completa a sus piezas

Gracias a separar `Screen`/`Content` (sección 8), las previews se hacen siempre sobre `XContent`, nunca sobre `XScreen` (que necesitaría un ViewModel real).

### Preview de la pantalla completa con datos de ejemplo

```kotlin
@Preview(showBackground = true)
@Composable
private fun PerfilContentPreview() {
    MiAppTheme {
        PerfilContent(
            uiState = PerfilUiState(
                nombre = "Óscar Novillo",
                email = "oscar@ejemplo.com"
            ),
            onEditarClick = {},
            onGuardarClick = {},
            onAjustesClick = {}
        )
    }
}
```

### Previews de estados distintos (carga, vacío, error)

```kotlin
@Preview(name = "Cargando")
@Composable
private fun PerfilContentCargandoPreview() {
    MiAppTheme {
        PerfilContent(uiState = PerfilUiState(isLoading = true), /* ... */)
    }
}

@Preview(name = "Error")
@Composable
private fun PerfilContentErrorPreview() {
    MiAppTheme {
        PerfilContent(uiState = PerfilUiState(error = "No se pudo cargar"), /* ... */)
    }
}
```

### Preview de piezas sueltas (componentes reutilizables)

```kotlin
@Preview
@Composable
private fun MiBottomBarPreview() {
    MiAppTheme {
        MiBottomBar(destinoActual = Destino.Home, onDestinoClick = {})
    }
}
```

> Regla práctica: **si un composable necesita un `ViewModel`, una `NavController` real o un `Context` de Android para funcionar, no se previsualiza directamente** — se previsualiza su versión `Content` o su pieza suelta, pasando datos de ejemplo a mano.

---

## 17. Ejemplo completo: app con 3 pantallas, bottom bar y top bar

Pequeña app de ejemplo que reúne todo lo anterior: `Home`, `Buscar`, `Perfil` con bottom bar fija, cada una con su propia top bar, y un `ViewModel` en `Home` como ejemplo de la conexión completa.

```kotlin
// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiAppTheme {
                MiApp()
            }
        }
    }
}
```

```kotlin
// MiApp.kt
enum class Destino(val ruta: Any, val icono: ImageVector, val tituloRes: Int) {
    Home(navigation.Home, Icons.Default.Home, R.string.home),
    Buscar(navigation.Buscar, Icons.Default.Search, R.string.search),
    Perfil(navigation.Perfil, Icons.Default.Person, R.string.profile)
}

@Composable
fun MiApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destinoActual = Destino.entries.find {
        backStackEntry?.destination?.hasRoute(it.ruta::class) == true
    }

    Scaffold(
        bottomBar = {
            if (destinoActual != null) {
                NavigationBar {
                    Destino.entries.forEach { destino ->
                        NavigationBarItem(
                            selected = destino == destinoActual,
                            onClick = {
                                navController.navigate(destino.ruta) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destino.icono, contentDescription = null) },
                            label = { Text(stringResource(destino.tituloRes)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Home> {
                HomeScreen(
                    onProductoClick = { id -> navController.navigate(DetalleProducto(id)) }
                )
            }
            composable<Buscar> { BuscarScreen() }
            composable<Perfil> { PerfilScreen() }
            composable<DetalleProducto> { entry ->
                val ruta: DetalleProducto = entry.toRoute()
                DetalleProductoScreen(
                    productoId = ruta.productoId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
```

```kotlin
// home/HomeViewModel.kt
data class HomeUiState(
    val productos: List<Producto> = emptyList(),
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val repositorio: ProductoRepository = ProductoRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val productos = repositorio.obtenerProductos()
            _uiState.update { it.copy(productos = productos, isLoading = false) }
        }
    }
}
```

```kotlin
// home/HomeScreen.kt — la pieza "Screen": conecta con el ViewModel
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onProductoClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        uiState = uiState,
        onProductoClick = onProductoClick
    )
}

// home/HomeContent.kt — la pieza "Content": solo UI, previsualizable
@Composable
fun HomeContent(
    uiState: HomeUiState,
    onProductoClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(stringResource(R.string.home)) })
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.productos.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.no_products),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        items(uiState.productos, key = { it.id }) { producto ->
                            ListItem(
                                headlineContent = { Text(producto.nombre) },
                                supportingContent = { Text(producto.precio) },
                                modifier = Modifier.clickable { onProductoClick(producto.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    MiAppTheme {
        HomeContent(
            uiState = HomeUiState(
                productos = listOf(
                    Producto(id = "1", nombre = "Camiseta", precio = "19,99 €"),
                    Producto(id = "2", nombre = "Zapatillas", precio = "59,99 €")
                ),
                isLoading = false
            ),
            onProductoClick = {}
        )
    }
}
```

Resumen de lo que ilustra este ejemplo:

- **El tema** (`MiAppTheme`) envuelve toda la app, una sola vez, en `MainActivity`.
- **Un `Scaffold` de app** gestiona la `NavigationBar` fija, calculada a partir del `NavController`, no de un estado aparte.
- **Cada pantalla tiene su propio `Scaffold` interno** solo para la `TopAppBar` (`HomeContent`), y el padding de ambos `Scaffold` no choca porque cada uno gestiona lo suyo.
- **`HomeScreen`/`HomeContent`** siguen el patrón Screen+Content: el ViewModel solo lo conoce `HomeScreen`.
- **La navegación** usa rutas tipadas (`Home`, `DetalleProducto(id)`), no strings.
- **El preview** se hace sobre `HomeContent`, con datos de ejemplo, sin tocar el ViewModel real.

---

## 18. Estructura de carpetas recomendada

Para que todo lo anterior no acabe siendo un único archivo gigante, una organización típica por *feature* (más mantenible que agrupar por tipo de archivo):

```
app/
 └── src/main/java/com/miapp/
      ├── MainActivity.kt
      ├── MiApp.kt
      ├── navigation/
      │    ├── Destinos.kt
      │    └── AppNavHost.kt
      ├── theme/
      │    ├── Theme.kt
      │    ├── Color.kt
      │    ├── Typography.kt
      │    └── Shape.kt
      ├── home/
      │    ├── HomeScreen.kt
      │    ├── HomeContent.kt
      │    ├── HomeViewModel.kt
      │    └── HomeUiState.kt
      ├── buscar/
      │    ├── BuscarScreen.kt
      │    ├── BuscarContent.kt
      │    └── BuscarViewModel.kt
      ├── perfil/
      │    └── ...
      └── common/
           ├── components/   ← composables reutilizados entre features (botones, tarjetas)
           └── data/          ← repositorios, modelos compartidos
```

> Agrupar por *feature* (en vez de tener una carpeta global `viewmodels/`, otra `screens/`...) hace mucho más fácil encontrar todo lo relacionado con una pantalla, y es el patrón que escala mejor al crecer la app.

---

## 19. Errores comunes

| Error | Consecuencia | Solución |
|---|---|---|
| No aplicar `innerPadding` del `Scaffold` al contenido | La TopBar/BottomBar tapan parte del contenido | Siempre `.padding(innerPadding)` en el contenedor raíz del contenido |
| Pasar el `ViewModel` entero a `XContent` | `XContent` deja de ser previsualizable/testeable de forma aislada | `XContent` solo recibe datos y lambdas |
| Crear el `ViewModel` con `PerfilViewModel()` directo | Pierde supervivencia a rotación y no recibe dependencias inyectadas | `viewModel()` / `hiltViewModel()` |
| Guardar "a dónde navegar" como evento en el `StateFlow` de `uiState` | Si la pantalla se recompone, puede repetirse la navegación | Canal de eventos de una sola vez (`Channel`/`SharedFlow`) + `LaunchedEffect` |
| Rutas de navegación como strings sueltos (`"detalle/{id}"`) | Errores en tiempo de ejecución por typos, difícil de refactorizar | Rutas tipadas con `@Serializable` |
| Guardar el destino de la bottom bar en un `mutableStateOf` aparte del `NavController` | Se desincroniza con la pila de navegación real | Derivar el destino activo de `currentBackStackEntryAsState()` |
| Previsualizar `XScreen` directamente | Falla o no compila el preview, porque necesita un `ViewModel`/contexto reales | Previsualizar siempre `XContent` |
| Anidar `MaterialTheme` dentro de cada pantalla | Inconsistencias de tema, redundante | Un único `MaterialTheme` en la raíz |
| Meter lógica de negocio dentro de `LaunchedEffect` en la UI | Difícil de testear, mezcla responsabilidades | La lógica va en el `ViewModel`; `LaunchedEffect` solo reacciona a eventos/efectos |

---

## 20. Checklist final

- [ ] ¿`MaterialTheme` envuelve toda la app una sola vez, en la raíz?
- [ ] ¿Hay como máximo un `Surface` grande de fondo, y el resto lo resuelve `Scaffold`/componentes Material?
- [ ] ¿Cada `Scaffold` aplica su `innerPadding` al contenido?
- [ ] ¿Cada pantalla sigue el patrón `Screen` (con ViewModel) + `Content` (solo UI)?
- [ ] ¿El `ViewModel` se obtiene con `viewModel()`/`hiltViewModel()`, nunca instanciado a mano?
- [ ] ¿El estado de negocio vive en el `ViewModel` (`StateFlow`), y el estado de UI efímero en `remember`/`rememberSaveable`?
- [ ] ¿Los eventos de una sola vez (navegación, snackbars puntuales) usan un canal aparte, no el `StateFlow` de estado continuo?
- [ ] ¿La navegación usa rutas tipadas, no strings sueltos?
- [ ] ¿El destino activo de la bottom bar se deriva del `NavController`, no de un estado paralelo?
- [ ] ¿Las previews se hacen sobre `XContent` con datos de ejemplo, cubriendo al menos el estado normal, de carga y de error?
- [ ] ¿La estructura de carpetas agrupa por *feature*, no por tipo de archivo?

---

## Resumen rápido

1. **Tema una vez, arriba del todo.** `Surface` grande de fondo; el resto de superficies las resuelven `Scaffold` y los componentes Material.
2. **`Scaffold` por pantalla** para la TopBar; opcionalmente **un `Scaffold` de app** envolviendo el `NavHost` para una bottom bar fija. Aplica siempre el `innerPadding`.
3. **Patrón Screen + Content**: `XScreen` conecta con el `ViewModel`; `XContent` solo pinta UI y es lo que se previsualiza.
4. **El `ViewModel` se pide con `viewModel()`/`hiltViewModel()`** desde la entrada de navegación correspondiente, nunca instanciado a mano.
5. **State hoisting**: estado de negocio arriba (ViewModel), estado de UI efímero abajo (`remember`).
6. **Eventos**: funciones/clase sellada UI→ViewModel; canal de un solo uso (`Channel`/`SharedFlow`) ViewModel→UI para navegación y mensajes puntuales.
7. **Navegación con rutas tipadas** (`@Serializable`): Navigation Compose (`NavHost`/`NavController`) sigue siendo válida y es la más documentada; **Navigation 3** (`NavDisplay`/`rememberNavBackStack`) ya es estable (1.2.0) y da control directo de la pila como lista, con mejor soporte para layouts adaptables maestro-detalle.
8. **Previews siempre sobre `Content`**, con varios estados (normal, carga, error, vacío).
