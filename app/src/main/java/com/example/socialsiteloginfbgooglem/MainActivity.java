package com.example.socialsiteloginfbgooglem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.socialsiteloginfbgooglem.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

//main activity
public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnConnectionFailedListener, GoogleApiClient.OnConnectionFailedListener {

    private TextView name, email, welcome;
    private CallbackManager callbackManager;

    private LoginButton loginButton;

    private ImageView photo;

    private LinearLayout profsection;
    private Button gsignout;
    private SignInButton gsignin;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;
    private GoogleSignInClient mGoogleSignInClient;


    // oncreate method related to main activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        name = findViewById(R.id.name);
        email = findViewById(R.id.email);

        welcome = findViewById(R.id.welcome);
        loginButton = findViewById(R.id.login_button);
        photo = (ImageView) findViewById(R.id.photo);

//related to google sign in


        profsection = findViewById(R.id.profsection);
        gsignin = findViewById(R.id.gsigninbutton);

        gsignout = findViewById(R.id.gsignout);

        profsection = findViewById(R.id.profsection);

        gsignin.setOnClickListener((View.OnClickListener) MainActivity.this);
        gsignout.setOnClickListener((View.OnClickListener) MainActivity.this);

        profsection.setVisibility(View.GONE);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();


// related to fb login

        callbackManager = CallbackManager.Factory.create();


        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        loginStatusCheck();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(MainActivity.this, "loign success", Toast.LENGTH_SHORT).show();
                profsection.setVisibility(View.VISIBLE);
                gsignin.setVisibility(View.GONE);
                gsignout.setVisibility(View.GONE);

            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "login cancelled", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "login error, pls try again", Toast.LENGTH_SHORT).show();
            }
        });


    }


    //related to google sign in

    private void signIn() {

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);
   

    }

    private void signOut() {


        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
                Toast.makeText(MainActivity.this, "sign out success", Toast.LENGTH_SHORT).show();
              
            }
        });
    }

    private void handleResult(GoogleSignInResult result) {
        if (result.isSuccess()) {

            GoogleSignInAccount account = result.getSignInAccount();
            String Name = account.getDisplayName();
            String Email = account.getEmail();
            String imageUrl = account.getPhotoUrl().toString();
            name.setText(Name);

            welcome.setText("Hello " + Name + ",\nHere are your details");
            email.setText(Email);
            Glide.with(this).load(imageUrl).into(photo);


            updateUI(true);
            Toast.makeText(this, "sign in success", Toast.LENGTH_SHORT).show();


        } else {
            updateUI(false);
        }

    }

    private void updateUI(boolean isLogin) {
        if (isLogin) {

            profsection.setVisibility(View.VISIBLE);
            gsignin.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            gsignout.setVisibility(View.VISIBLE);


        } else {
            profsection.setVisibility(View.GONE);
            gsignin.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            gsignout.setVisibility(View.VISIBLE);


        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.gsigninbutton:
                signIn();
                break;

            case R.id.gsignout:
                signOut();
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != REQ_CODE) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);

        }


    }

    //related to fb login token
    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                name.setText("");
                welcome.setText("");
                email.setText("");
                photo.setImageResource(0);
                profsection.setVisibility(View.GONE);
                gsignin.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "user logged out", Toast.LENGTH_SHORT).show();

            } else {
                userProfile(currentAccessToken);


            }


        }


    };


    private void userProfile(AccessToken newAccessToken) {

        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String mail = object.getString("email");
                    String id = object.getString("id");
                    String imageurl = "https:/graph.facebook.com/" + id + "/picture?type=large";

                    name.setText(first_name + " " + last_name);
                    email.setText(mail);


                    Picasso.get().load(imageurl).into(photo);
                    welcome.setText("Welcome " + first_name + ",\nHere are your details");


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }


    private void loginStatusCheck() {
        if (AccessToken.getCurrentAccessToken() != null) {

            userProfile(AccessToken.getCurrentAccessToken());
        }


    }


}