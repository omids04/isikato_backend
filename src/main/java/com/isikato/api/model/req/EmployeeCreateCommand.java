package com.isikato.api.model.req;

import com.isikato.infrastructure.entities.Employee;
import com.isikato.infrastructure.entities.IsikatoFile;

import javax.validation.constraints.Email;

public record EmployeeCreateCommand(long id,
                                    String username,
                                    String password,
                                    String name,
                                    @Email String email,
                                    String phone,
				    IdCommand image,
                                    boolean enabled) {

    public EmployeeCreateCommand(String username, String password, String name, String email, String phone, IdCommand image, boolean enabled){
        this(0L, username, password, name, email, phone, image, enabled);
    }

    public Employee toEntity(){
        return Employee
                .builder()
                .id(id)
                .email(email)
                .name(name)
                .password(password)
                .phone(phone)
                .username(username)
                .enabled(enabled)
		.image(IsikatoFile.builder().id(image.id()).build())
                .build();
    }
}
