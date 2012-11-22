package AVR;

public class AT90S2343 extends Atmel
{	
	//AT90S2343
	private static final int FLASH_2343 = 2048;
	private static final int EEPROM_2343 = 128;
	private static final int SRAM_2343 = 128;

    protected void HandleInterrupts() { } 

    protected void doExternalInterrupt(int number) { }

	public AT90S2343(){
		super(FLASH_2343, SRAM_2343, true);
	}
}
