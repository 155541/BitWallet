package revolhope.splanes.com.bitwallet.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
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
                content.setSpan(new ForegroundColorSpan(context.getColor(R.color.colorAccent)),
                        0, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.textViewName.setText(content);
            }
            else {
                holder.textViewName.setText(R.string.prompt_home);
            }
        }
        else {
            if (position != directories.size()) { // NOT LAST
                SpannableString content = new SpannableString(directories.get(position-1).getName());
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                content.setSpan(new ForegroundColorSpan(context.getColor(R.color.colorAccent)),
                        0, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.textViewName.setText(content);
            }
            else { // LAST
                holder.textViewName.setText(directories.get(position-1).getName());
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

    public void setDirectories(List<Directory> directories) {
        this.directories.clear();
        this.directories.addAll(directories);
        notifyDataSetChanged();
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

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        if (getAdapterPosition() == 0 && !directories.isEmpty()) {
                            listener.onClick(null);
                        }
                        else if (directories.size() > getAdapterPosition()-1 &&
                                 getAdapterPosition() > 0){

                            Directory d = directories.get(getAdapterPosition()-1);
                            listener.onClick(d);

                            Iterator<Directory> it = directories.iterator();
                            boolean b = false;

                            do {
                                if (b) {
                                    it.next();
                                    it.remove();
                                }
                                else if (it.next().get_id().equals(d.get_id())) {
                                    b = true;
                                }
                            } while (it.hasNext());

                            notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }

    public interface OnClickListener {
        void onClick(Directory directory);
    }
}
