# Guía de Material 3 en Compose: estado, ViewModel y componentes

Oct 27, 2026 · @oscar

En Compose Material 3 el estado vive en el ViewModel y el composable solo lo pinta; para texto, la forma actual es TextFieldState (estable en Material 3 1.4.0), y los indicadores Expressive siguen en alpha/beta (1.5.0-beta01) bajo @ExperimentalMaterial3ExpressiveApi.

## Qué es estable y qué no (octubre 2026)

Lo clásico (TextField con `TextFieldState`, Checkbox, RadioButton, Button, progress lineal/circular) es estable en Material 3 1.4.0; todo lo Expressive exige `@OptIn(ExperimentalMaterial3ExpressiveApi::class)` y, hoy, la versión 1.5.0 en beta.

| Componente / API | Dónde vive | Estado |
| --- | --- | --- |
| `BasicTextField(state: TextFieldState)`, `InputTransformation`, `OutputTransformation`, `TextFieldLineLimits` | Compose Foundation | Estable (graduado desde BasicTextField2; Foundation 1.8, abril 2025, dato de memoria) |
| `TextField(state)` y `OutlinedTextField(state)` | Material 3 | Overloads añadidos en 1.4.0-alpha17; sin anotación experimental en 1.4.0 (24-sep-2025) |
| `SecureTextField`, `OutlinedSecureTextField` | Material 3 | Nuevos en 1.4.0 |
| `TextField(value, onValueChange)` | Material 3 | Sigue disponible, pero Foundation recomienda migrar a `TextFieldState` |
| `Checkbox`, `TriStateCheckbox`, `RadioButton`, `Button` y variantes | Material 3 | Estable |
| `LinearProgressIndicator`, `CircularProgressIndicator` | Material 3 | Estable |
| `LoadingIndicator`, `ContainedLoadingIndicator`, `LinearWavyProgressIndicator`, `CircularWavyProgressIndicator` | Material 3 Expressive | Experimental: `@ExperimentalMaterial3ExpressiveApi` |
| `ToggleButton`, `ButtonGroup`, tamaños de botón (`ExtraSmall` a `ExtraLarge`) | Material 3 Expressive | Experimental |

La 1.4.0 estable no expone la superficie Expressive; vuelve a ser pública en 1.5.0-beta01, y el equipo de Material ha comunicado que promoverá esas APIs a estable con la 1.5.0 "más adelante en 2026".

Dependencias para usar todo lo de esta guía:

```kotlin
// build.gradle.kts (módulo app)
dependencies {
    // Solo lo estable (TextFieldState, Checkbox, Radio, Button, progress clásico)
    implementation("androidx.compose.material3:material3:1.4.0")

    // Si quieres también Expressive (beta; puede haber una versión más reciente)
    // implementation("androidx.compose.material3:material3:1.5.0-beta01")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:<ultima>")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:<ultima>") // collectAsStateWithLifecycle
}
```

## Dónde poner el estado: ViewModel, UiState y hoisting

Regla práctica: el estado de pantalla (lo que la lógica de negocio necesita o decide) va en el ViewModel; el estado de elemento (cursor, selección, scroll, animación) puede vivir en el composable. Los componentes de Material 3 son "sin estado": reciben el valor y emiten eventos, y tú decides quién es el dueño.

```kotlin
// 1) Estado inmutable de la pantalla
data class SettingsUiState(
    val notificationsEnabled: Boolean = false,
    val theme: Theme = Theme.System,
    val isSaving: Boolean = false,
    val saveProgress: Float? = null,   // null = indeterminado
)

enum class Theme { System, Light, Dark }

// 2) ViewModel: único dueño del estado, expone StateFlow y funciones de evento
class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onNotificationsChange(enabled: Boolean) =
        _uiState.update { it.copy(notificationsEnabled = enabled) }

    fun onThemeChange(theme: Theme) =
        _uiState.update { it.copy(theme = theme) }

    fun onSave() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            // ... trabajo real
            _uiState.update { it.copy(isSaving = false) }
        }
    }
}

// 3) Pantalla "con estado": conecta ViewModel y UI
@Composable
fun SettingsRoute(viewModel: SettingsViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        state = state,
        onNotificationsChange = viewModel::onNotificationsChange,
        onThemeChange = viewModel::onThemeChange,
        onSave = viewModel::onSave,
    )
}

// 4) Pantalla "sin estado": solo pinta y emite eventos (fácil de previsualizar y testear)
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onNotificationsChange: (Boolean) -> Unit,
    onThemeChange: (Theme) -> Unit,
    onSave: () -> Unit,
) { /* los ejemplos de las siguientes secciones van aquí dentro */ }
```

