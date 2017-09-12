package com.lakshaysinghla.imaginiserdev1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.io.File;

import static com.lakshaysinghla.imaginiserdev1.MainActivity.list;
import static com.lakshaysinghla.imaginiserdev1.MainActivity.uri_list;

/**
 * Created by Lakshay Singhla on 10-Jul-17.
 */

public class PhotoListViewAdapter extends RecyclerView.Adapter <PhotoListViewAdapter.MyViewHolder>{

    Context context;
    public PhotoListViewAdapter(Context context){
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
            ,PopupMenu.OnMenuItemClickListener{
        TextView tv ;
        //SimpleDraweeView icon;
        ImageView icon;
        TextView size;
        ImageView option;
        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            //icon = (SimpleDraweeView) itemView.findViewById(R.id.photo_icon);
            icon = (ImageView) itemView.findViewById(R.id.photo_icon);
            tv = (TextView) itemView.findViewById(R.id.photo_name);
            size = (TextView) itemView.findViewById(R.id.photo_size);
            option = (ImageView) itemView.findViewById(R.id.option);
            option.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.option) {
                PopupMenu pm = new PopupMenu(context, option);
                ((Activity) context).getMenuInflater().inflate(R.menu.option_menu, pm.getMenu());
                pm.setOnMenuItemClickListener(this);
                pm.show();
            }
            else if (v.getId() == R.id.list_element){
                new ImageViewer.Builder(context , uri_list)
                        .setStartPosition(getAdapterPosition())
                        .allowSwipeToDismiss(false)
                        .show();
                //new ImageViewer.Builder(context , MainActivity.uri_list).setStartPosition(getAdapterPosition()+1).show();

                //Intent i = new Intent();
                //i.setAction(Intent.ACTION_VIEW);
                //String s = list.get(getAdapterPosition()).getPath() ;
                //i.setDataAndType( MainActivity.uri_list.get(getAdapterPosition()) , "image/*");
                //context.startActivity(i);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(item.getItemId() == R.id.properties) {
                LayoutInflater li = ((Activity)context).getLayoutInflater();
                View v = li.inflate(R.layout.dialog_properties,null);
                TextView name  = (TextView) v.findViewById(R.id.Name);
                TextView size  = (TextView) v.findViewById(R.id.Size);
                TextView dimen  = (TextView) v.findViewById(R.id.Dimen);
                TextView createdAt  = (TextView) v.findViewById(R.id.CreatedAt);
                TextView path  = (TextView) v.findViewById(R.id.Path);

                PhotoListItem photo_item = list.get(getAdapterPosition());
                name.setText(photo_item.getName());
                size.setText(photo_item.getSize());
                createdAt.setText(photo_item.getCreatedAt());
                path.setText(photo_item.getPath());
                int h = photo_item.getHeight();
                int w = photo_item.getWidth();
                dimen.setText(h + " X " + w);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Properties");
                alertDialogBuilder.setView( v );
                AlertDialog alert = alertDialogBuilder.create();

                alertDialogBuilder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                alertDialogBuilder.show();
                return true;
            }
            else if(item.getItemId() == R.id.share){

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                //File f = new File(MainActivity.list.get(getAdapterPosition()).getPath());
                Uri screenshotUri = uri_list.get(getAdapterPosition());
                sharingIntent.setType("image/jpeg");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                context.startActivity( Intent.createChooser(sharingIntent, "Share image using") );

                /*
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Test");
                intent.putExtra(Intent.EXTRA_TEXT, "Hello Testing something");
                intent.putExtra("share_body" , "Hello Testing something 1");
                //context.startActivityForResult( Intent.createChooser(intent , "Share Via (Lakshay)") , Request_Resize );


                PackageManager pm = context.getPackageManager();
                List<ResolveInfo> ri = pm.queryIntentActivities(intent , 0);
                Collections.sort(ri, new ResolveInfo.DisplayNameComparator(pm));

                final Dialog dialog = new Dialog(context);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.custom_dialog);

                RecyclerView gv = (RecyclerView) dialog.findViewById(R.id.dialog_grid);
                int numberOfColumn = 3;
                gv.setLayoutManager(new GridLayoutManager(context , numberOfColumn) );
                DialogGridViewAdapter adapter = new DialogGridViewAdapter(context , pm , dialog);
                adapter.getlist(ri);
                gv.setAdapter(adapter);
                dialog.show();
                /*
                //choose only some intents to show
                List<LabeledIntent> otherAppIntentList = new ArrayList<LabeledIntent>();
                for(int i=0;i< ri.size();i++){
                    ResolveInfo thi_ri = ri.get(i);
                    String packageName = thi_ri.activityInfo.packageName;
                    Intent intentToAdd = new Intent();
                    if(packageName.contains("com.whatsapp") || packageName.contains("com.facebook.katana")){
                        intentToAdd.setComponent(new ComponentName(packageName , thi_ri.activityInfo.name));
                        intentToAdd.setAction(Intent.ACTION_SEND);
                        intentToAdd.setType("text/plain");
                        intentToAdd.setPackage(packageName);
                        intentToAdd.putExtra(Intent.EXTRA_TEXT, "Hello Testing Something 2");
                        CharSequence temp = thi_ri.loadLabel(pm);

                        otherAppIntentList.add(new LabeledIntent(intentToAdd , packageName , thi_ri.loadLabel(pm) , thi_ri.icon));
                    }
                }
                LabeledIntent[] extraIntents = otherAppIntentList.toArray(new LabeledIntent[ otherAppIntentList.size() ]);
                Intent chooserIntent  = Intent.createChooser(intent  ,"Choose Activity To Send Message");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS , extraIntents);
                startActivityForResult(chooserIntent , Request_Resize);
                */
                return true;
            }
            return false;
        }
    }

    public void removeItem(int position){
        list.remove(position);
        uri_list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position , list.size());
    }

    public void addItem(int position , PhotoListItem item){
        list.add(position , item);
        uri_list.add(position , Uri.fromFile( new File(item.getPath()) ));
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_list_item,parent , false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PhotoListItem item = list.get(position);

        //holder.icon.setImageURI( MainActivity.uri_list.get(position) );

        Glide.with(context)
                .load(item.getPath())
                .fitCenter()
                .placeholder(R.drawable.blank)
                .into(holder.icon);
        //holder.icon.setImageURI(Uri.parse(list.get(position).getPath()));
        holder.tv.setText(item.getName());
        holder.size.setText(item.getSize());

    }
}
