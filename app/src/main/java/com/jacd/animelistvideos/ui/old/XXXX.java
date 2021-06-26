package com.jacd.animelistvideos.ui.old;

import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.jacd.animelistvideos.R;
import com.jacd.animelistvideos.common.RecyclerViewClickInterface;
import com.jacd.animelistvideos.common.Utils;
import com.jacd.animelistvideos.adapter.old.RecyclerExampleAdapter;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class XXXX extends AppCompatActivity implements RecyclerViewClickInterface {

    private static final String TAG = "XXXX";

    private LinearLayout llSearch;
    private ImageButton ibtnSearchShow, ibtnSearchHide, ibtnSearchSearch;
    private EditText etSearchText;

    private RecyclerView recyclerView;
    private RecyclerExampleAdapter recyclerExampleAdapter;
    private List<String> moviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initComponent();
    }

    private void initComponent() {

        ibtnSearchShow = (ImageButton) findViewById(R.id.ibtn_search_show);
        llSearch = (LinearLayout) findViewById(R.id.ll_search);

        ibtnSearchShow.setVisibility(View.VISIBLE);
        llSearch.setVisibility(View.GONE);

        ibtnSearchHide = (ImageButton) findViewById(R.id.ibtn_search_cancel);
        ibtnSearchSearch = (ImageButton) findViewById(R.id.ibtn_search_show_hide);

        etSearchText = (EditText) findViewById(R.id.et_search_search);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        moviesList = new ArrayList<>();

        moviesList.add("Iron Man");
        moviesList.add("The Incredible Hulk");
        moviesList.add("Iron Man 2");
        moviesList.add("Thor");
        moviesList.add("Captain America: The First Avenger");
        moviesList.add("The Avengers");
        moviesList.add("Iron Man 3");
        moviesList.add("Thor: The Dark World");
        moviesList.add("Captain America: The Winter Soldier");
        moviesList.add("Guardians of the Galaxy");
        moviesList.add("Avengers: Age of Ultron");
        moviesList.add("Ant-Man");
        moviesList.add("Captain America: Civil War");
        moviesList.add("Doctor Strange");
        moviesList.add("Guardians of the Galaxy Vol. 2");
        moviesList.add("Spider-Man: Homecoming");
        moviesList.add("Thor: Ragnarok");
        moviesList.add("Black Panther");
        moviesList.add("Avengers: Infinity War");
        moviesList.add("Ant-Man and the Wasp");
        moviesList.add("Captain Marvel");
        moviesList.add("Avengers: Endgame");
        moviesList.add("Spider-Man: Far From Home");

        print(moviesList);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ibtnSearchShow.setOnClickListener(view -> {
            Utils.animateLayout(true, llSearch);
            view.setVisibility(View.GONE);
        });
        ibtnSearchHide.setOnClickListener(view -> {
            Utils.animateLayout(false, llSearch);
            new Handler().postDelayed(()->{
                ibtnSearchShow.setVisibility(View.VISIBLE);
            }, 470);
        });

        ibtnSearchSearch.setOnClickListener(view -> {
            Toast.makeText(this, "Aqui Buscar", Toast.LENGTH_SHORT).show();
        });
    }

    private void print(List<String> list){
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);

        recyclerExampleAdapter = new RecyclerExampleAdapter(list, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerExampleAdapter);
        recyclerView.addItemDecoration(dividerItemDecoration);

    }

    String deletedMovie = null;
    List<String> archivedMovies = new ArrayList<>();

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

            final int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    deletedMovie = moviesList.get(position);
                    moviesList.remove(position);
                    recyclerExampleAdapter.notifyItemRemoved(position);
                    Snackbar.make(recyclerView, deletedMovie + ", visto.", Snackbar.LENGTH_LONG)
                            .setAction("Undo", view -> {
                                moviesList.add(position, deletedMovie);
                                recyclerExampleAdapter.notifyItemInserted(position);
                            }).show();
                    break;
                case ItemTouchHelper.RIGHT:
                    final String movieName = moviesList.get(position);
                    archivedMovies.add(movieName);

                    moviesList.remove(position);
                    recyclerExampleAdapter.notifyItemRemoved(position);

                    Snackbar.make(recyclerView, movieName + ", Deleted.", Snackbar.LENGTH_LONG)
                            .setAction("Undo",view -> {
                                archivedMovies.remove(archivedMovies.lastIndexOf(movieName));
                                moviesList.add(position, movieName);
                                recyclerExampleAdapter.notifyItemInserted(position);
                            }).show();

                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(XXXX.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(XXXX.this, R.color.color_done))
                    .addSwipeLeftActionIcon(R.drawable.ic_done_white_24dp)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(XXXX.this, R.color.color_delete))
                    .addSwipeRightActionIcon(R.drawable.ic_delete_white_24dp)
                    .setActionIconTint(ContextCompat.getColor(recyclerView.getContext(), android.R.color.white))
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };



    //    These are the interface Methods from our custom RecyclerViewClickInterface
    @Override
    public void onItemClick(int position) {
        //Intent intent = new Intent(this, NewActivity.class);
        //intent.putExtra("MOVIE_NAME", moviesList.get(position));
        //startActivity(intent);
    }

    @Override
    public void onLongItemClick(final int position) {
//        moviesList.remove(position);
//        recyclerAdapter.notifyItemRemoved(position);
    }
}
