package com.ferbook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.ferbook.R;
import com.ferbook.adapters.PostsAdapter;
import com.ferbook.models.Post;
import com.ferbook.providers.Authprovider;
import com.ferbook.providers.PostProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class FiltersActivity extends AppCompatActivity {

    String mExtraCategory;

    Authprovider mAuthprovider;
    RecyclerView mRecyclerView;
    PostProvider mPostProvider;
    PostsAdapter mPostsAdapter;

    Toolbar      mToolbar;

    TextView     tv_filt_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        mExtraCategory = getIntent().getStringExtra("category");

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Publicaciones de "+mExtraCategory);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuthprovider = new Authprovider();
        mPostProvider = new PostProvider();

        mRecyclerView   = findViewById(R.id.recyclerView_FilteredPosts);

        // El siguiente LinearLayoutManager va a mostrar los layouts uno abajo del otro
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FiltersActivity.this);
        // (para hacer funcionar la linea anterior, hay que pasar el linearLayoutManager al mRecyclerView de abajo)
        // pero ahora voy a reemplazarlo por el layout de cuadrícula

        mRecyclerView.setLayoutManager(new GridLayoutManager(FiltersActivity.this, 2));

        tv_filt_number = findViewById(R.id.tv_filteredPostNumber);

    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByCategory(mExtraCategory);

        FirestoreRecyclerOptions<Post> options =
                new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        mPostsAdapter = new PostsAdapter(options, FiltersActivity.this, tv_filt_number);

        mRecyclerView.setAdapter(mPostsAdapter);
        mPostsAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();

        mPostsAdapter.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }

}