package com.essentials.customerapp.utilities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.essentials.customerapp.view.ui.activity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.essentials.customerapp.R;

public class ViewDialog {

    public ViewDialog() {
    }

    public static void showDialog(final Activity activity, String msg, String title, final boolean isSignIn){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);

        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        TextView tvDialogMessage = dialog.findViewById(R.id.tvDialogMessage);
        tvTitle.setText(title);
        tvDialogMessage.setText(msg);

        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (isSignIn){
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                    activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }else {
                    FirebaseAuth.getInstance().signOut();
                    new MySharedPref(activity).deleteAllData();
                }
            }
        });
        dialog.show();
    }
}
