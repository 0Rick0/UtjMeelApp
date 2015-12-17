package tk.r_ware.utjmeelapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import tk.r_ware.utjmeelapp.Communication.Communication;

public class CreateAccount extends Activity {

    private TextView mEmail, mUsername, mPassword, mPasswordRepeat;
    private Button mCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        mEmail = (TextView)findViewById(R.id.create_account_email);
        mUsername = (TextView)findViewById(R.id.create_account_username);
        mPassword = (TextView)findViewById(R.id.create_account_password1);
        mPasswordRepeat = (TextView)findViewById(R.id.create_account_password2);
        mCreate = (Button)findViewById(R.id.create_account_create);

        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccountTask task = new CreateAccountTask(mUsername.getText().toString(),mEmail.getText().toString(),mPassword.getText().toString(),mPasswordRepeat.getText().toString());
                task.execute();
            }
        });
    }

    public class CreateAccountTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mEmail;
        private final String mPassword;
        private final String mPasswordRepeat;

        CreateAccountTask(String mUsername, String email, String password, String mPasswordRepeat) {
            this.mUsername = mUsername;
            mEmail = email;
            mPassword = password;
            this.mPasswordRepeat = mPasswordRepeat;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if(Communication.getInstance().createAccount(mUsername, mPassword, mPasswordRepeat, mEmail)){
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
//            mAuthTask = null;
//            showProgress(false);

            if (success) {
                //Communication.getInstance().trySaveInfo(getApplicationContext());
                //Intent i = new Intent(Login.this,UtjMeelMain.class);
                //startActivity(i);
                finish();
            } else {
                //todo error
                Toast.makeText(CreateAccount.this, "Kan account niet aanmaken", Toast.LENGTH_SHORT).show();
                Toast.makeText(CreateAccount.this, Communication.getInstance().getLastError(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
//            mAuthTask = null;
//            showProgress(false);
        }
    }


}
