package whereapp.kps.cpe.com.wheresapp;

import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

/**
 * Created by apple on 3/2/15.
 */
public class StaticConnection{
    public static final String HOST = "158.108.207.170";
    public static final int PORT = 5222;
    public static final String SERVICE = "localhost";

    public static XMPPConnection connectionStatic;

    public static void setConnection(XMPPConnection connection) {

        connectionStatic = connection;
        if (connection != null){
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            connection.addPacketListener(new PacketListener() {
                @Override
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    if (message.getBody() != null){
                        String fromName = StringUtils.parseBareAddress(message.getFrom());
                        Log.i("StaticConnection ", " Text Recieved " + message.getBody() + " from " +  fromName);
                    }

                }
            },filter);

            PacketFilter Lofilter = new MessageTypeFilter(Message.Type.location);
            connection.addPacketListener(new PacketListener() {
                @Override
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    String fromName = StringUtils.parseBareAddress(message.getFrom());
                    Log.i("StaticConnection ", " Location Recieved from " +  fromName);

                }
            },Lofilter);
        }
    }
    private  void setconnect(XMPPConnection connection){

        connectionStatic = connection;
    }
    public static XMPPConnection getConnection() {

        return connectionStatic;
    }


    public void connect() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT, SERVICE);
                XMPPConnection connection = new XMPPConnection(connConfig);
                try {
                    connection.connect();
                    setconnect(connection);
                    Log.i("StaticConnection", "Connected to " + connection.getHost());
                } catch (XMPPException ex) {
                    Log.e("XMPPChatDemoActivity", "Failed to connect to " + connection.getHost());
                    Log.e("XMPPChatDemoActivity", ex.toString());
                    setConnection(null);
                }
            }
        });
        t.start();
    }

}
