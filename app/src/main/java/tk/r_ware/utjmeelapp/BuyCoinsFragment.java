package tk.r_ware.utjmeelapp;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;

import tk.r_ware.utjmeelapp.Communication.Communication;
import tk.r_ware.utjmeelapp.Communication.containers.Info;
import tk.r_ware.utjmeelapp.Communication.containers.InfoItem;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BuyCoinsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuyCoinsFragment extends Fragment {

    private Info info;
    private List<String> usernames;
    private boolean vallidInput = false;

    private Spinner sTarget;
    private TextView tvTotal;
    private EditText etAmount;
    private Button btAddCoins;


    public BuyCoinsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BuyCoinsFragment.
     */
    public static BuyCoinsFragment newInstance() {
        BuyCoinsFragment fragment = new BuyCoinsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_buy_coins, container, false);


        sTarget = (Spinner)v.findViewById(R.id.sTarget);
        tvTotal = (TextView)v.findViewById(R.id.tvTotal);
        etAmount = (EditText)v.findViewById(R.id.etAmount);
        btAddCoins = (Button)v.findViewById(R.id.btAddCoins);

        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    Double iAmount = Double.valueOf(s.toString());
                    tvTotal.setText(" / " + info.getPrice() + " = " + new DecimalFormat("#.00").format(iAmount / info.getPrice()) + " ©");
                    vallidInput = true;
                } catch (NumberFormatException ex) {
                    vallidInput = false;
                    //help not parsable
                    tvTotal.setText("NaN");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btAddCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!vallidInput){
                    Toast.makeText(getContext(), "Input was not valid!", Toast.LENGTH_SHORT).show();
                    return;
                }
                PayTask task = new PayTask();
                task.execute((String)sTarget.getSelectedItem(), etAmount.getText().toString());
            }
        });

        info = Communication.getInstance().getCachedInfo();

        usernames = info.getUsernames();
        ArrayAdapter<CharSequence> Ausernames = new ArrayAdapter<>(this.getContext(),R.layout.white_spinner_item);
        for (String s : usernames){
            if(s.equalsIgnoreCase("UtjMeel")) continue;
            Ausernames.add(s);
        }

        sTarget.setAdapter(Ausernames);

        tvTotal.setText(" / " + info.getPrice() + " = 0 ©");

        return v;
    }

    private class PayTask extends AsyncTask<String,Void,Boolean> {

        private String errorText = null;
        private String longErrorText = null;

        @Override
        protected Boolean doInBackground(String... params) {
            String targetName;

            try{
                targetName = params[0];
            }catch (IndexOutOfBoundsException ex){
                ex.printStackTrace();
                errorText = "Error while processing payment!";

                //Toast.makeText(getApplicationContext(), "Error while processing payment!", Toast.LENGTH_SHORT).show();
                return false;//can't find an item
            }

            double amount = Double.parseDouble(params[1]) / info.getPrice();

            boolean result = Communication.getInstance().transfer("Munt",amount,"UtjMeel",targetName,"");
            if(!result){
                errorText = "Error while transfering!";
                longErrorText = Communication.getInstance().getLastError();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(result){
                Toast.makeText(getContext(),"Succes!",Toast.LENGTH_SHORT).show();
                return;
            }
            if(errorText != null){
                Toast.makeText(getContext(), errorText, Toast.LENGTH_SHORT).show();
            }
            if(longErrorText != null){
                Toast.makeText(getContext(), longErrorText, Toast.LENGTH_LONG).show();
            }
        }
    }

}
