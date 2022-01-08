package com.essentials.customerapp.view.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.essentials.customerapp.data.model.ResponseModel;
import com.essentials.customerapp.data.repository.WalletRepository;
import com.essentials.customerapp.fcm.FCMConstant;
import com.essentials.customerapp.models.FCMTokenModel;
import com.essentials.customerapp.utilities.AppUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.essentials.customerapp.R;
//import com.essentials.customerapp.databinding.FragmentLoginBinding;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.models.UserModel;
import com.essentials.customerapp.models.WalletModel;
import com.essentials.customerapp.utilities.CheckNetwork;
import com.essentials.customerapp.utilities.Constant;
import com.essentials.customerapp.utilities.Constants;
import com.essentials.customerapp.utilities.CustomToast;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;
import com.essentials.customerapp.viewmodel.LoginViewModel;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;


public class LoginFragment extends Fragment implements View.OnClickListener {
    private OnFragmentInteractionListener mListener;
    private EditText editTextMobile;
    private AppCompatActivity mContext;
    private GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager callbackManager;
    private static final String TAG = "LoginFragment";
    private final static int RC_SIGN_IN = 2;
    private MySharedPref sharedPref;
    private View view;
    private String referralCode = "";
    private DatabaseReference walletReference;
    private TextView btnNext;
    private LoginViewModel loginViewModel;
    private String userIdOne = "";
    private LinearLayout llParent;
    private EditText etReferralCode;
    private WalletRepository walletRepository;

    //    private FragmentLoginBinding loginBinding;


    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        editTextMobile = view.findViewById(R.id.edtPhone);
        btnNext = view.findViewById(R.id.tvNext);
        llParent = view.findViewById(R.id.llParent);
        CardView ivFbLogin = view.findViewById(R.id.cvLoginWithFacebook);
        CardView ivGoogleLogin = view.findViewById(R.id.cvLoginWithGoogle);
        btnNext = view.findViewById(R.id.tvNext);
        etReferralCode = view.findViewById(R.id.etReferralCode);
        CustomToast customToast = new CustomToast();
        callbackManager = CallbackManager.Factory.create();
        setUpgooglElOginAndFaceBook();

        editTextMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 10) {
                    btnNext.setEnabled(true);
                    btnNext.setBackgroundColor(getResources().getColor(R.color.theme_yellow));
                } else {
                    btnNext.setEnabled(false);
                    btnNext.setBackgroundColor(getResources().getColor(R.color.divider));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnNext.setOnClickListener(this);
        ivFbLogin.setOnClickListener(this);
        ivGoogleLogin.setOnClickListener(this);

        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String phoneNo) {
        if (mListener != null) {
            mListener.onFragmentInteraction(phoneNo, referralCode);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (AppCompatActivity) context;
        mContext.setTitle("Login");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        String mobile = editTextMobile.getText().toString().trim();
        if (!CheckNetwork.checkNet(mContext)) {
            CustomToast.showRectToast(mContext, llParent, Constant.NO_INTERNET);
        } else {
            if (view.getId() == R.id.cvLoginWithFacebook) {
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
            } else if (view.getId() == R.id.cvLoginWithGoogle) {
                signIn();
            } else if (view.getId() == R.id.tvNext) {
                if (mobile.isEmpty() || mobile.length() < 10) {
                    editTextMobile.setError("Enter a valid mobile");
                    editTextMobile.requestFocus();
                    return;
                }
                onButtonPressed(mobile);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String phoneNo, String referralCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showDialog(mContext, "Please wait.. Login In");
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed", e);
                CustomToast.showRectToast(mContext, llParent, "Google Sign In Failed");
                dismissDialog();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        saveUserGoogle(account);
    }

    private void saveUserGoogle(GoogleSignInAccount account) {
        GoogleSignInAccount user = account;
        String userName = user.getDisplayName();
        String userEmail = user.getEmail();
        String userId = user.getId();
        Uri userImageUrl = user.getPhotoUrl();

        /*Create table for user in firebase realtime database*/
        registerUserAddress("", userEmail, userName, String.valueOf(userImageUrl), userId, PrefConstant.GOOGLE);
    }

    private void saveFaceBookData(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("TAG", object.toString());

                        String fName = object.optString("first_name");
                        String lName = object.optString("last_name");
                        String userName = fName + " " + lName;
                        String userEmail = "";
                        try {
                            userEmail = object.optString("email");
                        } catch (Exception ex) {
                            Log.e(TAG, "Email Not found", ex);
                        }
                        String userId = object.optString("id");
                        Constant.USER_ID = userId;
                        Constant.IS_USER_LOGIN = true;
                        String userImageUrl = "https://graph.facebook.com/" + userId + "/picture?type=normal";
                        registerUserAddress("", userEmail, userName, userImageUrl, userId, PrefConstant.FACEBOOK);
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void saveUSerDataToPref(String mobileNo, String email, String name, String imageUrl,
                                    String userId, int loginMode) {
        sharedPref.writeString(PrefConstant.USER_PHONE_NO, mobileNo);
        sharedPref.writeString(PrefConstant.USER_EMAIL, email);
        sharedPref.writeString(PrefConstant.USER_NAME, name);
        sharedPref.writeString(PrefConstant.USER_IMAGE_URL, imageUrl);
        sharedPref.writeString(PrefConstant.USER_ID, userId);
        Constant.IS_USER_LOGIN = true;
        Constant.USER_ID = userId;
        sharedPref.writeInt(PrefConstant.LOGIN_MODE, loginMode);
    }

    private void registerUserAddress(String mobileNo, String email, String name, String imageUrl, String userId, int loginMode) {
        DatabaseReference mDatabaseRef = DatabaseReferences.getUserReference();
        mDatabaseRef.child(userId).setValue(new UserModel(userId, name, email, "", imageUrl));
        saveUSerDataToPref(mobileNo, email, name, imageUrl, userId, loginMode);
        addSavedCartItemTOFBDB();
        dismissDialog();
        Constant.USER_ID = userId;
        Constant.IS_USER_LOGIN = true;
        referralCode = etReferralCode.getText().toString().trim();
        checkForWalletExist(userId, referralCode, name, true); // true for new user
    }

    /**
     * @param userId       user id
     * @param referralCode referral code
     * @param userName     name of user
     * @param newUser      new User or referred user
     */
    private void checkForWalletExist(String userId, String referralCode, String userName, boolean newUser) {
        Log.i(TAG, "checkForWalletExist: " + userId + " " + referralCode + " " + userName + " " + newUser);

        showDialog(mContext, "Please Wait.. Validating Wallet");
        walletRepository = WalletRepository.getInstance();
        MutableLiveData<ResponseModel> walletExist = walletRepository.checkForWalletExist(userId, referralCode, newUser);
        walletExist.observe(mContext, new Observer<ResponseModel>() {
            @Override
            public void onChanged(ResponseModel responseModel) {
                if (responseModel != null) {
                    dismissDialog();
                    if (responseModel.isNewUser()) { // If newUser true
                        if (responseModel.isStatus()) { //Wallet Exit move to home
                            moveToHomeActivity();
                        } else {  // Wallet doest not exist, create new wallet
                            createWalletForUser(userId, userName, referralCode);
                        }
                    } else { // else
                        /*if (responseModel.isStatus()) { // IF wallet exist for referral code add transaction for 50
                            addWalletTransaction(responseModel.getUserId(), responseModel.getUserName(), false, referralCode);
                        } else {  // else move to home
                            moveToHomeActivity();
                        }*/
                        moveToHomeActivity();
                    }
                }
            }
        });
    }

    private static Dialog dialog;

    public static void showDialog(final Activity activity, String msg) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.message_dialog);
        dialog.setCancelable(true);
        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        tvMessage.setText(msg);
        dialog.show();
    }

    public static void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void createWalletForUser(String userId, String userName, String referralCode) {
        Log.i(TAG, "createWalletForUser: " + userId + " " + userName + " " + referralCode);
        /*Creating wallet for new user and adding 200 amount*/
        showDialog(mContext, "Please Wait.. Creating new wallet for user");
        MutableLiveData<WalletModel> createWallet = walletRepository.createWalletForUser(userId, userName);
        createWallet.observe(mContext, new Observer<WalletModel>() {
            @Override
            public void onChanged(WalletModel model) {
                if (model != null) {
                    dismissDialog();
                    sharedPref.writeString(PrefConstant.WALLED_ID, model.getWalletId());
                    sharedPref.writeString(PrefConstant.WALLED_REF_CODE, model.getWalletReferCode());
//                    addWalletTransaction(userId, userName, true, referralCode);
                    moveToHomeActivity();
                }
            }
        });
    }

    private void addWalletTransaction(String userId, String userName, boolean newUser, String referralCode) {
        Log.i(TAG, "addWalletTransaction: " + userId + " " + userName + " " + newUser + " " + referralCode);
        double addAmount = 0.0;
        String transdetails = "";
        if (newUser) {
            addAmount = 200.0;
            transdetails = "New Registration";
        } else {
            addAmount = 50.0;
            transdetails = "Amount added for referring new user " + userName;
        }

//        CustomToast.showRectToast(mContext, llParent, "Adding amount... to referral code wallet");
        CustomToast.showRectToast(mContext, llParent, "Adding amount... to the user wallet");
        MutableLiveData<ResponseModel> amountAdded = walletRepository.addWalletTransaction(userId, addAmount, true, transdetails, newUser);
        amountAdded.observe(mContext, new Observer<ResponseModel>() {
            @Override
            public void onChanged(ResponseModel responseModel) {
                if (responseModel != null) {
                    if (newUser) {
                        if (referralCode.isEmpty())
                            moveToHomeActivity();
                        else
                            checkForWalletExist("", referralCode, userName, false);
                    } else {
                        moveToHomeActivity();
                    }
                }
            }
        });
    }

    private void moveToHomeActivity() {
        pushTokenToFbTable();
        Intent intent = new Intent();
        intent.putExtra(Constants.LOGIN_STATUS, true);
        mContext.setResult(200, intent);
        mContext.finish();
    }

    private void pushTokenToFbTable() {
        String fcmToken = sharedPref.readString(PrefConstant.FCM_TOKEN,"");
        String userId = sharedPref.readString(PrefConstant.USER_ID,"");
        if (!fcmToken.isEmpty() && !userId.isEmpty()) {
            String key = DatabaseReferences.getUserFCMTokenRef().child(Constant.USER_ID).push().getKey();
            FCMTokenModel model = new FCMTokenModel();
            model.setUserId(userId);
            model.setUserName(sharedPref.readString(PrefConstant.USER_NAME, ""));
            model.setCreateTime(AppUtils.dateAndTime());
            model.setFcmToken(fcmToken);
            model.setKey(key);
            DatabaseReferences.getUserFCMTokenRef().child(Constant.USER_ID).setValue(model);
        }
    }


    private void addSavedCartItemTOFBDB() {
        if (!sharedPref.readString(PrefConstant.CART_ITEM, "").isEmpty()) {
            List<CartModel> list;
            Gson gson = new Gson();
            String cartItem = sharedPref.readString(PrefConstant.CART_ITEM, "");
            if (!cartItem.isEmpty()) {
                Type type = new TypeToken<List<CartModel>>() {
                }.getType();
                list = gson.fromJson(cartItem, type);
                for (CartModel model : list) {
                    DatabaseReferences.getUserCartItem().child(Constant.USER_ID)
                            .child(model.getProduct_Id()).setValue(model);
                }
            }

        }
    }

    private void setUpgooglElOginAndFaceBook() {
        sharedPref = new MySharedPref(mContext);
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        saveFaceBookData(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        dismissDialog();
                        CustomToast.showRectToast(mContext, llParent, "Facebook login cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        dismissDialog();
                        CustomToast.showRectToast(mContext, llParent, exception.toString());
                    }
                });


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);
    }
}
