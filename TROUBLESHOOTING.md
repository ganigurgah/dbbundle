# DbBundle - Sorun Giderme Kılavuzu

## ❌ Hata: "No qualifying bean of type 'DbBundleService'"

### Sorun
```
Error creating bean with name 'messageController': Unsatisfied dependency expressed through field 'dbBundleService': 
No qualifying bean of type 'com.valora.dbbundle.service.DbBundleService' available
```

### Çözümler

#### 1. Auto-Configuration Kontrolü

Spring Boot 3.x+ kullanıyorsanız, kütüphanenin doğru auto-configuration dosyasına sahip olduğundan emin olun:

**Kontrol:**
```bash
# JAR içeriğini kontrol edin
jar -tf dbbundle-1.0.0.jar | grep AutoConfiguration.imports
```

**Olması gereken:**
```
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

#### 2. MessageRepository Bean'i Eksik

DbBundleService, MessageRepository'ye ihtiyaç duyar. Uygulamanızda şunları oluşturduğunuzdan emin olun:

**Entity:**
```java
@Entity
@Table(name = "messages")
public class Message implements MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "msg_key")
    private String msgKey;
    
    @Column(name = "msg_value")
    private String msgValue;
    
    @Column(name = "locale_code")
    private String localeCode;
    
    // Getter metodlarını implement edin
    @Override
    public String getMsgKey() { return msgKey; }
    
    @Override
    public String getMsgValue() { return msgValue; }
    
    @Override
    public String getLocaleCode() { return localeCode; }
    
    // Setters...
}
```

**Repository:**
```java
@Repository
public interface MessageJpaRepository extends JpaRepository<Message, Long>, MessageRepository {
    
    @Override
    List<Message> findByLocaleCode(String localeCode);
    
