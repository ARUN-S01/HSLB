package com.example.hslb;

import static com.example.hslb.MainActivity.app;
import static com.example.hslb.OrderPlaced.order_finished;
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


public class OrderFinished extends Fragment {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    LinearLayout mLayout;
    public static ArrayList<Order> order_delete;
    private MongoClient mongoClient_;
    private MongoDatabase mongoDatabase_;
    private ArrayList<Order> order_finish;
    private MongoCollection<Document> mongoCollection_;
    private User user;

    private String del_name;
    private String del_num;


    public OrderFinished() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_finished, container, false);
        //mLayout = (LinearLayout) rootView.findViewById(R.id.linearlayoutFinished);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycleFinished);
        // Inflate the layout for this fragment
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        order_finish = new ArrayList<>();
        order_delete = new ArrayList<>();

        user = app.currentUser();
        mongoClient_ = user.getMongoClient("mongodb-atlas");
        mongoDatabase_ = mongoClient_.getDatabase("HSLB");
        mongoCollection_ = mongoDatabase_.getCollection("order_finished");
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButtonFinished);
        //if(order_name_finished != null){

            //insert_into_Db_finished();
        //}
        get_from_db();
        orderAdapter = new OrderAdapter((androidx.recyclerview.widget.RecyclerView) recyclerView, order_finish, getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(orderAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               order_finish.clear();
                get_from_db();
            }
        });

        return rootView;
    }

    public void insert_into_Db_finished(){

        mongoCollection_.insertOne(new Document("user-id-field",user.getId()).append("order_name","Noodles").append("order_number","5"))
                .getAsync(result -> {
                    if(result.isSuccess()){

                    }
                    else{

                    }
                });
    }
    public void get_from_db(){
        Document to_find = new Document();
        to_find.append("user-id-field",user.getId());
        RealmResultTask<MongoCursor<Document>> findTask = mongoCollection_.find(to_find).iterator();

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
                    order_finish.add(or);

//                    new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
//                        @Override
//                        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                            // this method is called
//                            // when the item is moved.
//                            return false;
//                        }
//
//                        @Override
//                        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                            // this method is called when we swipe our item to right direction.
//                            // on below line we are getting the item at a particular position.
//                            Order deletedCourse = order_finish.get(viewHolder.getAdapterPosition());
//
//                            // below line is to get the position
//                            // of the item at that position.
//                            int position = viewHolder.getAdapterPosition();
//
//                            // this method is called when item is swiped.
//                            // below line is to remove item from our array list.
//                            order_finish.remove(viewHolder.getAdapterPosition());
//
//                            // below line is to notify our item is removed from adapter.
//                            orderAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
//
//                            // below line is to display our snackbar with action.
//                            Snackbar.make(recyclerView, deletedCourse.getOrder_Name(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    // adding on click listener to our action of snack bar.
//                                    // below line is to add our item to array list with a position.
//                                    order_finish.add(position, deletedCourse);
//
//                                    // below line is to notify item is
//                                    // added to our adapter class.
//                                    orderAdapter.notifyItemInserted(position);
//                                }
//                            }).show();
//                        }
//                        // at last we are adding this
//                        // to our recycler view.
//                    }).attachToRecyclerView(recyclerView);
                    new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
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

                            Order MovedCourse = order_finish.get(viewHolder.getAdapterPosition());

                            // below line is to get the position
                            // of the item at that position.
                            int position = viewHolder.getAdapterPosition();

                            // this method is called when item is swiped.
                            // below line is to remove item from our array list.

                            order_name_finished = MovedCourse.getOrder_Name();
                            order_status_finished = MovedCourse.getOrder_Status();
                            order_delete.add(MovedCourse);
                            order_finish.remove(viewHolder.getAdapterPosition());
                            // Toast.makeText(getContext(),order_finished.toString(),Toast.LENGTH_SHORT).show();
                            // below line is to notify our item is removed from adapter.
                            orderAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                            insert_into_delivered();
                            delete_from_db(MovedCourse.getOrder_Name(),MovedCourse.getOrder_Status());

                            // below line is to display our snackbar with action.
                            Snackbar.make(recyclerView, MovedCourse.getOrder_Name(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // adding on click listener to our action of snack bar.
                                    // below line is to add our item to array list with a position.
                                    order_finish.add(position, MovedCourse);

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
    public void delete_from_db(String name,String number){
        Document to_delete = new Document();
        to_delete.append("user-id-field",user.getId());
        to_delete.append("order_name",name);
        to_delete.append("order_number",number);
        try{
            mongoCollection_.deleteOne(to_delete).getAsync(result -> {
                if(result.isSuccess()){
                    //Toast.makeText(getContext(),"Done",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e){
            Toast.makeText(getContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    public void insert_into_delivered(){
        MongoCollection mongoCollection1 = mongoDatabase_.getCollection("order_delivered");
        mongoCollection1.insertOne(new Document("user-id-field",user.getId()).append("order_name",order_name_finished).append("order_number",order_status_finished))
                .getAsync(result -> {
                    if(result.isSuccess()){
                        //Toast.makeText(getContext(),"Success Finished",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}