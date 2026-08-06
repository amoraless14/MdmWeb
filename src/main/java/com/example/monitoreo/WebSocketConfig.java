package com.example.monitoreo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import repository.TabletRepository;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final TabletRepository tabletRepository;

    public WebSocketConfig(TabletRepository tabletRepository) {
        this.tabletRepository = tabletRepository;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(
                new MdmSocketHandler(tabletRepository),
                "/ws-mdm/{id}").setAllowedOrigins("*");

        registry.addHandler(
                new GpsWebSocketHandler(),
                "/ws-gps").setAllowedOrigins("*");
    }
}