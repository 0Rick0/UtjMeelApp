package tk.r_ware.utjmeelapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

import tk.r_ware.utjmeelapp.Communication.Communication;
import tk.r_ware.utjmeelapp.Communication.containers.InfoItem;

public class Transfer extends AppCompatActivity {

//    private View myFragmentView;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//    {
//        myFragmentView = inflater.inflate(R.layout.activity_transfer, container, false);
//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//
//        return myFragmentView;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Spinner sItem = (Spinner)findViewById(R.id.sItem);
        Spinner sTo = (Spinner)findViewById(R.id.spTo);
        Spinner sFrom = (Spinner)findViewById(R.id.spFrom);

        List<InfoItem> items = Communication.getInstance().getCachedInfo().getItems();
        ArrayAdapter<CharSequence> Aitems = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        for(InfoItem item : items){
            Aitems.add(item.getName());
        }
        Aitems.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItem.setAdapter(Aitems);

        List<String> usernames = Communication.getInstance().getCachedInfo().getUsernames();
        ArrayAdapter<CharSequence> Ausernames = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        for (String s : usernames){
            Ausernames.add(s);
        }
        sTo.setAdapter(Ausernames);
        sFrom.setAdapter(Ausernames);
        for (int i=0; i<usernames.size();i++){
            if(usernames.get(i).equalsIgnoreCase(Communication.getInstance().getUsername())){
                sFrom.setSelection(i);//select own username
                break;
            }
        }
        for (int i=0; i<usernames.size();i++){
            if(usernames.get(i).equalsIgnoreCase("UtjMeel")){//find id of keet
                sTo.setSelection(i);//select keet as default target
                break;
            }
        }
        if(Communication.getInstance().getCachedInfo().getUser_type()>2){
            sFrom.setEnabled(false);//if normal user don't allow changing this

        }
    }


    public boolean onOptionsItemSelected(MenuItem item){
        //Intent myIntent = new Intent(getApplicationContext(), UtjMeelMain.class);
        //startActivityForResult(myIntent, 0);
        finish();
        return true;

    }
}
