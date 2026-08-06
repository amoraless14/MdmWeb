package com.example.monitoreo;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class GpsWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        String id = session.getId();

        MdmSocketHandler.registrarWeb(id, session);

        System.out.println("WEB GPS CONECTADA " + id);
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status) {

        MdmSocketHandler.eliminarWeb(
                session.getId()
        );

        System.out.println("WEB GPS DESCONECTADA");
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message) {

        // no usamos mensajes desde la web
    }
}