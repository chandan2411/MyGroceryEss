package com.essentials.customerapp.firebase;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.essentials.customerapp.models.UserModel;


public class DatabaseReferences {

    private static DatabaseReference userRef;
    private static DatabaseReference cartListRef;
    private static DatabaseReference userFCMTokenRef;
    private static DatabaseReference orderHistoryRef;
    private static DatabaseReference orderStatusRef;
    private static DatabaseReference itemOrderedRef;
    private static DatabaseReference walletRef;
    private static DatabaseReference referralCodeRef;
    private static DatabaseReference addressRef;
    private static DatabaseReference userWalletTransRef;
    private static DatabaseReference deliverableLocationRef;
    private static DatabaseReference productRef;
    private static DatabaseReference offerRef;
    private static DatabaseReference offerRef2;
    private static DatabaseReference offerRef3;
    private static DatabaseReference offerRef4;
    private static DatabaseReference offerRef5;
    private static DatabaseReference categoryRef;
    private static DatabaseReference subCategoryRef;
    private static DatabaseReference grandCategoryRef;

    private DatabaseReferences() {
    }

    public static DatabaseReference getUserReference() {
        if (userRef == null) {
            userRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_USER);
        }
        return userRef;
    }

    public static DatabaseReference getUserCartItem() {
        if (cartListRef == null) {
            cartListRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_USER_CART);
        }
        return cartListRef;
    }

    public static DatabaseReference getUserFCMTokenRef() {
        if (userFCMTokenRef == null) {
            userFCMTokenRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_USER_FCM_TOKEN);
        }
        return userFCMTokenRef;
    }

    public static DatabaseReference getOrderHistoryRef() {
        if (orderHistoryRef == null) {
            orderHistoryRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_USER_ORDER_HISTORY);
        }
        return orderHistoryRef;
    }

    public static DatabaseReference getOrderStatusRef() {
        if (orderStatusRef == null) {
            orderStatusRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_USER_ORDER_STATUS);
        }
        return orderStatusRef;
    }

    public static DatabaseReference getItemOrderedRef() {
        if (itemOrderedRef == null) {
            itemOrderedRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_USER_ORDERED_ITEM);
        }
        return itemOrderedRef;
    }

    public static DatabaseReference getCategoryRef() {
        if (categoryRef == null) {
            categoryRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_PRODUCT_CATEGORY);
        }
        return categoryRef;
    }

    public static DatabaseReference getSubCategoryRef() {
        if (subCategoryRef == null) {
            subCategoryRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_SUB_PRODUCT_CATEGORY);
        }
        return subCategoryRef;
    }

    public static DatabaseReference getGrandCategoryRef() {
        if (grandCategoryRef == null) {
            grandCategoryRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_GRAND_PRODUCT_CATEGORY);
        }
        return grandCategoryRef;
    }

    public static DatabaseReference getProductRef() {
        if (productRef == null) {
            productRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_PRODUCTS);
        }
        return productRef;
    }

    public static DatabaseReference getOfferRef() {
        if (offerRef == null) {
            offerRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_OFFERS);
        }
        return offerRef;
    }
    public static DatabaseReference getOffer2Ref() {
        if (offerRef2 == null) {
            offerRef2 = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_OFFERS2);
        }
        return offerRef2;
    }
    public static DatabaseReference getOffer3Ref() {
        if (offerRef3 == null) {
            offerRef3 = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_OFFERS3);
        }
        return offerRef3;
    }
    public static DatabaseReference getOffer4Ref() {
        if (offerRef4 == null) {
            offerRef4 = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_OFFERS4);
        }
        return offerRef4;
    }
    public static DatabaseReference getOffer5Ref() {
        if (offerRef5 == null) {
            offerRef5= FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_OFFERS4);
        }
        return offerRef5;
    }

    public static DatabaseReference getUserAddressRef() {
        if (addressRef == null) {
            addressRef = FirebaseDatabase.getInstance().getReference("User_Address");
        }
        return addressRef;
    }

    public static DatabaseReference getWalletReference() {
        if (walletRef == null) {
            walletRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_WALLET);

        }
        return walletRef;
    }

    public static DatabaseReference getReferralCodeReference() {
        if (referralCodeRef == null) {
            referralCodeRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_REFERRAL_CODE);

        }
        return referralCodeRef;
    }

    public static DatabaseReference getWalletTransReference() {
        if (userWalletTransRef == null) {
            userWalletTransRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_WALLET_TRANS);
        }
        return userWalletTransRef;
    }

    public static DatabaseReference getDeliverableLocations() {
        if (deliverableLocationRef == null) {
            deliverableLocationRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.DELIVERABLE_LOCATIONS);
        }
        return deliverableLocationRef;
    }

    public static StorageReference getCategoryImageStorageRef() {
        return null;
    }




    /*public static DatabaseReference getProductCategoryRef() {
        if (productCategoryRef == null) {
            productCategoryRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_PRODUCT_CATEGORY);
        }
        return productCategoryRef;
    }

    public static DatabaseReference getProductSubCategoryRef() {
        if (productSubCategoryRef == null) {
            productSubCategoryRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_SUB_PRODUCT_CATEGORY);
        }
        return productSubCategoryRef;
    }

    public static DatabaseReference getProductsRef() {
        if (productsRef == null) {
            productsRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constant.FB_PRODUCTS);
        }
        return productsRef;
    }

    public static void makeRingtoneUserFavourite(RingtoneModel ringtoneModel, String userId) {
        getUserFavoriteRingtoneReference(userId).child(ringtoneModel.getRingtoneId()).setValue(ringtoneModel);
    }

    public static void makeWallpaperUserFavourite(WallPaperModel ringtoneModel, String userId) {
        getUserFavoriteWallpaperReference(userId).child(ringtoneModel.getWallPaperId()).setValue(ringtoneModel);
    }

    public static void removeRingtoneFromUserFavourite(String ringtoneId) {
        if (userWishListRef != null)
            userWishListRef.child(ringtoneId).removeValue();
    }

    public static void removeWallpaperFromUserFavourite(String ringtoneId) {
        if (userWallpaperFavRef != null)
            userWallpaperFavRef.child(ringtoneId).removeValue();
    }

    public static void registerUser(UserModel model) {
        DatabaseReference drForUSer = FirebaseDatabase.getInstance().getReference(Constant.FB_USER).child(model.getUserId());
        drForUSer.setValue(model);
    }

    public static StorageReference getRingtoneStorageRef() {
        if (ringtoneStorageRef == null) {
            ringtoneStorageRef = FirebaseStorage.getInstance().getReference(Constant.FB_RINGTONE);
        }
        return ringtoneStorageRef;
    }

    public static DatabaseReference getRingtoneCategoryRefence() {
        if (ringtoneCategoryRef == null) {
            ringtoneCategoryRef = FirebaseDatabase.getInstance().getReference(Constant.FB_RINGTONE_CATEGORY);
        }
        return ringtoneCategoryRef;
    }*/

    public static void registerUser(UserModel model) {
        DatabaseReference drForUSer = FirebaseDatabase.getInstance().getReference(Constant.FB_USER).child(model.getUserId());
        drForUSer.setValue(model);
    }


    public static void nullReferences() {
        cartListRef =null;
        addressRef = null;
        orderHistoryRef = null;
        itemOrderedRef =null;
        walletRef = null;
        userFCMTokenRef = null;
    }
}
