import java.util.concurrent.CompletableFuture;

public class Ejercicio5 {
    /*
    Crea:
        CompletableFuture<Integer>
    que calcule:
        10 + 20
    Después utiliza:
        thenApply()
    para multiplicarlo por 2.

    Finalmente utiliza:
        thenAccept()
    para mostrar:
        Resultado: 60
     */
    void main(){
        CompletableFuture<Void> future= CompletableFuture.supplyAsync(()-> {
            return  10 + 20;
        }).thenApply(x->x*2).thenAccept(x->IO.println("Resultado: "+x));
    }
}
