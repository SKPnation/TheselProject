package com.skiplab.theselproject.Questionnaire;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skiplab.theselproject.AddPost.CategoryActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Search.SearchFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class FourteenthQuestionnaire extends Fragment {

    private static final String DEFAULT_LOCAL = "Nigeria";

    Button selectBtn, createBtn;
    Spinner sp_country;

    public FourteenthQuestionnaire() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fourteenth_questionnaire, container, false);

        selectBtn = view.findViewById(R.id.selectBtn);
        createBtn = view.findViewById(R.id.createBtn);
        sp_country = view.findViewById(R.id.sp_country);

        Bundle b = this.getArguments();
        if(b != null){
            String s =b.getString("referred");
        }

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCountryList();
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customPIN = generatePIN();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Do not forget your PIN");
                builder.setCancelable(false);
                builder.setMessage(customPIN);

                builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseReference pinRef = FirebaseDatabase.getInstance().getReference("pins");
                        pinRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("requestPin")
                                .setValue(customPIN)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dialog.dismiss();

                                        createBtn.setVisibility(View.GONE);

                                        getActivity().finish();
                                    }
                                });

                    }
                });

                builder.show();
            }
        });

        return view;
    }

    private void getCountryList() {
        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<>();
        String country;
        for (Locale loc: locale){
            country = loc.getDisplayCountry();
            if (country.length() > 0 && !countries.contains(country)){
                countries.add(country);
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,countries);
        sp_country.setAdapter(adapter);
        sp_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                //Toast.makeText(getActivity(), ""+selectedItem, Toast.LENGTH_SHORT).show();

                createBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private String generatePIN() {
        String keys = "0123456789";

        StringBuilder sb = new StringBuilder(4);

        for (int i = 0; i < 4; i++) {
            int index = (int)(keys.length() * Math.random());
            sb.append(keys.charAt(index));
        }

        return sb.toString();
    }

}
