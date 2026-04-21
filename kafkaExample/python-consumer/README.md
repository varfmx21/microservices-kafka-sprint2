# Python Kafka Consumer

Consumer adicional en Python para el proyecto, capaz de:

- Conectarse a Kafka
- Consumir mensajes desde uno o varios topicos
- Procesar y visualizar datos de forma independiente (resumen en consola)

## Requisitos

- Python 3.10+
- Kafka activo (en este proyecto, normalmente `localhost:9092`)

## Instalacion

```bash
cd kafkaExample/python-consumer
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
```

## Ejecucion basica

```bash
python consumer.py --from-beginning
```

Por defecto consume estos topicos:

- `unsc-topic`
- `covenant-topic`
- `flood-topic`
- `forerunner-response-topic`

## Ejecucion personalizada

```bash
python consumer.py \
  --bootstrap-servers localhost:9092 \
  --topics unsc-topic covenant-topic flood-topic \
  --group-id python-extra-consumer \
  --report-every 10
```

## Variables de entorno opcionales

- `KAFKA_BOOTSTRAP_SERVERS` (ejemplo: `localhost:9092`)
- `KAFKA_TOPICS` (separados por coma)
- `KAFKA_GROUP_ID`

Ejemplo:

```bash
set KAFKA_BOOTSTRAP_SERVERS=localhost:9092
set KAFKA_TOPICS=unsc-topic,covenant-topic
set KAFKA_GROUP_ID=python-consumer-env
python consumer.py
```
