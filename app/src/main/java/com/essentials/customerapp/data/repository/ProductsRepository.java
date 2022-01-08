package com.essentials.customerapp.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.essentials.customerapp.data.model.GrandCategoryModel;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.essentials.customerapp.firebase.models.CategoryModel;
import com.essentials.customerapp.firebase.models.ProductModel;
import com.essentials.customerapp.firebase.models.SubCategoryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductsRepository {

    private static ProductsRepository productsRepository;
    private MutableLiveData<List<CategoryModel>> productCategory = new MutableLiveData<>();
    private MutableLiveData<List<SubCategoryModel>> productSubCategory = new MutableLiveData<>();
    private MutableLiveData<List<GrandCategoryModel>> productGrandCategory = new MutableLiveData<>();
    private MutableLiveData<List<ProductModel>> productDataList = new MutableLiveData<>();
    private MutableLiveData<ProductModel> productData = new MutableLiveData<>();

    public ProductsRepository() {
    }

    public static ProductsRepository getInstance() {
        if (productsRepository == null) {
            productsRepository = new ProductsRepository();
        }
        return productsRepository;
    }

    public MutableLiveData<List<CategoryModel>> getProductCategory() {
        productCategory.setValue(null);
        DatabaseReferences.getCategoryRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<CategoryModel> categoryModelList = new ArrayList<>();
                ArrayList<CategoryModel> categoryModelList1 = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    CategoryModel categoryModel = postSnapshot.getValue(CategoryModel.class);
                    categoryModelList.add(categoryModel);
                    productCategory.setValue(categoryModelList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                productCategory.setValue(new ArrayList<>());
            }
        });
        return productCategory;
    }

    public MutableLiveData<List<SubCategoryModel>> getSubCategoryList(String categoryID) {
        productSubCategory.setValue(null);
        DatabaseReferences.getSubCategoryRef().equalTo(categoryID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<SubCategoryModel> subCategoryModelList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    SubCategoryModel subCategoryModel = postSnapshot.getValue(SubCategoryModel.class);
                    subCategoryModelList.add(subCategoryModel);
                }
                productSubCategory.setValue(subCategoryModelList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                productSubCategory.setValue(new ArrayList<>());
            }
        });
        return productSubCategory;
    }

    public MutableLiveData<List<GrandCategoryModel>> getGrandCategoryList(String subCatID) {
        productGrandCategory.setValue(null);
        DatabaseReferences.getGrandCategoryRef().orderByChild("SubCat_Id").equalTo(subCatID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<GrandCategoryModel> grandCategoryModels = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    GrandCategoryModel grandCategoryModel = postSnapshot.getValue(GrandCategoryModel.class);
                    grandCategoryModels.add(grandCategoryModel);
                }
                productGrandCategory.setValue(grandCategoryModels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                productGrandCategory.setValue(new ArrayList<>());

            }
        });
        return productGrandCategory;
    }

    public MutableLiveData<List<ProductModel>> getProductList(String sortParams, String grandCatId,
                                                              String subCatId, String searchText, String offerId) {
        productDataList.setValue(null);
        Query productRef = null;
        if (!searchText.isEmpty()) {
//            String sText = searchText.substring(0,1).toUpperCase()+searchText.substring(1, searchText.length()-1);
//            String eText = searchText.substring(1, searchText.length()).toLowerCase();
            productRef = DatabaseReferences.getProductRef().orderByChild("Product_Name").startAt(searchText)
                    .endAt(searchText+"\uf8ff");
        } else if (!grandCatId.isEmpty()) {
            productRef = DatabaseReferences.getProductRef()/*.orderByChild(sortParams)*/.orderByChild("Product_Grand_Cat_Id")
                    .equalTo(grandCatId);
        } else if (!subCatId.isEmpty()) {
            productRef = DatabaseReferences.getProductRef()/*.orderByChild(sortParams)*/.orderByChild("productSubCategoryID")
                    .equalTo(grandCatId);
        } else if (!offerId.isEmpty()) {
            productRef = DatabaseReferences.getProductRef().orderByChild("Product_Offer_Id").equalTo(offerId);
        } else {
            productRef = DatabaseReferences.getProductRef()/*.orderByChild(sortParams)*/.orderByChild("productSubCategoryID")
                    .equalTo(grandCatId);
        }
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ProductModel> productModelList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ProductModel ringtoneModel = postSnapshot.getValue(ProductModel.class);
                    productModelList.add(ringtoneModel);
                }
                productDataList.setValue(productModelList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                productDataList.setValue(new ArrayList<>());
            }
        });

        return productDataList;
    }

    public MutableLiveData<ProductModel> getProductData(String productId) {
        productData.setValue(null);
        DatabaseReferences.getProductRef().orderByChild("Product_Id").equalTo(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ProductModel productModel1 = null;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    productModel1 = postSnapshot.getValue(ProductModel.class);
                }
                productData.setValue(productModel1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                productData.setValue(null);

            }
        });
        return productData;
    }


}
