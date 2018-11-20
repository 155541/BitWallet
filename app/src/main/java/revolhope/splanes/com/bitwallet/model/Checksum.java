package revolhope.splanes.com.bitwallet.model;

import java.util.Objects;

public class Checksum {

    private Long accountId;
    private String sha1;

    public Checksum(Long accountId, String sha1) {
        this.accountId = accountId;
        this.sha1 = sha1;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getSha1() {
        return sha1;
    }

    public boolean validate(Account account)
    {
       /*Cryptography crypto = Cryptography.getInstance();
        String sha1 = crypto.encryptSha1(Util.getChecksumString(account));
        return Objects.equals(sha1, this.sha1);*/
        return false;
    }
}
