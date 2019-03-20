package revolhope.splanes.com.bitwallet.view.modals;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import revolhope.splanes.com.bitwallet.R;
import revolhope.splanes.com.bitwallet.db.DaoCallbacks;
import revolhope.splanes.com.bitwallet.db.DaoDirectory;
import revolhope.splanes.com.bitwallet.model.Directory;

public class ModalMoveItem extends BottomSheetDialogFragment {

    public static final String TAG = "ModalMoveItem";
    public static final String ARG0 = "IsAccount";
    public static final String ARG1 = "Callback";

    private DaoDirectory daoDirectory;
    private OnMoveListener onMoveListener;
    private Long currentDir;

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
                !(bundle.getSerializable(ARG1) instanceof OnMoveListener))
        {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
        View view = inflater.inflate(R.layout.modal_holder_options, container, false);
        bindComponents(view, context, bundle.getBoolean(ARG0, false),
                (OnMoveListener) bundle.getSerializable(ARG1));
        return view;
    }

    private void bindComponents(View view, Context context, boolean isAccount,
                                OnMoveListener listener) {

        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewMove);

        textViewTitle.setText("Move to: Home");
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(null);

    }

    private class MoveAdapter extends RecyclerView.Adapter<MoveAdapter.Holder> {

        private Context context;
        private List<Directory> directories;

        private MoveAdapter(Context context, List<Directory> directories) {
            this.context = context;
            this.directories = directories;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.modal_move_item_holder,
                    parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            if (directories != null && position < directories.size()) {
                holder.directoryName.setText(directories.get(position).getName());
            }
        }

        @Override
        public int getItemCount() {
            return directories != null ? directories.size() : 0;
        }

        private class Holder extends RecyclerView.ViewHolder {
            private TextView directoryName;
            Holder(View view) {
                super(view);
                directoryName = view.findViewById(R.id.textViewDirectoryName);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (directories != null && directories.size() > getAdapterPosition()) {
                            try {
                                daoDirectory.findChildrenAt(directories.get(getAdapterPosition()).get_id(),
                                        new DaoCallbacks.Select<Directory>() {
                                            @Override
                                            public void onSelected(Directory[] selection) {

                                            }
                                        });
                            }
                            catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }

    public interface OnMoveListener extends Serializable {
        void onMove(Long newParent);
    }

}
