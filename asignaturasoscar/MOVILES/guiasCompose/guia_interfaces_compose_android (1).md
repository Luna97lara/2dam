# Guía práctica: interfaces en Android con Jetpack Compose

Guía equivalente a la de Views/XML, pero para construir pantallas con **Jetpack Compose**: qué layouts usar, cómo pensar el tamaño y la posición, cuándo usar porcentajes o medidas fijas, y cómo organizar un proyecto Compose.

---

## Índice

1. [Mentalidad: de XML a Compose](#1-mentalidad-de-xml-a-compose)
2. [Los contenedores básicos: Row, Column, Box](#2-los-contenedores-básicos-row-column-box)
3. [Modifiers: el equivalente a las constraints](#3-modifiers-el-equivalente-a-las-constraints)
4. [Tamaños: wrap, fill, weight y medidas exactas](#4-tamaños-wrap-fill-weight-y-medidas-exactas)
5. [¿Cuándo usar ConstraintLayout en Compose?](#5-cuándo-usar-constraintlayout-en-compose)
6. [Porcentajes vs medidas exactas](#6-porcentajes-vs-medidas-exactas)
7. [Alineación, arrangement y padding](#7-alineación-arrangement-y-padding)
8. [Material 3: Theme, tokens y componentes](#8-material-3-theme-tokens-y-componentes)
9. [Listas y contenido dinámico](#9-listas-y-contenido-dinámico)
10. [Scroll, teclado e insets](#10-scroll-teclado-e-insets)
11. [Diseño adaptable (móvil, tablet, plegables)](#11-diseño-adaptable)
12. [Estado: state hoisting y recomposición](#12-estado-state-hoisting-y-recomposición)
13. [Organización del proyecto](#13-organización-del-proyecto)
14. [Accesibilidad](#14-accesibilidad)
15. [Rendimiento](#15-rendimiento)
16. [Ejemplo completo](#16-ejemplo-completo-pantalla-de-login)
17. [Errores comunes](#17-errores-comunes)
18. [Checklist final](#18-checklist-final)

---

## 1. Mentalidad: de XML a Compose

En XML piensas en **vistas y constraints**: "esta vista está anclada a aquella". En Compose piensas en **funciones que describen la UI** y en **cómo se anidan unas dentro de otras**. No hay IDs, no hay `findViewById`, no hay un árbol de XML: todo es código Kotlin.

Equivalencias rápidas:

| XML / View | Compose |
|---|---|
| `ConstraintLayout` | `ConstraintLayout` (sí existe, ver sección 5) o composición de `Row`/`Column`/`Box` |
| `LinearLayout` horizontal | `Row` |
| `LinearLayout` vertical | `Column` |
| `FrameLayout` (superponer) | `Box` |
| `RecyclerView` | `LazyColumn` / `LazyRow` / `LazyVerticalGrid` |
| `ScrollView` / `NestedScrollView` | `Modifier.verticalScroll()` |
| `include layout` | Función `@Composable` reutilizable |
| `styles.xml` | `MaterialTheme` + objetos de estilo en Kotlin |
| `dimens.xml` | Constantes `val` o un objeto `Dimens` |
| `strings.xml` | Sigue siendo `strings.xml` (se accede con `stringResource()`) |
| `android:id` | No hace falta (se referencia la variable/composable directamente) |
| `findViewById` + listener | Lambda pasada como parámetro (`onClick: () -> Unit`) |

> La gran diferencia mental: en XML declaras **de qué depende cada vista**; en Compose declaras **una jerarquía de funciones**, y el tamaño/posición surge de cómo las anidas y qué `Modifier` les aplicas.

---

## 2. Los contenedores básicos: Row, Column, Box

Son el 90 % de lo que necesitas para maquetar. Equivalen a `LinearLayout` (horizontal/vertical) y `FrameLayout`.

```kotlin
@Composable
fun EjemploRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Izquierda")
        Text("Derecha")
    }
}

@Composable
fun EjemploColumn() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Línea 1")
        Text("Línea 2")
    }
}

@Composable
fun EjemploBox() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.fondo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            "Texto superpuesto",
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
```

### Cuándo usar cada uno

| Situación | Composable |
|---|---|
| Elementos uno al lado de otro | `Row` |
| Elementos uno debajo de otro | `Column` |
| Superponer elementos (imagen + texto, badge sobre icono) | `Box` |
| Relaciones complejas entre muchas vistas, tipo "constraints cruzadas" | `ConstraintLayout` (ver sección 5) |
| Listas largas | `LazyColumn` / `LazyRow` |

A diferencia de XML, **anidar `Row`/`Column`/`Box` no es un problema de rendimiento** como podía serlo anidar `LinearLayout`s con `layout_weight`. Compose está optimizado para esto, así que no fuerces todo a un único `ConstraintLayout` "para aplanar la jerarquía"; en Compose, **la composición natural es anidar contenedores simples**.

---

## 3. Modifiers: el equivalente a las constraints

En XML, el tamaño y la posición se definen con atributos del layout (`layout_width`, `layout_constraintTop_toTopOf`...). En Compose, **todo pasa por el parámetro `modifier`**, que es una cadena de transformaciones aplicadas en orden.

```kotlin
Text(
    text = "Hola",
    modifier = Modifier
        .padding(16.dp)
        .background(Color.LightGray)
        .padding(8.dp)
        .fillMaxWidth()
)
```

### Regla de oro

> **El orden de los modifiers importa.** Cada uno envuelve al resultado del anterior. `padding` antes de `background` deja un margen fuera del fondo; `background` antes de `padding` pinta el fondo hasta el borde y luego añade espacio interno.

### Modifiers más usados

| Modifier | Equivalente en XML |
|---|---|
| `.fillMaxWidth()` / `.fillMaxHeight()` / `.fillMaxSize()` | `match_parent` |
| `.wrapContentSize()` | `wrap_content` (ya es el comportamiento por defecto) |
| `.width(100.dp)` / `.height(48.dp)` / `.size(48.dp)` | tamaño fijo en dp |
| `.weight(1f)` (dentro de Row/Column) | `layout_weight` |
| `.padding(16.dp)` | `layout_margin` / `padding` |
| `.align(Alignment.Center)` (dentro de Box) | constraints de centrado |
| `.offset(x = 4.dp, y = 4.dp)` | ajuste fino de posición |
| `.aspectRatio(16f/9f)` | `layout_constraintDimensionRatio` |
| `.clickable { }` | `OnClickListener` |
| `.testTag("login_button")` | `android:id` (para tests) |

---

## 4. Tamaños: wrap, fill, weight y medidas exactas

En Compose **no existe un equivalente directo a `0dp` (match_constraint)**; en su lugar usas `fillMaxWidth()`, `weight()` o tamaños explícitos según el contenedor.

### Por defecto: wrap_content

Si no pones ningún modifier de tamaño, un composable ocupa **el mínimo necesario para su contenido** — el equivalente a `wrap_content`.

```kotlin
Text("Texto corto") // ya es "wrap_content" por defecto
```

### Ocupar todo el ancho/alto disponible

```kotlin
Button(
    onClick = { },
    modifier = Modifier.fillMaxWidth()
) {
    Text("Continuar")
}
```

### Repartir espacio con `weight` (como `layout_weight` o las chains con pesos)

```kotlin
Row(modifier = Modifier.fillMaxWidth()) {
    Button(
        onClick = { },
        modifier = Modifier.weight(1f)
    ) { Text("Cancelar") }

    Spacer(modifier = Modifier.width(8.dp))

    Button(
        onClick = { },
        modifier = Modifier.weight(2f)
    ) { Text("Confirmar") }
}
```

`weight(1f)` y `weight(2f)` reparten el espacio sobrante en proporción 1:2 — igual que `layout_constraintHorizontal_weight` en chains de ConstraintLayout.

### Tamaño mínimo/máximo

```kotlin
Modifier
    .widthIn(min = 100.dp, max = 400.dp)
    .heightIn(min = 48.dp)
```

### Tamaño fijo

```kotlin
Icon(
    imageVector = Icons.Default.Favorite,
    contentDescription = stringResource(R.string.favorite),
    modifier = Modifier.size(24.dp)
)
```

### Resumen comparativo

| XML (ConstraintLayout) | Compose |
|---|---|
| `wrap_content` | Sin modifier de tamaño (comportamiento por defecto) |
| `match_parent` | `.fillMaxWidth()` / `.fillMaxHeight()` / `.fillMaxSize()` |
| `0dp` + 2 anclajes opuestos | `.fillMaxWidth()` dentro del contenedor, o `.weight()` en una chain |
| `0dp` + `layout_constraintWidth_percent` | `.fillMaxWidth(fraction = 0.6f)` |
| `layout_constraintWidth_min/max` | `.widthIn(min = ..., max = ...)` |
| Peso en chains (`constraintHorizontal_weight`) | `.weight(n)` dentro de `Row`/`Column` |
| `layout_constraintDimensionRatio` | `.aspectRatio(ratio)` |

---

## 5. ¿Cuándo usar ConstraintLayout en Compose?

Sí, **existe** la librería `androidx.constraintlayout:constraintlayout-compose`, con una API muy parecida en concepto a la de XML (referencias, `linkTo`, guidelines, barriers, chains), pero en Kotlin.

```kotlin
ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
    val (avatar, nombre, boton) = createRefs()

    Image(
        painter = painterResource(R.drawable.avatar),
        contentDescription = null,
        modifier = Modifier
            .size(48.dp)
            .constrainAs(avatar) {
                start.linkTo(parent.start, margin = 16.dp)
                top.linkTo(parent.top, margin = 16.dp)
            }
    )

    Text(
        text = "Nombre de usuario",
        modifier = Modifier.constrainAs(nombre) {
            start.linkTo(avatar.end, margin = 8.dp)
            top.linkTo(avatar.top)
            bottom.linkTo(avatar.bottom)
        }
    )

    Button(
        onClick = { },
        modifier = Modifier.constrainAs(boton) {
            end.linkTo(parent.end, margin = 16.dp)
            top.linkTo(avatar.top)
            bottom.linkTo(avatar.bottom)
        }
    ) {
        Text("Seguir")
    }
}
```

### ¿Cuándo usarlo?

| Situación | Recomendación |
|---|---|
| Layout simple o moderado (la mayoría de pantallas) | **`Row`/`Column`/`Box`** — más idiomático, más fácil de leer |
| Muchas vistas con relaciones cruzadas difíciles de expresar con anidamiento | `ConstraintLayout` |
| Necesitas animar relaciones de posición entre estados (como `MotionLayout`) | `ConstraintLayout` + `MotionLayout` para Compose |
| Vienes de XML y quieres portar un layout complejo casi 1:1 | `ConstraintLayout` como paso intermedio, para migrar luego a `Row`/`Column` si procede |

> **A diferencia de XML, en Compose `ConstraintLayout` ya no es "la opción por defecto".** La comunidad y Google recomiendan usar primero `Row`, `Column` y `Box`, y reservar `ConstraintLayout` para los casos realmente complejos donde anidar contenedores sería más difícil de mantener que un conjunto de constraints.

---

## 6. Porcentajes vs medidas exactas

Igual que con las guidelines en XML, la pregunta es: ¿esta medida debe ser proporcional a la pantalla o fija en dp?

### Porcentaje del ancho/alto disponible

```kotlin
Box(
    modifier = Modifier
        .fillMaxWidth(fraction = 0.6f)
        .fillMaxHeight(fraction = 0.3f)
)
```

### Repartir proporcionalmente con `weight`

```kotlin
Row(modifier = Modifier.fillMaxSize()) {
    Box(modifier = Modifier.weight(0.4f)) { /* imagen */ }
    Box(modifier = Modifier.weight(0.6f)) { /* formulario */ }
}
```

### Medidas fijas en dp

```kotlin
Modifier.padding(horizontal = 24.dp)
```

### Guía de decisión (igual que con las guidelines en XML)

| Usa **porcentaje** / `weight` | Usa **dp fijo** |
|---|---|
| Dividir la pantalla en zonas proporcionales (imagen 40 % / formulario 60 %) | Márgenes laterales de contenido (24dp en todos los dispositivos) |
| Repartir botones o columnas en proporción | Tamaños de icono, avatar, altura de barra |
| Adaptarse a pantallas muy distintas (móvil pequeño vs tablet) | Separaciones entre elementos de una lista |
| Alturas de secciones "hero" | Tamaños táctiles mínimos (48dp) |

### No hay guidelines como tales, pero el equivalente es:

- Una **constante reutilizable** (`val ScreenPadding = 24.dp`) aplicada con `.padding()`.
- Un **`Modifier` de extensión** propio para repetir una combinación de paddings/tamaños en todo el proyecto.
- En `ConstraintLayout` para Compose, si realmente necesitas una guideline, existe `createGuidelineFromStart(0.5f)` o `createGuidelineFromStart(24.dp)`, con la misma lógica que en XML.

```kotlin
ConstraintLayout {
    val guideline = createGuidelineFromStart(fraction = 0.5f)
    // o: createGuidelineFromStart(margin = 24.dp)
}
```

---

## 7. Alineación, arrangement y padding

### `Arrangement` (cómo se reparten los hijos en el eje principal)

```kotlin
Row(horizontalArrangement = Arrangement.SpaceBetween) { ... }
Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { ... }
Column(verticalArrangement = Arrangement.Center) { ... }
```

| Arrangement | Efecto |
|---|---|
| `Start` / `Top` (por defecto) | Pegados al inicio |
| `End` / `Bottom` | Pegados al final |
| `Center` | Centrados juntos |
| `SpaceBetween` | Extremos pegados, espacio repartido entre medios |
| `SpaceAround` | Espacio igual alrededor de cada uno |
| `SpaceEvenly` | Espacio idéntico entre todos, incluidos extremos |
| `spacedBy(8.dp)` | Separación fija entre elementos |

### `Alignment` (cómo se alinean en el eje transversal)

```kotlin
Row(verticalAlignment = Alignment.CenterVertically) { ... }
Column(horizontalAlignment = Alignment.CenterHorizontally) { ... }
Box(contentAlignment = Alignment.Center) { ... }
```

### Padding: contentPadding vs Modifier.padding

- `Modifier.padding(16.dp)`: espacio alrededor del propio composable.
- `contentPadding` (en `LazyColumn`, `LazyRow`): espacio interno de la zona de scroll, respetando que el contenido se vea al hacer scroll hasta el final, muy usado junto con **insets** (ver sección 10).

---

## 8. Material 3: Theme, tokens y componentes

Compose tiene una integración con Material 3 mucho más directa que las vistas XML.

### Theme

```kotlin
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

- **Color dinámico (Material You)**: con `dynamicColor = true` en Android 12+, la paleta se genera a partir del wallpaper del usuario. Es exclusivo de Compose/Views modernas; no existe en XML tradicional sin pasar por atributos de tema equivalentes.
- Accede a los colores con `MaterialTheme.colorScheme.primary`, nunca con `Color(0xFF...)` hardcodeado.
- Accede a tipografías con `MaterialTheme.typography.headlineMedium`, `.bodyLarge`, etc.

### Componentes

Usa siempre los de `androidx.compose.material3`: `Button`, `OutlinedTextField`, `TextField`, `TopAppBar`, `Scaffold`, `Card`, `NavigationBar`... Igual que en XML preferías `MaterialButton` a `Button` genérico.

### Material Theme Builder

Puedes generar el esquema de color completo (claro/oscuro) desde `m3.material.io` y exportarlo como código Compose (`Color.kt` + `Theme.kt`), lo cual es más directo que en XML, donde había que traducirlo a `colors.xml` y atributos de tema a mano.

---

## 9. Listas y contenido dinámico

El equivalente a `RecyclerView` + `Adapter` + `ViewHolder` es `LazyColumn` (o `LazyRow`, `LazyVerticalGrid`), con muchísimo menos código.

```kotlin
@Composable
fun ListaUsuarios(usuarios: List<Usuario>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = usuarios,
            key = { it.id } // clave estable: evita recomposiciones innecesarias
        ) { usuario ->
            TarjetaUsuario(usuario)
        }
    }
}
```

- **`key = { it.id }`** es importante: sin una clave estable, Compose puede recomponer o animar mal al insertar/eliminar elementos. Es el equivalente a los `DiffUtil`/`stable ids` de RecyclerView.
- No hace falta `ViewHolder` ni `notifyDataSetChanged`: Compose recompone solo los ítems que cambian.
- Para rejillas: `LazyVerticalGrid(columns = GridCells.Fixed(2))` o `GridCells.Adaptive(minSize = 120.dp)` (este último es el equivalente "responsive" sin tener que calcular columnas a mano).

---

## 10. Scroll, teclado e insets

### Scroll simple (equivalente a NestedScrollView)

```kotlin
Column(
    modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
) {
    // contenido que puede no caber
}
```

> Igual que en XML: si usas `LazyColumn`, **no la metas dentro de un `Modifier.verticalScroll()`**; son dos sistemas de scroll distintos y chocan. `LazyColumn` ya gestiona su propio scroll.

### Teclado

Compose respeta `windowSoftInputMode="adjustResize"` del manifest igual que las vistas. Además, hay un inset específico:

```kotlin
Modifier.imePadding() // añade padding igual a la altura del teclado cuando está visible
```

Muy útil en formularios dentro de un `Scaffold`, para que el campo activo no quede tapado.

### Edge-to-edge y window insets

Compose tiene modifiers dedicados, más cómodos que gestionar `WindowInsetsCompat` a mano:

```kotlin
Scaffold(
    modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing)
) { padding ->
    // contenido
}
```

- `WindowInsets.safeDrawing`: combina barra de estado, barra de navegación y recorte de pantalla (notch).
- `Modifier.statusBarsPadding()`, `.navigationBarsPadding()`, `.imePadding()`: insets específicos cuando necesitas controlarlos por separado.
- En `onCreate`, igual que en XML, llama a `enableEdgeToEdge()` antes de `setContent { }`.

---

## 11. Diseño adaptable

### 11.1 ¿De dónde sale `WindowSizeClass`?

`WindowSizeClass` no es magia ni algo que Compose adivine solo: viene de la librería **`androidx.compose.material3.adaptive` / `androidx.compose.material3.windowsizeclass`** (en versiones nuevas de Material 3 ya viene integrada), que mide la ventana real en la que vive tu `Activity` — no el tamaño físico del dispositivo, sino el tamaño **disponible para tu app en ese momento** (importante en pantallas plegables, modo multiventana o split-screen).

Dependencia necesaria:

```kotlin
// build.gradle.kts (módulo app)
dependencies {
    implementation("androidx.compose.material3:material3-window-size-class")
    // o, en versiones recientes de Material 3, ya viene incluida en material3
}
```

Se calcula **una vez, en la Activity**, con `calculateWindowSizeClass(this)`, y se pasa hacia abajo como parámetro — nunca se recalcula dentro de cada composable hijo:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiAppTheme {
                // Aquí nace el windowSizeClass: se mide la Activity actual
                val windowSizeClass = calculateWindowSizeClass(this)
                AppRoot(windowSizeClass = windowSizeClass)
            }
        }
    }
}
```

`WindowSizeClass` tiene dos ejes, y casi siempre se usa el de ancho:

```kotlin
windowSizeClass.widthSizeClass  // WindowWidthSizeClass.Compact / Medium / Expanded
windowSizeClass.heightSizeClass // WindowHeightSizeClass.Compact / Medium / Expanded
```

| `WindowWidthSizeClass` | Ancho aproximado | Ejemplo típico |
|---|---|---|
| `Compact` | < 600dp | Móvil en vertical |
| `Medium` | 600dp – 840dp | Móvil en horizontal, tablet pequeña en vertical, plegable abierto |
| `Expanded` | ≥ 840dp | Tablet grande, escritorio, plegable grande |

> **Se recalcula automáticamente** cuando la ventana cambia de tamaño (rotación, redimensionar en multiventana, plegar/desplegar), porque `calculateWindowSizeClass` está implementado como una función `@Composable` que observa la configuración actual — no necesitas detectar el cambio tú mismo con listeners.

### 11.2 Flujo completo: Activity → WindowSizeClass → pantalla adaptable

Aquí está el recorrido entero, desde la `Activity` hasta los composables reutilizados, para una app sencilla de "bandeja de entrada" (lista de mensajes + detalle), el caso clásico de layout maestro-detalle.

```kotlin
// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val windowSizeClass = calculateWindowSizeClass(this)
                    BandejaEntradaApp(windowSizeClass = windowSizeClass)
                }
            }
        }
    }
}
```

```kotlin
// BandejaEntradaApp.kt
@Composable
fun BandejaEntradaApp(
    windowSizeClass: WindowSizeClass,
    viewModel: BandejaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // La decisión de layout se toma UNA vez, en lo alto del árbol,
    // y de ahí para abajo todo es composición normal.
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            BandejaCompacta(
                mensajes = uiState.mensajes,
                mensajeSeleccionado = uiState.seleccionado,
                onMensajeClick = viewModel::onMensajeSeleccionado,
                onVolver = viewModel::onVolverALista
            )
        }
        WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> {
            BandejaExpandida(
                mensajes = uiState.mensajes,
                mensajeSeleccionado = uiState.seleccionado,
                onMensajeClick = viewModel::onMensajeSeleccionado
            )
        }
    }
}
```

#### Layout compacto (móvil): navegación por pantallas, una cosa a la vez

```kotlin
@Composable
fun BandejaCompacta(
    mensajes: List<Mensaje>,
    mensajeSeleccionado: Mensaje?,
    onMensajeClick: (Mensaje) -> Unit,
    onVolver: () -> Unit
) {
    // En móvil solo cabe una cosa: lista O detalle, nunca las dos
    if (mensajeSeleccionado == null) {
        ListaMensajes(
            mensajes = mensajes,
            onMensajeClick = onMensajeClick,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        DetalleMensaje(
            mensaje = mensajeSeleccionado,
            onVolver = onVolver,
            modifier = Modifier.fillMaxSize()
        )
    }
}
```

#### Layout expandido (tablet/escritorio): las dos columnas a la vez

```kotlin
@Composable
fun BandejaExpandida(
    mensajes: List<Mensaje>,
    mensajeSeleccionado: Mensaje?,
    onMensajeClick: (Mensaje) -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        ListaMensajes(
            mensajes = mensajes,
            onMensajeClick = onMensajeClick,
            modifier = Modifier
                .weight(0.35f)
                .fillMaxHeight()
        )

        VerticalDivider()

        if (mensajeSeleccionado != null) {
            DetalleMensaje(
                mensaje = mensajeSeleccionado,
                onVolver = null, // en tablet no hace falta botón "volver": las dos columnas conviven
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxHeight()
            )
        } else {
            // Estado vacío cuando aún no se ha seleccionado nada
            Box(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.select_a_message),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
```

#### Los composables que se reutilizan en ambos layouts

Esta es la parte clave: **`ListaMensajes` y `DetalleMensaje` no saben ni les importa si están en modo compacto o expandido.** Son composables "tontos" (reciben datos y lambdas, no leen `WindowSizeClass`), así que el mismo código sirve en los dos layouts sin duplicar nada:

```kotlin
@Composable
fun ListaMensajes(
    mensajes: List<Mensaje>,
    onMensajeClick: (Mensaje) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(mensajes, key = { it.id }) { mensaje ->
            ListItem(
                headlineContent = { Text(mensaje.remitente) },
                supportingContent = {
                    Text(mensaje.asunto, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                leadingContent = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                modifier = Modifier.clickable { onMensajeClick(mensaje) }
            )
        }
    }
}

@Composable
fun DetalleMensaje(
    mensaje: Mensaje,
    onVolver: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // El botón "volver" solo se muestra si se recibe el callback
        // (en expandido se pasa null porque no hace falta)
        onVolver?.let {
            IconButton(onClick = it) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
        }
        Text(mensaje.asunto, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(mensaje.remitente, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(16.dp))
        Text(mensaje.cuerpo, style = MaterialTheme.typography.bodyLarge)
    }
}
```

> **Patrón a retener:** el componente que decide *qué layout usar* (`BandejaEntradaApp`) vive arriba del todo y es el único que conoce `WindowSizeClass`. Los composables de contenido (`ListaMensajes`, `DetalleMensaje`) son agnósticos al tamaño de pantalla: solo reciben `Modifier`, datos y callbacks. Esto es justo lo que en XML lograbas con `<include>` reutilizando un layout en `layout/` y en `layout-sw600dp/`, pero aquí sin duplicar el archivo: **un único árbol de composables que se reorganiza según cómo lo invoques.**

### 11.3 ¿Cuándo usar `WindowWidthSizeClass` y cuándo `BoxWithConstraints`?

| Situación | Herramienta |
|---|---|
| Decisión de alto nivel ("¿pantalla completa o maestro-detalle?") | `WindowSizeClass` desde la Activity |
| Un componente concreto necesita adaptarse a **su propio** espacio disponible (no al de toda la pantalla), ej. una tarjeta dentro de un grid que puede estar más o menos ancha | `BoxWithConstraints` |

```kotlin
@Composable
fun TarjetaAdaptable(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier) {
        if (maxWidth < 300.dp) {
            ContenidoCompactoDeTarjeta()
        } else {
            ContenidoAmplioDeTarjeta()
        }
    }
}
```

`BoxWithConstraints` mide el espacio **local** (el que le ha tocado a ese composable concreto dentro de su padre), mientras que `WindowSizeClass` mide la ventana completa de la Activity. Usa `WindowSizeClass` para la estructura general de la pantalla, y `BoxWithConstraints`/`GridCells.Adaptive` para componentes individuales que deban reaccionar a su propio ancho.

### 11.4 Ancho máximo de contenido

```kotlin
Modifier
    .fillMaxWidth()
    .widthIn(max = 480.dp)
```

Útil incluso dentro de un layout `Expanded`: por ejemplo, el panel de detalle puede ocupar `weight(0.65f)` de la pantalla, pero el texto dentro no debería estirarse a 1200dp de ancho porque se vuelve difícil de leer; combínalo con `widthIn(max = ...)` centrado.

### 11.5 Previsualización en distintos tamaños

```kotlin
@Preview(name = "Móvil", widthDp = 360, heightDp = 640)
@Preview(name = "Tablet", widthDp = 840, heightDp = 1024)
@Preview(name = "Fuente grande", fontScale = 1.5f)
@Preview(name = "Modo oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewLogin() {
    MiAppTheme { PantallaLogin() }
}
```

Esta es una gran ventaja frente a XML: puedes generar **varias previsualizaciones a la vez** (tamaño, tema, idioma, escala de fuente) sin cambiar de archivo ni de dispositivo.

Para previsualizar directamente los distintos `WindowSizeClass` sin tener que calcular `calculateWindowSizeClass` en el preview (que necesita una Activity real), puedes previsualizar los composables de layout (`BandejaCompacta`, `BandejaExpandida`) por separado, pasándoles datos de ejemplo:

```kotlin
@Preview(name = "Compacto", widthDp = 360, heightDp = 780)
@Composable
fun PreviewBandejaCompacta() {
    MiAppTheme {
        BandejaCompacta(
            mensajes = mensajesDeEjemplo,
            mensajeSeleccionado = null,
            onMensajeClick = {},
            onVolver = {}
        )
    }
}

@Preview(name = "Expandido", widthDp = 1000, heightDp = 780)
@Composable
fun PreviewBandejaExpandida() {
    MiAppTheme {
        BandejaExpandida(
            mensajes = mensajesDeEjemplo,
            mensajeSeleccionado = mensajesDeEjemplo.first(),
            onMensajeClick = {}
        )
    }
}
```

Así compruebas ambos layouts en el editor sin necesidad de un dispositivo físico ni de redimensionar una ventana real.

---

## 12. Estado: state hoisting y recomposición

En XML, el estado vivía en la Activity/Fragment y se empujaba a las vistas con `setText`, `setVisibility`, etc. En Compose, **la UI es una función del estado**: cuando el estado cambia, Compose recompone automáticamente lo que depende de él.

### Patrón recomendado: state hoisting

El composable "tonto" no guarda su propio estado; lo recibe y notifica cambios hacia arriba.

```kotlin
@Composable
fun CampoEmail(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.email)) },
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun PantallaLogin(viewModel: LoginViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CampoEmail(
        value = uiState.email,
        onValueChange = viewModel::onEmailChanged
    )
}
```

- `remember { mutableStateOf(...) }`: estado local de un composable (ej. si un desplegable está abierto).
- `ViewModel` + `StateFlow`/`collectAsStateWithLifecycle()`: estado que sobrevive a recomposiciones y cambios de configuración, igual que en XML con ViewModel + LiveData/Flow.
- Evita guardar lógica de negocio dentro del composable; igual que en XML no la metías en la vista.

---

## 13. Organización del proyecto

### Nombres

- Composables en `PascalCase`, como cualquier función: `LoginScreen`, `UserCard`, `PrimaryButton`.
- Archivos: uno por pantalla o grupo de componentes relacionados: `LoginScreen.kt`, `UserCard.kt`.
- Parámetro `modifier: Modifier = Modifier` **siempre presente y siempre el primero opcional**, para que quien use tu composable pueda ajustarlo desde fuera (tamaño, padding, etc.), igual que puedes añadir atributos a una vista personalizada en XML.

```kotlin
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(onClick = onClick, modifier = modifier) {
        Text(text)
    }
}
```

### Recursos: siguen existiendo

- **Strings**: `strings.xml`, accedidos con `stringResource(R.string.login_title)`.
- **Dimensiones**: en Compose se prefieren constantes Kotlin a `dimens.xml`:

```kotlin
object Spacing {
    val screenPadding = 24.dp
    val small = 8.dp
    val medium = 16.dp
    val touchTargetMin = 48.dp
}
```

- **Colores**: del `MaterialTheme.colorScheme`, no de `colors.xml` sueltos.
- **Iconos**: `painterResource()` para drawables XML, o `Icons.Default.*` / `Icons.Filled.*` de Material Icons Extended.

### Previews con datos de ejemplo

Igual que los atributos `tools:text` en XML, usa `@Preview` con datos falsos para comprobar textos largos, vacíos, etc.:

```kotlin
@Preview
@Composable
fun UserCardPreview() {
    MiAppTheme {
        UserCard(nombre = "Nombre de usuario muy largo para probar el desbordamiento")
    }
}
```

---

## 14. Accesibilidad

- **`contentDescription`** en `Image`/`Icon` con significado; `null` si es decorativo (equivalente a `importantForAccessibility="no"`).
- **Área táctil mínima 48dp**: los componentes Material ya la respetan por defecto; ten cuidado si usas `Modifier.size()` para achicarlos.
- **`Modifier.semantics { }`** para casos avanzados: agrupar elementos, dar un rol, describir estado.
- Usa `MaterialTheme.typography` en vez de tamaños de texto fijos, así respetan la configuración de accesibilidad del sistema.
- Prueba con **TalkBack** y con el preview `fontScale = 2f`.
- `Modifier.clearAndSetSemantics { }` para evitar que TalkBack lea dos veces un icono y su texto juntos cuando forman una sola unidad.

---

## 15. Rendimiento

- **`key` en listas** (`items(list, key = { it.id })`): evita recomposiciones y recálculos innecesarios al reordenar o filtrar.
- **Evita lambdas que se recrean en cada recomposición** cuando afectan a composables grandes; usa `remember` para cálculos costosos:

```kotlin
val listaFiltrada = remember(query, items) {
    items.filter { it.nombre.contains(query, ignoreCase = true) }
}
```

- **No leas estado que no necesitas** dentro de un composable grande: si solo un `Text` depende de un valor, que sea ese `Text` (o un composable hijo) el que lo lea, no el padre entero, para limitar qué se recompone.
- Usa el **Layout Inspector** y las métricas de recomposición de Android Studio para detectar recomposiciones excesivas.
- Evita `Modifier` innecesarios o duplicados (como aplicar `.padding()` dos veces cuando uno solo bastaría).

---

## 16. Ejemplo completo: pantalla de login

Mismo caso que en la guía de XML, para comparar directamente.

```kotlin
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onNavigateToRegister: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.screenPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Zona del logo: proporcional a la altura de pantalla
            Spacer(modifier = Modifier.fillMaxHeight(0.1f))

            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = stringResource(R.string.app_logo),
                modifier = Modifier.size(96.dp)
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            Text(
                text = stringResource(R.string.login_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChanged,
                label = { Text(stringResource(R.string.email)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
            )

            Spacer(modifier = Modifier.height(Spacing.small))

            var passwordVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChanged,
                label = { Text(stringResource(R.string.password)) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                            contentDescription = stringResource(
                                if (passwordVisible) R.string.hide_password
                                else R.string.show_password
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            Button(
                onClick = viewModel::onLoginClick,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.login))
                }
            }

            Spacer(modifier = Modifier.height(Spacing.medium))

            TextButton(
                onClick = onNavigateToRegister,
                modifier = Modifier.heightIn(min = Spacing.touchTargetMin)
            ) {
                Text(stringResource(R.string.register_prompt))
            }

            Spacer(modifier = Modifier.height(Spacing.medium))
        }
    }
}
```

Puntos a notar, en paralelo con el ejemplo de XML:

- **`Spacing.screenPadding`** es el equivalente a las guidelines en dp para márgenes laterales.
- **`fillMaxHeight(0.1f)`** como espaciador es el equivalente al guideline en porcentaje para la zona del logo.
- **`widthIn(max = 480.dp)`** limita el ancho en tablets, igual que `layout_constraintWidth_max`.
- Todo dentro de `Modifier.verticalScroll()` + `imePadding()` para que el teclado no tape los campos.
- El estado (`email`, `password`, `isLoading`) vive en el `ViewModel`, no en la pantalla.

---

## 17. Errores comunes

| Error | Consecuencia | Solución |
|---|---|---|
| Mezclar `LazyColumn` dentro de `Modifier.verticalScroll()` | Fallos de medición, scroll roto o "infinite height" | Usar solo uno de los dos sistemas de scroll |
| Olvidar `key` en `items()` | Recomposiciones y animaciones incorrectas al reordenar/filtrar | `items(list, key = { it.id })` |
| Guardar estado de negocio con `remember` dentro del composable | Se pierde en cambios de configuración o navegación | Subirlo a un `ViewModel` |
| Hardcodear colores con `Color(0xFF...)` | No respeta tema claro/oscuro ni Material You | Usar `MaterialTheme.colorScheme.*` |
| No pasar `modifier: Modifier = Modifier` en composables reutilizables | El composable no se puede ajustar desde fuera | Siempre incluirlo y aplicarlo |
| Tamaños de texto fijos en vez de `MaterialTheme.typography` | No escala con accesibilidad | Usar los estilos del tema |
| No gestionar insets (edge-to-edge) | Contenido tapado por barras de sistema | `Modifier.windowInsetsPadding(...)`, `enableEdgeToEdge()` |
| Lógica pesada ejecutada en cada recomposición sin `remember` | Tirones y mal rendimiento | `remember(key1, key2) { cálculo }` |
| Usar `ConstraintLayout` para todo "por costumbre de XML" | Código más verboso de lo necesario | Preferir `Row`/`Column`/`Box` salvo casos complejos |
| No probar con fuente grande, tablet ni modo oscuro | Errores visuales en producción | Varias anotaciones `@Preview` |

---

## 18. Checklist final

- [ ] ¿El layout usa `Row`/`Column`/`Box` como primera opción, y `ConstraintLayout` solo si hace falta?
- [ ] ¿Los tamaños usan `fillMaxWidth`/`weight`/medidas fijas de forma consistente con su propósito (proporcional vs fijo)?
- [ ] ¿Hay `widthIn(max = ...)` para limitar el ancho en pantallas grandes?
- [ ] ¿El estado vive en un `ViewModel` y se sube con state hoisting (no `remember` para datos de negocio)?
- [ ] ¿Colores y tipografías vienen de `MaterialTheme`, no hardcodeados?
- [ ] ¿Strings en `strings.xml` y constantes de espaciado centralizadas (no valores mágicos)?
- [ ] ¿`LazyColumn`/`LazyRow` con `key` estable en vez de iterar manualmente o mezclar con scroll normal?
- [ ] ¿Insets gestionados (`windowInsetsPadding`, `imePadding`) y `enableEdgeToEdge()` llamado?
- [ ] ¿`contentDescription` en imágenes/iconos con significado, `null` en los decorativos?
- [ ] ¿Probado con varias `@Preview` (tablet, fuente grande, modo oscuro, textos largos)?
- [ ] ¿`modifier: Modifier = Modifier` presente en los composables reutilizables?
- [ ] ¿Sin recomposiciones innecesarias (uso correcto de `remember`, `key`, lectura de estado acotada)?

---

## Resumen rápido

1. **`Row`/`Column`/`Box` por defecto**; `ConstraintLayout` para Compose solo en relaciones complejas.
2. **Todo pasa por `Modifier`**: tamaño, posición, padding, clics — y el orden de los modifiers importa.
3. **`fillMaxWidth()`/`weight()`** para proporciones; **dp fijo** para márgenes y tamaños táctiles consistentes.
4. **Material 3 nativo**: `MaterialTheme`, color dinámico, componentes `material3.*`.
5. **`LazyColumn` con `key`** para listas; nunca mezclado con scroll manual.
6. **Estado arriba (ViewModel), UI abajo (composables sin memoria propia de negocio)**.
7. **Window Size Classes** en vez de carpetas `layout-sw600dp` para adaptar a tablets y plegables.
8. **Múltiples `@Preview`** para probar tamaños, temas y accesibilidad sin tocar un dispositivo.
