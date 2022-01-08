package com.essentials.customerapp.view.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.essentials.customerapp.data.model.ResponseModel;
import com.essentials.customerapp.data.repository.WalletRepository;
import com.essentials.customerapp.fcm.FCMConstant;
import com.essentials.customerapp.models.FCMTokenModel;
import com.essentials.customerapp.utilities.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.essentials.customerapp.R;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.models.UserModel;
import com.essentials.customerapp.models.WalletModel;
import com.essentials.customerapp.models.WalletTransaction;
import com.essentials.customerapp.utilities.AppUtils;
import com.essentials.customerapp.utilities.Constants;
import com.essentials.customerapp.utilities.CustomToast;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VerificationFragment extends Fragment {
    private static final String ARG_PARAM1 = "mobileNo";
    private static final String ARG_PARAM2 = "referralCode";
    private static final String TAG = "VerificationCode";
    private String mobileNo;
    private String mVerificationId;
    private EditText editTextCode;
    private FirebaseAuth mAuth;
    private LinearLayout parent;
    private TextView tvCodeResendTimer;
    private TextView tvMessage;
    private TextView btnResendCode;
    private MySharedPref sharedPref;
    private View view;
    private CustomToast customToast;
    private Activity mContext;
    private TextView tvNext;
    private String verificationCode = "";
    private DatabaseReference walletReference;
    private String referralCode = "";
    private String userIdOne = "";
    private RelativeLayout rlParent;
    private WalletRepository walletRepository;


    public VerificationFragment() {
    }

    public static VerificationFragment newInstance(String mobileNo, String referralCode) {
        VerificationFragment fragment = new VerificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, mobileNo);
        args.putString(ARG_PARAM2, referralCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mobileNo = getArguments().getString(ARG_PARAM1);
            referralCode = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (Activity) context;
        mContext.setTitle("Verification Code");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_verify_number, container, false);
        sharedPref = new MySharedPref(mContext);
        mAuth = FirebaseAuth.getInstance();

        editTextCode = view.findViewById(R.id.editTextCode);
        tvNext = view.findViewById(R.id.tvNext);
        tvMessage = view.findViewById(R.id.tvMessage);
        btnResendCode = view.findViewById(R.id.btnResendCode);
        parent = view.findViewById(R.id.parent);
        tvCodeResendTimer = view.findViewById(R.id.tvCodeResendTimer);
        rlParent = view.findViewById(R.id.rlParent);

        /*Adding message with mobile no to textview*/
        StringBuilder message = new StringBuilder();
        message.append("We've sent an SMS with activation code to ");
        message.append(mobileNo);
        tvMessage.setText(message);
        startTimer();
        sendVerificationCode(mobileNo);
        btnResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimer();
                sendVerificationCode(mobileNo);
            }
        });

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyVerificationCode(verificationCode);
            }
        });

        editTextCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                verificationCode = charSequence.toString();
                if (charSequence.length() == 6) {
                    tvNext.setEnabled(true);
                    tvNext.setBackgroundColor(getResources().getColor(R.color.theme_yellow));
                } else {
                    tvNext.setEnabled(false);
                    tvNext.setBackgroundColor(getResources().getColor(R.color.divider));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }


    private void startTimer() {
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvCodeResendTimer.setText("Resend code: " + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                tvCodeResendTimer.setVisibility(View.GONE);
                btnResendCode.setVisibility(View.VISIBLE);
            }
        }.start();
    }


    @Override
    public void onDetach() {
        super.onDetach();
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


    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {
        showDialog(mContext, "Requesting verification code");
        CustomToast.showRectToast(mContext, view, "We've sent an SMS with activation code to your phone number");
        btnResendCode.setVisibility(View.GONE);
        tvCodeResendTimer.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            AppUtils.dismissProgressDialog();
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                editTextCode.setText(code);
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            dismissDialog();
            CustomToast.showRectToast(mContext, view, "" + e.getMessage());
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            dismissDialog();
            super.onCodeSent(s, forceResendingToken);
            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {
        PhoneAuthCredential credential;
        if (code.isEmpty() || code.length() < 6) {
            editTextCode.setError("Enter valid code");
            editTextCode.requestFocus();
            return;
        }
        try {
            //creating the credential
            credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);

        } catch (Exception ex) {
            CustomToast.showRectToast(mContext, view, "Something went wrong");
        }

        //signing the user
    }

    private void registerUserAddress(String mobileNo, String email, String name, String imageUrl, String userId, int loginMode) {
        if (!mobileNo.isEmpty()) {
            sharedPref.writeInt(PrefConstant.LOGIN_MODE, PrefConstant.MOBILE);
            DatabaseReference mDatabaseRef = DatabaseReferences.getUserReference();
            Constant.USER_ID = mobileNo;
            Constant.IS_USER_LOGIN = true;
            mDatabaseRef.child(mobileNo).setValue(new UserModel(mobileNo, "", "", "", ""));
            addSavedCartItemTOFBDB();
            saveUSerDataToPref(mobileNo, "", "", "", mobileNo, PrefConstant.MOBILE);
            dismissDialog();

            checkForWalletExist(mobileNo, referralCode, "", true);
        }
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
        walletExist.observe(this, new Observer<ResponseModel>() {
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


    private void createWalletForUser(String userId, String userName, String referralCode) {
        Log.i(TAG, "createWalletForUser: " + userId + " " + userName + " " + referralCode);
        /*Creating wallet for new user and adding 200 amount*/
        showDialog(mContext, "Please Wait.. Creating new wallet for user");
        MutableLiveData<WalletModel> createWallet = walletRepository.createWalletForUser(userId, userName);
        createWallet.observe(this, new Observer<WalletModel>() {
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
        CustomToast.showRectToast(mContext, rlParent, "Adding amount... to the user wallet");
        MutableLiveData<ResponseModel> amountAdded = walletRepository.addWalletTransaction(userId, addAmount, true, transdetails, newUser);
        amountAdded.observe(this, new Observer<ResponseModel>() {
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
        mContext.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
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


    private void saveUSerDataToPref(String mobileNo, String email, String name, String imageUrl, String userId, int loginMode) {
        sharedPref.writeString(PrefConstant.USER_PHONE_NO, mobileNo);
        sharedPref.writeString(PrefConstant.USER_EMAIL, email);
        sharedPref.writeString(PrefConstant.USER_NAME, name);
        sharedPref.writeString(PrefConstant.USER_IMAGE_URL, imageUrl);
        sharedPref.writeString(PrefConstant.USER_ID, mobileNo);
        Constant.IS_USER_LOGIN = true;
        Constant.USER_ID = userId;
        sharedPref.writeInt(PrefConstant.LOGIN_MODE, loginMode);
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

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        showDialog(mContext, "Verifying code");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            registerUserAddress(mobileNo, "", "", "", "", PrefConstant.MOBILE);
                            /*sharedPref.writeString(PrefConstant.USER_PHONE_NO, mobileNo);
                            Intent intent = new Intent();
                            intent.putExtra(Constants.LOGIN_STATUS, true);
                            mContext.setResult(200, intent);
                            mContext.finish();
                            mContext.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);*/

                        } else {
                            //verification unsuccessful.. display an error message
                            String message = "Something is wrong, we will fix it soon...";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                            CustomToast.showRectToast(mContext, rlParent, message);
                        }
                        dismissDialog();
                    }
                });
    }

}
