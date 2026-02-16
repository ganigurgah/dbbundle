# DbBundle - Database-backed ResourceBundle Library

DbBundle, Spring Boot ve JSF uygulamalarında çoklu dil desteği için veritabanı tabanlı ResourceBundle yönetimi sağlayan bir Maven kütüphanesidir. Caffeine cache ile performans optimizasyonu sunar.

## Özellikler

- ✅ Veritabanı tabanlı çoklu dil desteği
- ✅ Spring Boot ve JSF uygulamaları ile tam uyumluluk
- ✅ `@ValoraBundle` anotasyonu ile kolay dependency injection
- ✅ JSF EL expressions desteği: `#{msg['key']}`
- ✅ Caffeine cache ile yüksek performans
- ✅ Runtime'da dinamik dil değiştirme
- ✅ Auto-configuration ile kolay kurulum

## Kurulum

### Maven Dependency

```xml
<dependency>
    <groupId>com.valora</groupId>
    <artifactId>dbbundle</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Veritabanı Yapılandırması

Uygulamanızda aşağıdaki alanlara sahip bir dil tablosu oluşturun:

```sql
CREATE TABLE messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    msg_key VARCHAR(255) NOT NULL,
    msg_value TEXT NOT NULL,
    locale_code VARCHAR(10) NOT NULL,
    UNIQUE KEY unique_key_locale (msg_key, locale_code)
);

-- Örnek veriler
INSERT INTO messages (msg_key, msg_value, locale_code) VALUES
('welcome.message', 'Hoş geldiniz', 'tr'),
('welcome.message', 'Welcome', 'en'),
('welcome.message', 'Bienvenue', 'fr'),
('goodbye.message', 'Güle güle', 'tr'),
('goodbye.message', 'Goodbye', 'en'),
('goodbye.message', 'Au revoir', 'fr');
```

## Uygulama Yapılandırması

### 1. Entity Sınıfı Oluşturma

```java
import com.valora.dbbundle.model.MessageEntity;
import javax.persistence.*;

@Entity
@Table(name = "messages")
public class Message implements MessageEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "msg_key", nullable = false)
    private String msgKey;
    
    @Column(name = "msg_value", nullable = false)
    private String msgValue;
    
    @Column(name = "locale_code", nullable = false)
    private String localeCode;
    
    // Getters
    @Override
    public String getMsgKey() {
        return msgKey;
    }
    
    @Override
    public String getMsgValue() {
        return msgValue;
    }
    
    @Override
    public String getLocaleCode() {
        return localeCode;
    }
    
    // Setters
    public void setMsgKey(String msgKey) {
        this.msgKey = msgKey;
    }
    
    public void setMsgValue(String msgValue) {
        this.msgValue = msgValue;
    }
    
    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }
}
```

### 2. JPA Repository Oluşturma

```java
import com.valora.dbbundle.model.MessageEntity;
import com.valora.dbbundle.repository.MessageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageJpaRepository extends JpaRepository<Message, Long>, MessageRepository {
    
    @Override
    List<Message> findByLocaleCode(String localeCode);
    
    @Override
    Message findByMsgKeyAndLocaleCode(String msgKey, String localeCode);
}
```

### 3. application.properties/yml Yapılandırması

```properties
# Varsayılan dil
dbbundle.default-locale=tr

# Cache ayarları
dbbundle.cache.max-size=100
dbbundle.cache.expire-minutes=60

# Veritabanı bağlantısı
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
```

## Kullanım

### Spring Boot'ta @ValoraBundle ile Kullanım

```java
import com.valora.dbbundle.annotation.ValoraBundle;
import org.springframework.stereotype.Service;
import java.util.ResourceBundle;

@Service
public class MyService {
    
    @ValoraBundle
    private ResourceBundle bundle;
    
    public void printWelcomeMessage() {
        String message = bundle.getString("welcome.message");
        System.out.println(message);
    }
}
```

### DbBundleService ile Kullanım

```java
import com.valora.dbbundle.service.DbBundleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class LanguageController {
    
    @Autowired
    private DbBundleService dbBundleService;
    
    public void changeLanguage(String localeCode) {
        // Dili değiştir (tr, en, fr, vb.)
        dbBundleService.changeLocale(localeCode);
    }
    
    public String getMessage(String key) {
        return dbBundleService.getMessage(key);
    }
    
    public void clearCache() {
        dbBundleService.clearCache();
    }
}
```

### JSF XHTML'de Kullanım

```xhtml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core">
<h:head>
    <title>DbBundle Example</title>
