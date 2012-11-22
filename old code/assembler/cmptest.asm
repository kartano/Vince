	LDI r16,0
	LDI r17,1
	CP r16,r17
	BRNE NotEqual
	CLC
	BREAK
NotEqual:	SEC
		BREAK
	