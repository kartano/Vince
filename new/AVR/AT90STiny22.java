package AVR;

public class AT90STiny22 extends Atmel
{	
      //AT90STiny22
      private static final int FLASH_Tiny22 = 2048;
      private static final int EEPROM_Tiny22 = 128;
      private static final int SRAM_Tiny22 = 128;	
      private static final int PCBitSize = 16;

      protected void HandleInterrupts() { }

      protected void doExternalInterrupt(int number) { }

      public AT90STiny22(){
            super(FLASH_Tiny22, SRAM_Tiny22, true, PCBitSize);
      }
}
