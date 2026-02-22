# Network & Security Lab 🛡️ | Core Backend Competencies

Este repositorio constituye un laboratorio técnico donde se exploran y resuelven desafíos críticos en el desarrollo de software de sistemas, comunicaciones en red y seguridad criptográfica. Cada módulo representa un pilar fundamental de la arquitectura backend moderna.

---

## 📂 Contenido del Laboratorio

| Módulo | Enfoque Técnico | Tecnologías Clave |
| :--- | :--- | :--- |
| **HttpSecureProject** | Servicios Web, CRUD Seguro y FTP | Java 17, MySQL, SHA-256, JWT |
| **Concurrency_Mastery** | Sincronización de hilos y concurrencia | Multithreading, Sincronización FIFO |
| **Multiplayer_TCP_Engine** | Comunicaciones en tiempo real (Sockets) | TCP Sockets, Multi-client threads |
| **System_Process_Management** | Interacción con SO y persistencia | ProcessBuilder, File I/O, Modularidad |

---

## 🛠️ Detalles de los Módulos

### 1. HttpSecureProject (Servicios en Red y Seguridad)
Ecosistema robusto de servicios basado en arquitectura Cliente-Servidor.
* **Seguridad Avanzada:** Implementación de hashing **SHA-256** con **Salting** único por usuario para mitigar ataques de diccionario.
* **Prevención de Inyecciones:** Uso estricto de `PreparedStatement` para neutralizar ataques de SQL Injection en MySQL.
* **Concurrencia Eficiente:** Servidor no bloqueante mediante `ThreadPoolExecutor` para gestionar múltiples sesiones simultáneas.
* **Integración FTP:** Cliente especializado para transferencia de recursos mediante protocolos estándar.

### 2. Concurrency_Mastery (Simulación de Suministro)
Resolución del problema clásico del **Productor-Consumidor** aplicado a una cadena de suministro industrial.
* **Sincronización:** Uso de `synchronized`, `wait()` y `notifyAll()` para garantizar la integridad de los datos en un buffer compartido de capacidad limitada.
* **Gestión FIFO:** Implementación de una cola de procesamiento estricta donde los elementos se producen y consumen en orden numérico exacto.

### 3. Multiplayer_TCP_Engine (Mastermind Multijugador)
Motor de juego de lógica basado en protocolos de capa de aplicación propios sobre **TCP Sockets**.
* **Soporte Multijugador:** Servidor concurrente capaz de gestionar hasta 10 partidas independientes de forma simultánea.
* **Persistencia de Datos:** Sistema de ranking global con guardado físico en `ranking.txt` y acceso sincronizado mediante `ConcurrentHashMap`.

### 4. System_Process_Management (Gestión de Procesos)
Aplicación modular para la gestión de datos persistentes con separación total de responsabilidades.
* **Orquestación de Procesos:** Uso de `ProcessBuilder` para lanzar y coordinar procesos externos encargados del almacenamiento y recuperación de datos.
* **Persistencia CSV:** Sistema de almacenamiento estructurado en ficheros individuales por registro con validaciones de integridad.

---

## 🚀 Competencias Demostradas
- **Programación Segura:** Manejo de datos sensibles y protección contra vulnerabilidades comunes.
- **Arquitectura de Red:** Diseño de protocolos de comunicación y gestión de Sockets TCP/HTTP.
- **Concurrencia:** Control de hilos, prevención de condiciones de carrera y optimización de recursos.

---

## 👤 Autor
**Aitor Jury Rodríguez** - *Software Developer & Intern @ BBVA Technology*
- [LinkedIn](https://www.linkedin.com/in/aitor-jury-rodr%C3%ADguez-6330742b1/)
- [Email](mailto:aitor.jr04@gmail.com)
