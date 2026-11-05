package com.dashboard;

import jakarta.persistence.*;

@Entity
@Table(name = "project_status")

public class ProjectStatus
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Integer statusId;

    @Column(name = "status_name", nullable = false)
    private String statusName;

    @Column(name = "status_color")
    private String statusColor;

    public Integer getStatusId() {
        return statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public String getStatusColor() {
        return statusColor;
    }
}