Flujo unidireccional: el estado baja (ViewModel a UI) y los eventos suben (UI a ViewModel). Los ejemplos siguientes reutilizan este esquema.

## TextField: de value/onValueChange a TextFieldState

`TextFieldState` es un contenedor de texto editable y observable que reemplaza el patrón `value` + `onValueChange`: en vez de que la UI reciba un `String` y devuelva el nuevo, el campo edita directamente un objeto de estado que tú conservas.

### Cuándo se incorporó

- Foundation: `BasicTextField(state: TextFieldState)` nació como experimental (BasicTextField2) y se graduó a estable junto con `InputTransformation`, `OutputTransformation`, `TextFieldLineLimits` y `TextFieldDecorator`. Las notas oficiales de Foundation piden migrar de `BasicTextField(value, onValueChange)` a la sobrecarga con `TextFieldState`.
- Material 3: las sobrecargas `TextField(state)` y `OutlinedTextField(state)` se añadieron en 1.4.0-alpha17 y forman parte de Material 3 1.4.0 (24-sep-2025), junto con `SecureTextField` y `OutlinedSecureTextField`.
- Después se han ido puliendo: `undoState` sin anotación experimental, `TextFieldState` guarda y restaura estilos, `edit { }` ya no borra el historial de deshacer, menú contextual con clic derecho.

### Antes y ahora

```kotlin
// ANTES: el String vive en la UI y circula en cada pulsación
var text by remember { mutableStateOf("") }
TextField(
    value = text,
    onValueChange = { text = it },
    label = { Text("Nombre") },
    singleLine = true,
)

// AHORA: el campo edita un TextFieldState
val state = rememberTextFieldState(initialText = "")
TextField(
    state = state,
    label = { Text("Nombre") },
    lineLimits = TextFieldLineLimits.SingleLine,
)
```

### Cómo se usa el estado

`rememberTextFieldState` ya es guardable (sobrevive a cambios de configuración y a la recreación del proceso con `rememberSaveable`), y existe un lint que avisa si creas un `TextFieldState` sin recordarlo.

```kotlin
val state = rememberTextFieldState("Hola", initialSelection = TextRange(4))

// Leer (CharSequence; conviértelo con toString() si lo vas a guardar o comparar)
val text: String = state.text.toString()
val selection: TextRange = state.selection
val composition: TextRange? = state.composition   // texto en composición del IME

// Escribir desde código
state.setTextAndPlaceCursorAtEnd("Nuevo valor")
state.clearText()
state.edit {                        // edición atómica con cursor y selección
    replace(0, length, "Otro valor")
    placeCursorAtEnd()
}

// Reaccionar a cambios: es un State de Compose, así que snapshotFlow lo observa
LaunchedEffect(state) {
    snapshotFlow { state.text }
        .collect { /* validar, buscar, etc. */ }
}
```

Punto clave: `TextFieldState` no es inmutable, así que no lo metas dentro de un `data class UiState`; guárdalo como propiedad aparte (siguiente apartado).

### Con ViewModel (login con validación)

El ViewModel es dueño de los `TextFieldState` y deriva el error de ellos con `snapshotFlow`; el resto del estado (errores, carga) sí va en un `UiState` inmutable.

