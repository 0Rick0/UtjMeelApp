package tk.r_ware.utjmeelapp;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tk.r_ware.utjmeelapp.Communication.Communication;
import tk.r_ware.utjmeelapp.Communication.containers.Transaction;
import tk.r_ware.utjmeelapp.TransactionsFragment.OnListFragmentInteractionListener;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Transaction} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyTransactionRecyclerViewAdapter extends RecyclerView.Adapter<MyTransactionRecyclerViewAdapter.ViewHolder> {

    private final List<Transaction> mValues;
    private final OnListFragmentInteractionListener mListener;
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
    private final Context context;

    private final ImageGetter imageGetter;

    public MyTransactionRecyclerViewAdapter(List<Transaction> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        this.context = context;
        imageGetter = new ImageGetter(context,utils.dpToPx(14));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Transaction item = mValues.get(position);

        holder.mItem = item;
        if(Communication.getInstance().getUsername().equalsIgnoreCase(item.getSourceName())) {
            holder.mShortView.setText(String.format(context.getString(R.string.transaction_short_text_to), item.getItemCount(), item.getItemName(), item.getTargetName()));
        }else{
            holder.mShortView.setText(String.format(context.getString(R.string.transaction_short_text_from), item.getItemCount(), item.getItemName(), item.getSourceName()));
        }
        holder.mDescriptionView.setText(String.format(context.getString(R.string.transaction_description_text),format.format(item.getDate()),item.getDescription()));
        holder.mPriceView.setText(Html.fromHtml(String.format(context.getString(R.string.transaction_price_text), item.getAmount()),imageGetter,null));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mShortView;
        public final TextView mDescriptionView;
        public final TextView mPriceView;
        public Transaction mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mShortView = (TextView) view.findViewById(R.id.transactionShort);
            mDescriptionView = (TextView) view.findViewById(R.id.transactionDescription);
            mPriceView = (TextView) view.findViewById(R.id.transactionPrice);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mShortView.getText() + "'";
        }
    }
}
