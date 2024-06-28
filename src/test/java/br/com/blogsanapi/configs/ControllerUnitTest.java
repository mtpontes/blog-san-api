package br.com.blogsanapi.configs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.context.annotation.Import;

import br.com.blogsanapi.infra.security.SecurityConfigurations;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AutoConfigureJsonTesters
@Import(SecurityConfigurations.class)
public @interface ControllerUnitTest {}