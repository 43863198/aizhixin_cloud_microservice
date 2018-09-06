package com.aizhixin.test.example;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class UserInfo {
    private long id;
    private String login;
    private String name;
    private String email;
    private String phoneNumber;
    private String avatar;
    private String password;
    private String groupType;
    private String shortName;
}