</h:head>
<h:body>
    <h1>
        <h:outputText value="#{msg['welcome.message']}" />
    </h1>
    
    <h:outputLabel value="#{msg['goodbye.message']}" />
    
    <!-- Dil değiştirme butonları -->
    <h:form>
        <h:commandButton value="Türkçe" 
                         action="#{languageBean.changeLocale('tr')}" />
        <h:commandButton value="English" 
                         action="#{languageBean.changeLocale('en')}" />
        <h:commandButton value="Français" 
                         action="#{languageBean.changeLocale('fr')}" />
    </h:form>
</h:body>
</html>
```

### JSF Managed Bean

```java
import com.valora.dbbundle.service.DbBundleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@Component
@ManagedBean
@SessionScoped
public class LanguageBean {
    
    @Autowired
    private DbBundleService dbBundleService;
    
    public void changeLocale(String localeCode) {
        dbBundleService.changeLocale(localeCode);
    }
    
    public String getCurrentLocale() {
        return dbBundleService.getCurrentLocale().getLanguage();
    }
}
```

## Runtime'da Dil Değiştirme

```java
// Dil koduna göre değiştirme
dbBundleService.changeLocale("tr");  // Türkçe
dbBundleService.changeLocale("en");  // İngilizce
dbBundleService.changeLocale("fr");  // Fransızca

// Locale nesnesi ile değiştirme
Locale turkishLocale = new Locale("tr");
dbBundleService.changeLocale(turkishLocale);
```

## Cache Yönetimi

```java
// Tüm cache'i temizle
dbBundleService.clearCache();

// Belirli bir dil için cache'i temizle
dbBundleService.clearCache("tr");

// Cache istatistiklerini görüntüle
String stats = dbBundleService.getCacheStats();
System.out.println(stats);

// Cache ayarlarını değiştir
dbBundleService.configureCache(200, 120); // maxSize: 200, expireMinutes: 120
```

## Gelişmiş Yapılandırma

### Özel Cache Ayarları

```yaml
dbbundle:
  default-locale: tr
  cache:
    max-size: 200
    expire-minutes: 120
```

### JSF faces-config.xml

Eğer auto-configuration çalışmazsa, manuel olarak EL Resolver'ı ekleyebilirsiniz:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<faces-config xmlns="http://xmlns.jcp.org/xml/ns/javaee"
              version="2.3">
    <application>
        <el-resolver>com.valora.dbbundle.el.JsfMessageELResolver</el-resolver>
    </application>
</faces-config>
```

## API Referansı

### DbBundleService

| Method | Açıklama |
|--------|----------|
| `getBundle()` | Mevcut dil için ResourceBundle döndürür |
| `getBundle(Locale locale)` | Belirtilen dil için ResourceBundle döndürür |
| `getMessage(String key)` | Mevcut dil için mesaj değerini döndürür |
| `getMessage(String key, Locale locale)` | Belirtilen dil için mesaj değerini döndürür |
| `changeLocale(String localeCode)` | Dil koduna göre dili değiştirir |
| `changeLocale(Locale locale)` | Locale nesnesine göre dili değiştirir |
| `getCurrentLocale()` | Mevcut Locale'i döndürür |
| `clearCache()` | Tüm cache'i temizler |
| `clearCache(String localeCode)` | Belirtilen dil için cache'i temizler |
| `configurCache(long maxSize, long expireMinutes)` | Cache ayarlarını değiştirir |
| `getCacheStats()` | Cache istatistiklerini döndürür |

## Projeyi Build Etme

```bash
mvn clean install
```

Build edilen JAR dosyası `target/dbbundle-1.0.0.jar` konumunda oluşturulacaktır.

## Gereksinimler

- Java 11+
- Spring Boot 2.7+
- Maven 3.6+
- JPA destekli veritabanı (MySQL, PostgreSQL, H2, vb.)

## Lisans

Bu proje MIT lisansı altında lisanslanmıştır.

## 🏫 Diğer Kaynaklar
📚[ChangeLog](CHANGELOG.md)<br>
📚[Installation](INSTALLATIOIN.md)<br>
📚[Examples](EXAMPLES.md)<br>
📚[JSF Integration](JSF-INTEGRATIOIN.md)<br>
📚[Troubleshooting](TROUBLESHOOTING.md)<br>


## İletişim

Sorularınız için issue açabilirsiniz.
