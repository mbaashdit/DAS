package com.aashdit.districtautomationsystem.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.aashdit.districtautomationsystem.R;
import com.aashdit.districtautomationsystem.Util.Constants;
import com.aashdit.districtautomationsystem.Util.SharedPrefManager;


public class NavigationFragment extends Fragment implements View.OnClickListener {

    public DrawerLayout mDrawerLayout;
    SharedPrefManager sp;
    private TextView category_tv;
    private TextView mTvHmeDecor;
    private TextView mTvClothing;
    private TextView mTvStationary;
    private TextView mTvMore;
    private TextView mTvWishlist;
    private TextView mTvAccount;
    private TextView mTvOrders;
    private TextView mTvContact;
    private TextView mTvFaq;
    private MenuClickListener listener;


    public NavigationFragment() {
        // Required empty public constructor
    }

    public static NavigationFragment newInstance(String param1, String param2) {

        return new NavigationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = SharedPrefManager.getInstance(getActivity());

        TextView mTvInitial = view.findViewById(R.id.intial);
        TextView mTvClosure = view.findViewById(R.id.closer);
        TextView mTvCpassword = view.findViewById(R.id.changepassword);
        TextView mTvLogout = view.findViewById(R.id.logout);
        TextView mTvUserNmae = view.findViewById(R.id.user_name);
        TextView mTvUserRole = view.findViewById(R.id.user_role);
        ImageView mIvUserImage = view.findViewById(R.id.iv_user_image);

        mTvUserNmae.setText(sp.getStringData(Constants.NAME));
        mTvUserRole.setText(sp.getStringData(Constants.ROLE_NAME));
        Glide.with(getActivity()).load(sp.getStringData(Constants.IMAGE)).placeholder(R.drawable.avatardefault).into(mIvUserImage);


        mTvInitial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickMenuItem(0);
            }
        });
        mTvClosure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickMenuItem(1);
            }
        });
        mTvCpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickMenuItem(2);
            }
        });

        mTvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickMenuItem(3);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.intial:
                listener.onClickMenuItem(0);
                break;
            case R.id.closer:
                listener.onClickMenuItem(1);
                break;

            case R.id.changepassword:
                listener.onClickMenuItem(2);
                break;

            case R.id.logout:
                listener.onClickMenuItem(3);
                break;


            default:
                break;
        }
    }

    public void setUp(DrawerLayout drawerLayout, MenuClickListener listener) {

        mDrawerLayout = drawerLayout;
        this.listener = listener;
    }

    @Override
    public void onDestroy() {
        mDrawerLayout = null;
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    public interface MenuClickListener {
        void onClickMenuItem(int position);
    }
}
