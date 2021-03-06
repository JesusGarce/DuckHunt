package es.jesusgarce.duckhunt.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import es.jesusgarce.duckhunt.R;
import es.jesusgarce.duckhunt.models.User;

import java.util.LinkedList;
import java.util.List;


public class UserRankingFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    List<User> userList;
    MyUserRecyclerViewAdapter adapter;
    FirebaseFirestore db;
    RecyclerView recyclerView;
    String level;

    public UserRankingFragment(String level) {
        this.level = level;
    }

    @SuppressWarnings("unused")
    public static UserRankingFragment newInstance(int columnCount) {
        UserRankingFragment fragment = new UserRankingFragment("");
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            db.collection("users")
                    .orderBy(level, Query.Direction.DESCENDING)
                    .limit(10)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            userList = new LinkedList<>();
                            for (DocumentSnapshot document : task.getResult()){
                                User user = document.toObject(User.class);
                                userList.add(user);
                            }
                            adapter = new MyUserRecyclerViewAdapter(userList, level);
                            recyclerView.setAdapter(adapter);
                        }
                    });
        }

        return view;
    }
}
