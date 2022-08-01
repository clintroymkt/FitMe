package com.example.MyHealth.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.MyHealth.R;

public class DocOfflineFrag extends Fragment implements AdapterView.OnItemClickListener{
    public DocOfflineFrag(){

    }




    public static DocOfflineFrag getInstance(){
        DocOfflineFrag docOfflineFrag = new DocOfflineFrag();
        return docOfflineFrag;
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.row_docoffline, container,false);





        return view;



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView doclistView;
        String[] doctorName = {"Doctor 1", "Doctor 2","Doctor 3", "Doctor 4","Doctor 5"};
        String[] specialty = {"Specialty", "Specialty","Specialty", "Specialty","Specialty"};
        int[] doctorImage = {R.drawable.ic_patient,R.drawable.ic_patient,R.drawable.ic_patient,R.drawable.ic_patient,R.drawable.ic_patient};

        doclistView = (ListView)view.findViewById(R.id.offlineDocList);
        ArrayAdapter<String> adapter= new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,doctorName);

        doclistView.setAdapter(adapter);
        doclistView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position==0)
        {
            Intent intent = new Intent(getActivity().getApplicationContext(), ScheduleConsult.class);
            startActivity(intent);
            getActivity().finish();
        }
        if (position==1)
        {
            Intent intent = new Intent(getActivity().getApplicationContext(), ScheduleConsult.class);
            startActivity(intent);
            getActivity().finish();
        }
        if (position==2)
        {
            Intent intent = new Intent(getActivity().getApplicationContext(), ScheduleConsult.class);
            startActivity(intent);
            getActivity().finish();
        }
        if (position==3)
        {
            Intent intent = new Intent(getActivity().getApplicationContext(), ScheduleConsult.class);
            startActivity(intent);
            getActivity().finish();
        }
        if (position==4)
        {
            Intent intent = new Intent(getActivity().getApplicationContext(), ScheduleConsult.class);
            startActivity(intent);
            getActivity().finish();
        }
    }




}
