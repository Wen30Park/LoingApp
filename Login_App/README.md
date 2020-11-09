# Login con Firebase (user/password) y autenticación con Google
App desarrollada en Kotlin. 

A continuación explico los pasos para que la app funcione sin problemas:

1.- Para poder utilizar todas las posibilidades de Firebase, es necesario crear una cuenta o añadir una existente en https://firebase.google.com/?hl=es-419. Una vez hayamos creado la cuenta, debemos conectar android studio con dicha cuenta.

2.- Una vez hecho esto deberemos conectar nuestra app a Firebase. Para ello debemos ir a Tool-> Firebase -> Authentication y seguir los pasos para conectar y añadir las dependecias necesarias para poder realizar la conexión.

3.- En este caso la aplicación cuenta con autenticación mediante un usuario y contraseña, o a través de los servicios de Google. Por lo tanto debemos ir a la consola de Firebase -> Athentication -> Método de acceso, y habilitar la autenticación por medio de "Correo electrónico/contraseña" y "Google"

*Para poder utilizar la autenticación por medio de correo electrónico y contraseña es necesario añadir un usuario manualmente
