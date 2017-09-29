package me.gavin.photo.viewer.app;

import java.io.Serializable;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/6/3
 */
public class Image implements Serializable {

    private long id;
    private Long parentId;
    private String parent;
    private int count;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
