.data
str1: .asciiz "Please enter a: "
str2: .asciiz "Please enter b: "
str3: .asciiz "Please enter c: "
output: .space 100
newline: .asciiz "\n"

.text

.globl main
main:
la $a0, str1
addi $v0, $0, 4
syscall #prints message asking for user input (a)
addi $v0, $0, 5
syscall #asks for user input, stores result in $v0
add $t0, $0, $v0 #stores a in t0
la $a0, str2
addi $v0, $0, 4
syscall #prints message asking for user input (b)
addi $v0, $0, 5
syscall #asks for user input, stores result in $v0
add $t1, $0, $v0 #stores b in t1
la $a0, str3
addi $v0, $0, 4
syscall #prints message asking for user input (c)
addi $v0, $0, 5
syscall #asks for user input, stores result in $v0
add $t2, $0, $v0 #stores c in t2
li $t3, 0 #stores 0 in our iterator register
la $t5, output #stores address of output in $t5

Loop:
slt $t7, $t2, $t3 #returns 1 if x>c, 0 if x<=c
bgtz $t7, End #go to end if x>c
mult $t3, $t3 #stores x^2 in $LO
mflo $t4 #loads x^2 into $t4
rem $t4, $t4, $t1 #stores x^2 mod b in $t4
rem $t6, $t0, $t1 #stores a mod(b) in $t6 
bne $t6, $t4, Next #go to Next subroutine if x does not satisfy equation in question
la $t6, output #loads address of output into $t6 (we don't need that register until we loop again)
sb $t3, 0($t6) #loads x into $t3
la $a0, 0($t3) #loads x into $a0 to print it
li $v0, 1 #readies print integer command
syscall #prints the value of x that works in formula
la $a0, newline #loads newline into $a0
li $v0, 4 #readies print string command
syscall #prints newline


Next:
addi $t3, $t3, 1 #go to next x
j Loop #restart the loop with new x

End:
li $v0, 10 #readies the exit command
syscall #exits
