package es.jesusgarce.duckhunt.ui;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.jesusgarce.duckhunt.R;
import es.jesusgarce.duckhunt.common.Constants;
import es.jesusgarce.duckhunt.models.User;

import java.util.List;

public class MyUserRecyclerViewAdapter extends RecyclerView.Adapter<MyUserRecyclerViewAdapter.ViewHolder> {

    private final List<User> mValues;
    String level;

    public MyUserRecyclerViewAdapter(List<User> items, String level) {
        mValues = items;
        this.level = level;
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
        if (level.equals(Constants.LEVEL_HARD_DATABASE))
            holder.textDucks.setText(String.valueOf(mValues.get(position).getDucksHard()));
        else if (level.equals(Constants.LEVEL_EASY_DATABASE))
            holder.textDucks.setText(String.valueOf(mValues.get(position).getDucksEasy()));
        else
            holder.textDucks.setText(String.valueOf(mValues.get(position).getDucksMedium()));

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
