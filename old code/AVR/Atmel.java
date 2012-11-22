//=============================================
// atmel Class
//
// VERSION: 1.4.1 - Unstable, modified by Craig Eales
//
// MODIFICATION HISTORY:
//
// 30/07/2003 - 1.4.1 - BUTCHERED code! removed threads!!! made observable...!eek
// 27/07/2003 - 1.3.11 - Minor changes to disassembler stuff
// 26/07/2003 - 1.3.10 - Added in Paul's last instruction code
// 24/07/2003 - 1.3.9 - Cleared up data memory get'n'set
//                    - Added get'n'set for program memory
// 23/07/2003 - 1.3.8 - Minor improvements, overhauled decode sequence
// 21/07/2003 - 1.3.7 - Cleaned up stack code, cleaned up Run method
// 20/07/2003 - 1.3.6 - Fixed Interrupt code, moved stack to seperate class
// 18/07/2003 - 1.3.5 - Interrupt code is now abstract
// 17/07/2003 - 1.3.4 - Work on interrupts, implemented RJMP
// 16/07/2003 - 1.3.3 - Performance improvements
// 16/07/2003 - 1.3.2 - Adjusted thread mechanism
// 15/07/2003 - 1.3.1 - Changed Constructor
// 13/07/2003 - 1.3.0 - Added Paul's modification to use threads!
// 10/07/2003 - 1.2.3 - Removed redundant branch instructions
// 08/07/2003 - 1.2.2 - Fixed bug with get/set io registers
//                    - Fixed bug with sets for all status flags in sreg
// 07/07/2003 - 1.2.1 - Added in code to load hex files
// 06/07/2003 - 1.2.0 - Modified to use unsigned byte class
// 02/07/2003 - 1.1.7 - Will now load binary files
// 01/07/2003 - 1.1.6 - Fetch method now works for 32 bit instructions
// 30/06/2003 - 1.1.5 - Optimised some code
// 28/06/2003 - 1.1.4 - More instructions, synchronized stack pointer stuff
// 26/06/2003 - 1.1.3 - Added store instructions, fixed debug method
// 25/06/2003 - 1.1.2 - Added "dumping" methods, more instructions
// 23/06/2003 - 1.1.1 - Now uses Utils class
// 22/06/2003 - 1.1.0 - Moved XML stuff to seperate utility
//		              - Instruction decoding MUCH faster
// 13/06/2003 - 1.0.6 - Removed MAR register
//                    - PC is only incremented after current instruction
//                      execution has completed - not during the fetch
// 29/05/2003 - 1.0.5 - Completed new XML based decode for Execute
// 25/05/2003 - 1.0.4 - Work on new execution method
// 18/05/2003 - 1.0.3 - Instruction set now loaded from XML document
// 15/05/2003 - 1.0.2 - Bug fixes with some instructions
// 11/05/2003 - 1.0.1 - Added more instructions
// 09/05/2003 - 1.0.0 - Program Memory changed to type INT, commenced execution code
// 04/05/2003 - ?.?.? - Minor changes, started on opcodes
//=============================================

package AVR;

import java.lang.*;
import java.io.*;
import java.util.*;

/**
 * This class is an abstract to be used to develop
 * other Atmel MCU types.
 * @author Simon Mitchell
 * @author Paul Pearce
 * @author Craig Eales
 * @version 1.4.1
 */

abstract public class Atmel
{
        //----------------------------------------------
        // Version numbering.
        //----------------------------------------------
        private final int myMajor = 1;
        private final int myMinor = 4;
        private final int myRevision = 1;

        private boolean mExecuting;

        //----------------------------------------------
        // Remember the last runtime exception.
        //----------------------------------------------
        private RuntimeException mLastRuntimeException;
        private int mLastInstructionAddress;

        //----------------------------------------------
        // Seems silly, but the decoder will run faster
        // if it doesn't have to keep making space for
        // these things for every instruction.
        //----------------------------------------------
        private int d, r, dvalue, rvalue, k, K, result, s, q, b, A, Avalue;

        //----------------------------------------------
        // Internal CPU registers
        //----------------------------------------------

         // Memory buffer register - used to read opcodes during execiton
         private int mMBR;
        // Memory buffer register 2 - used ONLY for 32 bit instructions
        private int mMBR2;

    // Program Counter
        protected int mPC;
	//Holds Instruction String (for user)
	private String currentInstruction;

    //-----------------------------------------------
        // These make parts of the code easier to read
    //-----------------------------------------------
        // Note:  These are all I/O registers!
        protected static final int MCUSR = 0x34;
        protected static final int MCUCR = 0x35;
        protected static final int GIFR = 0x3A;
        protected static final int GIMSK = 0x3B;
        protected static final int SREG = 0x3F;

    //-----------------------------------------------
    // The stack
    //-----------------------------------------------
      private StackInterface mStack;

    //-----------------------------------------------
    // Other stuff
    //-----------------------------------------------
    private boolean mStepmode;

    //----------------------------------------
    // MCU structure - these values will change
    // depending on which MCU type is being used
    //----------------------------------------
        protected int PROGRAM_MEMORY_SIZE;
        protected int DATA_MEMORY_SIZE;
      protected int EXTERNAL_INTERRUPTS = 0;
      protected boolean WATCHDOG = false;

        // These values never change
        private int GENERAL_REG_COUNT=32;
        private int IO_REG_COUNT = 64;

        // Program Memory - flash
        // Just a big clump of stuff, really
        private int[] program_memory;

        // Data Memory - general/IO registers plus SD RAM
        // Note that the indexes, general registers and I/O
        // registers will all squeeze into here
        private UByte[] data_memory;

        //----------------------------------------------
        // Constants to speed up some code.
        // Makes things look prettier!
        //----------------------------------------------
        private final byte BYTE_0 = 0x00;

 	//----------------------------------------------
	// Constructor
	//----------------------------------------------
    /**
     * Contructs a new instance of the Atmel class
     */
    public Atmel(int ProgramCapacity, int DataCapacity, boolean SoftwareStack)
    {
	mLastInstructionAddress = 0;
	mExecuting = false;
	PROGRAM_MEMORY_SIZE = ProgramCapacity;
	DATA_MEMORY_SIZE = GENERAL_REG_COUNT + IO_REG_COUNT + DataCapacity;
      if (SoftwareStack)
            mStack = new SoftwareStack();
      else
            mStack = new HardwareStack();
     	mStack.set_parent(this);
      mStepmode = false;
	program_memory = new int[PROGRAM_MEMORY_SIZE];
	data_memory = new UByte[DATA_MEMORY_SIZE];
	power_on_reset();
    }

        //----------------------------------------------
        // Load methods - there are a couple of types
        //----------------------------------------------

        /** Load and parse the contents of the Hex file into memory
         * This will use the Vince lexer (built from JLex)
         * @param filename The name of the .hex file to load
         */
        public void LoadHexFile(String filename) throws RuntimeException
        {
            int memPtr = 0;
            Yytoken current_token;
            boolean moreLines = true;

            int startingAddress;
            String recordType;
            int valueForMemory;

            File theFile = new File(filename);
            if (!theFile.exists())
                throw new RuntimeException(Constants.Error(Constants.FILE_NOT_FOUND) + " " + filename);
            try
            {
                        //Create token stream from input hex-format file
                        FileInputStream is = new FileInputStream(theFile);
                        Yylex yy = new Yylex(is); //instantiate lexer object
                        current_token = yy.yylex(); //advance first token
                        while (moreLines)
                        {
                        if (memPtr >= PROGRAM_MEMORY_SIZE)
                            throw new RuntimeException(Constants.Error(Constants.OUT_OF_MEMORY));

                        if(current_token.m_index == 0)
                        {
                            //end of file
                            moreLines = false;
                        }
                        else if(current_token.m_index == 1)
                        {
                                //get starting address
                                startingAddress = Integer.parseInt(current_token.m_text);
                                current_token = yy.yylex(); //advance lexer
                                //get record type information
                                if(current_token.m_index != 2)
                                {
                                        //file semantics incorrect (contract breached by assembler)
                                        throw new RuntimeException(Constants.Error(Constants.INVALID_HEXFILE));
                                }
                                recordType = current_token.m_text;
                                current_token = yy.yylex(); //advance lexer

                                //depending upon the record type, select record location
                                if(recordType.equalsIgnoreCase("data record"))
                                {
                                        //data record: most of the action happens here
                                        //set memory pointer to startingAddress
                                        memPtr = startingAddress;

                                        // Load in the current instruction
                                        valueForMemory = Integer.parseInt(current_token.m_text,16);
                                        // Swap the nibbles
                                        valueForMemory = ((valueForMemory & 0xff) << 8) + ((valueForMemory & 0xff00) >> 8);
                                        program_memory[memPtr++] = valueForMemory;

                                        current_token = yy.yylex();

                                        //get record and then load record into memory
                                        while(current_token.m_index == 3)
                                        {
                                                //ASSERTION: data records are two bytes long
                                                //load record into memory
                                                valueForMemory = Integer.parseInt(current_token.m_text,16);
                                                // Swap the nibbles
                                                valueForMemory = ((valueForMemory & 0xff) << 8) + ((valueForMemory & 0xff00) >> 8);
                                                program_memory[memPtr++] = valueForMemory;

                                                current_token = yy.yylex(); //advance lexer
                                        }

                                }
                                else if(recordType.equalsIgnoreCase("end of file record"))
                                {
                                        //acknowledged end of file approaching
                                        current_token = yy.yylex(); //advance lexer
                                }
                                else if(recordType.equalsIgnoreCase("extendedSAR"))
                                {
                                        //extended segment address record
                                        //limited use in AVR
                                        current_token = yy.yylex(); //advance lexer
                                }
                                else if(recordType.equalsIgnoreCase("startSAR"))
                                {
                                        //start of segment address record
                                        //limited use in AVR
                                        current_token = yy.yylex(); //advance lexer
                                }
                                else if(recordType.equalsIgnoreCase("extendedLAR"))
                                {
                                        //extended linear address record
                                        //use unknown as yet...
                                        current_token = yy.yylex(); //advance lexer
                                }
                                else if(recordType.equalsIgnoreCase("startLAR"))
                                {
                                        //start of linear address record
                                        //use unknown as yet...
                                        current_token = yy.yylex(); //advance lexer
                                }
                        }
                        else
                        {
                                //file semantics incorrect (contract breached by assembler)
                                throw new RuntimeException(Constants.Error(Constants.INVALID_HEXFILE));
                        }
                        }
                        is.close();
                }
                catch (IOException e)
                {
                        throw new RuntimeException(Constants.Error(Constants.INVALID_HEXFILE));
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                        //Something VERY BAD happened in the lexer:
                        //failed to recognise data in file.
                        throw new RuntimeException(Constants.Error(Constants.INVALID_HEXFILE));
                }
            return;
        }