    @Override
    Message findByMsgKeyAndLocaleCode(String msgKey, String localeCode);
}
```

#### 3. Component Scan Sorunu

Main Application sınıfınıza annotation ekleyin:

```java
@SpringBootApplication
@EntityScan(basePackages = {"com.yourpackage.entity", "com.valora.dbbundle.model"})
@EnableJpaRepositories(basePackages = {"com.yourpackage.repository", "com.valora.dbbundle.repository"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

#### 4. Manuel Bean Tanımlama (Geçici Çözüm)

Eğer auto-configuration çalışmazsa, manuel olarak bean tanımlayabilirsiniz:

```java
@Configuration
public class DbBundleManualConfig {
    
    @Bean
    public DbBundleService dbBundleService(MessageRepository messageRepository) {
        DbBundleService service = new DbBundleService(messageRepository);
        service.init();
        return service;
    }
    
    @Bean
    public ValoraBundleAnnotationProcessor valoraBundleAnnotationProcessor(DbBundleService service) {
        return new ValoraBundleAnnotationProcessor(service);
    }
}
```

#### 5. Dependency Kontrolü

pom.xml'de dependency doğru eklenmiş mi?

```xml
<dependency>
    <groupId>com.valora</groupId>
    <artifactId>dbbundle</artifactId>
    <version>1.0.0</version>
</dependency>
```

Maven'i yeniden build edin:
```bash
mvn clean install
```

## ❌ Hata: "Table 'messages' doesn't exist"

### Çözüm 1: DDL Auto Mode
```properties
spring.jpa.hibernate.ddl-auto=update
```

### Çözüm 2: Manuel Tablo Oluşturma
```sql
CREATE TABLE messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    msg_key VARCHAR(255) NOT NULL,
    msg_value TEXT NOT NULL,
    locale_code VARCHAR(10) NOT NULL,
    UNIQUE KEY unique_key_locale (msg_key, locale_code)
);
```

## ❌ JSF #{msg} Çalışmıyor

### Sorun
JSF sayfalarında `#{msg['key']}` çalışmıyor veya `???key???` gösteriyor.

### Çözüm 1: faces-config.xml'de Doğru Resolver'ı Kullanın

`src/main/webapp/WEB-INF/faces-config.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<faces-config xmlns="http://xmlns.jcp.org/xml/ns/javaee"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                                  http://xmlns.jcp.org/xml/ns/javaee/web-facesconfig_2_3.xsd"
              version="2.3">
    
    <application>
        <!-- ÖNEMLİ: JsfMessageELResolver kullanın (JDK 24 için) -->
        <el-resolver>com.valora.dbbundle.el.JsfMessageELResolver</el-resolver>
    </application>
    
</faces-config>
```

### Çözüm 2: DbBundleService Bean Kontrol

Uygulama başlatıldığında log'larda şunu arayın:

```
INFO : Creating DbBundleService bean
INFO : DbBundleService configured for JSF EL Resolver
```

Eğer görmüyorsanız, MessageRepository bean'i eksik olabilir.

### ⚠️ JDK 24 ve Jakarta EL Uyumsuzluğu

**v1.0.0-fixed** sürümünü kullandığınızdan emin olun:
- `JsfMessageELResolver` → JSF için (`javax.el.ELResolver`)
- `MessageELResolver` → Jakarta EL için (`jakarta.el.ELResolver`)

faces-config.xml'de **JsfMessageELResolver** kullanmalısınız.

Detaylı JSF entegrasyonu için `JSF-INTEGRATION.md` dosyasına bakın.

## ❌ Hata: "application.addELResolver" Type Mismatch (JDK 24)

### Sorun
```
Type mismatch: cannot convert from jakarta.el.ELResolver to javax.el.ELResolver
```

### Çözüm
DbBundle v1.0.0-fixed sürümünde bu sorun çözülmüştür:

1. **JsfMessageELResolver** (`javax.el`) - JSF uygulamaları için
2. **MessageELResolver** (`jakarta.el`) - Modern Jakarta EE için

faces-config.xml'de doğru sınıfı kullanın:
```xml
<el-resolver>com.valora.dbbundle.el.JsfMessageELResolver</el-resolver>
```
```

## ❌ Cache Çalışmıyor

### Debug Mode Açın
```properties
logging.level.com.valora.dbbundle=DEBUG
```

### Cache İstatistiklerini Kontrol Edin
```java
@Autowired
private DbBundleService dbBundleService;

public void checkCache() {
    String stats = dbBundleService.getCacheStats();
    System.out.println("Cache Stats: " + stats);
}
```

## ❌ @ValoraBundle Injection Çalışmıyor

### Kontrol Listesi:

1. **Sınıf Spring Bean mi?**
```java
@Service // veya @Component, @Controller
public class MyService {
    @ValoraBundle
    private ResourceBundle bundle;
}
```

2. **Field'ın tipi doğru mu?**
```java
@ValoraBundle
private ResourceBundle bundle; // ✅ Doğru

@ValoraBundle
private DbBundleService bundle; // ❌ Yanlış
```

3. **ValoraBundleAnnotationProcessor Bean'i var mı?**
```bash
# Application başlatırken log'larda şunu arayın:
"Creating ValoraBundleAnnotationProcessor bean"
```

## ❌ Locale Değişmiyor

### Runtime Locale Değiştirme

```java
@Autowired
private DbBundleService dbBundleService;

public void changeLanguage(String localeCode) {
    // Önce locale'i değiştir
    dbBundleService.changeLocale(localeCode);
    
    // Yeni ResourceBundle'ı al
    ResourceBundle bundle = dbBundleService.getBundle();
    
    // Test et
    String message = bundle.getString("test.key");
    System.out.println(message);
}
```

## ❌ "???" Mesajları Görünüyor

Bu, message key'in veritabanında bulunamadığı anlamına gelir.

### Kontrol:
```sql
SELECT * FROM messages WHERE msg_key = 'your.key' AND locale_code = 'tr';
```

### Test:
```java
String msg = dbBundleService.getMessage("test.key");
// Eğer "???test.key???" dönerse, key veritabanında yok
```

## 🔍 Debug Checklist

1. ✅ DbBundle dependency pom.xml'de var mı?
2. ✅ Maven clean install çalıştırıldı mı?
3. ✅ Message entity MessageEntity interface'ini implement ediyor mu?
4. ✅ MessageRepository interface'i extend ediliyor mu?
5. ✅ Veritabanı tablosu oluşturuldu mu?
6. ✅ Test verileri eklendi mi?
7. ✅ application.properties doğru yapılandırıldı mı?
8. ✅ Spring Boot auto-configuration çalışıyor mu?

## 📝 Detaylı Log Açma

```properties
# application.properties
logging.level.com.valora.dbbundle=DEBUG
logging.level.org.springframework.boot.autoconfigure=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

## 💡 Hala Çözülmedi mi?

1. **Maven Clean Install**
```bash
cd dbbundle
mvn clean install -U
```

2. **IDE'yi Restart Edin**

3. **Target Klasörünü Silin**
```bash
cd your-project
mvn clean
```

4. **Manuel Configuration Deneyin** (yukarıdaki Çözüm 4'e bakın)

## 📞 Destek

Sorununuz devam ediyorsa:
1. Hata mesajının tam logunu alın
2. application.properties dosyanızı kontrol edin
3. Kullandığınız Spring Boot versiyonunu belirtin
4. Entity ve Repository kodlarınızı gözden geçirin
