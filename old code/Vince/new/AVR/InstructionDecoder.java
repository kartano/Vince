//=============================================
// InstructionDecoder class
//
//   DATE:  12/10/2003
// VERSION: 1.0.0
//
// MODIFICATION HISTORY:
// 24/10/2003 - Added RCall disassembler
// ** From here on, relying on CVS for versioning
// 12/10/2003 - 1.0.0 - Prototype
//=============================================

package AVR;

public final class InstructionDecoder
{
      // Dud - instruction decoding failed
      public static final int Instr_UNKNOWN = 0x0000;
 
      // 0x0xxx instructions
      public static final int Instr_ADD = 0x0001;
      public static final int Instr_CPC = 0x0002;
      public static final int Instr_FMUL = 0x0003;
      public static final int Instr_FMULS = 0x0004;
      public static final int Instr_LSL = 0x0005;
      public static final int Instr_MOVW = 0x0006;
      public static final int Instr_MULS = 0x0007;
      public static final int Instr_MULSU = 0x0008;
      public static final int Instr_NOP = 0x0009;
      public static final int Instr_SBC = 0x000a;

      // 0x1xxx instructions
      public static final int Instr_ADC = 0x1000;
      public static final int Instr_CP = 0x1001;
      public static final int Instr_CPSE = 0x1002;
      public static final int Instr_ROL = 0x1003;
      public static final int Instr_SUB = 0x1004;
     
      // 0x2xxx instructions
      public static final int Instr_AND = 0x2000;
      public static final int Instr_EOR = 0x2001;
      public static final int Instr_MOV = 0x2002;
      public static final int Instr_OR = 0x2003;

      // 0x3xxx instructions
      public static final int Instr_CPI = 0x3000;

      // 0x4xxx instructions
      public static final int Instr_SBCI = 0x4000;

      // 0x5xxx instructions
      public static final int Instr_SUBI = 0x5000;

      // 0x6xxx instructions
      public static final int Instr_ORI = 0x6000;
      
      // 0x7xxx instructions
      public static final int Instr_ANDI = 0x7000;

      // 0x8xxx instructions
      public static final int Instr_LD_IV = 0x8000;
      public static final int Instr_LD_VII = 0x8001;
      public static final int Instr_LDD_I = 0x8002;
      public static final int Instr_ST_IV = 0x8003;
      public static final int Instr_ST_VII = 0x8004;
      public static final int Instr_ST_VIII = 0x8005;
      public static final int Instr_ST_XI = 0x8006;

      // 0x9xxx instructions
      public static final int Instr_ADIW = 0x9000;
      public static final int Instr_ASR = 0x9001;
      public static final int Instr_BCLR = 0x9002;
      public static final int Instr_BSET = 0x9003;
      public static final int Instr_CALL = 0x9004;
      public static final int Instr_CBI = 0x9005;
      public static final int Instr_COM = 0x9006;
      public static final int Instr_DEC = 0x9007;
      public static final int Instr_EICALL = 0x9008;
      public static final int Instr_EIJMP = 0x9009;
      public static final int Instr_ELPM_I = 0x900a;
      public static final int Instr_ELPM_II = 0x900b;
      public static final int Instr_ELPM_III = 0x900c;
      public static final int Instr_FMULSU = 0x900d;
      public static final int Instr_ICALL = 0x900e;
      public static final int Instr_IJMP = 0x900f;
      public static final int Instr_BREAK = 0x9010;
      public static final int Instr_INC = 0x9011;
      public static final int Instr_JMP = 0x9012;
      public static final int Instr_LD_I = 0x9013;
      public static final int Instr_LD_II = 0x9014;
      public static final int Instr_LD_III = 0x9015;
      public static final int Instr_LD_V = 0x9016;
      public static final int Instr_LD_VI = 0x9017;
      public static final int Instr_LDD_II = 0x9018;
      public static final int Instr_LDD_III = 0x9019;
      public static final int Instr_LDD_IV = 0x901a;
      public static final int Instr_LDS = 0x901b;
      public static final int Instr_LPM_I = 0x901c;
      public static final int Instr_LPM_II = 0x901d;
      public static final int Instr_LPM_III = 0x901e;
      public static final int Instr_LSR = 0x901f;
      public static final int Instr_MUL = 0x9020;
      public static final int Instr_NEG = 0x9021;
      public static final int Instr_POP = 0x9022;
      public static final int Instr_PUSH = 0x9023;
      public static final int Instr_RET = 0x9024;
      public static final int Instr_RETI = 0x9025;
      public static final int Instr_ROR = 0x9026;
      public static final int Instr_SBI = 0x9027;
      public static final int Instr_SBIC = 0x9028;
      public static final int Instr_SBIS = 0x9029;
      public static final int Instr_SBIW = 0x902a;
      public static final int Instr_SLEEP = 0x902b;
      public static final int Instr_SPM = 0x902c;
      public static final int Instr_ST_I = 0x902d;
      public static final int Instr_ST_II = 0x902e;
      public static final int Instr_ST_III = 0x902f;
      public static final int Instr_ST_V = 0x9030;
      public static final int Instr_ST_VI = 0x9031;
      public static final int Instr_ST_IX = 0x9032;
      public static final int Instr_ST_X = 0x9033;
      public static final int Instr_STS = 0x9034;
      public static final int Instr_SWAP = 0x9035;
      public static final int Instr_WDR = 0x9036;

      // 0xbxxx instructions
      public static final int Instr_IN = 0xb000;
      public static final int Instr_OUT = 0xb001;

      // 0xcxxx instructions
      public static final int Instr_RJMP = 0xc000;

      // 0xdxxx instructions
      public static final int Instr_RCALL = 0xd000;

      // 0xexxx instructions
      public static final int Instr_LDI= 0xe000;

      // 0xfxxx instructions
      public static final int Instr_BLD = 0xf000;
      public static final int Instr_BRBC = 0xf001;
      public static final int Instr_BRBS = 0xf002;
      public static final int Instr_BST = 0xf003;
      public static final int Instr_SBRC = 0xf004;
      public static final int Instr_SBRS = 0xf005;

      // Used by the disassembly process
      private static int d, r, dvalue, rvalue, k, K, result, s, q, b, A, Avalue;
      
      // Private constructor
      private InstructionDecoder() {}
      
