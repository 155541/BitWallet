package revolhope.splanes.com.bitwallet.view;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

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

    private long parentId;
    private int insertControl;

    private boolean isNew = true;
    private static K k;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        // #Note: code below is to make sure keyboard just opens when user press
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
            DialogHelper.showInfo(this,"Error",
                                  "Oops.. something went wrong. Try it again",
                                  null);
            // That below is done to prevent a NullPointerException
            account = new Account();
            k = null;
        }

        toolbar.setTitle(isNew ? "New Account" : "Update Account");

        TextInputEditText editText_Id = findViewById(R.id.editText_Id);
        final TextInputEditText editText_Account = findViewById(R.id.editText_Account);
        final TextInputEditText editText_URL = findViewById(R.id.editText_URL);
        final TextInputEditText editText_User = findViewById(R.id.editText_User);
        final TextInputEditText editText_Password = findViewById(R.id.editText_Password);
        ImageView imageViewCopy = findViewById(R.id.iv_copy);
        final TextInputEditText editText_Brief = findViewById(R.id.editText_Brief);
        final TextInputEditText editText_ExpirationDate = findViewById(R.id.editText_ExpirationDate);
        final SwitchCompat switchExpirationDate = findViewById(R.id.switch_ExpirationDate);
        MaterialButton btGenerate = findViewById(R.id.btGenerate);
        MaterialButton btDone = findViewById(R.id.btDone);

        btDone.setText(isNew ? "Create" : "Update");

        editText_Id.setText(account.get_id());
        if (!isNew) {

            editText_Account.setText(account.getAccount());
            editText_URL.setText(account.getUrl() == null ? "" : account.getUrl());
            editText_User.setText(account.getUser() == null ? "" : account.getUser());
            editText_Brief.setText(account.getBrief() == null ? "" : account.getBrief());
            switchExpirationDate.setChecked(account.isExpire());
            editText_ExpirationDate.setText(account.getDateExpire() == null ||
                    account.getDateExpire() == 0 ?
                    "" : AppUtils.format("dd/MM/yyyy", account.getDateExpire()));

            imageViewCopy.setVisibility(View.VISIBLE);
            imageViewCopy.setOnClickListener(new View.OnClickListener() {
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
                    DialogHelper.showInfo(this,"Decryption error",
                                          "Something went wrong when decrypting",
                                          null);
                }
            }
            catch (Exception e) {
                DialogHelper.showInfo(this,"Decryption error", e.getMessage(),
                        null);
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
                                editText_ExpirationDate.setText(
                                        AppUtils.format("dd/MM/yyyy", cal.getTimeInMillis()));
                            }
                        });
                    }
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));

        switchExpirationDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    dialog.show();
                }
                else {
                    editText_ExpirationDate.setText("");
                }
            }
        });

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Editable editableAccount = editText_Account.getText();
                Editable editablePwd = editText_Password.getText();

                if (editableAccount != null && !editableAccount.toString().isEmpty() &&
                        editablePwd != null && !editablePwd.toString().isEmpty()) {

                    account.setParent(parentId);
                    account.setAccount(editableAccount.toString());
                    account.setUser(editText_User.getText() != null ?
                            editText_User.getText().toString() : "");
                    account.setUrl(editText_URL.getText() != null ?
                            editText_URL.getText().toString() : "");
                    account.setBrief(editText_Brief.getText() != null ?
                            editText_Brief.getText().toString() : "");
                    account.setDateCreate(AppUtils.timestamp());
                    account.setDateUpdate(null);

                    if (switchExpirationDate.isChecked() &&
                        editText_ExpirationDate.getText() != null) {

                        Long expire = AppUtils.toMillis("dd/MM/yyyy",
                                editText_ExpirationDate.getText().toString());

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

                    processAccount(account, editablePwd.toString());

                }
                else {
                    DialogHelper.showInfo(getApplicationContext(),
                                          "Fields are wrong",
                                          "Account name and Password fields are mandatory",
                                           null);
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
                    DialogHelper.showInfo(this,"Encryption error",
                                          "Something went wrong when encrypting...",
                                          null);
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

    static void setK(K k) {
        AccountActivity.k = k;
    }

    public int toPixels(float dp) {
        float density = this.getResources().getDisplayMetrics().density;
        return (int)(dp * density);
    }
}
