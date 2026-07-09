# BuildMyPC - Grupo 4

## Descripción del Proyecto

BuildMyPC es una plataforma desarrollada bajo una arquitectura de microservicios orientada a la gestión y configuración de componentes de computadores. El sistema permite administrar usuarios, componentes de hardware, compatibilidad entre piezas y generación de cotizaciones para builds personalizadas.

El proyecto fue desarrollado utilizando tecnologías basadas en Java y Spring Boot, implementando servicios independientes que se comunican entre sí para garantizar escalabilidad, mantenibilidad y modularidad.

---

## Integrantes del Grupo 4

- Juan David Córdova
- Joan Lara
- Benjamin Cofre

---

## Arquitectura del Proyecto

El sistema está compuesto por múltiples microservicios independientes:

- `msvc_auth-service` → Gestión de autenticación.
- `msvc_user-service` → Administración de usuarios.
- `msvc_component-service` → Gestión general de componentes.
- `msvc_cpu-service` → Administración de procesadores.
- `msvc_gpu-service` → Administración de tarjetas gráficas.
- `msvc_ram-service` → Administración de memorias RAM.
- `msvc_motherboard-service` → Administración de placas madre.
- `msvc_power-supply-service` → Administración de fuentes de poder.
- `msvc_build-service` → Gestión de configuraciones/builds de computadores.
- `msvc_compatibility-service` → Validación de compatibilidad entre componentes.
- `msvc_benchmark-service` → Gestión de benchmarks y rendimiento.
- `msvc_quotation-service` → Generación de cotizaciones.

---

## Funcionalidades Implementadas

### Gestión de Usuarios
- Administración de perfiles de usuario.

### Gestión de Componentes
- CRUD de componentes de hardware.
- Registro y administración de CPUs.
- Registro y administración de GPUs.
- Registro y administración de RAM.
- Registro y administración de placas madre.
- Registro y administración de fuentes de poder.

### Gestión de Builds
- Creación de builds personalizadas.
- Asociación de componentes a una build.
- Validación de compatibilidad entre componentes.

### Compatibilidad y Benchmark
- Verificación de compatibilidad entre piezas.
- Gestión de benchmarks y rendimiento de componentes.

---

## Tecnologías Utilizadas

- Java
- Spring Boot
- Maven
- H2 Database
- Git y GitHub
- IntelliJ IDEA

---

## Estructura del Proyecto

```plaintext
Grupo_4/
│
├── msvc_auth-service/
├── msvc_user-service/
├── msvc_component-service/
├── msvc_cpu-service/
├── msvc_gpu-service/
├── msvc_ram-service/
├── msvc_motherboard-service/
├── msvc_power-supply-service/
├── msvc_build-service/
├── msvc_compatibility-service/
├── msvc_benchmark-service/
├── msvc_quotation-service/
└── data/
```

---

## Requisitos Previos

Antes de ejecutar el proyecto, asegúrese de tener instalado:

- Java JDK 17 o superior.
- Maven 3.8 o superior.
- IntelliJ IDEA o cualquier IDE compatible.
- Git.

---

## Pasos para Ejecutar el Proyecto

### 1. Clonar el Repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
```

### 2. Acceder al Proyecto

```bash
cd Grupo_4
```

### 3. Ejecutar los Microservicios

Cada microservicio puede ejecutarse individualmente.

Ejemplo:

```bash
cd msvc_auth-service
mvn spring-boot:run
```

Repetir el proceso para cada microservicio necesario.

---

## Base de Datos

El proyecto utiliza bases de datos H2 locales almacenadas en la carpeta:

```plaintext
/data
```

---

## Consideraciones

- Verificar que los puertos configurados para cada microservicio no entren en conflicto.
- Asegurarse de levantar primero los servicios base requeridos por otros microservicios.
- El proyecto puede ser abierto directamente desde IntelliJ IDEA.

---

### Link para el Gateway

```bash
http://localhost:8080/docs/swagger-ui.html
```


## Estado del Proyecto

Proyecto académico desarrollado para la asignatura de Fullstack utilizando arquitectura de microservicios.

