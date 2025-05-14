package com.jobportal.entity;
import com.jobportal.dto.Certification;
import com.jobportal.dto.Experiance;
import com.jobportal.dto.ProfileDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Base64;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "profiles")
public class Profile {
    @Id
    private long id;
    private String name;
    private String email;
    private String mobile;
    private String jobTitle;
    private String company;
    private String location ;
    private String about;
    private byte[] picture;
    private Long totalExp;
    private List<String> skills;
    private List<Experiance> experiances;
    private List<Certification> certification;
    private List<Long>savedJobs;

    public ProfileDTO toDTO(){
        return new ProfileDTO(this.id,this.name ,this.email,this.mobile,this.jobTitle,this.company,this.location,this.about,this.picture!=null?Base64.getEncoder().encodeToString(this.picture):null, this.totalExp, this.skills,this.experiances,this.certification,this.savedJobs);
    }
}
