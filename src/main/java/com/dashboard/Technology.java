package com.dashboard;


import jakarta.persistence.*;

@Entity
@Table(name = "technology", schema = "public")
public class Technology
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "technology_id")
    private Integer technologyId;

    @Column(name = "technology_name", nullable = false, length = 100)
    private String technologyName;

    @Column(name = "technology_slug", length = 100)
    private String technologySlug;

    @Column(name = "technology_logo", length = 255)
    private String technologyLogo;

    @Column(name = "technology_version", length = 50)
    private String technologyVersion;

    @Column(name = "technology_category", length = 50)
    private String technologyCategory;

    public Technology() {
    }

    public Integer getTechnologyId() {
        return technologyId;
    }

    public void setTechnologyId(Integer technologyId) {
        this.technologyId = technologyId;
    }

    public String getTechnologyName() {
        return technologyName;
    }

    public void setTechnologyName(String technologyName) {
        this.technologyName = technologyName;
    }

    public String getTechnologySlug() {
        return technologySlug;
    }

    public void setTechnologySlug(String technologySlug) {
        this.technologySlug = technologySlug;
    }

    public String getTechnologyLogo() {
        return technologyLogo;
    }

    public void setTechnologyLogo(String technologyLogo) {
        this.technologyLogo = technologyLogo;
    }

    public String getTechnologyVersion() {
        return technologyVersion;
    }

    public void setTechnologyVersion(String technologyVersion) {
        this.technologyVersion = technologyVersion;
    }

    public String getTechnologyCategory() {
        return technologyCategory;
    }

    public void setTechnologyCategory(String technologyCategory) {
        this.technologyCategory = technologyCategory;
    }
}