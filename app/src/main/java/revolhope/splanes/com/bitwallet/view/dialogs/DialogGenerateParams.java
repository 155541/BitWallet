package revolhope.splanes.com.bitwallet.view.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import revolhope.splanes.com.bitwallet.R;
import revolhope.splanes.com.bitwallet.helper.AppUtils;
import revolhope.splanes.com.bitwallet.helper.RandomGenerator;

public class DialogGenerateParams extends DialogFragment {

    private EditText editText_sizeOther;
    private DialogCallback callback;
    private int mode;
    private int size;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getContext() != null) {
            Activity activity = (Activity) getContext();
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            ViewGroup viewGroup = activity.findViewById(android.R.id.content);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_generate_params,
                                                             viewGroup, false);
            bindComponents(view, getContext());

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),
                    R.style.AppDialogStyle);

            Spannable span = new SpannableString("Set password params");
            span.setSpan(new ForegroundColorSpan(getContext().getColor(R.color.colorPrimaryDark)),
                        0, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            builder.setTitle(span);
            builder.setView(view);
            builder.setPositiveButton("Generate", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (size == RandomGenerator.SIZE_OTHER ) {
                        String str = editText_sizeOther.getText().toString();
                        try { size = Integer.parseInt(str); }
                        catch (NumberFormatException e) { size = -1; }
                    }
                    if (callback != null) callback.getResult(mode, size);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) { }
            });

            return builder.create();

        }
        else return super.onCreateDialog(savedInstanceState);
    }

    public void bindComponents(@NonNull View view, @NonNull final Context context) {

        RadioButton rb_simple = view.findViewById(R.id.radioButton_simple);
        RadioButton rb_complex = view.findViewById(R.id.radioButton_complex);
        rb_simple.setChecked(true);
        mode = RandomGenerator.MODE_SIMPLE;

        View.OnClickListener listenerType = new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View view) {
                boolean checked = ((RadioButton) view).isChecked();
                switch(view.getId()) {
                    case R.id.radioButton_simple:
                        if (checked) mode = RandomGenerator.MODE_SIMPLE;
                        else size = -1;
                        break;
                    case R.id.radioButton_complex:
                        if (checked) mode = RandomGenerator.MODE_COMPLEX;
                        else size = -1;
                        break;
                }
            }
        };

        rb_simple.setOnClickListener(listenerType);
        rb_complex.setOnClickListener(listenerType);
        Spinner spinnerSize = view.findViewById(R.id.spinnerSize);
        ArrayAdapter<CharSequence> spinSizeAdapter =
                ArrayAdapter.createFromResource(context, R.array.ArraySpinnerContractSizes,
                                                android.R.layout.simple_spinner_item);
        spinnerSize.setAdapter(spinSizeAdapter);
        spinnerSize.setSelection(0);
        spinnerSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object o = parent.getItemAtPosition(position);
                size = AppUtils.stringToConstant(o.toString());
                if (size == RandomGenerator.SIZE_OTHER) {
                    editText_sizeOther.setVisibility(View.VISIBLE);
                }
                else  {
                    editText_sizeOther.setVisibility(View.GONE);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
                size = RandomGenerator.SIZE_12;
            }
        });

        editText_sizeOther = view.findViewById(R.id.editText_otherSize);
        editText_sizeOther.setVisibility(View.GONE);
    }

    public void setCallback(DialogCallback callback) {
        this.callback = callback;
    }

    public interface DialogCallback {
        void getResult(int mode, int size);
    }
}
