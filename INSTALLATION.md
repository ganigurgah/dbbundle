# DbBundle Kurulum ve Yapılandırma Rehberi

## Adım 1: Kütüphaneyi Projenize Ekleyin

### Maven ile Kurulum

1. DbBundle JAR dosyasını local Maven repository'nize yükleyin:

```bash
mvn install:install-file \
  -Dfile=dbbundle-1.0.0.jar \
  -DgroupId=com.valora \
  -DartifactId=dbbundle \
  -Dversion=1.0.0 \
  -Dpackaging=jar
```

2. Projenizin `pom.xml` dosyasına dependency ekleyin:

```xml
<dependency>
    <groupId>com.valora</groupId>
    <artifactId>dbbundle</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Adım 2: Veritabanı Tablosunu Oluşturun

### MySQL

```sql
CREATE TABLE messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    msg_key VARCHAR(255) NOT NULL,
    msg_value TEXT NOT NULL,
    locale_code VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_key_locale (msg_key, locale_code),
    INDEX idx_locale (locale_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### PostgreSQL

```sql
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    msg_key VARCHAR(255) NOT NULL,
    msg_value TEXT NOT NULL,
    locale_code VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (msg_key, locale_code)
);

CREATE INDEX idx_locale ON messages(locale_code);
```

### H2 (Test için)

```sql
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    msg_key VARCHAR(255) NOT NULL,
    msg_value TEXT NOT NULL,
    locale_code VARCHAR(10) NOT NULL,
    UNIQUE (msg_key, locale_code)
);
```

## Adım 3: Entity Sınıfını Oluşturun

`src/main/java/com/yourpackage/entity/Message.java`:

```java
package com.yourpackage.entity;

import com.valora.dbbundle.model.MessageEntity;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "messages", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"msg_key", "locale_code"}))
public class Message implements MessageEntity, Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "msg_key", nullable = false, length = 255)
    private String msgKey;
    
    @Column(name = "msg_value", nullable = false, columnDefinition = "TEXT")
    private String msgValue;
    
    @Column(name = "locale_code", nullable = false, length = 10)
    private String localeCode;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

## Adım 4: Repository Interface Oluşturun

`src/main/java/com/yourpackage/repository/MessageJpaRepository.java`:

```java
package com.yourpackage.repository;

import com.valora.dbbundle.model.MessageEntity;
import com.valora.dbbundle.repository.MessageRepository;
import com.yourpackage.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageJpaRepository extends JpaRepository<Message, Long>, MessageRepository {
    
    @Override
    @Query("SELECT m FROM Message m WHERE m.localeCode = :localeCode")
    List<Message> findByLocaleCode(@Param("localeCode") String localeCode);
    
    @Override
    @Query("SELECT m FROM Message m WHERE m.msgKey = :msgKey AND m.localeCode = :localeCode")
    Message findByMsgKeyAndLocaleCode(@Param("msgKey") String msgKey, 
                                      @Param("localeCode") String localeCode);
}
```

## Adım 5: Application Properties Yapılandırması

`src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/yourdb?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# DbBundle Configuration
dbbundle.default-locale=tr
dbbundle.cache.max-size=100
dbbundle.cache.expire-minutes=60

# Logging
logging.level.com.valora.dbbundle=DEBUG
```

VEYA `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/yourdb?useSSL=false&serverTimezone=UTC
    username: root
    password: yourpassword
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect

dbbundle:
  default-locale: tr
  cache:
    max-size: 100
    expire-minutes: 60

logging:
  level:
    com.valora.dbbundle: DEBUG
```

## Adım 6: JSF Uygulamaları İçin Ek Yapılandırma

### 6.1 faces-config.xml

`src/main/webapp/WEB-INF/faces-config.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<faces-config xmlns="http://xmlns.jcp.org/xml/ns/javaee"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                                  http://xmlns.jcp.org/xml/ns/javaee/web-facesconfig_2_3.xsd"
              version="2.3">
    
    <application>
        <el-resolver>com.valora.dbbundle.el.MessageELResolver</el-resolver>
        
        <locale-config>
            <default-locale>tr</default-locale>
            <supported-locale>en</supported-locale>
            <supported-locale>fr</supported-locale>
        </locale-config>
    </application>
    
</faces-config>
```

### 6.2 web.xml (JSF için)

`src/main/webapp/WEB-INF/web.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                             http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">
    
    <display-name>DbBundle JSF Application</display-name>
    
    <!-- JSF Servlet -->
    <servlet>
        <servlet-name>Faces Servlet</servlet-name>
        <servlet-class>javax.faces.webapp.FacesServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
    </servlet>
    
    <servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>*.xhtml</url-pattern>
    </servlet-mapping>
    
    <!-- Welcome File -->
    <welcome-file-list>
        <welcome-file>index.xhtml</welcome-file>
    </welcome-file-list>
    
</web-app>
```

## Adım 7: İlk Verileri Ekleyin

### SQL Script ile

`src/main/resources/data.sql`:

```sql
-- Türkçe
INSERT INTO messages (msg_key, msg_value, locale_code) VALUES
('app.title', 'Benim Uygulamam', 'tr'),
('welcome.message', 'Hoş geldiniz!', 'tr'),
('login.title', 'Giriş Yap', 'tr'),
('login.username', 'Kullanıcı Adı', 'tr'),
('login.password', 'Şifre', 'tr'),
('button.submit', 'Gönder', 'tr'),
('button.cancel', 'İptal', 'tr');

-- English
INSERT INTO messages (msg_key, msg_value, locale_code) VALUES
('app.title', 'My Application', 'en'),
('welcome.message', 'Welcome!', 'en'),
('login.title', 'Login', 'en'),
('login.username', 'Username', 'en'),
('login.password', 'Password', 'en'),
('button.submit', 'Submit', 'en'),
('button.cancel', 'Cancel', 'en');

-- Français
INSERT INTO messages (msg_key, msg_value, locale_code) VALUES
('app.title', 'Mon Application', 'fr'),
('welcome.message', 'Bienvenue!', 'fr'),
('login.title', 'Connexion', 'fr'),
('login.username', 'Nom d\'utilisateur', 'fr'),
('login.password', 'Mot de passe', 'fr'),
('button.submit', 'Soumettre', 'fr'),
('button.cancel', 'Annuler', 'fr');
```

### Java ile (CommandLineRunner)

```java
@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private MessageJpaRepository messageRepository;
    
    @Override
    public void run(String... args) {
        if (messageRepository.count() == 0) {
            initializeMessages();
        }
    }
    
    private void initializeMessages() {
        // Türkçe mesajlar
        createMessage("app.title", "Benim Uygulamam", "tr");
        createMessage("welcome.message", "Hoş geldiniz!", "tr");
        
        // English messages
        createMessage("app.title", "My Application", "en");
        createMessage("welcome.message", "Welcome!", "en");
        
        // Messages françaises
        createMessage("app.title", "Mon Application", "fr");
        createMessage("welcome.message", "Bienvenue!", "fr");
    }
    
    private void createMessage(String key, String value, String locale) {
        Message message = new Message();
        message.setMsgKey(key);
        message.setMsgValue(value);
        message.setLocaleCode(locale);
        messageRepository.save(message);
    }
}
```

## Adım 8: Uygulamayı Başlatın ve Test Edin

```bash
mvn spring-boot:run
```

### Test REST Endpoint

```java
@RestController
@RequestMapping("/test")
public class TestController {
    
