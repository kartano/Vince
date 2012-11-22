//=============================================
// atmel Class
//
// MODIFICATION HISTORY:
// 06/05/2004     - Bug fixed:  dec_z() was using wrong lock object
// 07/04/2004     - Bug fixed:  dec_y() was assinging result to Z index
//                - Livness:  various locking improvements
// 11/07/2003     - Added supportsWatchdog property
// 29/10/2003     - Added IJMP, ICALL, SLEEP
//                - Added 16/22 bit PC modes
//                - Subroutine calls can now store 22 bit PC not just 16
// 27/10/2003     - Cleaned more indents, removed last runtime exception
//                - Fixed ST instructions - these now use setDataMemory
//                - Removed "mExecuting" property
// 25/10/2003     - More work on memory observer
//                - Changed LoadHex and LoadBin to use memory accessor/mutator
//                - Tidied up indentation
// 21/10/2003 - Cleaned up the  - Tidied up indentation
// 21/10/2003 - Cleane API, exposed the Atmel stack
// ** NOTE: As of today (21/10/2003) I'm using CVS for version numbers ***
// 10/10/2003 - 1.5.0 - Reworked to function with new version of GUI
//                    - Removed the "currentInstruction" string
//                    - Changed instruction decoder to use new class
//                    - Moved the GetOperand stuff into Utils
//                    - Removed some (now) redundant methods  
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
//                - Instruction decoding MUCH faster
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
import gui.*;

/**
 * This class is an abstract to be used to develop other Atmel MCU types.
 * @author Simon Mitchell
 * @author Paul Pearce
 * @author Craig Eales
 */

abstract public class Atmel extends Observable
{
      //----------------------------------------------
      // Dummy objects used for locking particular
      // functions.  Makes code move lively!
      // Thank you, Doug Lea!
      //----------------------------------------------
      private Object mLock_IndexX = new Object();
      private Object mLock_IndexY = new Object();
      private Object mLock_IndexZ = new Object();
      
      //----------------------------------------------
      // Cycle counter
      //----------------------------------------------
      private long mClockCycles;
      
      //----------------------------------------------
      // Maximum Program Counter value
      // This is used to wrap the program counter during fetch
      // and is based on the PC_BIT_SIZE value
      //----------------------------------------------
      private int mMaxPCValue;

      //----------------------------------------------
      // Sleep mode - TRUE = we sleep
      //----------------------------------------------
      protected boolean mSleeping;
      
      //----------------------------------------------
      // Remember the last runtime exception.
      //----------------------------------------------
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

      //----------------------------------------
      // MCU structure - these values will change
      // depending on which MCU type is being used
      //----------------------------------------
      protected int PROGRAM_MEMORY_SIZE;
      protected int DATA_MEMORY_SIZE;
      protected int EXTERNAL_INTERRUPTS = 0;
      protected boolean WATCHDOG = false;
      protected int PC_BIT_SIZE = 16;

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

      /**
      * Contructs a new instance of the Atmel class
      */
      public Atmel(int ProgramCapacity, int DataCapacity, boolean SoftwareStack, int PCBitSize)
      {
            mLastInstructionAddress = 0;
            PROGRAM_MEMORY_SIZE = ProgramCapacity;
            DATA_MEMORY_SIZE = GENERAL_REG_COUNT + IO_REG_COUNT + DataCapacity;
            PC_BIT_SIZE = PCBitSize;
            if (SoftwareStack)
                  mStack = new SoftwareStack(this);
            else
                  mStack = new HardwareStack(this);
            program_memory = new int[PROGRAM_MEMORY_SIZE];
            data_memory = new UByte[DATA_MEMORY_SIZE];

            for(int i=0;i<DATA_MEMORY_SIZE;i++)
                  data_memory[i] = new UByte(0xFF);

            mMaxPCValue = (2 << PC_BIT_SIZE);
            power_on_reset();
      }

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
                                    this.setProgramMemory(memPtr++,valueForMemory);

                                    current_token = yy.yylex();

