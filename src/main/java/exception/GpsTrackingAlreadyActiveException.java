package exception;

public class GpsTrackingAlreadyActiveException extends RuntimeException {

    public GpsTrackingAlreadyActiveException() {
        super("Este dispositivo ya está siendo rastreado en tiempo real.");
    }

}