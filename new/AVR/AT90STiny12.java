package AVR;

public class AT90STiny12 extends Atmel
{	
      //AT90STiny12
      private static final int FLASH_Tiny12 = 1024;
      private static final int EEPROM_Tiny12 = 64;
      private static final int SRAM_Tiny12 = 0;
      private static final int PCBitSize = 16;

      protected void HandleInterrupts() { }

      protected void doExternalInterrupt(int number) { }

      public AT90STiny12(){
            super(FLASH_Tiny12, SRAM_Tiny12, false, PCBitSize);
      }
}
