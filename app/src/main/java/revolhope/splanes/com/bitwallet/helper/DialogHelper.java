package revolhope.splanes.com.bitwallet.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import revolhope.splanes.com.bitwallet.R;

public class DialogHelper extends DialogFragment {

    public static final String TAG = "DialogHelper";
    private AlertDialog.Builder builder;

    // NEW
    public interface DialogHelperListener {
        void onDialogClick();
    }

    public static void showInfo(@NonNull Context context,
                                @Nullable String title,
                                @NonNull String body,
                                @Nullable final DialogHelperListener listener) {

        Drawable icon = context.getDrawable(R.drawable.ic_dialog_info);
        Spannable span = new SpannableString(title != null ? title : "Information");
        AlertDialog.Builder builder = new AlertDialog.Builder(context,
                R.style.AppDialogStyle);

        span.setSpan(new ForegroundColorSpan(context.getColor(R.color.colorPrimaryDark)),
                0, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setTitle(span);
        if (icon != null) {
            icon.setAlpha(85);
            icon.setTint(context.getColor(R.color.colorPrimary));
            builder.setIcon(icon);
        }
        builder.setMessage(body);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) listener.onDialogClick();
            }
        });
        builder.create().show();
    }

/*
    public DialogHelper(@NonNull Context context) {
        builder = new AlertDialog.Builder(context);
    }
    
    public void setStrings(@NonNull String title, @Nullable String message) {
        builder.setTitle(title);
        builder.setMessage(message);
    }
    
    public void setPositiveButton(@NonNull String positiveButton,
                                   @NonNull DialogInterface.OnClickListener listener) {
        builder.setPositiveButton(positiveButton, listener);
    }
    
    public void setNeutralButton(@NonNull String neutralButton,
                                 @NonNull DialogInterface.OnClickListener listener) {
        builder.setNeutralButton(neutralButton, listener);
    }
    
    public void setNegativeButton(@NonNull String negativeButton,
                                  @NonNull DialogInterface.OnClickListener listener) {
        builder.setNegativeButton(negativeButton, listener);
    }
    
    public void setIcon(int drawableRes) {
        builder.setIcon(drawableRes);
    }

    public void setView(View view) {
        builder.setView(view);
    }
    
    public AlertDialog createDialog() {
        return builder.create();
    }
    
    public void show() {
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showInfo(@NonNull String title, @NonNull String message, @NonNull Context context) {
        DialogHelper help = new DialogHelper(context);
        help.setStrings(title, message);
        help.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        help.setIcon(R.drawable.ic_dialog_info);
        help.show();
    }
    
    public static AlertDialog showLoader(@NonNull String title,
                                         @NonNull Context context, 
                                         @NonNull DialogInterface.OnClickListener cancelListener) {
        
        Activity activity = (Activity) context;
        ViewGroup vg = (ViewGroup) activity.findViewById(android.R.id.content);
        
        DialogHelper help = new DialogHelper(context);
        help.setStrings(title, null);
        help.setView(LayoutInflater.from(context).inflate(R.layout.dialog_loader, vg, false));
        help.setNegativeButton("Cancel", cancelListener);
        AlertDialog d = help.createDialog();
        d.show();
        return d;
    }*/
}
