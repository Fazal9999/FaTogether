package com.kptech.peps.activity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kptech.peps.R;
import com.kptech.peps.model.ContentRequestData;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.recycler.IntrestSelAdapter;
import com.kptech.peps.server.BackendServer;
import com.kptech.peps.server.ResponseReceiver;
import com.kptech.peps.utils.DataValidator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateContentNewsActivity extends AppBaseActivity {
    private static final String TAG = CreateContentNewsActivity.class.getName();
    private static final int REQ_SELECT_TOPIC = 2;
    private EditText mAccountName;
    private RelativeLayout mNicheYesLayout;
    private RecyclerView mRecyclerView;
    private IntrestSelAdapter mAdpater;
    private List<String> mSelectedTopics = new ArrayList<>();
    private String mAdultContent;
    private String type;

    EditText start_at, end_at;
    Long stat_timestamp  = System.currentTimeMillis(), end_timestamp = System.currentTimeMillis();

    TimePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_content_news);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        if (type.equals("content")){
            setHeaderView("Content Account Request");
        }else{
            setHeaderView("Podcast Account Request");
        }

        start_at = findViewById(R.id.start_at);
        end_at = findViewById(R.id.end_at);

        start_at.setInputType(InputType.TYPE_NULL);
        start_at.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(CreateContentNewsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                start_at.setText(sHour + " hrs : " + sMinute + " mins");
                                final Calendar calendar1 = Calendar.getInstance();
                                calendar1.set(Calendar.HOUR_OF_DAY, sHour);
                                calendar1.set(Calendar.MINUTE, sMinute);
                                stat_timestamp = calendar1.getTimeInMillis();
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        end_at.setInputType(InputType.TYPE_NULL);
        end_at.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(CreateContentNewsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                end_at.setText(sHour + " hrs : " + sMinute + " mins");

                                final Calendar calendar1 = Calendar.getInstance();
                                calendar1.set(Calendar.HOUR_OF_DAY, sHour);
                                calendar1.set(Calendar.MINUTE, sMinute);

                                end_timestamp  = calendar1.getTimeInMillis();
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });


        initViews();
    }

    private void initViews(){
        mAccountName = findViewById(R.id.accnt_name);

        RadioGroup group = findViewById(R.id.adult_group_layout);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.adult_yes:
                        mAdultContent = "1";
                        break;
                    case R.id.adult_no:
                        mAdultContent = "0";
                        break;
                }
            }
        });

        mAdultContent = "1";

        mNicheYesLayout = findViewById(R.id.niche_yes_layout);
        mNicheYesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateContentNewsActivity.this, SelectInterestActivity.class);
                intent.putStringArrayListExtra("SelectedTopics",(ArrayList<String>)mSelectedTopics);
                startActivityForResult(intent,REQ_SELECT_TOPIC);
            }
        });

        mRecyclerView = findViewById(R.id.recyler_grid);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),4);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdpater = new IntrestSelAdapter();
        mAdpater.setmContext(this);
        mRecyclerView.setAdapter(mAdpater);

        TextView submit = findViewById(R.id.submit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRequest();
            }
        });

    }

    private void createRequest(){
        if(DataValidator.isDataValid(mAccountName)){

            showProgressDialog(null, null);

            ContentRequestData data = new ContentRequestData();
            UserAccount accnt = DataHolder.getInstance().getmCurrentUser();
            if(accnt != null) {
                data.setEmail(accnt.getEmail());
                if(DataValidator.isValid(accnt.getFirst_name())) {
                    data.setFirst_name(accnt.getFirst_name());
                }
                if(DataValidator.isValid(accnt.getLast_name())) {
                    data.setLast_name(accnt.getLast_name());
                }
                data.setAccount_name(mAccountName.getText().toString());

                data.setIs_adult_material(mAdultContent);
                //include topics
                if(mSelectedTopics != null && mSelectedTopics.size() >0){
                    data.setInterests(mSelectedTopics);
                }

                data.setStart_at(stat_timestamp);
                data.setEnd_at(end_timestamp);

                data.setStatus("0");
                data.setUser_id(accnt.getUser_id());
                if (type.equals("content")){
                    data.setRequest_type("contentAccount");
                }else{
                    data.setRequest_type("podcastAccount");
                }

                BackendServer.getInstance().createContentRequest(data, new ResponseReceiver() {
                    @Override
                    public void onSuccess(int code, Object result) {
                        cancelProgressDialog();
                        Toast.makeText(CreateContentNewsActivity.this, "Successfully created ", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        cancelProgressDialog();
                        Toast.makeText(CreateContentNewsActivity.this, "Error creating request", Toast.LENGTH_SHORT).show();

                    }
                });



            }else{
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK && reqCode == REQ_SELECT_TOPIC){
            if(data != null) {
                List<String> list = data.getStringArrayListExtra("selectedTopic");
                if(list != null){
                    Log.d(TAG,"received list size is "+ list.size());
                    mSelectedTopics = list;
                    mAdpater.setItemList(list);
                    mAdpater.notifyDataSetChanged();
                }
            }

        }

    }
}
