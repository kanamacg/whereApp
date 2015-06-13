package whereapp.kps.cpe.com.wheresapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ListGroups extends Activity {
    private ListView ListOfGroups;
    private List<String> ListShow = new ArrayList<String>();
    private String selectLong;
    private String[] Cmd = {"Delete chat","Leave group"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listgroups);
        ListOfGroups = (ListView) findViewById(R.id.listViewGroups);

        XMPPConnection connection = StaticConnection.getConnection();
        if (connection != null) {
            registerForContextMenu(ListOfGroups);
            showGroup();

            ListOfGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selected = (String) ((TextView) view).getText();
                    ListFriends.setUserCore(selected);
                    Intent intent = new Intent(ListGroups.this, MainChatGroup.class);
                    startActivity(intent);
                }
            });

            ListOfGroups.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    selectLong = (String) ((TextView) view).getText();
                    return false;
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), ".::Please check your connection::.", Toast.LENGTH_LONG).show();
        }
    }

    private void showGroup(){

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, ListShow);
        final XMPPConnection connection = StaticConnection.getConnection();
        if (connection != null) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    adapter.clear();
                    DBChecker myDB = new DBChecker(ListGroups.this);
                    String arrData[] = myDB.SelectData("1");
                    File f = new File("/storage/emulated/0/whereApp/");

                    FilenameFilter textFilter = new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.toLowerCase().endsWith(".txt");
                        }
                    };

                    File[] files = f.listFiles(textFilter);
                    for (File file : files) {
                        try {
                            int i = file.getCanonicalPath().indexOf(".txt");
                            int j = file.getCanonicalPath().indexOf("Group");
                            String str = file.getCanonicalPath().substring(j + 5, i);
                            if (j > 0) {
                                if (!str.equals("")) {
                                    ListShow.add(str);
                                    try {
                                        MultiUserChat muc = new MultiUserChat(connection, str + "@conference.localhost");
                                        muc.join(arrData[1]);
                                        //muc.create("kkk");
                                        //WriteFile.Deletefile("Group" + str);
                                        //WriteFile.createfile("Group" + str);
                                        //Message msg = new Message(selectLong + "@conference.localhost", Message.Type.groupchat);
                                        //msg.setBody("entries this group");
                                        //connection.sendPacket(msg);
                                    } catch (XMPPException e) {
                                        Log.i("List Group ", "Error join " + str);
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ListOfGroups.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            });
            t.start();
        }else {
            Toast.makeText(getApplicationContext(), ".::Please check your connection::.", Toast.LENGTH_LONG).show();
        }

    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.setHeaderIcon(android.R.drawable.stat_notify_more);
        menu.setHeaderTitle("Menu");
        String[] menuItems = Cmd;
        for (int i = 0; i<menuItems.length; i++) {
            menu.add(Menu.NONE, i, i, menuItems[i]);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int menuItemIndex = item.getItemId();
        String[] menuItems = Cmd;
        String CmdName = menuItems[menuItemIndex];
        switch (CmdName){
            case "Delete chat": {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Delete chat history")
                        .setMessage("Are you sure to delete chat ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                WriteFile.Deletefile("Group" + selectLong);
                                WriteFile.createfile("Group" + selectLong);
                                Toast.makeText(getApplicationContext(), ".::Success::.", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
            }
            case "Leave group" : {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Leave group")
                        .setMessage("Are you sure you want to leave " + selectLong + " ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                XMPPConnection connection = StaticConnection.getConnection();
                                DBChecker myDB = new DBChecker(ListGroups.this);
                                String arrData[] = myDB.SelectData("1");
                                WriteFile.Deletefile("Group" + selectLong);
                                WriteFile.Deletefile("Member"+selectLong);
                                showGroup();
                                /*Message msg = new Message(selectLong + "@conference.localhost", Message.Type.groupchat);
                                msg.setBody("leave this group");
                                connection.sendPacket(msg);*/
                                Presence leavePresence = new Presence(Presence.Type.unavailable);
                                leavePresence.setTo(selectLong + "@conference.localhost/" + arrData[1]);
                                connection.sendPacket(leavePresence);
                                Toast.makeText(getApplicationContext(),".::Success::." ,Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
            }
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_groups, menu);
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
