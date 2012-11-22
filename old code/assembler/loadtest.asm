.DEF MyPreferredRegister = R16
.DEF AnotherRegister = R15
	
     LDI MyPreferredRegister, 150
     MOV AnotherRegister, MyPreferredRegister
     BREAK
	