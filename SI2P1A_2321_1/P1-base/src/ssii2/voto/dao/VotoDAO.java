/**
 * Pr&aacute;ctricas de Sistemas Inform&aacute;ticos II
 * 
 * Implementacion de la interfaz de voto utilizando como backend
 * una base de datos.
 * Implementa dos modos de acceso (proporcionados por la clase DBTester):
 * - Conexion directa: mediante instanciacion del driver y creacion de conexiones
 * - Conexion a traves de datasource: obtiene nuevas conexiones de un pool identificado
 *   a traves del nombre del datasource (DSN)
 *
 */

package ssii2.voto.dao;

import ssii2.voto.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.time.LocalDate;



/**
 * @author jaime, daniel
 */
public class VotoDAO extends DBTester {

    private boolean debug = false;

    /**
     * TODO: Declarar un atributo booleano "prepared"
     * que indique si hay que usar prepared statements o no
     * Utilizar statements preparados o no
     */
    private boolean prepared = true;
    /***********************************************************/

    /* Para prepared statements, usamos cadenas constantes
     * Esto agiliza el trabajo del pool de statements */
    private static final String DELETE_VOTOS_QRY =
        "delete from voto " +
        "where idProcesoElectoral=?";
    private static final String SELECT_VOTOS_QRY =
        "select * from voto " +
        "where idProcesoElectoral=?";

    /**
     * TODO: Declarar consultas SQL estaticas
     * para uso con prepared statement
     * Hay que convertir las consultas de getQryXXX()
     */
    /*private static final String ...*/
    /**************************************************/
    private static final String SELECT_CENSO_QRY =
                    "select * from censo " +
                    "where numeroDNI=? " +
                    " and nombre=? " +
                    " and fechaNacimiento=? " +
                    " and codigoAutorizacion=? ";

    private static final String INSERT_VOTO_QRY =
                    "insert into voto(" +
                    "idCircunscripcion,idMesaElectoral,idProcesoElectoral,nombreCandidatoVotado,numeroDNI)" +
                    " values (?,?,?,?,?)";

    private static final String SELECT_VOTO_TRANSACCION_QRY =
                    "select idVoto, codRespuesta, marcaTiempo" +
                    " from voto " +
                    " where idProcesoElectoral = ?" +
                    " and numeroDNI = ?";
    /**************************************************/

    
    /**
     * Constructor de la clase     
     */
    public VotoDAO() {
        return;
    }


    /**
     *  getQryCompruebaCenso
     */
    private String getQryCompruebaCenso(CensoBean censo) {
        String qry = "select * from censo "
                    + "where numeroDNI ='" + censo.getNumeroDNI()
                    + "' and nombre='" + censo.getNombre()
                    + "' and fechaNacimiento='" + censo.getFechaNacimiento()
                    + "' and codigoAutorizacion='" + censo.getCodigoAutorizacion() + "'";
        return qry;
    }
    
    /**
     *  getQryInsertVoto
     */
    private String getQryInsertVoto(VotoBean voto) {
        String qry = "insert into voto("
                    + "idCircunscripcion,"
                    + "idMesaElectoral,idProcesoElectoral,"
                    + "nombreCandidatoVotado, numeroDNI)"
                    + " values ("
                    + "'" + voto.getIdCircunscripcion() + "'," 
		    + "'" + voto.getIdMesaElectoral() + "'," 
                    + "'" + voto.getIdProcesoElectoral() + "'," 
                    + "'" + voto.getNombreCandidatoVotado() + "'," 
                    + "'" + voto.getCenso().getNumeroDNI() + "')";
        return qry;
    }

    /**
     *  getQryBuscaVotoTransaccion()
     */
    private String getQryBuscaVotoTransaccion(VotoBean voto) {
        String qry = "select idVoto, codRespuesta, marcaTiempo"
                        + " from voto "
                        + " where idProcesoElectoral= '" + voto.getIdProcesoElectoral()
                        + "'   and numeroDNI = '" + voto.getCenso().getNumeroDNI() + "'";
        return qry;
    }


    /**
     *
     * Comprobacion de la tarjeta
     * @param tarjeta Objeto con toda la informacion de la tarjeta
     * @return true si la comprobacion contra el censo contenido en
     *         en la tabla CENSO fue satisfactoria, false en caso contrario     */

    public boolean compruebaCenso(CensoBean censo) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        boolean ret = false;
        String qry = null;

