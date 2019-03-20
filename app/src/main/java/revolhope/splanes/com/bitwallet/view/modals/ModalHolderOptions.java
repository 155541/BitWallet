package revolhope.splanes.com.bitwallet.view.modals;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

import revolhope.splanes.com.bitwallet.R;
import revolhope.splanes.com.bitwallet.helper.AppContract;

public class ModalHolderOptions extends BottomSheetDialogFragment {

    public static final String TAG = "ModalHolderOptions";
    public static final String ARG0 = "IsAccount";
    public static final String ARG1 = "Callback";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        Context context = getContext();
        if (bundle == null ||
           context == null ||
           !bundle.containsKey(ARG0) ||
           !bundle.containsKey(ARG1) ||
           !(bundle.getSerializable(ARG1) instanceof OnModalOptionPicked))
        {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
        View view = inflater.inflate(R.layout.modal_holder_options, container, false);
        bindComponents(view, context, bundle.getBoolean(ARG0, false),
                (OnModalOptionPicked) bundle.getSerializable(ARG1));
        return view;
    }

    private void bindComponents(@NonNull View view, @NonNull Context context,
                                final boolean isAccount, final OnModalOptionPicked callback) {

        TextView textViewAction1 = view.findViewById(R.id.textViewAction1);
        TextView textViewAction2 = view.findViewById(R.id.textViewAction2);
        TextView textViewAction3 = view.findViewById(R.id.textViewAction3);
        ImageView icAction1 = view.findViewById(R.id.ic_action1);
        ImageView icAction2 = view.findViewById(R.id.ic_action2);

        Drawable drawableMove = context.getDrawable(R.drawable.ic_move_item);
        Drawable drawableAux;
        if (isAccount) {
            textViewAction1.setText(context.getString(R.string.prompt_bitwallet));
            icAction1.setImageResource(R.drawable.ic_app_brand);
            textViewAction2.setText(context.getString(R.string.prompt_move));
            icAction2.setImageResource(R.drawable.ic_move_item);
        }
        else {
            textViewAction1.setText(context.getString(R.string.prompt_move));
            icAction1.setImageResource(R.drawable.ic_move_item);
            textViewAction2.setText(context.getString(R.string.prompt_update));
            icAction2.setImageResource(R.drawable.ic_update_item);
        }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.textViewAction1:
                        callback.optionPicked(isAccount ? AppContract.ITEM_WEB :
                                                          AppContract.ITEM_MOVE);
                        return;
                    case R.id.textViewAction2:
                        callback.optionPicked(isAccount ? AppContract.ITEM_MOVE :
                                                          AppContract.ITEM_UPDATE);
                        return;
                    case R.id.textViewAction3:
                        callback.optionPicked(AppContract.ITEM_DROP);
                }
            }
        };
        textViewAction1.setOnClickListener(listener);
        textViewAction2.setOnClickListener(listener);
        textViewAction3.setOnClickListener(listener);
    }

    public interface OnModalOptionPicked extends Serializable {
        void optionPicked(int option);
    }
}
