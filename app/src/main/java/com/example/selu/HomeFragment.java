package com.example.selu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private SharedPreferences sharedPreferences;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SearchView searchView;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);


        String username = sharedPreferences.getString("username", "");


        ImageButton btnKemplang = view.findViewById(R.id.btnkemplang);
        TextView tvGreeting = view.findViewById(R.id.tvHiUsername);
        ImageButton btnMentah = view.findViewById(R.id.btnmentah);
        ImageButton btnBhnMentah = view.findViewById(R.id.btnbhnmentah);
        ImageButton btnGiling = view.findViewById(R.id.btngiling);
        ImageButton btnCabe = view.findViewById(R.id.btncabe);
        ImageButton profilButton = view.findViewById(R.id.profil);

        btnKemplang.setOnClickListener(this);
        btnMentah.setOnClickListener(this);
        btnBhnMentah.setOnClickListener(this);
        btnGiling.setOnClickListener(this);
        btnCabe.setOnClickListener(this);
        profilButton.setOnClickListener(this);

        if (!username.isEmpty()) {
            String greetingMessage = "Hi, " + username;
            tvGreeting.setText(greetingMessage);
        } else {
            // Handle the case when the username is empty or not available
            tvGreeting.setText("Hi Guest!");
        }

        return view;
    }


    @Override
    public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnkemplang:
                    startActivity(new Intent(getActivity(), KemplangActivity.class));
                    break;
                case R.id.btnmentah:
                    startActivity(new Intent(getActivity(), MentahActivity.class));
                    break;
                case R.id.btnbhnmentah:
                    startActivity(new Intent(getActivity(), BhnmentahActivity.class));
                    break;
                case R.id.btngiling:
                    startActivity(new Intent(getActivity(), GilingActivity.class));
                    break;
                case R.id.btncabe:
                    startActivity(new Intent(getActivity(), CabeActivity.class));
                    break;
                case R.id.profil:
                    startActivity(new Intent(getActivity(), ProfilActivity.class));
                    break;
            }
        }
    }



