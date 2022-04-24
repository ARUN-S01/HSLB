package com.example.hslb;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hslb.placeorder.Order;
import com.example.hslb.placeorder.OrderAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import io.realm.RealmResults;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;
import io.realm.mongodb.mongo.result.DeleteResult;

import static com.example.hslb.MainActivity.app;

import org.bson.Document;

public class OrderPlaced extends Fragment {
    public static String order_name_finished;
    public static String order_status_finished;
    String m_Text = "";
    //TextView mEditText;
    LinearLayout mLayout;
    int j = 0;
    public static ArrayList<Order> order;
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> mongoCollection;
    private User user;
    public static ArrayList<Order> order_finished;

    public OrderPlaced() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(app != null){
            user = app.currentUser();
            mongoClient = user.getMongoClient("mongodb-atlas");
            mongoDatabase = mongoClient.getDatabase("HSLB");
            mongoCollection = mongoDatabase.getCollection("order_placed");
        }
        else{
            Toast.makeText(getContext(),"Please Turn on the internet",Toast.LENGTH_SHORT).show();
        }

        View rootView = inflater.inflate(R.layout.fragment_order_placed, container, false);
        TextView mEditText = new TextView(getContext());

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.floating_action_button);
        FloatingActionButton reload = (FloatingActionButton) rootView.findViewById(R.id.reload_floating_button);

        order = new ArrayList<>();
        order_finished = new ArrayList<>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.orderRecyclerView);
//        Order placedOrder = new Order();
//        placedOrder.setOrder_Name("Chaptti");
//        placedOrder.setOrder_status("Pending");
//        order.add(placedOrder);

        get_data_from_db();
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.clear();
                get_data_from_db();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("ADD ORDER");
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        j +=1;
                        Order place = new Order();
                        LinearLayout ll = new LinearLayout(getContext());
                        ll.setOrientation(LinearLayout.HORIZONTAL);
                        m_Text = input.getText().toString();

                        place.setOrder_Name(m_Text);
                        place.setOrder_status(Integer.toString(j));
                        if (app != null){
                            insert_into_DB(place.getOrder_Name(),place.getOrder_Status());
                        }
                        order.add(place);


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
                                Order deletedCourse = order.get(viewHolder.getAdapterPosition());

                                // below line is to get the position
                                // of the item at that position.
                                int position = viewHolder.getAdapterPosition();

                                // this method is called when item is swiped.
                                // below line is to remove item from our array list.
                                order.remove(viewHolder.getAdapterPosition());

                                // below line is to notify our item is removed from adapter.
                                orderAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                delete_from_db(deletedCourse.getOrder_Name(),deletedCourse.getOrder_Status());
                                // below line is to display our snackbar with action.
                                Snackbar.make(recyclerView, deletedCourse.getOrder_Name(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // adding on click listener to our action of snack bar.
                                        // below line is to add our item to array list with a position.
                                        order.add(position, deletedCourse);

                                        // below line is to notify item is
                                        // added to our adapter class.
                                        orderAdapter.notifyItemInserted(position);
                                    }
                                }).show();
                            }
                            // at last we are adding this
                            // to our recycler view.
                        }).attachToRecyclerView(recyclerView);

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

                                Order MovedCourse = order.get(viewHolder.getAdapterPosition());

                                // below line is to get the position
                                // of the item at that position.
                                int position = viewHolder.getAdapterPosition();

                                // this method is called when item is swiped.
                                // below line is to remove item from our array list.

                                order_name_finished = MovedCourse.getOrder_Name();
                                order_status_finished = MovedCourse.getOrder_Status();
                                order_finished.add(MovedCourse);
                                order.remove(viewHolder.getAdapterPosition());

                               // Toast.makeText(getContext(),order_finished.toString(),Toast.LENGTH_SHORT).show();
                                // below line is to notify our item is removed from adapter.
                                orderAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                delete_from_db(MovedCourse.getOrder_Name(),MovedCourse.getOrder_Status());

                                // below line is to display our snackbar with action.
                                Snackbar.make(recyclerView, MovedCourse.getOrder_Name(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // adding on click listener to our action of snack bar.
                                        // below line is to add our item to array list with a position.
                                        order.add(position, MovedCourse);

                                        // below line is to notify item is
                                        // added to our adapter class.
                                        orderAdapter.notifyItemInserted(position);
                                    }
                                }).show();
                            }
                            // at last we are adding this
                            // to our recycler view.
                        }).attachToRecyclerView(recyclerView);

                    //enableSwipeToDeleteAndUndo();
//                        mEditText.setText(m_Text);
//                        mEditText.setWidth(100);

//                        mEditText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
//                                LinearLayout.LayoutParams.WRAP_CONTENT));
//                        ll.addView(mEditText);

