package tk.r_ware.utjmeelapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import tk.r_ware.utjmeelapp.Communication.Communication;
import tk.r_ware.utjmeelapp.Communication.containers.Transaction;
import tk.r_ware.utjmeelapp.dummy.DummyContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TransactionsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private RecyclerView recyclerView;

    private List<Transaction> mTransactions;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TransactionsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TransactionsFragment newInstance(int columnCount) {
        TransactionsFragment fragment = new TransactionsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTransactions = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_transactions_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyTransactionRecyclerViewAdapter(mTransactions, mListener,context));
        }

        LoadTransactionsTask task = new LoadTransactionsTask(getContext());
        task.execute();

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Transaction item);
    }

    public class LoadTransactionsTask extends AsyncTask<Void,Void,List<Transaction>> {

        private Context context;

        public LoadTransactionsTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<Transaction> doInBackground(Void... params) {

            return Communication.getInstance().transactions().getTransactions();
        }

        @Override
        protected void onPostExecute(final List<Transaction> transactions){
            if(transactions == null){
                Toast.makeText(context, "Failed to load transactions!", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, Communication.getInstance().getLastError(), Toast.LENGTH_SHORT).show();
                return;
            }

            mTransactions.addAll(transactions);

            Collections.sort(mTransactions, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction o1, Transaction o2) {
                    return o2.getDate().compareTo(o1.getDate());
                }
            });

            recyclerView.getAdapter().notifyDataSetChanged();
            //recyclerView.setAdapter(new MyTransactionRecyclerViewAdapter(DummyContent.ITEMS, mListener,context));
        }


    }
}
