package com.aizhixin.test.task;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@ToString
public class CurrentUserDTO {
    @Getter @Setter private Long id;
    @Getter @Setter private String login;
    @Getter @Setter private String userName;
    @Getter @Setter private String avatar;
    @Getter @Setter private String phoneNumber;
    @Getter @Setter private String email;
    @Getter @Setter private String roleGroup;
    @Getter @Setter private List<String> roles;
}
