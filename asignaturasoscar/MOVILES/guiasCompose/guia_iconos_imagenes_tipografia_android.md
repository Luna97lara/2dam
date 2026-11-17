# Guía práctica: iconos, imágenes y tipografía en Android (XML y Compose)

Guía comparada: cómo gestionar bien **iconos**, **imágenes** y **tipos de letra** en una app Android, mostrando lado a lado el enfoque en **vistas XML** y en **Jetpack Compose**.

---

## Índice

1. [Iconos: tipos y cuándo usar cada uno](#1-iconos-tipos-y-cuándo-usar-cada-uno)
2. [Vector Drawables: el formato recomendado para iconos](#2-vector-drawables-el-formato-recomendado-para-iconos)
3. [Iconos en XML](#3-iconos-en-xml)
4. [Iconos en Compose](#4-iconos-en-compose)
5. [Tintado de iconos (tint)](#5-tintado-de-iconos-tint)
6. [Imágenes: formatos y dónde colocarlas](#6-imágenes-formatos-y-dónde-colocarlas)
7. [Densidades y carpetas drawable/mipmap](#7-densidades-y-carpetas-drawablemipmap)
8. [Imágenes en XML](#8-imágenes-en-xml)
9. [Imágenes en Compose](#9-imágenes-en-compose)
10. [Carga de imágenes remotas (red)](#10-carga-de-imágenes-remotas-red)
11. [scaleType / ContentScale: que no se deforme la imagen](#11-scaletype--contentscale-que-no-se-deforme-la-imagen)
12. [Tipografía: fuentes del sistema vs fuentes personalizadas](#12-tipografía-fuentes-del-sistema-vs-fuentes-personalizadas)
13. [Añadir una fuente personalizada al proyecto](#13-añadir-una-fuente-personalizada-al-proyecto)
14. [Tipografía en XML](#14-tipografía-en-xml)
15. [Tipografía en Compose](#15-tipografía-en-compose)
16. [Unidades de texto: sp, no dp](#16-unidades-de-texto-sp-no-dp)
17. [Accesibilidad de iconos, imágenes y texto](#17-accesibilidad-de-iconos-imágenes-y-texto)
18. [Rendimiento y buenas prácticas](#18-rendimiento-y-buenas-prácticas)
19. [Ejemplo completo: tarjeta con icono, imagen y texto](#19-ejemplo-completo-tarjeta-con-icono-imagen-y-texto)
20. [Errores comunes](#20-errores-comunes)
21. [Checklist final](#21-checklist-final)

---

## 1. Iconos: tipos y cuándo usar cada uno

| Tipo de icono | Uso típico | Formato recomendado |
|---|---|---|
| Icono de la app (launcher) | El icono que aparece en el cajón de apps | Adaptive Icon (`mipmap`), ver sección 7 |
| Icono de acción / UI (favorito, volver, menú) | Botones, barras, listas | **Vector Drawable** (XML) |
| Icono de notificación | Barra de estado | Vector Drawable monocromo, simple |
| Icono de marca / ilustración compleja | Logos con detalle, degradados | PNG/WebP, o SVG importado como Vector si es simple |

> **Regla general: si el icono es un trazo simple (una sola forma, sin foto ni degradados complejos), usa Vector Drawable.** Es el equivalente a un SVG dentro de Android, y es la base tanto para XML como para Compose.

---

## 2. Vector Drawables: el formato recomendado para iconos

Un Vector Drawable es un archivo XML que describe el icono como rutas vectoriales (paths), no como píxeles. Ventajas:

- **Escala sin pérdida de calidad** a cualquier densidad de pantalla (no hace falta exportar @1x/@2x/@3x como con PNG).
- **Un solo archivo** para todas las densidades, en vez de 5 carpetas `drawable-mdpi`, `-hdpi`, `-xhdpi`, etc.
- Ocupa **mucho menos espacio** en el APK que varios PNG.
- Se puede **tintar** fácilmente (ver sección 5), tanto en XML como en Compose.

### Cómo conseguir un Vector Drawable

1. **Material Symbols** (iconos oficiales de Google, antes "Material Icons"): descárgalos directamente en formato Android desde `fonts.google.com/icons`.
2. **Android Studio → Asset Studio**: clic derecho en `res` → `New` → `Vector Asset`. Permite elegir un icono del catálogo de Material o importar un SVG propio.
3. **Importar un SVG propio**: Asset Studio lo convierte automáticamente a Vector Drawable (`.xml`).

### Anatomía de un Vector Drawable

```xml
<!-- res/drawable/ic_favorite.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?attr/colorControlNormal">
    <path
        android:fillColor="@android:color/white"
        android:pathData="M12,21.35l-1.45,-1.32C5.4,15.36 2,12.28 2,8.5 2,5.42..." />
</vector>
```

- `width`/`height`: tamaño "natural" del icono (normalmente 24dp para iconos de UI, siguiendo Material Design).
- `viewportWidth`/`viewportHeight`: sistema de coordenadas interno del path (no tiene por qué coincidir con el tamaño final).
- `tint`: color por defecto; se puede sobrescribir en tiempo de uso (ver sección 5).
- `fillColor` en el `path`: si pones aquí un color fijo y luego aplicas `tint`, el tint prevalece.

---

## 3. Iconos en XML

### `ImageView` con un Vector Drawable

```xml
<ImageView
    android:id="@+id/ivFavorito"
    android:layout_width="24dp"
    android:layout_height="24dp"
    android:src="@drawable/ic_favorite"
    android:contentDescription="@string/add_to_favorites"
    app:tint="?attr/colorPrimary" />
```

- `android:src`: la fuente del icono.
- `app:tint` (de AppCompat/Core): aplica un tinte en tiempo de layout. Es preferible a `android:tint` por compatibilidad hacia atrás.
- **Siempre `contentDescription`**, salvo que el icono sea puramente decorativo (entonces `android:importantForAccessibility="no"`).

### Icono dentro de un botón (`MaterialButton` con icono)

```xml
<com.google.android.material.button.MaterialButton
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/save"
    app:icon="@drawable/ic_save"
    app:iconTint="?attr/colorOnPrimary"
    app:iconGravity="textStart" />
```

### `IconButton`-like con `ImageButton`

```xml
<ImageButton
    android:layout_width="48dp"
    android:layout_height="48dp"
    android:src="@drawable/ic_menu"
    android:contentDescription="@string/open_menu"
    android:background="?attr/selectableItemBackgroundBorderless"
    app:tint="?attr/colorOnSurface" />
```

> `?attr/selectableItemBackgroundBorderless` añade el efecto ripple circular típico de Material sin un fondo cuadrado, igual que hace `IconButton` en Compose automáticamente.

---

## 4. Iconos en Compose

### `Icon` con Material Icons

Compose trae una librería de iconos lista para usar (`androidx.compose.material:material-icons-extended` para el catálogo completo).

```kotlin
Icon(
    imageVector = Icons.Default.Favorite,
    contentDescription = stringResource(R.string.add_to_favorites),
    tint = MaterialTheme.colorScheme.primary,
    modifier = Modifier.size(24.dp)
)
```

- `imageVector`: un `ImageVector` (el equivalente en memoria a un Vector Drawable). `Icons.Default.*`, `Icons.Filled.*`, `Icons.Outlined.*`, `Icons.Rounded.*` son familias de estilo.
- `tint`: color del icono; por defecto hereda `LocalContentColor`.
- Si el icono es decorativo: `contentDescription = null`.

### Icono propio (Vector Drawable del proyecto) en Compose

```kotlin
Icon(
    painter = painterResource(R.drawable.ic_save),
    contentDescription = stringResource(R.string.save),
    tint = MaterialTheme.colorScheme.onPrimary
)
```

> Diferencia clave: `imageVector` es para iconos vectoriales "nativos" de Compose (los de Material o los tuyos convertidos a `ImageVector`); `painterResource` carga cualquier recurso `drawable` (vector o no) del proyecto, igual que haría un `ImageView`.

### Icono dentro de un botón

```kotlin
Button(onClick = { }) {
    Icon(
        imageVector = Icons.Default.Save,
        contentDescription = null, // el texto del botón ya describe la acción
        modifier = Modifier.size(18.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(stringResource(R.string.save))
}
```

### `IconButton` (icono solo, con ripple y área táctil correctas)

```kotlin
IconButton(onClick = { /* abrir menú */ }) {
    Icon(
        imageVector = Icons.Default.Menu,
        contentDescription = stringResource(R.string.open_menu)
    )
}
```

`IconButton` ya aplica los 48dp de área táctil mínima y el ripple circular, equivalente al `ImageButton` + `selectableItemBackgroundBorderless` de XML.

---

## 5. Tintado de iconos (tint)

| Objetivo | XML | Compose |
|---|---|---|
| Tinte fijo | `app:tint="@color/mi_color"` | `tint = Color(0xFF...)` |
| Tinte del tema | `app:tint="?attr/colorPrimary"` | `tint = MaterialTheme.colorScheme.primary` |
| Sin tinte (colores propios del icono, ej. un logo multicolor) | Quitar `tint`, o `app:tint="@null"` | `tint = Color.Unspecified` |
| Tinte distinto según estado (seleccionado/no) | `ColorStateList` en XML (`res/color/icon_tint_selector.xml`) | `if (seleccionado) colorA else colorB` directamente en Kotlin |

```xml
<!-- res/color/icon_tint_selector.xml -->
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:color="?attr/colorPrimary" android:state_selected="true" />
    <item android:color="?attr/colorOnSurfaceVariant" />
</selector>
```

```kotlin
// Equivalente en Compose: condicional directo, sin archivo aparte
val tint = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant
Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = tint)
```

> En Compose no existen los `ColorStateList`: el "estado" se resuelve con lógica Kotlin normal (if/when) porque la recomposición ya se encarga de repintar cuando cambia el estado.

---

## 6. Imágenes: formatos y dónde colocarlas

| Formato | Cuándo usarlo |
|---|---|
| **WebP** (recomendado) | Fotos e imágenes complejas: mejor compresión que PNG/JPG a igual calidad |
| **PNG** | Imágenes con transparencia que no son fotográficas (capturas, ilustraciones planas) |
| **JPG** | Fotos sin transparencia, cuando el peso importa más que una compresión perfecta |
| **Vector Drawable (XML)** | Ilustraciones simples, iconos, logos de una sola forma |
| **SVG externo** | No se usa directamente en Android; se convierte a Vector Drawable al importarlo |

Android Studio puede convertir automáticamente PNG/JPG a WebP al compilar (`Convert to WebP` al hacer clic derecho en la imagen), sin tener que cambiar el código.

---

## 7. Densidades y carpetas drawable/mipmap

Si usas imágenes rasterizadas (PNG/JPG/WebP, no vectores), Android necesita una versión por densidad de pantalla para que se vea nítida sin reescalar en tiempo real:

```
res/
  drawable-mdpi/    → 1x   (baseline)
  drawable-hdpi/    → 1.5x
  drawable-xhdpi/   → 2x
  drawable-xxhdpi/  → 3x
  drawable-xxxhdpi/ → 4x
```

- Si solo tienes una resolución, colócala en `drawable-xxhdpi` (la más común hoy) y Android la escalará hacia abajo en pantallas de menor densidad; escalar hacia arriba desde una densidad baja se ve peor que escalar hacia abajo desde una alta.
- **Mejor aún: evita este problema usando Vector Drawable siempre que el contenido lo permita** (sección 2), así no hace falta exportar 5 tamaños.
- `mipmap/` es solo para el **icono de la app** (el launcher), no para imágenes de la interfaz; Android a veces usa una densidad superior a la del dispositivo para el icono si se ve en pantallas grandes (como en el selector de apps reciente).

### Adaptive Icon (icono de la app)

```xml
<!-- res/mipmap-anydpi-v26/ic_launcher.xml -->
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_monochrome" />
</adaptive-icon>
```

El `monochrome` es el que usa Android 13+ para los iconos temáticos (Material You), que se colorean según el fondo del usuario.

---

## 8. Imágenes en XML

### `ImageView` básico

```xml
<ImageView
    android:id="@+id/ivPortada"
    android:layout_width="match_parent"
    android:layout_height="200dp"
    android:src="@drawable/foto_portada"
    android:contentDescription="@string/cover_photo_description"
    android:scaleType="centerCrop" />
```

### Dentro de ConstraintLayout, con proporción

```xml
<ImageView
    android:id="@+id/ivBanner"
    android:layout_width="0dp"
    android:layout_height="0dp"
    android:contentDescription="@string/banner_description"
    android:scaleType="centerCrop"
    android:src="@drawable/banner"
    app:layout_constraintDimensionRatio="16:9"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
```

### Imagen decorativa (sin significado informativo)

```xml
<ImageView
    android:layout_width="match_parent"
    android:layout_height="120dp"
    android:src="@drawable/fondo_decorativo"
    android:importantForAccessibility="no" />
```

---

## 9. Imágenes en Compose

### `Image` básica

```kotlin
Image(
    painter = painterResource(R.drawable.foto_portada),
    contentDescription = stringResource(R.string.cover_photo_description),
    contentScale = ContentScale.Crop,
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
)
```

### Con proporción (equivalente a `layout_constraintDimensionRatio`)

```kotlin
Image(
    painter = painterResource(R.drawable.banner),
    contentDescription = stringResource(R.string.banner_description),
    contentScale = ContentScale.Crop,
    modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9f)
)
```

### Imagen decorativa

```kotlin
Image(
    painter = painterResource(R.drawable.fondo_decorativo),
    contentDescription = null, // decorativa: no se lee en TalkBack
    modifier = Modifier
        .fillMaxWidth()
        .height(120.dp)
)
```

### Imagen recortada en forma (círculo, esquinas redondeadas)

```kotlin
Image(
    painter = painterResource(R.drawable.avatar),
    contentDescription = stringResource(R.string.user_avatar),
    contentScale = ContentScale.Crop,
    modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
)
```

En XML, el equivalente requiere un `ShapeableImageView` de Material Components:

```xml
<com.google.android.material.imageview.ShapeableImageView
    android:layout_width="48dp"
    android:layout_height="48dp"
    android:src="@drawable/avatar"
    android:contentDescription="@string/user_avatar"
    android:scaleType="centerCrop"
    app:shapeAppearanceOverlay="@style/CircleImage" />
```

```xml
<!-- res/values/styles.xml -->
<style name="CircleImage">
    <item name="cornerSize">50%</item>
</style>
```

---

## 10. Carga de imágenes remotas (red)

Android no tiene un cargador de imágenes de red incluido "de serie" con caché, redimensionado y gestión de ciclo de vida; se usa una librería. **Coil** es la recomendada hoy en día, con soporte nativo para Compose.

### XML (con Coil)

```kotlin
// build.gradle.kts
implementation("io.coil-kt.coil3:coil-android:3.x.x")
```

```kotlin
findViewById<ImageView>(R.id.ivAvatar).load("https://ejemplo.com/avatar.jpg") {
    crossfade(true)
    placeholder(R.drawable.placeholder_avatar)
    error(R.drawable.error_avatar)
    transformations(CircleCropTransformation())
}
```

### Compose (con Coil)

```kotlin
// build.gradle.kts
implementation("io.coil-kt.coil3:coil-compose:3.x.x")
```

```kotlin
AsyncImage(
    model = "https://ejemplo.com/avatar.jpg",
    contentDescription = stringResource(R.string.user_avatar),
    contentScale = ContentScale.Crop,
    placeholder = painterResource(R.drawable.placeholder_avatar),
    error = painterResource(R.drawable.error_avatar),
    modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
)
```

> `AsyncImage` es, en la práctica, la versión Compose de `Image`, pero resuelve la carga asíncrona, el placeholder y el error por ti. No construyas el equivalente a mano con `LaunchedEffect` + `Bitmap` salvo que necesites algo muy específico.

---

## 11. scaleType / ContentScale: que no se deforme la imagen

| XML (`android:scaleType`) | Compose (`ContentScale`) | Efecto |
|---|---|---|
| `centerCrop` | `ContentScale.Crop` | Rellena el contenedor recortando lo que sobra, sin deformar |
| `fitCenter` | `ContentScale.Fit` | Cabe entera dentro del contenedor, puede dejar espacio vacío |
| `centerInside` | `ContentScale.Inside` | Como `Fit`, pero no agranda si la imagen es más pequeña que el contenedor |
| `fitXY` | `ContentScale.FillBounds` | Estira para llenar exactamente, **puede deformar** — evitar salvo caso muy concreto |
| `center` | `ContentScale.None` | Tamaño original, centrada, puede recortarse si no cabe |

> **Por defecto, muchas apps usan mal `fitXY`/`FillBounds` "porque rellena bonito" y acaban deformando fotos.** Para fotos de personas o productos, `centerCrop`/`Crop` casi siempre es la opción correcta.

---

## 12. Tipografía: fuentes del sistema vs fuentes personalizadas

| Opción | Cuándo usarla |
|---|---|
| **Fuente del sistema** (Roboto por defecto) | La mayoría de apps; es legible, ya está optimizada y no añade peso al APK |
| **Google Fonts (fuentes variables, "Downloadable Fonts")** | Quieres una tipografía de marca sin empaquetarla tú mismo; Android la descarga a través de Google Play Services y la cachea para todas las apps que la usen |
| **Fuente empaquetada en el proyecto** (`res/font/`) | Necesitas control total, offline garantizado, o una fuente que no está en Google Fonts |

---

## 13. Añadir una fuente personalizada al proyecto

1. Coloca el archivo `.ttf`/`.otf` en `res/font/` (nombre en minúsculas, sin espacios: `roboto_condensed_bold.ttf`).
2. Crea un **font family** si tienes varios pesos:

```xml
<!-- res/font/montserrat.xml -->
<font-family xmlns:android="http://schemas.android.com/apk/res/android">
    <font
        android:fontStyle="normal"
        android:fontWeight="400"
        android:font="@font/montserrat_regular" />
    <font
        android:fontStyle="normal"
        android:fontWeight="700"
        android:font="@font/montserrat_bold" />
    <font
        android:fontStyle="italic"
        android:fontWeight="400"
        android:font="@font/montserrat_italic" />
</font-family>
```

Esto sirve **igual tanto para XML como para Compose**: ambos leen el mismo recurso `R.font.montserrat`.

---

## 14. Tipografía en XML

### Uso directo en un `TextView`

```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/login_title"
    android:fontFamily="@font/montserrat"
    android:textSize="24sp"
    android:textStyle="bold"
    android:textColor="?attr/colorOnSurface" />
```

### Mejor: definir un estilo de texto reutilizable

```xml
<!-- res/values/styles.xml -->
<style name="TextAppearance.App.Headline" parent="TextAppearance.Material3.HeadlineMedium">
    <item name="fontFamily">@font/montserrat</item>
    <item name="android:textSize">24sp</item>
    <item name="android:textColor">?attr/colorOnSurface</item>
</style>
```

```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/login_title"
    android:textAppearance="@style/TextAppearance.App.Headline" />
```

> Partir de un `TextAppearance.Material3.*` como padre asegura que mantienes el tamaño/line-height/letter-spacing pensado por Material Design, y solo sobrescribes la fuente.

### Fuente por defecto para toda la app (en el tema)

```xml
<!-- res/values/themes.xml -->
<style name="Theme.MiApp" parent="Theme.Material3.DayNight">
    <item name="android:fontFamily">@font/montserrat</item>
    <item name="fontFamily">@font/montserrat</item>
</style>
```

---

## 15. Tipografía en Compose

### Definir la familia de fuentes una vez

```kotlin
val Montserrat = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_bold, FontWeight.Bold),
    Font(R.font.montserrat_italic, FontWeight.Normal, FontStyle.Italic)
)
```

### Integrarla en la `Typography` del tema (recomendado)

```kotlin
val AppTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
)

@Composable
fun MiAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = AppTypography,
        content = content
    )
}
```

```kotlin
Text(
    text = stringResource(R.string.login_title),
    style = MaterialTheme.typography.headlineMedium,
    color = MaterialTheme.colorScheme.onSurface
)
```

> Igual que en XML conviene heredar de `TextAppearance.Material3.*`, en Compose conviene definir la fuente **una vez en `Typography`** y usar siempre `MaterialTheme.typography.*`, no poner `fontFamily`/`fontSize` sueltos en cada `Text`.

### Override puntual (cuando de verdad hace falta)

```kotlin
Text(
    text = "Precio especial",
    style = MaterialTheme.typography.bodyLarge.copy(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.error
    )
)
```

Usar `.copy()` sobre un estilo existente mantiene el resto de propiedades (line-height, tamaño base) y solo cambia lo necesario, en vez de reconstruir el `TextStyle` entero.

### Fuentes variables de Google Fonts descargadas en tiempo de ejecución

```kotlin
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val montserratGoogleFont = GoogleFont("Montserrat")

val MontserratDownloadable = FontFamily(
    Font(googleFont = montserratGoogleFont, fontProvider = provider)
)
```

Esto evita empaquetar los `.ttf` en el APK; Android los descarga y cachea del lado del sistema. El equivalente en XML es prácticamente idéntico (un `preloaded-fonts-list` + el mismo `GoogleFont.Provider` vía XML), pero en proyectos nuevos se usa mucho más desde Compose/Kotlin directamente.

---

## 16. Unidades de texto: sp, no dp

| Unidad | Para qué |
|---|---|
| **sp** (scale-independent pixels) | **Siempre** para tamaño de texto (`textSize` en XML, `fontSize` en Compose) |
| **dp** | Todo lo demás: iconos, paddings, anchos, altos |

- `sp` escala con la **preferencia de tamaño de fuente** del sistema (accesibilidad); `dp` no.
- Si usas `dp` para texto, un usuario con la fuente del sistema aumentada no verá el cambio en esa app, lo cual es un problema de accesibilidad.
- En Compose, `TextUnit` cubre ambos casos (`.sp` y `.dp`), pero para texto usa siempre `.sp`.

```kotlin
// ✅ Bien
fontSize = 16.sp

// ❌ Mal: el texto no escala con la accesibilidad del sistema
fontSize = 16.dp.toSp() // además esto ni siquiera es una conversión directa válida
```

---

## 17. Accesibilidad de iconos, imágenes y texto

- **Icono/imagen con significado → `contentDescription` descriptivo.** "Icono de estrella" es peor que "Añadir a favoritos".
- **Icono/imagen decorativa → sin descripción.** XML: `android:importantForAccessibility="no"`. Compose: `contentDescription = null`.
- **Icono + texto juntos que dicen lo mismo** (ej. icono de guardar al lado del texto "Guardar"): el icono no necesita descripción propia (`contentDescription = null`), porque el texto ya lo explica; si lo describes también, TalkBack lo lee dos veces.
- **No metas información solo en una imagen** (como una captura con texto dentro) sin ofrecer el texto real en algún sitio accesible.
- **Contraste de texto**: con tipografías finas o tamaños pequeños, vigila el contraste mínimo (4.5:1) entre el texto y el fondo, especialmente con texto sobre imágenes.
- **Texto sobre imagen**: añade un degradado o capa semitransparente detrás del texto para garantizar legibilidad en cualquier foto.

```kotlin
Box {
    Image(
        painter = painterResource(R.drawable.foto),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                )
            )
    )
    Text(
        text = "Título sobre la foto",
        color = Color.White,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp)
    )
}
```

---

## 18. Rendimiento y buenas prácticas

- **Vector Drawable para iconos siempre que se pueda**: menos peso de APK, nitidez en cualquier densidad.
- **WebP en vez de PNG/JPG** para fotos: ahorra tamaño sin pérdida de calidad perceptible.
- **No cargues imágenes a resolución completa** si se van a mostrar pequeñas; usa las opciones de redimensionado de Coil (`size()`) en vez de cargar un bitmap de 4000px para un avatar de 48dp.
- **Cachea** las imágenes de red (Coil ya lo hace por defecto, en memoria y disco).
- **No instancies `BitmapFactory` a mano en el hilo principal**: bloquea la UI. Usa siempre una librería de carga (Coil) que ya gestiona hilos.
- **Reutiliza el mismo `FontFamily`/`Typography`** en vez de crear objetos nuevos cada recomposición en Compose (defínelo como `val` a nivel de archivo o dentro del tema, no dentro de la función que se recompone a menudo).
- **No abuses de fuentes personalizadas con muchos pesos**: cada peso es un archivo que aumenta el tamaño del APK; incluye solo los pesos (regular, bold, italic...) que realmente usas.
- **Evita redimensionar manualmente un Vector Drawable con `scaleType` raro**: ya escala limpio por definición; si se ve raro, revisa el `viewportWidth/Height` del propio XML del vector.

---

## 19. Ejemplo completo: tarjeta con icono, imagen y texto

Mismo componente construido en XML y en Compose, para comparar.

### XML

```xml
<!-- res/layout/item_card_destino.xml -->
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardCornerRadius="16dp"
    app:cardElevation="2dp">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <com.google.android.material.imageview.ShapeableImageView
            android:id="@+id/ivDestino"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:contentDescription="@string/destination_photo"
            android:scaleType="centerCrop"
            app:layout_constraintDimensionRatio="16:9"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:shapeAppearanceOverlay="@style/TopRoundedImage"
            tools:src="@tools:sample/backgrounds/scenic" />

        <TextView
            android:id="@+id/tvNombre"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginStart="16dp"
            android:layout_marginTop="12dp"
            android:layout_marginEnd="8dp"
            android:textAppearance="@style/TextAppearance.App.Headline"
            app:layout_constraintEnd_toStartOf="@id/ivFavorito"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@id/ivDestino"
            tools:text="Kioto, Japón" />

        <ImageButton
            android:id="@+id/ivFavorito"
            android:layout_width="40dp"
            android:layout_height="40dp"
            android:layout_marginEnd="8dp"
            android:background="?attr/selectableItemBackgroundBorderless"
            android:contentDescription="@string/add_to_favorites"
            android:src="@drawable/ic_favorite_border"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="@id/tvNombre"
            app:tint="?attr/colorPrimary" />

        <TextView
            android:id="@+id/tvDescripcion"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginHorizontal="16dp"
            android:layout_marginTop="4dp"
            android:layout_marginBottom="16dp"
            android:textAppearance="@style/TextAppearance.Material3.BodyMedium"
            android:textColor="?attr/colorOnSurfaceVariant"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@id/tvNombre"
            tools:text="Templos centenarios y jardines de otoño." />

    </androidx.constraintlayout.widget.ConstraintLayout>
</com.google.android.material.card.MaterialCardView>
```

### Compose

```kotlin
@Composable
fun TarjetaDestino(
    nombre: String,
    descripcion: String,
    imagenUrl: String,
    esFavorito: Boolean,
    onFavoritoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                model = imagenUrl,
                contentDescription = stringResource(R.string.destination_photo),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onFavoritoClick) {
                    Icon(
                        imageVector = if (esFavorito) Icons.Filled.Favorite
                            else Icons.Outlined.FavoriteBorder,
                        contentDescription = stringResource(R.string.add_to_favorites),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = 16.dp
                )
            )
        }
    }
}
```

Puntos a notar en paralelo:

- La imagen usa **proporción 16:9** en ambos (`layout_constraintDimensionRatio` / `aspectRatio`), recortada con `centerCrop`/`Crop`.
- El icono de favorito tiene **área táctil correcta** (`ImageButton` 40–48dp con ripple circular / `IconButton`).
- El texto usa **estilos del tema** (`TextAppearance.App.Headline` / `MaterialTheme.typography.headlineMedium`), no tamaños sueltos.
- `contentDescription` presente en imagen e icono interactivo.

---

## 20. Errores comunes

| Error | Consecuencia | Solución |
|---|---|---|
| Usar PNG para iconos simples en vez de Vector Drawable | APK más pesado, borroso en algunas densidades | Vector Drawable |
| `scaleType="fitXY"` / `ContentScale.FillBounds` en fotos | Imagen deformada | `centerCrop` / `Crop` |
| Tamaño de texto en `dp` | No escala con accesibilidad | Usar siempre `sp` |
| `contentDescription` ausente en imágenes con significado | TalkBack no puede describir el contenido | Añadir descripción clara |
| `contentDescription` presente en elementos puramente decorativos | TalkBack lee ruido innecesario | `null` / `importantForAccessibility="no"` |
| Cargar bitmaps a resolución completa para miniaturas | Consumo de memoria innecesario, scroll con tirones | Redimensionar con Coil (`size()`) |
| Definir `fontFamily`/`fontSize` sueltos en cada `Text`/`TextView` | Inconsistencia tipográfica, difícil de mantener | Centralizar en `Typography` (Compose) o `TextAppearance` (XML) |
| Empaquetar muchos pesos de una fuente sin usarlos todos | APK innecesariamente grande | Incluir solo los pesos usados |
| Crear el `FontFamily` dentro de un composable que se recompone a menudo | Recreación innecesaria del objeto | Definirlo como `val` a nivel de archivo/tema |
| No comprobar contraste de texto sobre imágenes | Texto ilegible en fotos claras/oscuras | Degradado o capa semitransparente detrás del texto |

---

## 21. Checklist final

- [ ] ¿Los iconos simples son Vector Drawable, no PNG?
- [ ] ¿Las fotos usan WebP y la densidad/proporción adecuada?
- [ ] ¿`scaleType`/`ContentScale` es `centerCrop`/`Crop` para fotos (no `fitXY`/`FillBounds`)?
- [ ] ¿Las imágenes de red pasan por una librería de carga (Coil) con placeholder y error?
- [ ] ¿`contentDescription` presente en todo lo que aporta información, y `null`/`no` en lo decorativo?
- [ ] ¿El texto usa `sp`, nunca `dp`?
- [ ] ¿La tipografía está centralizada en `Typography` (Compose) o `TextAppearance`/tema (XML), no repetida suelta en cada vista?
- [ ] ¿Se incluyen solo los pesos de fuente realmente usados?
- [ ] ¿Los iconos interactivos tienen 48dp de área táctil (`IconButton`/`ImageButton` + ripple)?
- [ ] ¿Hay suficiente contraste en texto sobre imágenes o fondos de color?
- [ ] ¿Se ha probado con fuente del sistema aumentada y en modo oscuro?

---

## Resumen rápido

1. **Iconos → Vector Drawable** casi siempre; tíntalos con el color del tema, no fijo.
2. **Fotos → WebP**, cargadas con Coil, recortadas con `centerCrop`/`Crop` para no deformarlas.
3. **Nunca `dp` para texto**: siempre `sp`.
4. **Tipografía centralizada**: `Typography` en Compose, `TextAppearance` + tema en XML — nunca suelta en cada vista.
5. **`contentDescription`** siempre que el contenido informe algo; `null` cuando es decorativo.
6. **Mismos recursos (`res/font`, `res/drawable`) sirven para XML y Compose** — solo cambia cómo se referencian en código.
