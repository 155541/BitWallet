
public class K {

    private Long _id;
    private Long accId;
    private String pwdBase64;
    private GCMParameterSpec spec;
    private Long deadline;
    
    public K (@NonNull _id, @NonNull accId, @NonNull pwdBase64
              @NonNull GCMParameterSpec spec, @NonNull deadline) {
        
        this._id = _id;
        this.accId = accId;
        this.pwdBase64 = pwdBase64;
        this.spec = spec;
        this.deadline = deadline;
    }
    
    public K() {}
    
}