```kotlin
data class LoginUiState(
    val emailError: String? = null,
    val isLoading: Boolean = false,
)

@OptIn(FlowPreview::class)
class LoginViewModel : ViewModel() {
    // Estado de los campos: viven en el ViewModel, sobreviven a rotaciones
    val email = TextFieldState()
    val password = TextFieldState()

    private val _ui = MutableStateFlow(LoginUiState())
    val ui: StateFlow<LoginUiState> = _ui.asStateFlow()

    init {
        // Validación reactiva: cada cambio de texto pasa por snapshotFlow
        viewModelScope.launch {
            snapshotFlow { email.text.toString() }
                .debounce(300)
                .collect { value ->
                    val error = if (value.isNotEmpty() && '@' !in value) "Email no válido" else null
                    _ui.update { it.copy(emailError = error) }
                }
        }
    }

    fun onLogin() {
        if (_ui.value.isLoading) return
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true) }
            // auth(email.text.toString(), password.text.toString())
            password.clearText()
            _ui.update { it.copy(isLoading = false) }
        }
    }
}

@Composable
fun LoginRoute(vm: LoginViewModel = viewModel()) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    LoginScreen(vm.email, vm.password, ui, vm::onLogin)
}

@Composable
fun LoginScreen(
    email: TextFieldState,
    password: TextFieldState,
    ui: LoginUiState,
    onLogin: () -> Unit,
) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            state = email,
            label = { Text("Email") },
            isError = ui.emailError != null,
            supportingText = { ui.emailError?.let { Text(it) } },
            lineLimits = TextFieldLineLimits.SingleLine,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
        )
        OutlinedSecureTextField(
            state = password,
            label = { Text("Contraseña") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            onKeyboardAction = KeyboardActionHandler { onLogin() },
        )
        Button(onClick = onLogin, enabled = !ui.isLoading) { Text("Entrar") }
    }
}
```

Dos matices de diseño:

- Un `TextFieldState` dentro del ViewModel se conserva en rotaciones, pero no tras la muerte del proceso. Si necesitas eso, deja el estado en la UI con `rememberTextFieldState()` (guardable) y avisa al ViewModel con `snapshotFlow` o al pulsar enviar; o respáldalo con `SavedStateHandle`.
- Si prefieres que la pantalla no conozca el tipo `TextFieldState`, el ViewModel puede exponer `StateFlow<String>` y la UI sincronizarlo con `state.setTextAndPlaceCursorAtEnd(...)`; para formularios normales, pasar el `TextFieldState` es más simple y evita desajustes de cursor.

### Transformaciones: filtrar entrada y formatear salida

`InputTransformation` corre tras cada cambio del usuario y puede modificarlo o rechazarlo (sustituye al filtro dentro de `onValueChange`). `OutputTransformation` solo cambia lo que se muestra, sin tocar el estado (sustituye a `VisualTransformation`).

```kotlin
// Teléfono: el estado guarda solo dígitos; la pantalla muestra (123) 456-7890
class PhoneOutputTransformation : OutputTransformation {
    override fun TextFieldBuffer.transformOutput() {
        if (length > 0) insert(0, "(")
        if (length > 4) insert(4, ") ")
        if (length > 9) insert(9, "-")
    }
}

TextField(
    state = rememberTextFieldState(),
    label = { Text("Teléfono") },
    lineLimits = TextFieldLineLimits.SingleLine,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
    inputTransformation = InputTransformation
        .byValue { _, proposed -> proposed.filter { it.isDigit() } }
        .maxLength(10),
    outputTransformation = PhoneOutputTransformation(),
)
```

Imports útiles: `androidx.compose.foundation.text.input.*` (incluye las extensiones `maxLength`, `then`, `byValue`).

### Tabla de migración

| Antes (value/onValueChange) | Ahora (TextFieldState) |
| --- | --- |
| `value`, `onValueChange` | `state` |
| `singleLine = true`, `maxLines`, `minLines` | `lineLimits = TextFieldLineLimits.SingleLine` o `MultiLine(min, max)` |
| `keyboardActions = KeyboardActions(onDone = …)` | `onKeyboardAction = KeyboardActionHandler { … }` |
| `visualTransformation` | `outputTransformation` |
| Filtrar dentro de `onValueChange` | `inputTransformation` |
| `PasswordVisualTransformation` | `SecureTextField` / `OutlinedSecureTextField` |
| `TextFieldValue` (cursor y selección) | `state.selection`, `state.edit { }` |
| `label` simple | `label` + `labelPosition = TextFieldLabelPosition...` en Material 3 reciente |

## Checkbox y TriStateCheckbox

`Checkbox` no guarda nada: recibe `checked` y notifica con `onCheckedChange`; el ViewModel decide el nuevo valor. Para casilla "seleccionar todo" se usa `TriStateCheckbox`, que recibe un `ToggleableState` (`On`, `Off`, `Indeterminate`) derivado de la lista.

