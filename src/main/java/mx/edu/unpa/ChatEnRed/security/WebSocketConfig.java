package mx.edu.unpa.ChatEnRed.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilitamos un broker simple en memoria
        // Los clientes se suscribirán a rutas que empiecen con "/topic" o "/queue"
        config.enableSimpleBroker("/topic", "/queue");

        // Prefijo para los mensajes que van DEL cliente AL servidor (si usáramos @MessageMapping)
        config.setApplicationDestinationPrefixes("/app");
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Punto de conexión (Handshake). Angular se conectará aquí.
        // setAllowedOriginPatterns("*") es vital para evitar errores de CORS en desarrollo.
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // Habilita fallback por si el navegador no soporta WS nativo
    }
}
