package com.example.stayfit.adapter;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DoctorAdapter  extends RecyclerView.Adapter<DoctorAdapter.viewHolder>{
    ArrayList<Doctor> list;
    Context context;


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

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Doctor model=list.get(position);
        Picasso.get()
                .load(model.getImageurl())
                .into(holder.image);

//        StringBuilder availableDays= new StringBuilder(" ");
//        StringBuilder availableTime= new StringBuilder(" ");
//        StringBuilder languagesSpoken= new StringBuilder(" ");
//        String[] availableDaysArray = model.getAvailableDays();
//        String[] availableTimeArray = model.getAvailableTime();
//        String[] languagesSpokenArray = model.getLanguagesSpoken();
//        for(int i = 0; i < availableDaysArray.length-1; i++){
//            availableDays.append(" | ").append(availableDaysArray[i]);
//        }
//        availableDays.append(" | ").append(availableDaysArray[availableDaysArray.length - 1]);
//        for(int i = 0; i < availableDaysArray.length-1; i++){
//            availableTime.append(" | ").append(availableTimeArray[i]);
//        }
//        availableTime.append(" | ").append(availableTimeArray[availableTimeArray.length - 1]);
//        for(int i = 0; i < availableDaysArray.length-1; i++){
//            languagesSpoken.append(" | ").append(languagesSpokenArray[i]);
//        }
//        languagesSpoken.append(" | ").append(languagesSpokenArray[languagesSpokenArray.length - 1]);
//        holder.availableDays.setText(availableDays.toString());
//        holder.availableTime.setText(availableTime.toString());
//        holder.languagesSpoken.setText(languagesSpoken.toString());

        holder.name.setText(model.getName());
        holder.rating.setText("" + model.getRating());
        holder.speciality.setText(model.getSpeciality());
        holder.experience.setText(model.getExperience());
        holder.availableDays.setText(model.getAvailableDays());
        holder.availableTime.setText(model.getAvailableTime());
        holder.languagesSpoken.setText(model.getLanguagesSpoken());
        holder.education.setText(model.getEducation());

        holder.btnBookAppointment.setOnClickListener(view -> btnBookAppointmentClicked());

    }

    private void btnBookAppointmentClicked() {
        Toast.makeText(context, "Appointment request received", Toast.LENGTH_SHORT).show();
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