    /**
     * Loads a binary file into program memory
     * @param filename The name of the .bin file to load
     */
        public void LoadBinaryFile(String filename) throws RuntimeException
        {
            byte bytepair[];
            int memPtr = 0;

            bytepair = new byte[2];
            File theFile = new File(filename);
            if (!theFile.exists())
                throw new RuntimeException(Constants.Error(Constants.FILE_NOT_FOUND) + " " + filename);
            try
        {
                        FileInputStream is = new FileInputStream(theFile);
                        int readingdata = is.read(bytepair);
                        while (readingdata > 0)
                        {
                        if (memPtr >= PROGRAM_MEMORY_SIZE)
                                throw new RuntimeException(Constants.Error(Constants.OUT_OF_MEMORY));
                                program_memory[memPtr++] = Utils.unsigned_byte(bytepair[0]) + (Utils.unsigned_byte(bytepair[1]) << 8);
                                readingdata = is.read(bytepair);
                        }
                        is.close();
            }
            catch (IOException e)
            {
                        System.err.println("Error reading in binary file " + filename);
                        System.err.println(e.toString());
                        System.exit(-1);
            }
            return;
        }

        //----------------------------------------------
        // Version numbering
        //----------------------------------------------

    /**
     * Returns the Atmel class Major version
     * @return Atmel class Major version
     */
        public int Major() { return myMajor; }
    /**
     * Returns the Atmel class Minor version
     * @return Atmel class Minor version
     */
        public int Minor() { return myMinor; }
    /**
     * Returns the Atmel class Revision version
     * @return Atmel class Revision version
     */
        public int Revision() { return myRevision ; }

        //----------------------------------------------
        // Internal CPU stuff
        //----------------------------------------------

    /**
     * Returns contents of the Memory Buffer Register
     * @return Contents of Memory Buffer Register
     */
        public int MBR() { return mMBR; }
    /**
     * Returns Program Counter
     * @return Program Counter
     */
        public int PC() { return mPC; }
    /**
     * Return size of Program Memory
     * @return Program Memory size
     */
        public int ProgramMemorySize() { return PROGRAM_MEMORY_SIZE; }
    /**
     * Return size of Data Memory
     * @return Data Memory size
     */
        public int DataMemorySize() { return DATA_MEMORY_SIZE; }
    /**
     * Return number of General Registers
     * @return Number of General Registers
     */
        public int GeneralRegCount() { return GENERAL_REG_COUNT; }
    /**
     * Return number of IO Registers
     * @return Number of IO Registers
     */
        public int IORegCount() { return IO_REG_COUNT; }

    /**
     * Returns the memory address of the last executed instruction
     */
    public int getLastInstructionAddress() { return mLastInstructionAddress; }

    /**
     * Accessor for Data Memory
     * @param index Data Memory location to read
     * @return Value in Data Memory location
     */
     public int getDataMemory(int index)
     {
            int retValue = 0;
            try { retValue = data_memory[index].intValue(); }
            catch (ArrayIndexOutOfBoundsException OOB) { }
            return retValue;
     }

    /**
      * Mutator for Data Memory
      * @param index Data Memory location to read
      * @param value Value to write to Data Memory location
      */
      public void setDataMemory(int index, int value)
      {
            try { data_memory[index].setValue(value); }
            catch (ArrayIndexOutOfBoundsException OOB) { }
      }

      /**
      * Accessor for Program Memory
      * @param index Program Memory location to read
      * @return Value in Program Memory location
      */
      public int getProgramMemory(int index)
      {
            int retValue = 0;
            try { retValue = program_memory[index]; }
            catch (ArrayIndexOutOfBoundsException OOB) { }
            return retValue;
      }
      /**
      * Mutator for Program Memory
      * @param index Program Memory location to write to
      * @param value Value to write to Program Memory
      */
      public void setProgramMemory(int index, int value)
      {
            try { program_memory[index] = value; }
            catch (ArrayIndexOutOfBoundsException OOB) { }
      }

     // Added: 22-7-03 Craig Eales.  Dump of Data Memory as String[].
     public String[] getDataMemoryArray()
     {
       String[] dMemory = new String[data_memory.length];
       for(int i=0;i<data_memory.length;i++)
       {
         dMemory[i]=data_memory[i].toHexString();
       }
       return dMemory;
     }

     // Added: 21-7-03 Craig Eales.  Dump of Prog Memory as int[].
     public String[] getProgMemoryArray()
     {
       String[] pMemory = new String[program_memory.length];
       for(int i=0;i<program_memory.length;i++)
       {
         pMemory[i]=Utils.hex(program_memory[i]);
       }
       return pMemory;
//       Integer x;
//       String[] pMemory = new String[2*program_memory.length];
//       for(int i=0;i<program_memory.length;i++)
//       {
//         x = new Integer(program_memory[i] & 0x000000FF);
//         pMemory[2*i]=(Integer.toHexString(x.intValue()));
//         x = new Integer((program_memory[i] & 0x0000FF00)>>8);
//         pMemory[2*i+1]=(Integer.toHexString(x.intValue()));
//       }
//       return pMemory;
     }


    /**
     * Accessor for Step Mode flag
     */
    public boolean getStepmode() { return mStepmode; }
    /**
     * Mutexor for Step Mode flag
     */
    public void setStepmode(boolean flag) { mStepmode = flag; }

        //----------------------------------------------
        // Index setting and getting
        //----------------------------------------------
        private void set_x(int RHS)
        {
                // R26 and R27 contain the values of index X
                try
                {
                        set_reg(26,Utils.get_lobyte(RHS));
                        set_reg(27,Utils.get_hibyte(RHS));
                }
                catch(InvalidRegisterException e) { }
        }

        private int get_x() { return get_reg(26) + (get_reg(27) << 8); }

        private void set_y(int RHS)
        {
                // R28 and R29 contain the values of index Y
                try
                {
                        set_reg(28,Utils.get_lobyte(RHS));
                        set_reg(29,Utils.get_hibyte(RHS));
                }
                catch(InvalidRegisterException e) { }
        }

        private int get_y() { return get_reg(28) + (get_reg(29) << 8); }

        private void set_z(int RHS)
        {
                // R30 and R31 contain the values of index Y
                try
                {
                        set_reg(30,Utils.get_lobyte(RHS));
                        set_reg(31,Utils.get_hibyte(RHS));
                }
                catch(InvalidRegisterException e) { }
        }

        private int get_z() { return get_reg(28) + (get_reg(29) << 8); }

        // Synchronized methods to bump the indexes UP
        private synchronized void inc_x() { set_x(get_x() + 1); }
        private synchronized void inc_y() { set_z(get_y() + 1); }
        private synchronized void inc_z() { set_z(get_z() + 1); }

        // Synchronized methods to bump the indexes DOWN
        private synchronized void dec_x() { set_x(get_x() - 1); }
        private synchronized void dec_y() { set_z(get_y() - 1); }
        private synchronized void dec_z() { set_z(get_z() - 1); }

        //----------------------------------------------
        // General purpose registers getting and setting
        //----------------------------------------------

        private void set_reg(int index, int RHS) throws InvalidRegisterException
        {
                if ((index < 0) || (index >= GENERAL_REG_COUNT))
                        throw new InvalidRegisterException("Invalid general register index of " + index + " was used.");
                else
                        data_memory[index].setValue(RHS);
        }

        private int get_reg(int index)
        {
                if ((index < 0) || (index >= GENERAL_REG_COUNT))
                        return 0;
                else
                        return data_memory[index].intValue();
        }

        //----------------------------------------------
        // I/O registers getting and setting
        //----------------------------------------------
      /**
      * Sets a value in an IO Register
      * @param index Number of IO Register to set
      * @param RHS Value to set - ONLY the lower 8 bits are used, sign bit is ignored
      */
        public void set_ioreg(int index, int RHS) throws InvalidRegisterException
        {
                if ((index < 0) || (index >= IO_REG_COUNT))
                    throw new InvalidRegisterException("Invalid i/o register index of " + index + " was used.");
                else
                   data_memory[index+IO_REG_COUNT].setValue(RHS);
        }

      /**
      * Returns a value for an IO Register
      * @param index Number of IO Register to read
      * @return Value in IO Register
      */
        public int get_ioreg(int index)
        {
                if ((index < 0) || (index >= IO_REG_COUNT))
                        return 0;
                else
                        return data_memory[index+IO_REG_COUNT].intValue();
        }

        //----------------------------------------------
        // Status Register methods
        //
        // Status register is stored in I/O register $3F (data memory location $5F)
        // The SREG bits are as follows:
        // Bit 7 (128) "I"	-	Global Interrupt Enable
        // Bit 6 (63)  "T"	-	Bit Copy Storage
        // Bit 5 (32)  "H"	-	Half Carry flag
        // Bit 4 (16)  "S"	-	Sign bit
        // Bit 3 (8)   "V"	-	Two's complement overflow flag
        // Bit 2 (4)	 "N"	-	Negative flag
        // Bit 1 (2)	 "Z"	-	Zero flag
        // Bit 0 (1)	 "C"	-	Carry flag
        //----------------------------------------------

        // Gets
        private int get_sreg() { return get_ioreg(0x3F); }
        protected boolean get_iflag() { return (Utils.bit7(get_ioreg(SREG))); }
        protected boolean get_tflag() { return (Utils.bit6(get_ioreg(SREG))); }
        protected boolean get_hflag() { return (Utils.bit5(get_ioreg(SREG))); }
        protected boolean get_sflag() { return (Utils.bit4(get_ioreg(SREG))); }
        protected boolean get_vflag() { return (Utils.bit3(get_ioreg(SREG))); }
        protected boolean get_nflag() { return (Utils.bit2(get_ioreg(SREG))); }
        protected boolean get_zflag() { return (Utils.bit1(get_ioreg(SREG))); }
        protected boolean get_cflag() { return (Utils.bit0(get_ioreg(SREG))); }

        // Sets
        private void set_sreg(int RHS)
        {
                try { set_ioreg(0x3F,RHS); }
                catch(InvalidRegisterException e) { }
        }

        // If RHS = True then the appropriate bit is activated
        // Else the bit is cleared
        protected void set_iflag(boolean RHS) { set_sreg(Utils.setbit(7,RHS, get_sreg())); }
        protected void set_tflag(boolean RHS) { set_sreg(Utils.setbit(6,RHS, get_sreg())); }
        protected void set_hflag(boolean RHS) { set_sreg(Utils.setbit(5,RHS, get_sreg())); }
        protected void set_sflag(boolean RHS) { set_sreg(Utils.setbit(4,RHS, get_sreg())); }
        protected void set_vflag(boolean RHS) { set_sreg(Utils.setbit(3,RHS, get_sreg())); }
        protected void set_nflag(boolean RHS) { set_sreg(Utils.setbit(2,RHS, get_sreg())); }
        protected void set_zflag(boolean RHS) { set_sreg(Utils.setbit(1,RHS, get_sreg())); }
    protected void set_cflag(boolean RHS) { set_sreg(Utils.setbit(0,RHS, get_sreg())); }

        //----------------------------------------------
        // Stack Pointer
      // This will be hardware or software, depending on the nature of the MCU
      // selected.
        //----------------------------------------------

        //private int get_sp() { return mStack.get_sp(); }

        //private void set_sp(int RHS) throws RuntimeException { mStack.set_sp(RHS); }

        //private void push(int value) throws RuntimeException { mStack.push(value); }

        //private int pop() throws RuntimeException { return mStack.pop(); }

