import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Problema2 {
    /*
    Simula un sistema bancario donde múltiples clientes realizan operaciones concurrentes sobre una cuenta bancaria.

    Especificaciones técnicas:
        Saldo inicial: 10.000€
        Clientes simultáneos: 50 hilos
        Operaciones por cliente: 10 operaciones aleatorias cada uno
        Tipos de operaciones:
        Retiro (60% probabilidad): Entre 1€ y 100€
        Ingreso (40% probabilidad): Entre 1€ y 50€
        Tiempo por operación: 100-300ms (simular latencia bancaria)
    Restricciones:
        El saldo nunca puede ser negativo
        Si no hay fondos suficientes, la operación debe fallar
        Todas las operaciones deben quedar registradas con timestamp
    Implementaciones requeridas:
        Con ReentrantLock - Usando locks explícitos
        Con synchronized - Usando sincronización implícita
        Con volatile - Para demostrar que NO es suficiente

                    === BANCO VIRTUAL ===
                    Saldo inicial: 10000.00€
                    50 clientes realizando 500 operaciones totales...

                    --- CON REENTRANTLOCK ---
                    Saldo final: 9847.32€
                    Operaciones exitosas: 487/500
                    Operaciones fallidas: 13 (fondos insuficientes)
                    Tiempo total: 2.3s ✅

                    --- CON SYNCHRONIZED ---
                    Saldo final: 9847.32€
                    Operaciones exitosas: 487/500
                    Tiempo total: 2.1s ✅

                    --- CON VOLATILE ---
                    Saldo final: 8923.45€ ❌ INCORRECTO
                    Operaciones perdidas detectadas!
     */

    public static Random random = new Random();

    private double saldo=10000;

    Thread[] clientes = new Thread[50];

    private List<String> historial = new ArrayList<>();

    public void operaciones(){
        int probabilidad = random.nextInt(1,11);

        if(probabilidad>4){
            double retiro=random.nextDouble(1, 101);
            if(saldo>retiro){
                saldo=saldo-retiro;
                historial.add("Retirada de efectivo\n"+ LocalDateTime.now());
            } else {
                System.err.println("Saldo insuficiente");
                historial.add("Retirada fallida\n"+LocalDateTime.now());
            }
        } else {
            saldo=saldo+random.nextDouble(1, 51);
            historial.add("Ingreso de efectivo \n"+LocalDateTime.now());
        }
    }

    ReentrantLock lock = new ReentrantLock();

    void main(){
        for (int i = 0; i < clientes.length; i++) {
            clientes[i]= new Thread(()->{
                for (int j = 0; j < 10; j++) {
                    lock.lock();
                    try{
                        Thread.sleep(50);
                        operaciones();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        lock.unlock();
                    }
                }
            });
        }

        Arrays.stream(clientes).forEach(Thread::start);
        Arrays.stream(clientes).forEach(hilo ->{
            try {
                hilo.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        IO.println("""
                === BANCO VIRTUAL ===
                Saldo inicial: 10000.00€
                50 clientes realizando 500 operaciones totales...""");
        historial.forEach(System.out::println);
        IO.println("Saldo final: "+saldo+"€");

    }

}
