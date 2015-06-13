package whereapp.kps.cpe.com.wheresapp;

import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.HashMap;


public class MainList extends ActionBarActivity {

    LocalActivityManager mLocalActivityManager;
    private ArrayList<HashMap<String, Object>> MyArrList = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        ListFriends.setUserCore("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlist);
        MyArrList = Main.Me;
        DBChecker myDB = new DBChecker(this);
        String arrData[] = myDB.SelectData("1");
        setTitle(arrData[1]);
        for (HashMap me : MyArrList){
            if (me.get("nick") != null) {
                setTitle(me.get("nick").toString());
                break;
            }
        }

        mLocalActivityManager = new LocalActivityManager(this, false);
        mLocalActivityManager.dispatchCreate(savedInstanceState);

        TabHost tabHost = (TabHost) findViewById(R.id.tabhost2);
        tabHost.setup(mLocalActivityManager);

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tab1")
                .setIndicator("Friends")
                .setContent(new Intent(MainList.this, ListFriends.class));

        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec("tab2")
                .setIndicator("Groups")
                .setContent(new Intent(MainList.this, ListGroups.class));

        tabHost.addTab(tabSpec);
        tabHost.addTab(tabSpec2);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
        case R.id.action_addfriend :
        {
            Intent intent = new Intent(MainList.this,AddFriend.class);
            startActivity(intent);
            return true;
        }
        case R.id.action_maps :
        {
            Intent intent = new Intent(MainList.this,MapsGroup.class);
            startActivity(intent);
            return true;
        }
        case R.id.action_newgroup :
        {
            Intent intent = new Intent(MainList.this,CreateGroup.class);
            startActivity(intent);
            return true;
        }
        case R.id.action_logout :
        {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Log out")
                    .setMessage("Are you sure to Log out  ?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            XMPPConnection connection = StaticConnection.getConnection();
                            Presence presence = new Presence(Presence.Type.unavailable);
                            connection.sendPacket(presence);
                            StaticConnection.setConnection(null, MainList.this);
                            DBChecker myDB = new DBChecker(MainList.this);
                            myDB.Deletdata("1");
                            final ProgressDialog progress = ProgressDialog.show(MainList.this, "Progressing........",
                                    "Please wait....", true);

                            new Thread(new Runnable() {
                                @Override
                                public void run()
                                {
                                    // do the thing that takes a long time
                                    StaticConnection connectionFirst = new StaticConnection();
                                    connectionFirst.connect();
                                    try {
                                        Thread.sleep(1500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(MainList.this,Splash.class);
                                            WriteFile.DeleteFolder();
                                            progress.dismiss();
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                            Toast.makeText(getApplicationContext(), ".::Success to log out::.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }).start();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return true;
        }
        case R.id.action_settings :
        {
            Intent intent = new Intent(MainList.this,Setting.class);
            startActivity(intent);
            return true;
        }
    }
        return super.onOptionsItemSelected(item);
    }
}