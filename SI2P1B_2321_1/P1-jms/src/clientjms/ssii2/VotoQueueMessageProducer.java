package ssii2;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.Queue;
import jakarta.jms.Connection;
import jakarta.jms.Session;
import jakarta.jms.MessageProducer;
import jakarta.jms.TextMessage;
import jakarta.jms.JMSException;
import jakarta.annotation.Resource;
import jakarta.jms.QueueBrowser;
import java.util.Enumeration;
import javax.naming.InitialContext;

public class VotoQueueMessageProducer {

    // TODO: Anotar los siguientes objetos para
    // conectar con la connection factory y con la cola
    // definidas en el enunciado

    @Resource(mappedName = "jms/VotoConnectionFactory")
    private static ConnectionFactory connectionFactory;

    @Resource(mappedName = "jms/VotosQueue")
    private static Queue queue;

    //private static ConnectionFactory connectionFactory;
    //private static Queue queue;

    // Método de prueba
    public static void browseMessages(Session session)
    {
      try
      {
        Enumeration messageEnumeration;
        TextMessage textMessage;  
        QueueBrowser browser = session.createBrowser(queue);
        messageEnumeration = browser.getEnumeration();
        if (messageEnumeration != null)
        {
          if (!messageEnumeration.hasMoreElements())
          {
            System.out.println("Cola de mensajes vacía!");
          }
          else
          {
            System.out.println("Mensajes en cola:");
            while (messageEnumeration.hasMoreElements())
            {
              textMessage =
                (TextMessage) messageEnumeration.nextElement();
              System.out.println(textMessage.getText());
            }
          }
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    public static void main(String[] args) {
      Connection connection = null;
      Session session = null;
      MessageProducer messageProducer = null;
      TextMessage message = null;
  
      if (args.length != 1) {
          System.err.println("Uso: VotoQueueMessageProducer [-browse | <msg>]");
          return;
      }
  
      try {
          // Inicializar connectionFactory y queue mediante JNDI
          InitialContext jndi = new InitialContext();
          connectionFactory = (ConnectionFactory) jndi.lookup("jms/VotoConnectionFactory");
          queue = (Queue) jndi.lookup("jms/VotosQueue");
  
          connection = connectionFactory.createConnection();
          session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
          if (args[0].equals("-browse")) {
              browseMessages(session);
          } else {
              // Crear un productor de mensajes para la cola
              messageProducer = session.createProducer(queue);
  
              // Crear un mensaje de texto y establecer su contenido
              message = session.createTextMessage();
              message.setText(args[0]);
  
              // Enviar el mensaje a la cola
              messageProducer.send(message);
              System.out.println("Mensaje enviado correctamente: " + args[0]);
          }
      } catch (Exception e) {
          System.out.println("Excepcion : " + e.toString());
      } finally {
          if (connection != null) {
              try {
                  connection.close();
              } catch (JMSException e) {
                  e.printStackTrace();
              }
          }
          System.exit(0);
      }
  }
  
} // class
