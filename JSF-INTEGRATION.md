# DbBundle - JSF Entegrasyonu

## JSF Uygulamalarında Kullanım

DbBundle, JSF uygulamalarında `#{msg['key']}` EL expression'ları ile kullanılabilir.

### 🔧 Yapılandırma

#### 1. faces-config.xml

`src/main/webapp/WEB-INF/faces-config.xml` dosyasına EL Resolver'ı ekleyin:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<faces-config xmlns="http://xmlns.jcp.org/xml/ns/javaee"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                                  http://xmlns.jcp.org/xml/ns/javaee/web-facesconfig_2_3.xsd"
              version="2.3">
    
    <application>
        <!-- DbBundle EL Resolver -->
        <el-resolver>com.valora.dbbundle.el.JsfMessageELResolver</el-resolver>
        
        <locale-config>
            <default-locale>tr</default-locale>
            <supported-locale>en</supported-locale>
            <supported-locale>fr</supported-locale>
        </locale-config>
    </application>
    
</faces-config>
```

#### 2. Spring Configuration

Spring Boot application'ınız otomatik olarak DbBundleService'i JSF EL Resolver'a bağlar. Herhangi bir ek konfigürasyon gerekmez.

### 📝 XHTML Kullanımı

```xhtml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core"
      xmlns:ui="http://xmlns.jcp.org/jsf/facelets">
<h:head>
    <title>#{msg['app.title']}</title>
</h:head>
<h:body>
    <h1>
        <h:outputText value="#{msg['welcome.message']}" />
    </h1>
    
    <h:form>
        <h:panelGrid columns="2">
            <h:outputLabel value="#{msg['label.username']}: " />
            <h:inputText value="#{loginBean.username}" />
            
            <h:outputLabel value="#{msg['label.password']}: " />
            <h:inputSecret value="#{loginBean.password}" />
        </h:panelGrid>
        
        <h:commandButton value="#{msg['button.login']}" 
                         action="#{loginBean.doLogin}" />
    </h:form>
    
    <!-- Dil değiştirme -->
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

### 🎯 JSF Managed Bean

```java
import com.valora.dbbundle.service.DbBundleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Locale;

@Component
@ManagedBean(name = "languageBean")
@SessionScoped
public class LanguageBean implements Serializable {
    
    @Autowired
    private DbBundleService dbBundleService;
    
    private String selectedLocale = "tr";
    
    public void changeLocale(String localeCode) {
        // DbBundle locale'ini değiştir
        dbBundleService.changeLocale(localeCode);
        
        // JSF ViewRoot locale'ini de güncelle
        FacesContext.getCurrentInstance()
                .getViewRoot()
                .setLocale(new Locale(localeCode));
        
        this.selectedLocale = localeCode;
    }
    
    public String getSelectedLocale() {
        return selectedLocale;
    }
    
    public void setSelectedLocale(String selectedLocale) {
        this.selectedLocale = selectedLocale;
    }
    
    public String getCurrentLocale() {
        return dbBundleService.getCurrentLocale().getLanguage();
    }
}
```

### 🔄 Dinamik Dil Değiştirme

#### Dropdown ile Dil Seçimi

```xhtml
<h:form>
    <h:selectOneMenu value="#{languageBean.selectedLocale}">
        <f:selectItem itemValue="tr" itemLabel="Türkçe" />
        <f:selectItem itemValue="en" itemLabel="English" />
        <f:selectItem itemValue="fr" itemLabel="Français" />
        <f:ajax listener="#{languageBean.changeLocale}" 
                render="@form" />
    </h:selectOneMenu>
</h:form>
```

#### Link ile Dil Değiştirme

```xhtml
<h:commandLink value="TR" 
               action="#{languageBean.changeLocale('tr')}" />
<h:commandLink value="EN" 
               action="#{languageBean.changeLocale('en')}" />
<h:commandLink value="FR" 
               action="#{languageBean.changeLocale('fr')}" />
```

### 🎨 PrimeFaces ile Kullanım

