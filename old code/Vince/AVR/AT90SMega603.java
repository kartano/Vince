package AVR;

public class AT90SMega603 extends Atmel
{	
	//AT90SMega603
	private static final int FLASH_Mega603 = 65536;
	private static final int EEPROM_Mega603 = 2048;
	private static final int SRAM_Mega603 = 4096;

    protected void HandleInterrupts() { }

    protected void doExternalInterrupt(int number) { }

	public AT90SMega603(){
		super(FLASH_Mega603, SRAM_Mega603, true);
	}
}
