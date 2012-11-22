	      rjmp main
int0:	      break
int1:       break

main:	      cli               ; Make sure no interrupts happen yet
            ldi r16, 255
            out $3b,r16   ; Set interrupt mask in GIMSK register for ints 0 and 1
            ldi r16, $00
            out $3a,r16   ; Clear the GIFR register bits
            ldi r16, $80
            out $3d, r16      ; Set stack pointer to 0x0080
            sei          ; Enable global interrupts
loop:       inc r1       ; Infinite loop
		rjmp loop
	
