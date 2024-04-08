package ssii2.voto;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import java.io.Serializable;
import ssii2.voto.CensoBean;

/*
 * Managed Bean de ambito de sesion que recoge los datos del censo de la persona que vota.
 */

@Named // Permite acceder al bean a traves del EL
@SessionScoped  // Hace que el bean persista en la sessión
public class VotoBean implements Serializable {

    private String idVoto;
    private String idCircunscripcion;
    private String idMesaElectoral;
    private String idProcesoElectoral;
    private String nombreCandidatoVotado;
    private String codigoRespuesta;
    private String marcaTiempo;

    @Inject CensoBean censo; // referencia al censo de la sesión obtenida por inyeccion

    public VotoBean() {
    }

    public String getIdVoto() {
        return idVoto;
    }

    public void setIdVoto(String idVoto) {
        this.idVoto = idVoto;
    }

    public String getIdCircunscripcion() {
        return idCircunscripcion;
    }

    public void setIdCircunscripcion(String idCircunscripcion) {
        this.idCircunscripcion = idCircunscripcion;
    }

    public String getIdMesaElectoral() {
        return idMesaElectoral;
    }

    public void setIdMesaElectoral(String idMesaElectoral) {
        this.idMesaElectoral = idMesaElectoral;
    }

    public String getMarcaTiempo() {
        return marcaTiempo;
    }

    public void setMarcaTiempo(String marcaTiempo) {
        this.marcaTiempo = marcaTiempo;
    }

    public String getIdProcesoElectoral() {
        return idProcesoElectoral;
    }

    public void setIdProcesoElectoral(String idProcesoElectoral) {
        this.idProcesoElectoral = idProcesoElectoral;
    }

    public void setNombreCandidatoVotado(String nombreCandidatoVotado) {
        this.nombreCandidatoVotado = nombreCandidatoVotado;
    }

    public String getNombreCandidatoVotado() {
        return this.nombreCandidatoVotado;
    }

    public void setCodigoRespuesta(String codigoRespuesta) {
        this.codigoRespuesta = codigoRespuesta;
    }

    public String getCodigoRespuesta() {
        return this.codigoRespuesta;
    }

    public CensoBean getCenso() {
        return this.censo;
    }

    public void setCenso(CensoBean censo) {
        this.censo = censo;
    }

}

