package com.example.hslb;

import static com.example.hslb.MainActivity.app;
import static com.example.hslb.OrderPlaced.order_name_finished;
import static com.example.hslb.OrderPlaced.order_status_finished;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hslb.placeorder.Order;
import com.example.hslb.placeorder.OrderAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.bson.Document;

import java.util.ArrayList;

import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;


public class Delivered extends Fragment {

    private ArrayList<Order> or_del;
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> mongoCollection;

    private User user;

    public Delivered() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_delivered, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewDelivered);
        // Inflate the layout for this fragment
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        or_del = new ArrayList<>();
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButtonDelivered);


        user = app.currentUser();
        mongoClient = user.getMongoClient("mongodb-atlas");
        mongoDatabase = mongoClient.getDatabase("HSLB");
        mongoCollection = mongoDatabase.getCollection("order_delivered");

        get_from_db_del();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                or_del.clear();
                get_from_db_del();
            }
        });

        orderAdapter = new OrderAdapter((androidx.recyclerview.widget.RecyclerView) recyclerView, or_del, getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(orderAdapter);



        return rootView;

    }

    public void get_from_db_del(){
        Document to_find = new Document();
        to_find.append("user-id-field",user.getId());
        RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find(to_find).iterator();

        findTask.getAsync(task->{
            if(task.isSuccess()){
                MongoCursor<Document> result = null;
                result = task.get();
                while(result.hasNext()){
                    Document ds = result.next();
                    Order or = new Order();
                    LinearLayout ll = new LinearLayout(getContext());
                    ll.setOrientation(LinearLayout.HORIZONTAL);
                    or.setOrder_Name(ds.get("order_name").toString());
                    or.setOrder_status(ds.get("order_number").toString());
                    or_del.add(or);

                    new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                        @Override
                        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                            // this method is called
                            // when the item is moved.
                            return false;
                        }

                        @Override
                        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                            // this method is called when we swipe our item to right direction.
                            // on below line we are getting the item at a particular position.
                            Order deletedCourse = or_del.get(viewHolder.getAdapterPosition());

                            // below line is to get the position
                            // of the item at that position.
                            int position = viewHolder.getAdapterPosition();

                            // this method is called when item is swiped.
                            // below line is to remove item from our array list.
                            or_del.remove(viewHolder.getAdapterPosition());

                            // below line is to notify our item is removed from adapter.
                            orderAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());

                            // below line is to display our snackbar with action.
                            Snackbar.make(recyclerView, deletedCourse.getOrder_Name(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // adding on click listener to our action of snack bar.
                                    // below line is to add our item to array list with a position.
                                    or_del.add(position, deletedCourse);

                                    // below line is to notify item is
                                    // added to our adapter class.
                                    orderAdapter.notifyItemInserted(position);
                                }
                            }).show();
                        }
                        // at last we are adding this
                        // to our recycler view.
                    }).attachToRecyclerView(recyclerView);


                    //Toast.makeText(getContext(),"Please Wait..",Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(getContext(),"Couldn't retrive data",Toast.LENGTH_SHORT).show();
            }
        });

    }


}