# Geliver Java SDK

[![Maven Central](https://img.shields.io/maven-central/v/io.geliver/geliver-java.svg)](https://central.sonatype.com/artifact/io.geliver/geliver-java)

Resmi Java istemcisi (framework bağımsız). Java 17+ gerektirir. HTTP isteklerinde Java 11 `HttpClient` kullanır ve JSON için Jackson ile çalışır.

- Dokümantasyon: https://docs.geliver.io
- Maven ile kullanın ya da bu repo altındaki `java/` modülünü derleyin.

## Kurulum (yerel)

```bash
mvn -q -DskipTests package
```

## Hızlı Başlangıç (TR)

```java
import io.geliver.sdk.*;
import io.geliver.sdk.models.*;

public class QuickStart {
  public static void main(String[] args) throws Exception {
    var token = System.getenv().getOrDefault("GELIVER_TOKEN", "YOUR_TOKEN");
    var client = new GeliverClient(token);

    // Gönderici adresi oluşturma (tek seferlik). ID'yi saklayıp tekrar kullanın.
    var sender = client.addresses().createSender(new java.util.HashMap<>() {{
      put("name", "ACME Inc."); put("email", "ops@acme.test"); put("phone", "+905051234567"); put("address1", "Hasan Mahallesi");
      put("countryCode", "TR"); put("cityName", "Istanbul"); put("cityCode", "34");
      put("districtName", "Esenyurt"); put("zip", "34020");
    }});

    // Inline alıcı bilgileriyle test gönderisi oluşturma. Canlı ortam için createTest yerine create fonksiyonunu kullanın.
    var shipment = client.shipments().createTest(new java.util.HashMap<>() {{
      put("sourceCode", "API"); put("senderAddressID", sender.get("id"));
      put("recipientAddress", new java.util.HashMap<>() {{
        put("name", "John Doe"); put("email", "john@example.com"); put("phone", "+905051234568");
        put("address1", "Atatürk Mahallesi"); put("countryCode", "TR");
        put("cityName", "Istanbul"); put("cityCode", "34");
        put("districtName", "Esenyurt"); put("zip", "34020");
      }});
      // İstek alanları string olmalıdır
      put("length", "10.0"); put("width", "10.0"); put("height", "10.0");
      put("distanceUnit", "cm"); put("weight", "1.0"); put("massUnit", "kg");
      put("order", new java.util.HashMap<>() {{
        put("orderNumber", "WEB-12345");
        // sourceIdentifier alanına mağazanızın tam adresini (ör. https://magazam.com) ekleyin.
        put("sourceIdentifier", "https://magazam.com");
        put("totalAmount", "150");
        put("totalAmountCurrency", "TRY");
      }});
    }});

    // Teklifler create yanıtındaki offers alanında gelir
    var offers = shipment.getOffers();
    if (offers == null || offers.getCheapest() == null) {
      throw new RuntimeException("Teklifler hazır değil; GET /shipments ile tekrar kontrol edin.");
    }

    var tx = client.transactions().acceptOffer(offers.getCheapest().getId());
    System.out.println("Transaction: " + tx.getId() + " paid=" + tx.getIsPayed());
    // Barcode, tracking number ve URL'ler Transaction içindeki Shipment'ta yer alır
    if (tx.getShipment() != null) {
      System.out.println("Barcode: " + tx.getShipment().getBarcode());
      System.out.println("Tracking number: " + tx.getShipment().getTrackingNumber());
      System.out.println("Label URL: " + tx.getShipment().getLabelURL());
      //# Tracking URL (Kargo takip numarası) alanı bazı firmalarda gönderici şubesine teslimden veya kurye sizden teslim aldıktan sonra dolar. Bu sebeple webhook kullarak bu alanı almanız önerilir.
      System.out.println("Tracking URL: " + tx.getShipment().getTrackingUrl());
    }

    // Etiket indirme: Teklif kabulünden sonra (Transaction) gelen URL'leri kullanabilirsiniz de; URL'lere her shipment nesnesinin içinden ulaşılır.
    if (tx.getShipment() != null && tx.getShipment().getLabelURL() != null) {
      var pdf = client.shipments().downloadLabelByUrl(tx.getShipment().getLabelURL());
      java.nio.file.Files.write(java.nio.file.Path.of("label.pdf"), pdf);
    }
    if (tx.getShipment() != null && tx.getShipment().getResponsiveLabelURL() != null) {
      var html = client.shipments().downloadResponsiveLabelByUrl(tx.getShipment().getResponsiveLabelURL());
      java.nio.file.Files.writeString(java.nio.file.Path.of("label.html"), html);
    }


  }
}
```

## Notlar ve İpuçları (TR)

- `length`, `width`, `height`, `weight` istek alanları string gönderilmelidir (örn. "10.0").
- Barkod ve etiketler teklif kabulünden (Transaction) sonra üretilir; kabulün ardından URL'lerden indirebilirsiniz.
- Takip numarası (trackingNumber) her zaman işlemin hemen ardından oluşmayabilir; prod ortamında webhookları kullanın.
- İleride API'ye yeni alanlar eklense bile kütüphane bozulmaz: JSON ayrıştırıcı bilinmeyen alanları yoksayar.
- Adres kuralları: phone alanı hem gönderici hem alıcı adresleri için zorunludur. Zip alanı gönderici adresi için zorunludur; alıcı adresi için opsiyoneldir. `addresses().createSender(...)` phone/zip eksikse, `addresses().createRecipient(...)` phone eksikse hata verir.

## Örnekler

- Full flow: `src/main/java/io/geliver/examples/FullFlow.java`
- Tek aşamada gönderi (Create Transaction):

```java
var tx = client.transactions().create(new java.util.HashMap<>() {{
  put("senderAddressID", sender.get("id"));
  put("recipientAddress", new java.util.HashMap<>() {{
    put("name","OneStep Recipient"); put("address1","Atatürk Mahallesi"); put("countryCode","TR"); put("cityName","Istanbul"); put("cityCode","34"); put("districtName","Esenyurt");
  }});
  put("length","10.0"); put("width","10.0"); put("height","10.0"); put("distanceUnit","cm"); put("weight","1.0"); put("massUnit","kg");
}});
```

- Kapıda ödeme:

```java
var txPod = client.transactions().create(new java.util.HashMap<>() {{
  put("senderAddressID", sender.get("id"));
  put("recipientAddress", new java.util.HashMap<>() {{
    put("name","POD Recipient"); put("address1","Atatürk Mahallesi"); put("countryCode","TR"); put("cityName","Istanbul"); put("cityCode","34"); put("districtName","Esenyurt");
  }});
  put("length","10.0"); put("width","10.0"); put("height","10.0"); put("distanceUnit","cm"); put("weight","1.0"); put("massUnit","kg");
  put("providerServiceCode","PTT_KAPIDA_ODEME");
  put("productPaymentOnDelivery", true);
  put("order", new java.util.HashMap<>() {{ put("orderNumber","POD-12345"); put("totalAmount","150"); put("totalAmountCurrency","TRY"); }});
}});
```

- Kendi anlaşmanızla etiket satın alma:

```java
var txOwn = client.transactions().create(new java.util.HashMap<>() {{
  put("senderAddressID", sender.get("id"));
  put("recipientAddress", new java.util.HashMap<>() {{
    put("name","OwnAg Recipient"); put("address1","Atatürk Mahallesi"); put("countryCode","TR"); put("cityName","Istanbul"); put("cityCode","34"); put("districtName","Esenyurt");
  }});
  put("length","10.0"); put("width","10.0"); put("height","10.0"); put("distanceUnit","cm"); put("weight","1.0"); put("massUnit","kg");
  put("providerServiceCode","SURAT_STANDART");
  put("providerAccountID","c0dfdb42-012d-438c-9d49-98d13b4d4a2b");
}});
```

### Gönderi Listeleme, Getir, Güncelle, İptal, Klonla

- Listeleme (docs): https://docs.geliver.io/docs/shipments_and_transaction/list_shipments
- Gönderi getir (docs): https://docs.geliver.io/docs/shipments_and_transaction/list_shipments
- Paket güncelle (docs): https://docs.geliver.io/docs/shipments_and_transaction/update_package_shipment
- Gönderi iptal (docs): https://docs.geliver.io/docs/shipments_and_transaction/cancel_shipment
- Gönderi klonla (docs): https://docs.geliver.io/docs/shipments_and_transaction/clone_shipment

```java
// Listeleme (sayfalandırma). list(...) dönen zarfta pagination + data alanları yer alır.
var params = new java.util.HashMap<String, Object>();
params.put("page", 1);
params.put("limit", 20);
var envelope = client.shipments().list(params);
@SuppressWarnings("unchecked")
var shipments = (java.util.List<Shipment>) envelope.get("data");
for (var s : shipments) {
  System.out.println(s.getId() + " " + s.getStatusCode());
}

// Getir
var fetched = client.shipments().get("SHIPMENT_ID");
var status = fetched.getTrackingStatus();
System.out.println("Tracking: " + (status != null ? status.getTrackingStatusCode() : null));

// Paket güncelle (eni, boyu, yüksekliği ve ağırlığı string gönderin)
var updated = client.shipments().updatePackage(fetched.getId(), new java.util.HashMap<>() {{
  put("length", "12.0");
  put("width", "12.0");
  put("height", "10.0");
  put("distanceUnit", "cm");
  put("weight", "1.2");
  put("massUnit", "kg");
}});

// İptal
client.shipments().cancel(fetched.getId());

// Klonla
var cloned = client.shipments().clone(fetched.getId());
System.out.println("Cloned shipment: " + cloned.getId());
```

## Diğer Kaynaklar (Java)

- Webhooklar

  - Oluştur: `client.webhooks().create("https://example.com/webhook", null)`
  - Listele: `client.webhooks().list()`
  - Sil: `client.webhooks().delete(webhookId)`
  - Test: `client.webhooks().test("price.updated", "https://example.com/webhook")`
  - Minimal örnek:

    ```java
    import com.fasterxml.jackson.databind.ObjectMapper;
    import io.geliver.sdk.models.WebhookUpdateTrackingRequest;

    var mapper = new ObjectMapper();
    var evt = mapper.readValue(body, WebhookUpdateTrackingRequest.class);
    if ("TRACK_UPDATED".equals(evt.getEvent())) {
      var shipment = evt.getShipment();
      System.out.println("Tracking update: " + shipment.getTrackingUrl() + " " + shipment.getTrackingNumber());
    }
    ```

- Kendi Kargo Anlaşmanız (Provider Accounts)

  - Listele: `client.providers().listAccounts()`
  - Oluştur: `client.providers().createAccount(Map.of("username","u","password","p","name","My","providerCode","SURAT","version",1))`
  - Sil: `client.providers().deleteAccount(id, true)`

- Kargo Şablonları (Parcel Templates)

  - Oluştur: `client.parcelTemplates().create(Map.of("name","Small","distanceUnit","cm","massUnit","kg","height","4","length","4","weight","1","width","4"))`
  - Listele: `client.parcelTemplates().list()`
  - Sil: `client.parcelTemplates().delete(templateId)`

- Fiyat Listesi

  - `client.prices().listPrices("parcel", "10.0","10.0","10.0","1.0","cm","kg")`

- Coğrafi Veriler

  - Şehirler (typed): `client.geo().listCitiesTyped("TR") // List<City>`
  - İlçeler (typed): `client.geo().listDistrictsTyped("TR", "34") // List<District>`
  - İsterseniz Map listeleriyle de çalışabilirsiniz: `listCities`, `listDistricts`

- Organizasyonlar
  - Bakiye: `client.organizations().getBalance(orgId)`

## Örnek Çalıştırma

```bash
export GELIVER_TOKEN=YOUR_TOKEN
mvn -q -Dtest=FullFlowTest -DfailIfNoTests=false test
# ya da örnek ana sınıf:
mvn -q -Dexec.mainClass=io.geliver.examples.FullFlow exec:java
```

---

## Hatalar ve İstisnalar

- İstemci şu durumlarda `GeliverApiException` fırlatır: (1) HTTP 4xx/5xx; (2) JSON envelope `result == false`.
- Hata alanları: `code`, `additionalMessage`, `status`, `body`, `message`.

```java
try {
  client.shipments().create(Map.of(/* ... */));
} catch (io.geliver.sdk.GeliverApiException e) {
  System.err.println("code: " + e.code);
  System.err.println("message: " + e.getMessage());
  System.err.println("additional: " + e.additionalMessage);
  System.err.println("status: " + e.status);
}
```