```xhtml
<p:outputLabel value="#{msg['label.name']}" for="name" />
<p:inputText id="name" value="#{userBean.name}" />

<p:commandButton value="#{msg['button.save']}" 
                 action="#{userBean.save}" 
                 update="messages" />

<p:messages id="messages" />

<p:selectOneButton value="#{languageBean.selectedLocale}">
    <f:selectItem itemValue="tr" itemLabel="TR" />
    <f:selectItem itemValue="en" itemLabel="EN" />
    <f:selectItem itemValue="fr" itemLabel="FR" />
    <p:ajax listener="#{languageBean.changeLocale}" 
            update="@all" />
</p:selectOneButton>
```

### 📋 DataTable Örneği

```xhtml
<h:dataTable value="#{userBean.users}" var="user">
    <h:column>
        <f:facet name="header">
            <h:outputText value="#{msg['column.name']}" />
        </f:facet>
        <h:outputText value="#{user.name}" />
    </h:column>
    
    <h:column>
        <f:facet name="header">
            <h:outputText value="#{msg['column.email']}" />
        </f:facet>
        <h:outputText value="#{user.email}" />
    </h:column>
    
    <h:column>
        <f:facet name="header">
            <h:outputText value="#{msg['column.actions']}" />
        </f:facet>
        <h:commandButton value="#{msg['button.edit']}" 
                         action="#{userBean.edit(user)}" />
        <h:commandButton value="#{msg['button.delete']}" 
                         action="#{userBean.delete(user)}" />
    </h:column>
</h:dataTable>
```

### 🔍 Debug ve Troubleshooting

#### Log Kontrolü

application.properties:
```properties
logging.level.com.valora.dbbundle=DEBUG
```

Uygulama başlatıldığında şu log'u görmeli:
```
INFO  : Creating DbBundleService bean
INFO  : DbBundleService configured for JSF EL Resolver
```

#### Test JSF Sayfası

```xhtml
<h:outputText value="Test: #{msg['welcome.message']}" />
```

Eğer `???welcome.message???` görürseniz:
1. Veritabanında key var mı kontrol edin
2. Locale doğru mu kontrol edin
3. DbBundleService bean'i var mı kontrol edin

### 💡 Best Practices

1. **Dil değiştirmede hem DbBundle hem JSF locale'ini güncelleyin**
```java
dbBundleService.changeLocale(localeCode);
FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale(localeCode));
```

2. **Session scope kullanın** - Dil tercihi kullanıcı bazlı
```java
@SessionScoped
public class LanguageBean implements Serializable { }
```

3. **AJAX ile dil değiştirme** - Sayfayı yenilemeye gerek yok
```xhtml
<f:ajax listener="#{languageBean.changeLocale}" render="@all" />
```

4. **Cache yönetimi** - Admin panelinde mesajları güncellerken
```java
dbBundleService.clearCache();
```

### 🌐 Çoklu EL Expression Kullanımı

```xhtml
<!-- Basit kullanım -->
<h:outputText value="#{msg['simple.key']}" />

<!-- Dinamik key -->
<h:outputText value="#{msg[userBean.messageKey]}" />

<!-- Attribute içinde -->
<h:inputText title="#{msg['tooltip.username']}" />

<!-- JavaScript içinde -->
<script>
    var message = "#{msg['js.confirm.delete']}";
    if (confirm(message)) {
        // delete action
    }
</script>
```

### ⚙️ Gelişmiş Yapılandırma

#### Custom Locale Resolver

Eğer URL parametresinden locale almak istiyorsanız:

```java
@Component
public class LocaleInterceptor extends HandlerInterceptorAdapter {
    
    @Autowired
    private DbBundleService dbBundleService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) {
        String lang = request.getParameter("lang");
        if (lang != null) {
            dbBundleService.changeLocale(lang);
            
            // JSF için de ayarla
            FacesContext facesContext = FacesContext.getCurrentInstance();
            if (facesContext != null) {
                facesContext.getViewRoot().setLocale(new Locale(lang));
            }
        }
        return true;
    }
}
```

URL: `http://localhost:8080/page.xhtml?lang=en`

### 📞 Destek

JSF entegrasyonu ile ilgili sorunlar için `TROUBLESHOOTING.md` dosyasına bakın.
