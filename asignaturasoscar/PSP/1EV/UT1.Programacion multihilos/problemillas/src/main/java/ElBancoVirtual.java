import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class ElBancoVirtual {
    /*
    Simula un sistema bancario donde múltiples clientes realizan operaciones
    concurrentes sobre una cuenta bancaria.

    Especificaciones técnicas:
        - Saldo inicial: 10.000€
        - Clientes simultáneos: 50 hilos
        - Operaciones por cliente: 10 operaciones aleatorias cada uno

    Tipos de operaciones:
        - Retiro (60% probabilidad): Entre 1€ y 100€
        - Ingreso (40% probabilidad): Entre 1€ y 50€
        - Tiempo por operación: 100-300ms (simular latencia bancaria)

    Implementaciones requeridas:
        - Con ReentrantLock - Usando locks explícitos
        - Con synchronized - Usando sincronización implícita
        - Con volatile - Para demostrar que NO es suficiente
     */

    private double saldo;

    private int exito=0;
    private int fallo=0;

    public synchronized boolean retirar(double cantidad){
        if(saldo>0){
            saldo-=cantidad;
            return true;
        } else {
            return false;
        }
    }

    public synchronized void ingresar(double cantidad){
        saldo+=cantidad;
    }

    public double consultarSaldo(){
        return saldo;
    }

    public List<String> obtenerHistorial(){
        return null;
    }

    public static Random random = new Random();

    public static ReentrantLock lock = new ReentrantLock();

    public static Thread[] clientes = new Thread[50];

    void main(){
        IO.println("Saldo inicial: 10000.00€");
        IO.println("50 clientes realizando 500 operaciones totales...");
        long tiempoInicial = System.currentTimeMillis();
        ElBancoVirtual bancoVirtual = new ElBancoVirtual();
        for(int i=0;i<50;i++){
            clientes[i] = new Thread(() -> {
                try {
                    Thread.sleep(random.nextInt(1000,5000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for (int tarea = 0; tarea < 10; tarea++) {
                    if (tarea%2==0){
                        if(bancoVirtual.retirar(random.nextDouble(1, 100))){
                            bancoVirtual.exito++;
                        }else{
                            fallo++;
                        }
                    } else {
                        bancoVirtual.ingresar(random.nextDouble(1, 50));
                        bancoVirtual.exito++;
                    }
                }
            });
            clientes[i].start();
        }
        for (int i = 0; i < 50; i++) {
            try {
                clientes[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        long tiempoFinal = System.currentTimeMillis();
        long tiempoTotal = tiempoFinal - tiempoInicial;
        double segundos = tiempoTotal % 1000.0;

        IO.println("Saldo final: "+saldo+"€");
        IO.println("Operaciones exitosas: "+exito);
        IO.println("Operaciones fallidas: "+fallo);
        IO.println("Tiempo total: "+segundos+" s");
    }





}
