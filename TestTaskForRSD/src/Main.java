
public class Main {

    //Threads count as you can see
    private static final int THREADS_COUNT = 30;
    public static void main(String[] args) {



        for (int i = 0; i < THREADS_COUNT; i++) {
            new Thread(new BruteThread()).start();
        }
    }


}
