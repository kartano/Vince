package AVR;

public class AT90STiny10 extends Atmel
{	
	//AT90STiny10
	private static final int FLASH_Tiny10 = 1024;
	private static final int EEPROM_Tiny10 = 64;
	private static final int SRAM_Tiny10 = 0;

    protected void HandleInterrupts() { } 

    protected void doExternalInterrupt(int number) { }

	public AT90STiny10(){
		super(FLASH_Tiny10, SRAM_Tiny10, false);
	}
}
