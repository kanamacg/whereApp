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

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;


public class Main extends ActionBarActivity {

    private EditText name;
    private EditText pass;
    private Button signin;
    private Button signup;

    private String USERNAME = "";
    private String PASSWORD = "";
    private XMPPConnection stop = null;

//    public final ProgressDialog dialog = ProgressDialog.show(this,"Connecting...", "Please wait...", true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        name = (EditText) findViewById(R.id.editName);
        pass = (EditText) findViewById(R.id.editPass);

        signin = (Button) findViewById(R.id.buttonSignIn);
        signup = (Button) findViewById(R.id.buttonSignUp);

        final DBChecker myDB = new DBChecker(this);
        String arrData[] = myDB.SelectData("1");

        final Intent intent = new Intent(Main.this,ListFriends.class);
        final XMPPConnection connection = StaticConnection.getConnection();

        if (connection == null)
        {
            Toast.makeText(Main.this, "Please Check network connection !", Toast.LENGTH_LONG).show();
        }
        else if(arrData == null && connection != null ){
            signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    USERNAME = name.getText().toString();
                    PASSWORD = pass.getText().toString();
                    myDB.InsertData("1",USERNAME,PASSWORD);

                    if (connection != null && USERNAME != null && PASSWORD != null) {
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //dialog.show();

                                try {
                                    connection.login(USERNAME, PASSWORD);
                                    StaticConnection.setConnection(connection);
                                    Log.i("Main ", "Log in as " + USERNAME);
                                    // dialog.dismiss();
                                    startActivity(intent);
                                    finish();
                                } catch (XMPPException e) {
                                    // dialog.dismiss();
                                    Log.i("Main ", "Error Log in as " + USERNAME);
                                    Log.i("Main ", "Error Log " + e.toString());
                                    Toast.makeText(Main.this, "Please Check network connection !", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                        t.start();
                    }
                    else if (connection == null){
                        Toast.makeText(Main.this, "Please Check network connection !", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(Main.this, "Please insert username or password !", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else if(arrData != null && connection != null){

            USERNAME = arrData[1].toString();
            PASSWORD = arrData[2].toString();
            Log.i("Main ", "Database " + USERNAME + PASSWORD);

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    XMPPConnection connection = StaticConnection.getConnection();
                    try {
                        connection.login(USERNAME, PASSWORD);
                        StaticConnection.setConnection(connection);
                        Log.i("Main ", "Log in as " + USERNAME);
                        Intent intent = new Intent(Main.this, ListFriends.class);
                        startActivity(intent);
                        finish();
                    } catch (XMPPException e) {
                        Log.i("Main ", "Error Log in as " + USERNAME);
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDB.Deletdata("1");
                Intent intent = new Intent(Main.this,CreateUser.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
