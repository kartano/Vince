package AVR;

public class AT90S8515 extends Atmel
{	
      //AT90S8515
      private static final int FLASH_8515 = 8192;
      private static final int EEPROM_8515 = 512;
      private static final int SRAM_8515 = 512;
	private static final int PCBitSize = 16;

      protected void HandleInterrupts() { }

      protected void doExternalInterrupt(int number) { }

      public AT90S8515(){
            super(FLASH_8515, SRAM_8515, true, PCBitSize);
      }
}
