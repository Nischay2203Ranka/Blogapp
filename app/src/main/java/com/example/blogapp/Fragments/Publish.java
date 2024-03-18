package com.example.blogapp.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.blogapp.databinding.FragmentPublishBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;


public class Publish extends Fragment {

    FragmentPublishBinding binding;
    Uri filepath ;


    public Publish() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPublishBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        selectimage();

        super.onViewCreated(view, savedInstanceState);
    }

    private void selectimage() {

        binding.view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent , "Select your Image") , 101);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            filepath = data.getData();
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            binding.imgThumbnail.setVisibility(View.VISIBLE);
            binding.imgThumbnail.setImageBitmap(bitmap);
            binding.view2.setVisibility(View.VISIBLE);
            binding.bSelectimage.setVisibility(View.VISIBLE);
            uploaddata(filepath);


        }

    }

    private void uploaddata(Uri filepath) {
        binding.btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.bTitle.getText().toString().equals("")){
                    binding.bTitle.setError("Filed is required!");
                } else if (binding.bDesc.getText().toString().equals("")) {
                    binding.bDesc.setError("Filed is required !");
                } else if (binding.bAuthor.getText().toString().equals("")) {
                    binding.bAuthor.setError("Filed is required !");

                }else {
                    ProgressDialog pd = new ProgressDialog(getContext());
                    pd.setTitle("Uploading ......");
                    pd.setMessage("Please wait while we upload the data to our Firebase storage and Firestore");
                    pd.setCancelable(false);
                    pd.show();

                    String title = binding.bTitle.getText().toString();
                    String desc = binding.bDesc.getText().toString();
                    String author = binding.bAuthor.getText().toString();

                    if(filepath!=null){
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference reference = storage.getReference().child("images/" + filepath.toString()+".jpg");
                        reference.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        String file_url = task.getResult().toString();
                                        String date = (String) DateFormat.format("dd" , new Date());
                                        String month = (String) DateFormat.format("MMM" , new Date());
                                        String final_date = date + " " + month;

                                        HashMap<String , String> map = new HashMap<>();
                                        map.put("title" , title);
                                        map.put("desc" , desc);
                                        map.put("author" ,author);
                                        map.put("date" , final_date);
                                        map.put("img" , file_url);
                                        map.put("view_count" ,"0");

                                        FirebaseFirestore.getInstance().collection("Blog Application").document().set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    pd.dismiss();
                                                    Toast.makeText(getContext(), "Post Uploaded ! ", Toast.LENGTH_SHORT).show();

                                                    binding.imgThumbnail.setVisibility(View.INVISIBLE);
                                                    binding.view2.setVisibility(View.VISIBLE);
                                                    binding.bSelectimage.setVisibility(View.VISIBLE);
                                                    binding.bTitle.setText("");
                                                    binding.bDesc.setText("");
                                                    binding.bAuthor.setText("");

                                                }
                                            }
                                        });

                                    }
                                });

                            }
                        });
                    }
                }


            }
        });

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        binding=null;
    }
}