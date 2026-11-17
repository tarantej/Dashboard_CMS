
package com.dashboard.controller;

import com.dashboard.DashboardUsers;
import com.dashboard.Project;
import com.dashboard.ProjectStatus;
import com.dashboard.ProjectType;
import com.dashboard.repository.UserRepository;
import com.dashboard.service.ProjectService;
import com.dashboard.service.TechnologyService;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final TechnologyService technologyService;

    public DashboardController(
            UserRepository userRepository,
            ProjectService projectService,
            TechnologyService technologyService) {

        this.userRepository = userRepository;
        this.projectService = projectService;
        this.technologyService = technologyService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Model model,
            Authentication authentication) {

        // =====================================
        // USER AUTHENTICATION
        // =====================================

        DashboardUsers user;

        if (authentication.getPrincipal() instanceof OAuth2User oauthUser) {

            String email = oauthUser.getAttribute("email");

            user = userRepository
                    .findByEmail(email)
                    .orElse(null);

            if (user == null) {
                return "redirect:/oauth-signup";
            }

        } else {

            user = userRepository
                    .findByUsername(authentication.getName())
                    .orElseThrow(() ->
                            new RuntimeException("User not found"));
        }

        // =====================================
        // GREETING
        // =====================================

        LocalTime now = LocalTime.now();

        String greeting;

        if (now.getHour() < 12) {
            greeting = "Good Morning";
        } else if (now.getHour() < 17) {
            greeting = "Good Afternoon";
        } else {
            greeting = "Good Evening";
        }

        model.addAttribute("greeting", greeting);
        model.addAttribute("first_name", user.getFirstName());


        // =====================================
        // PROJECT DATA
        // =====================================

        List<Project> projects = projectService.findAllProjects();

        List<Project> recentProjects =
                projectService.findLatestThreeProjects();

        model.addAttribute("recentProjects", recentProjects);

        // =====================================
        // SUMMARY CARDS
        // =====================================

        // Total Projects
        long totalProjects = projects.size();

        model.addAttribute("totalProjects", totalProjects);


        // In Progress Projects
        long inProgressProjects = projects.stream()
                .filter(project ->
                        project.getProjectStatus() != null)
                .filter(project ->
                        "In Progress".equalsIgnoreCase(
                                project.getProjectStatus().getStatusName()))
                .count();

        model.addAttribute(
                "inProgressProjects",
                inProgressProjects);


        // Projects added in the last 30 days
        Timestamp thirtyDaysAgo = Timestamp.valueOf(
                LocalDateTime.now().minusDays(30));

        long recentlyAddedProjects = projects.stream()
                .filter(project -> project.getCreatedAt() != null)
                .filter(project ->
                        project.getCreatedAt().after(thirtyDaysAgo))
                .count();

        model.addAttribute(
                "recentlyAddedProjects",
                recentlyAddedProjects);

        // =====================================
        // PROJECT TYPE STATISTICS
        // =====================================

        Map<String, Long> typeCounts = new HashMap<>();

        for (Project project : projects) {

            if (project.getProjectType() != null) {

                String typeName =
                        project.getProjectType().getTypeName();

                typeCounts.merge(typeName, 1L, Long::sum);
            }
        }


        // Project type chart colours
        String[] typePalette = {
                "#4e73df",
                "#36b9cc",
                "#1cc88a",
                "#f6c23e",
                "#858796"
        };

        Map<String, String> typeColors = Map.of(
                "Personal", "#4e73df",
                "Learning", "#36b9cc",
                "Client", "#1cc88a",
                "University", "#f6c23e",
                "Open Source", "#858796"
        );


        // Build project type statistics
        List<Map<String, Object>> projectTypeStats =
                new ArrayList<>();

        int typeIndex = 0;

        for (ProjectType type : projectService.findAllProjectTypes()) {

            String typeName = type.getTypeName();

            Map<String, Object> stat = new LinkedHashMap<>();

            long count = typeCounts.getOrDefault(typeName, 0L);

            long totalTypedProjects = typeCounts.values()
                    .stream()
                    .mapToLong(Long::longValue)
                    .sum();

            int percentage = totalTypedProjects == 0
                    ? 0
                    : (int) Math.round(
                    count * 100.0 / totalTypedProjects);

            stat.put("name", typeName);
            stat.put("count", count);
            stat.put("percentage", percentage);

            stat.put(
                    "color",
                    typeColors.getOrDefault(
                            typeName,
                            typePalette[typeIndex % typePalette.length]));

            projectTypeStats.add(stat);

            typeIndex++;
        }


        // Send statistics to Thymeleaf
        model.addAttribute("projectTypeStats", projectTypeStats);


        // Doughnut chart data
        model.addAttribute(
                "projectTypeLabels",
                projectTypeStats.stream()
                        .map(stat -> stat.get("name"))
                        .toList());

        model.addAttribute(
                "projectTypeCounts",
                projectTypeStats.stream()
                        .map(stat -> stat.get("count"))
                        .toList());

        model.addAttribute(
                "projectTypeColors",
                projectTypeStats.stream()
                        .map(stat -> stat.get("color"))
                        .toList());

        Map<String, String> statusColors = Map.of(
                "primary", "#4e73df",
                "success", "#1cc88a",
                "info", "#36b9cc",
                "warning", "#f6c23e",
                "danger", "#e74a3b",
                "secondary", "#858796",
                "dark", "#5a5c69"
        );

        // =====================================
        // PROJECT STATUS STATISTICS
        // =====================================

        Map<String, Long> statusCounts = new HashMap<>();

        for (Project project : projects) {

            if (project.getProjectStatus() != null) {

                String statusName =
                        project.getProjectStatus().getStatusName();

                statusCounts.merge(statusName, 1L, Long::sum);
            }
        }

        long totalStatusProjects = statusCounts.values()
                .stream()
                .mapToLong(Long::longValue)
                .sum();

        List<Map<String, Object>> projectStatusStats =
                new ArrayList<>();

        for (ProjectStatus status : projectService.findAllProjectStatuses()) {

            String statusName = status.getStatusName();

            long count = statusCounts.getOrDefault(statusName, 0L);

            int percentage = totalStatusProjects == 0
                    ? 0
                    : (int) Math.round(
                    count * 100.0 / totalStatusProjects);

            Map<String, Object> stat = new LinkedHashMap<>();

            stat.put("name", statusName);
            stat.put("count", count);
            stat.put("percentage", percentage);
//            stat.put("color", status.getStatusColor());
            String statusColor = status.getStatusColor();

            stat.put(
                    "color",
                    statusColors.getOrDefault(
                            statusColor == null ? "" : statusColor.toLowerCase(),
                            "#858796"
                    )
            );

            projectStatusStats.add(stat);
        }

        model.addAttribute("totalStatusProjects", totalStatusProjects);
        model.addAttribute("projectStatusStats", projectStatusStats);

        // Doughnut chart data
        model.addAttribute(
                "projectStatusLabels",
                projectStatusStats.stream()
                        .map(stat -> stat.get("name"))
                        .toList());

        model.addAttribute(
                "projectStatusCounts",
                projectStatusStats.stream()
                        .map(stat -> stat.get("count"))
                        .toList());

        model.addAttribute(
                "projectStatusColors",
                projectStatusStats.stream()
                        .map(stat -> stat.get("color"))
                        .toList());

        // =====================================
        // TECHNOLOGY USAGE STATISTICS
        // =====================================

        Map<Integer, Map<String, Object>> technologyCounts =
                new HashMap<>();

        for (Project project : projects) {

            if (project.getTechnologies() == null) {
                continue;
            }

            project.getTechnologies().forEach(technology -> {

                technologyCounts.computeIfAbsent(
                        technology.getTechnologyId(),
                        id -> {

                            Map<String, Object> stat =
                                    new LinkedHashMap<>();

                            stat.put(
                                    "name",
                                    technology.getTechnologyName());

                            stat.put("count", 0);

                            return stat;
                        });


                Map<String, Object> stat =
                        technologyCounts.get(
                                technology.getTechnologyId());

                int currentCount = (Integer) stat.get("count");

                stat.put("count", currentCount + 1);
            });
        }


        // Sort by most-used technology
        int maxTechnologyCount = technologyCounts.values()
                .stream()
                .mapToInt(stat -> (Integer) stat.get("count"))
                .max()
                .orElse(0);


        List<Map<String, Object>> technologyUsage =
                technologyCounts.values()
                        .stream()
                        .sorted((a, b) ->
                                Integer.compare(
                                        (Integer) b.get("count"),
                                        (Integer) a.get("count")))
                        .limit(5)
                        .peek(stat -> {

                            int count = (Integer) stat.get("count");

                            int percentage = maxTechnologyCount == 0
                                    ? 0
                                    : (int) Math.round(
                                    count * 100.0 /
                                    maxTechnologyCount);

                            stat.put("percentage", percentage);
                        })
                        .toList();

        model.addAttribute(
                "technologyUsage",
                technologyUsage);


        // Total technologies in catalogue
        model.addAttribute(
                "totalTechnologies",
                technologyService.findAllTechnologies().size());

        return "Dashboard/dashboard";
    }


    // =====================================
    // BLANK PAGE
    // =====================================

    @GetMapping("/blank")
    public String blankPage(Model model, Authentication authentication) {

        DashboardUsers user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        model.addAttribute("pageTitle", "Blank Page");

        return "Dashboard/blank";
    }
}