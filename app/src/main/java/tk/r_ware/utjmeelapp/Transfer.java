package tk.r_ware.utjmeelapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import tk.r_ware.utjmeelapp.Communication.Communication;
import tk.r_ware.utjmeelapp.Communication.containers.Info;
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

    private List<String> usernames;
    private List<InfoItem> items;

    private Spinner sItem;
    private Spinner sFrom;
    private Spinner sTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        sItem = (Spinner)findViewById(R.id.sItem);
        sTo = (Spinner)findViewById(R.id.spTo);
        sFrom = (Spinner)findViewById(R.id.spFrom);

        Info info = Communication.getInstance().getCachedInfo();
        if(info == null){
            finish();//quit if info is null
            return;
        }
        items = info.getItems();
        ArrayAdapter<CharSequence> Aitems = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        for(InfoItem item : items){
            Aitems.add(item.getName());
        }
        Aitems.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItem.setAdapter(Aitems);

        usernames = Communication.getInstance().getCachedInfo().getUsernames();
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

        Button btPay = (Button)findViewById(R.id.btPay);
        final AppCompatActivity act = this;
        btPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etAmount = (EditText)findViewById(R.id.etAmount);
                PayTask task = new PayTask(act);
                task.execute(sFrom.getSelectedItemPosition(), sTo.getSelectedItemPosition(), sItem.getSelectedItemPosition(), Integer.parseInt(etAmount.getText().toString()));
            }
        });
    }


    public boolean onOptionsItemSelected(MenuItem item){
        //Back button in Action bar


        //Intent myIntent = new Intent(getApplicationContext(), UtjMeelMain.class);
        //startActivityForResult(myIntent, 0);
        finish();
        return true;

    }

    private class PayTask extends AsyncTask<Integer,Void,Boolean> {

        private String errorText = null;
        private String longErrorText = null;

        private AppCompatActivity mContext;

        public PayTask(AppCompatActivity mContext) {
            this.mContext = mContext;
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            String srcName,toName;
            InfoItem item;
            try{
                srcName = usernames.get(params[0]);
                toName = usernames.get(params[1]);
                item = items.get(params[2]);
            }catch (IndexOutOfBoundsException ex){
                ex.printStackTrace();
                errorText = "Error while processing payment!";

                //Toast.makeText(getApplicationContext(), "Error while processing payment!", Toast.LENGTH_SHORT).show();
                return false;//can't find an item
            }

            int amount = params[3];

            boolean result = Communication.getInstance().transfer(item.getName(),amount,srcName,toName,"");
            if(!result){
                errorText = "Error while transfering!";
                longErrorText = Communication.getInstance().getLastError();
                //Toast.makeText(getApplicationContext(), "Error while transferring!", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), Communication.getInstance().getLastError(), Toast.LENGTH_LONG).show();
            }
            return result;
    }

        @Override
        protected void onPostExecute(Boolean result){
            if(result){
                mContext.finish();
                return;
            }
            if(errorText != null){
                Toast.makeText(getApplicationContext(), errorText, Toast.LENGTH_SHORT).show();
            }
            if(longErrorText != null){
                Toast.makeText(getApplicationContext(), longErrorText, Toast.LENGTH_LONG).show();
            }
        }
    }
}
