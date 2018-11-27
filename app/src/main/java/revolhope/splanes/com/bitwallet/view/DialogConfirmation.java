package revolhope.splanes.com.bitwallet.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import revolhope.splanes.com.bitwallet.R;

public class DialogConfirmation extends DialogFragment {

    private boolean isDirectory;
    private OnConfirmListener callback;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getContext() == null) return super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        Spannable spannable = new SpannableString("Delete " +
                                                         (isDirectory ? "directory" : "account"));
        spannable.setSpan(new ForegroundColorSpan(getContext().getColor(R.color.colorPrimaryDark)),
                0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setTitle(spannable);
        builder.setMessage("Do you really want to delete this " +
                (isDirectory ? "directory" : "account") + "?\n\nThis action can not be undone");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callback.onConfirm();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });


        return builder.create();
    }

    public void isDirectory(boolean isDirectory) { this.isDirectory = isDirectory; }

    public void setListener(OnConfirmListener listener) { this.callback = listener; }

    public interface OnConfirmListener {
        void onConfirm();
    }
}
