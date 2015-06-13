package whereapp.kps.cpe.com.wheresapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class CreateGroup extends Activity {

    private EditText group;
    private String nameGroup = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.creategroup);

        Button add = (Button) findViewById(R.id.buttonAddGroup);
        group = (EditText) findViewById(R.id.editGroup);
        final Intent intent = new Intent(CreateGroup.this,SelectUser.class);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameGroup = group.getText().toString();

                if (nameGroup.equals("")) {
                    Toast.makeText(getApplicationContext(), ".::Please insert name::.", Toast.LENGTH_LONG).show();
                } else if (!nameGroup.equals("")) {
                    if (!(nameGroup.indexOf(" ") > 0)) {
                        intent.putExtra("nameGroup", nameGroup);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(), ".::Name can't space::.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_group, menu);
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
