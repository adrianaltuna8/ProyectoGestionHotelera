package com.GestionHotelera.Administracion.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;


public class ThymeleafConfig {
//    @Bean
  /* */  public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }
}