      //=========================================================================
      /**
      * Returns the length of the current instruction.
      * @param The first 16 bits of the current instruction (I.E: The MBR)
      * @return The length of this instruction - 16 or 32
      */
      public static int InstructionLength(int MBR)
      {
            int returnValue;
            if (((MBR & 0xfe0e) == 0x940c) || ((MBR & 0xfe0e) == 0x940e) || ((MBR & 0xfe0f) == 0x9000) || ((MBR & 0xfe0f) == 0x9200))
                  returnValue = 32;
            else
                  returnValue = 16;
                  
            return returnValue;
      }
      
      //=========================================================================
      /**
      * Disassembles an instruction and returns the mneumonics as a String.
      * Note that unlike the decoder, this method returns a "???" string if the
      * instruction was not defined
      * @param First 16 bits of the instruction
      * @param Second 16 bits of the instruction, if the instruction is 32 bits long
      * @param Program memory address for this instruction
      * @return String value representing the instruction code
      */
      public static String DisassembleInstruction(int MBR, int MBR2, int PC)
      {
            int theInstruction;
            String returnValue;
            
            try { theInstruction = DecodeInstruction(MBR); }
            catch (RuntimeException e) { theInstruction = Instr_UNKNOWN; }

            switch (theInstruction)
            {
                  case Instr_ADD :
                        returnValue = Perform_ADD(MBR);
                        break;
                  case Instr_CPC :
                        returnValue = Perform_CPC(MBR);
                        break;
                  case Instr_FMUL :
                        returnValue = Perform_FMUL(MBR);
                        break;
                  case Instr_FMULS :
                        returnValue = Perform_FMULS(MBR);
                        break;
                  case Instr_LSL :
                        returnValue = Perform_LSL(MBR);
                        break;
                  case Instr_MOVW :
                        returnValue = Perform_MOVW(MBR);
                        break;
                  case Instr_MULS :
                        returnValue = Perform_MULS(MBR);
                        break;
                  case Instr_MULSU :
                        returnValue = Perform_MULSU(MBR);
                        break;
                  case Instr_NOP :
                        returnValue = new String("NOP");
                        break;
                  case Instr_SBC :
                        returnValue = Perform_SBC(MBR);
                        break;
                  case Instr_ADC :
                        returnValue = Perform_ADC(MBR);
                        break;
                  case Instr_CP :
                        returnValue = Perform_CP(MBR);
                        break;
                  case Instr_CPSE :
                        returnValue = Perform_CPSE(MBR);
                        break;
                  case Instr_ROL :
                        returnValue = Perform_ROL(MBR);
                        break;
                  case Instr_SUB :
                        returnValue = Perform_SUB(MBR);
                        break;
                  case Instr_AND :
                        returnValue = Perform_AND(MBR);
                        break;
                  case Instr_EOR :
                        returnValue = Perform_EOR(MBR);
                        break;
                  case Instr_MOV :
                        returnValue = Perform_MOV(MBR);
                        break;
                  case Instr_OR :
                        returnValue = Perform_OR(MBR);
                        break;
                  case Instr_CPI :
                        returnValue = Perform_CPI(MBR);
                        break;
                  case Instr_SBCI :
                        returnValue = Perform_SBCI(MBR);
                        break;
                  case Instr_SUBI :
                        returnValue = Perform_SUBI(MBR);
                        break;
                  case Instr_ORI :
                        returnValue = Perform_ORI(MBR);
                        break;
                  case Instr_ANDI :
                        returnValue = Perform_ANDI(MBR);
                        break;
                  case Instr_LD_IV :
                        returnValue = Perform_LD_IV(MBR);
                        break;
                  case Instr_LD_VII :
                        returnValue = Perform_LD_VII(MBR);
                        break;
                  case Instr_LDD_I :
                        returnValue = Perform_LDD_I(MBR);
                        break;
                  case Instr_ST_IV :
                        returnValue = Perform_ST_IV(MBR);
                        break;
                  case Instr_ST_VII :
                        returnValue = Perform_ST_VII(MBR);
                        break;
                  case Instr_ST_VIII :
                        returnValue = Perform_ST_VIII(MBR);
                        break;
                  case Instr_ST_XI :
                        returnValue = Perform_ST_XI(MBR);
                        break;
                  case Instr_ADIW :
                        returnValue = Perform_ADIW(MBR);
                        break;
                  case Instr_ASR :
                        returnValue = Perform_ASR(MBR);
                        break;
                  case Instr_BCLR :
                        returnValue = Perform_BCLR(MBR);
                        break;
                  case Instr_BSET :
                        returnValue = Perform_BSET(MBR);
                        break;
                  case Instr_CALL :
                        returnValue = Perform_CALL(MBR, MBR2);
                        break;
                  case Instr_CBI :
                        returnValue = Perform_CBI(MBR);
                        break;
                  case Instr_COM :
                        returnValue = Perform_COM(MBR);
                        break;
                  case Instr_DEC :
                        returnValue = Perform_DEC(MBR);
                        break;
                  case Instr_EICALL :
                        returnValue = Perform_EICALL(MBR);
                        break;
                  case Instr_EIJMP :
                        returnValue = Perform_EIJMP(MBR);
                        break;
                  case Instr_ELPM_I :
                        returnValue = Perform_ELPM_I(MBR);
                        break;
                  case Instr_ELPM_II :
                        returnValue = Perform_ELPM_II(MBR);
                        break;
                  case Instr_ELPM_III  :
                        returnValue = Perform_ELPM_III(MBR);
                        break;
                  case Instr_FMULSU :
                        returnValue = Perform_FMULSU(MBR);
                        break;
                  case Instr_ICALL :
                        returnValue = Perform_ICALL(MBR);
                        break;
                  case Instr_IJMP :
                        returnValue = Perform_IJMP(MBR);
                        break;
                  case Instr_BREAK :
                        returnValue = new String("BREAK");
                        break;
                  case Instr_INC :
                        returnValue = Perform_INC(MBR);
                        break;
                  case Instr_JMP :
                        returnValue = Perform_JMP(MBR, MBR2);
                        break;
                  case Instr_LD_I :
                        returnValue = Perform_LD_I(MBR);
                        break;
                  case Instr_LD_II :
                        returnValue = Perform_LD_II(MBR);
                        break;
                  case Instr_LD_III :
                        returnValue = Perform_LD_III(MBR);
                        break;
                  case Instr_LD_V :
                        returnValue = Perform_LD_V(MBR);
                        break;
                  case Instr_LD_VI :
                        returnValue = Perform_LD_VI(MBR);
                        break;
                  case Instr_LDD_II :
                        returnValue = Perform_LDD_II(MBR);
                        break;
                  case Instr_LDD_III :
                        returnValue = Perform_LDD_III(MBR);
                        break;
                  case Instr_LDD_IV :
                        returnValue = Perform_LDD_IV(MBR);
                        break;
                  case Instr_LDS :
                        returnValue = Perform_LDS(MBR, MBR2);
                        break;
                  case Instr_LPM_I :
            		returnValue= new String("LPM (LPM r0,z)");
                        break;
                  case Instr_LPM_II :
                        returnValue = Perform_LPM_II(MBR);
                        break;
                  case Instr_LPM_III :
                        returnValue = Perform_LPM_III(MBR);
                        break;
                  case Instr_LSR :
                        returnValue = Perform_LSR(MBR);
                        break;
                  case Instr_MUL :
                        returnValue = Perform_MUL(MBR);
                        break;
                  case Instr_NEG :
                        returnValue = Perform_NEG(MBR);
                        break;
                  case Instr_POP :
                        returnValue = Perform_POP(MBR);
                        break;
                  case Instr_PUSH :
                        returnValue = Perform_PUSH(MBR);
                        break;
                  case Instr_RET :
                        returnValue = new String("RET");
                        break;
                  case Instr_RETI :
                        returnValue = new String("RETI");
                        break;
                  case Instr_ROR :
                        returnValue = Perform_ROR(MBR);
                        break;
                  case Instr_SBI :
                        returnValue = Perform_SBI(MBR);
                        break;
                  case Instr_SBIC :
                        returnValue = Perform_SBIC(MBR);
                        break;
                  case Instr_SBIS :
                        returnValue = Perform_SBIS(MBR);
                        break;
                  case Instr_SBIW :
                        returnValue = Perform_SBIW(MBR);
                        break;
                  case Instr_SLEEP :
                        returnValue = new String("SLEEP");
                        break;
                  case Instr_SPM :
                        returnValue = Perform_SPM(MBR);
                        break;
                  case Instr_ST_I :
                        returnValue = Perform_ST_I(MBR);
                        break;
                  case Instr_ST_II :
                        returnValue = Perform_ST_II(MBR);
                        break;
                  case Instr_ST_III :
                        returnValue = Perform_ST_III(MBR);
                        break;
                  case Instr_ST_V :
                        returnValue = Perform_ST_V(MBR);
                        break;
                  case Instr_ST_VI :
                        returnValue = Perform_ST_VI(MBR);
                        break;
                  case Instr_ST_IX :
                        returnValue = Perform_ST_IX(MBR);
                        break;
                  case Instr_ST_X :
                        returnValue = Perform_ST_X(MBR);
                        break;
                  case Instr_STS :
                        returnValue = Perform_STS(MBR);
                        break;
                  case Instr_SWAP :
                        returnValue = Perform_SWAP(MBR);
                        break;
                  case Instr_WDR :
                        returnValue = new String("WDR");
                        break;
                  case Instr_IN :
                        returnValue = Perform_IN(MBR);
                        break;
                  case Instr_OUT :
                        returnValue = Perform_OUT(MBR);
                        break;
                  case Instr_RJMP :
                        returnValue = Perform_RJMP(MBR, PC);
                        break;
                  case Instr_RCALL :
                        returnValue = Perform_RCALL(MBR, PC);
                        break;
                  case Instr_LDI :
                        returnValue = Perform_LDI(MBR);
                        break;
                  case Instr_BLD :
                        returnValue = Perform_BLD(MBR);
                        break;
                  case Instr_BRBC :
                        returnValue = Perform_BRBC(MBR, PC);
                        break;
                  case Instr_BRBS :
                        returnValue = Perform_BRBS(MBR, PC);
                        break;
                  case Instr_BST :
                        returnValue = Perform_BST(MBR);
                        break;
                  case Instr_SBRC :
                        returnValue = Perform_SBRC(MBR);
                        break;
                  case Instr_SBRS :
                        returnValue = Perform_SBRS(MBR);
                        break;
                  default :
                        returnValue = new String("???");
                        break;
            } 
            return returnValue;
      }
      
