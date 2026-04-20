import { useState, useEffect } from "react";

export default function Producer() {
    const [message, setMessage] = useState("");
    const [forerunnerResponse, setForerunnerResponse] = useState("");

    useEffect(() => {
        const fetchForerunnerResponse = async () => {
            try {
                const response = await fetch("http://localhost:8000/producer", {
                    method: "GET"
                });
                const data = await response.text();
                setForerunnerResponse(data);
            } catch (error) {
                console.error("Error fetching forerunner response:", error);
            }
        };

        fetchForerunnerResponse();
        const interval = setInterval(fetchForerunnerResponse, 3000);
        
        return () => clearInterval(interval);
    }, []);

    const sendMessage = async (keyword) => {
        const fullMessage = message + " " + keyword;
        await fetch("http://localhost:8000/producer", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(fullMessage)
        });
        setMessage("");
    };

    return (
        <div style={{ padding: 20, fontFamily: 'Arial, sans-serif' }}>
            <h2>Kafka Producer</h2>

            <input
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                placeholder="Escribe mensaje principal"
                style={{
                    width: '100%',
                    padding: '10px',
                    marginBottom: '20px',
                    border: '1px solid #ccc',
                    borderRadius: '5px',
                    fontSize: '16px'
                }}
            />

            <div style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
                gap: '10px',
                marginBottom: '20px'
            }}>
                <button
                    onClick={() => sendMessage("unsc chief")}
                    style={{
                        padding: '15px',
                        backgroundColor: '#28a745',
                        color: 'white',
                        border: 'none',
                        borderRadius: '5px',
                        fontSize: '16px',
                        cursor: 'pointer',
                        transition: 'background-color 0.3s'
                    }}
                    
                    onMouseOver={(e) => e.target.style.backgroundColor = '#1e7e34'}
                    onMouseOut={(e) => e.target.style.backgroundColor = '#28a745'}
                >
                    UNSC Chief
                </button>

                <button
                    onClick={() => sendMessage("unsc cortana")}
                    style={{
                        padding: '15px',
                        backgroundColor: '#007bff',
                        color: 'white',
                        border: 'none',
                        borderRadius: '5px',
                        fontSize: '16px',
                        cursor: 'pointer',
                        transition: 'background-color 0.3s'
                    }}
                    onMouseOver={(e) => e.target.style.backgroundColor = '#0056b3'}
                    onMouseOut={(e) => e.target.style.backgroundColor = '#007bff'}
                >
                    UNSC Cortana
                </button>

                <button
                    onClick={() => sendMessage("covenant prophet")}
                    style={{
                        padding: '15px',
                        backgroundColor: '#dc3545',
                        color: 'white',
                        border: 'none',
                        borderRadius: '5px',
                        fontSize: '16px',
                        cursor: 'pointer',
                        transition: 'background-color 0.3s'
                    }}
                    onMouseOver={(e) => e.target.style.backgroundColor = '#c82333'}
                    onMouseOut={(e) => e.target.style.backgroundColor = '#dc3545'}
                >
                    Covenant Prophet
                </button>

                <button
                    onClick={() => sendMessage("flood gravemind")}
                    style={{
                        padding: '15px',
                        backgroundColor: '#ffc107',
                        color: 'black',
                        border: 'none',
                        borderRadius: '5px',
                        fontSize: '16px',
                        cursor: 'pointer',
                        transition: 'background-color 0.3s'
                    }}
                    onMouseOver={(e) => e.target.style.backgroundColor = '#e0a800'}
                    onMouseOut={(e) => e.target.style.backgroundColor = '#ffc107'}
                >
                    Flood Gravemind
                </button>

                
            </div>

            <div style={{ marginTop: '20px' }}>
                <label style={{ display: 'block', marginBottom: '8px', fontWeight: 'bold', fontSize: '16px' }}>
                    forerunner response:
                </label>
                <input
                    type="text"
                    value={forerunnerResponse}
                    readOnly
                    placeholder="Esperando respuesta..."
                    style={{
                        width: '100%',
                        padding: '10px',
                        border: '1px solid #ccc',
                        borderRadius: '5px',
                        fontSize: '16px',
                        backgroundColor: '#686868',
                        cursor: 'not-allowed'
                    }}
                />
            </div>
        </div>

    );
}
