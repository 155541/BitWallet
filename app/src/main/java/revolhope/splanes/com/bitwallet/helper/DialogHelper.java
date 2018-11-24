package revolhope.splanes.com.bitwallet.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import revolhope.splanes.com.bitwallet.R;

public class DialogHelper {
   
    private AlertDialog.Builder builder;
    
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
    }
}
