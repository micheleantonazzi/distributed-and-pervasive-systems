public aspect test{
    before():call(* *.*(..)) && !within(test){
        System.out.println("aspect");
    }
}