/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.logs.Logger;
import com.dxc.mycollector.model.TaskDetails;
import com.dxc.mycollector.model.TaskInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gospel on 2017/8/21.
 * About ShowExamineRecord
 */

public class ShowTaskInfo extends BaseActivity {
    private ListView listview;
    TextView textView;
    String result = null;
    private Button taskAdd;
    Context context;
    List<TaskInfo> listtasks = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.task_download_main_layout);
        context = this;
        taskAdd = (Button) this.findViewById(R.id.add_task);
        listview = (ListView) this.findViewById(R.id.task_listView);
//        textView = (TextView) this.findViewById(R.id.center111);
        //新增安全检查记录
        taskAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), AddExamineRecord.class));

//                Toast.makeText(context, "insert.......", Toast.LENGTH_LONG);
//                TaskInfo taskInfo = new TaskInfo();
//                taskInfo.setTaskType("T0101");
//                taskInfo.setMeasureType("T0201");
//                int result = SqliteUtils.getInstance(context).saveTaskInfo(taskInfo);
//                if (result == 1) {
//                    Logger.i(TAG, "insert taskinfo success");
//                }
//                startActivity(new Intent(context, CeLiangActivity.class));
            }
        });
        //获取已经下载的任务信息
        getAllTasks();
        //初始化ListView
        initDrawerList();
    }

    private void getAllTasks() {
        listtasks = new ArrayList<>();
        List<TaskInfo> alltask = SqliteUtils.getInstance(this).loadTasks();
        for (TaskInfo taskinfo : alltask) {
            listtasks.add(taskinfo);
        }
    }

    private void initDrawerList() {

        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                Holder holder = null;
                if (convertView == null) {
                    holder = new Holder();
                    convertView = LayoutInflater.from(context).inflate(R.layout.task_download_list_item_layout, null);
                    holder.tasknamepoint = (TextView) convertView.findViewById(R.id.show_task_name_point);
                    holder.taskname = (TextView) convertView.findViewById(R.id.show_task_name);
                    Button upbtn = (Button) convertView.findViewById(R.id.kaishicl);
                    convertView.setTag(holder);
                    upbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getApplicationContext(), CeLiangActivity.class));
//                            Toast.makeText(ShowTaskInfo.this, "接口正在开发中...", Toast.LENGTH_SHORT).show();
//                            selectItem(position);
                        }
                    });
                } else {
                    holder = (Holder) convertView.getTag();
                }
                TaskInfo taskInfo = listtasks.get(position);
                String showstr = taskInfo.getTaskId() + "-" + (taskInfo.getTaskType().equals("T0101") ? "拱顶沉降" : "水平收敛");
                showstr += "(" + getMeasureType(taskInfo.getMeasureType()) + ")";
                holder.tasknamepoint.setText(showstr);
                holder.taskname.setText(taskInfo.getStartTime() + "-" + taskInfo.getEndTime());

                return convertView;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public Object getItem(int position) {
                return listtasks.get(position);
            }

            @Override
            public int getCount() {
                return listtasks.size();
            }
        };
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new DrawerItemClickListener());
    }

    public String getMeasureType(String measureType) {
        String str = "";
        if (measureType.equals("T0201")) {
            str = "水准仪";
        }
        if (measureType.equals("T0202")) {
            str = "刚挂尺";
        }
        if (measureType.equals("T0203")) {
            str = "全站仪";
        }
        if (measureType.equals("T0204")) {
            str = "收敛计";
        }
        return str;
    }

    static class Holder {
        TextView tasknamepoint = null;
        TextView taskname = null;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            selectItem(position);
        }
    }

    /**
     * 选择行
     *
     * @param position
     */
    private void selectItem(int position) {
        Toast.makeText(this, planetTitles[position], Toast.LENGTH_SHORT).show();
        TaskInfo taskInfo = listtasks.get(position);
        TaskDetails[] detailDatas = taskInfo.getDetail();
        String[] strarr = new String[detailDatas.length];
        int i = 0;
        for (TaskDetails detailData : detailDatas) {
            strarr[i] = detailData.getMileageLabel() + "-" + detailData.getPointLabel() + "(" + detailData.getProName() + ")";
        }
        new AlertDialog.Builder(this)
                .setTitle("测量任务列表")
                .setItems(strarr, null)
                .setNegativeButton("确定", null)
                .show();
    }

    /**
     * 嵌套ListView
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}