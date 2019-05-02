package com.example.ufree;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
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
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * A login screen that offers login via email/password.
 */
public class SignUp extends AppCompatActivity implements LoaderCallbacks<Cursor> {


    // Keep track of the login task to ensure we can cancel it if requested.
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mFullNameView;
    private AutoCompleteTextView mEmailView;
    private EditText mPhoneNumberView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;

    private View mProgressView;
    private View mLoginFormView;

    // Profile pic result codes
    private final int GALLERY = 0;
    private final int CAMERA = 1;
    private final int PICK_IMAGE_REQUEST = 71;

    // Uploading image
    private Uri filePath;

    // Database elements
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = db.getReference();

    private FirebaseAuth auth;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();

    // Fields for creating user
    private String fullName;
    private String email;
    private String phoneNumber;
    private String password;
    private String profileImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Set up the sign up form.
        mFullNameView = (EditText) findViewById(R.id.full_name);
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
                final AlertDialog.Builder pictureDialog = new AlertDialog.Builder(SignUp.this);
                pictureDialog.setTitle("Choose Profile Picture");
                String[] pictureDialogItems = {
                        "Select photo from gallery"};
                pictureDialog.setItems(pictureDialogItems,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        choosePhotoFromGallery();
                                        break;
                                }
                            }
                        });
                pictureDialog.show();
            }
        });

        ImageView userView = (ImageView) findViewById(R.id.user_pic);
        userView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder pictureDialog = new AlertDialog.Builder(SignUp.this);
                pictureDialog.setTitle("Choose Profile Picture");
                String[] pictureDialogItems = {
                        "Select photo from gallery"};
                pictureDialog.setItems(pictureDialogItems,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        choosePhotoFromGallery();
                                        break;
                                }
                            }
                        });
                pictureDialog.show();
            }
        });
    }

    // User chooses photo from gallery
    private void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, this.GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ImageView defaultView = findViewById(R.id.profile_picture);
        CardView cardView = findViewById(R.id.user_pic_wrapper);
        ImageView userView = findViewById(R.id.user_pic);

        // Something went wrong/user cancelled request
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        // If user chose gallery
        if (requestCode == GALLERY) {
            if (data != null) {
                filePath = data.getData();
                try {
                    defaultView.setVisibility(View.GONE);
                    cardView.setVisibility(View.VISIBLE);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                    userView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Gallery image failed");
                }
            }
            return;
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
        mFullNameView.setError(null);
        mEmailView.setError(null);
        mPhoneNumberView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);

        // Store values at the time of the sign up attempt.
        fullName = mFullNameView.getText().toString().trim();
        email = mEmailView.getText().toString().trim();
        phoneNumber =  mPhoneNumberView.getText().toString().trim();
        password = mPasswordView.getText().toString().trim();
        String confirmPassword = mConfirmPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for valid first name
        if (TextUtils.isEmpty(fullName)) {
            mFullNameView.setError(getString(R.string.error_field_required));
            focusView = mFullNameView;
            cancel = true;
        } else if (!isFullNameValid(fullName)) {
            mFullNameView.setError(String.format(getString(R.string.error_invalid_field), "full name"));
            focusView = mFullNameView;
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

            mAuthTask = new UserLoginTask(fullName, email, phoneNumber, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isFullNameValid(String fullName) {
        return fullName.length() > 1;
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
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {}

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {}


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

        private final String fullName;
        private final String email;
        private final String phoneNumber;
        private final String password;

        UserLoginTask(String iFullName, String iEmail, String iPhoneNumber, String iPassword) {
            fullName = iFullName;
            email = iEmail;
            phoneNumber = iPhoneNumber;
            password = iPassword;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
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
                                    HashMap<String, Long> eventIds = new HashMap<>();
                                    HashMap<String, Long> inviteIds = new HashMap<>();
                                    eventIds.put("-1", (long) -1);
                                    eventIds.put("-2", (long) -2);
                                    inviteIds.put("-2", (long) -2);
                                    inviteIds.put("-1", (long) -1);
                                    dbRef.child("users").child(email.replaceAll("[^a-zA-Z0-9]", "")).setValue(new User(fullName, phoneNumber, email, eventIds, inviteIds));
                                    uploadImage();
                                    dbRef.child("users").child(email.replaceAll("[^a-zA-Z0-9]", "")).setValue(new User(fullName, phoneNumber, email, eventIds, inviteIds));
                                    // save user id in Shared Preferences
                                    SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
                                    SharedPreferences.Editor preferencesEditor = sp.edit();
                                    preferencesEditor.putString("userID", email.replaceAll("[^a-zA-Z0-9]", ""));
                                    preferencesEditor.apply();
                                    startActivity(new Intent(SignUp.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });

                SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
                SharedPreferences.Editor spEdit = sp.edit();
                spEdit.putString("userID", email);
                spEdit.apply();
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

    private void uploadImage() {
        storageReference = storage.getReference(
                "profilePics/" + email.replaceAll("[^a-zA-Z0-9]", "") + ".jpg");
        if (filePath != null) {
            storageReference.putFile(filePath).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d("TAG", uri.toString());
                                    dbRef.child("users").child(email.replaceAll("[^a-zA-Z0-9]", ""))
                                            .child("profilePic").setValue(uri.toString());
                                }
                            });
                        }
                    }
            );
        }
    }
}

