package co.com.zenway.sqs.sender.solicitud.constantes;

public enum SolicitudNombreCola {
    COLA_NOTIFICACION("colaNotificacion"),
    COLA_REGISTRO_SOLICITUD("colaRegistroSolicitud"),
    COLA_RESULTADO_AUTOVALIDACION("colaResultadoAutovalidacion");

    private final String key;

    SolicitudNombreCola(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
