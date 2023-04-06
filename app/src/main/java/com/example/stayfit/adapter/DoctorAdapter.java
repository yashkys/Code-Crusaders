package com.example.stayfit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stayfit.R;
import com.example.stayfit.model.Doctor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DoctorAdapter  extends RecyclerView.Adapter<DoctorAdapter.viewHolder>{
    ArrayList<Doctor> list;
    Context context;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference myRef = database.getReference("users").child(mUser.getUid());
    public DoctorAdapter(ArrayList<Doctor> list,Context context){
        this.list=list;
        this.context=context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.sample_item_list_doctor,parent,false);
        return new DoctorAdapter.viewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Doctor model=list.get(position);
        Picasso.get()
                .load(model.getImageurl())
                .into(holder.image);

        holder.name.setText(model.getName());
        holder.rating.setText(Integer.toString(model.getRating()));
        holder.speciality.setText(model.getSpeciality());
        holder.experience.setText(model.getExperience());
        holder.availableDays.setText(model.getAvailableDays());
        holder.availableTime.setText(model.getAvailableTime());
        holder.languagesSpoken.setText(model.getLanguagesSpoken());
        holder.education.setText(model.getEducation());
        final String[] usernames_arr = {" "};
        final String[] user_username = {" "};
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve user step  data
                usernames_arr[0] = " " + dataSnapshot.child("RequestedDoctor").getValue(String.class);
                user_username[0] =  dataSnapshot.child("username").getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "An Error occurred", Toast.LENGTH_SHORT).show();
            }
        });
        String[] usernamesArray = usernames_arr[0].split(",");
        for (String str : usernamesArray) {
            if (str.equals(model.getUsername())) {
                holder.btnBookAppointment.setClickable(false);
                holder.btnBookAppointment.setText("Requested");
                break;
            }
        }
        holder.btnBookAppointment.setOnClickListener(view -> {

            btnBookAppointmentClicked(model.getUsername(), user_username[0]);
            holder.btnBookAppointment.setClickable(false);
            holder.btnBookAppointment.setText("Requested");
        });

    }

    private void btnBookAppointmentClicked(String username, String user_username) {
        Toast.makeText(context, "Appointment request received", Toast.LENGTH_SHORT).show();
        assert mUser != null;
        myRef.child("RequestedDoctor").setValue(","+username);
        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("pendingRequests");
        myRef2.child("username").setValue(username);
        myRef2.child("username").child(username).setValue(user_username);
    }

    @Override
    public int getItemCount(){
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, rating, speciality, experience, availableDays, availableTime, languagesSpoken, education ;
        Button btnBookAppointment;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.doctor_image);
            name = itemView.findViewById(R.id.doctor_name);
            rating = itemView.findViewById(R.id.doctor_rating);
            speciality = itemView.findViewById(R.id.doctor_speciality);
            experience = itemView.findViewById(R.id.doctor_experience);
            availableDays = itemView.findViewById(R.id.doctor_availability_day);
            availableTime = itemView.findViewById(R.id.doctor_availability_time);
            languagesSpoken = itemView.findViewById(R.id.languages_spoken);
            education = itemView.findViewById(R.id.doctor_education);
            btnBookAppointment = itemView.findViewById(R.id.book_appointment_button);
        }
    }
}
