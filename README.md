# Garantías sin Fricción — AutoBaterías API

API REST en **Spring Boot 4 (Java 21)** para el proyecto **Garantías sin Fricción** (*Grupo 1 – RedPill Tech*), orientada al registro y control de roles, usuarios, catálogo de baterías, ventas y validación de garantías.

---

## 1. Requisitos previos

- **Java JDK 21** (o superior compatible con `java.version=21`).
- **PostgreSQL 15+** (local o mediante **Docker**).
- **Maven**: no requiere instalación global; el proyecto incluye Maven Wrapper (`mvnw.cmd` en Windows, `./mvnw` en Linux/macOS).
- **Postman** (para importar y ejecutar la colección de pruebas de la API).

---

## 2. Estructura del proyecto

```text
MVP----Baterias/
└── demo/
    └── demo/                          # Raíz del proyecto Maven / Spring Boot
        ├── .env.example               # Plantilla de variables de entorno
        ├── .gitignore                 # Ignora .env, target/, .idea/, .vscode/
        ├── mvnw / mvnw.cmd            # Maven Wrapper
        ├── pom.xml                    # Dependencias (Spring Boot 4.1.2, JPA, Flyway, Security)
        ├── postman/
        │   └── AutoBaterias.postman_collection.json
        └── src/
            ├── main/java/autobaterias/demo/
            ├── main/resources/
            │   ├── application.properties
            │   └── dataBase/migrations/
            │       ├── V1__crear_tablas.sql
            │       ├── V2__insertar_roles.sql
            │       └── V3__insertar_marcas.sql
            └── test/java/autobaterias/demo/
```

> **Nota importante sobre el directorio de trabajo:** el proyecto utiliza `springboot4-dotenv`, que busca el archivo `.env` en el **directorio de trabajo** desde donde se inicia la aplicación (`demo/demo/`).

---

## 3. Configuración del archivo `.env`

1. Ve a la carpeta `demo/demo/`.
2. Copia el archivo `.env.example` y guárdalo como `.env` en esa **misma carpeta** (`demo/demo/.env`):

```env
DB_URL=jdbc:postgresql://localhost:5432/autobaterias
DB_USERNAME=postgres
DB_PASSWORD=tu_contrasena_aqui
```

> El archivo `.env` ya está incluido en `demo/demo/.gitignore`, por lo que **nunca se sube a Git** ni afecta la configuración de tus compañeros.

---

## 4. Configuración de la base de datos (PostgreSQL + Flyway)

La aplicación utiliza **Flyway** (`spring.jpa.hibernate.ddl-auto=validate`), por lo que al arrancar ejecuta automáticamente las migraciones:
- `V1__crear_tablas.sql`: crea las tablas `rol`, `marca`, `usuario`, `bateria`, `venta` y `detalle_venta`.
- `V2__insertar_roles.sql`: inserta el rol inicial `ADMINISTRADORA` (`id = 1`).
- `V3__insertar_marcas.sql`: inserta las marcas `MAC` (`1`), `WILLARD` (`2`), `BOSCH` (`3`), `VARTA` (`4`) y `TUDOR` (`5`).

Solo necesitas tener un servidor PostgreSQL activo con la base de datos `autobaterias` creada:

### Opción A: Usando Docker (recomendado si no quieres instalar PostgreSQL)
```powershell
docker run --name autobaterias-postgres `
  -e POSTGRES_DB=autobaterias `
  -e POSTGRES_USER=postgres `
  -e POSTGRES_PASSWORD=change_me `
  -p 5432:5432 -d postgres:16-alpine
```
Asegúrate de que `DB_PASSWORD` en `demo/demo/.env` coincida con `POSTGRES_PASSWORD`.

### Opción B: Usando PostgreSQL instalado localmente
1. Crea la base de datos en PostgreSQL (`psql` o `pgAdmin`):
   ```sql
   CREATE DATABASE autobaterias;
   ```
2. Coloca tu usuario y contraseña reales en `demo/demo/.env`.

---

## 5. Cómo ejecutar el proyecto

### Desde Terminal (PowerShell / Bash)
```powershell
cd demo/demo
.\mvnw.cmd spring-boot:run
```
*(En Linux/macOS usa `./mvnw spring-boot:run`.)*

### Desde Visual Studio Code
1. Instala el **Extension Pack for Java** y **Spring Boot Extension Pack**.
2. Abre la carpeta `demo/demo` en VS Code (o si abres la raíz del repositorio, asegúrate en `.vscode/launch.json` de que `"cwd": "${workspaceFolder}/demo/demo"` para que encuentre `.env`).
3. Ejecuta la clase principal `autobaterias.demo.AutoBateriasAplicacion` con **Run Java** o desde el panel **Spring Boot Dashboard**.
4. Alternativamente, usa la terminal integrada dentro de `demo/demo`:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

