public class DescargaImplementsRunnable implements Runnable{ //NO es un hilo, sino que contiene el trabajo a ejecutar
    @Override
    public void run() {
        for(int progreso = 0; progreso <=100; progreso+=20){
            System.out.println(Thread.currentThread().getName()+" "+progreso+"%");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
