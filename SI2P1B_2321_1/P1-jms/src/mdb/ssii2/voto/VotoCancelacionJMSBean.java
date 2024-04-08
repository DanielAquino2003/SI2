/**
 * Pr&aacute;ctricas de Sistemas Inform&aacute;ticos II
 * VisaCancelacionJMSBean.java
 */

package ssii2.voto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.ejb.EJBException;
import jakarta.ejb.MessageDriven;
import jakarta.ejb.MessageDrivenContext;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.jms.MessageListener;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import jakarta.jms.JMSException;
import jakarta.annotation.Resource;
import java.util.logging.Logger;

/**
 * @author jaime y daniel
 */
@MessageDriven(mappedName = "jms/VotosQueue")
public class VotoCancelacionJMSBean extends DBTester implements MessageListener {
  static final Logger logger = Logger.getLogger("VotoCancelacionJMSBean");
  @Resource
  private MessageDrivenContext mdc;

  private static final String UPDATE_CANCELA_QRY = null;
   // TODO : Definir UPDATE sobre la tabla votos para poner
   // codRespuesta a 999 dado un código de idVoto

  private static final String SELECT_CODIGO_RESPUESTA =
                    "SELECT codRespuesta " +
                    "FROM voto " +
                    "WHERE idVoto = ?";

  private static final String UPDATE_CODIGO_RESPUESTA =
                    "UPDATE voto " +
                    "SET codRespuesta = 999 " +
                    "WHERE idVoto = ?";

  private static final String UPDATE_NUMERO_VOTOS_RESTANTES =
                    "UPDATE censo " +
                    "SET numeroVotosRestantes = numeroVotosRestantes + 1 " +
                    "WHERE numeroDNI = (SELECT numeroDNI FROM voto WHERE idVoto = ?)";

  public VotoCancelacionJMSBean() {
  }

  // TODO : Método onMessage de ejemplo
  // Modificarlo para ejecutar el UPDATE definido más arriba,
  // asignando el idVoto a lo recibido por el mensaje
  // Para ello conecte a la BD, prepareStatement() y ejecute correctamente
  // la actualización
  public void onMessage(Message inMessage) {
      TextMessage msg = null;

      try {
          if (inMessage instanceof TextMessage) {
              msg = (TextMessage) inMessage;
              logger.info("MESSAGE BEAN: Message received: " + msg.getText());
              String idVoto = msg.getText();

              Connection con = getConnection();
              
              PreparedStatement selectStmt = con.prepareStatement(SELECT_CODIGO_RESPUESTA);
              selectStmt.setInt(1,Integer.parseInt(idVoto));
              ResultSet rs = selectStmt.executeQuery();
              String codRespuesta = "";
              if (rs.next()) {
                  codRespuesta = rs.getString("codRespuesta");
              }
              rs.close();
              selectStmt.close();
              if (codRespuesta.equals("000")) {
                  PreparedStatement updateRespStmt = con.prepareStatement(UPDATE_CODIGO_RESPUESTA);
                  updateRespStmt.setInt(1, Integer.parseInt(idVoto));
                  updateRespStmt.executeUpdate();
                  updateRespStmt.close();

                  PreparedStatement updateVotosStmt = con.prepareStatement(UPDATE_NUMERO_VOTOS_RESTANTES);
                  updateVotosStmt.setInt(1, Integer.parseInt(idVoto));
                  updateVotosStmt.executeUpdate();
                  updateVotosStmt.close();
              }
              con.close();
          } else {
              logger.warning(
                      "Message of wrong type: "
                      + inMessage.getClass().getName());
          }
      } catch (JMSException e) {
          e.printStackTrace();
          mdc.setRollbackOnly();
      } catch (Throwable te) {
          te.printStackTrace();
      }
  }


}
