## Dificultades encontradas durante la primera entrega

Durante el desarrollo de esta primera versión de la aplicación se han identificado varias dificultades, principalmente relacionadas con el cambio de entorno (de aplicación web a aplicación móvil) y el uso de Android Studio.

### 1. Adaptación del diseño web a móvil

Una de las principales dificultades ha sido trasladar el diseño del ecommerce desarrollado en Laravel a una interfaz móvil.

El diseño original está pensado para escritorio, con múltiples columnas y mayor espacio visual, mientras que en Android ha sido necesario adaptar todos los elementos a una disposición vertical, priorizando la legibilidad y la usabilidad en pantalla reducida.

Además, replicar el estilo visual (colores, tipografías y estructura) ha requerido varios ajustes hasta conseguir una apariencia coherente.

---

### 2. Gestión de múltiples Activities

Otra dificultad importante ha sido comprender y gestionar correctamente la navegación entre distintas pantallas (Activities).

Inicialmente, la aplicación contaba únicamente con una pantalla de login, pero posteriormente se han añadido nuevas Activities (home, catálogo y carrito), lo que ha requerido:

- Crear correctamente cada Activity
- Declararlas en el `AndroidManifest.xml`
- Gestionar la navegación mediante `Intent`

Se produjeron errores iniciales al no registrar algunas Activities en el manifest, lo que provocaba el cierre de la aplicación.

---

### 3. Paso de datos entre pantallas

Para implementar el carrito ha sido necesario aprender a pasar información entre Activities mediante `Intent`.

Esta parte ha supuesto una dificultad inicial, ya que se ha tenido que entender cómo enviar y recibir datos (nombre del producto y precio), así como cómo utilizarlos posteriormente en la pantalla de destino.

---

### 4. Implementación del carrito

El carrito se ha implementado de forma básica ("precaria"), tal como indica el enunciado.

Actualmente:

- Solo se muestra el último producto seleccionado
- No se almacenan múltiples productos
- No existe persistencia de datos

Aunque esta solución es funcional, se ha identificado como mejora futura la necesidad de utilizar estructuras más completas (listas) y almacenamiento local (SQLite).

---

### 5. Gestión de recursos gráficos

El uso de imágenes en Android ha requerido:

- Adaptar nombres de archivos (minúsculas, sin espacios)
- Ubicarlas correctamente en la carpeta `drawable`
- Ajustar tamaños y escalado para que se vean correctamente en pantalla

También se ha detectado que la tipografía influye significativamente en el resultado visual, aunque no se ha implementado una fuente personalizada en esta fase.

---

### 6. Uso de Git y organización del proyecto

A nivel de control de versiones, se ha trabajado en:

- Creación de un nuevo repositorio grupal
- Organización de carpetas (`apk`, `docs`)
- Preparación de ramas individuales para el trabajo en equipo

Esto ha permitido dejar una base preparada para el desarrollo colaborativo en fases posteriores.

---

## Conclusión

Las principales dificultades han estado relacionadas con la adaptación al entorno Android y la estructuración de la aplicación en múltiples pantallas.

A pesar de ello, se ha conseguido una versión funcional que cumple con los requisitos de la actividad, dejando preparada la base para continuar el desarrollo en equipo.