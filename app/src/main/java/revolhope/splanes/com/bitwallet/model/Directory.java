package revolhope.splanes.com.bitwallet.model;

public class Directory {

    private Long _id;
    private String name;
    private Long parentId;

    public Directory(Long _id, String name, Long parentId) {
        this._id = _id;
        this.name = name;
        this.parentId = parentId;
    }

    public Directory(String name, Long parentId) {
        this.name = name;
        this.parentId = parentId;
    }

    public Long get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParentId(Long id) { this.parentId = id; }
}
