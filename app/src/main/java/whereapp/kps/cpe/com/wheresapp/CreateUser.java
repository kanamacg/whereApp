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

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;


public class CreateUser extends ActionBarActivity {

    private EditText name;
    private EditText pass;
    private EditText repass;
    private Button signUp;
    private String USERNAME = "",PASSWORD = "",REPASS = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createuser);

        final XMPPConnection connection = StaticConnection.getConnection();
        final Intent intent = new Intent(CreateUser.this, ListFriends.class);

        name = (EditText) findViewById(R.id.editName);
        pass = (EditText) findViewById(R.id.editPass);
        repass = (EditText) findViewById(R.id.editRePass);

        signUp = (Button) findViewById(R.id.buttonSignUp);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                USERNAME = name.getText().toString();
                PASSWORD = pass.getText().toString();
                REPASS = repass.getText().toString();

        if (connection != null && USERNAME != null && PASSWORD != null && REPASS != null && PASSWORD.equals(REPASS)) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    Roster roster = connection.getRoster();
                    try {
                        AccountManager accountManager = connection.getAccountManager();
                        accountManager.createAccount(USERNAME,PASSWORD);
                        Log.i("Create User ", "Add " + name.getText().toString());

                        connection.login(USERNAME, PASSWORD);
                        Log.i("Create User ", "Log in as " + name.getText().toString());
                        StaticConnection.setConnection(connection);

                        ////// insert data to database

                        startActivity(intent);
                        finish();
                    } catch (XMPPException e1) {
                        Log.i("Create User ", "Can not add " + name.getText().toString());
                        Toast.makeText(CreateUser.this, "Please check your network", Toast.LENGTH_LONG).show();
                        e1.printStackTrace();
                    }

                }
            });
            t.start();
        }
        else if(!PASSWORD.equals(REPASS)){
            Toast.makeText(CreateUser.this, "Error : Password is not marching", Toast.LENGTH_LONG).show();
        }
        else if (connection == null){
            Toast.makeText(CreateUser.this, "Please Check network connection !", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(CreateUser.this, "Please insert data again !! ", Toast.LENGTH_LONG).show();
        }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_user, menu);
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
