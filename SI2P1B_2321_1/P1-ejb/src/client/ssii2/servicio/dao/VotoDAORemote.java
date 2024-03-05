/**
 * Pr&aacute;ctricas de Sistemas Inform&aacute;ticos II
 * 
 * Implementacion de la interfaz de VISA utilizando como backend
 * una base de datos.
 * Implementa dos modos de acceso (proporcionados por la clase DBTester):
 * - Conexion directa: mediante instanciacion del driver y creacion de conexiones
 * - Conexion a traves de datasource: obtiene nuevas conexiones de un pool identificado
 *   a traves del nombre del datasource (DSN)
 *
 */

package ssii2.servicio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import jakarta.ejb.Remote;
import ssii2.servicio.*;
import ssii2.voto.VotoBean;

@Remote
public interface VotoDAORemote {
    public boolean compruebaCenso(CensoBean censo);
    public VotoBean registraVoto(VotoBean voto);	
    public VotoBean[] getVotos(String idProcesoElectoral);
    public int delVotos(String idProcesoElectoral);
    public boolean isDebug();
    public boolean isPrepared();
    public void setPrepared(boolean prepared); 
    public void setDebug(boolean debug);
    public boolean isDirectConnection();    
    public void setDirectConnection(boolean directConnection);
}    

