# microservices-kafka-sprint2

Guia rapida para levantar todo el proyecto (Kafka + 2 servicios Spring Boot + frontend React).

<img src="./diagrama.png" alt="Diagrama del proyecto" width="700" />

## 1. Levantar infraestructura Kafka (Docker Compose)

Desde la carpeta `kafkaExample`:

```bash
cd kafkaExample
docker compose up -d
```

## 2. Levantar el servicio Spring Boot consumidor (`str-consumer`)

En una terminal nueva:

```bash
cd kafkaExample/str-consumer
mvn spring-boot:run
```

Alternativa con wrapper Maven:

```bash
cd kafkaExample/str-consumer
chmod +x mvnw
./mvnw spring-boot:run
```

Puerto esperado: `8100`.

## 3. Levantar el servicio Spring Boot productor (`str-producer`)

En otra terminal nueva:

```bash
cd kafkaExample/str-producer
mvn spring-boot:run
```

Alternativa con wrapper Maven:

```bash
cd kafkaExample/str-producer
chmod +x mvnw
./mvnw spring-boot:run
```

Puerto esperado: `8000`.

## 4. Levantar el frontend React (`frontProducerDoctors`)

En otra terminal nueva:

```bash
cd frontProducerDoctors
npm install
npm run dev
```

Abre la URL que muestra Vite (normalmente `http://localhost:5173`).

## 5. Orden recomendado de arranque

1. `docker compose up -d`
2. `str-consumer`
3. `str-producer`
4. `frontProducerDoctors`

## 6. Detener todo

1. Deten los servicios Spring Boot con `Ctrl + C` en sus terminales.
2. Baja Docker Compose:

```bash
cd kafkaExample
docker compose down
```
