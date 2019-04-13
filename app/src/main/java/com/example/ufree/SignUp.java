package com.example.ufree;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A login screen that offers login via email/password.
 */
public class SignUp extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private AutoCompleteTextView mEmailView;
    private EditText mPhoneNumberView;
    private TextView mBirthdayView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;

    private View mProgressView;
    private View mLoginFormView;

    private int RESULT_LOAD_IMAGE = 0;

    //database elements
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = db.getReference("users");

    private FirebaseAuth auth;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String birthday;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Set up the sign up form.
        mFirstNameView = (EditText) findViewById(R.id.first_name);
        mLastNameView =(EditText) findViewById(R.id.last_name);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPhoneNumberView = (EditText) findViewById(R.id.phone_number);
        mPasswordView = (EditText) findViewById(R.id.password);
        mConfirmPasswordView = (EditText) findViewById(R.id.confirm_password);

        auth = FirebaseAuth.getInstance();

        changeProfilePic();

        Button mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void changeProfilePic() {
        ImageView profilePictureImage = (ImageView) findViewById(R.id.profile_picture);
        profilePictureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                ImageView imageView = (ImageView) findViewById(R.id.profile_picture);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Attempts to sign in or register the account specified by the signup form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSignUp() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mFirstNameView.setError(null);
        mLastNameView.setError(null);
        mEmailView.setError(null);
        mPhoneNumberView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);

        // Store values at the time of the sign up attempt.
        firstName = mFirstNameView.getText().toString().trim();
        lastName = mLastNameView.getText().toString().trim();
        email = mEmailView.getText().toString().trim();
        phoneNumber =  mPhoneNumberView.getText().toString().trim();
        password = mPasswordView.getText().toString().trim();
        String confirmPassword = mConfirmPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for valid first name
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        } else if (!isFirstNameValid(firstName)) {
            mFirstNameView.setError(String.format(getString(R.string.error_invalid_field), "first name"));
            focusView = mFirstNameView;
            cancel = true;
        }

        // Check for valid last name
        if (TextUtils.isEmpty(lastName)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        } else if (!isFirstNameValid(lastName)) {
            mLastNameView.setError(String.format(getString(R.string.error_invalid_field), "last name"));
            focusView = mLastNameView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(String.format(getString(R.string.error_invalid_field), "email"));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberView.setError(getString(R.string.error_field_required));
            focusView = mPhoneNumberView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_field_required));
            focusView = mConfirmPasswordView;
            cancel = true;
        } else if (!isConfirmPasswordValid(password, confirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_password_dont_match));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);

            mAuthTask = new UserLoginTask(firstName, lastName, email, phoneNumber, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isFirstNameValid(String firstName) {
        return firstName.length() > 1;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private boolean isConfirmPasswordValid(String password1, String password2) {
        return password1.equals(password2);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    private interface ProfileQuery {
        int ADDRESS = 0;
        int IS_PRIMARY = 1;

        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String firstName;
        private final String lastName;
        private final String email;
        private final String phoneNumber;
        private final String password;

        UserLoginTask(String iFirstName, String iLastName, String iEmail, String iPhoneNumber, String iPassword) {
            firstName = iFirstName;
            lastName = iLastName;
            email = iEmail;
            phoneNumber = iPhoneNumber;
            password = iPassword;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignUp.this, "Sign Up Failed" + task.getException(), Toast.LENGTH_LONG).show();
                                } else {
                                    dbRef.child(email.replaceAll("[^a-zA-Z0-9]", "")).setValue(new User(firstName, lastName, phoneNumber, email));
                                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putBoolean("loggedIn", true);
                                    editor.commit();
                                    startActivity(new Intent(SignUp.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
            } else {
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

