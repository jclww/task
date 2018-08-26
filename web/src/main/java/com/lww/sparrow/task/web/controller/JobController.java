package com.lww.sparrow.task.web.controller;

import com.lww.sparrow.task.domain.entity.JobInfo;
import com.lww.sparrow.task.service.job.JobInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@Slf4j
@RequestMapping("/api/job")
public class JobController {
    @Resource
    private JobInfoService jobInfoService;

    @RequestMapping(value="/", method = RequestMethod.POST)
    @ResponseBody
    public JobInfo addJob(String username, @RequestBody JobInfo job) {
//        job.setExecuteUrl(username);
        jobInfoService.insert(job);
        return job;
    }
    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    @ResponseBody
    public JobInfo getJobById(String username, @PathVariable long id) {
        return jobInfoService.findById(id);
    }
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @ResponseBody
    public String hello() {
        log.info("hello");
        return "hello";
    }
}
