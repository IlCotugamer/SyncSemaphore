import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Ascensore_FINALE {
    private static final int N = 1; // Numero di piani
    private static Semaphore entrataPasseggeri = new Semaphore(1);
    private static Semaphore discesaPasseggeri = new Semaphore(1);
    private static Semaphore accessoAscensore = new Semaphore(0);
    private static Semaphore mutex = new Semaphore(1);
    private static List<Integer> prenotazioni = new ArrayList<>();
    private static int contatorePiano = 0;

    public static void main(String[] args) {
        Thread ascensoreThread = new Thread(() -> {
            boolean direzione = true;  //true = Sale; false = Scende;
            while (true) {
                try {
                    accessoAscensore.acquire();
                    accessoAscensore.acquire();
                    
                    System.out.println("Chiude le porte  ---  L'ascensore " + (direzione ? "sale" : "scende") + "\n");
                    System.out.printf("\tPiano: %d; Numero passeggeri: %d;\n", contatorePiano, prenotazioni.size());
                    System.out.println("---------------------------------------------------------------------");
                    for (int i = 0; i < prenotazioni.size(); i++) {
                        System.out.println("\t\tPasseggero: " + (i + 1) + " scende al piano: " + prenotazioni.get(i));
                    } System.out.println("---------------------------------------------------------------------");
                    Thread.sleep(3500);
                    System.out.println("\nApri le porte");
                    
                    
                    if (contatorePiano == N) direzione = false;
                    else if (contatorePiano == 0) direzione = true;

                    mutex.acquire();
                    if (direzione) contatorePiano++;
                    else contatorePiano--;
                    mutex.release();
                    
                    entrataPasseggeri.release();
                    discesaPasseggeri.release();
                } catch (InterruptedException e) {e.printStackTrace();}
            }
        });


        Thread salitaPasseggeriThread = new Thread(() -> {
            while (true){
                try {
                    entrataPasseggeri.acquire();

                    for (int i = 0; i < (int) Math.round(Math.random() * 16); i++){
                        mutex.acquire();
                        prenotazioni.add((int) Math.round(Math.random() * 6));
                        mutex.release();
                    }
                    accessoAscensore.release();
                } catch (InterruptedException e) {e.printStackTrace();}
            }
        });


        Thread discesaPasseggeriThread = new Thread(() -> {
            while (true){
                try {
                    discesaPasseggeri.acquire();

                    for (int i = 0; i < prenotazioni.size(); i++) 
                        if (prenotazioni.get(i) == contatorePiano){
                            mutex.acquire();
                            prenotazioni.remove(i);
                            mutex.release();
                        }
                    accessoAscensore.release();
                } catch (InterruptedException e) {e.printStackTrace();}
            }       
        });


        System.out.println("Inizio Procedura dell'ascensore automatico\n");
        salitaPasseggeriThread.start();
        discesaPasseggeriThread.start();
        ascensoreThread.start();
    }
}