### Desde IntelliJ IDEA
1. Abre la carpeta `demo/demo` (o haz clic derecho en `demo/demo/pom.xml` → **Add as Maven Project**).
2. Verifica en **File → Project Structure** que el SDK sea **Java 21**.
3. Ejecuta `AutoBateriasAplicacion`.
4. Si aparece error de variables `${DB_URL}`, revisa **Run → Edit Configurations...** y confirma que **Working directory** apunte a `.../MVP----Baterias/demo/demo` (donde está `.env`).

Cuando arranque correctamente, verás en consola que Flyway aplicó las 3 migraciones y Tomcat quedó escuchando en `http://localhost:8080`.

---

## 6. Ejecutar pruebas automatizadas

Las pruebas unitarias y de controladores (`MockMvc`) no requieren base de datos activa:

```powershell
cd demo/demo
.\mvnw.cmd test
```

---

## 7. Endpoints REST implementados

> **Seguridad temporal:** `ConfiguracionSeguridad` habilita temporalmente el acceso sin autenticación a `/roles/**`, `/usuarios/**` y `/baterias/**` para facilitar las pruebas del sprint. Los errores siguen el formato `{"estado": <codigo>, "mensaje": "<detalle>"}`.

### Roles (`/roles`)
| Método | Ruta | Descripción | Códigos de respuesta |
|---|---|---|---|
| `GET` | `/roles` | Lista todos los roles | `200` |
| `GET` | `/roles/{id}` | Busca un rol por ID | `200`, `400`, `404` |
| `POST` | `/roles` | Crea rol (`nombre` obligatorio, máx. 50, se normaliza a MAYÚSCULAS) | `201`, `400`, `409` |
| `PUT` | `/roles/{id}` | Actualiza el nombre del rol | `200`, `400`, `404`, `409` |
| `DELETE` | `/roles/{id}` | Elimina rol si no tiene usuarios asignados | `204`, `404`, `409` |

Ejemplo `POST /roles`:
```json
{
  "nombre": "vendedor"
}
```

### Usuarios (`/usuarios`)
| Método | Ruta | Descripción | Códigos de respuesta |
|---|---|---|---|
| `GET` | `/usuarios` | Lista todos los usuarios | `200` |
| `GET` | `/usuarios/{id}` | Busca un usuario por ID | `200`, `400`, `404` |
| `POST` | `/usuarios` | Registra usuario (cifra contraseña con BCrypt y nunca la devuelve) | `201`, `400`, `404`, `409` |
| `PUT` | `/usuarios/{id}` | Actualiza datos del usuario (sin modificar contraseña) | `200`, `400`, `404`, `409` |
| `DELETE` | `/usuarios/{id}` | Borrado lógico (`activo = false`) | `204`, `404` |

Ejemplo `POST /usuarios`:
```json
{
  "identificacion": "1053000111",
  "nombre": "Ana",
  "apellido": "Gomez",
  "telefono": "3001234567",
  "correo": "ana@autobaterias.com",
  "rolId": 1,
  "contrasena": "Secreta123"
}
```
> **Nota:** `correo` no debe incluir espacios al inicio o al final (validado por `@Email` antes de normalizar a minúsculas); `contrasena` requiere entre 8 y 72 caracteres.

### Baterías (`/baterias`)
| Método | Ruta | Descripción | Códigos de respuesta |
|---|---|---|---|
| `GET` | `/baterias` | Lista todas las baterías | `200` |
| `GET` | `/baterias/{id}` | Busca una batería por ID | `200`, `400`, `404` |
| `POST` | `/baterias` | Crea batería (`codigo` único normalizado a MAYÚSCULAS, `precio >= 0.00` máx. 2 decimales, `mesesGarantia >= 0`, `existencias >= 0`) | `201`, `400`, `404`, `409` |
| `PUT` | `/baterias/{id}` | Actualiza una batería existente | `200`, `400`, `404`, `409` |
| `DELETE` | `/baterias/{id}` | Borrado lógico (`activo = false`) | `204`, `404` |

Ejemplo `POST /baterias`:
```json
{
  "codigo": "MAC-42",
  "modelo": "Silver 42",
  "marcaId": 1,
  "precio": 450000.00,
  "mesesGarantia": 12,
  "existencias": 8
}
```

---

## 8. Pruebas manuales y automatizadas con Postman

1. Inicia la base de datos y levanta la aplicación en `http://localhost:8080`.
2. Abre **Postman** → **Import** y selecciona el archivo:
   `demo/demo/postman/AutoBaterias.postman_collection.json`
3. Verifica la variable de colección `baseUrl` (`http://localhost:8080`).
4. Ejecuta la colección completa con **Run collection** (en orden secuencial), ya que las peticiones iniciales crean registros y guardan automáticamente `rolId`, `usuarioId` y `bateriaId` para los casos de consulta, duplicado, actualización y borrado lógico.
5. Para probar manualmente desde terminal sin Postman, puedes listar los roles iniciales sembrados por Flyway:
   ```powershell
   curl.exe -s http://localhost:8080/roles
   ```
   Respuesta esperada:
   ```json
   [{"id":1,"nombre":"ADMINISTRADORA"}]
   ```
