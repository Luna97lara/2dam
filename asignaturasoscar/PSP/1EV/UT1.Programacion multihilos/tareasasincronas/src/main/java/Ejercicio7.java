import java.util.concurrent.CompletableFuture;

public class Ejercicio7 {
    /*
    Implementa:
        CompletableFuture<String> obtenerUsuario()
    que tarda un segundo y devuelva:
        Oscar
    Después implementa:
        CompletableFuture<String> obtenerEmail(String usuario)
    que tarde otro segundo y devuelva:
        usuario@example.com
    Utiliza thenCompose() para encadenar ambas operaciones.
    o utilices get() entre las dos tareas.
     */
    CompletableFuture<String> obtenerUsuario(){
        return CompletableFuture.supplyAsync(()-> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return  "Oscar";
        });
    }

    CompletableFuture<String> obtenerEmail(String usuario){
        return CompletableFuture.supplyAsync(()-> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "usuario@example.com";
        });
    }

    void main() throws InterruptedException {
        obtenerUsuario().thenCompose(usuario->obtenerEmail(usuario)).thenAccept(email->IO.println(email));
        Thread.sleep(3000);
    }



}
