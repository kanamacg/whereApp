package whereapp.kps.cpe.com.wheresapp;

import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.LocationMe;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.ArrayList;
import java.util.Iterator;


public class MainChatGroup extends ActionBarActivity {

    LocalActivityManager mLocalActivityManager;
    public static boolean chk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainchatgroup);
        if (getIntent().getStringExtra("group") != null)
            ListFriends.setUserCore(getIntent().getStringExtra("group"));

        setTitle("Group " + ListFriends.getUserCore());
        mLocalActivityManager = new LocalActivityManager(this, false);
        mLocalActivityManager.dispatchCreate(savedInstanceState);

        TabHost tabHost = (TabHost) findViewById(R.id.tabhostGr);
        tabHost.setup(mLocalActivityManager);

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tab1")
                .setIndicator("Chat")
                .setContent(new Intent(MainChatGroup.this, ChatGroup.class));

        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec("tab2")
                .setIndicator("Maps")
                .setContent(new Intent(MainChatGroup.this, MapsGroup.class));

        tabHost.addTab(tabSpec);
        tabHost.addTab(tabSpec2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocalActivityManager.dispatchPause(!isFinishing());

    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocalActivityManager.dispatchResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_chat_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_request: {
                RequestLocation(ListFriends.getUserCore());
                chk = true;
                return true;
            }
            case R.id.action_invite :{
                Intent intent = new Intent(MainChatGroup.this,SelectUser.class);
                intent.putExtra("nameGroup", ListFriends.getUserCore());
                startActivity(intent);
                return true;
            }
            case R.id.action_member :{
                AlertDialog.Builder alertadd = new AlertDialog.Builder(
                        MainChatGroup.this);
                ArrayList<String> myArr = new ArrayList<>();
                LayoutInflater factory = LayoutInflater.from(MainChatGroup.this);
                final View view = factory.inflate(R.layout.infowindows, null);
                TextView text= (TextView) view.findViewById(R.id.textView2);
                String info = "Members";
                XMPPConnection connection = StaticConnection.getConnection();
                MultiUserChat muc = new MultiUserChat(connection,ListFriends.getUserCore() + "@conference.localhost");
                Iterator<String> s = muc.getOccupants();
                while (s.hasNext()){
                    Log.i("member ", StringUtils.parseResource(s.next()));
                }
                int a = muc.getOccupantsCount();

                text.setText(ListFriends.getUserCore()+a);

                alertadd.setView(view);
                alertadd.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {

                    }
                });

                alertadd.show();
                return true;
            }
            case R.id.action_leave :{
                final String group =  ListFriends.getUserCore();
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Leave group")
                        .setMessage("Are you sure you want to leave " + group + " ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                XMPPConnection connection = StaticConnection.getConnection();
                                DBChecker myDB = new DBChecker(MainChatGroup.this);
                                String arrData[] = myDB.SelectData("1");
                                WriteFile.Deletefile("Group" + ListFriends.getUserCore());
                                Message msg = new Message(group + "@conference.localhost", Message.Type.groupchat);
                                msg.setBody("leave this group");
                                connection.sendPacket(msg);
                                Presence leavePresence = new Presence(Presence.Type.unavailable);
                                leavePresence.setTo(group + "@conference.localhost/" + arrData[1]);
                                connection.sendPacket(leavePresence);
                                Toast.makeText(getApplicationContext(), ".::Success::.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainChatGroup.this,MainList.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public void RequestLocation(String user){

        final XMPPConnection connection = StaticConnection.getConnection();
        Presence presence = new Presence(Presence.Type.available);
        connection.sendPacket(presence);

        LocationMe lo = new LocationMe(user+"@conference.localhost",LocationMe.Type.groupchat);
        lo.setBody("Request");
        StaticConnection.setLocationUser(null);
        connection.sendPacket(lo);
    }
}
