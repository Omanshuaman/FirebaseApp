package com.example.firebaseapp2.adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaseapp2.R;
import com.example.firebaseapp2.models.AllModelGroupChatList;
import com.example.firebaseapp2.models.ModelUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AdapterAllGroupChatList extends RecyclerView.Adapter<AdapterAllGroupChatList.HolderGroupChatList> {

    private final Context context;
    private final ArrayList<AllModelGroupChatList> groupChatLists;
    private final FirebaseAuth firebaseAuth;
    private String myGroupRole;


    public AdapterAllGroupChatList(Context context, ArrayList<AllModelGroupChatList> groupChatLists, FirebaseAuth firebaseAuth) {
        this.context = context;
        this.groupChatLists = groupChatLists;
        this.firebaseAuth = firebaseAuth;
    }

    @NonNull
    @Override
    public HolderGroupChatList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.allrow_groupchats_list, parent, false);

        return new HolderGroupChatList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChatList holder, int position) {

        //get data
        AllModelGroupChatList model = groupChatLists.get(position);
        final String groupId = model.getGroupId();
        String groupIcon = model.getGroupIcon();
        String groupTitle = model.getGroupTitle();

        holder.nameTv.setText("");
        holder.timeTv.setText("");
        holder.messageTv.setText("");

        //set data
        holder.groupTitleTv.setText(groupTitle);
        try {
            Picasso.get().load(groupIcon).placeholder(R.drawable.ic_group_primary).into(holder.groupIconIv);
        } catch (Exception e) {
            holder.groupIconIv.setImageResource(R.drawable.ic_group_primary);
        }

        //handle group click
        holder.itemView.setOnClickListener(v -> {

            final String myUID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

            /*Check if user already added or not
             * If added: show remove-participant/make-admin/remove-admin option (Admin will not able to change role of creator)
             * If not added, show add participant option*/
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
            ref.child(groupId).child("Participants")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                //user exists/participant

                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//
//                                if (myGroupRole.equals("creator")) {
//                                    Toast.makeText(context, "Creator of Group...", Toast.LENGTH_SHORT).show();
//
//                                }
//                                else
                                //user doesn't exists/not-participant: add
                                builder.setTitle("Add Participant")
                                        .setMessage("Add this user in this group?")
                                        .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //add user
                                                addParticipant(myUID, groupId);
                                            }
                                        })
                                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        });

    }

    private void grouprole(String groupId) {
        final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");

        ref1.child(groupId).child("Participants").child(Objects.requireNonNull(firebaseAuth.getUid()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            myGroupRole = "" + dataSnapshot.child("role").getValue();
                            Log.d("TAG", "onDataChange: "+myGroupRole);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void addParticipant(String user, String groupId) {
        //setup user data - add user in group
        String timestamp = "" + System.currentTimeMillis();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", user);
        hashMap.put("role", "participant");
        hashMap.put("timestamp", "" + timestamp);
        //add that user in Groups>groupId>Participants
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(user).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //added successfully
                        Toast.makeText(context, "Added successfully...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed adding user in group
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public int getItemCount() {
        return groupChatLists.size();
    }

    //view holder class
    static class HolderGroupChatList extends RecyclerView.ViewHolder {

        //ui views
        private final ImageView groupIconIv;
        private final TextView groupTitleTv;
        private final TextView nameTv;
        private final TextView messageTv;
        private final TextView timeTv;

        public HolderGroupChatList(@NonNull View itemView) {
            super(itemView);

            groupIconIv = itemView.findViewById(R.id.groupIconIv);
            groupTitleTv = itemView.findViewById(R.id.groupTitleTv);
            nameTv = itemView.findViewById(R.id.nameTv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
        }
    }
}