```kotlin
data class Task(val id: Int, val title: String, val done: Boolean)

class TasksViewModel : ViewModel() {
    private val _tasks = MutableStateFlow(
        listOf(Task(1, "Comprar pan", false), Task(2, "Llamar al banco", true), Task(3, "Revisar PR", false))
    )
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    fun onToggle(id: Int, done: Boolean) = _tasks.update { list ->
        list.map { if (it.id == id) it.copy(done = done) else it }
    }

    fun onToggleAll(done: Boolean) = _tasks.update { list -> list.map { it.copy(done = done) } }
}

@Composable
fun TasksRoute(vm: TasksViewModel = viewModel()) {
    val tasks by vm.tasks.collectAsStateWithLifecycle()
    TasksScreen(tasks, vm::onToggle, vm::onToggleAll)
}

@Composable
fun TasksScreen(
    tasks: List<Task>,
    onToggle: (id: Int, done: Boolean) -> Unit,
    onToggleAll: (Boolean) -> Unit,
) {
    // El estado del padre se DERIVA de los hijos; no se guarda aparte
    val allState = when {
        tasks.isNotEmpty() && tasks.all { it.done } -> ToggleableState.On
        tasks.none { it.done } -> ToggleableState.Off
        else -> ToggleableState.Indeterminate
    }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TriStateCheckbox(
                state = allState,
                onClick = { onToggleAll(allState != ToggleableState.On) },
            )
            Text("Seleccionar todo")
        }
        HorizontalDivider()
        LazyColumn {
            items(tasks, key = { it.id }) { task ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        // Toda la fila es el objetivo táctil y accesible
                        .toggleable(
                            value = task.done,
                            role = Role.Checkbox,
                            onValueChange = { onToggle(task.id, it) },
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(checked = task.done, onCheckedChange = null) // null: lo gestiona la fila
                    Spacer(Modifier.width(12.dp))
                    Text(task.title)
                }
            }
        }
    }
}
```

Colocación: pon el `Checkbox` a la izquierda del texto y hazlo "hijo" de una fila `toggleable` con `onCheckedChange = null`, para que lectores de pantalla anuncien un único control y el área de toque sea la fila entera. Si el `Checkbox` va solo, deja su `onCheckedChange` normal. Usa siempre `key` en `LazyColumn` para que el estado visual siga al elemento al reordenar.

## RadioButton: un grupo, una sola opción

Un `RadioButton` tampoco tiene estado propio: el "grupo" es solo un valor único en el ViewModel (aquí `theme`) y cada botón se pinta como `selected = (opción == valor)`. Para accesibilidad, agrupa con `Modifier.selectableGroup()` y haz seleccionable la fila entera.

```kotlin
// Reutiliza SettingsUiState / SettingsViewModel de la sección de estado
@Composable
fun ThemePicker(
    selected: Theme,
    onSelected: (Theme) -> Unit,
    modifier: Modifier = Modifier,
) {
    val labels = mapOf(Theme.System to "Sistema", Theme.Light to "Claro", Theme.Dark to "Oscuro")

    Column(modifier.selectableGroup()) {            // anuncia "grupo" a TalkBack
        Theme.entries.forEach { option ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)                  // objetivo táctil mínimo
                    .selectable(
                        selected = option == selected,
                        onClick = { onSelected(option) },
                        role = Role.RadioButton,
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = option == selected, onClick = null) // lo gestiona la fila
                Spacer(Modifier.width(12.dp))
                Text(labels.getValue(option))
            }
        }
    }
}

// Uso dentro de SettingsScreen
ThemePicker(selected = state.theme, onSelected = onThemeChange)
```

Cuándo no usarlo: con muchas opciones (más de 5 o 6) o si el valor ya está claro por contexto, un menú desplegable o un `SingleChoiceSegmentedButtonRow` ocupa menos. Y recuerda que un radio nunca debería quedar sin selección tras cargar: inicializa el estado con un valor por defecto válido.

## Botones: variantes, estado de carga y ToggleButton

El botón en sí casi no tiene estado: lo que importa es `enabled`, qué muestra su contenido (texto, icono o progreso) y quién recibe `onClick`. Elige la variante por jerarquía visual y no mezcles dos "primarios" en la misma pantalla.

