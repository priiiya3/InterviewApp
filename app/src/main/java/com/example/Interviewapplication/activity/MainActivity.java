package com.example.Interviewapplication.activity;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Interviewapplication.R;
import com.example.Interviewapplication.adapter.ListAdapter;
import com.example.Interviewapplication.bottomSheetFragment.CreateEditBottomSheetFragment;
import com.example.Interviewapplication.bottomSheetFragment.ShowCalendarViewBottom;
import com.example.Interviewapplication.broadcastReceiver.ReminderBroadcastReceiver;
import com.example.Interviewapplication.database.DatabaseClient;
import com.example.Interviewapplication.model.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements CreateEditBottomSheetFragment.setRefreshListener {

    @BindView(R.id.ListRecycler)
    RecyclerView ListRecycler;
    @BindView(R.id.addTask)
    TextView addTask;
    ListAdapter taskAdapter;
    List<Task> tasks = new ArrayList<>();
    @BindView(R.id.calendar)
    ImageView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpAdapter();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ComponentName receiver = new ComponentName(this, ReminderBroadcastReceiver.class);
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        addTask.setOnClickListener(view -> {
            CreateEditBottomSheetFragment createTaskBottomSheetFragment = new CreateEditBottomSheetFragment();
            createTaskBottomSheetFragment.setTaskId(0, false, this, MainActivity.this);
            createTaskBottomSheetFragment.show(getSupportFragmentManager(), createTaskBottomSheetFragment.getTag());
        });

        getSavedTasks();

        calendar.setOnClickListener(view -> {
            ShowCalendarViewBottom showCalendarViewBottomSheet = new ShowCalendarViewBottom();
            showCalendarViewBottomSheet.show(getSupportFragmentManager(), showCalendarViewBottomSheet.getTag());
        });
    }

    public void setUpAdapter() {
        taskAdapter = new ListAdapter(this, tasks, this);
        ListRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ListRecycler.setAdapter(taskAdapter);
    }

    private void getSavedTasks() {

        class GetSavedTasks extends AsyncTask<Void, Void, List<Task>> {
            @Override
            protected List<Task> doInBackground(Void... voids) {
                tasks = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .dataBaseAction()
                        .getAllTasksList();
                return tasks;
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                setUpAdapter();
            }
        }

        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute();
    }

    @Override
    public void refresh() {
        getSavedTasks();
    }
}