      //=========================================================================
      /**
      * Decodes the instruction and returns the pneumonic (see constants)
      * @param 16bit memory buffer register
      * @return Integer representing the instruction name (see constants)
      */
      public static int DecodeInstruction(int MBR) throws RuntimeException
      {
            int returnValue = Instr_UNKNOWN;
            
            switch (MBR & 0xf000)
            {
            case 0x0000 :
			//======Add Without Carry=====
			if((MBR & 0xfc00) == 0x0c00)
			    returnValue = Instr_ADD;

			//======Compare with Carry=====
			else if((MBR & 0xfc00) == 0x0400)
			    returnValue = Instr_CPC;

			//======Fractional Multiply Unsigned=====
			else if((MBR & 0xff88) == 0x0308)
			    returnValue = Instr_FMUL;

			//======Fractional Multiply Signed=====
			else if((MBR & 0xff88) == 0x0380)
			    returnValue = Instr_FMULS;

			//======Logical Shift Left=====
			else if((MBR & 0xfc00) == 0x0c00)
			    returnValue = Instr_LSL;

			//======Copy Register Word=====
			else if((MBR & 0xff00) == 0x0100)
			    returnValue = Instr_MOVW;

			//======Multiply Signed=====
			else if((MBR & 0xff00) == 0x0200)
			    returnValue = Instr_MULS;

			//======Multiply Signed with Unsigned=====
			else if((MBR & 0xff88) == 0x0300)
			    returnValue = Instr_MULSU;

			//======No Operation=====
			else if(MBR == 0x0)
			    returnValue = Instr_NOP;

			//======Subtract with Carry=====
			else if((MBR & 0xfc00) == 0x0800)
			    returnValue = Instr_SBC;
			break;
	    case 0x1000:
			//======Add With Carry=====
			if((MBR & 0xfc00) == 0x1c00)
			    returnValue = Instr_ADC;

			//======Compare=====
			else if((MBR & 0xfc00) == 0x1400)
			    returnValue = Instr_CP;

			//======Compare Skip If Equal=====
			else if((MBR & 0xfc00) == 0x1000)
			    returnValue = Instr_CPSE;

			//======Rotate Left through Carry=====
			else if((MBR & 0xfc00) == 0x1c00)
			    returnValue = Instr_ROL;

			//======Subtract without Carry=====
			else if((MBR & 0xfc00) == 0x1800)
			    returnValue = Instr_SUB;
			break;
	    case 0x2000 :
			//======Logical AND=====
			if((MBR & 0xfc00) == 0x2000)
			    returnValue = Instr_AND;

			//======Exclusive Or=====
			else if((MBR & 0xfc00) == 0x2400)
			    returnValue = Instr_EOR;

			//======Copy Register=====
			else if((MBR & 0xfc00) == 0x2c00)
			    returnValue = Instr_MOV;

			//======Logical Or=====
			else if((MBR & 0xfc00) == 0x2800)
			    returnValue = Instr_OR;
			break;
	    case 0x3000 :
		//=====Compare immediate=====
		returnValue = Instr_CPI;
		break;

	    case 0x4000 :
		//======Subtract Immediate with Carry=====
		    returnValue = Instr_SBCI;
		    break;
	    case 0x5000 :
		//======Subtract Immediate=====
		    returnValue = Instr_SUBI;
		    break;
	    case 0x6000 :
		       returnValue = Instr_ORI;
		       break;
	    case 0x7000 :
		//======Logical AND with Immediate=====
		    returnValue = Instr_ANDI;
		    break;
	    case 0x8000 :
			//======Load Indirect from Data Space to Register using index Z - Z Unchanged=====
			if((MBR & 0xfe0f) == 0x8000)
			    returnValue = Instr_LD_IV;

			//======Load Indirect from Data Space to Register using index Z - Z Unchanged, Q displacement=====
			else if((MBR & 0xd208) == 0x8000)
			    returnValue = Instr_LD_VII;

			//======Load Indirect from Data Space to Register using Index Y - Y Unchanged=====
			else if((MBR & 0xfe0f) == 0x8008)
			    returnValue = Instr_LDD_I;

			//======Store Indirect From Register to Data Space using Index Y - Y Unchanged=====
			else if((MBR & 0xfe0f) == 0x8208)
			    returnValue = Instr_ST_IV;

			//======Store Indirect From Register to Data Space using Index Y - Y Unchanged, Q Displacement=====
			else if((MBR & 0xd208) == 0x8208)
			    returnValue = Instr_ST_VII;

			//======Store Indirect From Register to Data Space using Index Z - Z Unchanged=====
			else if((MBR & 0xfe0f) == 0x8200)
			    returnValue = Instr_ST_VIII;

			//======Store Indirect From Register to Data Space using Index Y - Y Unchanged, Q Displacement=====
			else if((MBR & 0xd208) == 0x8200)
			    returnValue = Instr_ST_XI;
			break;
	    case 0x9000 :
			//======Add immediate to word=====
			if((MBR & 0xff00) == 0x9600)
			    returnValue = Instr_ADIW;

			//======Arithmatic shift right=====
			else if((MBR & 0xfe0f) == 0x9405)
			    returnValue = Instr_ASR;
			
			//======Bit Clear in SREG=====
			else if((MBR & 0xff8f) == 0x9488)
			    returnValue = Instr_BCLR;

			//======Bit set in SREG=====
			else if((MBR & 0xff8f) == 0x9408)
			    returnValue = Instr_BSET;

			//======Long Call to a Subroutine=====
			else if((MBR & 0xfe0e) == 0x940e)
			    returnValue = Instr_CALL;

			//======Clear bit in I/O Register=====
			else if((MBR & 0xff00) == 0x9800)
			    returnValue = Instr_CBI;

			//======One's Component=====
			else if((MBR & 0xfe0f) == 0x9400)
			    returnValue = Instr_COM;

			//======Decrement=====
			else if((MBR & 0xfe0f) == 0x940a)
			    returnValue = Instr_DEC;

			//======Extended Indirect Call to Subroutine=====
			else if(MBR == 0x9519)
			    returnValue = Instr_EICALL;

			//======Extended Indirect Jump=====
			else if(MBR == 0x9419)
			    returnValue = Instr_EIJMP;

			//======Extended Load Program Memory - R0 implied=====
			else if(MBR == 0x95d8)
			    returnValue = Instr_ELPM_I;

			//======Extended Load Program Memory - Rd, Z=====
			else if((MBR & 0xfe0f) == 0x9006)
			    returnValue = Instr_ELPM_II;

			//======Extended Load Program Memory - Rd, Z+=====
			else if((MBR & 0xfe0f) == 0x9007)
			    returnValue = Instr_ELPM_III;

			//======Fractional Multiply Signed with Unsigned=====
			else if((MBR & 0xff00) == 0x9800)
			    returnValue = Instr_FMULSU;

			//======Indirect Call to Subroutine=====
			else if(MBR == 0x9509)
			    returnValue = Instr_ICALL;
			
			//======Indirect Jump=====
			else if(MBR == 0x9409)
			    returnValue = Instr_IJMP;

			//======Break=====
			else if(MBR == 0x9598)
			    returnValue = Instr_BREAK;

			//======Increment=====
			else if((MBR & 0xfe0f) == 0x9403)
			    returnValue = Instr_INC;

			//======Jump=====
			else if((MBR & 0xfe0e) == 0x940c)
			    returnValue = Instr_JMP;

			//======Load Indirect from Data Space to Register using index X - X Unchanged=====
			else if((MBR & 0xfe0f) == 0x900c)
			    returnValue = Instr_LD_I;

			//======Load Indirect from Data Space to Register using index X - Post Incremented=====
			else if((MBR & 0xfe0f) == 0x900d)
			    returnValue = Instr_LD_II;

			//======Load Indirect from Data Space to Register using index X - Pre Decrement=====
			else if((MBR & 0xfe0f) == 0x900e)
			    returnValue = Instr_LD_III;

			//======Load Indriect from Data Space to Register using index Z - Post Increment=====
			else if((MBR & 0xfe0f) == 0x9001)
			    returnValue = Instr_LD_V;

			//======Load Indirect from Data Space to Register using index Z - Post Decrement=====
			else if((MBR & 0xfe0f) == 0x9002)
			    returnValue = Instr_LD_VI;

			//======Load Indirect from Data Space to Register using Index Y - Post Increment=====
			else if((MBR & 0xfe0f) == 0x9009)
			    returnValue = Instr_LDD_II;

			//======Load Indirect from Data Space to Register using Index Y - Pre Decrement=====
			else if((MBR & 0xfe0f) == 0x900a)
			    returnValue = Instr_LDD_III;

			//======Load Indirect from Data Space to Register using Index Y - Y Unchanged, Q Displacement=====
			else if((MBR & 0xff00) == 0x9800)
			    returnValue = Instr_LDD_IV;

			//======Load Direct from Data Space=====
			else if((MBR & 0xfe0f) == 0x9000)
			    returnValue = Instr_LDS;

			//======Load Program Memory - Z unchanged, R0 Implied=====
			else if(MBR == 0x95c8)
			    returnValue = Instr_LPM_I;

			//======Load Program Memory - Z unchanged=====
			else if((MBR & 0xfe0f) == 0x9004)
			    returnValue = Instr_LPM_II;

			//======Load Program Memory - Z Post Increment=====
			else if((MBR & 0xfe0f) == 0x9005)
			    returnValue = Instr_LPM_III;

			//======Logical Shift Right=====
			else if((MBR & 0xfe0f) == 0x9406)
			    returnValue = Instr_LSR;

			//======Multiply Unsigned=====
			else if((MBR & 0xfc00) == 0x9c00)
			    returnValue = Instr_MUL;

			//======Two's complement=====
			else if((MBR & 0xfe0f) == 0x9401)
			    returnValue = Instr_NEG;

			//======Pop Register from Stack=====
			else if((MBR & 0xfe0f) == 0x900f)
			    returnValue = Instr_POP;
			
			//======Push Register on Stack=====
			else if((MBR & 0xfe0f) == 0x920f)
			    returnValue = Instr_PUSH;

			//======Return from Subroutine=====
			else if(MBR == 0x9508)
			    returnValue = Instr_RET;

			//======Return from Interrupt=====
			else if(MBR == 0x9518)
			    returnValue = Instr_RETI;

			//======Rotate Right through Carry=====
			else if((MBR & 0xfe0f) == 0x9407)
			    returnValue = Instr_ROR;

			//======Set Bit in I/O Register=====
			else if((MBR & 0xff00) == 0x9a00)
			    returnValue = Instr_SBI;

			//======Skip if Bit in I/O Register is Cleared=====
			else if((MBR & 0xff00) == 0x9900)
			    returnValue = Instr_SBIC;

			//======Skip if Bit in I/O Register is Set=====
			else if((MBR & 0xff00) == 0x9b00)
			    returnValue = Instr_SBIS;

			//======Subtract Immediate from Word=====
			else if((MBR & 0xff00) == 0x9700)
			    returnValue = Instr_SBIW;

			//======Sets Sleep Mode=====
			else if(MBR == 0x9588)
			    returnValue = Instr_SLEEP;

			//======Store Program Memory=====
			else if(MBR == 0x95e8)
			    returnValue = Instr_SPM;

			//======Store Indirect From Register to Data Space using Index X - X Unchanged=====
			else if((MBR & 0xfe0f) == 0x920c)
			    returnValue = Instr_ST_I;

			//======Store Indirect From Register to Data Space using Index X - Post Increment=====
			else if((MBR & 0xfe0f) == 0x920d)
			    returnValue = Instr_ST_II;

			//======Store Indirect From Register to Data Space using Index X - Pre Decrement=====
			else if((MBR & 0xfe0f) == 0x920e)
			    returnValue = Instr_ST_III;

			//======Store Indirect From Register to Data Space using Index Y - Post Increment=====
			else if((MBR & 0xfe0f) == 0x9209)
			    returnValue = Instr_ST_V;

			//======Store Indirect From Register to Data Space using Index Y - Pre Decrement=====
			else if((MBR & 0xfe0f) == 0x920a)
			    returnValue = Instr_ST_VI;

			//======Store Indirect From Register to Data Space using Index Z - Post Increment=====
			else if((MBR & 0xfe0f) == 0x9201)
			    returnValue = Instr_ST_IX;

			//======Store Indirect From Register to Data Space using Index Z - Pre Decrement=====
			else if((MBR & 0xfe0f) == 0x9202)
			    returnValue = Instr_ST_X;

			//======Store Direct to Data Space=====
			else if((MBR & 0xfe0f) == 0x9200)
			    returnValue = Instr_STS;

			//======Swap Nibbles=====
			else if((MBR & 0xfe0f) == 0x9402)
			    returnValue = Instr_SWAP;

			//======Watchdog Reset=====
			else if(MBR == 0x95a8)
			    returnValue = Instr_WDR;
			break;
	    case 0xb000 :
			//======Load an I/O Location to Register=====
			if((MBR & 0xf800) == 0xb000)
			    returnValue = Instr_IN;
			//======Store Register to I/O Location=====
			else if((MBR & 0xf800) == 0xb800)
			    returnValue = Instr_OUT;
			break;
	    case 0xc000 :
		//======Relative Jump=====
		    returnValue = Instr_RJMP;
		    break;
	    case 0xd000 :
		//======Relative Call to Subroutine=====
		    returnValue = Instr_RCALL;
		    break;
	    case 0xe000 :
		//======Load Immediate=====
		    returnValue = Instr_LDI;
		    break;
	    case 0xf000 :
			//======Bit load from the T Flag in SREG to a bit in Register=====
			if((MBR & 0xfe08) == 0xf800)
			    returnValue = Instr_BLD;

			//======Branch if bit in SREG is cleared=====
			else if((MBR & 0xfc00) == 0xf400)
			    returnValue = Instr_BRBC;

			//======Branch if bit in SREG is set=====
			else if((MBR & 0xfc00) == 0xf000)
			    returnValue = Instr_BRBS;

			//======Bit Store from Bit in Register to T Flag in SREG=====
			else if((MBR & 0xfe08) == 0xfa00)
			    returnValue = Instr_BST;

			//======Skip if Bit in Register is Cleared=====
			else if((MBR & 0xfe08) == 0xfc00)
			    returnValue = Instr_SBRC;

			//======Skip if Bit in Register is Set=====
			else if((MBR & 0xfe08) == 0xfe00)
			    returnValue = Instr_SBRS;
			break;
	    }
	    if (returnValue == Instr_UNKNOWN)
	           throw new RuntimeException(Constants.Error(Constants.INVALID_INSTRUCTION));
	    else
      	    return returnValue;
      }
      
