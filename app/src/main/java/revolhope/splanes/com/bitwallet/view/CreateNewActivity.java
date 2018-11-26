package revolhope.splanes.com.bitwallet.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.Calendar;

import revolhope.splanes.com.bitwallet.R;
import revolhope.splanes.com.bitwallet.crypto.Cryptography;
import revolhope.splanes.com.bitwallet.db.DaoAccount;
import revolhope.splanes.com.bitwallet.db.DaoCallbacks;
import revolhope.splanes.com.bitwallet.db.DaoK;
import revolhope.splanes.com.bitwallet.helper.AppContract;
import revolhope.splanes.com.bitwallet.helper.AppUtils;
import revolhope.splanes.com.bitwallet.helper.RandomGenerator;
import revolhope.splanes.com.bitwallet.model.Account;
import revolhope.splanes.com.bitwallet.model.K;

public class CreateNewActivity extends AppCompatActivity {

    private EditText editText_Account;
    private EditText editText_Password;
    private long parentId;
    private int insertControl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        final Account account = new Account();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_dialogfullscreen);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        toolbar.setTitle("New Account");

        if (getIntent().getExtras() != null) {
            parentId = getIntent().getExtras().getLong(AppContract.EXTRA_CURRENT_DIR);
        }


        editText_Account = findViewById(R.id.editText_Account);
        editText_Password = findViewById(R.id.editText_Password);
        final EditText editText_URL = findViewById(R.id.editText_URL);
        final EditText editText_User = findViewById(R.id.editText_User);
        Button btGenerate = findViewById(R.id.btGenerate);
        final CheckBox checkBox_Expire = findViewById(R.id.checkBox_Expire);
        final TextView textView_ExpireDate = findViewById(R.id.textView_ExpireDate);
        final EditText editText_Brief = findViewById(R.id.editText_Brief);
        Button btCreate = findViewById(R.id.btCreate);

        TextView textView_Id = findViewById(R.id.textView_Id);
        textView_Id.setText(account.get_id());

        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkInputs()) {

                    account.setParent(parentId);
                    account.setAccount(editText_Account.getText().toString());
                    account.setUser(editText_User.getText().toString());
                    account.setUrl(editText_URL.getText().toString());
                    account.setBrief(editText_Brief.getText().toString());
                    account.setDateCreate(AppUtils.timestamp());
                    account.setDateUpdate(null);

                    if (checkBox_Expire.isChecked()) {

                        Long expire = AppUtils.toMillis("dd/MM/yyyy",
                                textView_ExpireDate.getText().toString());

                        account.setDateExpire(expire);
                        account.setExpire(true);
                        if (expire == null) {
                            // TODO: Throw message saying it was not possible to parse Date,
                            // TODO: If want to set expire date, update the account!
                            account.setExpire(false);
                        }
                    } else {
                        account.setDateExpire(null);
                        account.setExpire(false);
                    }

                    insertAccount(account, editText_Password.getText().toString());
                }
                else {
                    // TODO: Throw dialog: fields-> Account + Password are mandatory!
                }
            }
        });

        btGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogGenerateParams dialogGenerateParams = new DialogGenerateParams();
                dialogGenerateParams.setCallback(new DialogGenerateParams.DialogCallback() {
                    @Override
                    public void getResult(int mode, int size) {
                        String pwd = RandomGenerator.create(mode, size);
                        editText_Password.setText(pwd != null ? pwd : "Oops..Try again");
                    }
                });
                dialogGenerateParams.show(getSupportFragmentManager(), "GenDialog");
            }
        });

    }

    private void insertAccount(Account account, String pwd) {

        DaoAccount daoAccount = DaoAccount.getInstance(getApplicationContext());
        DaoK daoK = DaoK.getInstance(getApplicationContext());
        try {

            Cryptography crypto = new Cryptography();
            crypto.newKey(account.get_id());
            K k = crypto.encrypt(pwd.getBytes(Charset.forName("UTF-8")), account.get_id());
            if (k != null) {

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, 3);
                k.setDeadline(cal.getTimeInMillis());

                insertControl = 0;

                daoAccount.insert(new DaoCallbacks.Update<Account>() {
                    @Override
                    public void onUpdated(Account[] results) {
                        if (results != null && results.length != 0) increaseControl();
                    }
                }, new Account[]{account});

                daoK.insert(new DaoCallbacks.Update<K>() {
                    @Override
                    public void onUpdated(K[] results) {
                        if (results != null && results.length != 0) increaseControl();
                    }
                }, new K[]{k});



            }
            else {
                // TODO: Throw dialog: error while encryption
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void increaseControl() {
        insertControl++;
        if (insertControl == 2) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                             "Account created!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    private boolean checkInputs() {

        return !editText_Account.getText().toString().isEmpty() &&
               !editText_Password.getText().toString().isEmpty();
    }
}