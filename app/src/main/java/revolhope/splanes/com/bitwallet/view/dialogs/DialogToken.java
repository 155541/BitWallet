package revolhope.splanes.com.bitwallet.view.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import androidx.fragment.app.DialogFragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RadioButton;

import java.io.Serializable;
import java.util.Calendar;

import revolhope.splanes.com.bitwallet.R;
import revolhope.splanes.com.bitwallet.helper.FingerprintHelper;

import static android.content.Context.FINGERPRINT_SERVICE;

public class DialogToken extends DialogFragment {

    public static final String ARG0 = "OnAuthCallback";

    private int uses = 1;
    private long tmpLifetime = 0;
    private OnSendAuthSucceeded onSendAuthSucceeded;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getContext() == null) return super.onCreateDialog(savedInstanceState);
        if (getArguments() == null) return super.onCreateDialog(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments.containsKey(ARG0)) {
            onSendAuthSucceeded = (OnSendAuthSucceeded) arguments.getSerializable(ARG0);
            if (onSendAuthSucceeded == null) return super.onCreateDialog(savedInstanceState);
        }
        else return super.onCreateDialog(savedInstanceState);

        Activity activity = (Activity) getContext();
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_token, viewGroup, false);

        bindComponents(view);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        tmpLifetime = c.getTimeInMillis();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),
                R.style.AppDialogStyle);
        Spannable span = new SpannableString("Send request");
        span.setSpan(new ForegroundColorSpan(getContext().getColor(R.color.colorPrimaryDark)),
                0, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setView(view);
        builder.setTitle(span);
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });

        Dialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        return dialog;
    }

    private void bindComponents(@NonNull View view) {

        TextInputEditText editTextNumUses = view.findViewById(R.id.textInputEditTextNumOfUses);
        RadioButton lifetime1RadioButton = view.findViewById(R.id.radioButton_lifetime1);
        RadioButton lifetime2RadioButton = view.findViewById(R.id.radioButton_lifetime2);
        RadioButton lifetime5RadioButton = view.findViewById(R.id.radioButton_lifetime5);
        View.OnClickListener listenerLifetime = new View.OnClickListener() {
            private Calendar cal;
            @Override
            public void onClick(@NonNull View view) {
                boolean checked = ((RadioButton) view).isChecked();
                cal = Calendar.getInstance();
                switch(view.getId()) {
                    case R.id.radioButton_lifetime1:
                        if (checked) {
                            cal.set(Calendar.HOUR_OF_DAY, 23);
                            cal.set(Calendar.MINUTE, 59);
                            cal.set(Calendar.SECOND, 59);
                        }
                        break;
                    case R.id.radioButton_lifetime2:
                        if (checked) cal.add(Calendar.DAY_OF_MONTH, 2);
                        break;
                    case R.id.radioButton_lifetime5:
                        if (checked) cal.add(Calendar.DAY_OF_MONTH, 5);
                        break;
                }
                tmpLifetime = cal.getTimeInMillis();
            }
        };

        editTextNumUses.setText(String.valueOf(uses));
        editTextNumUses.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    TextInputEditText editText = (TextInputEditText) v;
                    try {
                        uses = Integer.parseInt(editText.getText().toString());
                    }
                    catch (NumberFormatException e) {
                        uses = 1;
                        e.printStackTrace();
                    }
                }
            }
        });
        lifetime1RadioButton.setChecked(true);
        lifetime1RadioButton.setOnClickListener(listenerLifetime);
        lifetime2RadioButton.setOnClickListener(listenerLifetime);
        lifetime5RadioButton.setOnClickListener(listenerLifetime);
    }

    @Override
    public void onStart() {
        super.onStart();
        final Context context = getContext();
        if (context == null) return;
        FingerprintManager fingerprintManager =
                (FingerprintManager) context.getSystemService(FINGERPRINT_SERVICE);
        if (fingerprintManager != null) {
            try  {
                FingerprintManager.CryptoObject cryptoObj =
                        new FingerprintManager.CryptoObject(FingerprintHelper.getFingerprintCipher());

                FingerprintHelper.AuthCallback callback =
                        new FingerprintHelper.AuthCallback(context,
                                new FingerprintHelper.AuthCallback.OnAuthListener() {
                            @Override
                            public void onAuthenticate(boolean isSucceed, String... errorCodes) {
                                if (isSucceed)
                                    onSendAuthSucceeded.onSendAuthSucceeded(uses, tmpLifetime);
                                else
                                    onSendAuthSucceeded.onSendAuthSucceeded(-1,-1);
                            }
                        });
                callback.startAuth(fingerprintManager, cryptoObj);
            }
            catch(Exception exc) {
                exc.printStackTrace();
            }
        }
    }

    public interface OnSendAuthSucceeded extends Serializable {
        void onSendAuthSucceeded(int uses, long deadLine);
    }
}