    @Autowired
    private DbBundleService dbBundleService;
    
    @GetMapping("/message/{key}")
    public String testMessage(@PathVariable String key) {
        return dbBundleService.getMessage(key);
    }
    
    @GetMapping("/locale/{code}")
    public String changeLocale(@PathVariable String code) {
        dbBundleService.changeLocale(code);
        return "Locale changed to: " + code;
    }
}
```

Test URL'leri:
- http://localhost:8080/test/message/welcome.message
- http://localhost:8080/test/locale/en
- http://localhost:8080/test/message/welcome.message

## Sorun Giderme

### 1. Bean bulunamıyor hatası

Eğer `MessageRepository` bean'i bulunamazsa, Main Application sınıfınıza:

```java
@SpringBootApplication
@EntityScan("com.yourpackage.entity")
@EnableJpaRepositories("com.yourpackage.repository")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 2. JSF'de #{msg} çalışmıyor

`faces-config.xml` dosyasının doğru konumda olduğundan emin olun ve EL Resolver'ın eklendiğini kontrol edin.

### 3. Cache çalışmıyor

Loglarda cache istatistiklerini kontrol edin:

```java
logger.info("Cache stats: {}", dbBundleService.getCacheStats());
```

## Tamamlandı!

Artık DbBundle kütüphaneniz kullanıma hazır. Detaylı kullanım örnekleri için `EXAMPLES.md` dosyasına bakın.
