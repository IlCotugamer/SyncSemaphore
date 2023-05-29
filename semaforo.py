from threading import Semaphore, Thread
from time import sleep
import random

ACCESSO_ASCENSORE = Semaphore(0)
ENTRATA_PASSEGGERI = Semaphore(1)
DISCESA_PASSEGGERI = Semaphore(1)
MUTEX = Semaphore(1)
N = 6
prenotazioni = list()
pianoCorrente = 0

def ascensoreThread():
    global pianoCorrente
    direzione = 1
    while True:
        ACCESSO_ASCENSORE.acquire()
        ACCESSO_ASCENSORE.acquire()
        
        print("CHIUDI LE PORTE --- "+ ("sale" if direzione else "scende") + "\n")
        print(f"\tPiano: {pianoCorrente}; Passeggeri: {len(prenotazioni)}\n")
        
        for i, prenotazione in enumerate(prenotazioni):
            print(f"Passeggero: {i + 1}, scende al piano: {prenotazione}")
    
        if pianoCorrente == N:
            direzione = 0
        elif pianoCorrente == 0:
            direzione = 1
        
        pianoCorrente += 1 if direzione else -1
        
        sleep(5.5)
        print("APRI LE PORTE\n")
        
        ENTRATA_PASSEGGERI.release()
        DISCESA_PASSEGGERI.release()

def entrataPasseggeriThread():
    while True:
        ENTRATA_PASSEGGERI.acquire()
        
        for _ in range(random.randint(0, 15)):
            MUTEX.acquire()
            prenotazioni.append(random.randint(0, N))
            MUTEX.release()

        ACCESSO_ASCENSORE.release()
        
def discesaPasseggeriThread():
    while True:
        DISCESA_PASSEGGERI.acquire()
        
        for i, prenotazione in enumerate(prenotazioni):
            if prenotazione == pianoCorrente:
                MUTEX.acquire()
                prenotazioni.pop(i)
                MUTEX.release()
                
        ACCESSO_ASCENSORE.release()

t1 = Thread(target=ascensoreThread)
t2 = Thread(target=entrataPasseggeriThread)
t3 = Thread(target=discesaPasseggeriThread)

t2.start()
t3.start()
t1.start()