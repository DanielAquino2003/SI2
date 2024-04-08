package ssii2.controlador;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import java.io.Serializable;
import jakarta.faces.context.*;

import ssii2.voto.*;
import ssii2.interaccion.*;
import ssii2.voto.dao.VotoDAO;

/*
 * Managed Bean de ambito de sesion que recoge los datos de la votacion.
 */

@Named // Permite acceder al bean a traves del EL
@SessionScoped  // Hace que el bean persista en la sessión

public class ControladorBean implements Serializable {

    // Referencia obtenida por inyección
    // a un bean de sesión con la información del voto
    
    @Inject private VotoBean voto; 

    // Referencia obtenida por inyección
    // a un bean de sesión con la información de la interacción
    // con el modelo de negocio 
 
    @Inject private InteraccionBean interaccion; 

    public ControladorBean() {
    }

    // Metodo que recibe la acción de enviar el voto e interactua con la lógica de negocio
    // encargada de registrar el voto

    public String enviarVoto() {

	    if (this.interaccion.getDebug() == true) {
	    	this.escribirLog("Solicitado el registro del voto.");
	    }
	    /* Instanciamos el objeto que presta la lógica de negocio de la aplicación */

            VotoDAO dao = new VotoDAO();

	    dao.setDirectConnection(this.interaccion.getConexionDirecta());
	    dao.setDebug(this.interaccion.getDebug());
	    dao.setPrepared(this.interaccion.getPreparedStatements());

	    /* Comprobamos que el ciudadano está en el censo */

	    if (dao.compruebaCenso(this.voto.getCenso()) == false) {

		String error_msg = "¡El ciudadano no se encuentra en el censo!";

	    	if (this.interaccion.getDebug() == true) {
	    		this.escribirLog(error_msg);
		}

		this.setMensajeError(error_msg);
	    	return "error";
	    } 

	    /* Registramos el voto */
	    
	    if (dao.registraVoto(this.voto) == false) {

		String error_msg = "¡No se ha podido registrar el voto!";

	    	if (this.interaccion.getDebug() == true) {
	    		this.escribirLog(error_msg);
		}

		this.setMensajeError(error_msg);
	    	return "error";

	    }

	    /* Todo ha ido bien. Vamos a la página de respuesta */

	    if (this.interaccion.getDebug() == true) {
	    	this.escribirLog("¡Voto registrado correctamente!");
	    }

	    return "respuesta";
    }

    // Metodo que recibe la acción de borrar los votos de un proceso electoral
    // e interactua con la lógica de negocio para llevarlo a cabo

    public String borrarVotos() {

	    /* Instanciamos el objeto que presta la lógica de negocio de la aplicación */

            VotoDAO dao = new VotoDAO();

	    // Borramos los votos

	    int votos_borrados = dao.delVotos(this.voto.getIdProcesoElectoral());

	    // Comprobamos el nº de votos borrados

	    if (votos_borrados != 0) {
	    	FacesContext.getCurrentInstance().getExternalContext().getSessionMap().
			put("numVotosBorrados", String.valueOf(votos_borrados));

	    	return "borradook";
	    } 
	    
	    String error_msg = "¡No se ha podido borrar ningún voto!";
	    this.setMensajeError(error_msg);

	    return "error";
    }

    // Metodo que recibe la acción de consultar los votos de un proceso electoral
    // e interactua con la lógica de negocio para llevarlo a cabo

    public String consultarVotos() {

	    /* Instanciamos el objeto que presta la lógica de negocio de la aplicación */

            VotoDAO dao = new VotoDAO();

	    // Obtenemos los votos

	    VotoBean[] votos = dao.getVotos(this.voto.getIdProcesoElectoral());

	    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().
			put("votosObtenidos", votos);

	    return "listadoVotos";
    }

    // Método que escribe en el log del servidor

    private void escribirLog(String log) {
	    System.out.println("[LOG INFO]:" + log + "\n");
    }

    // Metodo que fija en contexto un mensaje de error para ser mostrado en una página xhtml
    
    private void setMensajeError(String mensaje) {
	    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("error", mensaje);
    }
}

