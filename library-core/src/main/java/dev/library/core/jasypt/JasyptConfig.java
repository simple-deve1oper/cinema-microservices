package dev.library.core.jasypt;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(name = "EncryptedProperties", value = "classpath:encrypted.properties")
public class JasyptConfig {}