| Variante | Cuándo |
| --- | --- |
| `Button` (filled) | Acción principal de la pantalla |
| `FilledTonalButton` | Principal-secundaria (por ejemplo "Guardar borrador") |
| `OutlinedButton` | Acción secundaria que no debe competir |
| `ElevatedButton` | Sobre fondos con mucha textura |
| `TextButton` | Acciones de baja prioridad: diálogos, "Cancelar" |
| `IconButton`, `FilledIconButton`... | Acciones solo con icono (con `contentDescription`) |

### Botón con estado de carga desde el ViewModel

Reutiliza `SettingsUiState.isSaving` y `onSave` de la sección de estado. El ViewModel ignora pulsaciones mientras guarda (ya lo hicimos con `if (isLoading) return` en el login), y el botón conserva su ancho para que no "salte".

```kotlin
@Composable
fun SaveButton(isSaving: Boolean, onSave: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onSave,
        enabled = !isSaving,
        modifier = modifier,
    ) {
        if (isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.size(ButtonDefaults.IconSize),
                strokeWidth = 2.dp,
                color = LocalContentColor.current,
            )
        } else {
            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
        }
        Spacer(Modifier.width(ButtonDefaults.IconSpacing))
        Text(if (isSaving) "Guardando…" else "Guardar")
    }
}
```

Colocación: la acción principal va al final del flujo (abajo a la derecha en diálogos, ancho completo en formularios móviles); en una fila de dos botones, el secundario a la izquierda del principal. El icono, si lo hay, precede al texto.

### Material 3 Expressive: ToggleButton y tamaños

Algo nuevo (experimental): `ToggleButton` es un botón con estado `checked` que cambia de color primario a tonal y "morfa" entre tres formas (reposo, pulsado, marcado) según la interacción. Además hay cinco tamaños (`ExtraSmall`, `Small`, `Medium`, `Large`, `ExtraLarge`), y los valores por defecto (alto, forma, relleno, icono) se derivan del alto elegido.

```kotlin
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NotificationsToggle(enabled: Boolean, onChange: (Boolean) -> Unit) {
    val size = ButtonDefaults.MediumContainerHeight
    ToggleButton(
        checked = enabled,
        onCheckedChange = onChange,                 // el ViewModel es el dueño
        modifier = Modifier.heightIn(size),
        shapes = ToggleButtonDefaults.shapesFor(size),
        contentPadding = ButtonDefaults.contentPaddingFor(size),
    ) {
        Icon(
            if (enabled) Icons.Filled.Notifications else Icons.Outlined.Notifications,
            contentDescription = null,
            modifier = Modifier.size(ButtonDefaults.iconSizeFor(size)),
        )
        Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(size)))
        Text("Avisos", style = ButtonDefaults.textStyleFor(size))
    }
}

// En SettingsScreen
NotificationsToggle(state.notificationsEnabled, onNotificationsChange)
```

El mismo patrón con `ExtraSmallContainerHeight` o `ExtraLargeContainerHeight` cambia todo el conjunto de golpe. Para agrupar varios `ToggleButton` conectados existe `ButtonGroup`; consulta la referencia de tu versión exacta antes de usarlo, porque al ser experimental su API puede cambiar.

## Indicadores de progreso de Material 3 (estables)

Hay dos formas (lineal y circular) y dos modos: determinado (sabes el avance, `progress = { 0.4f }`) e indeterminado (sin parámetro de progreso). Regla de uso: determinado cuando puedes medir, indeterminado cuando no. El valor de progreso se pasa como lambda (`() -> Float`) para que solo se vuelva a dibujar el indicador, no el composable padre.

En el ViewModel, `progress: Float?` expresa ambos modos: `null` significa "trabajando, sin medida".

```kotlin
data class UploadUiState(
    val isUploading: Boolean = false,
    val progress: Float? = null,      // null = indeterminado
)

class UploadViewModel : ViewModel() {
    private val _ui = MutableStateFlow(UploadUiState())
    val ui: StateFlow<UploadUiState> = _ui.asStateFlow()

    fun startUpload() {
        viewModelScope.launch {
            _ui.value = UploadUiState(isUploading = true, progress = null) // preparando
            delay(800)
            for (step in 1..10) {                                           // subida simulada
                delay(300)
                _ui.value = UploadUiState(isUploading = true, progress = step / 10f)
            }
            _ui.value = UploadUiState()
        }
    }
}

@Composable
fun UploadProgress(ui: UploadUiState, modifier: Modifier = Modifier) {
    if (!ui.isUploading) return
    val progress = ui.progress
    if (progress == null) {
        LinearProgressIndicator(modifier.fillMaxWidth())                    // indeterminado
    } else {
        // Suaviza los saltos entre valores que llegan del ViewModel
        val animated by animateFloatAsState(
            targetValue = progress,
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
            label = "upload-progress",
        )
        LinearProgressIndicator(progress = { animated }, modifier = modifier.fillMaxWidth())
    }
}
```

