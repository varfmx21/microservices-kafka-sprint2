import { useState } from "react";

export default function Producer() {
    const [message, setMessage] = useState("");

    const sendMessage = async () => {
    await fetch("http://localhost:8000/producer", {
        method: "POST",
        headers: {
        "Content-Type": "application/json"
    },
        body: JSON.stringify(message)
    });

    setMessage("");
    };

    return (
    <div style={{ padding: 20 }}>
        <h2>Kafka Producer</h2>

        <input
        value={message}
        onChange={(e) => setMessage(e.target.value)}
        placeholder="Escribe mensaje"
    />

    <button onClick={sendMessage}>
        Enviar a Kafka
    </button>
    </div>
    );
}