	private static String Perform_ADD(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		r = Utils.GetOperand_XXXXXX1XXXXX1111(MBR);
		return new String("ADD r"+Utils.hex(d)+", r"+Utils.hex(r));
	}

	private static String Perform_ADC(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		r = Utils.GetOperand_XXXXXX1XXXXX1111(MBR);
		return new String("ADC r"+Utils.hex(d)+", r"+Utils.hex(r));
	}

	private static String Perform_ADIW(int MBR)
	{
	    d = Utils.GetOperand_XXXXXXXXXX11XXXX(MBR);
	    K = Utils.GetOperand_XXXXXXXX11XX1111(MBR);
	    return new String("ADIW r"+Utils.hex(d)+", "+Utils.hex(K));
	}

	private static String Perform_AND(int MBR)
	{
	      String returnValue;
            d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
            r = Utils.GetOperand_XXXXXX1XXXXX1111(MBR);
            returnValue = new String("AND r"+Utils.hex(d)+", r"+Utils.hex(r));
            // If both registers are the same, this may be a TST instruction
            if (d == r)
                  returnValue += " (TST r"+Utils.hex(d)+")";
            return returnValue;
      }

	private static String Perform_ANDI(int MBR)
	{
	    d = Utils.GetOperand_XXXXXXXX1111XXXX(MBR);
	    K = Utils.GetOperand_XXXX1111XXXX1111(MBR);
	    return new String("ANDI r"+Utils.hex(d)+", "+Utils.hex(K));
	}

