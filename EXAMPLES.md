# DbBundle Kullanım Örnekleri

## Örnek 1: Basit Spring Boot Uygulaması

### 1. Entity ve Repository

```java
// Message.java
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
    
    // Getters/Setters...
}

// MessageJpaRepository.java
@Repository
public interface MessageJpaRepository extends JpaRepository<Message, Long>, MessageRepository {
    List<Message> findByLocaleCode(String localeCode);
    Message findByMsgKeyAndLocaleCode(String msgKey, String localeCode);
}
```

### 2. REST Controller

```java
@RestController
@RequestMapping("/api")
public class MessageController {
    
    @Autowired
    private DbBundleService dbBundleService;
    
    @GetMapping("/message/{key}")
    public String getMessage(@PathVariable String key) {
        return dbBundleService.getMessage(key);
    }
    
    @PostMapping("/locale/{code}")
    public ResponseEntity<String> changeLocale(@PathVariable String code) {
        dbBundleService.changeLocale(code);
        return ResponseEntity.ok("Locale changed to: " + code);
    }
    
    @GetMapping("/cache/stats")
    public String getCacheStats() {
        return dbBundleService.getCacheStats();
    }
}
```

## Örnek 2: JSF Uygulaması

### 1. JSF Managed Bean

```java
@Component
@ManagedBean(name = "languageBean")
@SessionScoped
public class LanguageBean implements Serializable {
    
    @Autowired
    private DbBundleService dbBundleService;
    
    private String selectedLocale = "tr";
    
    public void changeLanguage() {
        dbBundleService.changeLocale(selectedLocale);
        FacesContext.getCurrentInstance().getViewRoot()
            .setLocale(new Locale(selectedLocale));
    }
    
    public String getSelectedLocale() {
        return selectedLocale;
    }
    
    public void setSelectedLocale(String selectedLocale) {
        this.selectedLocale = selectedLocale;
    }
}
```

### 2. XHTML Sayfası

```xhtml
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core">
<h:head>
    <title>#{msg['app.title']}</title>
</h:head>
<h:body>
    <h:form>
        <h1><h:outputText value="#{msg['welcome.message']}" /></h1>
        
        <h:panelGrid columns="2">
            <h:outputLabel value="#{msg['select.language']}: " />
            <h:selectOneMenu value="#{languageBean.selectedLocale}">
                <f:selectItem itemValue="tr" itemLabel="Türkçe" />
                <f:selectItem itemValue="en" itemLabel="English" />
                <f:selectItem itemValue="fr" itemLabel="Français" />
                <f:ajax listener="#{languageBean.changeLanguage}" 
                        render="@form" />
            </h:selectOneMenu>
        </h:panelGrid>
        
        <p><h:outputText value="#{msg['current.message']}" /></p>
    </h:form>
</h:body>
</html>
```

## Örnek 3: Servis Katmanında Kullanım

```java
@Service
public class NotificationService {
    
    @ValoraBundle
    private ResourceBundle bundle;
    
    @Autowired
    private DbBundleService dbBundleService;
    
    public void sendWelcomeEmail(User user) {
        // Kullanıcının diline göre mesaj al
        Locale userLocale = new Locale(user.getLanguage());
        String subject = dbBundleService.getMessage("email.welcome.subject", userLocale);
        String body = dbBundleService.getMessage("email.welcome.body", userLocale);
        
        // Email gönder
        emailService.send(user.getEmail(), subject, body);
    }
    
    public String getLocalizedMessage(String key) {
        return bundle.getString(key);
    }
}
```

## Örnek 4: Interceptor ile Otomatik Dil Değiştirme

```java
@Component
public class LocaleInterceptor implements HandlerInterceptor {
    
    @Autowired
    private DbBundleService dbBundleService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) {
        String locale = request.getParameter("lang");
        
        if (locale != null && !locale.isEmpty()) {
            dbBundleService.changeLocale(locale);
            HttpSession session = request.getSession();
            session.setAttribute("currentLocale", locale);
        } else {
            HttpSession session = request.getSession();
            String sessionLocale = (String) session.getAttribute("currentLocale");
            if (sessionLocale != null) {
                dbBundleService.changeLocale(sessionLocale);
            }
        }
        
        return true;
    }
}

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private LocaleInterceptor localeInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeInterceptor);
    }
}
```

