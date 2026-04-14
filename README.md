# ProyectoApp-G3

Aplicación Android desarrollada en Android Studio como adaptación del ecommerce realizado previamente en Laravel.

## Descripción

En este proyecto se ha desarrollado una primera versión funcional de una app móvil que simula el acceso a un ecommerce con estética retro.

Se ha partido de una implementación inicial individual (login y navegación básica) y se ha ampliado para incluir nuevas pantallas y funcionalidades propias del proyecto grupal.

La app cuenta con:

- Pantalla de login
- Pantalla principal (home) con estilo retro
- Catálogo de productos
- Carrito de compra básico

## Funcionalidades

- Introducción de usuario y contraseña (simulado)
- Navegación entre múltiples Activities:
  - Login → Home
  - Home → Catálogo
  - Home → Carrito
- Visualización de catálogo de productos con imágenes
- Selección de productos
- Carrito de compra "precario"

## Carrito de compra (implementación actual)

En esta fase del proyecto se ha implementado un carrito básico que permite:

- Seleccionar un producto desde el catálogo
- Visualizar su información en la pantalla de carrito
- Calcular subtotal, impuestos y total

Los datos del producto se pasan entre Activities mediante `Intent`, sin uso de base de datos ni almacenamiento persistente.

Por este motivo, el carrito muestra únicamente el último producto seleccionado, sin acumulación de múltiples productos. Esta solución se ha planteado como una implementación inicial, dejando para fases posteriores la mejora mediante almacenamiento local (SQLite) y gestión de listas de productos.

## Estructura del proyecto

- `MainActivity.kt` → pantalla de login
- `EcommerceActivity.kt` → pantalla principal (home)
- `CatalogActivity.kt` → catálogo de productos
- `CartActivity.kt` → carrito de compra
- `activity_main.xml` → diseño del login
- `activity_ecommerce.xml` → interfaz de la home
- `activity_catalog.xml` → diseño del catálogo
- `activity_cart.xml` → diseño del carrito
- `strings.xml` → textos de la aplicación

## Tecnologías utilizadas

- Android Studio
- Kotlin
- XML (Layouts)
- Git y GitHub

## Recursos incluidos

- Carpeta `/apk` → contiene el APK generado
- Carpeta `/docs` → documentación de la primera entrega

## Equipo

Grupo 3:
- Miguel
- Ronnald
- DavidL
- Juan

## Objetivo

Aprender a:

- Adaptar una aplicación web a entorno móvil
- Crear interfaces en Android con XML
- Gestionar múltiples Activities
- Implementar navegación entre pantallas
- Introducir lógica básica de interacción entre componentes

## Estado

Aplicación funcional con navegación completa entre pantallas, catálogo visual y carrito básico implementado.