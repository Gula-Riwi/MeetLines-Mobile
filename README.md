# MeetLine App 📱

MeetLine es una aplicación móvil nativa de Android diseñada para simplificar la gestión de citas y reservas en diversos tipos de negocios. Permite a los usuarios descubrir servicios, verificar disponibilidad y agendar citas de manera rápida y sencilla.

## ✨ Características Principales

### 🔐 Autenticación y Perfil
*   **Registro e Inicio de Sesión:** Sistema seguro de autenticación para usuarios.
*   **Gestión de Perfil:** Actualización de información personal y preferencias.
*   **Sesión Persistente:** Manejo automático de sesiones de usuario.

### 🏢 Exploración de Negocios
*   **Listado de Negocios:** Visualización de negocios disponibles por categorías.
*   **Búsqueda Avanzada:** Filtrado por nombre, categoría o ubicación.
*   **Detalle de Negocio:** Información completa, servicios ofrecidos, horarios y ubicación.
*   **Negocios Cercanos:** Geolocalización para encontrar servicios próximos.

### 📅 Gestión de Citas
*   **Agendamiento en Línea:** Selección intuitiva de servicios, fechas y horarios disponibles.
*   **Historial de Citas:** Vista de citas próximas, completadas y canceladas.
*   **Cancelación:** Posibilidad de cancelar citas programadas.

## 🛠️ Tecnologías y Arquitectura

El proyecto está construido siguiendo las mejores prácticas de desarrollo moderno en Android:

*   **Lenguaje:** [Kotlin](https://kotlinlang.org/) (100%)
*   **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) - Interfaz de usuario declarativa moderna.
*   **Arquitectura:** Clean Architecture (Capas de Presentación, Dominio y Datos) + MVVM.
*   **Inyección de Dependencias:** [Hilt](https://dagger.dev/hilt/).
*   **Red:** [Retrofit](https://square.github.io/retrofit/) + [OkHttp](https://square.github.io/okhttp/) para comunicación con API REST.
*   **Asincronía:** Kotlin Coroutines & Flow.
*   **Navegación:** Jetpack Navigation Compose.

### Estructura del Proyecto

```
app/src/main/java/com/meetline/app/
├── data/                # Capa de Datos (Repositorios, API, DTOs)
├── domain/              # Capa de Dominio (Modelos, Interfaces, Casos de Uso)
├── ui/                  # Capa de Presentación (Pantallas, ViewModels, Componentes)
├── di/                  # Módulos de Inyección de Dependencias
└── MeetLineApplication.kt
```

## 🚀 Configuración y Ejecución

### Prerrequisitos
*   Android Studio Koala o superior.
*   JDK 17.
*   Dispositivo Android o Emulador (API 26+).

### Pasos para ejecutar

1.  **Clonar el repositorio:**
    ```bash
    git clone https://github.com/Gula-Riwi/MeetLines-Mobile.git
    cd MeetLines-Mobile
    ```

2.  **Configurar propiedades:**
    El proyecto utiliza `gradle.properties` para configuraciones base. Asegúrate de tener configurado el SDK de Android correctamente en `local.properties`.

3.  **Compilar y Correr:**
    Abre el proyecto en Android Studio, espera a que Gradle sincronice las dependencias y ejecuta la app en tu emulador o dispositivo físico.

## 🤝 Contribución

Este proyecto sigue un flujo de trabajo basado en **Gitflow**:
*   `main`: Rama de producción estable.
*   `dev`: Rama de desarrollo principal.
*   `feature/*`: Ramas para nuevas funcionalidades.

Para contribuir, por favor crea una rama desde `dev` y envía un Pull Request.

## 📄 Licencia

Este proyecto es propiedad de **Gula Riwi**. Todos los derechos reservados.
