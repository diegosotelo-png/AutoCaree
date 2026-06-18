# AutoCare

**Equipo:** Yefry Calderon / Sotelo Garcia  
**Curso:** Programación en Móviles — Tecsup 2026  
**Temática:** Gestión y recordatorio de mantenimiento de vehículos personales

---

## Descripción

AutoCare es una aplicación Android que permite registrar vehículos y gestionar sus mantenimientos (cambio de aceite, frenos, SOAT, revisión técnica, etc.). Programa recordatorios para que el usuario no olvide fechas clave y consulta datos técnicos del vehículo desde una API externa.

---

## Pantallas

| Pantalla | Función |
|---|---|
| Lista de mantenimientos | Pantalla principal con todos los mantenimientos y filtro por estado (Pendiente / Realizado / Vencido) |
| Detalle | Información completa del mantenimiento, vehículo asociado y datos técnicos de la API |
| Formulario | Crear o editar un mantenimiento (selector de vehículo, DatePicker, tipo, switch de recordatorio) |
| Vehículos | Registrar y eliminar vehículos (marca, modelo, placa, tipo) |
| Resumen | Estadísticas: total, pendientes, realizados, vencidos y próximos en 30 días |

### Capturas

> _(Agregar capturas de pantalla aquí antes de la sustentación)_

---

## Arquitectura MVVM

El proyecto aplica MVVM con tres capas bien separadas:

- **Vista (Compose):** cada pantalla observa el `UiState` del ViewModel con `collectAsState()` y solo envía acciones, sin lógica de negocio ni acceso directo a Room o Retrofit.
- **ViewModel:** expone un `StateFlow<MantenimientoUiState>` con los datos de la pantalla. Recibe acciones de la vista, las delega al Repository y actualiza el estado.
- **Repository:** única fuente de datos. Centraliza el acceso a Room (persistencia local) y a Retrofit (API externa de datos técnicos del vehículo).

---

## Stack tecnológico

- Kotlin + Jetpack Compose + Material 3
- Navigation Compose (paso de argumentos por ruta)
- Room 2.7.1 — CRUD completo (Entity, DAO, Database)
- Retrofit 2.9.0 + Gson — API Ninjas Car Data
- Coroutines + Flow
- MVVM + Repository pattern

---

## Estructura de paquetes

```
com.auto_care_test/
├── data/
│   ├── local/
│   │   ├── dao/          VehiculoDao, MantenimientoDao
│   │   ├── database/     AutoCareDatabase
│   │   └── entity/       VehiculoEntity, MantenimientoEntity
│   ├── remote/
│   │   ├── api/          CarApiService, RetrofitClient
│   │   └── dto/          CarInfoDto
│   └── repository/       AutoCareRepository
├── domain/model/         Vehiculo, Mantenimiento
├── ui/
│   ├── mantenimiento/    ListaScreen, DetalleScreen, FormularioScreen
│   ├── vehiculo/         VehiculosScreen
│   ├── resumen/          ResumenScreen
│   ├── navigation/       NavGraph
│   └── theme/
└── viewmodel/            MantenimientoViewModel, VehiculoViewModel
```
