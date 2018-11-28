package revolhope.splanes.com.bitwallet.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import revolhope.splanes.com.bitwallet.R;
import revolhope.splanes.com.bitwallet.model.Directory;

public class RecyclerPathAdapter extends RecyclerView.Adapter<RecyclerPathAdapter.Holder> {

    private List<Directory> directories = new ArrayList<>();
    private Context context;
    private OnClickListener listener;

    public RecyclerPathAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.holder_path_main_fragment,
                parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        if (position > directories.size()+1) return;

        if (position == 0) { // FIRST
            if (!directories.isEmpty()) {
                SpannableString content = new SpannableString(context.getString(R.string.prompt_home));
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                holder.textViewName.setTextColor(context.getColor(R.color.colorAccent));
                holder.textViewName.setText(content);
            }
            else {
                holder.textViewName.setText(R.string.prompt_home);
                holder.textViewName.setTextColor(null);
            }
        }
        else {
            if (position != directories.size()) { // NOT LAST
                SpannableString content = new SpannableString(directories.get(position-1).getName());
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                holder.textViewName.setText(content);
                holder.textViewName.setTextColor(context.getColor(R.color.colorAccent));
            }
            else { // LAST
                holder.textViewName.setText(directories.get(position-1).getName());
                holder.textViewName.setTextColor(null);
            }
        }
    }

    @Override
    public int getItemCount() {

        if (!directories.isEmpty()) {
            return directories.size() + 1;
        }
        else return 1;
    }

    public void addDirectory(Directory directory) {
        this.directories.add(directory);
        notifyDataSetChanged();
    }

    public void removeDirectory(Directory directory) {
        for (Directory d : this.directories) {
            if (d.get_id().equals(directory.get_id())) {
                this.directories.remove(d);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    class Holder extends RecyclerView.ViewHolder {

        private TextView textViewName;

        private Holder(View view) {
            super(view);
            textViewName = view.findViewById(R.id.textViewName);

            if (getAdapterPosition() < (directories.size())) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null) {
                            listener.onClick(directories.get(getAdapterPosition()));
                        }
                    }
                });
            }
        }
    }

    public interface OnClickListener {
        void onClick(Directory directory);
    }
}
