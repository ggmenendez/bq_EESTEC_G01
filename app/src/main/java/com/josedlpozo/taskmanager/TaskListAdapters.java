package com.josedlpozo.taskmanager;

import android.app.ActivityManager.RunningTaskInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.josedlpozo.fragments.ProcessesFragment;
import com.josedlpozo.optimiza.R;

import java.util.ArrayList;
import java.util.List;

public class TaskListAdapters {

    public final static class TasksListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<RunningTaskInfo> list;
        private ProcessesFragment ctx;
        private PackageManager pm;

        public TasksListAdapter(ProcessesFragment context, List<RunningTaskInfo> list) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context.getActivity());

            this.list = list;
            this.ctx = context;
            this.pm = ctx.getActivity().getPackageManager();
        }

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.fragment_recyclerview, null);

                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.img);
                holder.text_name = (TextView) convertView.findViewById(R.id.name);
                holder.text_size = (TextView) convertView.findViewById(R.id.num);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            RunningTaskInfo ti = list.get(position);
            convertView.setVisibility(View.VISIBLE);
            String cmd = ti.baseActivity.getPackageName();
            holder.text_name.setText(cmd);
            ProcessInfo.PsRow row = ctx.getProcessInfo().getPsRow(cmd);
            if (row == null) {
                holder.text_size.setText(R.string.memory_unknown);
            } else {
                holder.text_size.setText((int) Math.ceil(row.mem / 1024) + "K");
            }
            return convertView;
        }

    }

    public final static class ProcessListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private ArrayList<DetailProcess> list;
        private ProcessesFragment ctx;
        private PackageManager pm;

        public ProcessListAdapter(ProcessesFragment context, ArrayList<DetailProcess> list) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context.getActivity());

            this.list = list;
            this.ctx = context;
            this.pm = ctx.getActivity().getPackageManager();
        }

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.fragment_recyclerview, null);

                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.img);
                holder.text_name = (TextView) convertView.findViewById(R.id.name);
                holder.text_size = (TextView) convertView.findViewById(R.id.num);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final DetailProcess dp = list.get(position);
            convertView.setVisibility(View.VISIBLE);
            String cmd = dp.getRuninfo().processName;
            holder.icon.setImageDrawable(dp.getAppinfo().loadIcon(pm));
            holder.text_name.setText(dp.getTitle());

            ProcessInfo.PsRow row = dp.getPsrow();
            if (row == null) {
                holder.text_size.setText(R.string.memory_unknown);
            } else {
                holder.text_size.setText((int) Math.ceil(row.mem / 1024) + "K");
            }
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    MiscUtil.getTaskMenuDialog(ctx, dp).show();
                }

            });
            return convertView;
        }

    }

    private static class ViewHolder {
        ImageView icon;
        TextView text_name;
        TextView text_size;
    }

}
