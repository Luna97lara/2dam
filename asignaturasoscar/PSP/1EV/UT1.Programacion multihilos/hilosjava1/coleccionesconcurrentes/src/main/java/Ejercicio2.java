import java.util.concurrent.ConcurrentHashMap;

public class Ejercicio2 {
    /*
    Implementa una caché compartida (ConcurrentHashMap<String, Integer>) a la que 5
    hilos acceden simultáneamente para leer y escribir valores. Comprueba que no se producen excepciones
    de concurrencia (a diferencia de usar un HashMap normal, que puedes probar también para ver el error).
     */

    public static final ConcurrentHashMap<Integer, String> mapita = new ConcurrentHashMap<>();

    void main(){
        mapita.put(1,"Luna");
        mapita.put(2,"Daniel");
        mapita.put(3,"Mike");
        IO.println(mapita);

    }


}
