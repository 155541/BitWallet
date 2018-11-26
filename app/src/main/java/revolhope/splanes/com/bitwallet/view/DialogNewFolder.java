package revolhope.splanes.com.bitwallet.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import revolhope.splanes.com.bitwallet.R;

public class DialogNewFolder extends DialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getContext() != null) {

            Activity activity = (Activity) getContext();
            ViewGroup viewGroup = activity.findViewById(android.R.id.content);

            View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_folder,
                                                                  viewGroup, false);
            final EditText editText_folderName = view.findViewById(R.id.editText_folderName);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            Spannable spannable = new SpannableString("New folder");
            spannable.setSpan(new ForegroundColorSpan(getContext().getColor(R.color.colorPrimaryDark)),
                    0,spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setTitle(spannable);
            builder.setView(view);
            builder.setPositiveButton("create", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String name = editText_folderName.getText().toString();
                    if (!name.isEmpty() && getActivity() != null) {
                        ((MainActivity) getActivity()).newFolder(name);
                    }
                    else {
                        Toast.makeText(getContext(), "Invalid folder name",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            return builder.create();
        }
        return super.onCreateDialog(savedInstanceState);
    }
}
