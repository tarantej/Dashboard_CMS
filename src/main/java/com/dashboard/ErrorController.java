package com.dashboard;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController
{
    @GetMapping("/400")
    public String error400()
    {

        return "error/400";
    }

    @GetMapping("/401")
    public String error401()
    {

        return "error/401";
    }

    @GetMapping("/403")
    public String error403()
    {

        return "error/403";
    }

    @GetMapping("/404")
    public String error404()
    {

        return "error/404";
    }

    @GetMapping("/405")
    public String error405()
    {

        return "error/405";
    }

    @GetMapping("/408")
    public String error408()
    {

        return "error/408";
    }

    @GetMapping("/409")
    public String error409()
    {

        return "error/409";
    }

    @GetMapping("/422")
    public String error422()
    {

        return "error/422";
    }

    @GetMapping("/429")
    public String error429()
    {

        return "error/429";
    }

    @GetMapping("/500")
    public String error500()
    {

        return "error/500";
    }

    @GetMapping("/502")
    public String error502()
    {

        return "error/502";
    }

    @GetMapping("/503")
    public String error503()
    {

        return "error/503";
    }
}