### Colocación

- Lineal: ancho completo, justo bajo la barra superior o dentro de la tarjeta/fila a la que se refiere (una subida concreta). Reserva su altura para que el contenido no salte al aparecer.
- Circular: centrado en el contenedor cuando carga una pantalla entera, o pequeño (20 a 24 dp, `strokeWidth = 2.dp`) dentro de un botón, como en `SaveButton`.
- Pantalla completa: modela la carga como un estado, no como un booleano suelto.

```kotlin
sealed interface ProfileState {
    data object Loading : ProfileState
    data class Content(val name: String) : ProfileState
    data class Error(val message: String) : ProfileState
}

@Composable
fun ProfileScreen(state: ProfileState) {
    when (state) {
        ProfileState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is ProfileState.Content -> Text(state.name)
        is ProfileState.Error -> Text(state.message)
    }
}
```

Para "tirar para recargar" no pongas tu propio indicador: usa `PullToRefreshBox`, que ya integra el indicador adecuado.

## Material 3 Expressive: LoadingIndicator y progreso ondulado

Expressive añade dos familias: `LoadingIndicator` (una forma que se transforma continuamente, para esperas cortas de duración desconocida) y los indicadores wavy (la barra o el anillo con onda, para progreso). Todo es experimental: necesitas `material3:1.5.0-beta01` o posterior y `@OptIn(ExperimentalMaterial3ExpressiveApi::class)`; con la 1.4.x estable no compila. En Compose Multiplatform, las APIs Expressive vienen en la rama alpha de `org.jetbrains.compose.material3`.

| Situación | Material 3 clásico | Expressive |
| --- | --- | --- |
| Espera corta, duración desconocida | `CircularProgressIndicator()` | `LoadingIndicator()` o `ContainedLoadingIndicator()` |
| Pull to refresh | Indicador por defecto | `PullToRefreshDefaults.LoadingIndicator(state, isRefreshing)` |
| Progreso medible, barra | `LinearProgressIndicator(progress = { p })` | `LinearWavyProgressIndicator(progress = { p })` |
| Progreso medible, anillo | `CircularProgressIndicator(progress = { p })` | `CircularWavyProgressIndicator(progress = { p })` |
| Espera larga e indeterminada | `LinearProgressIndicator()` | `LinearWavyProgressIndicator()` |

### LoadingIndicator y ContainedLoadingIndicator

`LoadingIndicator` dibuja formas poligonales que se transforman entre sí. Sin argumento de progreso anima indefinidamente; con `progress = { 0.5f }` la forma depende del avance. `ContainedLoadingIndicator` lo envuelve en un contenedor con color y forma propios, útil sobre contenido que se mezclaría con el indicador.

```kotlin
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ContentLoading(state: ProfileState) {            // ProfileState de la sección anterior
    when (state) {
        ProfileState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoadingIndicator()                         // indeterminado
        }
        is ProfileState.Content -> Text(state.name)
        is ProfileState.Error -> Text(state.message)
    }
}

// Determinado: la forma se transforma según el progreso que viene del ViewModel
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UploadLoading(progress: Float) {
    val animated by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessVeryLow,
        ),
        label = "loading-progress",
    )
    ContainedLoadingIndicator(progress = { animated })
}
```

Puedes cambiar `color`, `containerColor`, `containerShape` y la lista de `polygons` (con `MaterialShapes`) si la identidad de la marca lo pide.

### Wavy: lineal y circular

Aceptan `amplitude` (0 = línea recta, 1 = onda completa), `wavelength` (longitud de onda) y `waveSpeed` (dp por segundo; por defecto una longitud de onda por segundo), además de `stroke`, `trackStroke`, `gapSize`, `color` y `trackColor`. Con `progress` son determinados; sin él, indeterminados y la onda se desplaza sola. En la variante determinada, `amplitude` es una lambda que recibe el progreso, para poder aplanar la onda al terminar.

