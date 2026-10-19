import java.util.concurrent.CompletableFuture;

public class Ejercicio1 {
    /*
Corregir una cadena de `CompletableFuture` (1.5 Pto)

El objetivo del siguiente programa es calcular `((10 * 2) + 5)`,
convertir el resultado a texto y mostrar `Resultado: 25`.

El código contiene errores:

corrigelos y explica en un comentario que estaba mal y como lo has arreglado.

 */

    static void main() {
        CompletableFuture<Void> tarea =
                CompletableFuture.supplyAsync(() -> {return 10;})
                        .thenApply(n -> n * 2)
                        .thenApply(n -> n + 5)
                        .thenApply(String::valueOf)
                        .thenAccept(resultado ->
                                System.out.println("Resultado: " + resultado)
                        );

        tarea.join();

    }

    /*
    Cosas que corregí:
        1. El CompletableFuture tenía que ser <Void> en lugar de <Integer>
        2. Aquí: CompletableFuture.supplyAsync(() -> 10) faltaba un return delante del 10, así como las llaves {} y evidentemente el ;
        3. Esas llaves que faltaban ahí, estaban en realidad mal colocadas, concretamente se ubicaban en .thenApply(n -> {
                            n + 5;
                        })
     */


}
