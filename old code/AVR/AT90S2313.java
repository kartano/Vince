package AVR;

public class AT90S2313 extends Atmel
{	

	//AT90S2313	
	private static final int FLASH_2313 = 2048;
	private static final int EEPROM_2313 = 128;
	private static final int SRAM_2313 = 128;

    protected void HandleInterrupts() { } 

    protected void doExternalInterrupt(int number) { }

	public AT90S2313(){
		super(FLASH_2313, SRAM_2313, true);
	}
}
