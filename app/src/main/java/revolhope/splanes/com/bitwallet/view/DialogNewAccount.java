package revolhope.splanes.com.bitwallet.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.util.Calendar;

import revolhope.splanes.com.bitwallet.R;
import revolhope.splanes.com.bitwallet.crypto.Cryptography;
import revolhope.splanes.com.bitwallet.db.DaoAccount;
import revolhope.splanes.com.bitwallet.db.DaoCallbacks;
import revolhope.splanes.com.bitwallet.db.DaoK;
import revolhope.splanes.com.bitwallet.helper.AppUtils;
import revolhope.splanes.com.bitwallet.helper.DialogHelper;
import revolhope.splanes.com.bitwallet.model.Account;
import revolhope.splanes.com.bitwallet.model.K;

public class DialogNewAccount extends DialogFragment {

    private EditText editText_Account;
    private EditText editText_Password;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.activity_create, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_dialogfullscreen);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().cancel();
            }
        });
        toolbar.setTitle("New Account");

        final Account account = new Account();

        editText_Account = view.findViewById(R.id.editText_Account);
        editText_Password = view.findViewById(R.id.editText_Password);
        final EditText editText_URL = view.findViewById(R.id.editText_URL);
        final EditText editText_User = view.findViewById(R.id.editText_User);
        Button btGenerate = view.findViewById(R.id.btGenerate);
        final CheckBox checkBox_Expire = view.findViewById(R.id.checkBox_Expire);
        final TextView textView_ExpireDate = view.findViewById(R.id.textView_ExpireDate);
        final EditText editText_Brief = view.findViewById(R.id.editText_Brief);
        Button btCreate = view.findViewById(R.id.btCreate);

        TextView textView_Id = view.findViewById(R.id.textView_Id);
        textView_Id.setText(account.get_id());

        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkInputs()) {

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

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(width, height);
            }
        }
    }

    private void insertAccount(Account account, String pwd) {

        try {
            DaoAccount daoAccount = DaoAccount.getInstance(getContext());
            DaoK daoK = DaoK.getInstance(getContext());
            Cryptography crypto = new Cryptography();
            crypto.newKey(account.get_id());
            K k = crypto.encrypt(pwd.getBytes(Charset.forName("UTF-8")), account.get_id());
            if (k != null) {

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, 3);
                k.setDeadline(cal.getTimeInMillis());

                daoAccount.insert(new DaoCallbacks.Update<Account>() {
                                      @Override
                                      public void onUpdated(Account[] results) {}
                                  },
                        new Account[]{account});

                daoK.insert(new DaoCallbacks.Update<K>() {
                                @Override
                                public void onUpdated(K[] results) { }
                            },
                        new K[]{k});

            }
            else {
                if (getContext() != null)
                    DialogHelper.showInfo("Error",
                                          "Error while decryption.." ,
                                           getContext());
                else {
                    System.err.println(" :......: Encryption error: DialogNewAccount class," +
                                       " line: 152");
                }
            }
        } catch (Exception e) {
            if (getContext() != null)
                DialogHelper.showInfo("Exception", e.getMessage(), getContext());
            e.printStackTrace();
        }

    }

    private boolean checkInputs() {

        return !editText_Account.getText().toString().isEmpty() &&
                !editText_Password.getText().toString().isEmpty();
    }
}
