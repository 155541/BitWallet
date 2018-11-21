package revolhope.splanes.com.bitwallet.model;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

public class Account implements Serializable {

    private String _id;
    private String account;
    private String user;
    private String url;
    private String brief;
    private boolean expire;
    private Long dateCreate;
    private Long dateUpdate;
    private Long dateExpire;
    private Long parent;


    public Account(String _id, String account, String user, 
                   String url, String brief, boolean expire,
                   Long dateCreate, Long dateUpdate, Long dateExpire,
                   Long parent) {
        this._id = _id;
        this.account = account;
        this.user = user;
        this.url = url;
        this.brief = brief;
        this.expire = expire;
        this.dateCreate = dateCreate;
        this.dateUpdate = dateUpdate;
        this.dateExpire = dateExpire;
        this.parent = parent;
    }

    // TODO: Delete constructor below & all setters?
    public Account() {
        this._id = UUID.randomUUID().toString();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public boolean isExpire() {
        return expire;
    }

    public void setExpire(boolean expire) {
        this.expire = expire;
    }

    public Long getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Long dateCreate) {
        this.dateCreate = dateCreate;
    }

    public Long getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(Long dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public Long getDateExpire() {
        return dateExpire;
    }

    public void setDateExpire(Long dateExpire) {
        this.dateExpire = dateExpire;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account1 = (Account) o;
        return expire == account1.expire &&
                Objects.equals(dateCreate, account1.dateCreate) &&
                Objects.equals(dateUpdate, account1.dateUpdate) &&
                Objects.equals(dateExpire, account1.dateExpire) &&
                _id.equals(account1._id) &&
                account.equals(account1.account) &&
                Objects.equals(user, account1.user) &&
                Objects.equals(url, account1.url) &&
                parent.equals(account1.parent);
    }

    public boolean isValid()
    {
        if (expire) {
            //Calendar cal = Calendar.getInstance();
            //cal.setTimeInMillis(this.dateExpire);
            //return cal.after(Calendar.getInstance());

            return Calendar.getInstance().before(this.dateExpire);
        }
        else return true;
    }
}
