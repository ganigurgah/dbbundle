# Valora DBBundle Control 🚀

**DBBundle**, Java ekosisteminde yer alan uygulamalar için geliştirilmiş, geleneksel `.properties` dosyalarının statikliğinden kurtulmanızı sağlayan, veritabanı tabanlı bir dil (i18n) yönetim kütüphanesidir.



## ✨ Özellikler

* **Dinamik Yönetim:** Uygulamanızı durdurmadan veya yeniden başlatmadan veritabanı üzerinden çevirileri güncelleyin.
* **Esnek Provider Yapısı:** Çevirilerin nereden geleceğini (SQL, NoSQL, Harici API vb.) `TranslationProvider` arayüzü ile siz belirleyin.
* **Yüksek Performans:** **Caffeine Cache** entegrasyonu sayesinde her talepte veritabanına gitmez, çevirileri bellekte optimize edilmiş şekilde tutar.
* **Modern Teknoloji:** Spring Boot 3+ ve Jakarta Persistence (JPA) projeleriyle tam uyumludur.

## 🛠 Kurulum
Bu kodları indireceğiniz bir dizin oluşturduktan sonra, ilgili dizini terminal de açıp,
aşağıdaki komutları sırasıyla çalıştırarak uygulamayı projenizde kullanılabilir hale getirebilirsiniz


```Bash
git clone https://github.com/ganigurgah/dbbundle.git
cd dbbundle
mvn clean install
mvn install:install-file -Dfile=target\dbbundle-1.0.1.jar -DgroupId=com.valora.library -DartifactId=dbbundle -Dversion=1.0.1 -Dpackaging=jar -DgeneratePom=true

```

Projenize dahil etmek için `pom.xml` dosyanıza aşağıdaki bağımlılığı ekleyebilirsiniz:

```xml
<dependency>
    <groupId>com.valora.library</groupId>
    <artifactId>dbbundle</artifactId>
    <version>1.0.1</version>
</dependency>
```
## 🚀 Hızlı Kullanım
1. Kendi Sağlayıcınızı Tanımlayın
   Kütüphanenin veriye nasıl ulaşacağını belirtmek için TranslationProvider arayüzünü uygulayın:


```Java

public class MyDatabaseProvider implements TranslationProvider {
   @Override
   public Map<String, String> loadTranslations(String locale) {
   // Örnek: Veritabanından verileri çekme mantığı
   // SELECT key, value FROM translations WHERE lang = locale
   return myRepository.fetchMapByLocale(locale);
   }
}
```
2. Kullanıma Başlayın

```   Java

// Provider'ı başlatın
TranslationProvider provider = new MyDatabaseProvider();

// Kontrolcüye provider'ı enjekte edin
ValoraBundleControl bundleControl = new ValoraBundleControl(provider);

// "tr" dilindeki "greeting.hello" anahtarını getirin
String message = bundleControl.getTranslation("greeting.hello", "tr");
System.out.println(message); // Çıktı: Merhaba!

```

## 🧪 Testler

Kütüphane, Mockito ve JUnit 5 kullanılarak kapsamlı test süreçlerinden geçirilmiştir. Mevcut testleri çalıştırmak için:


```Bash
mvn test
```
## 👤 Yazar
Gani GÜRGAH - [GitHub Profile](https://www.github.com/ganigurgah)

## 📄 Lisans
Bu proje MIT Lisansı ile lisanslanmıştır. Detaylar için **_LICENSE_** dosyasına bakabilirsiniz.
