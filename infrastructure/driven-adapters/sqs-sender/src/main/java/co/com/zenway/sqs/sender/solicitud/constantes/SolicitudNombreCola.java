package co.com.zenway.sqs.sender.solicitud.constantes;

public enum SolicitudNombreCola {
    COLA_NOTIFICACION("colaNotificacion"),
    COLA_REGISTRO_SOLICITUD("colaRegistroSolicitud");

    private final String key;

    SolicitudNombreCola(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
