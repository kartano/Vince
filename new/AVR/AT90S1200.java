package AVR;

public class AT90S1200 extends Atmel
{	
      //AT90S1200
      private static final int FLASH_1200 = 1024;
      private static final int EEPROM_1200 = 64;
      private static final int SRAM_1200 = 0;
	private static final int PCBitSize = 16;

      protected void HandleInterrupts() { }

      protected void doExternalInterrupt(int number) { }

      public AT90S1200(){
            super(FLASH_1200, SRAM_1200,false, PCBitSize);
      }
}
