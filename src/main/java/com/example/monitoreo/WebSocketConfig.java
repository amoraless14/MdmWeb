package com.example.monitoreo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import repository.TabletRepository;
import service.TareaProgramadaService;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final TabletRepository tabletRepository;
    private final TareaProgramadaService tareaProgramadaService;

    public WebSocketConfig(
            TabletRepository tabletRepository,
            TareaProgramadaService tareaProgramadaService) {
        this.tabletRepository = tabletRepository;
        this.tareaProgramadaService = tareaProgramadaService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(
                new MdmSocketHandler(
                        tabletRepository,
                        tareaProgramadaService),
                "/ws-mdm/{id}").setAllowedOrigins("*");

        registry.addHandler(
                new GpsWebSocketHandler(),
                "/ws-gps").setAllowedOrigins("*");
    }
}