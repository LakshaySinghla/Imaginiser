package com.lakshaysinghla.imaginiserdev1;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Lakshay Singhla on 10-Jul-17.
 */

public class DialogGridViewAdapter extends RecyclerView.Adapter <DialogGridViewAdapter.MyViewHolder>{
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tv ;
        ImageView icon;
        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            icon = (ImageView) itemView.findViewById(R.id.grid_icon);
            tv = (TextView) itemView.findViewById(R.id.grid_label);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Test");
            intent.putExtra(Intent.EXTRA_TEXT, "Hello Testing something");
            intent.putExtra("share_body" , "Hello Testing something 1");
            ResolveInfo ri = ri_list.get(getAdapterPosition());
            ActivityInfo activity=ri.activityInfo;
            ComponentName name=new ComponentName(activity.applicationInfo.packageName, activity.name);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            intent.setComponent(name);
            context.startActivity(intent);
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            //Toast.makeText(context , "App: " + ri.loadLabel(pm) , Toast.LENGTH_LONG).show();
        }
    }

    Context context;
    Dialog dialog;
    List<ResolveInfo> ri_list;
    private PackageManager pm=null;

    public DialogGridViewAdapter(Context context , PackageManager pm , Dialog d){
        this.context = context;
        this.pm =pm;
        this.dialog = d;
    }

    public void getlist(List<ResolveInfo> ri){
        this.ri_list = ri;
    }

    @Override
    public int getItemCount() {
        return ri_list.size();
    }

    @Override
    public DialogGridViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_item,parent , false);
        DialogGridViewAdapter.MyViewHolder holder = new DialogGridViewAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DialogGridViewAdapter.MyViewHolder holder, int position) {
        holder.tv.setText(ri_list.get(position).loadLabel(pm));
        holder.icon.setImageDrawable(ri_list.get(position).loadIcon(pm));
    }

}
