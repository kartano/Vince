package AVR;

public class AT90STiny13 extends Atmel
{	
	//AT90STiny13
	private static final int FLASH_Tiny13 = 2048;
	private static final int EEPROM_Tiny13 = 128;
	private static final int SRAM_Tiny13 = 128;

    protected void HandleInterrupts() { } 

    protected void doExternalInterrupt(int number) { }

	public AT90STiny13(){
		super(FLASH_Tiny13, SRAM_Tiny13, true);
	}
}
