package whereapp.kps.cpe.com.wheresapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;

import java.util.ArrayList;
import java.util.Collection;


public class ListFriends extends ActionBarActivity {

    private ListView ListOfFriends;
    private ArrayList<String> List = new ArrayList<String>();
    private Button add;
    public static String userCore ;

    public static String getUserCore() {

        return userCore;
    }

    public static void setUserCore(String userCore) {

        ListFriends.userCore = userCore;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listfriends);

        ListOfFriends = (ListView) findViewById(R.id.listViewFriends);
        add = (Button) findViewById(R.id.buttonAdd);

        Locate locate = new Locate(this);
        locate.getLocation();


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                XMPPConnection connection = StaticConnection.getConnection();
                Roster roster = connection.getRoster();

                Collection<RosterEntry> entries = roster.getEntries();
                for (RosterEntry entry : entries) {
                    if(entry.getName() != null)
                    {
                        List.add(entry.getName().toString());
                        Log.i("List of Friends", "----->" + entry.getName().toString());
                    }else if (entry.getName() == null){
                        List.add("No Buddy");
                    }
                }
               /* if (List == null){
                    List.add("No Buddy");
                }*/

            }
        });
        t.start();
        setListAdapter();

        ListOfFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) ((TextView) view).getText();
                Toast.makeText(getApplicationContext(),selected, Toast.LENGTH_SHORT).show();
                setUserCore(selected);
                Intent intent = new Intent(ListFriends.this,MainChat.class);
                if (selected.equals("No Buddy"))
                    Toast.makeText(ListFriends.this, "Please add friends ", Toast.LENGTH_LONG).show();
                else {
                    startActivity(intent);
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListFriends.this,AddFriend.class);
                startActivity(intent);
            }
        });
    }

    private void setListAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, List);
        ListOfFriends.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_friends, menu);
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
