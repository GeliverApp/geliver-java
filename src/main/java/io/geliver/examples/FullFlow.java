package io.geliver.examples;

import io.geliver.sdk.GeliverClient;
import io.geliver.sdk.models.Shipment;
import io.geliver.sdk.models.Transaction;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FullFlow {
    public static void main(String[] args) throws Exception {
        var token = System.getenv().getOrDefault("GELIVER_TOKEN", "YOUR_TOKEN");
        var client = new GeliverClient(token);

        Map<String, Object> sender = client.addresses().createSender(new HashMap<>() {
            {
                put("name", "ACME Inc.");
                put("email", "ops@acme.test");
                put("address1", "Hasan Mahallesi");
                put("countryCode", "TR");
                put("cityName", "Istanbul");
                put("cityCode", "34");
                put("districtName", "Esenyurt");
                put("zip", "34020");
            }
        });

        Shipment s = client.shipments().createTest(new HashMap<>() {
            {
                put("senderAddressID", sender.get("id"));
                put("recipientAddress", new HashMap<>() {
                    {
                        put("name", "John Doe");
                        put("email", "john@example.com");
                        put("address1", "Atatürk Mahallesi");
                        put("countryCode", "TR");
                        put("cityName", "Istanbul");
                        put("cityCode", "34");
                        put("districtName", "Esenyurt");
                        put("zip", "34020");
                    }
                });
                put("order", new HashMap<>() {
                    {
                        put("orderNumber", "ABC12333322");
                        put("sourceIdentifier", "https://magazaadresiniz.com");
                        put("totalAmount", "150");
                        put("totalAmountCurrency", "TRY");
                    }
                });
                put("length", "10.0");
                put("width", "10.0");
                put("height", "10.0");
                put("distanceUnit", "cm");
                put("weight", "1.0");
                put("massUnit", "kg");
            }
        });

        var offers = s.getOffers();
        if (offers == null || offers.getCheapest() == null) {
            System.err.println("Error: No cheapest offer available");
            System.exit(1);
        }

        Transaction tx;
        try {
            tx = client.transactions().acceptOffer(offers.getCheapest().getId());
        } catch (Exception e) {
            System.err.println("Accept offer error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
            return; // For compiler
        }
        System.out.println("tx " + tx.getId() + " paid=" + tx.getIsPayed());
        if (tx.getShipment() != null) {
            System.out.println("Barcode: " + tx.getShipment().getBarcode());
            System.out.println("Tracking number: " + tx.getShipment().getTrackingNumber());
            System.out.println("Label URL: " + tx.getShipment().getLabelURL());
            System.out.println("Tracking URL: " + tx.getShipment().getTrackingUrl());
        }

        // Etiket indirme: LabelFileType kontrolü
        // Eğer LabelFileType "PROVIDER_PDF" ise, LabelURL'den indirilen PDF etiket
        // kullanılmalıdır.
        // Eğer LabelFileType "PDF" ise, responsiveLabelURL (HTML) dosyası
        // kullanılabilir.
        if (tx.getShipment() != null) {
            String labelFileType = tx.getShipment().getLabelFileType();
            if ("PROVIDER_PDF".equals(labelFileType)) {
                // PROVIDER_PDF: Sadece PDF etiket kullanılmalı
                if (tx.getShipment().getLabelURL() != null) {
                    byte[] pdf = client.shipments().downloadLabelByUrl(tx.getShipment().getLabelURL());
                    Files.write(Path.of("label-java.pdf"), pdf);
                    System.out.println("PDF etiket indirildi (PROVIDER_PDF)");
                }
            } else if ("PDF".equals(labelFileType)) {
                // PDF: ResponsiveLabel (HTML) kullanılabilir
                if (tx.getShipment().getResponsiveLabelURL() != null) {
                    String html = client.shipments()
                            .downloadResponsiveLabelByUrl(tx.getShipment().getResponsiveLabelURL());
                    Files.writeString(Path.of("label-java.html"), html);
                    System.out.println("HTML etiket indirildi (PDF)");
                }
                // İsteğe bağlı olarak PDF de indirilebilir
                if (tx.getShipment().getLabelURL() != null) {
                    byte[] pdf = client.shipments().downloadLabelByUrl(tx.getShipment().getLabelURL());
                    Files.write(Path.of("label-java.pdf"), pdf);
                }
            }
        }
    }
}
