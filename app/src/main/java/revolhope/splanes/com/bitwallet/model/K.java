
public class K {

    private Long _id;
    private Long accId;
    private String pwdBase64;
    private SecretKey _k;
    private GCMParameterSpec spec;
    private Long deadline;
    
    public K (@NonNull _id, @NonNull accId, @NonNull pwdBase64, @NonNull SecretKey _k
              @NonNull GCMParameterSpec spec, @NonNull deadline) {
        
        this._id = _id;
        this.accId = accId;
        this.pwdBase64 = pwdBase64;
        this._k = _k;
        this.spec = spec;
        this.deadline = deadline;
    }
    
    
    
}
