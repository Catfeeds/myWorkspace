package com.hunliji.marrybiz.model;

public class Gallery implements Identifiable {
    /**
     *
     */
    private static final long serialVersionUID = 5579102958334784832L;
    private long id;
    private String name;
    private int photoCount;
    private String path;
    private boolean isSelected;

    /**
     * @param id
     * @param name
     * @param photoCount
     * @param path
     */
    public Gallery(long id, String name, int photoCount, String path) {
        super();
        this.id = id;
        this.name = name;
        this.photoCount = photoCount;
        this.path = path;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the photoCount
     */
    public int getPhotoCount() {
        return photoCount;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}