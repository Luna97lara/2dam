public class Main {
    public static void main(String[] args) {
        //Creamos los objetos:
        DescargaExtendsThreads descargaExtendsThreads1 = new DescargaExtendsThreads();
        DescargaExtendsThreads descargaExtendsThreads2 = new DescargaExtendsThreads();
        DescargaExtendsThreads descargaExtendsThreads3 = new DescargaExtendsThreads();
        DescargaExtendsThreads descarga4 = new DescargaExtendsThreads();

        //Los arrancamos:
        descargaExtendsThreads1.start();
        descargaExtendsThreads2.start();
        descargaExtendsThreads3.start();
        descarga4.start();
        //Java crea/inicia la ejecución de ese hilo y acaba ejecutando run()

        //Creamos los objetos:
        DescargaImplementsRunnable descargaImplementsRunnable1 = new DescargaImplementsRunnable();
        DescargaImplementsRunnable descargaImplementsRunnable2 = new DescargaImplementsRunnable();
        DescargaImplementsRunnable descargaImplementsRunnable3 = new DescargaImplementsRunnable();
        DescargaImplementsRunnable descargaImplementsRunnable4 = new DescargaImplementsRunnable();

        //"Crea un hilo que ejecute este Runnable"
        Thread t1 = new Thread(descargaImplementsRunnable1);
        Thread t2 = new Thread(descargaImplementsRunnable2);
        Thread t3 = new Thread(descargaImplementsRunnable3);
        Thread t4 = new Thread(descargaImplementsRunnable4);

        //Los arrancamos:
        t1.start();
        t2.start();
        t3.start();
        t4.start();




    }
}
