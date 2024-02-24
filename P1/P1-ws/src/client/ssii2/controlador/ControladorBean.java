package ssii2.controlador;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import java.io.Serializable;
import jakarta.faces.context.*;

import ssii2.voto.*;
import ssii2.interaccion.*;
import ssii2.voto.dao.VotoDAOWS;
import ssii2.servicio.VotoDAOWSService;

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

        VotoDAOWSService service = new VotoDAOWSService();
        VotoDAOWS dao = service.getVotoDAOWSPort();

	    try {
            // Traducimos la información del voto al formato del servicio web
            ssii2.servicio.VotoBean votoParaServicio = traducirVotoParaServicio(this.voto);
			
            // Llamamos al método registraVoto() del servicio web
            ssii2.servicio.VotoBean votoRegistrado = dao.registraVoto(votoParaServicio);
            
            // Actualizamos los atributos del voto con los valores devueltos por el servicio web
            this.voto.setIdVoto(votoRegistrado.getIdVoto());
            this.voto.setMarcaTiempo(votoRegistrado.getMarcaTiempo());
            this.voto.setCodigoRespuesta(votoRegistrado.getCodigoRespuesta());
            
            // Todo ha ido bien. Vamos a la página de respuesta
            if (this.interaccion.getDebug() == true) {
                this.escribirLog("¡Voto registrado correctamente!");
            }
            return "respuesta";
        } catch (Exception e) {
            // Manejamos la excepción
            e.printStackTrace(); // Aquí puedes implementar el manejo específico de la excepción
            return "error"; // Retornamos una página de error
        }
    }

    // Metodo que recibe la acción de borrar los votos de un proceso electoral
    // e interactua con la lógica de negocio para llevarlo a cabo

    public String borrarVotos() {
        try {
            VotoDAOWSService service = new VotoDAOWSService();
            VotoDAOWS dao = service.getVotoDAOWSPort();

            // Llamada al método del servicio web para borrar votos
            int votosBorrados = dao.delVotos(this.voto.getIdProcesoElectoral());

            // Almacenamos el número de votos borrados en la sesión para mostrarlo en la página de éxito
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .put("numVotosBorrados", String.valueOf(votosBorrados));

            return "borradook";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    // Metodo que recibe la acción de consultar los votos de un proceso electoral
    // e interactua con la lógica de negocio para llevarlo a cabo

    public String consultarVotos() {
        try {
            VotoDAOWSService service = new VotoDAOWSService();
            VotoDAOWS dao = service.getVotoDAOWSPort();

            // Llamada al método del servicio web para consultar votos
            List<ssii2.servicio.VotoBean> votos = dao.getVotos(this.voto.getIdProcesoElectoral());

            // Convertimos la lista de votos del servicio web a un array de VotoBean[]
            ArrayList<VotoBean> votosList = new ArrayList<>();
            for (ssii2.servicio.VotoBean v : votos) {
                VotoBean votoBean = new VotoBean();
                votoBean.setIdVoto(v.getIdVoto());
                votoBean.setMarcaTiempo(v.getMarcaTiempo());
                votoBean.setCodigoRespuesta(v.getCodigoRespuesta());
                // Otros atributos si es necesario

                votosList.add(votoBean);
            }
            VotoBean[] votosArray = votosList.toArray(new VotoBean[votosList.size()]);

            // Almacenamos los votos en la sesión para mostrarlos en la página listavotos.xhtml
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .put("votosObtenidos", votosArray);

            return "listadoVotos";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    // Método que escribe en el log del servidor

    private void escribirLog(String log) {
	    System.out.println("[LOG INFO]:" + log + "\n");
    }

    // Metodo que fija en contexto un mensaje de error para ser mostrado en una página xhtml
    
    private void setMensajeError(String mensaje) {
	    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("error", mensaje);
    }

	// Método que traduce un objeto VotoBean al formato del servicio web
    private ssii2.servicio.VotoBean traducirVotoParaServicio(VotoBean voto) {
        ssii2.servicio.CensoBean censo_nuevo = new ssii2.servicio.CensoBean();
        ssii2.servicio.VotoBean voto_nuevo = new ssii2.servicio.VotoBean();

        voto_nuevo.setIdCircunscripcion(voto.getIdCircunscripcion());
        voto_nuevo.setIdMesaElectoral(voto.getIdMesaElectoral());
        voto_nuevo.setIdProcesoElectoral(voto.getIdProcesoElectoral());
        voto_nuevo.setNombreCandidatoVotado(voto.getNombreCandidatoVotado());
        censo_nuevo.setNumeroDNI(voto.getCenso().getNumeroDNI());
        censo_nuevo.setFechaNacimiento(voto.getCenso().getFechaNacimiento());
        censo_nuevo.setNombre(voto.getCenso().getNombre());
        censo_nuevo.setCodigoAutorizacion(voto.getCenso().getCodigoAutorizacion());

        voto_nuevo.setCenso(censo_nuevo);
        return voto_nuevo;
    }
}

