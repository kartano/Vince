package AVR;

/**
 * This is the implementation of the Atmel AVR AT90S8535 mcu
 * @author Paul Pearce, Simon Mitchell
 */
public class AT90S8535 extends Atmel
{	
	//AT90S8535
	private static final int FLASH_8535 = 4096;
	private static final int EEPROM_8535 = 512;
	private static final int SRAM_8535 = 512;

	public AT90S8535(){
		super(FLASH_8535, SRAM_8535, true);
		EXTERNAL_INTERRUPTS = 2;
		WATCHDOG = true;
	}

    protected void HandleInterrupts()
    {
	// For each interrupt, check the flags in order
	// of priority.
	// If any interrupts are flagged, set the program
	// counter to the appropriate vector and clear the
	// global interrupt enable register.
	// Also make sure that the flag for the fired
	// interrupt is cleared on the way out!!!  VERY
	// important!
        try
	{    
	    if (Utils.bit6(get_ioreg(GIFR)))
	    {
		mPC = 0x0001;
		set_iflag(false);
		set_ioreg(GIFR,get_ioreg(GIFR) ^ 0x40);
	    }
	    else if (Utils.bit7(get_ioreg(GIFR)))
	    {
		mPC = 0x0002;
		set_iflag(false);
		set_ioreg(GIFR,get_ioreg(GIFR) ^ 0x80);
	    }
	}
    catch (InvalidRegisterException e) { }
    }

    protected void doExternalInterrupt(int number)
    {
	switch (number)
	{
	    case 0 :
		if (!Utils.bit6(get_ioreg(GIMSK)))
		    return;
		// Flag that INT0 has occured
		try { set_ioreg(GIFR,get_ioreg(GIFR) | 0x40); }
		catch (InvalidRegisterException e) { }
		break;
	    case 1 :
		// Ditto for external interrupt 2
		if (!Utils.bit7(get_ioreg(GIMSK)))
		    return;
		try { set_ioreg(GIFR,get_ioreg(GIFR) | 0x80); }
		catch (InvalidRegisterException e) { }
		break;
	}
    }
}
