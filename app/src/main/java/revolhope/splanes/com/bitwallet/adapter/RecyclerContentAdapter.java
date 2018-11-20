package revolhope.splanes.com.bitwallet.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import revolhope.splanes.com.bitwallet.R;
import revolhope.splanes.com.bitwallet.helper.AppUtils;
import revolhope.splanes.com.bitwallet.model.Account;
import revolhope.splanes.com.bitwallet.model.Directory;

public class RecyclerContentAdapter extends RecyclerView.Adapter<RecyclerContentAdapter.Holder> {

    private static final int TYPE_DIR = 352;
    private static final int TYPE_ACC = 718;

    private List<Account> accounts;
    private List<Directory> directories;

    private List<Holder> holders;

    private Context context;

    public RecyclerContentAdapter(Context context)
    {
        this.context = context;
        holders = new ArrayList<>();
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

        holders.add(holder);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position)
    {

        if (accounts != null && !accounts.isEmpty() &&
            directories != null && !directories.isEmpty())
        {
            int dirSize = directories.size();
            if (position < dirSize)
            {
                Directory d = directories.get(position);
                HolderDir h = (HolderDir) holder;

                h.ivLogo.setImageDrawable(
                        context.getDrawable(R.drawable.ic_launcher_background));
                h.textView_Name.setText(d.getName());
            }
            else if ((position - dirSize) < accounts.size())
            {
                Account account = accounts.get(position - dirSize);
                HolderAccount h = (HolderAccount) holder;

                h.ivLogo.setImageDrawable(
                        context.getDrawable(R.drawable.ic_launcher_background));
                h.textView_Name.setText(account.getAccount());
                h.textView_Create.setText(
                        AppUtils.format("Create on: dd/MM/yyyy",
                                    account.getDateCreate()));
                if (account.getDateUpdate() != null)
                {
                    h.textView_Update.setText(
                            AppUtils.format("Updated on: dd/MM/yyyy",
                                    account.getDateUpdate()));
                }
                if (account.isExpire())
                {
                    h.textView_Expire.setText(
                            AppUtils.format("Expires on: dd/MM/yyyy",
                                    account.getDateExpire()));
                }
            }
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
        this.accounts = accounts;
        notifyDataSetChanged();
    }

    public void setDirectories(List<Directory> directories)
    {
        this.directories = directories;
        notifyDataSetChanged();
    }


// ============================================================================================== //
//                                          INNER CLASSES                                             //
// ============================================================================================== //

    abstract class Holder extends RecyclerView.ViewHolder
    {
        Holder(View view){ super(view); }
    }

    class HolderAccount extends Holder
    {
        private ImageView ivLogo;
        private TextView textView_Name;
        private TextView textView_Create;
        private TextView textView_Update;
        private TextView textView_Expire;

        HolderAccount (View view)
        {
            super(view);

            ivLogo = view.findViewById(R.id.ivLogo);
            textView_Name = view.findViewById(R.id.textView_Name);
            textView_Create = view.findViewById(R.id.textView_createOn);
            textView_Update = view.findViewById(R.id.textView_updateOn);
            textView_Expire = view.findViewById(R.id.textView_expire);
        }
    }

    class HolderDir extends Holder
    {
        private ImageView ivLogo;
        private TextView textView_Name;

        HolderDir (View view)
        {
            super(view);
            ivLogo = view.findViewById(R.id.ivLogo);
            textView_Name = view.findViewById(R.id.textView_Name);
        }
    }
}
