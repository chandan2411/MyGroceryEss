package com.essentials.customerapp.view.ui.activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import com.essentials.customerapp.view.ui.fragment.LoginFragment;
import com.essentials.customerapp.R;
import com.essentials.customerapp.viewmodel.LoginViewModel;
import com.essentials.customerapp.view.ui.fragment.VerificationFragment;

public class LoginActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener {

    int fragmentCount = 0;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private TextView tvTitle;
    //    ActivityLoginBinding binding;
    LoginViewModel loginViewModel;
//    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindViewAndModel();
        setFragment(LoginFragment.newInstance());
    }

    private void bindViewAndModel() {
        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.white_toolbar);
        tvTitle = findViewById(R.id.tvTitle);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }


    private void setFragment(Fragment loginFragment) {
        if (loginFragment instanceof LoginFragment) {
            fragmentCount = 0;
            tvTitle.setText("Login");
        } else {
            tvTitle.setText("Verification Code");
            fragmentCount = 1;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frameContainer, loginFragment)
                .setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left)
                .addToBackStack("Login")
                .commit();

    }


    @Override
    public void onBackPressed() {
//        final LoginFragment fragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag("Login");
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            tvTitle.setText("Login");
            getSupportFragmentManager().popBackStack();
        } else {
            this.finish();
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        }
    }

    @Override
    public void onFragmentInteraction(String phoneNo, String referralCode) {
        setFragment(VerificationFragment.newInstance(phoneNo, referralCode));
    }
}