## Örnek 5: Cache Yönetimi

```java
@Service
public class CacheManagementService {
    
    @Autowired
    private DbBundleService dbBundleService;
    
    @Scheduled(cron = "0 0 2 * * ?") // Her gece saat 02:00
    public void clearCacheNightly() {
        dbBundleService.clearCache();
        log.info("Message cache cleared at: {}", LocalDateTime.now());
    }
    
    public void refreshMessages(String localeCode) {
        // Belirli bir dil için cache'i temizle
        dbBundleService.clearCache(localeCode);
        
        // Yeni verileri yükle
        dbBundleService.getBundle(new Locale(localeCode));
        
        log.info("Messages refreshed for locale: {}", localeCode);
    }
    
    public Map<String, Object> getCacheInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("stats", dbBundleService.getCacheStats());
        info.put("currentLocale", dbBundleService.getCurrentLocale().getLanguage());
        return info;
    }
}
```

## Örnek 6: Admin Panel - Mesaj Yönetimi

```java
@RestController
@RequestMapping("/admin/messages")
public class MessageAdminController {
    
    @Autowired
    private MessageJpaRepository messageRepository;
    
    @Autowired
    private DbBundleService dbBundleService;
    
    @PostMapping
    public Message createMessage(@RequestBody Message message) {
        Message saved = messageRepository.save(message);
        
        // Cache'i güncelle
        dbBundleService.clearCache(message.getLocaleCode());
        
        return saved;
    }
    
    @PutMapping("/{id}")
    public Message updateMessage(@PathVariable Long id, @RequestBody Message message) {
        Message existing = messageRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
        
        existing.setMsgValue(message.getMsgValue());
        Message updated = messageRepository.save(existing);
        
        // Cache'i güncelle
        dbBundleService.clearCache(existing.getLocaleCode());
        
        return updated;
    }
    
    @DeleteMapping("/{id}")
    public void deleteMessage(@PathVariable Long id) {
        Message message = messageRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
        
        String locale = message.getLocaleCode();
        messageRepository.deleteById(id);
        
        // Cache'i güncelle
        dbBundleService.clearCache(locale);
    }
}
```

## Örnek 7: Çoklu Tenant Desteği

```java
@Service
public class MultiTenantMessageService {
    
    @Autowired
    private DbBundleService dbBundleService;
    
    private ThreadLocal<String> currentTenant = new ThreadLocal<>();
    
    public void setCurrentTenant(String tenantId) {
        currentTenant.set(tenantId);
    }
    
    public String getMessage(String key, String locale) {
        String tenantId = currentTenant.get();
        String compositeKey = tenantId + "." + key;
        
        return dbBundleService.getMessage(compositeKey, new Locale(locale));
    }
}
```

## Veritabanı Örnek Verileri

```sql
-- Türkçe mesajlar
INSERT INTO messages (msg_key, msg_value, locale_code) VALUES
('app.title', 'Uygulama Başlığı', 'tr'),
('welcome.message', 'Hoş geldiniz!', 'tr'),
('goodbye.message', 'Güle güle!', 'tr'),
('error.not.found', 'Kayıt bulunamadı', 'tr'),
('success.save', 'Başarıyla kaydedildi', 'tr');

-- İngilizce mesajlar
INSERT INTO messages (msg_key, msg_value, locale_code) VALUES
('app.title', 'Application Title', 'en'),
('welcome.message', 'Welcome!', 'en'),
('goodbye.message', 'Goodbye!', 'en'),
('error.not.found', 'Record not found', 'en'),
('success.save', 'Saved successfully', 'en');

-- Fransızca mesajlar
INSERT INTO messages (msg_key, msg_value, locale_code) VALUES
('app.title', 'Titre de l\'application', 'fr'),
('welcome.message', 'Bienvenue!', 'fr'),
('goodbye.message', 'Au revoir!', 'fr'),
('error.not.found', 'Enregistrement introuvable', 'fr'),
('success.save', 'Enregistré avec succès', 'fr');
```