	private static String Perform_ASR(int MBR)
	{
	    d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
	    return new String("ASR r"+Utils.hex(d));
	}

	private static String Perform_BCLR(int MBR)
	{
            String returnValue;
            s = Utils.GetOperand_XXXXXXXXX111XXXX(MBR);
            returnValue = new String("BCLR "+Utils.hex(s));
            switch (s)
            {
                  case 0 :    returnValue += " (CLC)";
                              break;
                  case 1 :    returnValue += " (CLZ)";
                              break;
                  case 2 :    returnValue += " (CLN)";
                              break;
                  case 3 :    returnValue += " (CLV)";
                              break;
                  case 4 :    returnValue += " (CLS)";
                              break;
                  case 5 :    returnValue += " (CLH)";
                              break;
                  case 6 :    returnValue += " (CLT)";
                              break;
                  case 7 :    returnValue += " (CLI)";
                              break;
            }
            return returnValue;
	}

	private static String Perform_BLD(int MBR)
	{
	    d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
	    b = Utils.GetOperand_XXXXXXXXXXXXX111(MBR);

	    return new String("BLD r"+Utils.hex(d)+", "+Utils.hex(b));
	}

	private static String Perform_BRBC(int MBR, int PC)
	{
	     String returnValue;
            k = Utils.GetOperand_XXXXXX1111111XXX(MBR);
            s = Utils.GetOperand_XXXXXXXXXXXXX111(MBR);
            int newPC = PC + Utils.branch_offset(k);

            returnValue = new String("BRBC "+Utils.hex(s)+", "+Utils.hex(newPC));
            switch (s)
            {
                  case 0 :    returnValue += " (BRCC "+Utils.hex(newPC)+")";
                              break;
                  case 1 :    returnValue += " (BRNE "+Utils.hex(newPC)+")";
                              break;
                  case 2 :    returnValue += " (BRPL "+Utils.hex(newPC)+")";
                              break;
                  case 3 :    returnValue += " (BRVC "+Utils.hex(newPC)+")";
                              break;
                  case 4 :    returnValue += " (BRGE "+Utils.hex(newPC)+")";
                              break;
                  case 5 :    returnValue += " (BRHC "+Utils.hex(newPC)+")";
                              break;
                  case 6 :    returnValue += " (BRTC "+Utils.hex(newPC)+")";
                              break;
                  case 7 :    returnValue += " (BRID "+Utils.hex(newPC)+")";
                              break;
            }
            return returnValue;
	}

