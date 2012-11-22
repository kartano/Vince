package AVR;

public class AT90S4433 extends Atmel
{	
	//AT90S4433
	private static final int FLASH_4433 = 4096;
	private static final int EEPROM_4433 = 256;
	private static final int SRAM_4433 = 128;

    protected void HandleInterrupts() { } 

    protected void doExternalInterrupt(int number) { }

	public AT90S4433(){
		super(FLASH_4433, SRAM_4433, true);
	}
}