//                        mLayout.addView(ll);
                        dialog.dismiss();
                    }

                });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter((androidx.recyclerview.widget.RecyclerView) recyclerView, order, getActivity());
        recyclerView.setAdapter(orderAdapter);
        return rootView;
    }
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallBack swipeToDeleteCallback = new SwipeToDeleteCallBack(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final Order item = order.get(position);

                order.remove(item);


               // Snackbar snackbar = Snackbar.make(getActivity(), "Item was removed from the list.", Snackbar.LENGTH_LONG);
//                snackbar.setAction("UNDO", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        //order.r(item, position);
//                        recyclerView.scrollToPosition(position);
//                    }
//                });
//
//                snackbar.setActionTextColor(Color.YELLOW);
//                snackbar.show();

            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }
    public void insert_into_DB(String name,String number){
        mongoCollection.insertOne(
                new Document("user-id-field", user.getId()).append("name","Employee").append("order_name",name).append("order_number",number))
                .getAsync(result -> {
                    if (result.isSuccess()) {
                        //Toast.makeText(LoginActivity.this,"Inserted custom user data document. _id of inserted document: "
                        //    + result.get().getInsertedId(),Toast.LENGTH_SHORT).show();
//                        User user1 = app.currentUser();
//                        Document customUserData = user1.getCustomData();
                        //Toast.makeText(LoginActivity.this,customUserData.toString(),Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(LoginActivity.this,result.getError().toString(),Toast.LENGTH_SHORT).show();
                        Log.e("EXAMPLE", "Unable to insert custom user data. Error: " + result.getError());
                    }
                });
    }
    public void delete_from_db(String name,String number){
        Document to_delete = new Document();
        to_delete.append("user-id-field",user.getId());
        to_delete.append("order_name",name);
        to_delete.append("order_number",number);
        try{
            mongoCollection.deleteOne(to_delete).getAsync(result -> {
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

    public void get_data_from_db(){
        Document to_find = new Document();
        to_find.append("user-id-field",user.getId());
        //mongoCollection.find
        MongoCollection mongo_data =mongoDatabase.getCollection("order_placed");
        RealmResultTask<MongoCursor<Document>> findTask = mongo_data.find(to_find).iterator();
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
                    order.add(or);
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
                            Order deletedCourse = order.get(viewHolder.getAdapterPosition());

                            // below line is to get the position
                            // of the item at that position.
                            int position = viewHolder.getAdapterPosition();

                            // this method is called when item is swiped.
                            // below line is to remove item from our array list.
                            order.remove(viewHolder.getAdapterPosition());

                            // below line is to notify our item is removed from adapter.
                            delete_from_db(deletedCourse.getOrder_Name(),deletedCourse.getOrder_Status());
                            orderAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());

                            // below line is to display our snackbar with action.
                            Snackbar.make(recyclerView, deletedCourse.getOrder_Name(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // adding on click listener to our action of snack bar.
                                    // below line is to add our item to array list with a position.
                                    order.add(position, deletedCourse);

                                    // below line is to notify item is
                                    // added to our adapter class.
                                    orderAdapter.notifyItemInserted(position);
                                }
                            }).show();
                        }
                        // at last we are adding this
                        // to our recycler view.
                    }).attachToRecyclerView(recyclerView);

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

                            Order MovedCourse = order.get(viewHolder.getAdapterPosition());

                            // below line is to get the position
                            // of the item at that position.
                            int position = viewHolder.getAdapterPosition();

                            // this method is called when item is swiped.
                            // below line is to remove item from our array list.

                            order_name_finished = MovedCourse.getOrder_Name();
                            order_status_finished = MovedCourse.getOrder_Status();
                            order_finished.add(MovedCourse);
                            insert_into_finished();
                            delete_from_db(MovedCourse.getOrder_Name(),MovedCourse.getOrder_Status());
                            order.remove(viewHolder.getAdapterPosition());
                            // Toast.makeText(getContext(),order_finished.toString(),Toast.LENGTH_SHORT).show();
                            // below line is to notify our item is removed from adapter.
                            orderAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
//                            delete_from_db(MovedCourse.getOrder_Name(),MovedCourse.getOrder_Status());

                            // below line is to display our snackbar with action.
                            Snackbar.make(recyclerView, MovedCourse.getOrder_Name(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // adding on click listener to our action of snack bar.
                                    // below line is to add our item to array list with a position.
                                    order.add(position, MovedCourse);

                                    // below line is to notify item is
                                    // added to our adapter class.
                                    orderAdapter.notifyItemInserted(position);
                                }
                            }).show();
                        }
                        // at last we are adding this
                        // to our recycler view.
                    }).attachToRecyclerView(recyclerView);
                    Toast.makeText(getContext(),"Please Wait..",Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(getContext(),"Could'nt retrive data",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void insert_into_finished(){
        MongoCollection mongoCollection1 = mongoDatabase.getCollection("order_finished");
        mongoCollection1.insertOne(new Document("user-id-field",user.getId()).append("order_name",order_name_finished).append("order_number",order_status_finished))
                .getAsync(result -> {
                    if(result.isSuccess()){
                        //Toast.makeText(getContext(),"Success Finished",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}