```kotlin
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UploadProgressExpressive(ui: UploadUiState, modifier: Modifier = Modifier) {
    if (!ui.isUploading) return
    val progress = ui.progress

    if (progress == null) {
        LinearWavyProgressIndicator(modifier.fillMaxWidth())       // indeterminado
    } else {
        val animated by animateFloatAsState(
            targetValue = progress,
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
            label = "wavy-progress",
        )
        // Trazo grueso: pide algo más de altura que el valor por defecto
        val thickWidth = with(LocalDensity.current) { 8.dp.toPx() }
        val thickStroke = remember(thickWidth) { Stroke(width = thickWidth, cap = StrokeCap.Round) }

        LinearWavyProgressIndicator(
            progress = { animated },
            modifier = modifier.fillMaxWidth().height(14.dp),
            stroke = thickStroke,
            trackStroke = thickStroke,
        )
    }
}

// Circular
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RingProgress(progress: Float) {
    CircularWavyProgressIndicator(progress = { progress })
}
```

El ViewModel no cambia: sigue exponiendo `UploadUiState(progress: Float?)`. Esa es la ventaja de separar el estado de la pintura: pasar de clásico a Expressive es cambiar el composable de la pantalla.

### Tema Expressive

Para que los componentes tomen forma y movimiento Expressive envuelve la app en `MaterialExpressiveTheme`, que acepta `colorScheme`, `motionScheme` (por ejemplo `MotionScheme.expressive()`), `shapes` y `typography`:

```kotlin
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialExpressiveTheme(
        motionScheme = MotionScheme.expressive(),
        content = content,
    )
}
```

## Fuentes y límites de esta guía

Los ejemplos de código siguen las firmas de la documentación oficial, pero no los he compilado; revisa imports y nombres contra tu versión exacta de Material 3. El número de versión de Foundation en que se estabilizó `BasicTextField(state)` (1.8) es de memoria. Las fuentes de abajo son resultados de búsqueda, no todas las páginas se abrieron enteras.

- [Notas de versión de Compose Material 3](https://developer.android.com/jetpack/androidx/releases/compose-material3): `TextField(state)` desde 1.4.0-alpha17, última beta 1.5.0
- [Notas de versión de Compose Foundation](https://developer.android.com/jetpack/androidx/releases/compose-foundation): `BasicTextField(state)` estable, cambios en `TextFieldState`
- [Cambio "Stabilize BasicTextField(state)"](https://android.googlesource.com/platform/frameworks/support/+/0247fceafdff10de3ae94e64507c21f5ee35d1dc%5E%21)
- [Referencia de TextField con TextFieldState](https://composables.com/docs/androidx.compose.material3/material3/components/TextField)
- [OutputTransformation](https://developer.android.com/reference/kotlin/androidx/compose/foundation/text/input/OutputTransformation) y [paquete text.input](https://developer.android.com/reference/kotlin/androidx/compose/foundation/text/input/package-summary)
- [Novedades de Material 3 1.4.0](https://medium.com/@basit.shaabaz/jetpack-compose-material-3-whats-new-in-1-4-0-sept-24-2025-d85f828ec8a5) (`SecureTextField`)
- [LoadingIndicator](https://developer.android.com/reference/kotlin/androidx/compose/material3/LoadingIndicator.composable) y [ContainedLoadingIndicator](https://developer.android.com/reference/kotlin/androidx/compose/material3/ContainedLoadingIndicator.composable)
- [LinearWavyProgressIndicator](https://developer.android.com/reference/kotlin/androidx/compose/material3/LinearWavyProgressIndicator.composable) y [CircularWavyProgressIndicator](https://developer.android.com/reference/kotlin/androidx/compose/material3/CircularWavyProgressIndicator.composable)
- [ToggleButton](https://composables.com/jetpack-compose/androidx.compose.material3/material3/components/ToggleButton.md)
- [Material Android is Compose-first](https://m3.material.io/blog/material-is-compose-first): plan de estabilizar Expressive con la 1.5.0 en 2026
- [Discusión sobre 1.4.0 sin API Expressive pública](https://github.com/seijikohara/femto-car-launcher/issues/427)
- [Compose Multiplatform 1.9: MaterialExpressiveTheme](https://kotlinlang.org/docs/multiplatform/whats-new-compose-190.html)
