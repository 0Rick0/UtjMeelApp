package tk.r_ware.utjmeelapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import tk.r_ware.utjmeelapp.Communication.Communication;
import tk.r_ware.utjmeelapp.Communication.containers.Info;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PayFragment extends Fragment {

    private Info info;

    public PayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PayFragment newInstance() {
        PayFragment fragment = new PayFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new infoRetriever().execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_pay, container, false);

        Button b1 = (Button)v.findViewById(R.id.btPay1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getView().getContext(),Transfer.class);
                startActivity(i);
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class infoRetriever extends AsyncTask<Void,Void,Info>{

        @Override
        protected Info doInBackground(Void... params) {
            return Communication.getInstance().info();
        }

        @Override
        protected void onPostExecute(Info result){
            info = result;
            if(info == null){
                ((TextView) getView().findViewById(R.id.tvCoins)).setText("Error");
                Toast.makeText(getView().getContext(),Communication.getInstance().getLastError(),Toast.LENGTH_LONG).show();
            }else {
                ((TextView) getView().findViewById(R.id.tvCoins)).setText(result.getBalance() + "©");
            }
        }
    }
}
