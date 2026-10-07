import java.util.concurrent.CompletableFuture;

public class Ejercicio6 {
    /*
    Crea una cadena:
        10
         ↓
        multiplicar por 2
         ↓
        sumar 5
         ↓
        convertir a String
         ↓
        mostrar

     Utiliza:
        supplyAsync
        thenApply
        thenAccept

     El resultado debe ser:
        Resultado: 25
     */

    void main(){
        CompletableFuture.supplyAsync(()-> {
            return  10;
        }).thenApply(x->x*2).thenApply(x -> x+5).thenApply(String::valueOf).thenAccept(resultado ->IO.println("Resultado: "+ resultado)).join();
    }

}