        //----------------------------------------------
        // Resets
        //----------------------------------------------
    /**
     * Performs a power on reset
     */
        public void power_on_reset()
        {
                // Flush data memory
                for(int i=0;i<DATA_MEMORY_SIZE;i++) { data_memory[i] = new UByte(0);  }

            mPC = 0;
            mMBR = mMBR2 = 0;
                try
                {
                        for(int i=0;i<GENERAL_REG_COUNT;i++) { set_reg(i,0); }
                        for(int i=0;i<PROGRAM_MEMORY_SIZE;i++) { program_memory[i] = 0; }
                        for(int i=0;i<IO_REG_COUNT;i++) { set_ioreg(i,0); }
                }
                catch(InvalidRegisterException e) { }
                // Power on reset sets the PORF bit of the MCUSR
                try { set_ioreg(MCUSR,get_ioreg(MCUSR) | 0x01); }
                catch (InvalidRegisterException e) { }
        }

    /**
     * Performs an external reset
     */
        public void external_reset()
        {
                // Set EXTRF bit in MCUSR
                try { set_ioreg(MCUSR,get_ioreg(MCUSR) | 0x02); }
                catch (InvalidRegisterException e) { }

                // TO DO:  Handle an external reset
                //  This happens when the RESET pin of the atmel is set low for 50 ns or more
        }

    /**
     * Performs a watch dog reset
     */
        public void watchdog_reset()
        {
            if (!WATCHDOG)
                return;
                // TO DO:  Handle a watch dog reset
                //  This happens when the watch dog timer period expires and the watch dog is enabled
        }

        //----------------------------------------------
        // Fetch next instruction into memory buffer register
        //----------------------------------------------
        private void fetch() throws RuntimeException
        {
                boolean big_op;

                mLastInstructionAddress = mPC;

                if (mPC >= PROGRAM_MEMORY_SIZE)
                        throw new RuntimeException(Constants.Error(Constants.UPPER_MEMORY_LIMIT));

                mMBR = program_memory[mPC++];

                // These instructions are 32bits wide
                // If the current instruction matches these, read the second word
                big_op = ((mMBR & 0xfe0e) == 0x940c) || ((mMBR & 0xfe0e) == 0x940e) || ((mMBR & 0xfe0f) == 0x9000) || ((mMBR & 0xfe0f) == 0x9200);
                if (big_op)
                {
                        if (mPC >= PROGRAM_MEMORY_SIZE)
                                throw new RuntimeException(Constants.Error(Constants.UPPER_MEMORY_LIMIT));
                        mMBR2 = program_memory[mPC++];
                }
                else
                        mMBR2 = 0;
        }

    /**
     * Returns TRUE if the MCU is currently executing
     */
    public boolean isExecuting() { return mExecuting; }

    /**
     * Returns the last known Runtime Exception thrown by the run() method
     */
    public RuntimeException getLastRuntimeException() { return mLastRuntimeException; }

    // This abstract method should be overridden and implemented
    // for each processor type.  Most of them have their own
    // unique style for these anyway.
    protected abstract void HandleInterrupts();

    /**
     * Triggers an external interrupt for this MCU.
     * The External Interrupt number should be from 0 onwards.
     * If the MCU type has no external interrupts, or you supply
     * an invalid external interrupt number, this method will have no effect.
     */
    public void TriggerExternalInterrupt(int number)
    {
        if (number >= EXTERNAL_INTERRUPTS || number < 0)
            return;
        doExternalInterrupt(number);
    }

    // This abstract method should be overridden for anything
    // extending the Atmel class.
    protected abstract void doExternalInterrupt(int number);

	//----------------------------------------------
        // Execute one instruction
	//----------------------------------------------
	public void ExecuteNextInstruction() throws RuntimeException
        {
			if (get_iflag()) 
                  { 
                        HandleInterrupts();
                        // If an interrupt was triggered,
                        // push the return address onto the stack.
                        if (!get_iflag())
                        {
                              int lobyte = Utils.get_lobyte(mPC);
                              int hibyte = Utils.get_hibyte(mPC);
                              mStack.push(lobyte);
                              mStack.push(hibyte);
                        }
                  }
		fetch();

		switch (mMBR & 0xf000)
	    {
	    case 0x0000 :
			//======Add Without Carry=====
			if((mMBR & 0xfc00) == 0x0c00)
			    Perform_ADD();

			//======Compare with Carry=====
			else if((mMBR & 0xfc00) == 0x0400)
			    Perform_CPC();

			//======Fractional Multiply Unsigned=====
			else if((mMBR & 0xff88) == 0x0308)
			    Perform_FMUL();

			//======Fractional Multiply Signed=====
			else if((mMBR & 0xff88) == 0x0380)
			    Perform_FMULS();

			//======Logical Shift Left=====
			else if((mMBR & 0xfc00) == 0x0c00)
			    Perform_LSL();

			//======Copy Register Word=====
			else if((mMBR & 0xff00) == 0x0100)
			    Perform_MOVW();

			//======Multiply Signed=====
			else if((mMBR & 0xff00) == 0x0200)
			    Perform_MULS();

			//======Multiply Signed with Unsigned=====
			else if((mMBR & 0xff88) == 0x0300)
			    Perform_MULSU();

			//======No Operation=====
			else if(mMBR == 0x0)
			    Perform_NOP();

			//======Subtract with Carry=====
			else if((mMBR & 0xfc00) == 0x0800)
			    Perform_SBC();
			else
			    throw new RuntimeException(Constants.Error(Constants.INVALID_INSTRUCTION));
			break;
	    case 0x1000:
			//======Add With Carry=====
			if((mMBR & 0xfc00) == 0x1c00)
			    Perform_ADC();

			//======Compare=====
			else if((mMBR & 0xfc00) == 0x1400)
			    Perform_CP();

			//======Compare Skip If Equal=====
			else if((mMBR & 0xfc00) == 0x1000)
			    Perform_CPSE();

			//======Rotate Left through Carry=====
			else if((mMBR & 0xfc00) == 0x1c00)
			    Perform_ROL();

			//======Subtract without Carry=====
			else if((mMBR & 0xfc00) == 0x1800)
			    Perform_SUB();
			else
			    throw new RuntimeException(Constants.Error(Constants.INVALID_INSTRUCTION));
			break;
	    case 0x2000 :
			//======Logical AND=====
			if((mMBR & 0xfc00) == 0x2000)
			    Perform_AND();

			//======Exclusive Or=====
			else if((mMBR & 0xfc00) == 0x2400)
			    Perform_EOR();

			//======Copy Register=====
			else if((mMBR & 0xfc00) == 0x2c00)
			    Perform_MOV();

			//======Logical Or=====
			else if((mMBR & 0xfc00) == 0x2800)
			    Perform_OR();

			else
			    throw new RuntimeException(Constants.Error(Constants.INVALID_INSTRUCTION));
			break;
	    case 0x3000 :
		//=====Compare immediate=====
		Perform_CPI();
		break;

	    case 0x4000 :
		//======Subtract Immediate with Carry=====
		    Perform_SBCI();
		    break;
	    case 0x5000 :
		//======Subtract Immediate=====
		    Perform_SUBI();
		    break;
	    case 0x6000 :
		       Perform_ORI();
		       break;
	    case 0x7000 :
		//======Logical AND with Immediate=====
		    Perform_ANDI();
		    break;
	    case 0x8000 :
			//======Load Indirect from Data Space to Register using index Z - Z Unchanged=====
			if((mMBR & 0xfe0f) == 0x8000)
			    Perform_LD_IV();

			//======Load Indirect from Data Space to Register using index Z - Z Unchanged, Q displacement=====
			else if((mMBR & 0xd208) == 0x8000)
			    Perform_LD_VII();

			//======Load Indirect from Data Space to Register using Index Y - Y Unchanged=====
			else if((mMBR & 0xfe0f) == 0x8008)
			    Perform_LDD_I();

			//======Store Indirect From Register to Data Space using Index Y - Y Unchanged=====
			else if((mMBR & 0xfe0f) == 0x8208)
			    Perform_ST_IV();

			//======Store Indirect From Register to Data Space using Index Y - Y Unchanged, Q Displacement=====
			else if((mMBR & 0xd208) == 0x8208)
			    Perform_ST_VII();

			//======Store Indirect From Register to Data Space using Index Z - Z Unchanged=====
			else if((mMBR & 0xfe0f) == 0x8200)
			    Perform_ST_VIII();

			//======Store Indirect From Register to Data Space using Index Y - Y Unchanged, Q Displacement=====
			else if((mMBR & 0xd208) == 0x8200)
			    Perform_ST_XI();
			else
			    throw new RuntimeException(Constants.Error(Constants.INVALID_INSTRUCTION));
			break;
	    case 0x9000 :
			//======Add immediate to word=====
			if((mMBR & 0xff00) == 0x9600)
			    Perform_ADIW();

			//======Arithmatic shift right=====
			else if((mMBR & 0xfe0f) == 0x9405)
			    Perform_ASR();
			
			//======Bit Clear in SREG=====
			else if((mMBR & 0xff8f) == 0x9488)
			    Perform_BCLR();

			//======Bit set in SREG=====
			else if((mMBR & 0xff8f) == 0x9408)
			    Perform_BSET();

			//======Long Call to a Subroutine=====
			else if((mMBR & 0xfe0e) == 0x940e)
			    Perform_CALL();

			//======Clear bit in I/O Register=====
			else if((mMBR & 0xff00) == 0x9800)
			    Perform_CBI();

			//======One's Component=====
			else if((mMBR & 0xfe0f) == 0x9400)
			    Perform_COM();

			//======Decrement=====
			else if((mMBR & 0xfe0f) == 0x940a)
			    Perform_DEC();

			//======Extended Indirect Call to Subroutine=====
			else if(mMBR == 0x9519)
			    Perform_EICALL();

			//======Extended Indirect Jump=====
			else if(mMBR == 0x9419)
			    Perform_EIJMP();

			//======Extended Load Program Memory - R0 implied=====
			else if(mMBR == 0x95d8)
			    Perform_ELPM_I();

			//======Extended Load Program Memory - Rd, Z=====
			else if((mMBR & 0xfe0f) == 0x9006)
			    Perform_ELPM_II();

			//======Extended Load Program Memory - Rd, Z+=====
			else if((mMBR & 0xfe0f) == 0x9007)
			    Perform_ELPM_III();

			//======Fractional Multiply Signed with Unsigned=====
			else if((mMBR & 0xff00) == 0x9800)
			    Perform_FMULSU();

			//======Indirect Call to Subroutine=====
			else if(mMBR == 0x9509)
			    Perform_ICALL();
			
			//======Indirect Jump=====
			else if(mMBR == 0x9409)
			    Perform_IJMP();

			//======Break=====
			else if(mMBR == 0x9598)
			    Perform_BREAK();

			//======Increment=====
			else if((mMBR & 0xfe0f) == 0x9403)
			    Perform_INC();

			//======Jump=====
			else if((mMBR & 0xfe0e) == 0x940c)
			    Perform_JMP();

			//======Load Indirect from Data Space to Register using index X - X Unchanged=====
			else if((mMBR & 0xfe0f) == 0x900c)
			    Perform_LD_I();

			//======Load Indirect from Data Space to Register using index X - Post Incremented=====
			else if((mMBR & 0xfe0f) == 0x900d)
			    Perform_LD_II();

			//======Load Indirect from Data Space to Register using index X - Pre Decrement=====
			else if((mMBR & 0xfe0f) == 0x900e)
			    Perform_LD_III();

			//======Load Indriect from Data Space to Register using index Z - Post Increment=====
			else if((mMBR & 0xfe0f) == 0x9001)
			    Perform_LD_V();

			//======Load Indirect from Data Space to Register using index Z - Post Decrement=====
			else if((mMBR & 0xfe0f) == 0x9002)
			    Perform_LD_VI();

			//======Load Indirect from Data Space to Register using Index Y - Post Increment=====
			else if((mMBR & 0xfe0f) == 0x9009)
			    Perform_LDD_II();

			//======Load Indirect from Data Space to Register using Index Y - Pre Decrement=====
			else if((mMBR & 0xfe0f) == 0x900a)
			    Perform_LDD_III();

			//======Load Indirect from Data Space to Register using Index Y - Y Unchanged, Q Displacement=====
			else if((mMBR & 0xff00) == 0x9800)
			    Perform_LDD_IV();

			//======Load Direct from Data Space=====
			else if((mMBR & 0xfe0f) == 0x9000)
			    Perform_LDS();

			//======Load Program Memory - Z unchanged, R0 Implied=====
			else if(mMBR == 0x95c8)
			    Perform_LPM_I();

			//======Load Program Memory - Z unchanged=====
			else if((mMBR & 0xfe0f) == 0x9004)
			    Perform_LPM_II();

			//======Load Program Memory - Z Post Increment=====
			else if((mMBR & 0xfe0f) == 0x9005)
			    Perform_LPM_III();

			//======Logical Shift Right=====
			else if((mMBR & 0xfe0f) == 0x9406)
			    Perform_LSR();

			//======Multiply Unsigned=====
			else if((mMBR & 0xfc00) == 0x9c00)
			    Perform_MUL();

			//======Two's complement=====
			else if((mMBR & 0xfe0f) == 0x9401)
			    Perform_NEG();

			//======Pop Register from Stack=====
			else if((mMBR & 0xfe0f) == 0x900f)
			    Perform_POP();
			
			//======Push Register on Stack=====
			else if((mMBR & 0xfe0f) == 0x920f)
			    Perform_PUSH();

			//======Return from Subroutine=====
			else if(mMBR == 0x9508)
			    Perform_RET();

			//======Return from Interrupt=====
			else if(mMBR == 0x9518)
			    Perform_RETI();

			//======Rotate Right through Carry=====
			else if((mMBR & 0xfe0f) == 0x9407)
			    Perform_ROR();

			//======Set Bit in I/O Register=====
			else if((mMBR & 0xff00) == 0x9a00)
			    Perform_SBI();

			//======Skip if Bit in I/O Register is Cleared=====
			else if((mMBR & 0xff00) == 0x9900)
			    Perform_SBIC();

			//======Skip if Bit in I/O Register is Set=====
			else if((mMBR & 0xff00) == 0x9b00)
			    Perform_SBIS();

			//======Subtract Immediate from Word=====
			else if((mMBR & 0xff00) == 0x9700)
			    Perform_SBIW();

			//======Sets Sleep Mode=====
			else if(mMBR == 0x9588)
			    Perform_SLEEP();

			//======Store Program Memory=====
			else if(mMBR == 0x95e8)
			    Perform_SPM();

			//======Store Indirect From Register to Data Space using Index X - X Unchanged=====
			else if((mMBR & 0xfe0f) == 0x920c)
			    Perform_ST_I();

			//======Store Indirect From Register to Data Space using Index X - Post Increment=====
			else if((mMBR & 0xfe0f) == 0x920d)
			    Perform_ST_II();

			//======Store Indirect From Register to Data Space using Index X - Pre Decrement=====
			else if((mMBR & 0xfe0f) == 0x920e)
			    Perform_ST_III();

			//======Store Indirect From Register to Data Space using Index Y - Post Increment=====
			else if((mMBR & 0xfe0f) == 0x9209)
			    Perform_ST_V();

			//======Store Indirect From Register to Data Space using Index Y - Pre Decrement=====
			else if((mMBR & 0xfe0f) == 0x920a)
			    Perform_ST_VI();

			//======Store Indirect From Register to Data Space using Index Z - Post Increment=====
			else if((mMBR & 0xfe0f) == 0x9201)
			    Perform_ST_IX();

			//======Store Indirect From Register to Data Space using Index Z - Pre Decrement=====
			else if((mMBR & 0xfe0f) == 0x9202)
			    Perform_ST_X();

			//======Store Direct to Data Space=====
			else if((mMBR & 0xfe0f) == 0x9200)
			    Perform_STS();

			//======Swap Nibbles=====
			else if((mMBR & 0xfe0f) == 0x9402)
			    Perform_SWAP();

			//======Watchdog Reset=====
			else if(mMBR == 0x95a8)
			    Perform_WDR();
			else
			    throw new RuntimeException(Constants.Error(Constants.INVALID_INSTRUCTION));
			break;
	    case 0xb000 :
			//======Load an I/O Location to Register=====
			if((mMBR & 0xf800) == 0xb000)
			    Perform_IN();
			//======Store Register to I/O Location=====
			else if((mMBR & 0xf800) == 0xb800)
			    Perform_OUT();
			else
			    throw new RuntimeException(Constants.Error(Constants.INVALID_INSTRUCTION));
			break;
	    case 0xc000 :
		//======Relative Jump=====
		    Perform_RJMP();
		    break;
	    case 0xd000 :
		//======Relative Call to Subroutine=====
		    Perform_RCALL();
		    break;
	    case 0xe000 :
		//======Load Immediate=====
		    Perform_LDI();
		    break;
	    case 0xf000 :
			//======Bit load from the T Flag in SREG to a bit in Register=====
			if((mMBR & 0xfe08) == 0xf800)
			    Perform_BLD();

			//======Branch if bit in SREG is cleared=====
			else if((mMBR & 0xfc00) == 0xf400)
			    Perform_BRBC();

			//======Branch if bit in SREG is set=====
			else if((mMBR & 0xfc00) == 0xf000)
			    Perform_BRBS();

			//======Bit Store from Bit in Register to T Flag in SREG=====
			else if((mMBR & 0xfe08) == 0xfa00)
			    Perform_BST();

			//======Skip if Bit in Register is Cleared=====
			else if((mMBR & 0xfe08) == 0xfc00)
			    Perform_SBRC();

			//======Skip if Bit in Register is Set=====
			else if((mMBR & 0xfe08) == 0xfe00)
			    Perform_SBRS();
			else
			    throw new RuntimeException(Constants.Error(Constants.INVALID_INSTRUCTION));

			break;
	    default :
			    throw new RuntimeException(Constants.Error(Constants.INVALID_INSTRUCTION));
	    }

	}

