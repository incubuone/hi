public class test {
    public static void main(String[] args) {
        /*String a="1";
        if("1"==a){
            System.out.println("111");
        }
        System.out.println("222");*/
        String str="abc-->123";
        String[] values=str.split("-->");
        System.out.println(values[0]);
        System.out.println(values[1]);
    }
}
