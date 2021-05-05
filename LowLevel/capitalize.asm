# This program illustrates an exercise of capitalizing a string.
# The test string is hardcoded. The program should capitalize the input string
# Comment your work

	.data

inputstring: 	.asciiz "HeLlo TesTIng 123 HelLO"
outputstring:	.space 200


	.text
	.globl main
main: 
la $t1, inputstring #loads address of first char of string into t1
la $t7, outputstring #loads addres of first char of output string into t7

Loop: 
lb $t2, 0($t1) #loads byte at t1 (the string) into t2
beqz $t2, End  #checks if $t2 contains anything, goes to end if not
addi $t4, $0, 90 #sets $t4 as 90 (to get the right offset in the ASCII table
sub $t3, $t4, $t2 #does $t3=$t4-$t2
bgez $t3, Next #goes to Next if $t3 >= 0 (i.e. if the character was uppercase)
sub $t2, $t2, 32 #goes from lowercase index to uppercase index for ASCII values

Next: 
sb $t2, 0($t7) #stores the character from the input in the output
add $t1, $t1, 1 #adds 1 to $t1 (to go the next character)
add $t7, $t7, 1 #adds 1 to $t7 (to move to next character in output)
j Loop #goes back to beginning (next character)

End: la $a0, outputstring #loads the address of the output string into $a0
li $v0, 4 #readies the print command
syscall #prints
li $v0, 10 #readies the exit command
syscall #exits
