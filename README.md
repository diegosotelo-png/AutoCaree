# AutoCare 🚗

App móvil Android para gestión y recordatorio
de mantenimiento de vehículos personales.

## Equipo
- Diego Sotelo (Integrante A)
- Yefry Calderón (Integrante B)

## Temática
Gestión y recordatorio de mantenimiento
de vehículos personales.

## Tecnologías
- Kotlin + Jetpack Compose
- Arquitectura MVVM
- Room (base de datos local)
- Retrofit + API Ninjas Car Data
- Firebase Authentication
- Cloud Firestore
- WorkManager (notificaciones locales)
- Firebase Cloud Messaging (FCM)

## Cómo aplicamos MVVM
La Vista solo muestra datos y captura
acciones del usuario. El ViewModel gestiona
el estado con StateFlow y llama al Repository.
El Repository es la única fuente de datos,
centralizando Room, Retrofit y Firestore.
El ViewModel nunca sabe de dónde vienen
los datos.

## Pantallas Parte 1
- Lista de mantenimientos con filtros
- Detalle del mantenimiento + datos API
- Formulario crear/editar
- Vehículos
- Resumen/Estadísticas

## Pantallas Parte 2
- Login
- Registro
- Perfil con cierre de sesión

## API Externa
API Ninjas Car Data
https://api.api-ninjas.com/v1/cars
Muestra datos técnicos del vehículo
en la pantalla de detalle.

## Notificaciones
- Local: WorkManager programa recordatorios
  según la fecha del mantenimiento
- Push: Firebase Cloud Messaging recibe
  notificaciones desde la consola de Firebase
