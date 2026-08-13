package com.example.monitoreo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import Entidad.Tablet;
import repository.TabletRepository;
import java.util.Map;
import java.util.concurrent.*;
import java.util.Set;

public class MdmSocketHandler extends TextWebSocketHandler {

    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private static final Map<String, CompletableFuture<Boolean>> ackPendientes = new ConcurrentHashMap<>();

    private static final Map<String, CompletableFuture<Boolean>> updatePendientes = new ConcurrentHashMap<>();

    private static final Map<String, String> updateMensajes = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final TabletRepository tabletRepository;

    public MdmSocketHandler(TabletRepository tabletRepository) {
        this.tabletRepository = tabletRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        String uri = session.getUri().toString();
        String id = uri.substring(uri.lastIndexOf('/') + 1);

        sessions.put(id, session);

        System.out.println("Tablet conectada al túnel: " + id);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

        sessions.values().remove(session);

        System.out.println("Tablet desconectada del túnel");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {

        try {

            String payload = message.getPayload();
            System.out.println("MENSAJE DESDE TABLET: " + payload);

            JsonNode json = objectMapper.readTree(payload);

            if ("ack".equals(json.path("type").asText())) {

                String command = json.path("command").asText();
                String status = json.path("status").asText();
                String mensaje = json.path("message").asText("");

                String deviceId = obtenerDeviceIdPorSession(session);

                System.out.println(
                        "ACK RECIBIDO TABLET " + deviceId
                                + " COMANDO " + command
                                + " ESTADO " + status
                                + " MENSAJE " + mensaje);

                String key = deviceId + ":" + command;

                // ACTUALIZACIÓN DEL MDM
                if ("update_app".equals(command)) {

                    if ("downloading".equals(status)
                            || "received".equals(status)) {

                        System.out.println(
                                "Tablet " + deviceId
                                        + " está procesando la actualización");

                        // NO completar todavía
                        return;
                    }

                    CompletableFuture<Boolean> future = updatePendientes.remove(key);

                    if ("success".equals(status)) {

                        updateMensajes.put(key, mensaje);

                        if (future != null) {
                            future.complete(true);
                        }

                    } else if ("error".equals(status)
                            || status.startsWith("error_")) {

                        updateMensajes.put(key, mensaje);

                        if (future != null) {
                            future.complete(false);
                        }
                    }

                    return;
                }

                // RESTO DE COMANDOS
                CompletableFuture<Boolean> future = ackPendientes.remove(key);

                if (future != null) {
                    future.complete(true);
                }
            }

            if ("gps_update".equals(json.path("type").asText())) {

                String deviceId = obtenerDeviceIdPorSession(session);

                String gpsJson = objectMapper.createObjectNode()
                        .put("type", "gps_update")
                        .put("tabletId", deviceId)
                        .put("latitude", json.path("latitude").asDouble())
                        .put("longitude", json.path("longitude").asDouble())
                        .put("accuracy", json.path("accuracy").asDouble())
                        .toString();

                broadcastToWeb(gpsJson);

                System.out.println("GPS TIEMPO REAL TABLET " + deviceId);
            }

            if ("policy_sync".equals(json.path("type").asText())) {

                String deviceId = obtenerDeviceIdPorSession(session);

                System.out.println("POLICY_SYNC RECIBIDO DE TABLET " + deviceId);
                System.out.println(payload);

                Tablet tablet = tabletRepository.findById(
                        Long.valueOf(deviceId)).orElse(null);

                if (tablet != null) {

                    tablet.setRestricciones(
                            json.path("restricciones").toString());

                    tablet.setAppsBloqueadas(
                            json.path("apps_bloqueadas").asText(""));

                    tablet.setConfigReinicio(
                            json.path("config_reinicio").asText(""));

                    tablet.setUrlsPermitidas(
                            json.path("urls_permitidas").toString());

                    tabletRepository.save(tablet);

                    System.out.println("POLITICAS ACTUALIZADAS DESDE TABLET " + deviceId);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean enviarOrdenConAck(String deviceId, String json, String command) {

        try {

            WebSocketSession session = sessions.get(deviceId);

            if (session == null || !session.isOpen()) {
                System.out.println("Tablet " + deviceId + " no está conectada al túnel");
                return false;
            }

            String key = deviceId + ":" + command;

            CompletableFuture<Boolean> future = new CompletableFuture<>();
            ackPendientes.put(key, future);

            session.sendMessage(new TextMessage(json));

            System.out.println("Orden enviada a tablet " + deviceId);

            return future.get(10, TimeUnit.SECONDS);

        } catch (TimeoutException e) {
            System.out.println("La tablet " + deviceId + " no confirmó el comando a tiempo");
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean enviarActualizacionConAck(
            String deviceId,
            String apkUrl) {

        String command = "update_app";
        String key = deviceId + ":" + command;

        try {

            WebSocketSession session = sessions.get(deviceId);

            if (session == null || !session.isOpen()) {

                System.out.println(
                        "Tablet " + deviceId
                                + " no está conectada para actualizar");

                return false;
            }

            CompletableFuture<Boolean> future = new CompletableFuture<>();

            updatePendientes.put(key, future);

            String json = objectMapper
                    .createObjectNode()
                    .put("command", "update_app")
                    .put("url", apkUrl)
                    .toString();

            session.sendMessage(
                    new TextMessage(json));

            System.out.println(
                    "ACTUALIZACIÓN ENVIADA A TABLET "
                            + deviceId
                            + " URL: "
                            + apkUrl);

            // Esperamos descarga + instalación real
            return future.get(
                    5,
                    TimeUnit.MINUTES);

        } catch (TimeoutException e) {

            updatePendientes.remove(key);

            System.out.println(
                    "Tablet " + deviceId
                            + " no confirmó la instalación en 5 minutos");

            return false;

        } catch (Exception e) {

            updatePendientes.remove(key);
            e.printStackTrace();

            return false;
        }
    }

    public static void enviarOrden(String deviceId, String json) {

        try {

            WebSocketSession session = sessions.get(deviceId);

            if (session != null && session.isOpen()) {

                session.sendMessage(new TextMessage(json));

                System.out.println("Orden enviada a tablet " + deviceId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String obtenerDeviceIdPorSession(WebSocketSession session) {

        for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
            if (entry.getValue().equals(session)) {
                return entry.getKey();
            }
        }

        return "desconocido";
    }

    private static final Map<String, WebSocketSession> webSessions = new ConcurrentHashMap<>();

    public static void registrarWeb(String id, WebSocketSession session) {

        webSessions.put(id, session);

        System.out.println("WEB GPS CONECTADA " + id);
    }

    public static void eliminarWeb(String id) {

        webSessions.remove(id);
    }

    public static void broadcastToWeb(String json) {

        webSessions.values().forEach(session -> {

            try {

                if (session.isOpen()) {

                    session.sendMessage(
                            new TextMessage(json));
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        });
    }

    public static void solicitarActualizacion(String deviceId) {

        String json = """
                {
                    "pending_command":"sync_status"
                }
                """;

        enviarOrden(deviceId, json);
    }

    public static Set<String> obtenerTabletsConectadas() {
        return sessions.keySet();
    }
}