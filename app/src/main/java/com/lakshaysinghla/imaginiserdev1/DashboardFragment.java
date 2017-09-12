package com.lakshaysinghla.imaginiserdev1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import static com.lakshaysinghla.imaginiserdev1.MainActivity.list;

/**
 * Created by Lakshay Singhla on 28-Jun-17.
 */

public class DashboardFragment extends Fragment {

    public static DashboardFragment newFragment(){
        return new DashboardFragment();
    }

    PhotoListViewAdapter adapter;
    RecyclerView rv;
    //ArrayList<PhotoListItem> list = new ArrayList<>();
    //String path;
    MainActivity mainActivity;
    boolean positiveButtonPressed = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.dashborad_fragment , container, false);
        rv= (RecyclerView) rootview.findViewById(R.id.photo_grid);
        setHasOptionsMenu(true);

        if(list.size() > 0){
            ((LinearLayout)rootview.findViewById(R.id.your_photos)).setVisibility(View.VISIBLE);
            ((LinearLayout)rootview.findViewById(R.id.empty_screen)).setVisibility(View.GONE);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new PhotoListViewAdapter(getContext());
            //adapter.getlist(list);
            rv.setAdapter(adapter);
            initSwipe();
        }else{
            ((LinearLayout)rootview.findViewById(R.id.your_photos)).setVisibility(View.GONE);
            ((LinearLayout)rootview.findViewById(R.id.empty_screen)).setVisibility(View.VISIBLE);
        }

        return rootview;
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.RIGHT) {
                    deleteDialog(position);
                    /*File f = new File(list.get(position).getPath());
                    boolean delete = f.delete();
                    if(delete){
                        adapter.removeItem(position);
                    }
                    else {
                        Toast.makeText(getContext(),"File Can't be Deleted",Toast.LENGTH_LONG).show();
                    }
                    */
                    //deleteSubscriptionDialog(position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    if (dX > 0) {
                        Paint p = new Paint();
                        Bitmap icon;
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(),(float) dX, (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete);
                        RectF icon_delete = new RectF((float) itemView.getLeft() + 4*width ,(float) itemView.getTop() + width,(float) itemView.getLeft() + 5*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_delete,p);
                        RectF text = new RectF((float)itemView.getLeft()+ width, (float)itemView.getTop()+width, (float)itemView.getLeft()+ 3*width, (float)itemView.getBottom()-width);
                        Paint textPaint = new Paint();
                        textPaint.setColor(Color.WHITE);
                        textPaint.setTextSize(60f);
                        c.drawText("Delete",text.left,text.bottom-15f,textPaint);
                        /*
                        Paint p = new Paint();
                        Bitmap icon;
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete);
                        RectF icon_delete = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_delete,p);
                        RectF text = new RectF((float)itemView.getRight()- 5*width, (float)itemView.getTop()+width, (float)itemView.getRight()- 3*width, (float)itemView.getBottom()-width);
                        Paint textPaint = new Paint();
                        textPaint.setColor(Color.WHITE);
                        textPaint.setTextSize(60f);
                        c.drawText("Delete",text.left,text.bottom-15f,textPaint);
                        */
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv);
    }

    public void getMainActivity(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    private void deleteDialog(final int position){
        LayoutInflater li = (mainActivity).getLayoutInflater();
        View v = li.inflate(R.layout.dialog_delete,null);
        final CheckBox checkBox = (CheckBox) v.findViewById(R.id.delete_from_phone);
        checkBox.setChecked(true);
        final TextView tv = (TextView) v.findViewById(R.id.delete_text);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    checkBox.setChecked(false);
                }
                else checkBox.setChecked(true);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Do you really want to delete this photo ?");
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Yes", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                positiveButtonPressed = true;
                if(checkBox.isChecked()) {
                    File f = new File(list.get(position).getPath());
                    boolean delete = f.delete();
                    if (delete) {
                        adapter.removeItem(position);
                    } else {
                        Toast.makeText(getContext(), "File Can't be Deleted", Toast.LENGTH_LONG).show();
                    }
                } else{
                    Set<String> set = getPreference("list");
                    set.add( list.get(position).getName() );
                    setPreference("list",set);
                    adapter.removeItem(position);
                }
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"NO",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!positiveButtonPressed) {
                    PhotoListItem temp = list.get(position);
                    adapter.removeItem(position);
                    adapter.addItem(position, temp);
                }
                positiveButtonPressed = false;
            }
        });
        alertDialog.show();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //MenuInflater inflater = getContext().getMenuInflater();
        menu.clear();
        //inflater.inflate(R.menu.search_menu, menu);
        //SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        //searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        //    @Override
        //    public boolean onQueryTextSubmit(String query) {
        //        Log.v("Lak" , "Inside TextSubmit");
        //        return false;
        //    }

        //    @Override
        //    public boolean onQueryTextChange(String newText) {
        //        Log.v("Lak" , "Inside TextChange , query: "+newText);
        //        return false;
        //   }
        //});

        //return super.onCreateOptionsMenu(menu , inflater);
        super.onCreateOptionsMenu(menu, inflater);
    }

    Set<String> getPreference(String key){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> set = new HashSet<>();
        if(pref.contains(key)){
            set = pref.getStringSet(key,null);
        }
        return set;
    }

    private void setPreference(String key,Set<String> set){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(key, set).clear().apply();
    }
}