                                    //get record and then load record into memory
                                    while(current_token.m_index == 3)
                                    {
                                          //ASSERTION: data records are two bytes long
                                          //load record into memory
                                          valueForMemory = Integer.parseInt(current_token.m_text,16);
                                          // Swap the nibbles
                                          valueForMemory = ((valueForMemory & 0xff) << 8) + ((valueForMemory & 0xff00) >> 8);
                                          this.setProgramMemory(memPtr++,valueForMemory);
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
                  } // while more lines
                  is.close();
            } // try block
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
                        this.setProgramMemory(memPtr++,Utils.unsigned_byte(bytepair[0]) + (Utils.unsigned_byte(bytepair[1]) << 8));
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
      * Return the number of external interrupts
      * @return Number of external interrupts
      */

      public int ExternalInterruptCount() { return EXTERNAL_INTERRUPTS; }      

      /**
      * Returns the stack this AVR is using
      * @return The stack this AVR is using
      */
      public StackInterface Stack() { return mStack; }

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
      * Returns the size of the program counter in bits
      * @return Size of program counter in bits
      */
      public int getPCBitSize() { return PC_BIT_SIZE; }

      /**
      * Returns whether or not this AVR supports the Watchdog timer
      * @return True if this AVR supports the Watchdog.  False if not.
      */
      public boolean supportsWatchdog() { return WATCHDOG; }

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
            try 
            {
                  // If the memory address hasn't changed, don't notify any observers
                  if (data_memory[index].intValue() != value)
                  {
                        data_memory[index].setValue(value); 
                        this.setChanged();
                        notifyObservers(new MemoryChangeDetails(index, HexTableModel.OBSERVE_DATA_MEMORY));
                  }  
            }
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
      public synchronized void setProgramMemory(int index, int value)
      {
            try
            {
                  // If the memory address hasn't changed, don't notify any observers
                  if (program_memory[index] != value)
                  {
                        program_memory[index] = value; 
                        this.setChanged();
                        notifyObservers(new MemoryChangeDetails(index, HexTableModel.OBSERVE_PROGRAM_MEMORY));
                  }
            }
            catch (ArrayIndexOutOfBoundsException OOB) { }
      }

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
            catch(RuntimeException e) { }
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
            catch(RuntimeException e) { }
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
            catch(RuntimeException e) { }
      }

      private int get_z() { return get_reg(28) + (get_reg(29) << 8); }

      // Synchronized methods to bump the indexes UP
      private void inc_x()
      {
            synchronized(mLock_IndexX) { set_x(get_x() + 1); }
      }
      private void inc_y()
      {
            synchronized(mLock_IndexY) { set_y(get_y() + 1); }
      }
      private void inc_z()
      {
            synchronized(mLock_IndexZ) { set_z(get_z() + 1); }
      }

      // Synchronized methods to bump the indexes DOWN
      private void dec_x()
      {
            synchronized(mLock_IndexX) { set_x(get_x() - 1); }
      }
      private void dec_y()
      {
            synchronized(mLock_IndexY) { set_y(get_y() - 1); }
      }
      private void dec_z()
      {
            synchronized(mLock_IndexZ) { set_z(get_z() - 1); }
      }

      //----------------------------------------------
      // General purpose registers getting and setting
      //----------------------------------------------

      private void set_reg(int index, int RHS) throws AVR.RuntimeException
      {
            if ((index < 0) || (index >= GENERAL_REG_COUNT))
                  throw new RuntimeException(Constants.Error(Constants.INVALID_GENERAL_REGISTER)+" Register: " +Utils.hex(index,4));
            else
                  //data_memory[index].setValue(RHS);
                  setDataMemory(index,RHS);
      }

      private int get_reg(int index)
      {
            if ((index < 0) || (index >= GENERAL_REG_COUNT))
                  return 0;
            else
                  return getDataMemory(index);
      }

      /**
      * Sets a value in an IO Register
      * @param index Number of IO Register to set
      * @param RHS Value to set - ONLY the lower 8 bits are used, sign bit is ignored
      */
      public void set_ioreg(int index, int RHS) throws AVR.RuntimeException
      {
            if ((index < 0) || (index >= IO_REG_COUNT))
                  throw new RuntimeException(Constants.Error(Constants.INVALID_IO_REGISTER)+" Register: " +Utils.hex(index,4));
            else
                  setDataMemory(index+IO_REG_COUNT,RHS);
                  //data_memory[index+IO_REG_COUNT].setValue(RHS);
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
                  return getDataMemory(index+IO_REG_COUNT);
      }

      //----------------------------------------------
      // Status Register methods
      //
      // Status register is stored in I/O register $3F (data memory location $5F)
      // The SREG bits are as follows:
      // Bit 7 (128)    "I" - Global Interrupt Enable
      // Bit 6 (63)     "T" - Bit Copy Storage
      // Bit 5 (32)     "H" - Half Carry flag
      // Bit 4 (16)     "S" - Sign bit
      // Bit 3 (8)      "V" - Two's complement overflow flag
      // Bit 2 (4)      "N" - Negative flag
      // Bit 1 (2)      "Z" - Zero flag
      // Bit 0 (1)      "C" - Carry flag
      //----------------------------------------------

      // Gets
      private int get_sreg() { return get_ioreg(SREG); }
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
            try { set_ioreg(SREG,RHS); }
            catch(RuntimeException e) { }
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

      /**
      * Performs a power on reset
      */
      public synchronized void power_on_reset()
      {
            // Flush data memory
            for(int i=0;i<=DATA_MEMORY_SIZE;i++)
            this.setDataMemory(i,0x00);

            mPC = 0;
            mMBR = mMBR2 = 0;
            for(int i=0;i<=PROGRAM_MEMORY_SIZE;i++) 
                  this.setProgramMemory(i,0xFFFF);
            // Power on reset sets the PORF bit of the MCUSR
            try { set_ioreg(MCUSR,get_ioreg(MCUSR) | 0x01); }
            catch (RuntimeException e) { }
            mClockCycles = 0;
            mSleeping = false;
      }

      /**
      * Performs an external reset
      */
      public synchronized void external_reset()
      {
            // Set EXTRF bit in MCUSR
            try { set_ioreg(MCUSR,get_ioreg(MCUSR) | 0x02); }
            catch (RuntimeException e) { }

            // TO DO:  Handle an external reset
            //  This happens when the RESET pin of the atmel is set low for 50 ns or more
            mClockCycles = 0;
            mMBR = mMBR2 = 0;
            mPC = 0;
            mSleeping = false;
            System.out.println("Atmel.external_reset");
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
            // TO DO:  Reset the mCycle count? Yea or nay?
      }

      //----------------------------------------------
      // Fetch next instruction into memory buffer register
      // This automatically consumes 1 clock cycle for 16 bit instructions,
      // or 2 cycles for 32 bit.
      //----------------------------------------------
      private synchronized void fetch() throws RuntimeException
      {
            clockTick();
            mLastInstructionAddress = mPC;
            if (mPC >= PROGRAM_MEMORY_SIZE)
                  throw new RuntimeException(Constants.Error(Constants.UPPER_MEMORY_LIMIT));    

            mMBR = program_memory[mPC++];
            mPC %= mMaxPCValue;
            // These instructions are 32bits wide
            // If the current instruction matches these, read the second word
            if (InstructionDecoder.InstructionLength(mMBR) == 32)
            {
                  if (mPC >= PROGRAM_MEMORY_SIZE)
                        throw new RuntimeException(Constants.Error(Constants.UPPER_MEMORY_LIMIT));
                  mMBR2 = program_memory[mPC++];
                  mPC %= mMaxPCValue;
                  clockTick();
            }
            else
                  mMBR2 = 0;                  
      }

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
            int currentInstruction;

            // TO DO:  Check what happens in sleep mode.
            // The SM0 and SM1 bits of the MCUCR register
            // determine what interrupts can be triggered
            // while the AVR is asleep.
            // Basically, if we are in sleep mode:
            // IF BIT 4 of MCUCR = 1, WE ARE IN FULL SLEEP.
            //          Only check external interrupts.
            // ELSE WE ARE IN HALF SLEEP
            //          Check all interrupts
            if (get_iflag()) 
            { 
                  HandleInterrupts();

                  // If an interrupt was triggered,
                  // push the return address onto the stack.
                  if (!get_iflag())
                        PCToStack();
            }
            
            if (!mSleeping)
            {
                  fetch();
      
                  currentInstruction = InstructionDecoder.DecodeInstruction(mMBR);
               
                  switch (currentInstruction & 0xF000)
                  {
                        case 0 :
                        // 0x0xxx INSTRUCTIONS
                              if (currentInstruction == InstructionDecoder.Instr_ADD)
                                    Perform_ADD();
                              else if (currentInstruction == InstructionDecoder.Instr_CPC)
                                    Perform_CPC();
                              else if (currentInstruction == InstructionDecoder.Instr_FMUL)
                                    Perform_FMUL();
                              else if (currentInstruction == InstructionDecoder.Instr_FMULS)
                                    Perform_FMULS();
                              else if (currentInstruction == InstructionDecoder.Instr_LSL)
                                    Perform_LSL();
                              else if (currentInstruction == InstructionDecoder.Instr_MOVW)
                                    Perform_MOVW();
                              else if (currentInstruction == InstructionDecoder.Instr_MULS)
                                    Perform_MULS();
                              else if (currentInstruction == InstructionDecoder.Instr_MULSU)
                                    Perform_MULSU();
                              else if (currentInstruction == InstructionDecoder.Instr_NOP)
                                    Perform_NOP();
                              else
                                    Perform_SBC();
                              break;
                        case 0x1000 :
                              if (currentInstruction == InstructionDecoder.Instr_ADC)
                                    Perform_ADC();
                              else if (currentInstruction == InstructionDecoder.Instr_CP)
                                    Perform_CP();
                              else if (currentInstruction == InstructionDecoder.Instr_CPSE)
                                    Perform_CPSE();
                              else if (currentInstruction == InstructionDecoder.Instr_ROL)
                                    Perform_ROL();
                              else
                                    Perform_SUB();
                              break;
                        case 0x2000 :
                              if (currentInstruction == InstructionDecoder.Instr_AND)
                                    Perform_AND();
                              else if (currentInstruction == InstructionDecoder.Instr_EOR)
                                    Perform_EOR();
                              else if (currentInstruction == InstructionDecoder.Instr_MOV)
                                    Perform_MOV();
                              else
                                    Perform_OR();
                              break;
                        case 0x3000 : 
                              Perform_CPI();
                              break;
                        case 0x4000 :
                              Perform_SBCI();
                              break;
                        case 0x5000 :
                              Perform_SUBI();
                              break;
                        case 0x6000 :
                              Perform_ORI();
                              break;
                        case 0x7000 :
                              Perform_ANDI();
                              break;
                        case 0x8000 :
                              if (currentInstruction == InstructionDecoder.Instr_LD_IV)
                                    Perform_LD_IV();
                              else if (currentInstruction == InstructionDecoder.Instr_LD_VII)
                                    Perform_LD_VII();
                              else if (currentInstruction == InstructionDecoder.Instr_LDD_I)
                                    Perform_LDD_I();
                              else if (currentInstruction == InstructionDecoder.Instr_ST_IV)
                                    Perform_ST_IV();
                              else if (currentInstruction == InstructionDecoder.Instr_ST_VII)
                                    Perform_ST_VII();
                              else if (currentInstruction == InstructionDecoder.Instr_ST_VIII)
                                    Perform_ST_VIII();
                              else
                                    Perform_ST_XI();
                        case 0x9000 :
                              if (currentInstruction == InstructionDecoder.Instr_ADIW)
                                    Perform_ADIW();
                              else if (currentInstruction == InstructionDecoder.Instr_ASR)
                                    Perform_ASR();
                              else if (currentInstruction == InstructionDecoder.Instr_BCLR)
                                    Perform_BCLR();
                              else if (currentInstruction == InstructionDecoder.Instr_BSET)
                                    Perform_BSET();
                              else if (currentInstruction == InstructionDecoder.Instr_CALL)
                                    Perform_CALL();
                              else if (currentInstruction == InstructionDecoder.Instr_CBI)
                                    Perform_CBI();
                              else if (currentInstruction == InstructionDecoder.Instr_COM)
                                    Perform_COM();
                              else if (currentInstruction == InstructionDecoder.Instr_DEC)
                                    Perform_DEC();
                              else if (currentInstruction == InstructionDecoder.Instr_EICALL)
                                    Perform_EICALL();
                              else if (currentInstruction == InstructionDecoder.Instr_EIJMP)
                                    Perform_EIJMP();
                              else if (currentInstruction == InstructionDecoder.Instr_ELPM_I)
                                    Perform_ELPM_I();
                              else if (currentInstruction == InstructionDecoder.Instr_ELPM_II)
                                    Perform_ELPM_II();
                              else if (currentInstruction == InstructionDecoder.Instr_ELPM_III)
                                    Perform_ELPM_III();
                              else if (currentInstruction == InstructionDecoder.Instr_FMULSU)
                                    Perform_FMULSU();
                              else if (currentInstruction == InstructionDecoder.Instr_ICALL)
                                    Perform_ICALL();  
                              else if (currentInstruction == InstructionDecoder.Instr_IJMP)
                                    Perform_IJMP();
                              else if (currentInstruction == InstructionDecoder.Instr_BREAK)
                                    Perform_BREAK();
                              else if (currentInstruction == InstructionDecoder.Instr_INC)
                                    Perform_INC();
                              else if (currentInstruction == InstructionDecoder.Instr_JMP)
                                    Perform_JMP();
                              else if (currentInstruction == InstructionDecoder.Instr_LD_I)
                                    Perform_LD_I();
                              else if (currentInstruction == InstructionDecoder.Instr_LD_II)
                                    Perform_LD_II();
                              else if (currentInstruction == InstructionDecoder.Instr_LD_III)
                                    Perform_LD_III();
                              else if (currentInstruction == InstructionDecoder.Instr_LD_V)
                                    Perform_LD_V();
                              else if (currentInstruction == InstructionDecoder.Instr_LD_VI)
                                    Perform_LD_VI();
                              else if (currentInstruction == InstructionDecoder.Instr_LDD_II)
                                    Perform_LDD_II();
                              else if (currentInstruction == InstructionDecoder.Instr_LDD_III)
                                    Perform_LDD_III();
                              else if (currentInstruction == InstructionDecoder.Instr_LDD_IV)
                                    Perform_LDD_IV();
                              else if (currentInstruction == InstructionDecoder.Instr_LDS)
                                    Perform_LDS();
                              else if (currentInstruction == InstructionDecoder.Instr_LPM_I)
                                    Perform_LPM_I();
                              else if (currentInstruction == InstructionDecoder.Instr_LPM_II)
                                    Perform_LPM_II();
                              else if (currentInstruction == InstructionDecoder.Instr_LPM_III)
                                    Perform_LPM_III();
                              else if (currentInstruction == InstructionDecoder.Instr_LSR)
                                    Perform_LSR();
                              else if (currentInstruction == InstructionDecoder.Instr_MUL)
                                    Perform_MUL();
                              else if (currentInstruction == InstructionDecoder.Instr_NEG)
                                    Perform_NEG();
                              else if (currentInstruction == InstructionDecoder.Instr_POP)
                                    Perform_POP();
                              else if (currentInstruction == InstructionDecoder.Instr_PUSH)
                                    Perform_PUSH();
                              else if (currentInstruction == InstructionDecoder.Instr_RET)
                                    Perform_RET();
                              else if (currentInstruction == InstructionDecoder.Instr_RETI)
                                    Perform_RETI();
                              else if (currentInstruction == InstructionDecoder.Instr_ROR)
                                    Perform_ROR();
                              else if (currentInstruction == InstructionDecoder.Instr_SBI)
                                    Perform_SBI();
                              else if (currentInstruction == InstructionDecoder.Instr_SBIC)
                                    Perform_SBIC();
                              else if (currentInstruction == InstructionDecoder.Instr_SBIS)
                                    Perform_SBIS();
                              else if (currentInstruction == InstructionDecoder.Instr_SBIW)
                                    Perform_SBIW();
                              else if (currentInstruction == InstructionDecoder.Instr_SLEEP)
                                    Perform_SLEEP();
                              else if (currentInstruction == InstructionDecoder.Instr_SPM)
                                    Perform_SPM();
                              else if (currentInstruction == InstructionDecoder.Instr_ST_I)
                                    Perform_ST_I();
                              else if (currentInstruction == InstructionDecoder.Instr_ST_II)
                                    Perform_ST_II();
                              else if (currentInstruction == InstructionDecoder.Instr_ST_III)
                                    Perform_ST_III();
                              else if (currentInstruction == InstructionDecoder.Instr_ST_V)
                                    Perform_ST_V();
                              else if (currentInstruction == InstructionDecoder.Instr_ST_VI)
                                    Perform_ST_VI();
                              else if (currentInstruction == InstructionDecoder.Instr_ST_IX)
                                    Perform_ST_IX();
                              else if (currentInstruction == InstructionDecoder.Instr_ST_X)
                                    Perform_ST_X();
                              else if (currentInstruction == InstructionDecoder.Instr_STS)
                                    Perform_STS();
                              else if (currentInstruction == InstructionDecoder.Instr_SWAP)
                                    Perform_SWAP();
                              else
                                    Perform_WDR();
                              break;
                        case 0xb000 :
                              if (currentInstruction == InstructionDecoder.Instr_IN)
                                    Perform_IN();
                              else
                                    Perform_OUT();
                              break;
                        case 0xc000 :
                              Perform_RJMP();
                              break;
                        case 0xd000 :
                              Perform_RCALL();
                              break;
                        case 0xe000 :
                              Perform_LDI();
                              break;
                        case 0xf000 :
                              if (currentInstruction == InstructionDecoder.Instr_BLD)
                                    Perform_BLD();
                              else if (currentInstruction == InstructionDecoder.Instr_BRBC)
                                    Perform_BRBC();
                              else if (currentInstruction == InstructionDecoder.Instr_BRBS)
                                    Perform_BRBS();
                              else if (currentInstruction == InstructionDecoder.Instr_BST)
                                    Perform_BST();
                              else if (currentInstruction == InstructionDecoder.Instr_SBRC)
                                    Perform_SBRC();
                              else
                                    Perform_SBRS();
                              break;
                  } // Switch instruction
            } // If not sleeping
      }

      //========================================================
      // ACTUAL INSTRUCTIONS PERFORMED HERE
      //========================================================

      private void Perform_ADC()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            r = Utils.GetOperand_XXXXXX1XXXXX1111(mMBR);

            dvalue = get_reg(d);
            rvalue = get_reg(r);
            result = dvalue + rvalue;
            if (get_cflag()) result+=1;

            set_hflag(CarryFromBit3(dvalue, rvalue, result));
            set_nflag(Utils.bit7(result));
            set_vflag(Overflow(dvalue, rvalue, result));
            set_sflag(get_nflag() ^ get_vflag());
            set_zflag(result == 0);
            set_cflag(CarryFromResult(dvalue, rvalue, result));

            try {set_reg(d,result); }
            catch (RuntimeException e) { }

            return;
      }

      private void Perform_ADD()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            r = Utils.GetOperand_XXXXXX1XXXXX1111(mMBR);

            dvalue = get_reg(d);
            rvalue = get_reg(r);
            result = dvalue + rvalue;

            set_hflag(CarryFromBit3(dvalue, rvalue, result));
            set_nflag(Utils.bit7(result));
            set_vflag(Overflow(dvalue, rvalue, result));
            set_sflag(get_nflag() ^ get_vflag());
            set_zflag(result == 0);
            set_cflag(CarryFromResult(dvalue, rvalue, result));

            try {set_reg(d,result); }
            catch (RuntimeException e) { }
  
            return;
      }

      private void Perform_ADIW()
      {
            d = Utils.GetOperand_XXXXXXXXXX11XXXX(mMBR);
            K = Utils.GetOperand_XXXXXXXX11XX1111(mMBR);

            dvalue = get_reg(d);
            result = dvalue + K;

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
            catch (RuntimeException e) { };

            clockTick();

            return;
      }

      private void Perform_AND()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            r = Utils.GetOperand_XXXXXX1XXXXX1111(mMBR);

            dvalue = get_reg(d);
            rvalue = get_reg(r);

            result = dvalue & rvalue;

            set_vflag(false);
            set_sflag(get_nflag() ^ get_vflag());
            set_nflag(Utils.bit7(result));
            set_zflag(result ==0);

            try {set_reg(d,result); }
            catch (RuntimeException e) { }
            return;
      }

      private void Perform_ANDI()
      {
            d = Utils.GetOperand_XXXXXXXX1111XXXX(mMBR);
            K = Utils.GetOperand_XXXX1111XXXX1111(mMBR);

            dvalue = get_reg(d);
            result = dvalue & K;

            set_vflag(false);
            set_nflag(Utils.bit7(result));
            set_sflag(get_nflag() ^ get_vflag());
            set_zflag(result == 0);

            try { set_reg(d,result); }
            catch (RuntimeException e) { }
            return;
      }

      private void Perform_ASR()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);

            dvalue = get_reg(d);

            int bit7 = dvalue & 0x80;
            boolean bit1 = (dvalue & 0x01) > 0;

            result = (dvalue >> 1) & bit7;

            set_cflag(bit1);
            set_zflag(result==0);
            set_nflag(Utils.bit7(result));
            set_vflag(get_nflag() ^ get_cflag());
            set_sflag(get_nflag() ^ get_vflag());

            try { set_reg(d,result); }
            catch (RuntimeException e) { }
            return;
      }

      private void Perform_BCLR()
      {
            s = Utils.GetOperand_XXXXXXXXX111XXXX(mMBR);

            int bitvalue = 255 - (1 << s);

            set_sreg(get_sreg() & bitvalue);
            return;
      }

      private void Perform_BLD()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            b = Utils.GetOperand_XXXXXXXXXXXXX111(mMBR);

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
            k = Utils.GetOperand_XXXXXX1111111XXX(mMBR);
            s = Utils.GetOperand_XXXXXXXXXXXXX111(mMBR);
            int newPC = mPC + Utils.branch_offset(k);

            int bitvalue = 1 << s;
            if ((get_sreg() & bitvalue) == 0)
            {
                  mPC = newPC;
                  clockTick();
            }
            return;
      }

      private void Perform_BRBS()
      {
            k = Utils.GetOperand_XXXXXX1111111XXX(mMBR);
            s = Utils.GetOperand_XXXXXXXXXXXXX111(mMBR);

            int newPC = mPC + Utils.branch_offset(k);
            int bitvalue = 1 << s;
            if ((get_sreg() & bitvalue) == bitvalue)
            {
                  mPC = newPC;
                  clockTick();
            }
            return;
      }

      private void Perform_BREAK() throws RuntimeException
      {
            // TO DO:  Is this a BREAK or a NOP?
            throw new RuntimeException(Constants.Error(Constants.BREAK_MODE));
      }

      private void Perform_BSET()
      {
            s = Utils.GetOperand_XXXXXXXXX111XXXX(mMBR);

            int bitvalue = 1 << s;

            set_sreg(get_sreg() | bitvalue);
            return;
      }

      private void Perform_BST()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            b = Utils.GetOperand_XXXXXXXXXXXXX111(mMBR);

            dvalue = get_reg(d);
            int bitvalue = 1 << b;
            set_tflag((dvalue & bitvalue) == bitvalue);
            return;
      }

      private void Perform_CALL() throws RuntimeException
      {
            // NOTE!  This bit is not strictly correct!
            // The AVR would actually push mPC + 2 onto the stack.
            // However, we increment the PC during the fetch phase.
            PCToStack();
  
            int k = Utils.GetOperand_XXXXXXX11111XXX1(mMBR);
            mPC = k + mMBR2;
            
            if (PC_BIT_SIZE == 22)
                  clockTick();
            clockTick();
            clockTick();
            clockTick();
      
            return;
      }

      private void Perform_CBI()
      {
            A = Utils.GetOperand_XXXXXXXX11111XXX(mMBR);
            b = Utils.GetOperand_XXXXXXXXXXXXX111(mMBR);
            Avalue = get_ioreg(A);

            int bitmask = 255 - (1 << b);
            try { set_ioreg(A,(byte)(Avalue & bitmask)); }
            catch (RuntimeException e) { }
            clockTick();
            return;
      }

      private void Perform_COM()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            dvalue = get_reg(d);
            result = 255 - dvalue;

            set_sflag(get_nflag() ^ get_vflag());
            set_vflag(false);
            set_nflag(Utils.bit7(result));
            set_zflag(result==0);
            set_cflag(true);
            try { set_reg(d,result); }
            catch (RuntimeException e) { }
            return;
      }

      private void Perform_CP()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            r = Utils.GetOperand_XXXXXX1XXXXX1111(mMBR);

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
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            r = Utils.GetOperand_XXXXXX1XXXXX1111(mMBR);

            dvalue = get_reg(d);
            rvalue = get_reg(r);
            result = dvalue - rvalue;

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
            K = Utils.GetOperand_XXXX1111XXXX1111(mMBR);
            d = Utils.GetOperand_XXXXXXXX1111XXXX(mMBR);

            dvalue = get_reg(d);
            result = dvalue - K;

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
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            r = Utils.GetOperand_XXXXXX1XXXXX1111(mMBR);

            dvalue = get_reg(d);
            rvalue = get_reg(r);

            if (dvalue == rvalue)
                  fetch();
            return;
      }

      private void Perform_DEC()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);

            dvalue = get_reg(d);

            result = dvalue--;

            set_sflag(get_nflag() ^ get_vflag());
            set_vflag((result & 0x80)==0x00 && (result & 0x7F)==0x7f);
            set_nflag(Utils.bit7(result));
            set_zflag(result==0);
            try { set_reg(d,dvalue); }
            catch (RuntimeException e) { }
            return;
      }

      private void Perform_EICALL()
      {
            // NOTE!!!!
            // This instruction is only available to machines with
            // 22 bit (I.E:  about 8M of program memory)
            // TO DO:  Implement
            // 4 cycles - only works on 22bit PC
            clockTick();
            clockTick();
            clockTick();
            return;
      }

      private void Perform_EIJMP()
      {
            // Ditto for the above.  Only supported by machines with
            // 22 bits.
            // TO DO:  Implement
            clockTick();
            return;
      }
      
      private void Perform_ELPM_I()
      {
            // TO DO:  Implement
            clockTick();
            clockTick();
            return;
      }
      
      private void Perform_ELPM_II()
      {
            // TO DO:  Implement
            clockTick();
            clockTick();
            return;
      }
      
      private void Perform_ELPM_III()
      {
            // TO DO:  Implement
            clockTick();
            clockTick();
            return;
      }

      private void Perform_EOR()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            r = Utils.GetOperand_XXXXXX1XXXXX1111(mMBR);

            dvalue = get_reg(d);
            rvalue = get_reg(r);
            result = dvalue ^ rvalue;
            
            set_sflag(get_nflag() ^ get_vflag());
            set_vflag(false);
            set_nflag(Utils.bit7(result));
            set_zflag(result==0);
            try { set_reg(d,result); }
            catch (RuntimeException e) { }
            return;
      }
      
      private void Perform_FMUL()
      {
            // TO DO:  Implement
            clockTick();
            return;
      }
      
      private void Perform_FMULS()
      {
            // TO DO:  Implement
            clockTick();
            return;
      }
      
      private void Perform_FMULSU()
      {
            // TO DO:  Implement
            clockTick();
            return;
      }
 
      private void Perform_ICALL() throws RuntimeException
      {
            PCToStack();
            mPC = get_z();
            if (PC_BIT_SIZE == 22)
                  clockTick();
            clockTick();
            clockTick();
            return;
      }
      
      private void Perform_IJMP()
      {
            mPC = get_z();
            clockTick();
            return;
      }
 
      private void Perform_IN()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            A = Utils.GetOperand_XXXXX11XXXXX1111(mMBR);

            Avalue = get_ioreg(A);

            try { set_reg(d,Avalue); }
            catch (RuntimeException e) { }

            return;
      }

      private void Perform_INC()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            dvalue = get_reg(d);
            result = dvalue + 1;

            set_sflag(get_nflag() ^ get_vflag());
            set_vflag(dvalue == 0x7f);
            set_nflag(Utils.bit7(result));
            set_zflag(result ==0);
            try { set_reg(d,result); }
            catch (RuntimeException e) { }
            return;
      }

      private void Perform_JMP()
      {
            mPC = Utils.GetOperand_XXXXXXX11111XXX1(mMBR) + mMBR2;
            clockTick();
            clockTick();
            return;
      }

      private void Perform_LD_I()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            try { set_reg(d,data_memory[get_x()].intValue()); }
            catch (RuntimeException e) { }
            clockTick();
            return;
      }

      private void Perform_LD_II()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            try { set_reg(d,data_memory[get_x()].intValue()); }
            catch (RuntimeException e) { }
            inc_x();
            clockTick();
            return;
      }

      private void Perform_LD_III()
      {
            dec_x();
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            try { set_reg(d,data_memory[get_x()].intValue()); }
            catch (RuntimeException e) { }
            clockTick();
            return;
      }

      private void Perform_LD_IV()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            try { set_reg(d,data_memory[get_z()].intValue()); }
            catch (RuntimeException e) { }
            clockTick();
            return;
      }
      
      private void Perform_LD_V()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            try { set_reg(d,data_memory[get_z()].intValue()); }
            catch (RuntimeException e) { }
            inc_z();
            clockTick();
            return;
      }
      
      private void Perform_LD_VI()
      {
            dec_z();
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            try { set_reg(d,data_memory[get_z()].intValue()); }
            catch (RuntimeException e) { }
            clockTick();
            return;
      }

      private void Perform_LD_VII()
      {
            q = Utils.GetOperand_XX1X11XXXXXXX111(mMBR);
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            try { set_reg(d,data_memory[get_z() + q].intValue()); }
            catch (RuntimeException e) { }
            clockTick();
            return;
      }

      private void Perform_LDD_I()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            try { set_reg(d,data_memory[get_y()].intValue()); }
            catch (RuntimeException e) { }
            clockTick();
            return;
      }

      private void Perform_LDD_II()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            try { set_reg(d,data_memory[get_y()].intValue()); }
            catch (RuntimeException e) { }
            inc_y();
            clockTick();
            return;
      }
      
      private void Perform_LDD_III()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            dec_y();
            try { set_reg(d,data_memory[get_y()].intValue()); }
            catch (RuntimeException e) { }
            clockTick();
            return;
      }
      
      private void Perform_LDD_IV()
      {
            q = Utils.GetOperand_XX1X11XXXXXXX111(mMBR);
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            try { set_reg(d,data_memory[get_y() + q].intValue()); }
            catch (RuntimeException e) { }
            clockTick();
            return;
      }

      private void Perform_LDI()
      {
            d = Utils.GetOperand_XXXXXXXX1111XXXX(mMBR);
            K = Utils.GetOperand_XXXX1111XXXX1111(mMBR);

            try { set_reg(d,K); }
            catch (RuntimeException e) { } 
            clockTick();
            return;
      }

      private void Perform_LDS()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            k = mMBR2;

            try { set_reg(d,data_memory[k].intValue()); }
            catch (RuntimeException e) { }
            clockTick();
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
            try
            {
                  if (Utils.bit0(zindex))
                        set_reg(0,(program_memory[wordlocation] & 0xff00) >> 8);
                  else
                        set_reg(0,program_memory[wordlocation] & 0x00ff);
            }
            catch (RuntimeException e) { }    
            clockTick();
            clockTick();
            return;
      }

      private void Perform_LPM_II()
      {
            // See notes for LPM_I
            int zindex = get_z();
            int d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            int wordlocation = zindex >> 1;

            try
            {
                  if (Utils.bit0(zindex))
                        set_reg(d,(program_memory[wordlocation] & 0xff00) >> 8);
                  else
                        set_reg(d,program_memory[wordlocation] & 0x00ff);
            }
            catch (RuntimeException e) { }    
            clockTick();
            clockTick();
            return;
      }

      private void Perform_LPM_III()
      {
            // See notes for LPM_I
            int zindex = get_z();
            int d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            int wordlocation = zindex >> 1;

            try
            {
                  if (Utils.bit0(zindex))
                        set_reg(d,(program_memory[wordlocation] & 0xff00) >> 8);
                  else
                        set_reg(d,program_memory[wordlocation] & 0x00ff);
            }
            catch (RuntimeException e) { }    
            inc_z();
            clockTick();
            clockTick();
            return;
      }

      private void Perform_LSL()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);

            dvalue = get_reg(d);

            // Bit 7 gets shifted into carry flag
            set_cflag(Utils.bit7(dvalue));

            // Bit 0 gets set to 0
            dvalue = (dvalue << 1) & 0xfe;

            set_vflag(get_nflag() ^ get_cflag());
            set_sflag(get_nflag() ^ get_vflag());
            set_nflag(false);
            set_zflag(dvalue == 0);

            try { set_reg(d,dvalue); }
            catch (RuntimeException e) { };  
            return;
      }

      private void Perform_LSR()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);

            dvalue = get_reg(d);

            // Bit 0 gets shifted into carry flag
            set_cflag(Utils.bit0(dvalue));

            // Bit 7 gets set to 0
            dvalue = (dvalue >> 1) & 0x7f;

            set_vflag(get_nflag() ^ get_cflag());
            set_sflag(get_nflag() ^ get_vflag());
            set_nflag(false);
            set_zflag(dvalue == 0);

            try { set_reg(d,dvalue); }
            catch (RuntimeException e) { };  
            return;
      }

      private void Perform_MOV()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            r = Utils.GetOperand_XXXXXX1XXXXX1111(mMBR);

            rvalue = get_reg(r);
            try { set_reg(d,rvalue); }
            catch (RuntimeException e) { }
            return;
      }

      private void Perform_MOVW()
      {
            d = Utils.GetOperand_XXXXXXXX1111XXXX(mMBR);
            r = Utils.GetOperand_XXXXXXXXXXXX1111(mMBR);

            try
            {
                  set_reg(d,get_reg(r));
                  set_reg(d+1,get_reg(r+1));
            }
            catch (RuntimeException e) { }
            return;
      }

      private void Perform_MUL()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            r = Utils.GetOperand_XXXXXX1XXXXX1111(mMBR);

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
            catch (RuntimeException e) { }
            clockTick();
            return;
      }

      private void Perform_MULS()
      {
            // TO DO:  Implement
            clockTick();
            return;
      }

      private void Perform_MULSU()
      {
            // TO DO:  Implement
            clockTick();
            return;
      }
      
      private void Perform_NEG()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            dvalue = get_reg(d);

            result = 0x80 - dvalue;
     
            set_hflag(Utils.bit3(result) || Utils.bit3(dvalue));
            set_sflag(get_nflag() ^ get_vflag());
            set_vflag(result == 0x80);
            set_nflag(Utils.bit7(result));
            set_zflag(result ==0);
            set_cflag(result != 0);
            try { set_reg(d,result); }
            catch (RuntimeException e) { }
            return;
      }

      private void Perform_NOP() { return; }

      private void Perform_OR()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            r = Utils.GetOperand_XXXXXX1XXXXX1111(mMBR);

            dvalue = get_reg(d);
            rvalue = get_reg(r);
            result = dvalue | rvalue;

            set_sflag(get_nflag() ^ get_vflag());
            set_vflag(false);
            set_nflag(Utils.bit7(result));
            set_zflag(result==0);
            try { set_reg(d,result); }
            catch (RuntimeException e) { }
            return;
      }

      private void Perform_ORI()
      {
            d = Utils.GetOperand_XXXXXXXX1111XXXX(mMBR);
            K = Utils.GetOperand_XXXX1111XXXX1111(mMBR);

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
            r = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            A = Utils.GetOperand_XXXXX11XXXXX1111(mMBR);

            try { set_ioreg(A,get_reg(r)); }
            catch (RuntimeException e) { }
            return;
      }

      private void Perform_POP() throws RuntimeException
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);

            set_reg(d,mStack.pop());
            clockTick();
            return;
      }

      private void Perform_PUSH() throws RuntimeException
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);

            mStack.push(get_reg(d));
            clockTick();
            return;
      }

      private void Perform_RCALL() throws RuntimeException
      {
            k = Utils.GetOperand_XXXX111111111111(mMBR);
            int newPC = mPC + Utils.branch_offset_jmp(k);
            PCToStack();
            mPC = newPC;
            // TO DO:  3 cycles for 16 bit PC, 4 cycles for 22 bit     
            if (PC_BIT_SIZE == 22)
                  clockTick();
            clockTick();
            clockTick();
            return;
      }

      private void Perform_RET() throws RuntimeException
      {
            StackToPC();
            // TO DO:  4 cycles for 16 bit PC, 5 cycles for 22 bit
            if (PC_BIT_SIZE == 22)
                  clockTick();
            clockTick();
            clockTick();
            clockTick();
            return;
      }

      private void Perform_RETI() throws RuntimeException
      {
            StackToPC();
            set_iflag(true);
            // TO DO:  4 cycles for 16bit PC, 5 cycles for 22 bit
            if (PC_BIT_SIZE == 22)
                  clockTick();
            clockTick();
            clockTick();
            clockTick();
            return;
      }

      private void Perform_RJMP()
      {
            k=Utils.GetOperand_XXXX111111111111(mMBR);
            int newPC = mPC + Utils.branch_offset_jmp(k);
            mPC = newPC;
            clockTick();
            return;
      }

      private void Perform_ROL()
      {
            d = Utils.GetOperand_XXXXXX1111111111(mMBR);
  
            dvalue = get_reg(d);

            boolean cflag = Utils.bit7(dvalue);
            dvalue = (dvalue << 1) & 0xFE;
            if (get_cflag())
            dvalue = dvalue & 0x01;

            try { set_reg(d,dvalue); }
            catch (RuntimeException e) { }

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
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
  
            dvalue = get_reg(d);

            boolean cflag = Utils.bit7(dvalue);
            dvalue = (dvalue >> 1) & 0x7F;

            if (get_cflag())
                  dvalue = dvalue & 0x80;

            try { set_reg(d,dvalue); }
            catch (RuntimeException e) { }

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
            A = Utils.GetOperand_XXXXXXXX11111XXX(mMBR);
            b = Utils.GetOperand_XXXXXXXXXXXXX111(mMBR);

            byte bitvalue = (byte)(1 << b);
            byte result = (byte)(get_ioreg(A) | bitvalue);
            try { set_ioreg(A,result); }
            catch (RuntimeException e) { }
            clockTick();
            return;
      }

      private void Perform_SBIC() throws RuntimeException
      {
            A = Utils.GetOperand_XXXXXXXX11111XXX(mMBR);
            b = Utils.GetOperand_XXXXXXXXXXXXX111(mMBR);

            byte bitvalue = (byte)(1 << b);
            if ((get_ioreg(A) & bitvalue) == 0)
                  fetch();
            return;
      }

      private void Perform_SBIS() throws RuntimeException
      {
            A = Utils.GetOperand_XXXXXXXX11111XXX(mMBR);
            b = Utils.GetOperand_XXXXXXXXXXXXX111(mMBR);

            byte bitvalue = (byte)(1 << b);
            if ((get_ioreg(A) & bitvalue) == bitvalue)
                  fetch();
            return;
      }

      private void Perform_SBIW()
      {
            // TO DO:  Implement
            clockTick();
            return;
      }

      private void Perform_SBRC() throws RuntimeException
      {
            r = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            b = Utils.GetOperand_XXXXXXXXXXXXX111(mMBR);

            byte bitvalue = (byte)(1 << b);
            if ((get_reg(r) & bitvalue) == 0)
                  fetch();
            return;
      }

      private void Perform_SBRS() throws RuntimeException
      {
            r = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            b = Utils.GetOperand_XXXXXXXXXXXXX111(mMBR);

            byte bitvalue = (byte)(1 << b);
            if ((get_reg(r) & bitvalue) == bitvalue)
                  fetch();
            return;
      }

      private void Perform_SLEEP()
      {
            // Test bit 6 of the MCUCR.  If 0, ignore sleep commands
            if (Utils.bit5(get_ioreg(MCUCR)))
                  mSleeping = true;
            return;
      }

      private void Perform_SPM()
      {
            // TO DO:  Implement
            return;
      }

      private void Perform_ST_I()
      {
            r = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            setDataMemory(get_x(),get_reg(r));
            clockTick();
            return;
      }

      private void Perform_ST_II()
      {
            r = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            setDataMemory(get_x(),get_reg(r));
            inc_x();
            clockTick();
            return;
      }

      private void Perform_ST_III()
      {
            dec_x();
            r = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            setDataMemory(get_x(),get_reg(r));
            clockTick();
            return;
      }

      private void Perform_ST_IV()
      {
            r = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            setDataMemory(get_y(),get_reg(r));
            clockTick();
            return;
      }

      private void Perform_ST_V()
      {
            r = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            setDataMemory(get_y(),get_reg(r));
            inc_y();
            clockTick();
            return;
      }

      private void Perform_ST_VI()
      {
            dec_y();
            r = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            setDataMemory(get_y(),get_reg(r));
            clockTick();
            return;
      }

      private void Perform_ST_VII()
      {
            q = Utils.GetOperand_XX1X11XXXXXXX111(mMBR);
            r = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            setDataMemory(get_y() + q,get_reg(r));
            clockTick();
            return;
      }

      private void Perform_ST_VIII()
      {
            r = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            setDataMemory(get_z(),get_reg(r));
            clockTick();
            return;
      }

      private void Perform_ST_IX()
      {
            r = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            setDataMemory(get_z(),get_reg(r));
            inc_z();
            clockTick();
            return;
      }

      private void Perform_ST_X()
      {
            dec_z();
            r = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            setDataMemory(get_z(),get_reg(r));
            clockTick();
            return;
      }

      private void Perform_ST_XI()
      {
            r = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            q = Utils.GetOperand_XX1X11XXXXXXX111(mMBR);
            rvalue = get_reg(r);
            setDataMemory(get_z() + q,get_reg(r));
            clockTick();
            return;
      }

      private void Perform_STS()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            dvalue = get_reg(d);

            // TO DO:  Check RAMPD to determine which 64k segement of
            // memory to access.
            setDataMemory(mMBR2,get_reg(r));
            clockTick();
            return;
      }

      private void Perform_SUB()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);
            r = Utils.GetOperand_XXXXXX1XXXXX1111(mMBR);

            dvalue = get_reg(d);
            rvalue = get_reg(r);
            result = dvalue - rvalue;

            set_hflag((!Utils.bit3(dvalue) && Utils.bit3(rvalue)) || (Utils.bit3(rvalue) && Utils.bit3(result))  || (Utils.bit3(result) && !Utils.bit3(dvalue)));
            set_sflag(get_nflag() ^ get_vflag());
            set_vflag((Utils.bit7(dvalue) && !Utils.bit7(rvalue) && !Utils.bit7(result)) || (!Utils.bit7(dvalue) && Utils.bit7(rvalue) && Utils.bit7(result)));
            set_nflag(Utils.bit7(result));
            set_zflag(result==0);
            set_cflag((!Utils.bit7(dvalue) && Utils.bit7(rvalue)) || (Utils.bit7(rvalue) && Utils.bit7(result)) || (Utils.bit7(result) && !Utils.bit7(dvalue)));
            try { set_reg(d,result); }
            catch (RuntimeException e) { }
            return;
      }

      private void Perform_SUBI()
      {
            d = Utils.GetOperand_XXXXXXXX1111XXXX(mMBR);
            K = Utils.GetOperand_XXXX1111XXXX1111(mMBR);

            dvalue = get_reg(d);
            result = (dvalue - K);
  
            set_hflag((!Utils.bit3(dvalue) && Utils.bit3(K)) || (Utils.bit3(K) && Utils.bit3(result))  || (Utils.bit3(result) && !Utils.bit3(dvalue)));
            set_sflag(get_nflag() ^ get_vflag());
            set_vflag((Utils.bit7(dvalue) && !Utils.bit7(K) && !Utils.bit7(result)) || (!Utils.bit7(dvalue) && Utils.bit7(K) && Utils.bit7(result)));
            set_nflag(Utils.bit7(result));
            set_zflag(result==0);
            set_cflag((!Utils.bit7(dvalue) && Utils.bit7(K)) || (Utils.bit7(K) && Utils.bit7(result)) || (Utils.bit7(result) && !Utils.bit7(dvalue)));
            try { set_reg(d,result); }
            catch (RuntimeException e) { }
            return;
      }

      private void Perform_SWAP()
      {
            d = Utils.GetOperand_XXXXXXX11111XXXX(mMBR);

            dvalue = get_reg(d);
  
            result = ((dvalue & 0xF0) >> 4) | ((dvalue & 0x0F) << 4);
            try { set_reg(d,result); }
            catch (RuntimeException e) { }
            return;
      }

      private void Perform_WDR()
      {
            // TO DO:  Implement
            return;
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

      //-----------------------------------------------------
      // Bump the clock
      //-----------------------------------------------------
      private synchronized void clockTick()
      {
            // TO DO:  use the AdvancedTimer to time out the
            // execution clock cycles.
            if (mClockCycles == Long.MAX_VALUE)
                  mClockCycles = 0;
            else
                  mClockCycles++;
      }

      //-----------------------------------------------------
      // Stores the current program counter on the stack
      //-----------------------------------------------------
      private synchronized void PCToStack() throws RuntimeException
      {
            int first8bites, second8bites, third8bits;
            
            first8bites = Utils.get_lobyte(mPC);
            second8bites = Utils.get_hibyte(mPC);
            if (PC_BIT_SIZE == 22)
                  third8bits = Utils.get_lobyte(mPC >> 16);
            else
                  third8bits = 0;
            mStack.push(first8bites);
            mStack.push(second8bites);
            if (PC_BIT_SIZE == 22)
                  mStack.push(third8bits);
      }

      //-----------------------------------------------------
      // Restores the program counter from the stack
      //-----------------------------------------------------
      private synchronized void StackToPC() throws RuntimeException
      {
            int first8bites, second8bites, third8bits;

            if (PC_BIT_SIZE == 22)
                  third8bits = mStack.pop();
            else
                  third8bits = 0;
            second8bites = mStack.pop();
            first8bites = mStack.pop();
            
            mPC = first8bites + (second8bites << 8) + (third8bits << 16);
      }
      
      //-----------------------------------------------------
      // Utility functions to decode operands in opcodes
      // TO USE THESE, THE MEMORY BUFFER REGISTER IS ASSUMED
      // TO HAVE BEEN POPULATED (I.E:  by the fetch method)
      //-----------------------------------------------------

      // Returns TRUE if carry from bit 3 - used for H flag
      private boolean CarryFromBit3(int dvalue) { return (Utils.bit3(dvalue)); }

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
