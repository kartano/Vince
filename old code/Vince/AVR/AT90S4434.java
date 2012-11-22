package AVR;

public class AT90S4434 extends Atmel
{	
	//AT90S4434
	private static final int FLASH_4434 = 4096;
	private static final int EEPROM_4434 = 256;
	private static final int SRAM_4434 = 256;

    protected void HandleInterrupts() { }

    protected void doExternalInterrupt(int number) { }

	public AT90S4434(){
		super(FLASH_4434, SRAM_4434, true);
	}
}
