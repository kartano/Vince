package AVR;

public class AT90S2333 extends Atmel
{	
	//AT90S2333
	private static final int FLASH_2333 = 2048;
	private static final int EEPROM_2333 = 128;
	private static final int SRAM_2333 = 128;

    protected void HandleInterrupts() { }

    protected void doExternalInterrupt(int number) { }

public AT90S2333(){
		super(FLASH_2333, SRAM_2333, true);
	}
}