	//========================================================
	// ACTUAL INSTRUCTIONS PERFORMED HERE
	//========================================================

	private void Perform_ADD()
	{
		d = GetOperand_XXXXXXX11111XXXX();
		r = GetOperand_XXXXXX1XXXXX1111();

		dvalue = get_reg(d);
		rvalue = get_reg(r);
		result = dvalue + rvalue;

		currentInstruction = "add r"+Utils.hex(d)+", r"+Utils.hex(r);

		set_hflag(CarryFromBit3(dvalue, rvalue, result));
		set_nflag(Utils.bit7(result));
		set_vflag(Overflow(dvalue, rvalue, result));
		set_sflag(get_nflag() ^ get_vflag());
		set_zflag(result == 0);
		set_cflag(CarryFromResult(dvalue, rvalue, result));

		try {set_reg(d,result); }
		catch (InvalidRegisterException e) { }
		return;
	}

	private void Perform_ADC()
	{
		d = GetOperand_XXXXXXX11111XXXX();
		r = GetOperand_XXXXXX1XXXXX1111();

		dvalue = get_reg(d);
		rvalue = get_reg(r);
		result = dvalue + rvalue;
		if (get_cflag()) result+=1;

		currentInstruction = "adc r"+Utils.hex(d)+", r"+Utils.hex(r);

		set_hflag(CarryFromBit3(dvalue, rvalue, result));
		set_nflag(Utils.bit7(result));
		set_vflag(Overflow(dvalue, rvalue, result));
		set_sflag(get_nflag() ^ get_vflag());
		set_zflag(result == 0);
		set_cflag(CarryFromResult(dvalue, rvalue, result));

		try {set_reg(d,result); }
		catch (InvalidRegisterException e) { }
		return;
	}

	private void Perform_ADIW()
	{
	    d = GetOperand_XXXXXXXXXX11XXXX();
	    K = GetOperand_XXXXXXXX11XX1111();

	    dvalue = get_reg(d);

	    result = dvalue + K;

	    currentInstruction = "adiw r"+Utils.hex(d)+", "+Utils.hex(K);
	    
	    int lobyte = Utils.get_lobyte(result);
	    int hibyte = Utils.get_hibyte(result);

	    set_cflag(!Utils.bit15(result) && Utils.bit7(hibyte));
	    set_zflag(result==0);
	    set_nflag(Utils.bit15(result));
	    set_vflag(!Utils.bit7(hibyte) && Utils.bit15(result));
	    set_sflag(get_nflag() ^ get_vflag());

	    try 
	    { 
			set_reg(d,hibyte);
			set_reg(d+1,lobyte);
	    }
	    catch (InvalidRegisterException e) { };
	    return;
	}

	private void Perform_AND()
	{
	    d = GetOperand_XXXXXXX11111XXXX();
	    r = GetOperand_XXXXXX1XXXXX1111();

	    dvalue = get_reg(d);
	    rvalue = get_reg(r);

	    result = dvalue & rvalue;

	    currentInstruction = "and r"+Utils.hex(d)+", r"+Utils.hex(r);
            // If both registers are the same, this may be a TST instruction
          if (d == r)
            currentInstruction += " (TST r"+Utils.hex(d)+")";

	    set_vflag(false);
	    set_sflag(get_nflag() ^ get_vflag());
	    set_nflag(Utils.bit7(result));
	    set_zflag(result ==0);

	    try {set_reg(d,result); }
	    catch (InvalidRegisterException e) { }
		return;
	}

	private void Perform_ANDI()
	{
	    d = GetOperand_XXXXXXXX1111XXXX();
	    K = GetOperand_XXXX1111XXXX1111();

	    dvalue = get_reg(d);
	    result = dvalue & K;

	    currentInstruction = "andi r"+Utils.hex(d)+", "+Utils.hex(K);

	    set_vflag(false);
	    set_nflag(Utils.bit7(result));
	    set_sflag(get_nflag() ^ get_vflag());
	    set_zflag(result == 0);

	    try { set_reg(d,result); }
	    catch (InvalidRegisterException e) { }
		return;
	}

	private void Perform_ASR()
	{
	    d = GetOperand_XXXXXXX11111XXXX();

	    dvalue = get_reg(d);

	    currentInstruction = "asr r"+Utils.hex(d);

	    int bit7 = dvalue & 0x80;
	    boolean bit1 = (dvalue & 0x01) > 0;

	    result = (dvalue >> 1) & bit7;

	    set_cflag(bit1);
	    set_zflag(result==0);
	    set_nflag(Utils.bit7(result));
	    set_vflag(get_nflag() ^ get_cflag());
	    set_sflag(get_nflag() ^ get_vflag());

	    try { set_reg(d,result); }
	    catch (InvalidRegisterException e) { }
		return;
	}

