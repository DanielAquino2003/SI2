package ssii2.servicio;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

/*
 * Managed Bean de ambito de sesion que recoge los datos del censo de la persona que vota.
 */

public class CensoBean implements Serializable {

    private String numeroDNI;
    private String nombre;
    private String fechaNacimiento;
    private String codigoAutorizacion;

    public CensoBean() {
    }

    public String getNumeroDNI() {
        return numeroDNI;
    }

    public void setNumeroDNI(String numeroDNI) {
        this.numeroDNI= numeroDNI;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCodigoAutorizacion() {
        return codigoAutorizacion;
    }

    public void setCodigoAutorizacion(String codigoAutorizacion) {
        this.codigoAutorizacion = codigoAutorizacion;
    }
}

