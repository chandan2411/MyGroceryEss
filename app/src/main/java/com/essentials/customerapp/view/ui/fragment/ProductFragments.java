package com.essentials.customerapp.view.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.essentials.customerapp.R;
import com.essentials.customerapp.data.model.GrandCategoryModel;
import com.essentials.customerapp.data.repository.ProductsRepository;
import com.essentials.customerapp.firebase.models.ProductModel;
import com.essentials.customerapp.interfaces.AddorRemoveCallbacks;
import com.essentials.customerapp.view.ui.adapter.ProductAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProductFragments extends Fragment {

    private String gCatID = "";
    private List<ProductModel> productModelList;
    private Context mContext;
    private RecyclerView rvProduct;
    private RelativeLayout rlError;
    String Tag = "List";
    private static final String TAG = "ProductFragments";
    private ProductAdapter mAdapter;
    private int position;
    AddorRemoveCallbacks listener;


//    public static ProductFragments newInstance(List<GrandCategoryModel> gCatID, int position) {
    public static ProductFragments newInstance(List<ProductModel> productList, int position) {

        Bundle args = new Bundle();
//        args.putString("G_CAT_ID", gCatID.get(position).getGrand_Cat_Id());
        args.putParcelableArrayList("ProductList", (ArrayList<? extends Parcelable>) productList);;
        args.putInt("position", position);
        ProductFragments fragment = new ProductFragments();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);

    }

    public int getPosition() {
        return position;
    }

    private void observeProductData(String gCatID) {
        ProductsRepository.getInstance().getProductList("", gCatID, "", "", "")
                .observe((AppCompatActivity) mContext, new Observer<List<ProductModel>>() {
                    @Override
                    public void onChanged(List<ProductModel> productModels) {
                        if (productModels != null) {
                            if (productModels.isEmpty()) {
                                rlError.setVisibility(View.VISIBLE);
                                rvProduct.setVisibility(View.GONE);
                                mAdapter.setDataList(new ArrayList<>());
                            } else {
                                rlError.setVisibility(View.GONE);
                                rvProduct.setVisibility(View.VISIBLE);
                                mAdapter.setDataList(productModels);
                            }
                        }
                    }
                });
    }


    private void setUpRecyclerView() {
        mAdapter = new ProductAdapter(mContext);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        rvProduct.setLayoutManager(mLayoutManager);
        rvProduct.setItemAnimator(new DefaultItemAnimator());
        rvProduct.setAdapter(mAdapter);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment, container, false);
        rvProduct = view.findViewById(R.id.rvProduct);
        rlError = view.findViewById(R.id.rlError);

        Log.i(TAG, "onCreateView: " + getContext());

        if (getArguments() != null) {
//            gCatID = getArguments().getString("G_CAT_ID");
            productModelList = getArguments().getParcelableArrayList("ProductList");
            position = getArguments().getInt("position");
        }
        setUpRecyclerView();

        /*ObserveCartData*/

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (productModelList.isEmpty()) {
            rlError.setVisibility(View.VISIBLE);
            rvProduct.setVisibility(View.GONE);
            mAdapter.setDataList(new ArrayList<>());
        } else {
            rlError.setVisibility(View.GONE);
            rvProduct.setVisibility(View.VISIBLE);
            mAdapter.setDataList(productModelList);
        }
//        observeProductData(gCatID);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        try {
            listener = (AddorRemoveCallbacks) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnFragmentInteractionListener");
        }
        super.onAttach(context);
    }
}