	private void Perform_BCLR()
	{
	    s = GetOperand_XXXXXXXXX111XXXX();

	    currentInstruction = "bclr "+Utils.hex(s);
            // Depending on value of s, the instruction is more likely to
            // be one of these ...
            switch (s)
            {
                  case 0 :    currentInstruction += " (CLC)";
                              break;
                  case 1 :    currentInstruction += " (CLZ)";
                              break;
                  case 2 :    currentInstruction += " (CLN)";
                              break;
                  case 3 :    currentInstruction += " (CLV)";
                              break;
                  case 4 :    currentInstruction += " (CLS)";
                              break;
                  case 5 :    currentInstruction += " (CLH)";
                              break;
                  case 6 :    currentInstruction += " (CLT)";
                              break;
                  case 7 :    currentInstruction += " (CLI)";
                              break;
            }

	    int bitvalue = 255 - (1 << s);

	    set_sreg(get_sreg() & bitvalue);
		return;
	}

	private void Perform_BLD()
	{
	    d = GetOperand_XXXXXXX11111XXXX();
	    b = GetOperand_XXXXXXXXXXXXX111();

	    currentInstruction = "bld r"+Utils.hex(d)+", "+Utils.hex(b);

	    dvalue = get_reg(d);
	    int bitvalue = 1 << b;
	    if (get_tflag())
			dvalue = dvalue & bitvalue;
	    else
			dvalue = dvalue & (255 - bitvalue);
	    return;
	}

	private void Perform_BRBC()
	{
	    k = GetOperand_XXXXXX1111111XXX();
	    s = GetOperand_XXXXXXXXXXXXX111();
	    int newPC = mPC + branch_offset(k);

	    currentInstruction = "brbc "+Utils.hex(s)+", "+Utils.hex(newPC);
            switch (s)
            {
                  case 0 :    currentInstruction += " (BRCC "+Utils.hex(newPC)+")";
                              break;
                  case 1 :    currentInstruction += " (BRNE "+Utils.hex(newPC)+")";
                              break;
                  case 2 :    currentInstruction += " (BRPL "+Utils.hex(newPC)+")";
                              break;
                  case 3 :    currentInstruction += " (BRVC "+Utils.hex(newPC)+")";
                              break;
                  case 4 :    currentInstruction += " (BRGE "+Utils.hex(newPC)+")";
                              break;
                  case 5 :    currentInstruction += " (BRHC "+Utils.hex(newPC)+")";
                              break;
                  case 6 :    currentInstruction += " (BRTC "+Utils.hex(newPC)+")";
                              break;
                  case 7 :    currentInstruction += " (BRID "+Utils.hex(newPC)+")";
                              break;
            }

	    int bitvalue = 1 << s;
	    if ((get_sreg() & bitvalue) == 0)
			mPC = newPC;
	    return;
	}

	private void Perform_BRBS()
	{
	    k = GetOperand_XXXXXX1111111XXX();
	    s = GetOperand_XXXXXXXXXXXXX111();

            int newPC = mPC + branch_offset(k);
	    currentInstruction = "brbs "+Utils.hex(s)+", "+Utils.hex(newPC);
            switch (s)
            {
                  case 0 :    currentInstruction += " (BRCS "+Utils.hex(newPC)+")";
                              break;
                  case 1 :    currentInstruction += " (BREQ "+Utils.hex(newPC)+")";
                              break;
                  case 2 :    currentInstruction += " (BRMI "+Utils.hex(newPC)+")";
                              break;
                  case 3 :    currentInstruction += " (BRVS "+Utils.hex(newPC)+")";
                              break;
                  case 4 :    currentInstruction += " (BRLT "+Utils.hex(newPC)+")";
                              break;
                  case 5 :    currentInstruction += " (BRHS "+Utils.hex(newPC)+")";
                              break;
                  case 6 :    currentInstruction += " (BRTS "+Utils.hex(newPC)+")";
                              break;
                  case 7 :    currentInstruction += " (BRIE "+Utils.hex(newPC)+")";
                              break;
            }

	    int bitvalue = 1 << s;
	    if ((get_sreg() & bitvalue) == bitvalue)
			mPC = newPC;
	    return;
	}

	private void Perform_BREAK() throws RuntimeException
	{
	    currentInstruction = "break";
	    // TO DO:  Is this a BREAK or a NOP?
	    throw new RuntimeException(Constants.Error(Constants.BREAK_MODE));
	}

	private void Perform_BSET()
	{
		s = GetOperand_XXXXXXXXX111XXXX();

		currentInstruction = "bset "+Utils.hex(s);
            switch (s)
            {
                  case 0 :    currentInstruction += " (SEC)";
                              break;
                  case 1 :    currentInstruction += " (SEZ)";
                              break;
                  case 2 :    currentInstruction += " (SEN)";
                              break;
                  case 3 :    currentInstruction += " (SEV)";
                              break;
                  case 4 :    currentInstruction += " (SES)";
                              break;
                  case 5 :    currentInstruction += " (SEH)";
                              break;
                  case 6 :    currentInstruction += " (SET)";
                              break;
                  case 7 :    currentInstruction += " (SEI)";
                              break;
            }

		int bitvalue = 1 << s;

		set_sreg(get_sreg() | bitvalue);
		return;
	}

	private void Perform_BST()
	{
		d = GetOperand_XXXXXXX11111XXXX();
		b = GetOperand_XXXXXXXXXXXXX111();

		currentInstruction = "bst r"+Utils.hex(d)+", "+Utils.hex(b);

		dvalue = get_reg(d);
		int bitvalue = 1 << b;
		if ((dvalue & bitvalue) == bitvalue)
			set_tflag(true);
		else
			set_tflag(false);
		return;
	}

	private void Perform_CALL() throws RuntimeException
	{
	    try
	    {
			// NOTE!  This bit is not strictly correct!
			// The AVR would actually push mPC + 2 onto the stack.
			// However, we increment the PC during the fetch phase.
			int lobyte = Utils.get_lobyte(mPC);
			int hibyte = Utils.get_hibyte(mPC);
			mStack.push(lobyte);
			mStack.push(hibyte);
		
			int k = GetOperand_XXXXXXX11111XXX1();
			currentInstruction = "call "+Utils.hex(k + mMBR2);
			mPC = k + mMBR2;
	    }
	    catch (RuntimeException e) { throw e; }
  	    return;
	}

	private void Perform_CBI()
	{
	    A = GetOperand_XXXXXXXX11111XXX();
	    b = GetOperand_XXXXXXXXXXXXX111();
	    Avalue = get_ioreg(A);

	    currentInstruction = "cbi "+Utils.hex(A)+", "+Utils.hex(b);

	    int bitmask = 255 - (1 << b);
	    try { set_ioreg(A,(byte)(Avalue & bitmask)); }
	    catch (InvalidRegisterException e) { }
	    return;
	}

	private void Perform_COM()
	{
	    d = GetOperand_XXXXXXX11111XXXX();
	    dvalue = get_reg(d);
	    result = 255 - dvalue;

	    currentInstruction = "com r"+Utils.hex(d);
	    
	    set_sflag(get_nflag() ^ get_vflag());
	    set_vflag(false);
	    set_nflag(Utils.bit7(result));
	    set_zflag(result==0);
	    set_cflag(true);
	    try { set_reg(d,result); }
	    catch (InvalidRegisterException e) { }
	    return;
	}

	private void Perform_CP()
	{
		d = GetOperand_XXXXXXX11111XXXX();
		r = GetOperand_XXXXXX1XXXXX1111();

		currentInstruction = "cp r"+Utils.hex(d)+", r"+Utils.hex(r);

		dvalue = get_reg(d);
		rvalue = get_reg(r);

		result = dvalue - rvalue;

		set_hflag((!Utils.bit3(dvalue) && Utils.bit3(rvalue)) || (Utils.bit3(rvalue) && Utils.bit3(result)) || (Utils.bit3(result) && !Utils.bit3(dvalue)));
		set_sflag(get_nflag() ^ get_vflag());
		set_vflag((Utils.bit7(dvalue) && !Utils.bit7(rvalue) && Utils.bit7(result)) || (!Utils.bit7(dvalue) && Utils.bit7(rvalue) && Utils.bit7(result)));
		set_nflag(Utils.bit7(result));
		set_zflag(result==0);
		set_cflag((!Utils.bit7(dvalue) && Utils.bit7(rvalue)) || (Utils.bit7(rvalue) && Utils.bit7(result)) || (Utils.bit7(result) && !Utils.bit7(dvalue)));
		return;
	}

	private void Perform_CPC()
	{
		d = GetOperand_XXXXXXX11111XXXX();
		r = GetOperand_XXXXXX1XXXXX1111();

		dvalue = get_reg(d);
		rvalue = get_reg(r);
		result = dvalue - rvalue;

		currentInstruction = "cpc r"+Utils.hex(d)+", r"+Utils.hex(r);

		if (get_cflag())
			result--;

		set_hflag((!Utils.bit3(dvalue) && Utils.bit3(rvalue)) || (Utils.bit3(rvalue) && Utils.bit3(result)) || (Utils.bit3(result) && !Utils.bit3(dvalue)));
		set_sflag(get_nflag() ^ get_vflag());
		set_vflag((Utils.bit7(dvalue) && !Utils.bit7(rvalue) && Utils.bit7(result)) || (!Utils.bit7(dvalue) && Utils.bit7(rvalue) && Utils.bit7(result)));
		set_nflag(Utils.bit7(result));
		set_zflag(result==0);
		set_cflag((!Utils.bit7(dvalue) && Utils.bit7(rvalue)) || (Utils.bit7(rvalue) && Utils.bit7(result)) || (Utils.bit7(result) && !Utils.bit7(dvalue)));
		return;
	}

	private void Perform_CPI()
	{
	    K = GetOperand_XXXX1111XXXX1111();
	    d = GetOperand_XXXXXXXX1111XXXX();

	    dvalue = get_reg(d);
	    result = dvalue - K;

	    currentInstruction = "cpi r"+Utils.hex(d)+", "+Utils.hex(K);

	    set_hflag(CarryFromBit3(dvalue, (byte)K));
	    set_sflag(get_nflag() ^ get_vflag());
	    set_vflag((Utils.bit7(dvalue) && !Utils.bit7(K) && !Utils.bit7(result)) || (!Utils.bit7(dvalue) && Utils.bit7(K) && Utils.bit7(result)));
	    set_nflag(Utils.bit7(result));
	    set_zflag(result==0);
	    set_cflag((!Utils.bit7(dvalue) && Utils.bit7(K)) || (Utils.bit7(K) && Utils.bit7(result)) || (Utils.bit7(result) && !Utils.bit7(dvalue)));
	    return;
	}

	private void Perform_CPSE() throws RuntimeException
	{
	    d = GetOperand_XXXXXXX11111XXXX();
	    r = GetOperand_XXXXXX1XXXXX1111();

	    dvalue = get_reg(d);
	    rvalue = get_reg(r);

	    currentInstruction = "cpse r"+Utils.hex(d)+", r"+Utils.hex(r);

	    if (dvalue == rvalue)
	        fetch();
	    return;
	}

	private void Perform_DEC()
	{
	    d = GetOperand_XXXXXXX11111XXXX();

	    dvalue = get_reg(d);

	    result = dvalue--;

	    currentInstruction = "dec r"+Utils.hex(d);

	    set_sflag(get_nflag() ^ get_vflag());
	    set_vflag((result & 0x80)==0x00 && (result & 0x7F)==0x7f);
	    set_nflag(Utils.bit7(result));
	    set_zflag(result==0);
	    try { set_reg(d,dvalue); }
	    catch (InvalidRegisterException e) { }
	    return;
	}