	private static String Perform_BRBS(int MBR, int PC)
	{
            String returnValue;
            k = Utils.GetOperand_XXXXXX1111111XXX(MBR);
            s = Utils.GetOperand_XXXXXXXXXXXXX111(MBR);

            int newPC = PC + Utils.branch_offset(k);
            returnValue = new String("BRBS "+Utils.hex(s)+", "+Utils.hex(newPC));
            switch (s)
            {
                  case 0 :    returnValue += " (BRCS "+Utils.hex(newPC)+")";
                              break;
                  case 1 :    returnValue += " (BREQ "+Utils.hex(newPC)+")";
                              break;
                  case 2 :    returnValue += " (BRMI "+Utils.hex(newPC)+")";
                              break;
                  case 3 :    returnValue += " (BRVS "+Utils.hex(newPC)+")";
                              break;
                  case 4 :    returnValue += " (BRLT "+Utils.hex(newPC)+")";
                              break;
                  case 5 :    returnValue += " (BRHS "+Utils.hex(newPC)+")";
                              break;
                  case 6 :    returnValue += " (BRTS "+Utils.hex(newPC)+")";
                              break;
                  case 7 :    returnValue += " (BRIE "+Utils.hex(newPC)+")";
                              break;
            }
            return returnValue;
	}

	private static String Perform_BSET(int MBR)
	{
            String returnValue = new String();
            s = Utils.GetOperand_XXXXXXXXX111XXXX(MBR);
            returnValue = "BSET "+Utils.hex(s);
            switch (s)
            {
                  case 0 :    returnValue += " (SEC)";
                              break;
                  case 1 :    returnValue += " (SEZ)";
                              break;
                  case 2 :    returnValue += " (SEN)";
                              break;
                  case 3 :    returnValue += " (SEV)";
                              break;
                  case 4 :    returnValue += " (SES)";
                              break;
                  case 5 :    returnValue += " (SEH)";
                              break;
                  case 6 :    returnValue += " (SET)";
                              break;
                  case 7 :    returnValue += " (SEI)";
                              break;
            }
            return returnValue;
	}

	private static String Perform_BST(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		b = Utils.GetOperand_XXXXXXXXXXXXX111(MBR);
		return new String("BST r"+Utils.hex(d)+", "+Utils.hex(b));
	}

	private static String Perform_CALL(int MBR, int MBR2)
	{
	     int k = Utils.GetOperand_XXXXXXX11111XXX1(MBR);
            return new String("CALL "+Utils.hex(k + MBR2));
	}

	private static String Perform_CBI(int MBR)
	{
	    A = Utils.GetOperand_XXXXXXXX11111XXX(MBR);
	    b = Utils.GetOperand_XXXXXXXXXXXXX111(MBR);
	    return new String("CBI "+Utils.hex(A)+", "+Utils.hex(b));
	}

	private static String Perform_COM(int MBR)
	{
	    d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
	    return new String("COM r"+Utils.hex(d));
	}

	private static String Perform_CP(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		r = Utils.GetOperand_XXXXXX1XXXXX1111(MBR);
		return new String("CP r"+Utils.hex(d)+", r"+Utils.hex(r));
	}

