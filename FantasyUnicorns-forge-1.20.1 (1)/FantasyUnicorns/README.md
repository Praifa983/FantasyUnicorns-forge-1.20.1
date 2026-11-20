# Fantasy Unicorns (Forge 1.20.1)

ต้องใช้: JDK 17, Forge 47.3.0, GeckoLib 4.4.9, TerraBlender 3.0.1.7 (ใส่ในโฟลเดอร์ mods ตอนเล่นจริงด้วย)

## วิธีรัน/บิลด์
1. ดาวน์โหลด Forge MDK 1.20.1 อย่างเป็นทางการ แล้วคัดลอก `gradlew`, `gradlew.bat`, โฟลเดอร์ `gradle/` มาวางในโปรเจกต์นี้
   (หรือถ้ามี Gradle 8.1.1 ในเครื่อง รัน `gradle wrapper`)
2. `./gradlew runClient`  ทดสอบในเกม
3. `./gradlew build`      ได้ไฟล์ .jar ที่ build/libs/

## ลองเล่น
- Creative > แท็บ Fantasy Unicorns: ไข่เสก, แอปเปิ้ลวิเศษ, ใบไม้, ดอกไม้
- ป้อนแอปเปิ้ลวิเศษ (โอกาสเทม 1/3 ต่อลูก) > ใส่ Saddle > ขี่ > กด R ยิงลำแสง
- ผสมพันธุ์ด้วย Golden Carrot (ต้องเชื่องทั้งคู่)
- หาไบโอม Mystic Meadow: `/locate biome fantasyunicorns:mystic_meadow`

## สร้างเท็กซ์เจอร์/โมเดลใหม่
`python3 tools_generate_assets.py` (ต้องมี Pillow) หรือแก้ไฟล์ใน Blockbench โดยตรง
