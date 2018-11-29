package revolhope.splanes.com.bitwallet.view;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import revolhope.splanes.com.bitwallet.helper.DialogHelper;
import revolhope.splanes.com.bitwallet.helper.RandomGenerator;
import revolhope.splanes.com.bitwallet.model.Account;
import revolhope.splanes.com.bitwallet.model.K;
import revolhope.splanes.com.bitwallet.view.dialogs.DialogGenerateParams;

public class AccountActivity extends AppCompatActivity {

    private EditText editText_Account;
    private EditText editText_Password;
    private long parentId;
    private int insertControl;

    private boolean isNew = true;
    private static K k;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        final Account account;

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

        if (getIntent().getExtras() != null) {

            if (getIntent().hasExtra(AppContract.EXTRA_EDIT_ACC) && k != null) {

                account = (Account) getIntent().getExtras().
                        getSerializable(AppContract.EXTRA_EDIT_ACC);

                if (account == null) return;
                else isNew = false;
            }
            else {
                account = new Account();
                k = null;
                isNew = true;
            }

            parentId = getIntent().getExtras().getLong(AppContract.EXTRA_CURRENT_DIR);
        }
        else {
            DialogHelper.showInfo("Error",
                                  "Oops.. something went wrong. Try it again",
                                  this);
            // That below is done by prevent a NullPointerException
            account = new Account();
            k = null;
        }

        toolbar.setTitle(isNew ? "New Account" : "Update Account");

        editText_Account = findViewById(R.id.editText_Account);
        editText_Password = findViewById(R.id.editText_Password);
        final EditText editText_URL = findViewById(R.id.editText_URL);
        final EditText editText_User = findViewById(R.id.editText_User);
        Button btGenerate = findViewById(R.id.btGenerate);
        final CheckBox checkBox_Expire = findViewById(R.id.checkBox_Expire);
        final TextView textView_ExpireDate = findViewById(R.id.textView_ExpireDate);
        final EditText editText_Brief = findViewById(R.id.editText_Brief);
        Button btDone = findViewById(R.id.btDone);

        btDone.setText(isNew ? "Create" : "Update");

        TextView textView_Id = findViewById(R.id.textView_Id);
        textView_Id.setText(account.get_id());

        // TODO: Delete if doesn't works
        textView_Id.requestFocus();

        if (!isNew) {

            ImageView ivCopy = findViewById(R.id.iv_copy);
            ivCopy.setVisibility(View.VISIBLE);

            ivCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clipboard =
                            (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard != null) {
                        ClipData clip = ClipData.newPlainText("pwd", editText_Password.getText());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), "Copied", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });

            editText_Account.setText(account.getAccount());
            editText_URL.setText(account.getUrl() == null ? "" : account.getUrl());
            editText_User.setText(account.getUser() == null ? "" : account.getUser());
            editText_Brief.setText(account.getBrief() == null ? "" : account.getBrief());
            checkBox_Expire.setChecked(account.isExpire());
            textView_ExpireDate.setText(account.getDateExpire() == null ||
                                        account.getDateExpire() == 0 ?
                                        "" : "Expires on:" + AppUtils.format("dd/MM/yyyy",
                                                                account.getDateExpire()));

            try {

                Cryptography cryptography = new Cryptography();
                byte[] bytes = cryptography.decrypt(AppUtils.fromStringBase64(k.getPwdBase64()),
                                     k, account.get_id());

                if (bytes != null) {
                    editText_Password.setText(new String(bytes));
                    ConstraintLayout.LayoutParams params =
                            (ConstraintLayout.LayoutParams) editText_Password.getLayoutParams();
                    params.setMarginEnd(toPixels(8));
                    editText_Password.setLayoutParams(params);
                }
                else {
                    DialogHelper.showInfo("Decryption error",
                                          "Something went wrong when decrypting",
                                          this);
                }
            }
            catch (Exception e) {
                DialogHelper.showInfo("Decryption error", e.getMessage(), this);
            }
        }

        final Calendar cal = Calendar.getInstance();
        final DatePickerDialog dialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year,
                                          int month, int day) {
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, day);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView_ExpireDate.setText(
                                        String.format("Expires on: %s",AppUtils.format(
                                                                "dd/MM/yyyy",
                                                                 cal.getTimeInMillis())));
                            }
                        });
                    }
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));

        checkBox_Expire.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    dialog.show();
                }
            }
        });

        btDone.setOnClickListener(new View.OnClickListener() {
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

                    processAccount(account, editText_Password.getText().toString());

                }
                else {
                    DialogHelper.showInfo("Fields are wrong",
                                          "Account name and Password fields are mandatory",
                                           getApplicationContext());
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
                dialogGenerateParams.getWindow().getAttributes().windowAnimations = 
                    R.style.DialogFromRightAnim;
                dialogGenerateParams.show(getSupportFragmentManager(), "GenDialog");
            }
        });

    }

    private void processAccount(final Account account, String pwd) {

        final DaoAccount daoAccount = DaoAccount.getInstance(getApplicationContext());
        final DaoK daoK = DaoK.getInstance(getApplicationContext());
        try {
            Cryptography crypto = new Cryptography();

            if (isNew) {

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
                    DialogHelper.showInfo("Encryption error",
                                          "Something went wrong when encrypting...",
                                          this);
                }
            }
            else {
                Calendar cal = Calendar.getInstance();
                Calendar cal1 = Calendar.getInstance();
                cal1.setTimeInMillis(k.getDeadline());

                if (cal.after(cal1)) {
                    daoK.delete(new DaoCallbacks.Delete() {
                        @Override
                        public void onDelete(int deleteCode) {}
                    }, new Long[]{ k.get_id() });
                    crypto.newKey(account.get_id());
                    k = crypto.encrypt(pwd.getBytes(Charset.forName("UTF-8")), account.get_id());
                    if (k != null) {
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
                }
                else {

                    Long id = k.get_id();
                    k = crypto.encrypt(pwd.getBytes(Charset.forName("UTF-8")), account.get_id());
                    if (k != null) {
                        cal.add(Calendar.MONTH, 3);

                        k.set_id(id);
                        k.setDeadline(cal.getTimeInMillis());

                        insertControl = 0;

                        daoAccount.update(new DaoCallbacks.Update<Account>() {
                            @Override
                            public void onUpdated(Account[] results) {
                                if (results != null && results.length != 0) increaseControl();
                            }
                        }, new Account[]{account});

                        daoK.update(new DaoCallbacks.Update<K>() {
                            @Override
                            public void onUpdated(K[] results) {
                                if (results != null && results.length != 0) increaseControl();
                            }
                        }, new K[]{k});
                    }
                }
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
                             isNew ?  "Account created!" : "Account updated",
                             Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    private boolean checkInputs() {

        return !editText_Account.getText().toString().isEmpty() &&
               !editText_Password.getText().toString().isEmpty();
    }

    static void setK(K k) {
        AccountActivity.k = k;
    }

    public int toPixels(float dp) {
        float density = this.getResources().getDisplayMetrics().density;
        return (int)(dp * density);
    }
}