	private static String Perform_CPC(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		r = Utils.GetOperand_XXXXXX1XXXXX1111(MBR);
		return new String("CPC r"+Utils.hex(d)+", r"+Utils.hex(r));
	}

	private static String Perform_CPI(int MBR)
	{
	    K = Utils.GetOperand_XXXX1111XXXX1111(MBR);
	    d = Utils.GetOperand_XXXXXXXX1111XXXX(MBR);
	    return new String("CPI r"+Utils.hex(d)+", "+Utils.hex(K));
	}

	private static String Perform_CPSE(int MBR)
	{
	    d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
	    r = Utils.GetOperand_XXXXXX1XXXXX1111(MBR);
	    return new String("CPSE r"+Utils.hex(d)+", r"+Utils.hex(r));
	}

	private static String Perform_DEC(int MBR)
	{
	    d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
	    return new String("DEC r"+Utils.hex(d));
	}

	private static String Perform_EICALL(int MBR)
	{
		// TO DO:  Implement
		return new String("EICALL (NOT IMPLEMENTED YET)");
	}

	private static String Perform_EIJMP(int MBR)
	{
		// TO DO:  Implement
		return new String("EIJMP (NOT IMPLEMENTED YET)");
	}
	private static String Perform_ELPM_I(int MBR)
	{
		// TO DO:  Implement
		return new String("ELPM (NOT IMPLEMENTED YET)");
	}
	private static String Perform_ELPM_II(int MBR)
	{
		// TO DO:  Implement
		return new String("ELPM (NOT IMPLEMENTED YET)");
	}
	private static String Perform_ELPM_III(int MBR)
	{
		// TO DO:  Implement
		return new String("ELPM (NOT IMPLEMENTED YET)");
	}

	private static String Perform_EOR(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		r = Utils.GetOperand_XXXXXX1XXXXX1111(MBR);

		return new String("EOR r"+Utils.hex(d)+", r"+Utils.hex(r));
	}
	private static String Perform_FMUL(int MBR)
	{
		// TO DO:  Implement
		return new String("FMUL (NOT IMPLEMENTED YET)");
	}
	private static String Perform_FMULS(int MBR)
	{
		// TO DO:  Implement
		return new String("FMULS (NOT IMPLEMENTED YET)");
	}
	private static String Perform_FMULSU(int MBR)
	{
		// TO DO:  Implement
		return new String("FMULSU (NOT IMPLEMENTED YET)");
	}
	private static String Perform_ICALL(int MBR)
	{
		// TO DO:  Implement
		return new String("ICALL (NOT IMPLEMENTED YET)");
	}

	private static String Perform_IJMP(int MBR)
	{
		// TO DO:  Implement
		return new String("IJMP (NOT IMPLEMENTED YET)");
	}

	private static String Perform_IN(int MBR)
	{
	    d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
	    A = Utils.GetOperand_XXXXX11XXXXX1111(MBR);
	    return new String("IN r"+Utils.hex(d)+", "+Utils.hex(A));
	}

	private static String Perform_INC(int MBR)
	{
	    d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
	    return new String("INC r"+Utils.hex(d));
	}

	private static String Perform_JMP(int MBR, int MBR2)
	{
	    int PC = Utils.GetOperand_XXXXXXX11111XXX1(MBR) + MBR2;
          return new String("JMP "+Utils.hex(PC));
	}

	private static String Perform_LD_I(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("LD r"+Utils.hex(d)+", x");
	}

	private static String Perform_LD_II(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("LD r"+Utils.hex(d)+", x+");
	}

	private static String Perform_LD_III(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("LD r"+Utils.hex(d)+", -x");
	}

	private static String Perform_LD_IV(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("LD r"+Utils.hex(d)+", z");
	}
	private static String Perform_LD_V(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("LD r"+Utils.hex(d)+", z+");
	}
	private static String Perform_LD_VI(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("LD r"+Utils.hex(d)+", -z");
	}

	private static String Perform_LD_VII(int MBR)
	{
		q = Utils.GetOperand_XX1X11XXXXXXX111(MBR);
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("LD r"+Utils.hex(d)+", z+q");
	}

	private static String Perform_LDD_I(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
            return new String("LD r"+Utils.hex(d)+", y");
	}

	private static String Perform_LDD_II(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
            return new String("LD r"+Utils.hex(d)+", y+");
	}
	
	private static String Perform_LDD_III(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
            return new String("LD r"+Utils.hex(d)+", -y");
	}
	
	private static String Perform_LDD_IV(int MBR)
	{
		q = Utils.GetOperand_XX1X11XXXXXXX111(MBR);
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
            return new String("LD r"+Utils.hex(d)+", y+q");
	}

	private static String Perform_LDI(int MBR)
	{
		d = Utils.GetOperand_XXXXXXXX1111XXXX(MBR);
		K = Utils.GetOperand_XXXX1111XXXX1111(MBR);
		return new String("LDI r"+Utils.hex(d)+", "+Utils.hex(K));
	}

