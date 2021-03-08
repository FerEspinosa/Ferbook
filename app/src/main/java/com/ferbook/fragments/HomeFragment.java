package com.ferbook.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ferbook.R;
import com.ferbook.activities.MainActivity;
import com.ferbook.activities.PostActivity;
import com.ferbook.adapters.PostsAdapter;
import com.ferbook.models.Post;
import com.ferbook.providers.Authprovider;
import com.ferbook.providers.PostProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;


// * A simple {@link Fragment} subclass.

public class HomeFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener{

    View                    mView;
    FloatingActionButton    mFab;
    MaterialSearchBar       mSearchBar;

    Authprovider            mAuthprovider;
    RecyclerView            mRecyclerView;
    PostProvider            mPostProvider;
    PostsAdapter            mPostsAdapter;
    PostsAdapter            mPostsAdapterSearch;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        setHasOptionsMenu(true);

        mView           = inflater.inflate(R.layout.fragment_home, container, false);
        mFab            = mView.findViewById(R.id.fab);
        mRecyclerView   = mView.findViewById(R.id.recyclerView_Home);
        mSearchBar      = mView.findViewById(R.id.searchBar);

        // El siguiente LinearLayoutManager va a mostrar los layouts uno abajo del otro
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);


        setHasOptionsMenu(true);
        mAuthprovider = new Authprovider();
        mPostProvider = new PostProvider();

        mSearchBar.setOnSearchActionListener(this);
        mSearchBar.inflateMenu(R.menu.main_menu);
        mSearchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.itemLogout){
                    logout();
                }
                return true;
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPost();
            }
        });

        return mView;
    }

    private void goToPost() {
        Intent intent = new Intent(getContext(), PostActivity.class);
        startActivity(intent);
    }


    private void logout() {
        mAuthprovider.logout();
        Intent intent = new Intent(getContext(), MainActivity.class);

        //Evitar que desde el siguiente activity se pueda volver atras hacia esta activity con el botón back:
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);

    }

    private void searchByTitle (String title) {
        Query query = mPostProvider.getPostByTitle(title);
        FirestoreRecyclerOptions<Post> options =
                new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        mPostsAdapterSearch = new PostsAdapter(options, getContext());
        mPostsAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mPostsAdapterSearch);
        mPostsAdapterSearch.startListening();
    }

    private void getAllPosts () {
        Query query = mPostProvider.getAll();
        FirestoreRecyclerOptions<Post> options =
                new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        mPostsAdapter = new PostsAdapter(options, getContext());
        mRecyclerView.setAdapter(mPostsAdapter);
        mPostsAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
            getAllPosts();
    }

    @Override
    public void onStop() {
        super.onStop();

        mPostsAdapter.stopListening();

        if (mPostsAdapterSearch != null){
            mPostsAdapterSearch.stopListening();
        }

    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled) {
            getAllPosts();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        searchByTitle(text.toString().toLowerCase());
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}