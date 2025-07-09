package com.jobportal.entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import com.jobportal.dto.AccountType;
import com.jobportal.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id 

    private Long id;
    private String name;
    @Indexed(unique = true)
    private String email;
    private String mobile;
    private String password;
    private AccountType accountType;
    private Long profileId;
    public UserDTO toDTO() {
        return new UserDTO(this.id, this.name, this.email,this.mobile, this.password, this.accountType,this.profileId);
    }
}
