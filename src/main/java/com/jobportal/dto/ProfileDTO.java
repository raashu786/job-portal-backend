package com.jobportal.dto;
import com.jobportal.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {

    private long id;
    private String name;
    private String email;
    private String mobile;
    private String jobTitle;
    private String company;
    private String location ;
    private String about;
    private String picture;
    private Long totalExp;
    private List<String> skills;
    private List<Experiance> experiances;
    private List<Certification> certification;
    private List<Long>savedJobs;
    public Profile toEntity(){
        return new Profile(this.id,this.name,this.email,this.mobile,this.jobTitle,this.company,this.location,this.about,this.picture!=null? Base64.getDecoder().decode(this.picture):null,this.totalExp, this.skills,this.experiances,this.certification,this.savedJobs);
    }


}
