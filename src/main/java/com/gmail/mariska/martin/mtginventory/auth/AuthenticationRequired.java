package com.gmail.mariska.martin.mtginventory.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;

@NameBinding
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface AuthenticationRequired {

}
