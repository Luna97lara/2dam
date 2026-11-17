# Guía práctica: MVVM vs MVI en Android (y qué recomienda Google realmente)

Esta guía aclara un punto que genera mucha confusión: **Google no recomienda "MVVM" ni "MVI" como tal**. Recomienda un conjunto de *principios* (flujo de datos unidireccional, estado inmutable, fuente única de verdad) que en la práctica casi todo el mundo implementa con `ViewModel` + `StateFlow`, en un estilo que queda a medio camino entre ambos patrones clásicos. Aquí se explica de dónde viene cada patrón, qué cambia entre ellos, qué dice la documentación oficial a día de hoy, y cuándo tiene sentido acercarse más a uno u otro.

---

## Índice

1. [Antes de nada: qué dice Google exactamente](#1-antes-de-nada-qué-dice-google-exactamente)
2. [MVVM clásico: de dónde viene y cómo es](#2-mvvm-clásico-de-dónde-viene-y-cómo-es)
3. [MVI: de dónde viene y cómo es](#3-mvi-de-dónde-viene-y-cómo-es)
4. [Comparación directa, punto por punto](#4-comparación-directa-punto-por-punto)
5. [El "UDF con ViewModel" que usa Google en la práctica](#5-el-udf-con-viewmodel-que-usa-google-en-la-práctica)
6. [Ejemplo: la misma pantalla en MVVM "suelto", MVI estricto, y UDF-Google](#6-ejemplo-la-misma-pantalla-en-mvvm-suelto-mvi-estricto-y-udf-google)
7. [¿Cuándo acercarte más a MVI estricto?](#7-cuándo-acercarte-más-a-mvi-estricto)
8. [¿Cuándo MVVM "suelto" es perfectamente razonable?](#8-cuándo-mvvm-suelto-es-perfectamente-razonable)
9. [Errores y confusiones habituales](#9-errores-y-confusiones-habituales)
10. [Checklist de decisión](#10-checklist-de-decisión)
11. [Resumen rápido](#11-resumen-rápido)

---

## 1. Antes de nada: qué dice Google exactamente

La [guía oficial de arquitectura de apps](https://developer.android.com/topic/architecture) **no menciona "MVVM" ni "MVI" por su nombre en ningún momento**. Lo que sí establece, explícitamente, son principios:

- **Separación de responsabilidades**: no poner lógica ni datos de la app en clases `Activity`/`Fragment`, porque el sistema operativo puede destruirlas y recrearlas en cualquier momento.
- **La UI se dirige desde modelos de datos**, preferiblemente persistentes.
- **Fuente única de verdad (Single Source of Truth, SSOT)**: solo el propietario de un dato lo modifica, y lo expone como un tipo inmutable.
- **Flujo de datos unidireccional (UDF)** en todas las capas: "el estado fluye en una sola dirección, típicamente de componentes padre a componentes hijo. Los eventos que modifican los datos fluyen en la dirección opuesta."
- **Inyección de dependencias**, recomendando Hilt.
- **Testabilidad y seguridad de hilo principal**.
- **Diseño adaptable**: layouts reutilizables, estado de UI que sobrevive a cambios de configuración.

Y sobre el "contenedor de estado" (lo que casi siempre es un `ViewModel`):

> Los *state holders* como `ViewModel` **"guardan datos, los exponen a la UI y gestionan la lógica"**, y **"deben vivir el mismo tiempo que el elemento de UI para el que proveen estado"** — por ejemplo, el `ViewModel` de una pantalla debe sobrevivir mientras esa pantalla siga en la pila de navegación.

En resumen: **Google define el "qué" (los principios de UDF) pero no el "cómo" exacto (ni MVVM ni MVI con nombre y forma fija)**. Lo que se ve en el código de ejemplo oficial y en apps de referencia como *Now in Android* es, en la práctica, **un híbrido**: la estructura general de MVVM (una clase `ViewModel` que expone estado a la View) pero con las reglas de disciplina de MVI (estado inmutable, un único flujo de estado, eventos como objetos explícitos) — lo que mucha gente en la comunidad llama informalmente **"MVI-flavored MVVM"**.

Con esa base, entendamos los dos patrones "puros" para poder situar ese híbrido con criterio.

---

## 2. MVVM clásico: de dónde viene y cómo es

**Model-View-ViewModel** nació en el mundo de XAML/WPF (Microsoft) y se adoptó en Android porque encajaba bien con `LiveData`/`ViewModel` del framework.

```
View  ←─ observa ──  ViewModel  ──  usa  →  Model
  │                                            
  └──────── llama a métodos/funciones ─────────┘
```

### Características del MVVM "de libro"

- La **View** (Activity/Fragment, o en Compose, el composable) observa **varias propiedades sueltas** expuestas por el ViewModel: `nombre: LiveData<String>`, `isLoading: LiveData<Boolean>`, `error: LiveData<String?>`...
- La View llama directamente a **métodos públicos** del ViewModel para cada acción: `viewModel.onGuardarClick()`, `viewModel.onNombreChanged(texto)`.
- No hay un contrato formal de "todos los estados posibles de la pantalla"; cada propiedad se actualiza de forma independiente.
- El **binding** (en su origen XAML, en Android con `LiveData`+`DataBinding`) es lo que conecta ViewModel y View de forma más o menos automática.

### El problema que suele aparecer

Con muchas propiedades sueltas (`isLoading`, `error`, `datos`, `isEmpty`...) es fácil llegar a **combinaciones de estado inconsistentes o imposibles**: ¿qué pasa si `isLoading = true` y `error != null` a la vez? Nada te impide que ocurra, porque cada propiedad vive y se actualiza por separado.

```kotlin
// MVVM "suelto": varias propiedades independientes
class PerfilViewModel : ViewModel() {
    val nombre = MutableLiveData("")
    val isLoading = MutableLiveData(false)
    val error = MutableLiveData<String?>(null)

    fun cargar() {
        isLoading.value = true
        // si algo falla a mitad, error e isLoading pueden quedar desincronizados
    }
}
```

---

## 3. MVI: de dónde viene y cómo es

**Model-View-Intent** viene del mundo de la programación reactiva/funcional (inspirado en Redux/Elm), y llegó a Android de la mano de librerías como MVIKotlin, Orbit o implementaciones propias sobre Flow.

```
         ┌────────────────────────────┐
         │                            │
         ▼                            │
      Model (un único State)          │
         │                            │
         ▼                            │
       View  ── emite Intent/Action ──┘
```

### Características del MVI estricto

- **Un único objeto de estado inmutable** representa *toda* la pantalla en un momento dado (`data class PerfilState(...)`), nunca propiedades sueltas.
- Las acciones del usuario se modelan como **`Intent`/`Action` explícitos** (no confundir con los `Intent` de Android; aquí es terminología del patrón), normalmente una `sealed class`/`sealed interface`.
- Hay una **única función reductora** (`reduce`) que, dado el estado actual + un intent, produce el **siguiente estado completo** — nunca se muta el estado a trozos.
- El ciclo es estrictamente circular: **Intent → Model (nuevo State) → View (se redibuja con el State completo) → el usuario genera un nuevo Intent**.

```kotlin
// MVI estricto: un único estado, intents explícitos, reductor puro
data class PerfilState(
    val nombre: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface PerfilIntent {
    object Cargar : PerfilIntent
    data class NombreCambiado(val valor: String) : PerfilIntent
}

class PerfilViewModel : ViewModel() {
    private val _state = MutableStateFlow(PerfilState())
    val state: StateFlow<PerfilState> = _state.asStateFlow()

    fun procesar(intent: PerfilIntent) {
        _state.update { estadoActual -> reduce(estadoActual, intent) }
    }

    private fun reduce(estado: PerfilState, intent: PerfilIntent): PerfilState = when (intent) {
        PerfilIntent.Cargar -> estado.copy(isLoading = true, error = null)
        is PerfilIntent.NombreCambiado -> estado.copy(nombre = intent.valor)
    }
}
```

### La ventaja que resuelve

Como el estado es **un único objeto inmutable**, es **imposible** tener una combinación inconsistente que no hayas contemplado tú mismo en el `reduce`: en cualquier instante, la pantalla entera se deriva de un solo `PerfilState`, no de la combinación accidental de varias propiedades que se actualizan en momentos distintos.

### El coste que tiene

- Más ceremonia: hay que declarar una clase de estado, una clase (o varias) de intents, y una función reductora, incluso para pantallas sencillas.
- Cada cambio pequeño de estado obliga a pasar por el `copy()` del estado completo, lo cual es más verboso que `isLoading.value = true` suelto.
- La curva de aprendizaje es mayor para quien se incorpora al equipo.

---

## 4. Comparación directa, punto por punto

| | MVVM clásico | MVI estricto |
|---|---|---|
| Estado expuesto | Varias propiedades sueltas (`LiveData`/`StateFlow` independientes) | Un único objeto de estado inmutable |
| Acciones del usuario | Llamadas directas a métodos del ViewModel | Objetos `Intent`/`Action` explícitos, procesados por una función reductora |
| Consistencia del estado | No garantizada: estados "imposibles" son posibles por accidente | Garantizada por construcción: solo existen los estados que el `reduce` permite |
| Verbosidad | Baja: menos boilerplate para pantallas simples | Alta: clases de estado, intents y reductor incluso para lo sencillo |
| Depuración/Debugging | Más difícil rastrear "cómo llegó la pantalla a este estado" si hay muchas propiedades cruzadas | Más fácil: cada estado es resultado de una secuencia clara de intents (se puede hasta loguear/reproducir la secuencia) |
| Testing | Testear cada propiedad/método por separado | Testear el `reduce` como función pura: mismo input → mismo output, muy fácil de testear exhaustivamente |
| Curva de aprendizaje | Más suave, más intuitivo para quien viene de MVC/MVP | Más empinada, requiere pensar en términos de estado inmutable y reductores |
| Encaje con Compose | Bueno, pero puede tentar a usar varios `remember`/`mutableStateOf` sueltos que repiten el problema | Muy bueno: un único `State` encaja de forma natural con `collectAsStateWithLifecycle()` y una recomposición predecible |

---

## 5. El "UDF con ViewModel" que usa Google en la práctica

Mirando el código de ejemplo de la documentación oficial y de apps de referencia (como *Now in Android*, el proyecto open-source que Google mantiene como muestra de buenas prácticas), el patrón real que se usa **toma lo mejor de los dos**:

- **De MVI**: un único objeto de estado inmutable (`data class XUiState`) expuesto como `StateFlow`, en vez de propiedades sueltas.
- **De MVVM**: no hay un tipo formal `Intent`/`Action` ni una función `reduce` centralizada obligatoria; las acciones del usuario suelen ser **funciones públicas normales del ViewModel** (`fun onGuardarClick()`), aunque algunos equipos sí añaden una `sealed interface` de eventos cuando la pantalla tiene muchas acciones (como se mostró en la guía de arquitectura de pantallas, sección de eventos).

```kotlin
// El híbrido que recomienda la práctica real de Google (y el que ya usamos
// en las guías anteriores de este chat)

data class PerfilUiState(
    val nombre: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class PerfilViewModel(
    private val repositorio: UsuarioRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    fun onNombreChanged(nuevo: String) {
        _uiState.update { it.copy(nombre = nuevo) }
    }

    fun onGuardarClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val exito = repositorio.guardar(_uiState.value.nombre)
            _uiState.update {
                it.copy(isLoading = false, error = if (exito) null else "Error al guardar")
            }
        }
    }
}
```

Esto **no es MVVM puro** (porque el estado es un único objeto inmutable, no propiedades sueltas) **ni es MVI puro** (porque no hay un tipo `Intent` formal ni una función `reduce` separada: la "reducción" ocurre directamente dentro de cada función del ViewModel, con `_uiState.update { ... }`).

> Es exactamente el patrón que ya se usó en la guía de arquitectura de pantallas de este chat (`HomeUiState`, `PerfilUiState`, `onEvent`/funciones sueltas + canal de eventos de una sola vez). No fue casualidad: es el estilo que de facto recomienda Google a través de sus ejemplos, aunque la documentación nunca lo llame "MVVM" ni "MVI" con ese nombre.

---

## 6. Ejemplo: la misma pantalla en MVVM "suelto", MVI estricto, y UDF-Google

Para verlo en paralelo, una pantalla de login con: nombre, cargando, error.

### MVVM "suelto" (propiedades independientes)

```kotlin
class LoginViewModel : ViewModel() {
    val email = MutableStateFlow("")
    val isLoading = MutableStateFlow(false)
    val error = MutableStateFlow<String?>(null)

    fun onEmailChanged(valor: String) {
        email.value = valor
    }

    fun onLoginClick() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            val ok = autenticar(email.value)
            isLoading.value = false
            if (!ok) error.value = "Credenciales incorrectas"
        }
    }
}
```

En la UI, hay que combinar tres flujos distintos (con `combine` o leyendo tres `StateFlow` por separado), y nada impide, por ejemplo, que un bug deje `isLoading = true` junto con un `error` ya establecido de una llamada anterior que no se limpió a tiempo.

### MVI estricto (estado único + intents + reductor separado)

```kotlin
data class LoginState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface LoginIntent {
    data class EmailCambiado(val valor: String) : LoginIntent
    object LoginClick : LoginIntent
}

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun procesar(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailCambiado ->
                _state.update { reduce(it, intent) }
            LoginIntent.LoginClick ->
                viewModelScope.launch { ejecutarLogin(intent) }
        }
    }

    private fun reduce(estado: LoginState, intent: LoginIntent): LoginState = when (intent) {
        is LoginIntent.EmailCambiado -> estado.copy(email = intent.valor)
        LoginIntent.LoginClick -> estado // el login es async, se gestiona aparte
    }

    private suspend fun ejecutarLogin(intent: LoginIntent.LoginClick) {
        _state.update { it.copy(isLoading = true, error = null) }
        val ok = autenticar(_state.value.email)
        _state.update { it.copy(isLoading = false, error = if (ok) null else "Credenciales incorrectas") }
    }
}
```

### UDF-Google (el híbrido pragmático — el recomendado para la mayoría de apps)

```kotlin
data class LoginUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(valor: String) {
        _uiState.update { it.copy(email = valor) }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val ok = autenticar(_uiState.value.email)
            _uiState.update { it.copy(isLoading = false, error = if (ok) null else "Credenciales incorrectas") }
        }
    }
}
```

> Observa que el tercero tiene **la misma garantía de consistencia** que el MVI estricto (un único `LoginUiState` inmutable, imposible tener combinaciones no contempladas), pero con **la misma simplicidad de llamada directa a funciones** que el MVVM clásico (`onEmailChanged`, `onLoginClick`), sin necesidad de una clase `Intent` ni de una función `reduce` separada. Por eso es el que de hecho predomina en el código Android actual.

---

## 7. ¿Cuándo acercarte más a MVI estricto?

Vale la pena añadir la ceremonia completa de MVI (intents formales + reductor separado) cuando:

- **La pantalla tiene muchísimas acciones posibles** y la lista de funciones sueltas del ViewModel empieza a ser difícil de seguir (10-15+ acciones distintas).
- **Necesitas depurar/reproducir secuencias de estado con precisión** (por ejemplo, para un bug intermitente, loguear cada `Intent` recibido te da un historial exacto de "qué pidió el usuario, en qué orden").
- **Quieres testear la lógica de transición de estado de forma exhaustiva** como función pura, independiente de corrutinas o del ciclo de vida del ViewModel — el `reduce` se testea con pares (estado, intent) → estado esperado, sin mocks.
- **El equipo ya tiene convención/librería para ello** (Orbit MVI, MVIKotlin, o un patrón interno ya establecido) y quiere coherencia entre pantallas.
- **Flujos complejos con múltiples fuentes de eventos asíncronos** entrelazados (sockets, sensores, múltiples streams de datos) donde centralizar todo en un único reductor evita condiciones de carrera entre actualizaciones parciales.

---

## 8. ¿Cuándo MVVM "suelto" es perfectamente razonable?

A pesar de todo lo anterior, no hace falta forzar siempre un único estado:

- **Pantallas muy simples** con una o dos piezas de estado totalmente independientes entre sí (ej. un interruptor de "modo oscuro" en ajustes, que no interactúa con nada más de esa pantalla).
- **Prototipos rápidos** donde la velocidad de escribir código importa más que la robustez a largo plazo.
- Cuando el **equipo no tiene experiencia previa** con patrones de estado único y el coste de aprendizaje superaría el beneficio para el tamaño real de la app.

> En cualquier caso, **la recomendación general y lo que verás en el código oficial de Google es el híbrido de la sección 5**: casi nunca merece la pena el MVVM con propiedades totalmente sueltas para una pantalla con más de 2-3 piezas de estado relacionadas entre sí, porque el coste de agruparlas en un único `data class UiState` es mínimo y la ganancia en consistencia es inmediata.

---

## 9. Errores y confusiones habituales

| Confusión | Aclaración |
|---|---|
| "Google recomienda MVVM" | Google recomienda **principios de UDF**, y usa `ViewModel` como contenedor de estado — pero no prescribe el patrón MVVM con propiedades sueltas |
| "Google recomienda MVI" | Tampoco: no hay mención oficial a "MVI", ni exige intents formales ni una función `reduce` separada |
| "MVI es simplemente usar `sealed class` para los eventos" | Eso es solo una pieza; MVI estricto exige además **un único estado** y **una función reductora pura** — una `sealed class` de eventos por sí sola no es "hacer MVI", es una práctica que cualquier patrón puede adoptar |
| "Si mi ViewModel tiene un `data class UiState`, ya estoy haciendo MVI" | No necesariamente: si las acciones se gestionan con funciones sueltas (`onXClick()`) en vez de un `Intent` procesado por un `reduce` central, es el híbrido UDF, no MVI estricto — y eso está bien, es justo lo recomendado |
| "MVVM y MVI son excluyentes, hay que elegir uno y no mezclarlos nunca" | En la práctica real de Android, casi todo el código bueno es un híbrido; pelearse por la etiqueta exacta importa menos que cumplir los principios de UDF (estado inmutable, SSOT, flujo unidireccional) |
| Confundir el `Intent` de MVI con `android.content.Intent` | Son conceptos completamente distintos que comparten nombre por casualidad histórica; en MVI, "Intent" significa "intención/acción del usuario", no el objeto de Android para lanzar Activities |

---

## 10. Checklist de decisión

Para decidir cuánta ceremonia de MVI añadir a una pantalla concreta:

- [ ] ¿La pantalla tiene **3 o más piezas de estado relacionadas**? → Agrúpalas en un único `data class UiState` (ya estás en el híbrido UDF, mínimo recomendado).
- [ ] ¿Hay **más de 10 acciones posibles** o la lista de funciones del ViewModel ya es difícil de seguir? → Considera agrupar las acciones en una `sealed interface` de eventos.
- [ ] ¿Necesitas **reproducir/depurar secuencias de estado exactas** (bugs intermitentes, tests exhaustivos de transiciones)? → Añade una función `reduce` separada y testeable de forma aislada.
- [ ] ¿El equipo ya usa una **librería o convención MVI** en otras partes del proyecto? → Sé consistente con lo ya establecido, aunque esta pantalla en concreto sea simple.
- [ ] ¿Es una **pantalla muy simple** (1-2 piezas de estado, pocas acciones)? → No fuerces MVI completo; el híbrido ligero (o incluso un par de `remember` locales si no hay lógica de negocio real) es suficiente.
- [ ] En cualquier caso: ¿**el estado expuesto es inmutable y único** (no propiedades sueltas descoordinadas)? → Esto, sí o sí, cúmplelo siempre — es el principio de UDF que Google exige independientemente del patrón.

---

## 11. Resumen rápido

1. **Google no prescribe "MVVM" ni "MVI" por nombre.** Exige principios: flujo de datos unidireccional (UDF), fuente única de verdad, estado inmutable, `ViewModel` como contenedor de estado con el ciclo de vida de la pantalla.
2. **MVVM clásico**: varias propiedades de estado sueltas, llamadas directas a métodos — simple pero permite estados inconsistentes.
3. **MVI estricto**: un único estado inmutable, intents explícitos, función reductora pura — robusto pero con más ceremonia.
4. **Lo que de hecho se usa en la práctica (y en el código de referencia de Google, como *Now in Android*) es un híbrido**: un único `data class UiState` (de MVI) expuesto vía `StateFlow`, pero con funciones sueltas del ViewModel en vez de un `Intent`/`reduce` formal (de MVVM). Es el patrón que ya hemos usado en las guías anteriores de este chat.
5. **Añade la ceremonia completa de MVI** (intents + reductor) solo cuando la complejidad real de la pantalla lo justifique: muchas acciones, necesidad de depurar secuencias exactas, o convención ya establecida en el equipo.
6. **No fuerces MVI en pantallas triviales**: el coste de aprendizaje y verbosidad no se justifica si apenas hay 1-2 piezas de estado independientes.
7. **La única regla que no es negociable, venga del patrón que venga**: el estado que ve la UI debe ser **inmutable y tener una única fuente de verdad** — eso es lo que Google exige en su guía oficial, independientemente de cómo llames al patrón.

---

Sources: [Guide to app architecture](https://developer.android.com/topic/architecture)
