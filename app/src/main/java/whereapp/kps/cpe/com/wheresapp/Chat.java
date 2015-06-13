package whereapp.kps.cpe.com.wheresapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;

/**
 * Created by apple on 2/28/15.
 */
public class Chat extends Activity {
    private String user = "",txt = "",userout ="";
    private Button send;
    private EditText text;
    private ListView list;
    private ArrayList<String> messages = new ArrayList<String>();
    private Handler mHandler = new Handler();
    private  static XMPPConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        user = ListFriends.getUserCore();
        Log.i("Main Chat", "intent user --->  " + user);

        list = (ListView) findViewById(R.id.listMessages);
        send = (Button) findViewById(R.id.sendBtn);
        text = (EditText) findViewById(R.id.chatET);

        userout = user;
        user = user + "@localhost";

        final XMPPConnection connection = StaticConnection.getConnection();
        Presence presence = new Presence(Presence.Type.available);
        connection.sendPacket(presence);
        setConnection(connection);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txt = text.getText().toString();
                Message msg = new Message(user, Message.Type.chat);
                msg.setBody(txt);
                if (connection != null) {
                    connection.sendPacket(msg);
                    messages.add(userout + ":");
                    messages.add(txt);
                    setListAdapter();
                }
                text.setText("");

            }
        });


    }

    private void setConnection(XMPPConnection connection) {

        this.connection = connection;
    }


    private void setListAdapter() {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listitem, messages);
            list.setAdapter(adapter);
    }
}