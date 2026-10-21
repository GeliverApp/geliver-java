# Changelog

Bu dosya SDK'daki önemli değişiklikleri listeler.

This file documents notable changes in the SDK.

## Sürüm / Version

- Türkçe: Bu değişiklikler `0.5.0` sürümü için hazırlandı.
- English: These changes are prepared for version `0.5.0`.

## Türkçe

### 0.5.0

- `shipments().create(...)` ile `isReturn` ve gönderici/alıcı için bağımsız ID veya satır içi adres seçimi kullanılarak iade gönderisi oluşturulabilir.
- `IncomingShipment` örneği eklendi.

### 0.4.0

#### Eklendi

- `transactions().createReturn(...)` ile iadeyi oluşturup etiketi hemen satın alma akışı eklendi.
- İki yeni iade örneği eklendi:
  - `src/main/java/io/geliver/examples/ReturnShipment.java`
  - `src/main/java/io/geliver/examples/ReturnTransaction.java`

#### Değişti

- `shipments().createReturn(...)` artık shipment-only iade akışıdır ve etiketi satın almaz.
- İade dokümanı iki akışı ayrı anlatır.
- README örnekleri, etiketin daha sonra `transactions().acceptOffer(...)` ile satın alınabileceğini açıklar.

#### Düzeltildi

- Return shipment örneğindeki ilerleme çıktısı, generated offer model tipiyle uyumlu hale getirildi.

## English

### 0.5.0

- `shipments().create(...)` supports independent returns with `isReturn` and either an address ID or an inline address for each side.
- Added the `IncomingShipment` example.

### 0.4.0

#### Added

- Added `transactions().createReturn(...)` for creating a return shipment and purchasing the label immediately.
- Added return examples for:
  - `src/main/java/io/geliver/examples/ReturnShipment.java`
  - `src/main/java/io/geliver/examples/ReturnTransaction.java`

#### Changed

- `shipments().createReturn(...)` now represents the shipment-only return flow and does not purchase the label.
- Return documentation now explains the two return flows separately.
- README examples now document that label purchase can be performed later with `transactions().acceptOffer(...)`.

#### Fixed

- Fixed the return-shipment example progress output to match the generated offer model type.
