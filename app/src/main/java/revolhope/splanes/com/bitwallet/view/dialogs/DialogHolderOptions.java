package revolhope.splanes.com.bitwallet.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import revolhope.splanes.com.bitwallet.R;
import revolhope.splanes.com.bitwallet.helper.AppContract;

public class DialogHolderOptions extends DialogFragment {

    private boolean isDirectory;
    private int pickedOption = -1;
    private OnOptionPicked callback;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        if (getContext() == null) return super.onCreateDialog(savedInstanceState);

        String[] items;
        if (isDirectory) {
            items = new String[]{"Move", "Update", "Drop"};
        }
        else {
            items = new String[]{"BitWallet Web", "Move", "Drop"};
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),
                R.style.AppDialogStyle);

        Spannable spannable = new SpannableString("Pick an option");
        spannable.setSpan(new ForegroundColorSpan(getContext().getColor(R.color.colorPrimaryDark)),
                          0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setTitle(spannable);

        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        pickedOption = isDirectory ?
                                AppContract.ITEM_MOVE : AppContract.ITEM_WEB;
                        break;
                    case 1:
                        pickedOption = isDirectory ?
                                AppContract.ITEM_UPDATE : AppContract.ITEM_MOVE;
                        break;
                    case 2:
                        pickedOption = AppContract.ITEM_DROP;
                        break;
                }
            }
        });

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callback.optionPicked(pickedOption);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });

        return builder.create();
    }

    public void setCallback(OnOptionPicked callback) {
        this.callback = callback;
    }

    public void isDirectory(boolean b) { this.isDirectory = b; }

    public interface OnOptionPicked {
        void optionPicked(int option);
    }
}
