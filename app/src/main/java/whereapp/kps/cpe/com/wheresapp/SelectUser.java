package whereapp.kps.cpe.com.wheresapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class SelectUser extends ActionBarActivity {
    private ArrayList<HashMap<String, Object>> MyArrList = new ArrayList<>();
    private ArrayList<String> myArr = new ArrayList<>();
    private String room;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectuser);
        room = getIntent().getStringExtra("nameGroup");
        setTitle("Select to " + room);

        MyArrList = ListFriends.ArrListFriends;
        /*try {
            String path = "/storage/emulated/0/whereApp/"+"Member" + room +".txt";
            File file = new File(path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                myArr.add(line);
            }
            br.close();
            ArrayList<HashMap<String, Object>> buffer;
            buffer = MyArrList;
            int k = 0;
            for (HashMap use : MyArrList){
                for (int i = 0; i< myArr.size(); i++){
                    if (use.get("user").equals(myArr.get(i))){
                        Log.i("Select user ", MyArrList.get(k).toString());
                        buffer.remove(k);
                        break;
                    }
                }
                k++;
            }
            MyArrList = buffer;
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        final ListView lisView1 = (ListView)findViewById(R.id.listView1);
        final XMPPConnection connection = StaticConnection.getConnection();
        lisView1.setAdapter(new CountryAdapter(this));

        // Check All
        Button btnCheckAll = (Button) findViewById(R.id.btnCheckAll);
        btnCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = lisView1.getAdapter().getCount();
                for (int i = 0; i < count; i++) {
                    LinearLayout itemLayout = (LinearLayout) lisView1.getChildAt(i); // Find by under LinearLayout
                    CheckBox checkbox = (CheckBox) itemLayout.findViewById(R.id.ColChk);
                    checkbox.setChecked(true);
                }
            }
        });

        // Clear All
        Button btnClearAll = (Button) findViewById(R.id.btnClearAll);
        btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = lisView1.getAdapter().getCount();
                for (int i = 0; i < count; i++) {
                    LinearLayout itemLayout = (LinearLayout) lisView1.getChildAt(i); // Find by under LinearLayout
                    CheckBox checkbox = (CheckBox) itemLayout.findViewById(R.id.ColChk);
                    checkbox.setChecked(false);
                }
            }
        });

        final Intent intent = new Intent(SelectUser.this,MainList.class);
        // Get Item Checked
        Button btnGetItem = (Button) findViewById(R.id.btnGetItem);
        btnGetItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = lisView1.getAdapter().getCount();
                boolean ck = true;
                createRoom(connection,room);
                for (int i = 0; i < count; i++) {
                    LinearLayout itemLayout = (LinearLayout) lisView1.getChildAt(i); // Find by under LinearLayout
                    CheckBox checkbox = (CheckBox) itemLayout.findViewById(R.id.ColChk);
                    if (checkbox.isChecked()) {
                        ck = false;
                        try {
                            createInvitation(connection, checkbox.getTag().toString(), room);
                            WriteFile.createfile("Group" + room);
                        } catch (XMPPException e) {
                            Log.i("Select User ", "Error create Invite " + room);
                            e.printStackTrace();
                        }
                    }
                }
                if (!ck) {
                    ListFriends.setUserCore(room);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SelectUser.this, ".::Please select user::.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    public class CountryAdapter extends BaseAdapter
    {
        private Context context;

        public CountryAdapter(Context c)
        {
            // TODO Auto-generated method stub
            context = c;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return MyArrList.size();
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

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.column, null);

            }

            // ColImg

            ImageView imageView = (ImageView) convertView.findViewById(R.id.ColImgPath);
            imageView.getLayoutParams().height = 50;
            imageView.getLayoutParams().width = 50;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            try
            {
                if (MyArrList.get(position).get("photo") != null) {
                    imageView.setImageBitmap((Bitmap) MyArrList.get(position).get("photo"));
                }else {
                    imageView.setImageResource(R.drawable.share_icon);
                }
            } catch (Exception e) {
                imageView.setImageResource(R.drawable.share_icon);
            }

            // ColUser
            TextView txtCountry = (TextView) convertView.findViewById(R.id.ColUser);
            if (MyArrList.get(position).get("nick") != null){
                txtCountry.setText(MyArrList.get(position).get("nick").toString());
                txtCountry.setTag(MyArrList.get(position).get("user"));
            }else {
                txtCountry.setText(MyArrList.get(position).get("user").toString());
                txtCountry.setTag(MyArrList.get(position).get("user"));
            }

            // ColChk
            CheckBox Chk = (CheckBox) convertView.findViewById(R.id.ColChk);
            Chk.setTag(MyArrList.get(position).get("user"));

            return convertView;

        }

    }

    private void createRoom(XMPPConnection connection,String room){

        MultiUserChat muc = new MultiUserChat(connection,room + "@conference.localhost");
        // Create a MultiUserChat using a Connection for a room

        // Create the room
        try {
            muc.create(room);
            // Get the the room's configuration form
            Form form = muc.getConfigurationForm();
            // Create a new form to submit based on the original form
            Form submitForm = form.createAnswerForm();
            // Add default answers to the form to submit
            for (Iterator fields = form.getFields(); fields.hasNext();) {
                FormField field = (FormField) fields.next();
                if (!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable() != null) {
                    // Sets the default value as the answer
                    submitForm.setDefaultAnswer(field.getVariable());
                }
            }
            // Sets the new owner of the room
            List owners = new ArrayList();
            owners.add("kanama@localhost");
            submitForm.setAnswer("muc#roomconfig_roomowners", owners);
            // Send the completed form (with default values) to the server to configure the room
            muc.sendConfigurationForm(submitForm);
        } catch (XMPPException e) {
            e.printStackTrace();
        }

    }

    private void createInvitation(XMPPConnection connection,String user,String room) throws XMPPException{

        MultiUserChat muc = new MultiUserChat(connection,room + "@conference.localhost");
        muc.join(room);
        //setConfig(muc);
        muc.addInvitationRejectionListener(new InvitationRejectionListener() {
            @Override
            public void invitationDeclined(String invitee, String reason) {

            }
        });
        muc.invite(user + "@localhost","Join Me At " + room + "'s room !");
        muc.sendConfigurationForm(new Form(Form.TYPE_SUBMIT));
    }

    private void setConfig(MultiUserChat muc) {

        try {
            Form form = muc.getConfigurationForm();
            Form submitForm = form.createAnswerForm();
            for (Iterator fields = form.getFields();fields.hasNext();){
                FormField field = (FormField) fields.next();
                if(!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable()!= null){
                    submitForm.setDefaultAnswer(field.getVariable());
                }
            }
            submitForm.setAnswer("muc#roomconfig_publicroom", true);
            muc.sendConfigurationForm(submitForm);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_user, menu);
        return true;
    }

}