	private static String Perform_LDS(int MBR, int MBR2)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("LDS r"+Utils.hex(d)+", "+Utils.hex(MBR2));
	}

	private static String Perform_LPM_II(int MBR)
	{
		int d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("LPM r"+Utils.hex(d)+", z");
	}

	private static String Perform_LPM_III(int MBR)
	{
		int d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("LPM r"+Utils.hex(d)+", z+");
	}

	private static String Perform_LSL(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("LSL r"+Utils.hex(d));
	}

	private static String Perform_LSR(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("LSR r"+Utils.hex(d));
	}

	private static String Perform_MOV(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		r = Utils.GetOperand_XXXXXX1XXXXX1111(MBR);
		return new String("MOV r"+Utils.hex(d)+", r"+Utils.hex(r));
	}

	private static String Perform_MOVW(int MBR)
	{
		d = Utils.GetOperand_XXXXXXXX1111XXXX(MBR);
		r = Utils.GetOperand_XXXXXXXXXXXX1111(MBR);

		return new String("MOVW r"+Utils.hex(d)+", r"+Utils.hex(r));
	}

	private static String Perform_MUL(int MBR)
	{
	    d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
	    r = Utils.GetOperand_XXXXXX1XXXXX1111(MBR);
	    return new String("MUL r"+Utils.hex(d)+", r"+Utils.hex(r));
	}

	private static String Perform_MULS(int MBR)
	{
		// TO DO:  Implement
		return new String("MULS (NOT IMPLEMENTED YET)");
	}

	private static String Perform_MULSU(int MBR)
	{
		// TO DO:  Implement
		return new String("MULSU (NOT IMPLEMENTED YET)");
	}
	
	private static String Perform_NEG(int MBR)
	{
	    d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
	    return new String("NEG r"+Utils.hex(d));
	}

	private static String Perform_OR(int MBR)
	{
	    d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
	    r = Utils.GetOperand_XXXXXX1XXXXX1111(MBR);
	    return new String("OR r"+Utils.hex(d)+", r"+Utils.hex(r));
	}

	private static String Perform_ORI(int MBR)
	{
	    d = Utils.GetOperand_XXXXXXXX1111XXXX(MBR);
	    K = Utils.GetOperand_XXXX1111XXXX1111(MBR);
	    return new String("ORI r"+Utils.hex(d)+", "+Utils.hex(K));
	}

	private static String Perform_OUT(int MBR)
	{
	    r = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
	    A = Utils.GetOperand_XXXXX11XXXXX1111(MBR);
	    return new String("OUT "+Utils.hex(A)+", r"+Utils.hex(r));
	}

	private static String Perform_POP(int MBR)
	{
	    d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
	    return new String("POP r"+Utils.hex(d));
	}

	private static String Perform_PUSH(int MBR)
	{
	    d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
	    return new String("PUSH r"+Utils.hex(d));
	}

	private static String Perform_RCALL(int MBR, int PC)
	{
	    k = Utils.GetOperand_XXXX111111111111(MBR);
	    int newPC = PC + Utils.branch_offset_jmp(k);
 	    return new String("RCALL "+Utils.hex(newPC));
	}

	private static String Perform_RJMP(int MBR, int PC)
	{
		k=Utils.GetOperand_XXXX111111111111(MBR);
            int newPC = PC + Utils.branch_offset_jmp(k);
		return new String("RJMP "+Utils.hex(newPC));
	}

	private static String Perform_ROL(int MBR)
	{
		d = Utils.GetOperand_XXXXXX1111111111(MBR);
		return new String("ROL r"+Utils.hex(d));
	}

	private static String Perform_ROR(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("ROR r"+Utils.hex(d));
	}

	private static String Perform_SBC(int MBR)
	{
		// TO DO:  Implement
		return new String("SBC (NOT IMPLEMENTED YET)");
	}

	private static String Perform_SBCI(int MBR)
	{
		// TO DO:  Implement
		return new String("SBCI (NOT IMPLEMENTED YET)");
	}

	private static String Perform_SBI(int MBR)
	{
		A = Utils.GetOperand_XXXXXXXX11111XXX(MBR);
		b = Utils.GetOperand_XXXXXXXXXXXXX111(MBR);

		return new String("SBI "+Utils.hex(A)+", "+Utils.hex(b));
	}

	private static String Perform_SBIC(int MBR)
	{
		A = Utils.GetOperand_XXXXXXXX11111XXX(MBR);
		b = Utils.GetOperand_XXXXXXXXXXXXX111(MBR);

		return new String("SBIC "+Utils.hex(A)+", "+Utils.hex(b));
	}

	private static String Perform_SBIS(int MBR)
	{
		A = Utils.GetOperand_XXXXXXXX11111XXX(MBR);
		b = Utils.GetOperand_XXXXXXXXXXXXX111(MBR);

		return new String("SBIS "+Utils.hex(A)+", "+Utils.hex(b));
	}

	private static String Perform_SBIW(int MBR)
	{
		// TO DO:  Implement
	     return new String("SBIW (NOT IMPLEMENTED YET)");
	}

	private static String Perform_SBRC(int MBR)
	{
		r = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		b = Utils.GetOperand_XXXXXXXXXXXXX111(MBR);

		return new String("SBRC r"+Utils.hex(r)+", "+Utils.hex(b));
	}

	private static String Perform_SBRS(int MBR)
	{
		r = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		b = Utils.GetOperand_XXXXXXXXXXXXX111(MBR);

		return new String("SBRS r"+Utils.hex(r)+", "+Utils.hex(b));
	}

	private static String Perform_SPM(int MBR)
	{
		return new String("SPM (NOT IMPLEMENTED YET)");
	}

	private static String Perform_ST_I(int MBR)
	{
		r = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("ST x, r"+Utils.hex(r));
	}

	private static String Perform_ST_II(int MBR)
	{
		r = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("ST x+, r"+Utils.hex(r));
	}

	private static String Perform_ST_III(int MBR)
	{
		r = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("ST -x, r"+Utils.hex(r));
	}

	private static String Perform_ST_IV(int MBR)
	{
		r = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("ST y, r"+Utils.hex(r));
	}

	private static String Perform_ST_V(int MBR)
	{
		r = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("ST y+, r"+Utils.hex(r));
	}

	private static String Perform_ST_VI(int MBR)
	{
		r = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("ST -y, r"+Utils.hex(r));
	}

	private static String Perform_ST_VII(int MBR)
	{
		q = Utils.GetOperand_XX1X11XXXXXXX111(MBR);
		r = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("STD y+"+q+", r"+Utils.hex(r));
	}

	private static String Perform_ST_VIII(int MBR)
	{
		r = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("ST z, r"+Utils.hex(r));
	}

	private static String Perform_ST_IX(int MBR)
	{
		r = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("ST z+, r"+Utils.hex(r));
	}

	private static String Perform_ST_X(int MBR)
	{
		r = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("ST -z, r"+Utils.hex(r));
	}

	private static String Perform_ST_XI(int MBR)
	{
		r = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		q = Utils.GetOperand_XX1X11XXXXXXX111(MBR);
		return new String("STD z+"+q+", r"+Utils.hex(r));	
	}

	private static String Perform_STS(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("STS k, r"+Utils.hex(d));
	}

	private static String Perform_SUB(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		r = Utils.GetOperand_XXXXXX1XXXXX1111(MBR);
		return new String("SUB r"+Utils.hex(d)+", r"+Utils.hex(r));
	}

	private static String Perform_SUBI(int MBR)
	{
		d = Utils.GetOperand_XXXXXXXX1111XXXX(MBR);
		K = Utils.GetOperand_XXXX1111XXXX1111(MBR);
		return new String("SUBI r"+Utils.hex(d)+", "+Utils.hex(K));
	}

	private static String Perform_SWAP(int MBR)
	{
		d = Utils.GetOperand_XXXXXXX11111XXXX(MBR);
		return new String("SWAP r"+Utils.hex(d));
	}
}
