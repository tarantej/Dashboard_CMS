package com.dashboard;

import jakarta.persistence.*;

@Entity
@Table(name = "project_type")

public class ProjectType
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "type_name", nullable = false)
    private String typeName;

    public Integer getTypeId() {
        return typeId;
    }

    public String getTypeName() {
        return typeName;
    }
}