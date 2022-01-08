package com.essentials.customerapp.view.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.essentials.customerapp.ProductActivity;
import com.essentials.customerapp.R;
import com.essentials.customerapp.data.repository.ProductsRepository;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.essentials.customerapp.firebase.models.ProductModel;
import com.essentials.customerapp.interfaces.SearchItemCLickListener;
import com.essentials.customerapp.utilities.Constant;
import com.essentials.customerapp.utilities.Constants;
import com.essentials.customerapp.utilities.CustomToast;
import com.essentials.customerapp.view.ui.adapter.SearchViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class    SearchActivity extends BaseActivity implements /*SearchItemCLickListener, SearchView.OnQueryTextListener*/ View.OnClickListener {

    RecyclerView recyclerView;
    ArrayList<String> mylist;
    LinearLayoutManager manager;
    ArrayAdapter adapter;
    private Toolbar toolbar;
    //    private SearchView searchView;
    private RelativeLayout rlProgress;
    private AutoCompleteTextView etSearch;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() > 2) {
                if (!etSearch.isPopupShowing()) {
                    Toast toast= Toast.makeText(getApplicationContext(),
                            "No product found!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                    return;
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        rlProgress = findViewById(R.id.rlProgress);
        etSearch = findViewById(R.id.etSearch);
        etSearch.setOnItemClickListener(adapterItemClick);
        etSearch.addTextChangedListener(textWatcher);
        adapter = new ArrayAdapter(SearchActivity.this, android.R.layout.simple_list_item_1, Constant.SEARCH_TEXT);
        etSearch.setAdapter(adapter);
        findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
       /* recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new SearchViewAdapter(this);
        recyclerView.setAdapter(adapter);*/
     //   getProductList();
        opneKeyPad();
    }

    private void opneKeyPad() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    AdapterView.OnItemClickListener adapterItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            etSearch.getText();
            Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
            intent.putExtra(Constants.COME_FROM_HOME, Constants.SEARCH);
            intent.putExtra(Constants.SEARCH_TEXT, adapter.getItem(i).toString());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            etSearch.setText("");
            SearchActivity.this.finish();
        }
    };

    List<ProductModel> productModelList = new ArrayList<>();
    List<ProductModel> searchList = new ArrayList<>();

    private void getProductList(String searchText) {
        rlProgress.setVisibility(View.VISIBLE);
       /* Query productRef = DatabaseReferences.getProductRef().orderByChild("Product_Name");
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productModelList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    rlProgress.setVisibility(View.GONE);
                    ProductModel ringtoneModel = postSnapshot.getValue(ProductModel.class);
                    productModelList.add(ringtoneModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                rlProgress.setVisibility(View.GONE);
            }
        });*/
        /*ProductsRepository.getInstance().getProductList("", "", "", searchText, "")
                .observe(this, new Observer<List<ProductModel>>() {
                    @Override
                    public void onChanged(List<ProductModel> productModels) {
                        if (productModels != null) {
                            rlProgress.setVisibility(View.GONE);
                            if (productModels.isEmpty()) {
                                CustomToast.showRectToastMiddle(SearchActivity.this, recyclerView, "No Product Found");
                                adapter.setDataList(productModels);
                            } else {
                                adapter.setDataList(productModels);
                            }
                        }
                    }
                });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*getMenuInflater().inflate(R.menu.search_view, menu);
        try {
            *//*searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            searchView.setIconified(false);
            searchView.setQueryHint("Search 10000+ Products");
            recyclerView.stopScroll();
            searchView.setGravity(Gravity.CENTER_VERTICAL);
            searchView.setOnQueryTextListener(SearchActivity.this);*//*
        } catch (Exception ex) {
            Toast.makeText(this, "Crash Error " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }*/
        return super.onCreateOptionsMenu(menu);
    }

    /*@Override
    public void onSearchItemClick(ProductModel productModel) {
        Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
        intent.putExtra(Constants.COME_FROM_HOME, Constants.SEARCH);
        intent.putExtra(Constants.SEARCH_TEXT, productModel.getProduct_Name());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        this.finish();
    }*/

    /*@Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() >= 3) {
            getProductList(newText);
        } else {
            searchList.clear();
            adapter.setDataList(searchList);
            rlProgress.setVisibility(View.GONE);
        }
        return false;
    }*/

    private void getSearchProductList(String newText) {
        /*for (ProductModel model : productModelList) {
            if (model.getProduct_Name().contains(newText)) {
                searchList.add(model);
            }
        }
        adapter.setDataList(searchList);*/
    }

    List<ProductModel> list = new ArrayList<>();

    @Override
    public void onClick(View view) {

    }
}
