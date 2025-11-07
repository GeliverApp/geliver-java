# Geliver Java SDK

Resmi Java istemcisi (framework bağımsız). Java 17+ gerektirir. HTTP isteklerinde Java 11 `HttpClient` kullanır ve JSON için Jackson ile çalışır.

- Dokümantasyon: https://docs.geliver.io
- Maven ile kullanın ya da bu repo altındaki `java/` modülünü derleyin.

## Kurulum (yerel)

```bash
cd sdks/java
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
      put("name", "ACME Inc."); put("email", "ops@acme.test"); put("address1", "Street 1");
      put("countryCode", "TR"); put("cityName", "Istanbul"); put("cityCode", "34");
      put("districtName", "Esenyurt"); put("districtID", 107605); put("zip", "34020");
    }});

    // Inline alıcı bilgileriyle test gönderisi oluşturma. Canlı ortam için createTest yerine create fonksiyonunu kullanın.
    var shipment = client.shipments().createTest(new java.util.HashMap<>() {{
      put("sourceCode", "API"); put("senderAddressID", sender.get("id"));
      put("recipientAddress", new java.util.HashMap<>() {{
        put("name", "John Doe"); put("email", "john@example.com");
        put("address1", "Dest St 2"); put("countryCode", "TR");
        put("cityName", "Istanbul"); put("cityCode", "34");
        put("districtName", "Esenyurt"); put("districtID", 107605); put("zip", "34020");
      }});
      // İstek alanları string olmalıdır
      put("length", "10.0"); put("width", "10.0"); put("height", "10.0");
      put("distanceUnit", "cm"); put("weight", "1.0"); put("massUnit", "kg");
    }});

    // Teklifler create yanıtında hazır olabilir; >=%99 olana kadar bekleyin
    var offers = shipment.getOffers();
    if (offers == null || !offers.isReady()) {
      long start = System.currentTimeMillis();
      while (true) {
        var s = client.shipments().get(shipment.getId());
        offers = s.getOffers();
        if (offers != null && offers.isReady()) break;
        if (System.currentTimeMillis() - start > 60000) throw new RuntimeException("Timed out");
        Thread.sleep(1000);
      }
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

## Diğer Kaynaklar (Java)

- Webhooklar

  - Oluştur: `client.webhooks().create("https://example.com/webhook", null)`
  - Listele: `client.webhooks().list()`
  - Sil: `client.webhooks().delete(webhookId)`
  - Test: `client.webhooks().test("price.updated", "https://example.com/webhook")`

- Sağlayıcı Hesapları (Provider Accounts)

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
