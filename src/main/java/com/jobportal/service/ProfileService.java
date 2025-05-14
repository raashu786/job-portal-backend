package com.jobportal.service;

import com.jobportal.dto.ProfileDTO;
import com.jobportal.entity.Profile;
import com.jobportal.exception.JobPortalException;

import java.util.List;

public interface ProfileService {
    public Long createProfile(String email , String mobile) throws JobPortalException;
    public ProfileDTO getProfile(Long id) throws JobPortalException;
    public ProfileDTO updateProfile(ProfileDTO profileDTO) throws JobPortalException;

    public List<ProfileDTO> getAllProfile() throws JobPortalException;;
}
