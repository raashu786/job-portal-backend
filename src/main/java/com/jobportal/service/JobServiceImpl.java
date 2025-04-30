package com.jobportal.service;

import com.jobportal.dto.*;
import com.jobportal.entity.Applicant;
import com.jobportal.entity.Job;
import com.jobportal.exception.JobPortalException;
import com.jobportal.repositary.JobRepository;
import com.jobportal.utility.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("jobService")
public class JobServiceImpl implements JobService {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public JobDTO postJob(JobDTO jobDTO) throws JobPortalException {
        if(jobDTO.getId()==0){
            jobDTO.setId(Utilities.getNextSequence("jobs"));
            jobDTO.setPostTime(LocalDateTime.now());
            NotificationDTO notificationDTO=new NotificationDTO();
            notificationDTO.setAction("POSTED JOB");
            notificationDTO.setMessage("JOB POSTED SUCCESSFULLY FOR : "+jobDTO.getJobTitle()+"at"+jobDTO.getCompany());
            notificationDTO.setUserId(jobDTO.getPostedBy());
            notificationDTO.setRoute("/posted-job/"+jobDTO.getId());
                notificationService.sendNotification(notificationDTO);

        }else{
            Job job=jobRepository.findById(jobDTO.getId()).orElseThrow(()-> new JobPortalException("JOB_NOT_FOUND"));
            if(job.getJobStatus().equals(jobStatus.DRAFT)||jobDTO.getJobStatus().equals(jobStatus.CLOSED))jobDTO.setPostTime(LocalDateTime.now());
        }

        return jobRepository.save(jobDTO.toEntity()).toDTO();
    }

    @Override
    public List<JobDTO> getAllJobs() {

        return jobRepository.findAll().stream().map(x -> x.toDTO()).toList();
    }

    @Override
    public JobDTO getJobs(Long id) throws JobPortalException {
        return jobRepository.findById(id).orElseThrow(()-> new JobPortalException("JOB_NOT_FOUND")).toDTO();
    }

    @Override
    public void applyJob(Long id, ApplicantDTO applicantDTO) throws JobPortalException {
        Job job=jobRepository.findById(id).orElseThrow(()-> new JobPortalException("JOB_NOT_FOUND"));
        List<Applicant>applicants=job.getApplicants();
        if(applicants==null)applicants=new ArrayList<>();
        if (applicants.stream().filter((x)->x.getApplicantId()==applicantDTO.getApplicantId()).toList().size()>0)throw new JobPortalException("JOB_APPLIED_ALREADY");
        applicantDTO.setApplicationStatus(ApplicationStatus.APPLIED);
        applicants.add(applicantDTO.toEntity());
        job.setApplicants(applicants);
        jobRepository.save(job);


    }

    @Override
    public List<JobDTO> getJobPostedBy(Long id) {
        return jobRepository.findByPostedBy(id).stream().map((x)->x.toDTO()).toList();
    }

    @Override
    public void changeAppStatus(Application application) throws JobPortalException {
        Job job=jobRepository.findById(application.getId()).orElseThrow(()-> new JobPortalException("JOB_NOT_FOUND"));
        List<Applicant>applicants=job.getApplicants().stream().map((x)->{
            if(application.getApplicantId()==x.getApplicantId()){
                x.setApplicationStatus(application.getApplicationStatus());
                if(application.getApplicationStatus().equals(ApplicationStatus.INTERVIEWING))

                {
                    x.setInterviewTime(application.getInterviewTime());
                    NotificationDTO notificationDTO=new NotificationDTO();
                    notificationDTO.setAction("INTERVIEW SCHEDULE");
                    notificationDTO.setMessage("INTERVIEW SCHEDULE FOR JOB ID : "+application.getId());
                    notificationDTO.setUserId(application.getApplicantId());
                    notificationDTO.setRoute("/job-history");
                    try {
                        notificationService.sendNotification(notificationDTO);
                    } catch (JobPortalException e) {
                        throw new RuntimeException(e);
                    }
                }



            }
            return x;

        }).toList();
        job.setApplicants(applicants);
        jobRepository.save(job);
    }
}
