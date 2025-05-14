package com.jobportal.service;

import com.jobportal.dto.ApplicantDTO;
import com.jobportal.dto.Application;
import com.jobportal.dto.JobDTO;
import com.jobportal.exception.JobPortalException;

import java.util.List;

public interface JobService {
    public JobDTO postJob(JobDTO jobDTO) throws JobPortalException;

    public List<JobDTO> getAllJobs() throws JobPortalException;

    public JobDTO getJobs(Long id) throws JobPortalException;

    void applyJob(Long id, ApplicantDTO applicantDTO) throws JobPortalException;

    public  List<JobDTO> getJobPostedBy(Long id) throws JobPortalException;

    public void changeAppStatus(Application application) throws JobPortalException;
}
