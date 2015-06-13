package whereapp.kps.cpe.com.wheresapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;


public class AddFriend extends ActionBarActivity {

    private EditText user;
    private Button add;
    private String USER = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addfriend);

        user = (EditText) findViewById(R.id.editUser);
        add = (Button) findViewById(R.id.buttonAdd);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(AddFriend.this,ListFriends.class);

                USER = user.getText().toString();

                if(USER != null){
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            XMPPConnection connection = StaticConnection.getConnection();
                            Roster roster = connection.getRoster();
                            try {
                                roster.createEntry(USER, USER, null);
				                roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
				                Presence subscribe = new Presence(Presence.Type.subscribe);
				                subscribe.setTo(USER);
				                connection.sendPacket(subscribe);
                                Log.i("Add Friend", "Successful to add" + connection.getHost());
                                startActivity(intent);
                                finish();
			                } catch (XMPPException e) {
				                e.printStackTrace();
                                Log.i("Add Friend", "Not successful to add" + USER);
			                }
                        }
                    });
                    t.start();
                }else {
                    Log.i("Add Friend", "No data in USER ");
                    Toast.makeText(AddFriend.this, "Error :: Please insert USER again ! ", Toast.LENGTH_LONG).show();

                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_friend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
