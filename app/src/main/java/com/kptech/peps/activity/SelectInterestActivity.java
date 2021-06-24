package com.kptech.peps.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kptech.peps.R;
import com.kptech.peps.interfaces.RecyclerItemSelectedListener;
import com.kptech.peps.model.TopicModel;
import com.kptech.peps.recycler.TopicAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectInterestActivity extends AppBaseActivity {
    private List<TopicModel> interestModelList = new ArrayList<>();
    private List<TopicModel> filteredTopicList = new ArrayList<>();
    private List<String> selectedTopics = new ArrayList<>();
    private TopicAdapter adapter;
    RecyclerView recyclerView;
    Button done_button;
    EditText search_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_interet);
        recyclerView = findViewById(R.id.recycler_view);
        done_button = findViewById(R.id.done_button);
        search_text = findViewById(R.id.search_text);
        List<String> list = getIntent().getStringArrayListExtra("SelectedTopics");
        if(list != null){
            selectedTopics = list;
        }
        getTopicsList();

        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String query = charSequence.toString();
                filteredTopicList.clear();
                if (query.length() > 0) {
                    for (int j = 0; j < interestModelList.size(); j++) {
                        if (interestModelList.get(j).name.toLowerCase().contains(query.toLowerCase())) {
                            filteredTopicList.add(interestModelList.get(j));
                        }
                    }
                    setTopicsList(filteredTopicList);
                } else {
                    setTopicsList(interestModelList);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResult();
            }
        });
    }

    private void getTopicsList() {
//        String[] topicsList = getResources().getStringArray(R.array.IntrestList);
//        ArrayList<String> topicList = new ArrayList<>(Arrays.asList(topicsList));
//        interestModelList.clear();
//        for (int i = 0; i < topicList.size(); i++) {
//            TopicModel model = new TopicModel();
//            model.name = topicList.get(i);
//            interestModelList.add(model);
//        }
//        setTopicsList(interestModelList);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Interests");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                interestModelList.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    TopicModel model = postSnapshot.getValue(TopicModel.class);
                    interestModelList.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        Log.d("aaa", interestModelList.toString()+"/"+ interestModelList.size());

        setTopicsList(interestModelList);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendResult();
    }

    private void sendResult() {
        Intent intent = new Intent();
        if (selectedTopics.size() > 0){
            intent.putStringArrayListExtra("selectedTopic", (ArrayList<String>) selectedTopics);
            setResult(RESULT_OK, intent);
            finish();
        }else{
            finish();
        }
    }

    private void setTopicsList(List<TopicModel> topicsList) {
        adapter = new TopicAdapter(topicsList, selectedTopics, this, new RecyclerItemSelectedListener() {
            @Override
            public void onClick(int position, Object item, View view) {
                /*TopicModel interestModel = (TopicModel) item;
                if (interestModel.isChecked) {
                    if (!selectedTopics.contains(interestModel.name))
                        selectedTopics.add(interestModel.name);
                } else {
                    selectedTopics.remove(interestModel.name);
                }*/
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
