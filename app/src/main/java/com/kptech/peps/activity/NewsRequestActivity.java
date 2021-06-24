package com.kptech.peps.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kptech.peps.R;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.NewsRequestData;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.recycler.IntrestSelAdapter;
import com.kptech.peps.server.BackendServer;
import com.kptech.peps.server.ResponseReceiver;
import com.kptech.peps.utils.DataValidator;
import com.kptech.peps.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class NewsRequestActivity extends AppBaseActivity {
    private static final String TAG = NewsRequestActivity.class.getName();
    private static final int REQ_SELECT_TOPIC = 2;
    private EditText mNewsOrgname,mCompanyName,mContactName,mContactInfo;
    private RadioGroup mOfficialRepGroup, mOutlerGroup, mNicheGroup;
    private RelativeLayout mNicheYesLayout;
    private RecyclerView mRecyclerView;
    private IntrestSelAdapter mAdpater;
    private List<String> mSelectedTopics = new ArrayList<>();
    private String mRepSelection, mOutletSel, mNicheSel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_news_request);
        setHeaderView("News Account Request");
        initViews();

    }

    private void initViews(){
        mNewsOrgname = findViewById(R.id.news_org_name);
        mCompanyName = findViewById(R.id.name_of_company);
        mContactName = findViewById(R.id.contact_name);
        mContactInfo = findViewById(R.id.contact_info);

        mOfficialRepGroup = findViewById(R.id.rep_group_layout);
        mOfficialRepGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rep_yes:
                        mRepSelection = "1";
                        break;
                    case R.id.rep_no:
                        mRepSelection = "0";
                        break;
                }

            }
        });
        mRepSelection = "1";
        mOutlerGroup = findViewById(R.id.outlet_group_layout);
        mOutlerGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.outlet_yes:
                        mOutletSel = "1";
                        break;
                    case R.id.outlet_no:
                        mOutletSel = "0";
                        break;
                }

            }
        });
        mOutletSel = "1";
        mNicheGroup = findViewById(R.id.niche_group_layout);
        mNicheGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.niche_yes:
                        mNicheYesLayout.setVisibility(View.VISIBLE);
                        mNicheSel = "1";
                        break;
                    case R.id.niche_no:
                        mNicheYesLayout.setVisibility(View.GONE);
                        mNicheSel = "0";
                        break;
                }

            }
        });
        mNicheSel = "1";

        mNicheYesLayout = findViewById(R.id.niche_yes_layout);
        mNicheYesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsRequestActivity.this, SelectInterestActivity.class);
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

        Button submit = findViewById(R.id.submit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRequest();
            }
        });
    }

    private void createRequest(){
        if(DataValidator.isDataValid(mNewsOrgname) && DataValidator.isDataValid(mCompanyName) && DataValidator.isDataValid(mContactName)){

            showProgressDialog(null, null);

            NewsRequestData data = new NewsRequestData();
            UserAccount accnt = DataHolder.getInstance().getmCurrentUser();
            if(accnt != null) {
                data.setEmail(accnt.getEmail());
                data.setRow_key(Utils.getKey(accnt.getEmail()));
                if(DataValidator.isValid(accnt.getFirst_name())) {
                    data.setFirst_name(accnt.getFirst_name());
                }
                if(DataValidator.isValid(accnt.getLast_name())) {
                    data.setLast_name(accnt.getLast_name());
                }
                data.setOrg_name(mNewsOrgname.getText().toString());
                data.setCompany_name(mCompanyName.getText().toString());
                data.setIs_official_rep(mRepSelection);
                data.setRequest_type("newsAccount");
                data.setName_of_contact(mContactName.getText().toString());
                data.setStatus("0");
                if(DataValidator.isValid(mContactInfo.getText().toString())){
                    data.setContact_info(mContactInfo.getText().toString());
                }
                data.setIs_considered_news_outlet(mOutletSel);
                if(mNicheSel.equalsIgnoreCase("1")){ //YES
                    //include topics
                    if(mSelectedTopics != null && mSelectedTopics.size() >0){
                        data.setInterests(mSelectedTopics);
                    }
                }

                BackendServer.getInstance().createNewsRequest(data, new ResponseReceiver() {
                    @Override
                    public void onSuccess(int code, Object result) {
                        cancelProgressDialog();
                        Toast.makeText(NewsRequestActivity.this, "Successfully created ", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        cancelProgressDialog();
                        Toast.makeText(NewsRequestActivity.this, "Error creating request", Toast.LENGTH_SHORT).show();

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
