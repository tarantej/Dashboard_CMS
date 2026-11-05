package com.dashboard.repository;

import com.dashboard.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectStatusRepo  extends JpaRepository<ProjectStatus, Integer>
{

}