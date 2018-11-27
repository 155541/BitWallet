package revolhope.splanes.com.bitwallet.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import revolhope.splanes.com.bitwallet.R;
import revolhope.splanes.com.bitwallet.helper.AppUtils;
import revolhope.splanes.com.bitwallet.model.Account;
import revolhope.splanes.com.bitwallet.model.Directory;
import revolhope.splanes.com.bitwallet.view.MainFragment;

public class RecyclerContentAdapter extends RecyclerView.Adapter<RecyclerContentAdapter.Holder> {

    private static final int TYPE_DIR = 352;
    private static final int TYPE_ACC = 718;

    private List<Account> accounts;
    private List<Directory> directories;

    private MainFragment.OnAccClick onClickAcc;
    private MainFragment.OnDirClick onClickDir;

    private Context context;

    public RecyclerContentAdapter(Context context)
    {
        this.context = context;
    }

    @Override
    public long getItemId(int position) {

        if (accounts != null && !accounts.isEmpty() &&
                directories != null && !directories.isEmpty())
        {
            int dirSize = directories.size();
            if (position >= dirSize)
            {

                if (accounts.size() > (position - dirSize))
                {
                    return position-dirSize;
                }
                else
                {
                    return 0;
                }
            }
            else
            {
                return directories.get(position).get_id();
            }
        }
        else return super.getItemId(position);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Holder holder;
        if (viewType == TYPE_DIR)
        {
            holder = new HolderDir(LayoutInflater
                    .from(context)
                    .inflate(
                            R.layout.holder_content_dir_main_fragment,
                            parent,
                            false));
        }
        else
        {
            holder = new HolderAccount(LayoutInflater
                    .from(context)
                    .inflate(
                            R.layout.holder_content_acc_main_fragment,
                            parent,
                            false));
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position)
    {
        if (directories != null && !directories.isEmpty() &&
            directories.size() > position && holder instanceof HolderDir) {
            bindDirectory((HolderDir) holder, position);
        }
        else if (directories != null && !directories.isEmpty() &&
                 accounts != null && !accounts.isEmpty() &&
                 accounts.size() > position - directories.size() &&
                 holder instanceof HolderAccount) {
                bindAccount((HolderAccount) holder, position - directories.size());
        }
        else if (accounts != null && !accounts.isEmpty() &&
                 accounts.size() > position && holder instanceof HolderAccount) {
            bindAccount((HolderAccount) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (accounts != null) {
            count += accounts.size();
        }
        if (directories != null) {
            count += directories.size();
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {

        if (accounts != null && !accounts.isEmpty() &&
            directories != null && !directories.isEmpty())
        {
            int dirSize = directories.size();
            if (position < dirSize)
            {
                return TYPE_DIR;
            }
            else
            {
                return TYPE_ACC;
            }
        }
        return super.getItemViewType(position);
    }

    public void setAccounts(List<Account> accounts)
    {
        try {
            this.accounts = accounts;
            notifyDataSetChanged();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDirectories(List<Directory> directories)
    {
        try {
            this.directories = directories;
            notifyDataSetChanged();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDirectories(List<Directory> directories)
    {
        try {
            this.directories.addAll(directories);
            notifyDataSetChanged();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindAccount(@NonNull HolderAccount holder, int position) {

        Account account = accounts.get(position);
        holder.textView_Name.setText(account.getAccount());
        holder.textView_Create.setText(String.format("Created on: %s",
                AppUtils.format("dd/MM/yyyy",account.getDateCreate())));
        if (account.getDateUpdate() != null && account.getDateUpdate() != 0)
        {
            holder.textView_Update.setText(String.format("Updated on: %s",
                    AppUtils.format("dd/MM/yyyy", account.getDateUpdate())));
        }
        if (account.isExpire())
        {
            holder.textView_Expire.setText(String.format("Expires on: %s",
                    AppUtils.format("dd/MM/yyyy", account.getDateExpire())));
        }
    }

    private void bindDirectory(@NonNull HolderDir holder, int position) {
        Directory d = directories.get(position);
        holder.textView_Name.setText(d.getName());
    }

    public void setOnClickAcc(MainFragment.OnAccClick onClickAcc) {
        this.onClickAcc = onClickAcc;
    }
    public void setOnClickDir(MainFragment.OnDirClick onClickDir) {
        this.onClickDir = onClickDir;
    }

// ============================================================================================== //
//                                          INNER CLASSES                                         //
// ============================================================================================== //

    abstract class Holder extends RecyclerView.ViewHolder
    {
        Holder(View view){ super(view); }
    }

    class HolderAccount extends Holder
    {
        private TextView textView_Name;
        private TextView textView_Create;
        private TextView textView_Update;
        private TextView textView_Expire;

        HolderAccount (View view)
        {
            super(view);
            textView_Name = view.findViewById(R.id.textView_Name);
            textView_Create = view.findViewById(R.id.textView_createOn);
            textView_Update = view.findViewById(R.id.textView_updateOn);
            textView_Expire = view.findViewById(R.id.textView_expire);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int pos = directories == null || directories.isEmpty() ?
                              getAdapterPosition() : getAdapterPosition() - directories.size();
                    if (pos >= 0 && accounts != null && accounts.size() > pos) {
                        onClickAcc.onClick(accounts.get(pos));
                    }
                    else {
                        Toast.makeText(context, "Error to get the account clicked...",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = directories == null || directories.isEmpty() ?
                            getAdapterPosition() : getAdapterPosition() - directories.size();
                    if (pos >= 0 && accounts != null && accounts.size() > pos) {
                        onClickAcc.onLongClick(accounts.get(pos));
                    }
                    else {
                        Toast.makeText(context, "Error to get the account clicked...",
                                Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
            });

        }
    }

    class HolderDir extends Holder
    {
        private TextView textView_Name;

        HolderDir (View view)
        {
            super(view);
            textView_Name = view.findViewById(R.id.textView_Name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (directories != null && directories.size() > getAdapterPosition()) {
                        onClickDir.onClick(directories.get(getAdapterPosition()));
                    }
                    else {
                        Toast.makeText(context, "Error to get the directory clicked...",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (directories != null && directories.size() > getAdapterPosition()) {
                        onClickDir.onLongClick(directories.get(getAdapterPosition()));
                    }
                    else {
                        Toast.makeText(context, "Error to get the directory clicked...",
                                Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
            });
        }
    }
}