        // TODO: Utilizar en funcion de isPrepared()
        PreparedStatement pstmt = null;

        try {

            // Crear una conexion u obtenerla del pool
            con = getConnection();

            // Se busca la ocurrencia de la información del censo en la tabla

            if (isPrepared() == true) {
               String select  = SELECT_CENSO_QRY;
               errorLog(select);
               pstmt = con.prepareStatement(select);
               pstmt.setString(1, censo.getNumeroDNI());
               pstmt.setString(2, censo.getNombre());
               pstmt.setString(3, censo.getFechaNacimiento());
               pstmt.setString(4, censo.getCodigoAutorizacion());
               rs = pstmt.executeQuery();               
            } else {
            	stmt = con.createStatement();
            	qry = getQryCompruebaCenso(censo);
            	errorLog(qry);
            	rs = stmt.executeQuery(qry);
            } 
            
            /* Si hay siguiente registro, la info sobre el censo es valida OK */
            ret = rs.next();

	    // Comprobamos año censo actual

	    if (rs.getString("anioCenso").compareTo(String.valueOf(LocalDate.now().getYear())) != 0) {
            	errorLog("¡El censo no está actualizado!");
		ret = false;
	    }

        } catch (Exception ee) {
            errorLog(ee.toString());
            ret = false;
        } finally {
            try {
                if (rs != null) {
                    rs.close(); rs = null;
                }
                if (stmt != null) {
                    stmt.close(); stmt = null;
                }
                if (pstmt != null) {
                    pstmt.close(); pstmt = null;
                }
                if (con != null) {
                    closeConnection(con); con = null;
                }
            } catch (SQLException e) {
            }
        }

