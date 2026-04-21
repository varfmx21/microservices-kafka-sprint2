import argparse
import os
import signal
import sys
import time
from collections import defaultdict
from datetime import datetime, timezone

from kafka import KafkaConsumer
from kafka.errors import NoBrokersAvailable

RUNNING = True


def parse_args() -> argparse.Namespace:
    default_topics = os.getenv(
        "KAFKA_TOPICS",
        "unsc-topic,covenant-topic,flood-topic,forerunner-response-topic",
    )
    topics = [topic.strip() for topic in default_topics.split(",") if topic.strip()]

    parser = argparse.ArgumentParser(
        description="Kafka consumer en Python para consumir y visualizar mensajes."
    )
    parser.add_argument(
        "--bootstrap-servers",
        default=os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"),
        help="Servidor(es) Kafka, separados por coma.",
    )
    parser.add_argument(
        "--topics",
        nargs="+",
        default=topics,
        help="Lista de topicos a consumir. Ejemplo: --topics unsc-topic covenant-topic",
    )
    parser.add_argument(
        "--group-id",
        default=os.getenv("KAFKA_GROUP_ID", "python-analytics-consumer"),
        help="Group ID del consumer.",
    )
    parser.add_argument(
        "--from-beginning",
        action="store_true",
        help="Si se usa, consume desde earliest.",
    )
    parser.add_argument(
        "--report-every",
        type=int,
        default=5,
        help="Cada cuantos mensajes mostrar un resumen.",
    )
    parser.add_argument(
        "--timeout-ms",
        type=int,
        default=1000,
        help="Tiempo de espera de polling en ms.",
    )
    parser.add_argument(
        "--connect-retries",
        type=int,
        default=20,
        help="Cantidad maxima de reintentos de conexion.",
    )
    parser.add_argument(
        "--retry-interval",
        type=float,
        default=2.0,
        help="Segundos entre reintentos de conexion.",
    )
    return parser.parse_args()


def build_consumer(args: argparse.Namespace) -> KafkaConsumer:
    servers = [item.strip() for item in args.bootstrap_servers.split(",") if item.strip()]
    return KafkaConsumer(
        *args.topics,
        bootstrap_servers=servers,
        group_id=args.group_id,
        auto_offset_reset="earliest" if args.from_beginning else "latest",
        enable_auto_commit=True,
        value_deserializer=lambda value: value.decode("utf-8", errors="replace"),
        key_deserializer=lambda key: key.decode("utf-8", errors="replace") if key else None,
    )


def classify_message(text: str) -> str:
    lower = text.lower()
    if "unsc" in lower:
        return "UNSC"
    if "covenant" in lower:
        return "COVENANT"
    if "flood" in lower:
        return "FLOOD"
    if "forerunner" in lower:
        return "FORERUNNER"
    return "OTHER"


def render_report(topic_counts: dict, class_counts: dict, total_messages: int, started_at: float) -> None:
    elapsed_seconds = max(int(time.time() - started_at), 1)
    rate = total_messages / elapsed_seconds

    print("\n" + "=" * 72)
    print("RESUMEN DE CONSUMO")
    print("=" * 72)
    print(f"Mensajes totales: {total_messages} | Tiempo: {elapsed_seconds}s | Rate: {rate:.2f} msg/s")

    print("\nTopicos:")
    for topic, count in sorted(topic_counts.items()):
        bar = "#" * min(count, 40)
        print(f"- {topic:<28} {count:>5} {bar}")

    print("\nClasificacion:")
    for group, count in sorted(class_counts.items()):
        bar = "*" * min(count, 40)
        print(f"- {group:<28} {count:>5} {bar}")
    print("=" * 72 + "\n")


def handle_signal(signum, frame):
    del signum, frame
    global RUNNING
    RUNNING = False


def main() -> int:
    args = parse_args()

    signal.signal(signal.SIGINT, handle_signal)
    if hasattr(signal, "SIGTERM"):
        signal.signal(signal.SIGTERM, handle_signal)

    consumer = None
    for attempt in range(1, args.connect_retries + 1):
        try:
            consumer = build_consumer(args)
            break
        except NoBrokersAvailable:
            print(
                f"[Intento {attempt}/{args.connect_retries}] Kafka no disponible en {args.bootstrap_servers}."
            )
            time.sleep(args.retry_interval)

    if consumer is None:
        print("No fue posible conectar con Kafka. Verifica broker y puertos.")
        return 1

    print(f"Consumer Python conectado a {args.bootstrap_servers}")
    print(f"Topicos suscritos: {', '.join(args.topics)}")
    print(f"Group ID: {args.group_id}")

    started_at = time.time()
    total_messages = 0
    topic_counts = defaultdict(int)
    class_counts = defaultdict(int)

    try:
        while RUNNING:
            polled = consumer.poll(timeout_ms=args.timeout_ms)
            if not polled:
                continue

            for records in polled.values():
                for record in records:
                    total_messages += 1
                    topic_counts[record.topic] += 1

                    label = classify_message(record.value)
                    class_counts[label] += 1

                    ts = datetime.now(timezone.utc).astimezone().isoformat(timespec="seconds")
                    print(
                        f"[{ts}] topic={record.topic} partition={record.partition} "
                        f"offset={record.offset} key={record.key} class={label} msg={record.value}"
                    )

                    if total_messages % args.report_every == 0:
                        render_report(topic_counts, class_counts, total_messages, started_at)
    finally:
        if consumer is not None:
            consumer.close()

    render_report(topic_counts, class_counts, total_messages, started_at)
    print("Consumer Python finalizado correctamente.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
