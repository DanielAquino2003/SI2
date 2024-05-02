package ssii2.interaccion;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

/*
 * Managed Bean de ambito de sesion que especificaŕa como será la interacción con la lógica de negocio.
 * Toma valores por defecto.
 */

@Named // Permite acceder al bean a traves del EL
@SessionScoped  // Hace que el bean persista en la sessión
public class InteraccionBean implements Serializable {

    private boolean debug = false;
    private boolean conexionDirecta = false;
    private boolean preparedStatements = true;

    public InteraccionBean() {
    }

    public boolean getDebug() {
        return this.debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug ;
    }

    public boolean getConexionDirecta() {
        return conexionDirecta;
    }

    public void setConexionDirecta(boolean conexionDirecta) {
        this.conexionDirecta = conexionDirecta;
    }

    public void setPreparedStatements(boolean preparedStatements) {
        this.preparedStatements = preparedStatements;
    }

    public boolean getPreparedStatements() {
        return this.preparedStatements;
    }

}

