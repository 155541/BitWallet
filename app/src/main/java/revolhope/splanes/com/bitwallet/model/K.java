package revolhope.splanes.com.bitwallet.model;

import android.support.annotation.NonNull;

import javax.crypto.spec.GCMParameterSpec;

public class K {

    private Long _id;
    private Long accId;
    private String pwdBase64;
    private GCMParameterSpec spec;
    private Long deadline;
    
    public K (@NonNull Long _id, @NonNull Long accId, @NonNull String pwdBase64,
              @NonNull GCMParameterSpec spec, @NonNull Long deadline) {
        
        this._id = _id;
        this.accId = accId;
        this.pwdBase64 = pwdBase64;
        this.spec = spec;
        this.deadline = deadline;
    }
    
    public K() {}

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public Long getAccId() {
        return accId;
    }

    public void setAccId(Long accId) {
        this.accId = accId;
    }

    public String getPwdBase64() {
        return pwdBase64;
    }

    public void setPwdBase64(String pwdBase64) {
        this.pwdBase64 = pwdBase64;
    }

    public GCMParameterSpec getSpec() {
        return spec;
    }

    public void setSpec(GCMParameterSpec spec) {
        this.spec = spec;
    }

    public Long getDeadline() {
        return deadline;
    }

    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }
}
