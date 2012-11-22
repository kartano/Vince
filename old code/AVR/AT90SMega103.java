package AVR;

public class AT90SMega103 extends Atmel
{	
	//AT90SMega103
	private static final int FLASH_Mega103 = 131072;
	private static final int EEPROM_Mega103 = 4096;
	private static final int SRAM_Mega103 = 4096;

    protected void HandleInterrupts() { }

    protected void doExternalInterrupt(int number) { }

	public AT90SMega103(){
		super(FLASH_Mega103, SRAM_Mega103, true);
	}
}
