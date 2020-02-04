package es.jesusgarce.duckhunt.ui;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.jesusgarce.duckhunt.R;
import es.jesusgarce.duckhunt.models.User;

import java.util.List;

public class MyUserRecyclerViewAdapter extends RecyclerView.Adapter<MyUserRecyclerViewAdapter.ViewHolder> {

    private final List<User> mValues;

    public MyUserRecyclerViewAdapter(List<User> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        int pos = position + 1;
        holder.textPosition.setText(pos + "º");
        holder.textDucks.setText(String.valueOf(mValues.get(position).getDucks()));
        holder.textNick.setText(mValues.get(position).getNick());
    }

    @Override
    public int getItemCount() {
        if (mValues.size() == 0)
            return 0;
        return mValues.size();
    }

    public void putUsers(List<User> userList){
        mValues.clear();
        mValues.addAll(userList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView textPosition;
        public final TextView textDucks;
        public final TextView textNick;
        public User mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            textPosition = view.findViewById(R.id.textPosition);
            textDucks = view.findViewById(R.id.textDucks);
            textNick = view.findViewById(R.id.textNick);

            Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(), "starseed.ttf");
            textPosition.setTypeface(typeface);
            textDucks.setTypeface(typeface);
            textNick.setTypeface(typeface);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + textNick.getText() + "'";
        }
    }
}