	private void Perform_EICALL()
	{
		currentInstruction = "eicall";
	    // NOTE!!!!
	    // This instruction is only available to machines with
	    // 22 bit (I.E:  about 8M of program memory)
		// TO DO:  Implement
		return;
	}

	private void Perform_EIJMP()
	{
		currentInstruction = "eijmp";
	    // Ditto for the above.  Only supported by machines with
	    // 22 bits.
		// TO DO:  Implement
		return;
	}
	private void Perform_ELPM_I()
	{
		// TO DO:  Implement
		return;
	}
	private void Perform_ELPM_II()
	{
		// TO DO:  Implement
		return;
	}
	private void Perform_ELPM_III()
	{
		// TO DO:  Implement
		return;
	}

	private void Perform_EOR()
	{
		d = GetOperand_XXXXXXX11111XXXX();
		r = GetOperand_XXXXXX1XXXXX1111();

		dvalue = get_reg(d);
		rvalue = get_reg(r);
		result = dvalue ^ rvalue;

		currentInstruction = "eor r"+Utils.hex(d)+", r"+Utils.hex(r);
            // If doing an EOR on the same register, this is really a CLR instruction
		if (d == r)
		    currentInstruction += " (CLR r"+Utils.hex(d)+")";
            
		set_sflag(get_nflag() ^ get_vflag());
		set_vflag(false);
		set_nflag(Utils.bit7(result));
		set_zflag(result==0);
		try { set_reg(d,result); }
		catch (InvalidRegisterException e) { }
		return;
	}
	private void Perform_FMUL()
	{
		// TO DO:  Implement
		return;
	}
	private void Perform_FMULS()
	{
		// TO DO:  Implement
		return;
	}
	private void Perform_FMULSU()
	{
		// TO DO:  Implement
		return;
	}
	private void Perform_ICALL()
	{
		currentInstruction = "icall";
		// TO DO:  Implement
		return;
	}
	private void Perform_IJMP()
	{
		currentInstruction = "ijmp";
		// TO DO:  Implement
		return;
	}
	private void Perform_IN()
	{
	    d = GetOperand_XXXXXXX11111XXXX();
	    A = GetOperand_XXXXX11XXXXX1111();

	    Avalue = get_ioreg(A);

	    currentInstruction = "in r"+Utils.hex(d)+", "+Utils.hex(A);

	    try { set_reg(d,Avalue); }
	    catch (InvalidRegisterException e) { }

		return;
	}

	private void Perform_INC()
	{
	    d = GetOperand_XXXXXXX11111XXXX();
	    dvalue = get_reg(d);
	    result = dvalue + 1;

	    currentInstruction = "inc r"+Utils.hex(d);
	    
	    set_sflag(get_nflag() ^ get_vflag());
	    set_vflag(dvalue == 0x7f);
	    set_nflag(Utils.bit7(result));
	    set_zflag(result ==0);
	    try { set_reg(d,result); }
	    catch (InvalidRegisterException e) { }
	    return;
	}

	private void Perform_JMP()
	{
	    mPC = GetOperand_XXXXXXX11111XXX1() + mMBR2;
          currentInstruction = "jmp "+Utils.hex(mPC);
	    return;
	}

	private void Perform_LD_I()
	{
		d = GetOperand_XXXXXXX11111XXXX();
		currentInstruction = "ld r"+Utils.hex(d)+", x";
		try { set_reg(d,data_memory[get_x()].intValue()); }
		catch (InvalidRegisterException e) { }
		return;
	}

	private void Perform_LD_II()
	{
		d = GetOperand_XXXXXXX11111XXXX();
		currentInstruction = "ld r"+Utils.hex(d)+", x+";
		try { set_reg(d,data_memory[get_x()].intValue()); }
		catch (InvalidRegisterException e) { }
		inc_x();
		return;
	}

	private void Perform_LD_III()
	{
		dec_x();
		d = GetOperand_XXXXXXX11111XXXX();
		currentInstruction = "ld r"+Utils.hex(d)+", -x";
		try { set_reg(d,data_memory[get_x()].intValue()); }
		catch (InvalidRegisterException e) { }
		return;
	}

	private void Perform_LD_IV()
	{
		d = GetOperand_XXXXXXX11111XXXX();
		currentInstruction = "ld r"+Utils.hex(d)+", z";
		try { set_reg(d,data_memory[get_z()].intValue()); }
		catch (InvalidRegisterException e) { }
		return;
	}
	private void Perform_LD_V()
	{
		d = GetOperand_XXXXXXX11111XXXX();
		currentInstruction = "ld r"+Utils.hex(d)+", z+";
		try { set_reg(d,data_memory[get_z()].intValue()); }
		catch (InvalidRegisterException e) { }
		inc_z();
		return;
	}
	private void Perform_LD_VI()
	{
		dec_z();
		d = GetOperand_XXXXXXX11111XXXX();
		currentInstruction = "ld r"+Utils.hex(d)+", -z";
		try { set_reg(d,data_memory[get_z()].intValue()); }
		catch (InvalidRegisterException e) { }
		return;
	}

	private void Perform_LD_VII()
	{
		q = GetOperand_XX1X11XXXXXXX111();
		d = GetOperand_XXXXXXX11111XXXX();
		currentInstruction = "ld r"+Utils.hex(d)+", z+q";
		try { set_reg(d,data_memory[get_z() + q].intValue()); }
		catch (InvalidRegisterException e) { }
		return;
	}

	private void Perform_LDD_I()
	{
		d = GetOperand_XXXXXXX11111XXXX();
            currentInstruction="ld r"+Utils.hex(d)+", y";
		try { set_reg(d,data_memory[get_y()].intValue()); }
		catch (InvalidRegisterException e) { }
		return;
	}

	private void Perform_LDD_II()
	{
		d = GetOperand_XXXXXXX11111XXXX();
            currentInstruction="ld r"+Utils.hex(d)+", y+";
		try { set_reg(d,data_memory[get_y()].intValue()); }
		catch (InvalidRegisterException e) { }
		inc_y();
		return;
	}
	private void Perform_LDD_III()
	{
		d = GetOperand_XXXXXXX11111XXXX();
		dec_y();
            currentInstruction="ld r"+Utils.hex(d)+", -y";
		try { set_reg(d,data_memory[get_y()].intValue()); }
		catch (InvalidRegisterException e) { }
		return;
	}
	private void Perform_LDD_IV()
	{
		q = GetOperand_XX1X11XXXXXXX111();
		d = GetOperand_XXXXXXX11111XXXX();
            currentInstruction="ld r"+Utils.hex(d)+", y+q";
		try { set_reg(d,data_memory[get_y() + q].intValue()); }
		catch (InvalidRegisterException e) { }
		return;
	}

	private void Perform_LDI()
	{
		d = GetOperand_XXXXXXXX1111XXXX();
		K = GetOperand_XXXX1111XXXX1111();

		currentInstruction = "ldi r"+Utils.hex(d)+", "+Utils.hex(K);
		try { set_reg(d,K); }
		catch (InvalidRegisterException e) { } 
 		return;
	}

	private void Perform_LDS()
	{
		d = GetOperand_XXXXXXX11111XXXX();
		k = mMBR2;

		currentInstruction = "lds r"+Utils.hex(d)+", "+Utils.hex(k);

		try { set_reg(d,data_memory[k].intValue()); }
		catch (InvalidRegisterException e) { }
		return;
	}
	private void Perform_LPM_I()
	{
		// Note:  the z index is used to indicate
		// which BYTE is read from program memory.
		// This makes the LSB of the zindex work a bit
		// like a flag - 0 means get the low byte, 1 means get the high.
		// Thus, to find the program memory location, we need to rotate
		// the bits back by one (or, divide it by 2 if you like)
		int zindex = get_z();
		int wordlocation = zindex >> 1;
		currentInstruction = "lpm (LPM r0,z)";
		try
		{
			if (Utils.bit0(zindex))
				set_reg(0,(program_memory[wordlocation] & 0xff00) >> 8);
			else
				set_reg(0,program_memory[wordlocation] & 0x00ff);
		}
		catch (InvalidRegisterException e) { }				
		return;
	}

	private void Perform_LPM_II()
	{
		// See notes for LPM_I
		int zindex = get_z();
		int d = GetOperand_XXXXXXX11111XXXX();
		int wordlocation = zindex >> 1;

		currentInstruction = "lpm r"+Utils.hex(d)+", z";

		try
		{
			if (Utils.bit0(zindex))
				set_reg(d,(program_memory[wordlocation] & 0xff00) >> 8);
			else
				set_reg(d,program_memory[wordlocation] & 0x00ff);
		}
		catch (InvalidRegisterException e) { }				
		return;
	}

	private void Perform_LPM_III()
	{
		// See notes for LPM_I
		int zindex = get_z();
		int d = GetOperand_XXXXXXX11111XXXX();
		int wordlocation = zindex >> 1;

		currentInstruction = "lpm r"+Utils.hex(d)+", z+";

		try
		{
			if (Utils.bit0(zindex))
				set_reg(d,(program_memory[wordlocation] & 0xff00) >> 8);
			else
				set_reg(d,program_memory[wordlocation] & 0x00ff);
		}
		catch (InvalidRegisterException e) { }				
		inc_z();
		return;
	}

	private void Perform_LSL()
	{
		d = GetOperand_XXXXXXX11111XXXX();

		dvalue = get_reg(d);

		currentInstruction = "lsl r"+Utils.hex(d);

		// Bit 7 gets shifted into carry flag
		set_cflag(Utils.bit7(dvalue));

		// Bit 0 gets set to 0
		dvalue = (dvalue << 1) & 0xfe;

		set_vflag(get_nflag() ^ get_cflag());
		set_sflag(get_nflag() ^ get_vflag());
		set_nflag(false);
		set_zflag(dvalue == 0);

		try { set_reg(d,dvalue); }
		catch (InvalidRegisterException e) { };		
		return;
	}

	private void Perform_LSR()
	{
		d = GetOperand_XXXXXXX11111XXXX();

		dvalue = get_reg(d);

		currentInstruction = "lsr r"+Utils.hex(d);

		// Bit 0 gets shifted into carry flag
		set_cflag(Utils.bit0(dvalue));

		// Bit 7 gets set to 0
		dvalue = (dvalue >> 1) & 0x7f;

		set_vflag(get_nflag() ^ get_cflag());
		set_sflag(get_nflag() ^ get_vflag());
		set_nflag(false);
		set_zflag(dvalue == 0);

		try { set_reg(d,dvalue); }
		catch (InvalidRegisterException e) { };		
		return;
	}

	private void Perform_MOV()
	{
		d = GetOperand_XXXXXXX11111XXXX();
		r = GetOperand_XXXXXX1XXXXX1111();

		currentInstruction = "mov r"+Utils.hex(d)+", r"+Utils.hex(r);
            // Bit of an easter egg.  Humour me!
            if (d == r)
                  currentInstruction += " ummm ... kind of a silly instruction, isn't it?!?";
		rvalue = get_reg(r);
		try { set_reg(d,rvalue); }
		catch (InvalidRegisterException e) { }
		return;
	}

	private void Perform_MOVW()
	{
		d = GetOperand_XXXXXXXX1111XXXX();
		r = GetOperand_XXXXXXXXXXXX1111();

		currentInstruction = "movw r"+Utils.hex(d)+", r"+Utils.hex(r);
	
		try
		{
			set_reg(d,get_reg(r));
			set_reg(d+1,get_reg(r+1));
		}
		catch (InvalidRegisterException e) { }
		return;
	}

