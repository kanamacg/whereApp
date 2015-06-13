package whereapp.kps.cpe.com.wheresapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.VCardProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;


public class ChatGroup extends Activity {
    private String user = "",txt = "",filename,name,me;
    private EditText text;
    private ListView list;
    private ArrayList<String> messages = new ArrayList<>();
    private ArrayList<String> myArr = new ArrayList<>();
    private Handler mHandler = new Handler();
    private Bitmap photo = null;
    private ArrayList<HashMap<String, Object>> MyArrList = new ArrayList<>();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatgroup);

        MyArrList = ListFriends.ArrListFriends;
        user = ListFriends.getUserCore();
        name = user;
        filename = "Group" + user;
        user = user + "@conference.localhost";
        try {
            final XMPPConnection connection = StaticConnection.getConnection();

            if (connection != null) {

                list = (ListView) findViewById(R.id.listMessagesG);
                ImageView send = (ImageView) findViewById(R.id.imageSendG);
                text = (EditText) findViewById(R.id.chatETG);
                DBChecker myDB = new DBChecker(ChatGroup.this);
                final String arrData[] = myDB.SelectData("1");
                me = arrData[1];

                reading();
                for (int i = 0; i < myArr.size(); i++) {
                    String n = myArr.get(i);
                    if (n.indexOf("|") >= 0) {
                        String ss = n.substring(0, n.length() - 1);
                        messages.add(me + " : " + ss);
                    } else {
                        messages.add(n);
                    }
                }
                list.setAdapter(new Adapter(ChatGroup.this));

                MultiUserChat muc = new MultiUserChat(StaticConnection.getConnection(), name + "@conference.localhost");
                muc.addParticipantStatusListener(new ParticipantStatus());

                //--------------------------------Message send-----------------------------------------------

                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        txt = text.getText().toString();
                        if (!txt.equals("")) {
                            MultiUserChat muc = new MultiUserChat(connection, user);
                            try {
                                muc.sendMessage(txt);
                                messages.add(me + " : " + txt);
                                myArr.clear();
                                list.setAdapter(new Adapter(ChatGroup.this));
                                text.setText("");
                            } catch (XMPPException e) {
                                e.printStackTrace();
                            }catch (IllegalStateException r){
                                r.printStackTrace();
                            }
                        }

                    }
                });

                //--------------------------------Message Receiver-----------------------------------------------

                PacketFilter filter = new MessageTypeFilter(Message.Type.groupchat);
                connection.addPacketListener(new PacketListener() {
                    @Override
                    public void processPacket(Packet packet) {
                        Message message = (Message) packet;
                        if (message.getBody() != null) {
                            final boolean[] checkphoto = {false};
                            final String nick = StringUtils.parseResource(message.getFrom());
                            String fromName = StringUtils.parseBareAddress(message.getFrom());
                            String filename[] = fromName.split("@");
                            Log.i("Chat Group ", " Text Group Received " + message.getBody() + " nick " + nick + " Base " + filename[0]);
                            if (!nick.equals(name) && !nick.equals(me) && filename[0].equals(name)) {
                                messages.add(nick + " : " + message.getBody());
                                mHandler.post(new Runnable() {
                                    public void run() {
                                        for (HashMap use : MyArrList) {
                                            if (use.get("user") == nick) {
                                                photo = (Bitmap) use.get("photo");
                                                checkphoto[0] = true;
                                                break;
                                            }
                                        }
                                        if (!checkphoto[0]) {
                                            ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
                                            VCard vCard = new VCard();
                                            try {
                                                vCard.load(connection);
                                                vCard.load(connection, nick + "@localhost");
                                                Log.i("nick", nick);
                                                byte[] pix = vCard.getAvatar();
                                                if (pix != null)
                                                    photo = BitmapFactory.decodeByteArray(pix, 0, pix.length);
                                            } catch (XMPPException e) {
                                                e.printStackTrace();
                                            } catch (IllegalArgumentException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        list.setAdapter(new Adapter(ChatGroup.this));
                                    }
                                });
                            }
                        }
                    }
                }, filter);
            } else {
                Toast.makeText(getApplicationContext(), ".::Please check your connection::.", Toast.LENGTH_LONG).show();
            }
        }catch (IllegalStateException r){
            r.printStackTrace();
        }
    }

    public class Adapter extends BaseAdapter
    {
        private Context context;

        public Adapter(Context c)
        {
            // TODO Auto-generated method stub
            context = c;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return messages.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (messages.get(position).indexOf(me + " : ") == 0) {
                convertView = inflater.inflate(R.layout.chat_right, null);
            }
            else {
                convertView = inflater.inflate(R.layout.chat_left, null);
                // photo
                ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
                imageView.getLayoutParams().height = 50;
                imageView.getLayoutParams().width = 50;
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                int index = messages.get(position).indexOf(" :",0);
                String name = messages.get(position).substring(0,index);
                for (HashMap pix : MyArrList){
                    if (name.equals(pix.get("user"))) {
                        photo = (Bitmap) pix.get("photo");
                        break;
                    }
                }
                if (photo != null)
                    imageView.setImageBitmap(photo);
                else
                    imageView.setImageResource(R.drawable.share_icon);
            }
            TextView txtCountry = (TextView) convertView.findViewById(R.id.firstLine);
            txtCountry.setText(messages.get(position));
            list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            list.setSelection(getCount() - 1);

            return convertView;
        }
    }

    public void reading() {
        try {
            String path = "/storage/emulated/0/whereApp/" + filename + ".txt";
            File file = new File(path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                myArr.add(line);
            }
            br.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_group, menu);
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

    class ParticipantStatus implements ParticipantStatusListener {

        @Override
        public void adminGranted(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void adminRevoked(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void banned(String arg0, String arg1, String arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void joined(String participant) {
            //System.out.println(StringUtils.parseResource(participant) + " has joined the room.");
            final String user = StringUtils.parseResource(participant);
            messages.add(user + " : has joined the room.");
            mHandler.post(new Runnable() {
                public void run() {
                    boolean ckpix = false;
                    for (HashMap use : MyArrList) {
                        if (use.get("user") == user) {
                            photo = (Bitmap) use.get("photo");
                            ckpix = true;
                            break;
                        }
                    }
                    if (!ckpix) {
                        ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
                        VCard vCard = new VCard();
                        try {
                            vCard.load(StaticConnection.getConnection());
                            vCard.load(StaticConnection.getConnection(), user + "@localhost");
                            byte[] pix = vCard.getAvatar();
                            if (pix != null)
                                photo = BitmapFactory.decodeByteArray(pix, 0, pix.length);
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                    list.setAdapter(new Adapter(ChatGroup.this));
                }
            });
        }

        @Override
        public void kicked(String arg0, String arg1, String arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void left(String participant) {
            // TODO Auto-generated method stub
            //System.out.println(StringUtils.parseResource(participant) + " has left the room.");
            final String user = StringUtils.parseResource(participant);
            messages.add(user + " : has left the room.");
            mHandler.post(new Runnable() {
                public void run() {
                    boolean ckpix = false;
                    for (HashMap use : MyArrList) {
                        if (use.get("user") == user) {
                            photo = (Bitmap) use.get("photo");
                            ckpix = true;
                            break;
                        }
                    }
                    if (!ckpix) {
                        ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
                        VCard vCard = new VCard();
                        try {
                            vCard.load(StaticConnection.getConnection());
                            vCard.load(StaticConnection.getConnection(), user + "@localhost");
                            byte[] pix = vCard.getAvatar();
                            if (pix != null)
                                photo = BitmapFactory.decodeByteArray(pix, 0, pix.length);
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                    list.setAdapter(new Adapter(ChatGroup.this));
                }
            });

        }

        @Override
        public void membershipGranted(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void membershipRevoked(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void moderatorGranted(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void moderatorRevoked(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void nicknameChanged(String participant, String newNickname) {
            //System.out.println(StringUtils.parseResource(participant) + " is now known as " + newNickname + ".");
            final String user = StringUtils.parseResource(participant);
            messages.add(user + " : is now known as " + newNickname + ".");
            mHandler.post(new Runnable() {
                public void run() {
                    boolean ckpix = false;
                    for (HashMap use : MyArrList) {
                        if (use.get("user") == user) {
                            photo = (Bitmap) use.get("photo");
                            ckpix = true;
                            break;
                        }
                    }
                    if (!ckpix) {
                        ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
                        VCard vCard = new VCard();
                        try {
                            vCard.load(StaticConnection.getConnection());
                            vCard.load(StaticConnection.getConnection(), user + "@localhost");
                            byte[] pix = vCard.getAvatar();
                            if (pix != null)
                                photo = BitmapFactory.decodeByteArray(pix, 0, pix.length);
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                    list.setAdapter(new Adapter(ChatGroup.this));
                }
            });
        }

        @Override
        public void ownershipGranted(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void ownershipRevoked(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void voiceGranted(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void voiceRevoked(String arg0) {
            // TODO Auto-generated method stub

        }
    }
    }
