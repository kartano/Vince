package AVR;

public class AT90S2323 extends Atmel
{	
	//AT90S2323
	private static final int FLASH_2323 = 2048;
	private static final int EEPROM_2323 = 128;
	private static final int SRAM_2323 = 128;

    protected void HandleInterrupts() { }

    protected void doExternalInterrupt(int number) { }


	public AT90S2323(){
		super(FLASH_2323, SRAM_2323, true);
	}
}
