package br.com.fiap3espv.AutoEscola3ESPV.config;

import org.springframework.context.annotation.*;
import java.time.*;
@Configuration
public class TimeConfig {
    @Bean public Clock clock() { return Clock.system(ZoneId.of("America/Sao_Paulo")); }
}