	private void Perform_MUL()
	{
	    d = GetOperand_XXXXXXX11111XXXX();
	    r = GetOperand_XXXXXX1XXXXX1111();

	    currentInstruction = "mul r"+Utils.hex(d)+", r"+Utils.hex(r);

	    result = d * r;

	    int lobyte = Utils.get_lobyte(result);
	    int hibyte = Utils.get_hibyte(result);
	    set_cflag(Utils.bit15(result));
	    set_zflag(result==0);
	    try
	    {
			set_reg(0,hibyte);
			set_reg(1,lobyte);
	    }
	    catch (InvalidRegisterException e) { }
	    return;
	}

	private void Perform_MULS()
	{
		// TO DO:  Implement
		return;
	}

	private void Perform_MULSU()
	{
		// TO DO:  Implement
		return;
	}
	private void Perform_NEG()
	{
	    d = GetOperand_XXXXXXX11111XXXX();
	    dvalue = get_reg(d);

	    currentInstruction = "neg r"+Utils.hex(d);

	    result = 0x80 - dvalue;
	    
	    set_hflag(Utils.bit3(result) || Utils.bit3(dvalue));
	    set_sflag(get_nflag() ^ get_vflag());
	    set_vflag(result == 0x80);
	    set_nflag(Utils.bit7(result));
	    set_zflag(result ==0);
	    set_cflag(result != 0);
	    try { set_reg(d,result); }
	    catch (InvalidRegisterException e) { }
	    return;
	}

	private void Perform_NOP()
	{ 
		currentInstruction = "nop";
		return; 
	}

	private void Perform_OR()
	{
	    d = GetOperand_XXXXXXX11111XXXX();
	    r = GetOperand_XXXXXX1XXXXX1111();

	    currentInstruction = "or r"+Utils.hex(d)+", r"+Utils.hex(r);

	    dvalue = get_reg(d);
	    rvalue = get_reg(r);
	    result = dvalue | rvalue;

	    set_sflag(get_nflag() ^ get_vflag());
	    set_vflag(false);
	    set_nflag(Utils.bit7(result));
	    set_zflag(result==0);
	    try { set_reg(d,result); }
	    catch (InvalidRegisterException e) { }
	    return;
	}

	private void Perform_ORI()
	{
	    d = GetOperand_XXXXXXXX1111XXXX();
	    K = GetOperand_XXXX1111XXXX1111();

	    currentInstruction = "ori r"+Utils.hex(d)+", "+Utils.hex(K);

	    dvalue = get_reg(d);
	    result = K | dvalue;

	    set_sflag(get_nflag() ^ get_vflag());
	    set_vflag(false);
	    set_nflag(Utils.bit7(result));
	    set_zflag(result==0);
	    return;
	}

	private void Perform_OUT()
	{
	    r = GetOperand_XXXXXXX11111XXXX();
	    A = GetOperand_XXXXX11XXXXX1111();

	    currentInstruction = "out "+Utils.hex(A)+", r"+Utils.hex(r);

	    try { set_ioreg(A,get_reg(r)); }
	    catch (InvalidRegisterException e) { }
	    return;
	}

	private void Perform_POP() throws RuntimeException
	{
	    d = GetOperand_XXXXXXX11111XXXX();

	    currentInstruction = "pop r"+Utils.hex(d);

	    try { set_reg(d,mStack.pop()); }
	    catch (InvalidRegisterException e) { }
	    catch (RuntimeException e) { throw e; }
	    return;
	}

	private void Perform_PUSH() throws RuntimeException
	{
	    d = GetOperand_XXXXXXX11111XXXX();

	    currentInstruction = "push r"+Utils.hex(d);

	    mStack.push(get_reg(d));
	    return;
	}

	private void Perform_RCALL()
	{
		// TO DO:  Implement
  	    return;
	}

	private void Perform_RET() throws RuntimeException
	{
	    int hibyte = mStack.pop();
	    int lobyte = mStack.pop();

	    currentInstruction = "ret";

	    mPC = lobyte + (hibyte << 8);
	    return;
	}

	private void Perform_RETI() throws RuntimeException
	{
	    int hibyte = mStack.pop();
	    int lobyte = mStack.pop();

	    currentInstruction = "reti";

	    mPC = lobyte + (hibyte << 8);
            set_iflag(true);
          return;
	}

	private void Perform_RJMP()
	{
		k=GetOperand_XXXX111111111111();
            int newPC = mPC + branch_offset_jmp(k);
		mPC = newPC;
		currentInstruction = "rjmp "+Utils.hex(newPC);
		return;
	}

	private void Perform_ROL()
	{
		d = GetOperand_XXXXXX1111111111();
		
		dvalue = get_reg(d);

		currentInstruction = "rol r"+Utils.hex(d);

		boolean cflag = Utils.bit7(dvalue);
		dvalue = (dvalue << 1) & 0xFE;
		if (get_cflag())
			dvalue = dvalue & 0x01;

		try { set_reg(d,dvalue); }
		catch (InvalidRegisterException e) { }

		set_hflag(CarryFromBit3(dvalue));
		set_sflag(get_nflag() ^ get_vflag());
		set_vflag(get_nflag() ^ get_cflag());
		set_nflag(Utils.bit7(dvalue));
		set_zflag(dvalue == 0);
		set_cflag(cflag);
		return;
	}

	private void Perform_ROR()
	{
		d = GetOperand_XXXXXXX11111XXXX();
		
		dvalue = get_reg(d);

		currentInstruction = "ror r"+Utils.hex(d);

		boolean cflag = Utils.bit7(dvalue);
		dvalue = (dvalue >> 1) & 0x7F;

		if (get_cflag())
			dvalue = dvalue & 0x80;

		try { set_reg(d,dvalue); }
		catch (InvalidRegisterException e) { }

		set_hflag(CarryFromBit3(dvalue));
		set_sflag(get_nflag() ^ get_vflag());
		set_vflag(get_nflag() ^ get_cflag());
		set_nflag(Utils.bit7(dvalue));
		set_zflag(dvalue == 0);
		set_cflag(cflag);
		return;
	}

	private void Perform_SBC()
	{
		// TO DO:  Implement
		return;
	}

	private void Perform_SBCI()
	{
		// TO DO:  Implement
		return;
	}

	private void Perform_SBI()
	{
		A = GetOperand_XXXXXXXX11111XXX();
		b = GetOperand_XXXXXXXXXXXXX111();

		currentInstruction = "sbi "+Utils.hex(A)+", "+Utils.hex(b);

		byte bitvalue = (byte)(1 << b);
		byte result = (byte)(get_ioreg(A) | bitvalue);
		try { set_ioreg(A,result); }
		catch (InvalidRegisterException e) { }
		return;
	}

	private void Perform_SBIC() throws RuntimeException
	{
		A = GetOperand_XXXXXXXX11111XXX();
		b = GetOperand_XXXXXXXXXXXXX111();

		currentInstruction = "sbic "+Utils.hex(A)+", "+Utils.hex(b);

	        byte bitvalue = (byte)(1 << b);
	        if ((get_ioreg(A) & bitvalue) == 0)
		    fetch();
		return;
	}

	private void Perform_SBIS() throws RuntimeException
	{
		A = GetOperand_XXXXXXXX11111XXX();
		b = GetOperand_XXXXXXXXXXXXX111();

		currentInstruction = "sbis "+Utils.hex(A)+", "+Utils.hex(b);

		byte bitvalue = (byte)(1 << b);
		if ((get_ioreg(A) & bitvalue) == bitvalue)
		    fetch();
		return;
	}

	private void Perform_SBIW()
	{
		// TO DO:  Implement
		return;
	}

	private void Perform_SBRC() throws RuntimeException
	{
		r = GetOperand_XXXXXXX11111XXXX();
		b = GetOperand_XXXXXXXXXXXXX111();

		currentInstruction = "sbrc r"+Utils.hex(r)+", "+Utils.hex(b);

		byte bitvalue = (byte)(1 << b);
		if ((get_reg(r) & bitvalue) == 0)
			fetch();
		return;
	}

	private void Perform_SBRS() throws RuntimeException
	{
		r = GetOperand_XXXXXXX11111XXXX();
		b = GetOperand_XXXXXXXXXXXXX111();

		currentInstruction = "sbrs r"+Utils.hex(r)+", "+Utils.hex(b);

		byte bitvalue = (byte)(1 << b);
		if ((get_reg(r) & bitvalue) == bitvalue)
			fetch();
		return;
	}

	private void Perform_SLEEP()
	{

		currentInstruction = "sleep";
		// TO DO:  Implement
		return;
	}

	private void Perform_SPM()
	{
		// TO DO:  Implement
		return;
	}

	private void Perform_ST_I()
	{
		r = GetOperand_XXXXXXX11111XXXX();
		currentInstruction = "st x, r"+Utils.hex(r);
		data_memory[get_x()].setValue(get_reg(r));
		return;
	}

	private void Perform_ST_II()
	{
		r = GetOperand_XXXXXXX11111XXXX();
		currentInstruction = "st x+, r"+Utils.hex(r);
		data_memory[get_x()].setValue(get_reg(r));
		inc_x();
		return;
	}

	private void Perform_ST_III()
	{
		dec_x();
		r = GetOperand_XXXXXXX11111XXXX();
		currentInstruction = "st -x, r"+Utils.hex(r);
		data_memory[get_x()].setValue(get_reg(r));
		return;
	}

	private void Perform_ST_IV()
	{
		r = GetOperand_XXXXXXX11111XXXX();
		currentInstruction = "st y, r"+Utils.hex(r);
		data_memory[get_y()].setValue(get_reg(r));
		return;
	}

	private void Perform_ST_V()
	{
		r = GetOperand_XXXXXXX11111XXXX();
		currentInstruction = "st y+, r"+Utils.hex(r);
		data_memory[get_y()].setValue(get_reg(r));
		inc_y();
		return;
	}

	private void Perform_ST_VI()
	{
		dec_y();
		r = GetOperand_XXXXXXX11111XXXX();
		currentInstruction = "st -y, r"+Utils.hex(r);
		data_memory[get_y()].setValue(get_reg(r));
		return;
	}

	private void Perform_ST_VII()
	{
		q = GetOperand_XX1X11XXXXXXX111();
		r = GetOperand_XXXXXXX11111XXXX();
		currentInstruction = "std y+"+q+", r"+Utils.hex(r);
		data_memory[get_y() + q].setValue(get_reg(r));
		return;
	}

	private void Perform_ST_VIII()
	{
		r = GetOperand_XXXXXXX11111XXXX();
		currentInstruction = "st z, r"+Utils.hex(r);
		data_memory[get_z()].setValue(get_reg(r));
		return;
	}

	private void Perform_ST_IX()
	{
		r = GetOperand_XXXXXXX11111XXXX();
		currentInstruction = "st z+, r"+Utils.hex(r);
		data_memory[get_z()].setValue(get_reg(r));
		inc_z();
		return;
	}

	private void Perform_ST_X()
	{
		dec_z();
		r = GetOperand_XXXXXXX11111XXXX();
		currentInstruction = "st -z, r"+Utils.hex(r);
		data_memory[get_z()].setValue(get_reg(r));
		return;
	}