        return ret;

    }

    /**
     * Registra el voto en la base de datos
     * @param voto
     * @return
     */
    public synchronized boolean registraVoto(VotoBean voto) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        boolean ret = false;
        String codRespuesta = "999"; // En principio, voto invalido

        PreparedStatement pstmt = null;

        // Comprobamos campos voto
	
        if (voto.getIdCircunscripcion() == null || voto.getIdMesaElectoral() == null ||
            voto.getIdProcesoElectoral() == null || voto.getNombreCandidatoVotado() == null ||
	    voto.getCenso() == null || voto.getCenso().getNumeroDNI() == null) {
            errorLog("¡El voto tiene campos vacíos!");
           return false;
        }

        // Registrar el voto en la base de datos
	
        try {

            // Obtener conexion
            con = getConnection();

            // Insertar en la base de datos el voto

            if (isPrepared() == true) {
               String insert  = INSERT_VOTO_QRY;
               errorLog(insert);
               pstmt = con.prepareStatement(insert);
               pstmt.setString(1, voto.getIdCircunscripcion());
               pstmt.setString(2, voto.getIdMesaElectoral());
               pstmt.setString(3, voto.getIdProcesoElectoral());
               pstmt.setString(4, voto.getNombreCandidatoVotado());
               pstmt.setString(5, voto.getCenso().getNumeroDNI());
               ret = false;

               if (!pstmt.execute() && pstmt.getUpdateCount() == 1) {
                 ret = true;
               }

            } else {            

            	stmt = con.createStatement();
            	String insert = getQryInsertVoto(voto);
            	errorLog(insert);
            	ret = false;

            	if (!stmt.execute(insert) && stmt.getUpdateCount() == 1) {
                	ret = true;
		}
            }

            // Obtener id.voto
	    
            if (ret) {                

                if (isPrepared() == true) {
                    String select = SELECT_VOTO_TRANSACCION_QRY;
                    errorLog(select);
                    pstmt = con.prepareStatement(select);
                    pstmt.setString(1, voto.getIdProcesoElectoral());
                    pstmt.setString(2, voto.getCenso().getNumeroDNI());
                    rs = pstmt.executeQuery();
                } else {
                    String select = getQryBuscaVotoTransaccion(voto);
                    errorLog(select);
                    rs = stmt.executeQuery(select);
                }

                if (rs.next()) {

		    // Completamos la información que se genera al insertar el 
		    // voto en la base de datos
		    
                    voto.setIdVoto(String.valueOf(rs.getInt("idVoto")));
                    voto.setCodigoRespuesta(rs.getString("codRespuesta"));
                    voto.setMarcaTiempo(rs.getString("marcaTiempo"));

                } else {
                    ret = false;
                }

            }

        } catch (Exception e) {
            errorLog(e.toString());
            ret = false;
        } finally {
            try {
                if (rs != null) {
                    rs.close(); rs = null;
                }
                if (stmt != null) {
                    stmt.close(); stmt = null;
                }
                if (pstmt != null) {
                    pstmt.close(); pstmt = null;
                }
                if (con != null) {
                    closeConnection(con); con = null;
                }
            } catch (SQLException e) {
            }
        }

        return ret;
    }


    /**
     * Buscar los votos asociados a un proceso electoral
     * @param idProcesoElectoral
     * @return
     */

    public VotoBean[] getVotos(String idProcesoElectoral) {

        PreparedStatement pstmt = null;
        Connection pcon = null;
        ResultSet rs = null;
        VotoBean[] ret = null;
        ArrayList<VotoBean> votos = null;
        String qry = null;

        try {

            // Crear una conexion u obtenerla del pool
            pcon = getConnection();
            qry = SELECT_VOTOS_QRY;
            errorLog(qry + "[idProcesoElectoral=" + idProcesoElectoral + "]");

            // La preparacion del statement
            // es automaticamente tomada de un pool en caso
            // de que ya haya sido preparada con anterioridad
	    
            pstmt = pcon.prepareStatement(qry);
            pstmt.setString(1, idProcesoElectoral);
            rs = pstmt.executeQuery();

            votos = new ArrayList<VotoBean>();

            while (rs.next()) {
                CensoBean c = new CensoBean();
                VotoBean v = new VotoBean();
                v.setIdVoto(rs.getString("idVoto"));
                v.setIdCircunscripcion(rs.getString("idCircunscripcion"));
                v.setIdMesaElectoral(rs.getString("idMesaElectoral"));
                v.setIdProcesoElectoral(rs.getString("idProcesoElectoral"));
                v.setNombreCandidatoVotado(rs.getString("nombreCandidatoVotado"));
                v.setMarcaTiempo(rs.getString("marcaTiempo"));
                v.setCodigoRespuesta(rs.getString("codRespuesta"));

                votos.add(v);
            }

            ret = new VotoBean[ votos.size() ];
            ret = votos.toArray(ret);

            // Cerramos / devolvemos la conexion al pool
	    
            pcon.close();

        } catch (Exception e) {
            errorLog(e.toString());
        } finally {
            try {
                if (rs != null) {
                    rs.close(); rs = null;
                }
                if (pstmt != null) {
                    pstmt.close(); pstmt = null;
                }
                if (pcon != null) {
                    closeConnection(pcon); pcon = null;
                }
            } catch (SQLException e) {
            }
        }

        return ret;
    }

    // Borrar los votos asociados a un proceso electoral
    /**
     *
     * @param idProcesoElectoral
     * @return numero de registros afectados
     */

    public int delVotos(String idProcesoElectoral) {

        PreparedStatement pstmt = null;
        Connection pcon = null;
        ResultSet rs = null;
        int ret = 0;
        String qry = null;

        try {

            // Crear una conexion u obtenerla del pool
            pcon = getConnection();
            qry = DELETE_VOTOS_QRY;
            errorLog(qry + "[idProcesoElectoral=" + idProcesoElectoral + "]");

            // La preparacion del statement
            // es automaticamente tomada de un pool en caso
            // de que ya haya sido preparada con anterioridad
	    
            pstmt = pcon.prepareStatement(qry);
            pstmt.setString(1, idProcesoElectoral);
            ret = pstmt.executeUpdate();

            // Cerramos / devolvemos la conexion al pool
            pcon.close();

        } catch (Exception e) {
            errorLog(e.toString());

        } finally {
            try {
                if (rs != null) {
                    rs.close(); rs = null;
                }
                if (pstmt != null) {
                    pstmt.close(); pstmt = null;
                }
                if (pcon != null) {
                    closeConnection(pcon); pcon = null;
                }
            } catch (SQLException e) {
            }
        }

        return ret;
    }

    /**
     * TODO: Metodos isPrepared() y setPrepared()
     */
    /********************************************************/
    public boolean isPrepared() {
        return prepared;
    }

    public void setPrepared(boolean prepared) {
        this.prepared = prepared;
    }
    /********************************************************/

    /**
     * @return the debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Imprime traza de depuracion
     */
    private void errorLog(String error) {
        if (isDebug())
            System.err.println("[directConnection=" + this.isDirectConnection() +"] " +
                               error);
    }

}
