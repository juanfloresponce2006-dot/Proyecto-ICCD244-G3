Requisitos para la Instalación
El entorno de desarrollo debe cumplir con las siguientes especificaciones:

Java Development Kit (JDK): Versión 17, 21 o superior.

Entorno de Desarrollo (IDE): IntelliJ IDEA (recomendado).

Librería de Layouts Externos: El proyecto requiere obligatoriamente enlazar el archivo binario AbsoluteLayout-SNAPSHOT.jar (provisto en la carpeta lib/) dentro de las dependencias de bibliotecas del proyecto en el IDE para permitir la correcta compilación y posicionamiento de los overlays de apuestas sobre el paño de la ruleta.

Instrucciones de Ejecución
Abra tu IDE y carga el directorio del proyecto Casino/.

Verifica en las propiedades del proyecto que el JAR de la carpeta lib/ esté añadido correctamente a las dependencias del módulo de compilación.

Para ejecutar el entorno visual completo (GUI): Dirijete a src/main/java/base/Main.java, haz clic derecho y selecciona Run 'Main.main()'. Esto desplegará la ventana principal de bienvenida con opciones de inicio de sesión y registro interactivo.

Para ejecutar por terminal (Consola de comandos): Dirijete a src/main/java/base/MainConsole.java, asegúrate de invocar el arranque en el punto de entrada y selecciona Run 'MainConsole.main()'. Esto iniciará el flujo secuencial interactivo guiado por menús numéricos directo en la terminal de su entorno de desarrollo.
