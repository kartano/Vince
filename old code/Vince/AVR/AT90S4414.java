package AVR;

public class AT90S4414 extends Atmel
{	
	//AT90S4414
	private static final int FLASH_4414 = 4096;
	private static final int EEPROM_4414 = 256;
	private static final int SRAM_4414 = 256;

    protected void HandleInterrupts() { } 

    protected void doExternalInterrupt(int number) { }

	public AT90S4414(){
		super(FLASH_4414, SRAM_4414, true);
	}
}
