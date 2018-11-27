package revolhope.splanes.com.bitwallet.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import revolhope.splanes.com.bitwallet.R;
import revolhope.splanes.com.bitwallet.db.DaoCallbacks;
import revolhope.splanes.com.bitwallet.db.DaoDirectory;
import revolhope.splanes.com.bitwallet.model.Directory;

public class DialogMove extends DialogFragment {

    private Activity activity;
    private Adapter adapter;
    private DaoDirectory daoDirectory;
    private OnMoveListener listener;
    private String name = "";
    private Long currentDir;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getContext() == null)
            return super.onCreateDialog(savedInstanceState);

        activity = (Activity)getContext();
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_move, viewGroup, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewMove);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new Adapter();
        adapter.context = getContext();

        daoDirectory = DaoDirectory.getInstance(getContext());
        try {
            daoDirectory.findInRoot(new DaoCallbacks.Select<Directory>() {
                @Override
                public void onSelected(final Directory[] selection) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.directories = Arrays.asList(selection);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            });
            daoDirectory.findRoot(new DaoCallbacks.Select<Directory>() {
                @Override
                public void onSelected(Directory[] selection) {
                    if (selection != null && selection.length == 1)
                        currentDir = selection[0].get_id();
                }
            });
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        recyclerView.setAdapter(adapter);

        Spannable spannable = new SpannableString("Move: " + name);
        spannable.setSpan(new ForegroundColorSpan(getContext().getColor(R.color.colorPrimaryDark)),
                0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(spannable);
        builder.setView(view);
        builder.setPositiveButton("Move", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onMove(currentDir);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return builder.create();
    }


    private class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        private Context context;
        private List<Directory> directories;

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context)
                    .inflate(R.layout.holder_content_dir_main_fragment, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            if (directories != null && position < directories.size()) {
                holder.textViewName.setText(directories.get(position).getName());
            }
        }

        @Override
        public int getItemCount() {
            return directories != null ? directories.size() : 0;
        }

        private class Holder extends RecyclerView.ViewHolder {

            private TextView textViewName;

            private Holder(View view){
                super(view);
                textViewName = view.findViewById(R.id.textView_Name);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int position = getAdapterPosition();
                        if (directories != null && position < directories.size()) {

                            try {
                                currentDir = directories.get(position).get_id();
                                daoDirectory.findChildrenAt(currentDir,
                                        new DaoCallbacks.Select<Directory>() {
                                            @Override
                                            public void onSelected(final Directory[] selection) {
                                                if (activity != null) {
                                                    activity.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            adapter.directories =
                                                                    Arrays.asList(selection);
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                    });
                                                }
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

    public interface OnMoveListener {
        void onMove(long newParent);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setListener(OnMoveListener listener) {
        this.listener = listener;
    }
}