	private void Perform_ST_XI()
	{
		r = GetOperand_XXXXXXX11111XXXX();
		q = GetOperand_XX1X11XXXXXXX111();
		currentInstruction = "std z+"+q+", r"+Utils.hex(r);	
		rvalue = get_reg(r);
		data_memory[get_z() + q].setValue(rvalue);
		return;
	}

	private void Perform_STS()
	{
		d = GetOperand_XXXXXXX11111XXXX();
		currentInstruction = "sts k, r"+Utils.hex(d);
		dvalue = get_reg(d);

		// TO DO:  Check RAMPD to determine which 64k segement of
		// memory to access.
		
		data_memory[mMBR2].setValue(dvalue);
		return;
	}

	private void Perform_SUB()
	{
		d = GetOperand_XXXXXXX11111XXXX();
		r = GetOperand_XXXXXX1XXXXX1111();

		dvalue = get_reg(d);
		rvalue = get_reg(r);
		result = dvalue - rvalue;

		currentInstruction = "sub r"+Utils.hex(d)+", r"+Utils.hex(r);

		set_hflag((!Utils.bit3(dvalue) && Utils.bit3(rvalue)) || (Utils.bit3(rvalue) && Utils.bit3(result))  || (Utils.bit3(result) && !Utils.bit3(dvalue)));
		set_sflag(get_nflag() ^ get_vflag());
		set_vflag((Utils.bit7(dvalue) && !Utils.bit7(rvalue) && !Utils.bit7(result)) || (!Utils.bit7(dvalue) && Utils.bit7(rvalue) && Utils.bit7(result)));
		set_nflag(Utils.bit7(result));
		set_zflag(result==0);
		set_cflag((!Utils.bit7(dvalue) && Utils.bit7(rvalue)) || (Utils.bit7(rvalue) && Utils.bit7(result)) || (Utils.bit7(result) && !Utils.bit7(dvalue)));
		try { set_reg(d,result); }
		catch (InvalidRegisterException e) { }
		return;
	}

	private void Perform_SUBI()
	{
		d = GetOperand_XXXXXXXX1111XXXX();
		K = GetOperand_XXXX1111XXXX1111();

		dvalue = get_reg(d);
		result = (dvalue - K);
		
		currentInstruction = "subi r"+Utils.hex(d)+", "+Utils.hex(K);
		
		set_hflag((!Utils.bit3(dvalue) && Utils.bit3(K)) || (Utils.bit3(K) && Utils.bit3(result))  || (Utils.bit3(result) && !Utils.bit3(dvalue)));
		set_sflag(get_nflag() ^ get_vflag());
		set_vflag((Utils.bit7(dvalue) && !Utils.bit7(K) && !Utils.bit7(result)) || (!Utils.bit7(dvalue) && Utils.bit7(K) && Utils.bit7(result)));
		set_nflag(Utils.bit7(result));
		set_zflag(result==0);
		set_cflag((!Utils.bit7(dvalue) && Utils.bit7(K)) || (Utils.bit7(K) && Utils.bit7(result)) || (Utils.bit7(result) && !Utils.bit7(dvalue)));
		try { set_reg(d,result); }
		catch (InvalidRegisterException e) { }
		return;
	}

	private void Perform_SWAP()
	{
		d = GetOperand_XXXXXXX11111XXXX();

		dvalue = get_reg(d);
		
		currentInstruction = "swap r"+Utils.hex(d);
		
		result = ((dvalue & 0xF0) >> 4) | ((dvalue & 0x0F) << 4);
		try { set_reg(d,result); }
		catch (InvalidRegisterException e) { }
		return;
	}

	private void Perform_WDR()
	{
		currentInstruction = "wdr";
		// TO DO:  Implement
		return;
	}

	//----------------------------------------------
	// Debug
	// Basically just dumps current CPU state
	//----------------------------------------------

    /**
     * Displays internal register and CPU diagnostics.
     * This can be used if the Execute method throws a runtime
     * exception to help fix any bugs.
     * @see Atmel#Execute
     */
	public void debug()
	{
		System.out.println("ATMEL DEBUG INFO");
		System.out.println("===== ===== ====\n");
		
		// Dump the indexes
		System.out.println("Index X:\t0x" + Integer.toHexString(get_x()));
		System.out.println("Index Y:\t0x" + Integer.toHexString(get_y()));
		System.out.println("Index Z:\t0x" + Integer.toHexString(get_z()));
		
		// Counters etc
		System.out.println("PC:\t0x" + Integer.toHexString(mPC));
		System.out.println("MBR:\t0x" + Integer.toHexString(mMBR));
		System.out.println("MBR2:\t0x" + Integer.toHexString(mMBR2));

		// Dump the current status register flags
		System.out.println("Current status register flags:");
		System.out.println("I T H S V N Z C");
		int column, row;
		int bitvalue;
		for(column=7;column>=0;column--)
		{
			bitvalue = 1 << column;
			if ((get_sreg() & bitvalue) == bitvalue)
				System.out.print("* ");
		    else
				System.out.print("- ");
		}
		System.out.println("");

		// Dump the general registers
		int numOfRows = GENERAL_REG_COUNT / 8;
		int regPtr = 0;


		System.out.println("General Registers:");
		// Print column headings
		for(column=0;column<=7;column++) { System.out.print("\t+" + column); }
		System.out.println("");

		regPtr = 0;
	    for(row=0;row<numOfRows;row++)
		{
			System.out.print(Integer.toHexString(regPtr) + "\t");
		    for(column=0;column<=7;column++)
			{
			    if(regPtr <= GENERAL_REG_COUNT)
				{
					System.out.print(Integer.toHexString(get_reg(regPtr++)) + "\t");
				}
			}
			System.out.println("");
		}
	}

    /**
     * Dump the contents of Program Memory into an ASCII file
     * @param filename Name of file to dump program memory into
     */
        public void dump_programmemory(String filename)
        {
	    	Utils.dump(program_memory, filename, PROGRAM_MEMORY_SIZE);
        }

    /**
     * Dump the contents of Data Memory into an ASCII file
     * @param filename Name of file to dump data memory into
     */
        public void dump_datamemory(String filename)
        {
	    Utils.dump(data_memory, filename, DATA_MEMORY_SIZE);
	}

	//========================================================
	// UTILITY FUNCTIONS ONLY UNDER HERE!
	//========================================================

	// Returns a value to add to the program counter
	// if any branch condition is true
	private int branch_offset(int kvalue)
	{
		return ((kvalue ^ 0x40) - 0x40);
	}

    //  Returns a value to add to the program counter
    // for instructions that use 12 bit offsets (I.E: RJMP)
    private int branch_offset_jmp(int kvalue)
    {
	return ((kvalue ^ 0x0800) - 0x0800);
    }
	//-----------------------------------------------------
	// Utility functions to decode operands in opcodes
	// TO USE THESE, THE MEMORY BUFFER REGISTER IS ASSUMED
	// TO HAVE BEEN POPULATED (I.E:  by the fetch method)
	//-----------------------------------------------------

	// Returns TRUE if carry from bit 3 - used for H flag
	private boolean CarryFromBit3(int dvalue)
	{
		return (Utils.bit3(dvalue));
	}

    private boolean CarryFromBit3(int dvalue, int result)
    {
	    return ((!Utils.bit3(dvalue) && Utils.bit3(result)) || (Utils.bit3(dvalue) && Utils.bit3(result)) || (Utils.bit3(dvalue) && !Utils.bit3(result)));
    }

	private boolean CarryFromBit3(int dvalue, int rvalue, int result)
	{
	 	return ((Utils.bit3(dvalue) && Utils.bit3(rvalue)) || (Utils.bit3(rvalue) && !Utils.bit3(result)) || (!Utils.bit3(result) && Utils.bit3(dvalue)));
	}

	// Returns TRUE if overflow from two's complement of operation
	// Used for V flag
	private boolean Overflow(int dvalue, int rvalue, int result)
	{
		return ((Utils.bit7(dvalue) && Utils.bit7(rvalue) && !Utils.bit7(result)) || (!Utils.bit7(dvalue) && !Utils.bit7(rvalue) && Utils.bit7(result)));
	}

	// Returns TRUE if there was a carry from the result
	// Used for Carry flag
	private boolean CarryFromResult(int dvalue, int rvalue, int result)
	{
		return (Utils.bit7(dvalue) && Utils.bit7(rvalue)) || (Utils.bit7(rvalue) && !Utils.bit7(result)) || (!Utils.bit7(result) && Utils.bit7(dvalue));
	}

	private int GetOperand_XXXXXXX11111XXXX() {  return(mMBR & 0x01f0) >> 4; }
	private int GetOperand_XXXXXX1XXXXX1111() 
         { 
	     return(mMBR & 0x000F) + ((mMBR & 0x020f) >> 5); 
	}

	private int GetOperand_XX1X11XXXXXXX111()
	{
		int returnValue = 0;
		returnValue = mMBR & 0x0007;
		returnValue += ((mMBR & 0x0C00) >> 10);
		returnValue += ((mMBR & 0x2000) >> 13);
		return returnValue;
	}

	private int GetOperand_XXXXXXXX1111XXXX()
        {
		return (((mMBR & 0x00F0) >> 4) + 0x10);
        }
    
    private int GetOperand_XXXXXXXXXX11XXXX()
    {
		return (mMBR & 0x0030) + 24;
    }

    private int GetOperand_XXXX1111XXXX1111()
    {
	// TO DO:  Any number above 127 causes ugly, ugly
	// bugs because of the bloody sign bit in the byte.
         return (((mMBR & 0x0F00) >> 4) + (mMBR & 0x000F));
    }

    private int GetOperand_XXXXXXXX11XX1111()
    {
		return ((mMBR & 0x00C0) >> 2) + (mMBR & 0x000F);
    }
    
    private int GetOperand_XXXXXXXXX111XXXX()
    {
	return (mMBR & 0x0070) >> 4;
    }

    private byte GetOperand_XXXXXXXXXXXXX111()
    {
		return (byte)(mMBR & 0x007);
    }

    private int GetOperand_XXXXXX1111111XXX()
    {
		return (mMBR & 0x03f8) >> 3;
    }
	private int GetOperand_XXXXXXX111111111()
	{
		return (mMBR & 0x03ff);
	}
	private int GetOperand_XXXXXXXXXXXX1111()
	{
		return (mMBR & 0x000f);
	}
	private int GetOperand_XXXX111111111111()
	{
		return (mMBR & 0X0fff);
	}
	private int GetOperand_XXXXXX1111111111()
	{
		return (mMBR & 0x03ff);
	}

    private int GetOperand_XXXXXXXX11111XXX()
    {
		return ((mMBR & 0x00f8) >> 3);
    }
    
    private int GetOperand_XXXXXXX11111XXX1()
    {
		return (mMBR & 0x01f1 >> 3) + (mMBR & 0x0001);
    }

    private int GetOperand_XXXXX11XXXXX1111()
    {
		return ((mMBR & 0x0600) >> 5) + (mMBR & 0x000f);
    }
	
	private void OhMyGod()
	{
		int Zod = 0;
		int Brad = 1;
		int Alan = 2;
		int angry = 0;
		int drunk = 3;
		int plain = 4;
		int loud = 5;
		if ((Alan + Brad) == drunk)
			Zod = angry;
		else
			Zod = just(plain,loud);
		System.err.println("You two!  Git yer LARD ARSES into gear 'n git this ship mobilized!");
	}
	private int just(int uno, int due) { return uno + due; }
}
