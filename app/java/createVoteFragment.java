package com.example.e_voting_samadhan;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link createVoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class createVoteFragment<containers> extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public createVoteFragment() {
        // Required empty public constructor
    }

    EditText elec_name,topic_name,options,voterID;
    Button create_poll,insert_topic,insert_option,addvoter,file_chooser;
    Workbook workbook;
    //ArrayList<String> containers;
    TextView textView;
    private int STORAGE_REQUEST_CODE = 1;



    public static createVoteFragment newInstance(String param1, String param2) {
        createVoteFragment fragment = new createVoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    ArrayList<String> containers = new ArrayList<String>();


    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == Activity.RESULT_OK)
                    {
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        Toast.makeText(getContext(), uri.getPath(), Toast.LENGTH_SHORT).show();

                        WorkbookSettings ws = new WorkbookSettings();
                        ws.setGCDisabled(true);
                        File file = new File(uri.getPath().substring(uri.getPath().indexOf(":")+1));



                        if (file != null)
                        {
                            try {
                                workbook = workbook.getWorkbook(file);
                                Sheet sheet = workbook.getSheet(0);
                                for (int i=0; i<sheet.getRows(); i++)
                                {
                                    Cell[] row = sheet.getRow(i);
                                    containers.add(row[0].getContents());
                                    Toast.makeText(getContext(), containers.get(i), Toast.LENGTH_SHORT).show();
                                }


                            }

                            catch (IOException e) {
                                e.printStackTrace();
                            } catch (BiffException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            });



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_vote, container, false);



        options = view.findViewById(R.id.create_addOption);
        topic_name = view.findViewById(R.id.create_topic);
        elec_name = view.findViewById(R.id.create_electionname);
        voterID = view.findViewById(R.id.create_voterID);
        create_poll = view.findViewById(R.id.create_pollBtn);
        insert_topic = view.findViewById(R.id.create_insertTopicBtn);
        insert_option = view.findViewById(R.id.create_insertOptionBtn);
        addvoter = view.findViewById(R.id.create_AddVoter_Btn);
        file_chooser = view.findViewById(R.id.file_chooser);


        textView = view.findViewById(R.id.eg);
        insert_option.setEnabled(false);
        insert_topic.setEnabled(false);
        addvoter.setEnabled(false);
        file_chooser.setEnabled(false);



        file_chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                intent.setType("*/*");
//                intent = Intent.createChooser(intent,"choose a csv file");
//                activityResultLauncher.launch(intent);

                //textView.setText(containers.get(0));

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.setType("*/*");
                    intent = Intent.createChooser(intent,"choose a csv file");
                    activityResultLauncher.launch(intent);
                }else
                {
                    requestStoragePermission();
                }

            }


        });

        FirebaseUser user2 = FirebaseAuth.getInstance().getCurrentUser();

        //<--------------------------------------------------------------->
        // for  creating poll

        create_poll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( !elec_name.getText().toString().equals(""))
                {

                    String e = elec_name.getText().toString();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> user = new HashMap<>();
                    user.put("election_name", e);
                    user.put("organizer", user2.getDisplayName());
                    user.put("winner", "");

                    String docId = RandomString.getAlphaNumericString(8);


                    String auth_string = "",temp;
                    temp = user2.getUid();
                    for (int i=0;i<12;i++)
                    {
                        char c = temp.charAt(i);
                        auth_string = auth_string+String.valueOf(c);

                    }


                    docId = auth_string+docId;




                    String finalDocId = docId;
                    db.collection("users")
                            .document(finalDocId)
                            .collection("overview")
                            .document("document")
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused)
                                {
                                   insert_topic.setEnabled(true);
                                   create_poll.setEnabled(false);
                                    Toast.makeText(getContext(), "Poll created successfully", Toast.LENGTH_SHORT).show();
//<----------------------------------------------------------------------->

                                    //for inserting topic name
                                    insert_topic.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            if (!topic_name.getText().toString().equals(""))
                                            {
                                                String tn = topic_name.getText().toString();

                                                Map<String, Object> topic = new HashMap<>();
                                                topic.put("topic_name",tn);

                                                db.collection("users")
                                                        .document(finalDocId)
                                                        .collection("topic_name")
                                                        .add(topic)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {

                                                                insert_option.setEnabled(true);
                                                                addvoter.setEnabled(true);
                                                                file_chooser.setEnabled(true);
                                                                insert_topic.setEnabled(false);
                                                                Toast.makeText(getContext(), "Topic inserted successfully", Toast.LENGTH_SHORT).show();
//<-------------------------------------------------------------------------------------------------------->
                                                                // for inserting options

                                                                insert_option.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {

                                                                        if (!options.getText().toString().equals(""))
                                                                        {
                                                                            String opt = options.getText().toString();
                                                                            Map<String,Object> option = new HashMap<>();
                                                                            option.put("name",opt);
                                                                            option.put("vote_count","0");

                                                                            db.collection("users")
                                                                                    .document(finalDocId)
                                                                                    .collection("options_candidate_name")
                                                                                    .add(option)
                                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                        @Override
                                                                                        public void onSuccess(DocumentReference documentReference) {
                                                                                            insert_option.setText("next option");
                                                                                            options.setText("");
                                                                                            Toast.makeText(getContext(), "Option inserted successfully", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    });
                                                                        }

                                                                    }
                                                                });

                                                                addvoter.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v)
                                                                    {
                                                                        if (!voterID.getText().toString().equals(""))
                                                                        {
                                                                            String vID = voterID.getText().toString().trim();

                                                                            Map<String,Object> uid = new HashMap<>();
                                                                            uid.put(vID,"false");

                                                                            db.collection("users")
                                                                                    .document(finalDocId)
                                                                                    .set(uid, SetOptions.merge())
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused)
                                                                                        {
                                                                                           addvoter.setText("Add another voter");
                                                                                           voterID.setText("");
                                                                                           Toast.makeText(getContext(), "Voter added successfully", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    });
                                                                        }

                                                                    }
                                                                });
                                                                //---------------------------


                                                            }
                                                        });


                                            }
                                        }
                                    });


                                }
                            });

                }
                else
                {
                    insert_option.setEnabled(false);
                    insert_topic.setEnabled(false);
                }


            }
        });



        return view;
    }

    private void requestStoragePermission()
    {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_REQUEST_CODE);
        }
        else
            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_REQUEST_CODE)
        {
            if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getContext(), "permission granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}